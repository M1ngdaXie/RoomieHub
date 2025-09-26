package com.campusnest.campusnest_platform.response;

import com.campusnest.campusnest_platform.enums.MessageType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageResponse {
    
    private Long id;
    private Long conversationId;
    private UserSummaryResponse sender;
    private String content;
    private MessageType messageType;
    private LocalDateTime sentAt;
    private Boolean isEdited;
    private LocalDateTime editedAt;
    private Boolean isFromCurrentUser;
    
    public static MessageResponse fromMessage(com.campusnest.campusnest_platform.models.Message message, Long currentUserId) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setConversationId(message.getConversation().getId());
        response.setSender(UserSummaryResponse.fromUser(message.getSender()));
        response.setContent(message.getContent());
        response.setMessageType(message.getMessageType());
        response.setSentAt(message.getSentAt());
        response.setIsEdited(message.getIsEdited());
        response.setEditedAt(message.getEditedAt());
        response.setIsFromCurrentUser(message.getSender().getId().equals(currentUserId));
        return response;
    }
}