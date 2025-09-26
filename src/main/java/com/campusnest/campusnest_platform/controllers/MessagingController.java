package com.campusnest.campusnest_platform.controllers;

import com.campusnest.campusnest_platform.models.*;
import com.campusnest.campusnest_platform.repository.HousingListingRepository;
import com.campusnest.campusnest_platform.repository.UserRepository;
import com.campusnest.campusnest_platform.requests.CreateConversationRequest;
import com.campusnest.campusnest_platform.requests.SendMessageRequest;
import com.campusnest.campusnest_platform.response.*;
import com.campusnest.campusnest_platform.services.MessagingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messaging")
@Slf4j
public class MessagingController {

    @Autowired
    private MessagingService messagingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HousingListingRepository housingListingRepository;

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationSummaryResponse>> getConversations(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        User currentUser = getCurrentUser(authentication);
        log.info("Getting conversations for user: {}", maskEmail(currentUser.getEmail()));
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Conversation> conversations = messagingService.getUserConversations(currentUser, pageable);
        
        List<ConversationSummaryResponse> response = conversations.getContent().stream()
                .map(conversation -> {
                    Message lastMessage = messagingService.getLatestMessage(conversation.getId());
                    String lastMessagePreview = lastMessage != null ? 
                            truncateMessage(lastMessage.getContent(), 50) : "No messages yet";
                    
                    Long unreadCount = messagingService.getUnreadMessageCount(conversation.getId(), currentUser);
                    
                    return ConversationSummaryResponse.fromConversation(
                            conversation, currentUser, lastMessagePreview, unreadCount);
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<ConversationDetailResponse> getConversation(
            @PathVariable Long conversationId,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        User currentUser = getCurrentUser(authentication);
        log.info("Getting conversation {} for user: {}", conversationId, maskEmail(currentUser.getEmail()));
        
        if (!messagingService.canUserAccessConversation(conversationId, currentUser)) {
            return ResponseEntity.notFound().build();
        }
        
        Conversation conversation = messagingService.getConversation(conversationId, currentUser);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messagingService.getConversationMessages(conversationId, currentUser, pageable);
        
        List<MessageResponse> messageResponses = messages.getContent().stream()
                .map(message -> MessageResponse.fromMessage(message, currentUser.getId()))
                .collect(Collectors.toList());
        
        long totalMessageCount = messages.getTotalElements();
        
        ConversationDetailResponse response = ConversationDetailResponse.fromConversation(
                conversation, currentUser, messageResponses, totalMessageCount);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable Long conversationId,
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        
        User currentUser = getCurrentUser(authentication);
        log.info("Getting messages for conversation {} for user: {}", conversationId, maskEmail(currentUser.getEmail()));
        
        if (!messagingService.canUserAccessConversation(conversationId, currentUser)) {
            return ResponseEntity.notFound().build();
        }
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Message> messages = messagingService.getConversationMessages(conversationId, currentUser, pageable);
        
        List<MessageResponse> response = messages.getContent().stream()
                .map(message -> MessageResponse.fromMessage(message, currentUser.getId()))
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/conversations")
    public ResponseEntity<?> createConversation(
            @Valid @RequestBody CreateConversationRequest request,
            Authentication authentication) {
        
        try {
            User currentUser = getCurrentUser(authentication);
            log.info("Creating conversation between user {} and user {}", 
                    maskEmail(currentUser.getEmail()), request.getOtherParticipantId());
            
            User otherParticipant = userRepository.findById(request.getOtherParticipantId())
                    .orElseThrow(() -> new IllegalArgumentException("Other participant not found with ID: " + request.getOtherParticipantId()));
            
            HousingListing listing = housingListingRepository.findById(request.getHousingListingId())
                    .orElseThrow(() -> new IllegalArgumentException("Housing listing not found with ID: " + request.getHousingListingId()));
            
            // Validate users are different
            if (currentUser.getId().equals(otherParticipant.getId())) {
                return ResponseEntity.badRequest().body("Cannot create conversation with yourself");
            }
            
            Conversation conversation = messagingService.createOrGetConversation(
                    currentUser, otherParticipant, listing);
            
            // Send initial message if provided
            if (request.getInitialMessage() != null && !request.getInitialMessage().trim().isEmpty()) {
                messagingService.sendMessage(conversation.getId(), currentUser, request.getInitialMessage());
            }
            
            Message lastMessage = messagingService.getLatestMessage(conversation.getId());
            String lastMessagePreview = lastMessage != null ? 
                    truncateMessage(lastMessage.getContent(), 50) : "No messages yet";
            
            Long unreadCount = messagingService.getUnreadMessageCount(conversation.getId(), currentUser);
            
            ConversationSummaryResponse response = ConversationSummaryResponse.fromConversation(
                    conversation, currentUser, lastMessagePreview, unreadCount);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid request for conversation creation: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Error creating conversation: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Failed to create conversation");
        }
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable Long conversationId,
            @Valid @RequestBody SendMessageRequest request,
            Authentication authentication) {
        
        User currentUser = getCurrentUser(authentication);
        log.info("Sending message to conversation {} from user: {}", conversationId, maskEmail(currentUser.getEmail()));
        
        if (!messagingService.canUserAccessConversation(conversationId, currentUser)) {
            return ResponseEntity.notFound().build();
        }
        
        Message message = messagingService.sendMessage(
                conversationId, currentUser, request.getContent(), request.getMessageType());
        
        MessageResponse response = MessageResponse.fromMessage(message, currentUser.getId());
        
        return ResponseEntity.ok(response);
    }

    @PutMapping("/conversations/{conversationId}/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable Long conversationId,
            Authentication authentication) {
        
        User currentUser = getCurrentUser(authentication);
        log.info("Marking messages as read for conversation {} for user: {}", 
                conversationId, maskEmail(currentUser.getEmail()));
        
        if (!messagingService.canUserAccessConversation(conversationId, currentUser)) {
            return ResponseEntity.notFound().build();
        }
        
        messagingService.markMessagesAsRead(conversationId, currentUser);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadMessageCount(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        log.debug("Getting unread message count for user: {}", maskEmail(currentUser.getEmail()));
        
        Long unreadCount = messagingService.getTotalUnreadMessageCount(currentUser);
        
        return ResponseEntity.ok(unreadCount);
    }

    @DeleteMapping("/conversations/{conversationId}")
    public ResponseEntity<Void> deactivateConversation(
            @PathVariable Long conversationId,
            Authentication authentication) {
        
        User currentUser = getCurrentUser(authentication);
        log.info("Deactivating conversation {} for user: {}", conversationId, maskEmail(currentUser.getEmail()));
        
        if (!messagingService.canUserAccessConversation(conversationId, currentUser)) {
            return ResponseEntity.notFound().build();
        }
        
        messagingService.deactivateConversation(conversationId, currentUser);
        
        return ResponseEntity.ok().build();
    }

    private User getCurrentUser(Authentication authentication) {
        if (authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        throw new RuntimeException("User not found in authentication context");
    }

    private String truncateMessage(String message, int maxLength) {
        if (message == null) return null;
        if (message.length() <= maxLength) return message;
        return message.substring(0, maxLength) + "...";
    }

    private String maskEmail(String email) {
        if (email == null) return "null";
        int atIndex = email.indexOf("@");
        return atIndex > 0 ? email.substring(0, 1) + "***" + email.substring(atIndex) : email;
    }
}