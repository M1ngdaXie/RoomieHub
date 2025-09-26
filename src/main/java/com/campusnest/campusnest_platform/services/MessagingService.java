package com.campusnest.campusnest_platform.services;

import com.campusnest.campusnest_platform.enums.MessageType;
import com.campusnest.campusnest_platform.models.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface MessagingService {
    
    /**
     * Create a new conversation or get existing one between two users for a housing listing
     */
    Conversation createOrGetConversation(User user1, User user2, HousingListing listing);
    
    /**
     * Send a message in a conversation
     */
    Message sendMessage(Long conversationId, User sender, String content, MessageType messageType);
    
    /**
     * Send a text message (default type)
     */
    Message sendMessage(Long conversationId, User sender, String content);
    
    /**
     * Get all messages in a conversation (with pagination)
     */
    Page<Message> getConversationMessages(Long conversationId, User requestingUser, Pageable pageable);
    
    /**
     * Get all messages in a conversation (without pagination)
     */
    List<Message> getConversationMessages(Long conversationId, User requestingUser);
    
    /**
     * Get recent messages since a specific timestamp
     */
    List<Message> getRecentMessages(Long conversationId, User requestingUser, LocalDateTime since);
    
    /**
     * Get all conversations for a user
     */
    List<Conversation> getUserConversations(User user);
    
    /**
     * Get conversations for a user with pagination
     */
    Page<Conversation> getUserConversations(User user, Pageable pageable);
    
    /**
     * Mark messages as read by a user
     */
    void markMessagesAsRead(Long conversationId, User user);
    
    /**
     * Mark specific message as read
     */
    void markMessageAsRead(Long messageId, User user);
    
    /**
     * Get unread message count for a conversation
     */
    long getUnreadMessageCount(Long conversationId, User user);
    
    /**
     * Get total unread message count for a user
     */
    long getTotalUnreadMessageCount(User user);
    
    /**
     * Get conversation by ID if user is a participant
     */
    Conversation getConversation(Long conversationId, User user);
    
    /**
     * Get conversation with eagerly loaded participants (for WebSocket usage)
     */
    Conversation getConversationWithParticipants(Long conversationId, User user);
    
    /**
     * Check if user can access a conversation
     */
    boolean canUserAccessConversation(Long conversationId, User user);
    
    /**
     * Delete/deactivate a conversation
     */
    void deactivateConversation(Long conversationId, User user);
    
    /**
     * Get latest message in a conversation
     */
    Message getLatestMessage(Long conversationId);
    
    /**
     * Validate conversation and associated housing listing are still valid
     */
    void validateConversationIntegrity(Long conversationId, User user);
}