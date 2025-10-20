package com.campusnest.campusnest_platform.config.websocket;

import com.campusnest.campusnest_platform.models.Message;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.response.ChatMessageResponse;
import com.campusnest.campusnest_platform.services.MessagingService;
import com.campusnest.campusnest_platform.services.UserPresenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
public class WebSocketEventListener {

    @Autowired
    private UserPresenceService presenceService;

    @Autowired
    private MessagingService messagingService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Get authenticated user from session
        var principal = headerAccessor.getUser();
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            var auth = (UsernamePasswordAuthenticationToken) principal;
            if (auth.getPrincipal() instanceof User) {
                User user = (User) auth.getPrincipal();

                // Mark user as ONLINE
                presenceService.setUserOnline(user.getId());

                log.info("User {} CONNECTED via WebSocket - automatically marked ONLINE",
                        maskEmail(user.getEmail()));

                // ✨ NEW: Send any missed messages while user was offline
                sendMissedMessages(user);
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Get authenticated user from session
        var principal = headerAccessor.getUser();
        if (principal instanceof UsernamePasswordAuthenticationToken) {
            var auth = (UsernamePasswordAuthenticationToken) principal;
            if (auth.getPrincipal() instanceof User) {
                User user = (User) auth.getPrincipal();

                // Mark user as OFFLINE
                presenceService.setUserOffline(user.getId());

                log.info("User {} DISCONNECTED from WebSocket - automatically marked OFFLINE",
                        maskEmail(user.getEmail()));
            }
        }
    }

    /**
     * Send all unread messages to user when they reconnect
     */
    private void sendMissedMessages(User user) {
        try {
            // Get all conversations for this user
            var conversations = messagingService.getUserConversations(user);

            int totalMissedMessages = 0;

            for (var conversation : conversations) {
                // Get unread messages for this conversation
                long unreadCount = messagingService.getUnreadMessageCount(conversation.getId(), user);

                if (unreadCount > 0) {
                    // Get recent unread messages (last 50 to avoid overwhelming)
                    var messages = messagingService.getConversationMessages(conversation.getId(), user)
                            .stream()
                            .filter(msg -> !msg.getSender().getId().equals(user.getId())) // Not sent by current user
                            .limit(50)
                            .toList();

                    // Send each missed message via WebSocket
                    for (Message message : messages) {
                        ChatMessageResponse response = ChatMessageResponse.fromMessage(message);

                        // Send to conversation-specific queue
                        messagingTemplate.convertAndSendToUser(
                                user.getEmail(),
                                "/queue/messages/" + conversation.getId(),
                                response
                        );

                        // Also send to general queue
                        messagingTemplate.convertAndSendToUser(
                                user.getEmail(),
                                "/queue/messages",
                                response
                        );

                        totalMissedMessages++;
                    }

                    log.info("Sent {} missed messages from conversation {} to user {}",
                            messages.size(),
                            conversation.getId(),
                            maskEmail(user.getEmail()));
                }
            }

            if (totalMissedMessages > 0) {
                log.info("Successfully delivered {} total missed messages to user {} upon reconnection",
                        totalMissedMessages,
                        maskEmail(user.getEmail()));
            } else {
                log.debug("No missed messages to deliver for user {}", maskEmail(user.getEmail()));
            }

        } catch (Exception e) {
            log.error("Error sending missed messages to user {}: {}",
                    maskEmail(user.getEmail()),
                    e.getMessage(),
                    e);
        }
    }

    private String maskEmail(String email) {
        if (email == null) return "null";
        int atIndex = email.indexOf("@");
        return atIndex > 0 ? email.substring(0, 1) + "***" + email.substring(atIndex) : email;
    }
}

