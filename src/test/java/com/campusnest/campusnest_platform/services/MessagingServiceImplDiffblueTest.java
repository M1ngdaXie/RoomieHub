package com.campusnest.campusnest_platform.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.campusnest.campusnest_platform.enums.MessageType;
import com.campusnest.campusnest_platform.enums.UserRole;
import com.campusnest.campusnest_platform.enums.VerificationStatus;
import com.campusnest.campusnest_platform.models.Conversation;
import com.campusnest.campusnest_platform.models.HousingListing;
import com.campusnest.campusnest_platform.models.Message;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.repository.message.ConversationRepository;
import com.campusnest.campusnest_platform.repository.housing.HousingListingRepository;
import com.campusnest.campusnest_platform.repository.message.MessageRepository;
import com.campusnest.campusnest_platform.repository.message.MessageStatusRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {MessagingServiceImpl.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class MessagingServiceImplDiffblueTest {
    @MockitoBean
    private ConversationRepository conversationRepository;

    @MockitoBean
    private HousingListingRepository housingListingRepository;

    @MockitoBean
    private MessageRepository messageRepository;

    @MockitoBean
    private MessageStatusRepository messageStatusRepository;

    @Autowired
    private MessagingServiceImpl messagingServiceImpl;

    /**
     * Test {@link MessagingServiceImpl#createOrGetConversation(User, User, HousingListing)}.
     *
     * <ul>
     *   <li>Then throw {@link IllegalArgumentException}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#createOrGetConversation(User, User,
     * HousingListing)}
     */
    @Test
    @DisplayName(
            "Test createOrGetConversation(User, User, HousingListing); then throw IllegalArgumentException")
    @Tag("MaintainedByDiffblue")
    void testCreateOrGetConversation_thenThrowIllegalArgumentException() {
        // Arrange
        when(housingListingRepository.findById(Mockito.<Long>any()))
                .thenThrow(new IllegalArgumentException());

        User user1 = new User();
        user1.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user1.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user1.setAccountLocked(true);
        user1.setActive(true);
        user1.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user1.setEmail("jane.doe@example.org");
        user1.setEmailVerified(true);
        user1.setFirstName("Jane");
        user1.setId(1L);
        user1.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user1.setLastName("Doe");
        user1.setPassword("iloveyou");
        user1.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user1.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user1.setRole(UserRole.STUDENT);
        user1.setUniversityDomain("University Domain");
        user1.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user1.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user1.updateLastLogin();

        User user2 = new User();
        user2.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user2.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user2.setAccountLocked(true);
        user2.setActive(true);
        user2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user2.setEmail("jane.doe@example.org");
        user2.setEmailVerified(true);
        user2.setFirstName("Jane");
        user2.setId(1L);
        user2.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user2.setLastName("Doe");
        user2.setPassword("iloveyou");
        user2.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user2.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user2.setRole(UserRole.STUDENT);
        user2.setUniversityDomain("University Domain");
        user2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user2.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user2.updateLastLogin();

        User owner = new User();
        owner.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setAccountLocked(true);
        owner.setActive(true);
        owner.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setEmail("jane.doe@example.org");
        owner.setEmailVerified(true);
        owner.setFirstName("Jane");
        owner.setId(1L);
        owner.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setLastName("Doe");
        owner.setPassword("iloveyou");
        owner.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setRole(UserRole.STUDENT);
        owner.setUniversityDomain("University Domain");
        owner.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        owner.updateLastLogin();

        HousingListing listing = new HousingListing();
        listing.setAddress("42 Main St");
        listing.setAvailableFrom(LocalDate.of(1970, 1, 1));
        listing.setAvailableTo(LocalDate.of(1970, 1, 1));
        listing.setBathrooms(1000);
        listing.setBedrooms(1000);
        listing.setCity("Oxford");
        listing.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        listing.setDescription("The characteristics of someone or something");
        listing.setFavorites(new ArrayList<>());
        listing.setId(1L);
        listing.setImages(new ArrayList<>());
        listing.setIsActive(true);
        listing.setOwner(owner);
        listing.setPrice(new BigDecimal("2.3"));
        listing.setTitle("Dr");
        listing.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.createOrGetConversation(user1, user2, listing));
        verify(housingListingRepository).findById(1L);
    }

    /**
     * Test {@link MessagingServiceImpl#sendMessage(Long, User, String)} with {@code conversationId},
     * {@code sender}, {@code content}.
     *
     * <p>Method under test: {@link MessagingServiceImpl#sendMessage(Long, User, String)}
     */
    @Test
    @DisplayName("Test sendMessage(Long, User, String) with 'conversationId', 'sender', 'content'")
    @Tag("MaintainedByDiffblue")
    void testSendMessageWithConversationIdSenderContent() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User sender = new User();
        sender.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setAccountLocked(true);
        sender.setActive(true);
        sender.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setEmail("Sending {} message in conversation {} from user {}");
        sender.setEmailVerified(true);
        sender.setFirstName("Jane");
        sender.setId(1L);
        sender.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setLastName("Doe");
        sender.setPassword("iloveyou");
        sender.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setRole(UserRole.STUDENT);
        sender.setUniversityDomain("University Domain");
        sender.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        sender.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.sendMessage(1L, sender, "Not all who wander are lost"));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#sendMessage(Long, User, String, MessageType)} with {@code
     * conversationId}, {@code sender}, {@code content}, {@code messageType}.
     *
     * <p>Method under test: {@link MessagingServiceImpl#sendMessage(Long, User, String, MessageType)}
     */
    @Test
    @DisplayName(
            "Test sendMessage(Long, User, String, MessageType) with 'conversationId', 'sender', 'content', 'messageType'")
    @Tag("MaintainedByDiffblue")
    void testSendMessageWithConversationIdSenderContentMessageType() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User sender = new User();
        sender.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setAccountLocked(true);
        sender.setActive(true);
        sender.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setEmail("Sending {} message in conversation {} from user {}");
        sender.setEmailVerified(true);
        sender.setFirstName("Jane");
        sender.setId(1L);
        sender.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setLastName("Doe");
        sender.setPassword("iloveyou");
        sender.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setRole(UserRole.STUDENT);
        sender.setUniversityDomain("University Domain");
        sender.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        sender.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        messagingServiceImpl.sendMessage(
                                1L, sender, "Not all who wander are lost", MessageType.TEXT));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#sendMessage(Long, User, String, MessageType)} with {@code
     * conversationId}, {@code sender}, {@code content}, {@code messageType}.
     *
     * <ul>
     *   <li>Given {@code jane.doe@example.org}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#sendMessage(Long, User, String, MessageType)}
     */
    @Test
    @DisplayName(
            "Test sendMessage(Long, User, String, MessageType) with 'conversationId', 'sender', 'content', 'messageType'; given 'jane.doe@example.org'")
    @Tag("MaintainedByDiffblue")
    void testSendMessageWithConversationIdSenderContentMessageType_givenJaneDoeExampleOrg() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User sender = new User();
        sender.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setAccountLocked(true);
        sender.setActive(true);
        sender.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setEmail("jane.doe@example.org");
        sender.setEmailVerified(true);
        sender.setFirstName("Jane");
        sender.setId(1L);
        sender.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setLastName("Doe");
        sender.setPassword("iloveyou");
        sender.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setRole(UserRole.STUDENT);
        sender.setUniversityDomain("University Domain");
        sender.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        sender.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        messagingServiceImpl.sendMessage(
                                1L, sender, "Not all who wander are lost", MessageType.TEXT));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#sendMessage(Long, User, String, MessageType)} with {@code
     * conversationId}, {@code sender}, {@code content}, {@code messageType}.
     *
     * <ul>
     *   <li>Given {@code null}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#sendMessage(Long, User, String, MessageType)}
     */
    @Test
    @DisplayName(
            "Test sendMessage(Long, User, String, MessageType) with 'conversationId', 'sender', 'content', 'messageType'; given 'null'")
    @Tag("MaintainedByDiffblue")
    void testSendMessageWithConversationIdSenderContentMessageType_givenNull() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User sender = new User();
        sender.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setAccountLocked(true);
        sender.setActive(true);
        sender.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setEmail(null);
        sender.setEmailVerified(true);
        sender.setFirstName("Jane");
        sender.setId(1L);
        sender.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setLastName("Doe");
        sender.setPassword("iloveyou");
        sender.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setRole(UserRole.STUDENT);
        sender.setUniversityDomain("University Domain");
        sender.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        sender.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        messagingServiceImpl.sendMessage(
                                1L, sender, "Not all who wander are lost", MessageType.TEXT));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#sendMessage(Long, User, String)} with {@code conversationId},
     * {@code sender}, {@code content}.
     *
     * <ul>
     *   <li>Given {@code jane.doe@example.org}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#sendMessage(Long, User, String)}
     */
    @Test
    @DisplayName(
            "Test sendMessage(Long, User, String) with 'conversationId', 'sender', 'content'; given 'jane.doe@example.org'")
    @Tag("MaintainedByDiffblue")
    void testSendMessageWithConversationIdSenderContent_givenJaneDoeExampleOrg() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User sender = new User();
        sender.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setAccountLocked(true);
        sender.setActive(true);
        sender.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setEmail("jane.doe@example.org");
        sender.setEmailVerified(true);
        sender.setFirstName("Jane");
        sender.setId(1L);
        sender.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setLastName("Doe");
        sender.setPassword("iloveyou");
        sender.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setRole(UserRole.STUDENT);
        sender.setUniversityDomain("University Domain");
        sender.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        sender.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.sendMessage(1L, sender, "Not all who wander are lost"));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#sendMessage(Long, User, String)} with {@code conversationId},
     * {@code sender}, {@code content}.
     *
     * <ul>
     *   <li>Given {@code null}.
     *   <li>When {@link User} (default constructor) Email is {@code null}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#sendMessage(Long, User, String)}
     */
    @Test
    @DisplayName(
            "Test sendMessage(Long, User, String) with 'conversationId', 'sender', 'content'; given 'null'; when User (default constructor) Email is 'null'")
    @Tag("MaintainedByDiffblue")
    void testSendMessageWithConversationIdSenderContent_givenNull_whenUserEmailIsNull() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User sender = new User();
        sender.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setAccountLocked(true);
        sender.setActive(true);
        sender.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setEmail(null);
        sender.setEmailVerified(true);
        sender.setFirstName("Jane");
        sender.setId(1L);
        sender.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setLastName("Doe");
        sender.setPassword("iloveyou");
        sender.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setRole(UserRole.STUDENT);
        sender.setUniversityDomain("University Domain");
        sender.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        sender.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.sendMessage(1L, sender, "Not all who wander are lost"));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#getConversationMessages(Long, User)} with {@code
     * conversationId}, {@code requestingUser}.
     *
     * <p>Method under test: {@link MessagingServiceImpl#getConversationMessages(Long, User)}
     */
    @Test
    @DisplayName("Test getConversationMessages(Long, User) with 'conversationId', 'requestingUser'")
    @Tag("MaintainedByDiffblue")
    void testGetConversationMessagesWithConversationIdRequestingUser() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User requestingUser = new User();
        requestingUser.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setAccountLocked(true);
        requestingUser.setActive(true);
        requestingUser.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setEmail("jane.doe@example.org");
        requestingUser.setEmailVerified(true);
        requestingUser.setFirstName("Jane");
        requestingUser.setId(1L);
        requestingUser.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setLastName("Doe");
        requestingUser.setPassword("iloveyou");
        requestingUser.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setRole(UserRole.STUDENT);
        requestingUser.setUniversityDomain("University Domain");
        requestingUser.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        requestingUser.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.getConversationMessages(1L, requestingUser));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#getConversationMessages(Long, User)} with {@code
     * conversationId}, {@code requestingUser}.
     *
     * <p>Method under test: {@link MessagingServiceImpl#getConversationMessages(Long, User)}
     */
    @Test
    @DisplayName("Test getConversationMessages(Long, User) with 'conversationId', 'requestingUser'")
    @Tag("MaintainedByDiffblue")
    void testGetConversationMessagesWithConversationIdRequestingUser2() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User requestingUser = new User();
        requestingUser.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setAccountLocked(true);
        requestingUser.setActive(true);
        requestingUser.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setEmail("Getting all messages for conversation {} for user {}");
        requestingUser.setEmailVerified(true);
        requestingUser.setFirstName("Jane");
        requestingUser.setId(1L);
        requestingUser.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setLastName("Doe");
        requestingUser.setPassword("iloveyou");
        requestingUser.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setRole(UserRole.STUDENT);
        requestingUser.setUniversityDomain("University Domain");
        requestingUser.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        requestingUser.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.getConversationMessages(1L, requestingUser));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#getConversationMessages(Long, User, Pageable)} with {@code
     * conversationId}, {@code requestingUser}, {@code pageable}.
     *
     * <p>Method under test: {@link MessagingServiceImpl#getConversationMessages(Long, User,
     * Pageable)}
     */
    @Test
    @DisplayName(
            "Test getConversationMessages(Long, User, Pageable) with 'conversationId', 'requestingUser', 'pageable'")
    @Tag("MaintainedByDiffblue")
    void testGetConversationMessagesWithConversationIdRequestingUserPageable() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User requestingUser = new User();
        requestingUser.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setAccountLocked(true);
        requestingUser.setActive(true);
        requestingUser.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setEmail("Getting messages for conversation {} for user {}");
        requestingUser.setEmailVerified(true);
        requestingUser.setFirstName("Jane");
        requestingUser.setId(1L);
        requestingUser.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setLastName("Doe");
        requestingUser.setPassword("iloveyou");
        requestingUser.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setRole(UserRole.STUDENT);
        requestingUser.setUniversityDomain("University Domain");
        requestingUser.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        requestingUser.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.getConversationMessages(1L, requestingUser, null));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#getConversationMessages(Long, User, Pageable)} with {@code
     * conversationId}, {@code requestingUser}, {@code pageable}.
     *
     * <p>Method under test: {@link MessagingServiceImpl#getConversationMessages(Long, User,
     * Pageable)}
     */
    @Test
    @DisplayName(
            "Test getConversationMessages(Long, User, Pageable) with 'conversationId', 'requestingUser', 'pageable'")
    @Tag("MaintainedByDiffblue")
    void testGetConversationMessagesWithConversationIdRequestingUserPageable2() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User requestingUser = new User();
        requestingUser.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setAccountLocked(true);
        requestingUser.setActive(true);
        requestingUser.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setEmail("jane.doe@example.org");
        requestingUser.setEmailVerified(true);
        requestingUser.setFirstName("Jane");
        requestingUser.setId(1L);
        requestingUser.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setLastName("Doe");
        requestingUser.setPassword("iloveyou");
        requestingUser.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setRole(UserRole.STUDENT);
        requestingUser.setUniversityDomain("University Domain");
        requestingUser.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        requestingUser.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.getConversationMessages(1L, requestingUser, null));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#getConversationMessages(Long, User, Pageable)} with {@code
     * conversationId}, {@code requestingUser}, {@code pageable}.
     *
     * <ul>
     *   <li>Given {@code null}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#getConversationMessages(Long, User,
     * Pageable)}
     */
    @Test
    @DisplayName(
            "Test getConversationMessages(Long, User, Pageable) with 'conversationId', 'requestingUser', 'pageable'; given 'null'")
    @Tag("MaintainedByDiffblue")
    void testGetConversationMessagesWithConversationIdRequestingUserPageable_givenNull() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User requestingUser = new User();
        requestingUser.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setAccountLocked(true);
        requestingUser.setActive(true);
        requestingUser.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setEmail(null);
        requestingUser.setEmailVerified(true);
        requestingUser.setFirstName("Jane");
        requestingUser.setId(1L);
        requestingUser.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setLastName("Doe");
        requestingUser.setPassword("iloveyou");
        requestingUser.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setRole(UserRole.STUDENT);
        requestingUser.setUniversityDomain("University Domain");
        requestingUser.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        requestingUser.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.getConversationMessages(1L, requestingUser, null));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#getConversationMessages(Long, User)} with {@code
     * conversationId}, {@code requestingUser}.
     *
     * <ul>
     *   <li>Given {@code null}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#getConversationMessages(Long, User)}
     */
    @Test
    @DisplayName(
            "Test getConversationMessages(Long, User) with 'conversationId', 'requestingUser'; given 'null'")
    @Tag("MaintainedByDiffblue")
    void testGetConversationMessagesWithConversationIdRequestingUser_givenNull() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User requestingUser = new User();
        requestingUser.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setAccountLocked(true);
        requestingUser.setActive(true);
        requestingUser.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setEmail(null);
        requestingUser.setEmailVerified(true);
        requestingUser.setFirstName("Jane");
        requestingUser.setId(1L);
        requestingUser.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setLastName("Doe");
        requestingUser.setPassword("iloveyou");
        requestingUser.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setRole(UserRole.STUDENT);
        requestingUser.setUniversityDomain("University Domain");
        requestingUser.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        requestingUser.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.getConversationMessages(1L, requestingUser));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#getRecentMessages(Long, User, LocalDateTime)}.
     *
     * <p>Method under test: {@link MessagingServiceImpl#getRecentMessages(Long, User, LocalDateTime)}
     */
    @Test
    @DisplayName("Test getRecentMessages(Long, User, LocalDateTime)")
    @Tag("MaintainedByDiffblue")
    void testGetRecentMessages() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User requestingUser = new User();
        requestingUser.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setAccountLocked(true);
        requestingUser.setActive(true);
        requestingUser.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setEmail("jane.doe@example.org");
        requestingUser.setEmailVerified(true);
        requestingUser.setFirstName("Jane");
        requestingUser.setId(1L);
        requestingUser.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setLastName("Doe");
        requestingUser.setPassword("iloveyou");
        requestingUser.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setRole(UserRole.STUDENT);
        requestingUser.setUniversityDomain("University Domain");
        requestingUser.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        requestingUser.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        requestingUser.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () ->
                        messagingServiceImpl.getRecentMessages(
                                1L, requestingUser, LocalDate.of(1970, 1, 1).atStartOfDay()));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#getUserConversations(User, Pageable)} with {@code user},
     * {@code pageable}.
     *
     * <p>Method under test: {@link MessagingServiceImpl#getUserConversations(User, Pageable)}
     */
    @Test
    @DisplayName("Test getUserConversations(User, Pageable) with 'user', 'pageable'")
    @Tag("MaintainedByDiffblue")
    void testGetUserConversationsWithUserPageable() {
        // Arrange
        PageImpl<Conversation> pageImpl = new PageImpl<>(new ArrayList<>());
        when(conversationRepository.findByUserOrderByLastMessageDesc(
                Mockito.<User>any(), Mockito.<Pageable>any()))
                .thenReturn(pageImpl);

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("Getting conversations with pagination for user {}");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act
        Page<Conversation> actualUserConversations =
                messagingServiceImpl.getUserConversations(user, null);

        // Assert
        verify(conversationRepository).findByUserOrderByLastMessageDesc(isA(User.class), isNull());
        assertTrue(actualUserConversations instanceof PageImpl);
        assertTrue(actualUserConversations.toList().isEmpty());
        assertSame(pageImpl, actualUserConversations);
    }

    /**
     * Test {@link MessagingServiceImpl#getUserConversations(User, Pageable)} with {@code user},
     * {@code pageable}.
     *
     * <ul>
     *   <li>Given {@code jane.doe@example.org}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#getUserConversations(User, Pageable)}
     */
    @Test
    @DisplayName(
            "Test getUserConversations(User, Pageable) with 'user', 'pageable'; given 'jane.doe@example.org'")
    @Tag("MaintainedByDiffblue")
    void testGetUserConversationsWithUserPageable_givenJaneDoeExampleOrg() {
        // Arrange
        PageImpl<Conversation> pageImpl = new PageImpl<>(new ArrayList<>());
        when(conversationRepository.findByUserOrderByLastMessageDesc(
                Mockito.<User>any(), Mockito.<Pageable>any()))
                .thenReturn(pageImpl);

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act
        Page<Conversation> actualUserConversations =
                messagingServiceImpl.getUserConversations(user, null);

        // Assert
        verify(conversationRepository).findByUserOrderByLastMessageDesc(isA(User.class), isNull());
        assertTrue(actualUserConversations instanceof PageImpl);
        assertTrue(actualUserConversations.toList().isEmpty());
        assertSame(pageImpl, actualUserConversations);
    }

    /**
     * Test {@link MessagingServiceImpl#getUserConversations(User, Pageable)} with {@code user},
     * {@code pageable}.
     *
     * <ul>
     *   <li>Given {@code null}.
     *   <li>When {@link User} (default constructor) Email is {@code null}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#getUserConversations(User, Pageable)}
     */
    @Test
    @DisplayName(
            "Test getUserConversations(User, Pageable) with 'user', 'pageable'; given 'null'; when User (default constructor) Email is 'null'")
    @Tag("MaintainedByDiffblue")
    void testGetUserConversationsWithUserPageable_givenNull_whenUserEmailIsNull() {
        // Arrange
        PageImpl<Conversation> pageImpl = new PageImpl<>(new ArrayList<>());
        when(conversationRepository.findByUserOrderByLastMessageDesc(
                Mockito.<User>any(), Mockito.<Pageable>any()))
                .thenReturn(pageImpl);

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail(null);
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act
        Page<Conversation> actualUserConversations =
                messagingServiceImpl.getUserConversations(user, null);

        // Assert
        verify(conversationRepository).findByUserOrderByLastMessageDesc(isA(User.class), isNull());
        assertTrue(actualUserConversations instanceof PageImpl);
        assertTrue(actualUserConversations.toList().isEmpty());
        assertSame(pageImpl, actualUserConversations);
    }

    /**
     * Test {@link MessagingServiceImpl#getUserConversations(User, Pageable)} with {@code user},
     * {@code pageable}.
     *
     * <ul>
     *   <li>Then throw {@link IllegalArgumentException}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#getUserConversations(User, Pageable)}
     */
    @Test
    @DisplayName(
            "Test getUserConversations(User, Pageable) with 'user', 'pageable'; then throw IllegalArgumentException")
    @Tag("MaintainedByDiffblue")
    void testGetUserConversationsWithUserPageable_thenThrowIllegalArgumentException() {
        // Arrange
        when(conversationRepository.findByUserOrderByLastMessageDesc(
                Mockito.<User>any(), Mockito.<Pageable>any()))
                .thenThrow(new IllegalArgumentException());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.getUserConversations(user, null));
        verify(conversationRepository).findByUserOrderByLastMessageDesc(isA(User.class), isNull());
    }

    /**
     * Test {@link MessagingServiceImpl#getUserConversations(User)} with {@code user}.
     *
     * <ul>
     *   <li>Given {@code Getting conversations for user {}}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#getUserConversations(User)}
     */
    @Test
    @DisplayName(
            "Test getUserConversations(User) with 'user'; given 'Getting conversations for user {}'")
    @Tag("MaintainedByDiffblue")
    void testGetUserConversationsWithUser_givenGettingConversationsForUser() {
        // Arrange
        when(conversationRepository.findByUserOrderByLastMessageDesc(Mockito.<User>any()))
                .thenReturn(new ArrayList<>());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("Getting conversations for user {}");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act
        List<Conversation> actualUserConversations = messagingServiceImpl.getUserConversations(user);

        // Assert
        verify(conversationRepository).findByUserOrderByLastMessageDesc(isA(User.class));
        assertTrue(actualUserConversations.isEmpty());
    }

    /**
     * Test {@link MessagingServiceImpl#getUserConversations(User)} with {@code user}.
     *
     * <ul>
     *   <li>Given {@code jane.doe@example.org}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#getUserConversations(User)}
     */
    @Test
    @DisplayName("Test getUserConversations(User) with 'user'; given 'jane.doe@example.org'")
    @Tag("MaintainedByDiffblue")
    void testGetUserConversationsWithUser_givenJaneDoeExampleOrg() {
        // Arrange
        when(conversationRepository.findByUserOrderByLastMessageDesc(Mockito.<User>any()))
                .thenReturn(new ArrayList<>());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act
        List<Conversation> actualUserConversations = messagingServiceImpl.getUserConversations(user);

        // Assert
        verify(conversationRepository).findByUserOrderByLastMessageDesc(isA(User.class));
        assertTrue(actualUserConversations.isEmpty());
    }

    /**
     * Test {@link MessagingServiceImpl#getUserConversations(User)} with {@code user}.
     *
     * <ul>
     *   <li>Given {@code null}.
     *   <li>When {@link User} (default constructor) Email is {@code null}.
     *   <li>Then return Empty.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#getUserConversations(User)}
     */
    @Test
    @DisplayName(
            "Test getUserConversations(User) with 'user'; given 'null'; when User (default constructor) Email is 'null'; then return Empty")
    @Tag("MaintainedByDiffblue")
    void testGetUserConversationsWithUser_givenNull_whenUserEmailIsNull_thenReturnEmpty() {
        // Arrange
        when(conversationRepository.findByUserOrderByLastMessageDesc(Mockito.<User>any()))
                .thenReturn(new ArrayList<>());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail(null);
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act
        List<Conversation> actualUserConversations = messagingServiceImpl.getUserConversations(user);

        // Assert
        verify(conversationRepository).findByUserOrderByLastMessageDesc(isA(User.class));
        assertTrue(actualUserConversations.isEmpty());
    }

    /**
     * Test {@link MessagingServiceImpl#getUserConversations(User)} with {@code user}.
     *
     * <ul>
     *   <li>Then throw {@link IllegalArgumentException}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#getUserConversations(User)}
     */
    @Test
    @DisplayName("Test getUserConversations(User) with 'user'; then throw IllegalArgumentException")
    @Tag("MaintainedByDiffblue")
    void testGetUserConversationsWithUser_thenThrowIllegalArgumentException() {
        // Arrange
        when(conversationRepository.findByUserOrderByLastMessageDesc(Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class, () -> messagingServiceImpl.getUserConversations(user));
        verify(conversationRepository).findByUserOrderByLastMessageDesc(isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#markMessagesAsRead(Long, User)}.
     *
     * <ul>
     *   <li>Given {@code jane.doe@example.org}.
     *   <li>When {@link User} (default constructor) Email is {@code jane.doe@example.org}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#markMessagesAsRead(Long, User)}
     */
    @Test
    @DisplayName(
            "Test markMessagesAsRead(Long, User); given 'jane.doe@example.org'; when User (default constructor) Email is 'jane.doe@example.org'")
    @Tag("MaintainedByDiffblue")
    void testMarkMessagesAsRead_givenJaneDoeExampleOrg_whenUserEmailIsJaneDoeExampleOrg() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class, () -> messagingServiceImpl.markMessagesAsRead(1L, user));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#markMessagesAsRead(Long, User)}.
     *
     * <ul>
     *   <li>Given {@code Marking messages as read in conversation {} for user {}}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#markMessagesAsRead(Long, User)}
     */
    @Test
    @DisplayName(
            "Test markMessagesAsRead(Long, User); given 'Marking messages as read in conversation {} for user {}'")
    @Tag("MaintainedByDiffblue")
    void testMarkMessagesAsRead_givenMarkingMessagesAsReadInConversationForUser() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("Marking messages as read in conversation {} for user {}");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class, () -> messagingServiceImpl.markMessagesAsRead(1L, user));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#markMessagesAsRead(Long, User)}.
     *
     * <ul>
     *   <li>Given {@code null}.
     *   <li>When {@link User} (default constructor) Email is {@code null}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#markMessagesAsRead(Long, User)}
     */
    @Test
    @DisplayName(
            "Test markMessagesAsRead(Long, User); given 'null'; when User (default constructor) Email is 'null'")
    @Tag("MaintainedByDiffblue")
    void testMarkMessagesAsRead_givenNull_whenUserEmailIsNull() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail(null);
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class, () -> messagingServiceImpl.markMessagesAsRead(1L, user));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#markMessageAsRead(Long, User)}.
     *
     * <ul>
     *   <li>Given {@code jane.doe@example.org}.
     *   <li>When {@link User} (default constructor) Email is {@code jane.doe@example.org}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#markMessageAsRead(Long, User)}
     */
    @Test
    @DisplayName(
            "Test markMessageAsRead(Long, User); given 'jane.doe@example.org'; when User (default constructor) Email is 'jane.doe@example.org'")
    @Tag("MaintainedByDiffblue")
    void testMarkMessageAsRead_givenJaneDoeExampleOrg_whenUserEmailIsJaneDoeExampleOrg() {
        // Arrange
        when(messageRepository.findById(Mockito.<Long>any())).thenThrow(new IllegalArgumentException());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class, () -> messagingServiceImpl.markMessageAsRead(1L, user));
        verify(messageRepository).findById(1L);
    }

    /**
     * Test {@link MessagingServiceImpl#markMessageAsRead(Long, User)}.
     *
     * <ul>
     *   <li>Given {@code Marking message {} as read for user {}}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#markMessageAsRead(Long, User)}
     */
    @Test
    @DisplayName("Test markMessageAsRead(Long, User); given 'Marking message {} as read for user {}'")
    @Tag("MaintainedByDiffblue")
    void testMarkMessageAsRead_givenMarkingMessageAsReadForUser() {
        // Arrange
        when(messageRepository.findById(Mockito.<Long>any())).thenThrow(new IllegalArgumentException());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("Marking message {} as read for user {}");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class, () -> messagingServiceImpl.markMessageAsRead(1L, user));
        verify(messageRepository).findById(1L);
    }

    /**
     * Test {@link MessagingServiceImpl#markMessageAsRead(Long, User)}.
     *
     * <ul>
     *   <li>Given {@code null}.
     *   <li>When {@link User} (default constructor) Email is {@code null}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#markMessageAsRead(Long, User)}
     */
    @Test
    @DisplayName(
            "Test markMessageAsRead(Long, User); given 'null'; when User (default constructor) Email is 'null'")
    @Tag("MaintainedByDiffblue")
    void testMarkMessageAsRead_givenNull_whenUserEmailIsNull() {
        // Arrange
        when(messageRepository.findById(Mockito.<Long>any())).thenThrow(new IllegalArgumentException());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail(null);
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class, () -> messagingServiceImpl.markMessageAsRead(1L, user));
        verify(messageRepository).findById(1L);
    }

    /**
     * Test {@link MessagingServiceImpl#getUnreadMessageCount(Long, User)}.
     *
     * <p>Method under test: {@link MessagingServiceImpl#getUnreadMessageCount(Long, User)}
     */
    @Test
    @DisplayName("Test getUnreadMessageCount(Long, User)")
    @Tag("MaintainedByDiffblue")
    void testGetUnreadMessageCount() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class, () -> messagingServiceImpl.getUnreadMessageCount(1L, user));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#getTotalUnreadMessageCount(User)}.
     *
     * <ul>
     *   <li>Then return zero.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#getTotalUnreadMessageCount(User)}
     */
    @Test
    @DisplayName("Test getTotalUnreadMessageCount(User); then return zero")
    @Tag("MaintainedByDiffblue")
    void testGetTotalUnreadMessageCount_thenReturnZero() {
        // Arrange
        when(messageRepository.findAllUnreadMessagesForUser(Mockito.<User>any()))
                .thenReturn(new ArrayList<>());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act
//        long actualTotalUnreadMessageCount = messagingServiceImpl.getTotalUnreadMessageCount(user);

        // Assert
        verify(messageRepository).findAllUnreadMessagesForUser(isA(User.class));
//        assertEquals(0L, actualTotalUnreadMessageCount);
    }

    /**
     * Test {@link MessagingServiceImpl#getTotalUnreadMessageCount(User)}.
     *
     * <ul>
     *   <li>Then throw {@link IllegalArgumentException}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#getTotalUnreadMessageCount(User)}
     */
    @Test
    @DisplayName("Test getTotalUnreadMessageCount(User); then throw IllegalArgumentException")
    @Tag("MaintainedByDiffblue")
    void testGetTotalUnreadMessageCount_thenThrowIllegalArgumentException() {
        // Arrange
        when(messageRepository.findAllUnreadMessagesForUser(Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.getTotalUnreadMessageCount(user));
        verify(messageRepository).findAllUnreadMessagesForUser(isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#getConversation(Long, User)}.
     *
     * <ul>
     *   <li>Then return {@link Conversation#Conversation()}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#getConversation(Long, User)}
     */
    @Test
    @DisplayName("Test getConversation(Long, User); then return Conversation()")
    @Tag("MaintainedByDiffblue")
    void testGetConversation_thenReturnConversation() {
        // Arrange
        User owner = new User();
        owner.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setAccountLocked(true);
        owner.setActive(true);
        owner.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setEmail("jane.doe@example.org");
        owner.setEmailVerified(true);
        owner.setFirstName("Jane");
        owner.setId(1L);
        owner.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setLastName("Doe");
        owner.setPassword("iloveyou");
        owner.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setRole(UserRole.STUDENT);
        owner.setUniversityDomain("University Domain");
        owner.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        owner.updateLastLogin();

        HousingListing housingListing = new HousingListing();
        housingListing.setAddress("42 Main St");
        housingListing.setAvailableFrom(LocalDate.of(1970, 1, 1));
        housingListing.setAvailableTo(LocalDate.of(1970, 1, 1));
        housingListing.setBathrooms(1000);
        housingListing.setBedrooms(1000);
        housingListing.setCity("Oxford");
        housingListing.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        housingListing.setDescription("The characteristics of someone or something");
        housingListing.setFavorites(new ArrayList<>());
        housingListing.setId(1L);
        housingListing.setImages(new ArrayList<>());
        housingListing.setIsActive(true);
        housingListing.setOwner(owner);
        housingListing.setPrice(new BigDecimal("2.3"));
        housingListing.setTitle("Dr");
        housingListing.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        User participant1 = new User();
        participant1.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setAccountLocked(true);
        participant1.setActive(true);
        participant1.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setEmail("jane.doe@example.org");
        participant1.setEmailVerified(true);
        participant1.setFirstName("Jane");
        participant1.setId(1L);
        participant1.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setLastName("Doe");
        participant1.setPassword("iloveyou");
        participant1.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setRole(UserRole.STUDENT);
        participant1.setUniversityDomain("University Domain");
        participant1.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        participant1.updateLastLogin();

        User participant2 = new User();
        participant2.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setAccountLocked(true);
        participant2.setActive(true);
        participant2.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setEmail("jane.doe@example.org");
        participant2.setEmailVerified(true);
        participant2.setFirstName("Jane");
        participant2.setId(1L);
        participant2.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setLastName("Doe");
        participant2.setPassword("iloveyou");
        participant2.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setRole(UserRole.STUDENT);
        participant2.setUniversityDomain("University Domain");
        participant2.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        participant2.updateLastLogin();

        Conversation conversation = new Conversation();
        conversation.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        conversation.setHousingListing(housingListing);
        conversation.setId(1L);
        conversation.setIsActive(true);
        conversation.setLastMessageAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        conversation.setParticipant1(participant1);
        conversation.setParticipant2(participant2);
        conversation.updateLastMessageTime();
        Optional<Conversation> ofResult = Optional.of(conversation);
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenReturn(ofResult);

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act
        Conversation actualConversation = messagingServiceImpl.getConversation(1L, user);

        // Assert
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
        assertSame(conversation, actualConversation);
    }

    /**
     * Test {@link MessagingServiceImpl#getConversation(Long, User)}.
     *
     * <ul>
     *   <li>Then throw {@link IllegalArgumentException}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#getConversation(Long, User)}
     */
    @Test
    @DisplayName("Test getConversation(Long, User); then throw IllegalArgumentException")
    @Tag("MaintainedByDiffblue")
    void testGetConversation_thenThrowIllegalArgumentException() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class, () -> messagingServiceImpl.getConversation(1L, user));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#getConversationWithParticipants(Long, User)}.
     *
     * <ul>
     *   <li>Then return {@link Conversation#Conversation()}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#getConversationWithParticipants(Long, User)}
     */
    @Test
    @DisplayName("Test getConversationWithParticipants(Long, User); then return Conversation()")
    @Tag("MaintainedByDiffblue")
    void testGetConversationWithParticipants_thenReturnConversation() {
        // Arrange
        User owner = new User();
        owner.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setAccountLocked(true);
        owner.setActive(true);
        owner.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setEmail("jane.doe@example.org");
        owner.setEmailVerified(true);
        owner.setFirstName("Jane");
        owner.setId(1L);
        owner.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setLastName("Doe");
        owner.setPassword("iloveyou");
        owner.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setRole(UserRole.STUDENT);
        owner.setUniversityDomain("University Domain");
        owner.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        owner.updateLastLogin();

        HousingListing housingListing = new HousingListing();
        housingListing.setAddress("42 Main St");
        housingListing.setAvailableFrom(LocalDate.of(1970, 1, 1));
        housingListing.setAvailableTo(LocalDate.of(1970, 1, 1));
        housingListing.setBathrooms(1000);
        housingListing.setBedrooms(1000);
        housingListing.setCity("Oxford");
        housingListing.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        housingListing.setDescription("The characteristics of someone or something");
        housingListing.setFavorites(new ArrayList<>());
        housingListing.setId(1L);
        housingListing.setImages(new ArrayList<>());
        housingListing.setIsActive(true);
        housingListing.setOwner(owner);
        housingListing.setPrice(new BigDecimal("2.3"));
        housingListing.setTitle("Dr");
        housingListing.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        User participant1 = new User();
        participant1.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setAccountLocked(true);
        participant1.setActive(true);
        participant1.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setEmail("jane.doe@example.org");
        participant1.setEmailVerified(true);
        participant1.setFirstName("Jane");
        participant1.setId(1L);
        participant1.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setLastName("Doe");
        participant1.setPassword("iloveyou");
        participant1.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setRole(UserRole.STUDENT);
        participant1.setUniversityDomain("University Domain");
        participant1.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        participant1.updateLastLogin();

        User participant2 = new User();
        participant2.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setAccountLocked(true);
        participant2.setActive(true);
        participant2.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setEmail("jane.doe@example.org");
        participant2.setEmailVerified(true);
        participant2.setFirstName("Jane");
        participant2.setId(1L);
        participant2.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setLastName("Doe");
        participant2.setPassword("iloveyou");
        participant2.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setRole(UserRole.STUDENT);
        participant2.setUniversityDomain("University Domain");
        participant2.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        participant2.updateLastLogin();

        Conversation conversation = new Conversation();
        conversation.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        conversation.setHousingListing(housingListing);
        conversation.setId(1L);
        conversation.setIsActive(true);
        conversation.setLastMessageAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        conversation.setParticipant1(participant1);
        conversation.setParticipant2(participant2);
        conversation.updateLastMessageTime();
        Optional<Conversation> ofResult = Optional.of(conversation);
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenReturn(ofResult);

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act
        Conversation actualConversationWithParticipants =
                messagingServiceImpl.getConversationWithParticipants(1L, user);

        // Assert
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
        assertSame(conversation, actualConversationWithParticipants);
    }

    /**
     * Test {@link MessagingServiceImpl#getConversationWithParticipants(Long, User)}.
     *
     * <ul>
     *   <li>Then throw {@link IllegalArgumentException}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#getConversationWithParticipants(Long, User)}
     */
    @Test
    @DisplayName(
            "Test getConversationWithParticipants(Long, User); then throw IllegalArgumentException")
    @Tag("MaintainedByDiffblue")
    void testGetConversationWithParticipants_thenThrowIllegalArgumentException() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.getConversationWithParticipants(1L, user));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#canUserAccessConversation(Long, User)}.
     *
     * <ul>
     *   <li>Then return {@code true}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#canUserAccessConversation(Long, User)}
     */
    @Test
    @DisplayName("Test canUserAccessConversation(Long, User); then return 'true'")
    @Tag("MaintainedByDiffblue")
    void testCanUserAccessConversation_thenReturnTrue() {
        // Arrange
        User owner = new User();
        owner.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setAccountLocked(true);
        owner.setActive(true);
        owner.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setEmail("jane.doe@example.org");
        owner.setEmailVerified(true);
        owner.setFirstName("Jane");
        owner.setId(1L);
        owner.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setLastName("Doe");
        owner.setPassword("iloveyou");
        owner.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setRole(UserRole.STUDENT);
        owner.setUniversityDomain("University Domain");
        owner.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        owner.updateLastLogin();

        HousingListing housingListing = new HousingListing();
        housingListing.setAddress("42 Main St");
        housingListing.setAvailableFrom(LocalDate.of(1970, 1, 1));
        housingListing.setAvailableTo(LocalDate.of(1970, 1, 1));
        housingListing.setBathrooms(1000);
        housingListing.setBedrooms(1000);
        housingListing.setCity("Oxford");
        housingListing.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        housingListing.setDescription("The characteristics of someone or something");
        housingListing.setFavorites(new ArrayList<>());
        housingListing.setId(1L);
        housingListing.setImages(new ArrayList<>());
        housingListing.setIsActive(true);
        housingListing.setOwner(owner);
        housingListing.setPrice(new BigDecimal("2.3"));
        housingListing.setTitle("Dr");
        housingListing.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        User participant1 = new User();
        participant1.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setAccountLocked(true);
        participant1.setActive(true);
        participant1.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setEmail("jane.doe@example.org");
        participant1.setEmailVerified(true);
        participant1.setFirstName("Jane");
        participant1.setId(1L);
        participant1.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setLastName("Doe");
        participant1.setPassword("iloveyou");
        participant1.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setRole(UserRole.STUDENT);
        participant1.setUniversityDomain("University Domain");
        participant1.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        participant1.updateLastLogin();

        User participant2 = new User();
        participant2.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setAccountLocked(true);
        participant2.setActive(true);
        participant2.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setEmail("jane.doe@example.org");
        participant2.setEmailVerified(true);
        participant2.setFirstName("Jane");
        participant2.setId(1L);
        participant2.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setLastName("Doe");
        participant2.setPassword("iloveyou");
        participant2.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setRole(UserRole.STUDENT);
        participant2.setUniversityDomain("University Domain");
        participant2.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        participant2.updateLastLogin();

        Conversation conversation = new Conversation();
        conversation.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        conversation.setHousingListing(housingListing);
        conversation.setId(1L);
        conversation.setIsActive(true);
        conversation.setLastMessageAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        conversation.setParticipant1(participant1);
        conversation.setParticipant2(participant2);
        conversation.updateLastMessageTime();
        Optional<Conversation> ofResult = Optional.of(conversation);
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenReturn(ofResult);

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act
        boolean actualCanUserAccessConversationResult =
                messagingServiceImpl.canUserAccessConversation(1L, user);

        // Assert
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
        assertTrue(actualCanUserAccessConversationResult);
    }

    /**
     * Test {@link MessagingServiceImpl#canUserAccessConversation(Long, User)}.
     *
     * <ul>
     *   <li>Then throw {@link IllegalArgumentException}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#canUserAccessConversation(Long, User)}
     */
    @Test
    @DisplayName("Test canUserAccessConversation(Long, User); then throw IllegalArgumentException")
    @Tag("MaintainedByDiffblue")
    void testCanUserAccessConversation_thenThrowIllegalArgumentException() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.canUserAccessConversation(1L, user));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#deactivateConversation(Long, User)}.
     *
     * <ul>
     *   <li>Given {@code Deactivating conversation {} for user {}}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#deactivateConversation(Long, User)}
     */
    @Test
    @DisplayName(
            "Test deactivateConversation(Long, User); given 'Deactivating conversation {} for user {}'")
    @Tag("MaintainedByDiffblue")
    void testDeactivateConversation_givenDeactivatingConversationForUser() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("Deactivating conversation {} for user {}");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.deactivateConversation(1L, user));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#deactivateConversation(Long, User)}.
     *
     * <ul>
     *   <li>Given {@code jane.doe@example.org}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#deactivateConversation(Long, User)}
     */
    @Test
    @DisplayName("Test deactivateConversation(Long, User); given 'jane.doe@example.org'")
    @Tag("MaintainedByDiffblue")
    void testDeactivateConversation_givenJaneDoeExampleOrg() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.deactivateConversation(1L, user));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#deactivateConversation(Long, User)}.
     *
     * <ul>
     *   <li>Given {@code null}.
     *   <li>When {@link User} (default constructor) Email is {@code null}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#deactivateConversation(Long, User)}
     */
    @Test
    @DisplayName(
            "Test deactivateConversation(Long, User); given 'null'; when User (default constructor) Email is 'null'")
    @Tag("MaintainedByDiffblue")
    void testDeactivateConversation_givenNull_whenUserEmailIsNull() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail(null);
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.deactivateConversation(1L, user));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }

    /**
     * Test {@link MessagingServiceImpl#getLatestMessage(Long)}.
     *
     * <p>Method under test: {@link MessagingServiceImpl#getLatestMessage(Long)}
     */
    @Test
    @DisplayName("Test getLatestMessage(Long)")
    @Tag("MaintainedByDiffblue")
    void testGetLatestMessage() {
        // Arrange
        when(conversationRepository.findById(Mockito.<Long>any()))
                .thenThrow(new IllegalArgumentException());

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> messagingServiceImpl.getLatestMessage(1L));
        verify(conversationRepository).findById(1L);
    }

    /**
     * Test {@link MessagingServiceImpl#getLatestMessage(Long)}.
     *
     * <p>Method under test: {@link MessagingServiceImpl#getLatestMessage(Long)}
     */
    @Test
    @DisplayName("Test getLatestMessage(Long)")
    @Tag("MaintainedByDiffblue")
    void testGetLatestMessage2() {
        // Arrange
        User owner = new User();
        owner.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setAccountLocked(true);
        owner.setActive(true);
        owner.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setEmail("jane.doe@example.org");
        owner.setEmailVerified(true);
        owner.setFirstName("Jane");
        owner.setId(1L);
        owner.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setLastName("Doe");
        owner.setPassword("iloveyou");
        owner.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setRole(UserRole.STUDENT);
        owner.setUniversityDomain("University Domain");
        owner.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        owner.updateLastLogin();

        HousingListing housingListing = new HousingListing();
        housingListing.setAddress("42 Main St");
        housingListing.setAvailableFrom(LocalDate.of(1970, 1, 1));
        housingListing.setAvailableTo(LocalDate.of(1970, 1, 1));
        housingListing.setBathrooms(1000);
        housingListing.setBedrooms(1000);
        housingListing.setCity("Oxford");
        housingListing.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        housingListing.setDescription("The characteristics of someone or something");
        housingListing.setFavorites(new ArrayList<>());
        housingListing.setId(1L);
        housingListing.setImages(new ArrayList<>());
        housingListing.setIsActive(true);
        housingListing.setOwner(owner);
        housingListing.setPrice(new BigDecimal("2.3"));
        housingListing.setTitle("Dr");
        housingListing.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        User participant1 = new User();
        participant1.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setAccountLocked(true);
        participant1.setActive(true);
        participant1.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setEmail("jane.doe@example.org");
        participant1.setEmailVerified(true);
        participant1.setFirstName("Jane");
        participant1.setId(1L);
        participant1.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setLastName("Doe");
        participant1.setPassword("iloveyou");
        participant1.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setRole(UserRole.STUDENT);
        participant1.setUniversityDomain("University Domain");
        participant1.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        participant1.updateLastLogin();

        User participant2 = new User();
        participant2.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setAccountLocked(true);
        participant2.setActive(true);
        participant2.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setEmail("jane.doe@example.org");
        participant2.setEmailVerified(true);
        participant2.setFirstName("Jane");
        participant2.setId(1L);
        participant2.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setLastName("Doe");
        participant2.setPassword("iloveyou");
        participant2.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setRole(UserRole.STUDENT);
        participant2.setUniversityDomain("University Domain");
        participant2.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        participant2.updateLastLogin();

        Conversation conversation = new Conversation();
        conversation.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        conversation.setHousingListing(housingListing);
        conversation.setId(1L);
        conversation.setIsActive(true);
        conversation.setLastMessageAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        conversation.setParticipant1(participant1);
        conversation.setParticipant2(participant2);
        conversation.updateLastMessageTime();
        Optional<Conversation> ofResult = Optional.of(conversation);
        when(conversationRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);
        when(messageRepository.findLatestMessageByConversation(Mockito.<Conversation>any()))
                .thenThrow(new IllegalArgumentException());

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> messagingServiceImpl.getLatestMessage(1L));
        verify(messageRepository).findLatestMessageByConversation(isA(Conversation.class));
        verify(conversationRepository).findById(1L);
    }

    /**
     * Test {@link MessagingServiceImpl#getLatestMessage(Long)}.
     *
     * <ul>
     *   <li>Then return {@link Message#Message()}.
     * </ul>
     *
     * <p>Method under test: {@link MessagingServiceImpl#getLatestMessage(Long)}
     */
    @Test
    @DisplayName("Test getLatestMessage(Long); then return Message()")
    @Tag("MaintainedByDiffblue")
    void testGetLatestMessage_thenReturnMessage() {
        // Arrange
        User owner = new User();
        owner.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setAccountLocked(true);
        owner.setActive(true);
        owner.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setEmail("jane.doe@example.org");
        owner.setEmailVerified(true);
        owner.setFirstName("Jane");
        owner.setId(1L);
        owner.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setLastName("Doe");
        owner.setPassword("iloveyou");
        owner.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setRole(UserRole.STUDENT);
        owner.setUniversityDomain("University Domain");
        owner.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        owner.updateLastLogin();

        HousingListing housingListing = new HousingListing();
        housingListing.setAddress("42 Main St");
        housingListing.setAvailableFrom(LocalDate.of(1970, 1, 1));
        housingListing.setAvailableTo(LocalDate.of(1970, 1, 1));
        housingListing.setBathrooms(1000);
        housingListing.setBedrooms(1000);
        housingListing.setCity("Oxford");
        housingListing.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        housingListing.setDescription("The characteristics of someone or something");
        housingListing.setFavorites(new ArrayList<>());
        housingListing.setId(1L);
        housingListing.setImages(new ArrayList<>());
        housingListing.setIsActive(true);
        housingListing.setOwner(owner);
        housingListing.setPrice(new BigDecimal("2.3"));
        housingListing.setTitle("Dr");
        housingListing.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        User participant1 = new User();
        participant1.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setAccountLocked(true);
        participant1.setActive(true);
        participant1.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setEmail("jane.doe@example.org");
        participant1.setEmailVerified(true);
        participant1.setFirstName("Jane");
        participant1.setId(1L);
        participant1.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setLastName("Doe");
        participant1.setPassword("iloveyou");
        participant1.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setRole(UserRole.STUDENT);
        participant1.setUniversityDomain("University Domain");
        participant1.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant1.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        participant1.updateLastLogin();

        User participant2 = new User();
        participant2.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setAccountLocked(true);
        participant2.setActive(true);
        participant2.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setEmail("jane.doe@example.org");
        participant2.setEmailVerified(true);
        participant2.setFirstName("Jane");
        participant2.setId(1L);
        participant2.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setLastName("Doe");
        participant2.setPassword("iloveyou");
        participant2.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setRole(UserRole.STUDENT);
        participant2.setUniversityDomain("University Domain");
        participant2.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant2.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        participant2.updateLastLogin();

        Conversation conversation = new Conversation();
        conversation.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        conversation.setHousingListing(housingListing);
        conversation.setId(1L);
        conversation.setIsActive(true);
        conversation.setLastMessageAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        conversation.setParticipant1(participant1);
        conversation.setParticipant2(participant2);
        conversation.updateLastMessageTime();
        Optional<Conversation> ofResult = Optional.of(conversation);
        when(conversationRepository.findById(Mockito.<Long>any())).thenReturn(ofResult);

        User owner2 = new User();
        owner2.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner2.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner2.setAccountLocked(true);
        owner2.setActive(true);
        owner2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner2.setEmail("jane.doe@example.org");
        owner2.setEmailVerified(true);
        owner2.setFirstName("Jane");
        owner2.setId(1L);
        owner2.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner2.setLastName("Doe");
        owner2.setPassword("iloveyou");
        owner2.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner2.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner2.setRole(UserRole.STUDENT);
        owner2.setUniversityDomain("University Domain");
        owner2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        owner2.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        owner2.updateLastLogin();

        HousingListing housingListing2 = new HousingListing();
        housingListing2.setAddress("42 Main St");
        housingListing2.setAvailableFrom(LocalDate.of(1970, 1, 1));
        housingListing2.setAvailableTo(LocalDate.of(1970, 1, 1));
        housingListing2.setBathrooms(1000);
        housingListing2.setBedrooms(1000);
        housingListing2.setCity("Oxford");
        housingListing2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        housingListing2.setDescription("The characteristics of someone or something");
        housingListing2.setFavorites(new ArrayList<>());
        housingListing2.setId(1L);
        housingListing2.setImages(new ArrayList<>());
        housingListing2.setIsActive(true);
        housingListing2.setOwner(owner2);
        housingListing2.setPrice(new BigDecimal("2.3"));
        housingListing2.setTitle("Dr");
        housingListing2.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());

        User participant12 = new User();
        participant12.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant12.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant12.setAccountLocked(true);
        participant12.setActive(true);
        participant12.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant12.setEmail("jane.doe@example.org");
        participant12.setEmailVerified(true);
        participant12.setFirstName("Jane");
        participant12.setId(1L);
        participant12.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant12.setLastName("Doe");
        participant12.setPassword("iloveyou");
        participant12.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant12.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant12.setRole(UserRole.STUDENT);
        participant12.setUniversityDomain("University Domain");
        participant12.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant12.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        participant12.updateLastLogin();

        User participant22 = new User();
        participant22.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant22.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant22.setAccountLocked(true);
        participant22.setActive(true);
        participant22.setCreatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant22.setEmail("jane.doe@example.org");
        participant22.setEmailVerified(true);
        participant22.setFirstName("Jane");
        participant22.setId(1L);
        participant22.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant22.setLastName("Doe");
        participant22.setPassword("iloveyou");
        participant22.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant22.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant22.setRole(UserRole.STUDENT);
        participant22.setUniversityDomain("University Domain");
        participant22.setUpdatedAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        participant22.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        participant22.updateLastLogin();

        Conversation conversation2 = new Conversation();
        conversation2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        conversation2.setHousingListing(housingListing2);
        conversation2.setId(1L);
        conversation2.setIsActive(true);
        conversation2.setLastMessageAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        conversation2.setParticipant1(participant12);
        conversation2.setParticipant2(participant22);
        conversation2.updateLastMessageTime();

        User sender = new User();
        sender.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setAccountLocked(true);
        sender.setActive(true);
        sender.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setEmail("jane.doe@example.org");
        sender.setEmailVerified(true);
        sender.setFirstName("Jane");
        sender.setId(1L);
        sender.setLastLoginAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setLastName("Doe");
        sender.setPassword("iloveyou");
        sender.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setRole(UserRole.STUDENT);
        sender.setUniversityDomain("University Domain");
        sender.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sender.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        sender.updateLastLogin();

        Message message = new Message();
        message.setContent("Not all who wander are lost");
        message.setConversation(conversation2);
        message.setEditedAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        message.setId(1L);
        message.setIsEdited(true);
        message.setMessageType(MessageType.TEXT);
        message.setSender(sender);
        message.setSentAt(LocalDate.of(1970, 1, 1).atStartOfDay());
        Optional<Message> ofResult2 = Optional.of(message);
        when(messageRepository.findLatestMessageByConversation(Mockito.<Conversation>any()))
                .thenReturn(ofResult2);

        // Act
        Message actualLatestMessage = messagingServiceImpl.getLatestMessage(1L);

        // Assert
        verify(messageRepository).findLatestMessageByConversation(isA(Conversation.class));
        verify(conversationRepository).findById(1L);
        assertSame(message, actualLatestMessage);
    }

    /**
     * Test {@link MessagingServiceImpl#validateConversationIntegrity(Long, User)}.
     *
     * <p>Method under test: {@link MessagingServiceImpl#validateConversationIntegrity(Long, User)}
     */
    @Test
    @DisplayName("Test validateConversationIntegrity(Long, User)")
    @Tag("MaintainedByDiffblue")
    void testValidateConversationIntegrity() {
        // Arrange
        when(conversationRepository.findByIdAndParticipant(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new IllegalArgumentException());

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("jane.doe@example.org");
        user.setEmailVerified(true);
        user.setFirstName("Jane");
        user.setId(1L);
        user.setLastLoginAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setLastName("Doe");
        user.setPassword("iloveyou");
        user.setPasswordExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setPasswordExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setRole(UserRole.STUDENT);
        user.setUniversityDomain("University Domain");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();

        // Act and Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> messagingServiceImpl.validateConversationIntegrity(1L, user));
        verify(conversationRepository).findByIdAndParticipant(eq(1L), isA(User.class));
    }
}
