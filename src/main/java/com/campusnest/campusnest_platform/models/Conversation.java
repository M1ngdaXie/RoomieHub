package com.campusnest.campusnest_platform.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "conversations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant1_id", nullable = false)
    private User participant1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant2_id", nullable = false)
    private User participant2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housing_listing_id", nullable = false)
    private HousingListing housingListing;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (lastMessageAt == null) {
            lastMessageAt = createdAt;
        }
    }

    public boolean isParticipant(User user) {
        return participant1.getId().equals(user.getId()) || 
               participant2.getId().equals(user.getId());
    }

    public User getOtherParticipant(User currentUser) {
        if (participant1.getId().equals(currentUser.getId())) {
            return participant2;
        } else if (participant2.getId().equals(currentUser.getId())) {
            return participant1;
        }
        return null;
    }

    public void updateLastMessageTime() {
        this.lastMessageAt = LocalDateTime.now();
    }
}