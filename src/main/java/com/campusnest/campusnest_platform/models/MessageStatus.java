package com.campusnest.campusnest_platform.models;

import com.campusnest.campusnest_platform.enums.MessageStatusType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_status", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"message_id", "user_id", "status"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "message_id", nullable = false)
    private Message message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MessageStatusType status;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public static MessageStatus createSentStatus(Message message, User user) {
        MessageStatus status = new MessageStatus();
        status.setMessage(message);
        status.setUser(user);
        status.setStatus(MessageStatusType.SENT);
        status.setTimestamp(LocalDateTime.now());
        return status;
    }

    public static MessageStatus createDeliveredStatus(Message message, User user) {
        MessageStatus status = new MessageStatus();
        status.setMessage(message);
        status.setUser(user);
        status.setStatus(MessageStatusType.DELIVERED);
        status.setTimestamp(LocalDateTime.now());
        return status;
    }

    public static MessageStatus createReadStatus(Message message, User user) {
        MessageStatus status = new MessageStatus();
        status.setMessage(message);
        status.setUser(user);
        status.setStatus(MessageStatusType.READ);
        status.setTimestamp(LocalDateTime.now());
        return status;
    }
}