package com.campusnest.campusnest_platform.controllers;

import com.campusnest.campusnest_platform.models.Message;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.requests.ChatMessageRequest;
import com.campusnest.campusnest_platform.requests.TypingIndicatorRequest;
import com.campusnest.campusnest_platform.response.ChatMessageResponse;
import com.campusnest.campusnest_platform.response.TypingIndicatorResponse;
import com.campusnest.campusnest_platform.services.MessagingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@Slf4j
public class WebSocketMessagingController {

    @Autowired
    private MessagingService messagingService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private CacheManager cacheManager;

    @MessageMapping("/chat/send")
    public void sendMessage(ChatMessageRequest request, Principal principal) {
        try {
            User currentUser = getCurrentUser(principal);
            log.info("Received WebSocket message from user {} for conversation {}", 
                    maskEmail(currentUser.getEmail()), request.getConversationId());

            // Validate user can access conversation
            if (!messagingService.canUserAccessConversation(request.getConversationId(), currentUser)) {
                log.warn("User {} attempted to send message to unauthorized conversation {}", 
                        maskEmail(currentUser.getEmail()), request.getConversationId());
                return;
            }

            // Send message through service (this handles the database transaction and cache clearing)
            Message message = messagingService.sendMessage(
                    request.getConversationId(), 
                    currentUser, 
                    request.getContent(),
                    request.getMessageType() != null ? request.getMessageType() : 
                            com.campusnest.campusnest_platform.enums.MessageType.TEXT
            );
            
            // Additional cache clearing for real-time messaging
            clearUserCaches(currentUser.getId());

            // Create response
            ChatMessageResponse response = ChatMessageResponse.fromMessage(message);

            // Get conversation details with eagerly loaded participants
            var conversation = messagingService.getConversationWithParticipants(request.getConversationId(), currentUser);
            
            // Get other participant details safely
            Long otherParticipantId = null;
            String otherParticipantEmail = null;
            
            if (conversation.getParticipant1().getId().equals(currentUser.getId())) {
                otherParticipantId = conversation.getParticipant2().getId();
                otherParticipantEmail = conversation.getParticipant2().getEmail();
            } else {
                otherParticipantId = conversation.getParticipant1().getId();  
                otherParticipantEmail = conversation.getParticipant1().getEmail();
            }

            if (otherParticipantEmail != null) {
                // Send to the other participant using their email as user identifier
                String destination = "/queue/messages/" + request.getConversationId();
                log.info("Sending WebSocket message to user: {}, destination: {}, message: {}", 
                        maskEmail(otherParticipantEmail), destination, response);
                
                messagingTemplate.convertAndSendToUser(
                        otherParticipantEmail,
                        destination,
                        response
                );
                
                // Also send to a general message queue for testing
                messagingTemplate.convertAndSendToUser(
                        otherParticipantEmail,
                        "/queue/messages",
                        response
                );
                
                log.info("Message sent to user {} in conversation {}", 
                        maskEmail(otherParticipantEmail), request.getConversationId());
            }

            // Send confirmation back to sender
            messagingTemplate.convertAndSendToUser(
                    currentUser.getEmail(),
                    "/queue/message-sent/" + request.getConversationId(),
                    response
            );
            
            // Also send confirmation to general queue
            messagingTemplate.convertAndSendToUser(
                    currentUser.getEmail(),
                    "/queue/message-sent",
                    response
            );

        } catch (Exception e) {
            log.error("Error sending WebSocket message: {}", e.getMessage());
            
            // Send error back to sender
            if (principal != null) {
                messagingTemplate.convertAndSendToUser(
                        principal.getName(),
                        "/queue/errors",
                        "Failed to send message: " + e.getMessage()
                );
            }
        }
    }

    @MessageMapping("/chat/typing")
    public void handleTypingIndicator(TypingIndicatorRequest request, Principal principal) {
        try {
            User currentUser = getCurrentUser(principal);
            log.debug("Received typing indicator from user {} for conversation {}: {}", 
                    maskEmail(currentUser.getEmail()), request.getConversationId(), request.getIsTyping());

            // Validate user can access conversation
            if (!messagingService.canUserAccessConversation(request.getConversationId(), currentUser)) {
                log.warn("User {} attempted to send typing indicator to unauthorized conversation {}", 
                        maskEmail(currentUser.getEmail()), request.getConversationId());
                return;
            }

            // Get conversation details with eagerly loaded participants
            var conversation = messagingService.getConversationWithParticipants(request.getConversationId(), currentUser);
            
            // Get other participant details safely
            String otherParticipantEmail = null;
            
            if (conversation.getParticipant1().getId().equals(currentUser.getId())) {
                otherParticipantEmail = conversation.getParticipant2().getEmail();
            } else {
                otherParticipantEmail = conversation.getParticipant1().getEmail();
            }

            if (otherParticipantEmail != null) {
                // Create typing indicator response
                TypingIndicatorResponse response = TypingIndicatorResponse.create(
                        request.getConversationId(), currentUser, request.getIsTyping());

                // Send typing indicator to the other participant
                messagingTemplate.convertAndSendToUser(
                        otherParticipantEmail,
                        "/queue/typing/" + request.getConversationId(),
                        response
                );
                log.debug("Typing indicator sent to user {} in conversation {}", 
                        maskEmail(otherParticipantEmail), request.getConversationId());
            }

        } catch (Exception e) {
            log.error("Error handling typing indicator: {}", e.getMessage());
        }
    }

    @MessageMapping("/chat/join")
    public void joinConversation(Long conversationId, Principal principal) {
        try {
            User currentUser = getCurrentUser(principal);
            log.info("User {} joining conversation {}", maskEmail(currentUser.getEmail()), conversationId);

            // Validate user can access conversation
            if (!messagingService.canUserAccessConversation(conversationId, currentUser)) {
                log.warn("User {} attempted to join unauthorized conversation {}", 
                        maskEmail(currentUser.getEmail()), conversationId);
                return;
            }

            // Mark messages as read when user joins conversation
            messagingService.markMessagesAsRead(conversationId, currentUser);

            // Send confirmation
            messagingTemplate.convertAndSendToUser(
                    currentUser.getEmail(),
                    "/queue/conversation-joined/" + conversationId,
                    "Successfully joined conversation"
            );

            log.info("User {} successfully joined conversation {}", maskEmail(currentUser.getEmail()), conversationId);

        } catch (Exception e) {
            log.error("Error joining conversation: {}", e.getMessage());
        }
    }

    @MessageMapping("/chat/leave")
    public void leaveConversation(Long conversationId, Principal principal) {
        try {
            User currentUser = getCurrentUser(principal);
            log.info("User {} leaving conversation {}", maskEmail(currentUser.getEmail()), conversationId);

            // Send confirmation
            messagingTemplate.convertAndSendToUser(
                    currentUser.getEmail(),
                    "/queue/conversation-left/" + conversationId,
                    "Left conversation"
            );

        } catch (Exception e) {
            log.error("Error leaving conversation: {}", e.getMessage());
        }
    }

    @MessageMapping("/chat/status")
    @SendToUser("/queue/status")
    public String getConnectionStatus(Principal principal) {
        User currentUser = getCurrentUser(principal);
        log.debug("Connection status requested by user: {}", maskEmail(currentUser.getEmail()));
        
        return "Connected as " + currentUser.getFirstName() + " " + currentUser.getLastName();
    }

    private User getCurrentUser(Principal principal) {
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            var auth = (UsernamePasswordAuthenticationToken) principal;
            if (auth.getPrincipal() instanceof User) {
                return (User) auth.getPrincipal();
            }
        }
        throw new RuntimeException("User not found in WebSocket authentication context");
    }

    private void clearUserCaches(Long userId) {
        // Clear specific user caches that might be affected by new messages
        if (cacheManager.getCache("unread-counts") != null) {
            cacheManager.getCache("unread-counts").evict(userId);
        }
        if (cacheManager.getCache("conversations") != null) {
            cacheManager.getCache("conversations").clear(); // Clear all conversation caches
        }
        log.debug("Cleared caches for user: {}", userId);
    }

    private String maskEmail(String email) {
        if (email == null) return "null";
        int atIndex = email.indexOf("@");
        return atIndex > 0 ? email.substring(0, 1) + "***" + email.substring(atIndex) : email;
    }
}