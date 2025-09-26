package com.campusnest.campusnest_platform.requests;

import lombok.Data;

@Data
public class TypingIndicatorRequest {
    
    private Long conversationId;
    private Boolean isTyping;
}