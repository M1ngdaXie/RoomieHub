package com.campusnest.campusnest_platform.requests;

import com.campusnest.campusnest_platform.enums.MessageType;
import lombok.Data;

@Data
public class ChatMessageRequest {
    
    private Long conversationId;
    private String content;
    private MessageType messageType = MessageType.TEXT;
    private Long timestamp;
}