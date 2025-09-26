package com.campusnest.campusnest_platform.repository;

import com.campusnest.campusnest_platform.models.Conversation;
import com.campusnest.campusnest_platform.models.HousingListing;
import com.campusnest.campusnest_platform.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT c FROM Conversation c WHERE " +
           "(c.participant1 = :user1 AND c.participant2 = :user2) OR " +
           "(c.participant1 = :user2 AND c.participant2 = :user1) AND " +
           "c.housingListing = :listing AND c.isActive = true")
    Optional<Conversation> findByParticipantsAndListing(
            @Param("user1") User user1, 
            @Param("user2") User user2, 
            @Param("listing") HousingListing listing);

    @Query("SELECT c FROM Conversation c WHERE " +
           "(c.participant1 = :user OR c.participant2 = :user) AND " +
           "c.isActive = true ORDER BY c.lastMessageAt DESC")
    Page<Conversation> findByUserOrderByLastMessageDesc(
            @Param("user") User user, 
            Pageable pageable);

    @Query("SELECT c FROM Conversation c WHERE " +
           "(c.participant1 = :user OR c.participant2 = :user) AND " +
           "c.isActive = true ORDER BY c.lastMessageAt DESC")
    List<Conversation> findByUserOrderByLastMessageDesc(@Param("user") User user);

    @Query("SELECT c FROM Conversation c WHERE " +
           "c.housingListing = :listing AND c.isActive = true")
    List<Conversation> findByHousingListingAndActiveTrue(@Param("listing") HousingListing listing);

    @Query("SELECT COUNT(c) FROM Conversation c WHERE " +
           "(c.participant1 = :user OR c.participant2 = :user) AND " +
           "c.isActive = true")
    long countActiveConversationsByUser(@Param("user") User user);

    @Query("SELECT c FROM Conversation c WHERE " +
           "c.id = :conversationId AND " +
           "(c.participant1 = :user OR c.participant2 = :user)")
    Optional<Conversation> findByIdAndParticipant(
            @Param("conversationId") Long conversationId, 
            @Param("user") User user);
}