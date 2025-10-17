package com.campusnest.campusnest_platform.events;

import com.campusnest.campusnest_platform.enums.MessageType;
import com.campusnest.campusnest_platform.models.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageEvent {
    private Long messageId;
    private Long conversationId;
    private Long senderId;
    private String senderName;
    private String senderEmail;
    private Long recipientId;
    private String recipientName;
    private String recipientEmail;
    private String content;
    private MessageType messageType;
    private LocalDateTime timestamp;
    private Long housingListingId;

    public static MessageEvent fromMessage(Message message, Long recipientId, String recipientName, String recipientEmail){
        return MessageEvent.builder()
                .messageId(message.getId())
                .conversationId(message.getConversation().getId())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFirstName() + " " + message.getSender().getLastName())
                .senderEmail(message.getSender().getEmail())
                .recipientId(recipientId)
                .recipientName(recipientName)
                .recipientEmail(recipientEmail)
                .content(message.getContent())
                .messageType(message.getMessageType())
                .timestamp(message.getSentAt())
                .housingListingId(message.getConversation().getHousingListing().getId())
                .build();

    }
}
