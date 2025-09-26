package com.campusnest.campusnest_platform.response;

import com.campusnest.campusnest_platform.enums.MessageType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageResponse {
    
    private Long messageId;
    private Long conversationId;
    private UserSummaryResponse sender;
    private String content;
    private MessageType messageType;
    private LocalDateTime sentAt;
    private String status; // SENT, DELIVERED, READ
    
    public static ChatMessageResponse fromMessage(com.campusnest.campusnest_platform.models.Message message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setMessageId(message.getId());
        response.setConversationId(message.getConversation().getId());
        response.setSender(UserSummaryResponse.fromUser(message.getSender()));
        response.setContent(message.getContent());
        response.setMessageType(message.getMessageType());
        response.setSentAt(message.getSentAt());
        response.setStatus("SENT"); // Will be updated with actual status
        return response;
    }
}