package com.campusnest.campusnest_platform.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConversationDetailResponse {
    
    private Long id;
    private UserSummaryResponse otherParticipant;
    private HousingListingSummaryResponse housingListing;
    private List<MessageResponse> messages;
    private Long totalMessageCount;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private Boolean isActive;
    
    public static ConversationDetailResponse fromConversation(
            com.campusnest.campusnest_platform.models.Conversation conversation, 
            com.campusnest.campusnest_platform.models.User currentUser,
            List<MessageResponse> messages,
            Long totalMessageCount) {
        
        ConversationDetailResponse response = new ConversationDetailResponse();
        response.setId(conversation.getId());
        response.setOtherParticipant(UserSummaryResponse.fromUser(conversation.getOtherParticipant(currentUser)));
        response.setHousingListing(HousingListingSummaryResponse.fromHousingListing(conversation.getHousingListing()));
        response.setMessages(messages);
        response.setTotalMessageCount(totalMessageCount);
        response.setCreatedAt(conversation.getCreatedAt());
        response.setLastMessageAt(conversation.getLastMessageAt());
        response.setIsActive(conversation.getIsActive());
        return response;
    }
}