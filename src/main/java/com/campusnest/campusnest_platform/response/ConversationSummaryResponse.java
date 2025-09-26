package com.campusnest.campusnest_platform.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationSummaryResponse {
    
    private Long id;
    private UserSummaryResponse otherParticipant;
    private HousingListingSummaryResponse housingListing;
    private String lastMessagePreview;
    private LocalDateTime lastMessageAt;
    private Long unreadCount;
    private Boolean isActive;
    
    public static ConversationSummaryResponse fromConversation(
            com.campusnest.campusnest_platform.models.Conversation conversation, 
            com.campusnest.campusnest_platform.models.User currentUser,
            String lastMessagePreview,
            Long unreadCount) {
        
        ConversationSummaryResponse response = new ConversationSummaryResponse();
        response.setId(conversation.getId());
        response.setOtherParticipant(UserSummaryResponse.fromUser(conversation.getOtherParticipant(currentUser)));
        response.setHousingListing(HousingListingSummaryResponse.fromHousingListing(conversation.getHousingListing()));
        response.setLastMessagePreview(lastMessagePreview);
        response.setLastMessageAt(conversation.getLastMessageAt());
        response.setUnreadCount(unreadCount);
        response.setIsActive(conversation.getIsActive());
        return response;
    }
}