package com.campusnest.campusnest_platform.repository;

import com.campusnest.campusnest_platform.enums.MessageStatusType;
import com.campusnest.campusnest_platform.models.Message;
import com.campusnest.campusnest_platform.models.MessageStatus;
import com.campusnest.campusnest_platform.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageStatusRepository extends JpaRepository<MessageStatus, Long> {

    @Query("SELECT ms FROM MessageStatus ms WHERE ms.message = :message AND ms.user = :user")
    List<MessageStatus> findByMessageAndUser(
            @Param("message") Message message, 
            @Param("user") User user);

    @Query("SELECT ms FROM MessageStatus ms WHERE ms.message = :message AND ms.user = :user AND ms.status = :status")
    Optional<MessageStatus> findByMessageAndUserAndStatus(
            @Param("message") Message message, 
            @Param("user") User user, 
            @Param("status") MessageStatusType status);

    @Query("SELECT ms FROM MessageStatus ms WHERE ms.message = :message")
    List<MessageStatus> findByMessage(@Param("message") Message message);

    @Query("SELECT ms FROM MessageStatus ms WHERE ms.message IN :messages AND ms.user = :user AND ms.status = :status")
    List<MessageStatus> findByMessagesAndUserAndStatus(
            @Param("messages") List<Message> messages, 
            @Param("user") User user, 
            @Param("status") MessageStatusType status);

    @Query("SELECT CASE WHEN COUNT(ms) > 0 THEN true ELSE false END " +
           "FROM MessageStatus ms WHERE ms.message = :message AND ms.user = :user AND ms.status = :status")
    boolean existsByMessageAndUserAndStatus(
            @Param("message") Message message, 
            @Param("user") User user, 
            @Param("status") MessageStatusType status);

    @Query("SELECT ms FROM MessageStatus ms WHERE ms.user = :user AND ms.status = :status")
    List<MessageStatus> findByUserAndStatus(
            @Param("user") User user, 
            @Param("status") MessageStatusType status);

    void deleteByMessageAndUser(Message message, User user);
}