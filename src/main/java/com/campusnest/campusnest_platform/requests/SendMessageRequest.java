package com.campusnest.campusnest_platform.requests;

import com.campusnest.campusnest_platform.enums.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequest {
    
    @NotNull(message = "Conversation ID is required")
    private Long conversationId;
    
    @NotBlank(message = "Message content cannot be blank")
    private String content;
    
    private MessageType messageType = MessageType.TEXT;
}