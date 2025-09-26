package com.campusnest.campusnest_platform.response;

import lombok.Data;

@Data
public class TypingIndicatorResponse {
    
    private Long conversationId;
    private UserSummaryResponse user;
    private Boolean isTyping;
    private Long timestamp;
    
    public static TypingIndicatorResponse create(Long conversationId, com.campusnest.campusnest_platform.models.User user, Boolean isTyping) {
        TypingIndicatorResponse response = new TypingIndicatorResponse();
        response.setConversationId(conversationId);
        response.setUser(UserSummaryResponse.fromUser(user));
        response.setIsTyping(isTyping);
        response.setTimestamp(System.currentTimeMillis());
        return response;
    }
}