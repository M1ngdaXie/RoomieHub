package com.campusnest.campusnest_platform.services;

import com.campusnest.campusnest_platform.enums.MessageStatusType;
import com.campusnest.campusnest_platform.enums.MessageType;
import com.campusnest.campusnest_platform.models.*;
import com.campusnest.campusnest_platform.repository.ConversationRepository;
import com.campusnest.campusnest_platform.repository.MessageRepository;
import com.campusnest.campusnest_platform.repository.MessageStatusRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
public class MessagingServiceImpl implements MessagingService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageStatusRepository messageStatusRepository;

    @Autowired
    private com.campusnest.campusnest_platform.repository.HousingListingRepository housingListingRepository;

    @Override
    public Conversation createOrGetConversation(User user1, User user2, HousingListing listing) {
        log.info("Creating or getting conversation between users {} and {} for listing {}", 
                maskEmail(user1.getEmail()), maskEmail(user2.getEmail()), listing.getId());

        // Validate housing listing exists and is active
        if (listing == null) {
            throw new IllegalArgumentException("Housing listing cannot be null");
        }
        
        // Refresh listing from database to ensure it exists and get latest data
        HousingListing currentListing = housingListingRepository.findById(listing.getId())
                .orElseThrow(() -> new IllegalArgumentException("Housing listing not found with ID: " + listing.getId()));
        
        if (!currentListing.getIsActive()) {
            throw new IllegalArgumentException("Cannot create conversation for inactive housing listing: " + currentListing.getTitle());
        }
        
        log.info("Validated housing listing: {} (Active: {})", currentListing.getTitle(), currentListing.getIsActive());

        return conversationRepository.findByParticipantsAndListing(user1, user2, currentListing)
                .orElseGet(() -> {
                    log.info("Creating new conversation");
                    Conversation conversation = new Conversation();
                    conversation.setParticipant1(user1);
                    conversation.setParticipant2(user2);
                    conversation.setHousingListing(currentListing);
                    conversation.setIsActive(true);
                    
                    Conversation saved = conversationRepository.save(conversation);
                    
                    // Send system message to initialize conversation
                    sendMessage(saved.getId(), user1, 
                            "Conversation started about: " + currentListing.getTitle(), 
                            MessageType.SYSTEM);
                    
                    return saved;
                });
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "unread-counts", allEntries = true),
            @CacheEvict(value = "conversations", allEntries = true),
            @CacheEvict(value = "conversation-messages", key = "#conversationId")
    })
    public Message sendMessage(Long conversationId, User sender, String content, MessageType messageType) {
        log.info("Sending {} message in conversation {} from user {}", 
                messageType, conversationId, maskEmail(sender.getEmail()));

        Conversation conversation = getConversation(conversationId, sender);
        
        Message message = new Message();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setSentAt(LocalDateTime.now());
        
        Message savedMessage = messageRepository.save(message);
        
        // Update conversation last message time
        conversation.updateLastMessageTime();
        conversationRepository.save(conversation);
        
        // Create message status for sender (SENT)
        MessageStatus senderStatus = MessageStatus.createSentStatus(savedMessage, sender);
        messageStatusRepository.save(senderStatus);
        
        // Create message status for recipient (DELIVERED - will be updated when they come online)
        User recipient = conversation.getOtherParticipant(sender);
        if (recipient != null) {
            MessageStatus recipientStatus = MessageStatus.createDeliveredStatus(savedMessage, recipient);
            messageStatusRepository.save(recipientStatus);
        }
        
        log.info("Message sent successfully with ID: {}", savedMessage.getId());
        return savedMessage;
    }

    @Override
    public Message sendMessage(Long conversationId, User sender, String content) {
        return sendMessage(conversationId, sender, content, MessageType.TEXT);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Message> getConversationMessages(Long conversationId, User requestingUser, Pageable pageable) {
        log.debug("Getting messages for conversation {} for user {}", conversationId, maskEmail(requestingUser.getEmail()));
        
        Conversation conversation = getConversation(conversationId, requestingUser);
        return messageRepository.findByConversationOrderBySentAtDesc(conversation, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getConversationMessages(Long conversationId, User requestingUser) {
        log.debug("Getting all messages for conversation {} for user {}", conversationId, maskEmail(requestingUser.getEmail()));
        
        Conversation conversation = getConversation(conversationId, requestingUser);
        return messageRepository.findByConversationOrderBySentAtAsc(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getRecentMessages(Long conversationId, User requestingUser, LocalDateTime since) {
        log.debug("Getting recent messages since {} for conversation {}", since, conversationId);
        
        Conversation conversation = getConversation(conversationId, requestingUser);
        return messageRepository.findByConversationAndSentAtAfter(conversation, since);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Conversation> getUserConversations(User user) {
        log.debug("Getting conversations for user {}", maskEmail(user.getEmail()));
        return conversationRepository.findByUserOrderByLastMessageDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "conversations", key = "#user.id + ':' + #pageable.pageNumber")
    public Page<Conversation> getUserConversations(User user, Pageable pageable) {
        log.debug("Getting conversations with pagination for user {}", maskEmail(user.getEmail()));
        return conversationRepository.findByUserOrderByLastMessageDesc(user, pageable);
    }

    @Override
    @CacheEvict(value = "unread-counts", key = "#user.id")
    public void markMessagesAsRead(Long conversationId, User user) {
        log.info("Marking messages as read in conversation {} for user {}", conversationId, maskEmail(user.getEmail()));
        
        Conversation conversation = getConversation(conversationId, user);
        List<Message> unreadMessages = messageRepository.findUnreadMessagesInConversation(conversation, user);
        
        for (Message message : unreadMessages) {
            if (!messageStatusRepository.existsByMessageAndUserAndStatus(message, user, MessageStatusType.READ)) {
                MessageStatus readStatus = MessageStatus.createReadStatus(message, user);
                messageStatusRepository.save(readStatus);
            }
        }
        
        log.info("Marked {} messages as read", unreadMessages.size());
    }

    @Override
    public void markMessageAsRead(Long messageId, User user) {
        log.info("Marking message {} as read for user {}", messageId, maskEmail(user.getEmail()));
        
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        // Verify user can access this message
        if (!message.getConversation().isParticipant(user)) {
            throw new RuntimeException("User not authorized to access this message");
        }
        
        if (!messageStatusRepository.existsByMessageAndUserAndStatus(message, user, MessageStatusType.READ)) {
            MessageStatus readStatus = MessageStatus.createReadStatus(message, user);
            messageStatusRepository.save(readStatus);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadMessageCount(Long conversationId, User user) {
        Conversation conversation = getConversation(conversationId, user);
        return messageRepository.countUnreadMessagesInConversation(conversation, user);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "unread-counts", key = "#user.id")
    public long getTotalUnreadMessageCount(User user) {
        List<Message> unreadMessages = messageRepository.findAllUnreadMessagesForUser(user);
        return unreadMessages.size();
    }

    @Override
    @Transactional(readOnly = true)
    public Conversation getConversation(Long conversationId, User user) {
        return conversationRepository.findByIdAndParticipant(conversationId, user)
                .orElseThrow(() -> new RuntimeException("Conversation not found or user not authorized"));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Conversation getConversationWithParticipants(Long conversationId, User user) {
        Conversation conversation = conversationRepository.findByIdAndParticipant(conversationId, user)
                .orElseThrow(() -> new RuntimeException("Conversation not found or user not authorized"));
        
        // Eagerly load the participants to avoid lazy loading issues
        conversation.getParticipant1().getEmail(); // Force initialization
        conversation.getParticipant2().getEmail(); // Force initialization
        
        return conversation;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canUserAccessConversation(Long conversationId, User user) {
        return conversationRepository.findByIdAndParticipant(conversationId, user).isPresent();
    }

    @Override
    public void deactivateConversation(Long conversationId, User user) {
        log.info("Deactivating conversation {} for user {}", conversationId, maskEmail(user.getEmail()));
        
        Conversation conversation = getConversation(conversationId, user);
        conversation.setIsActive(false);
        conversationRepository.save(conversation);
    }

    @Override
    @Transactional(readOnly = true)
    public Message getLatestMessage(Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        return messageRepository.findLatestMessageByConversation(conversation)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateConversationIntegrity(Long conversationId, User user) {
        Conversation conversation = getConversation(conversationId, user);
        
        // Check if housing listing still exists
        if (conversation.getHousingListing() != null) {
            HousingListing listing = housingListingRepository.findById(conversation.getHousingListing().getId())
                    .orElse(null);
            
            if (listing == null) {
                log.warn("Conversation {} references deleted housing listing {}", 
                        conversationId, conversation.getHousingListing().getId());
                throw new IllegalStateException("The housing listing for this conversation no longer exists");
            }
            
            if (!listing.getIsActive()) {
                log.warn("Conversation {} references inactive housing listing: {}", 
                        conversationId, listing.getTitle());
                throw new IllegalStateException("The housing listing for this conversation is no longer active");
            }
        }
    }

    private String maskEmail(String email) {
        if (email == null) return "null";
        int atIndex = email.indexOf("@");
        return atIndex > 0 ? email.substring(0, 1) + "***" + email.substring(atIndex) : email;
    }
}