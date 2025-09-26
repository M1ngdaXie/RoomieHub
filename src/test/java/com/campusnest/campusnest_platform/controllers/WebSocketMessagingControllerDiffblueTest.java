package com.campusnest.campusnest_platform.controllers;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.campusnest.campusnest_platform.enums.MessageType;
import com.campusnest.campusnest_platform.enums.UserRole;
import com.campusnest.campusnest_platform.enums.VerificationStatus;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.requests.ChatMessageRequest;
import com.campusnest.campusnest_platform.services.MessagingService;
import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import com.sun.security.auth.UserPrincipal;

import java.security.Principal;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {WebSocketMessagingController.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class WebSocketMessagingControllerDiffblueTest {
    @MockitoBean
    private MessagingService messagingService;

    @MockitoBean
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private WebSocketMessagingController webSocketMessagingController;

    /**
     * Test {@link WebSocketMessagingController#sendMessage(ChatMessageRequest, Principal)}.
     *
     * <p>Method under test: {@link WebSocketMessagingController#sendMessage(ChatMessageRequest,
     * Principal)}
     */
    @Test
    @DisplayName("Test sendMessage(ChatMessageRequest, Principal)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({
            "void WebSocketMessagingController.sendMessage(ChatMessageRequest, Principal)"
    })
    void testSendMessage() throws MessagingException {
        // Arrange
        when(messagingService.canUserAccessConversation(Mockito.<Long>any(), Mockito.<User>any()))
                .thenThrow(new RuntimeException());
        doNothing()
                .when(simpMessagingTemplate)
                .convertAndSendToUser(Mockito.<String>any(), Mockito.<String>any(), Mockito.<Object>any());

        ChatMessageRequest request = new ChatMessageRequest();
        request.setContent("Not all who wander are lost");
        request.setConversationId(1L);
        request.setMessageType(MessageType.TEXT);
        request.setTimestamp(10L);

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
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
        user.setUniversityDomain("User not found in WebSocket authentication context");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();
        user.setEmail(null);

        // Act
        webSocketMessagingController.sendMessage(
                request, new UsernamePasswordAuthenticationToken(user, "Credentials"));

        // Assert
        verify(messagingService).canUserAccessConversation(eq(1L), isA(User.class));
        verify(simpMessagingTemplate)
                .convertAndSendToUser(isNull(), eq("/queue/errors"), isA(Object.class));
    }

    /**
     * Test {@link WebSocketMessagingController#sendMessage(ChatMessageRequest, Principal)}.
     *
     * <ul>
     *   <li>Given {@code jane.doe@example.org}.
     *   <li>When {@link User} (default constructor) Email is {@code jane.doe@example.org}.
     * </ul>
     *
     * <p>Method under test: {@link WebSocketMessagingController#sendMessage(ChatMessageRequest,
     * Principal)}
     */
    @Test
    @DisplayName(
            "Test sendMessage(ChatMessageRequest, Principal); given 'jane.doe@example.org'; when User (default constructor) Email is 'jane.doe@example.org'")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({
            "void WebSocketMessagingController.sendMessage(ChatMessageRequest, Principal)"
    })
    void testSendMessage_givenJaneDoeExampleOrg_whenUserEmailIsJaneDoeExampleOrg()
            throws MessagingException {
        // Arrange
        when(messagingService.sendMessage(
                Mockito.<Long>any(),
                Mockito.<User>any(),
                Mockito.<String>any(),
                Mockito.<MessageType>any()))
                .thenThrow(new RuntimeException());
        when(messagingService.canUserAccessConversation(Mockito.<Long>any(), Mockito.<User>any()))
                .thenReturn(true);
        doNothing()
                .when(simpMessagingTemplate)
                .convertAndSendToUser(Mockito.<String>any(), Mockito.<String>any(), Mockito.<Object>any());

        ChatMessageRequest request = new ChatMessageRequest();
        request.setContent("Not all who wander are lost");
        request.setConversationId(1L);
        request.setMessageType(MessageType.TEXT);
        request.setTimestamp(10L);

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
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
        user.setUniversityDomain("User not found in WebSocket authentication context");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();
        user.setEmail("jane.doe@example.org");

        // Act
        webSocketMessagingController.sendMessage(
                request, new UsernamePasswordAuthenticationToken(user, "Credentials"));

        // Assert
        verify(messagingService).canUserAccessConversation(eq(1L), isA(User.class));
        verify(messagingService)
                .sendMessage(
                        eq(1L), isA(User.class), eq("Not all who wander are lost"), eq(MessageType.TEXT));
        verify(simpMessagingTemplate)
                .convertAndSendToUser(eq("jane.doe@example.org"), eq("/queue/errors"), isA(Object.class));
    }

    /**
     * Test {@link WebSocketMessagingController#sendMessage(ChatMessageRequest, Principal)}.
     *
     * <ul>
     *   <li>Given {@link MessagingService}.
     *   <li>When {@link UserPrincipal#UserPrincipal(String)} with name is {@code principal}.
     * </ul>
     *
     * <p>Method under test: {@link WebSocketMessagingController#sendMessage(ChatMessageRequest,
     * Principal)}
     */
    @Test
    @DisplayName(
            "Test sendMessage(ChatMessageRequest, Principal); given MessagingService; when UserPrincipal(String) with name is 'principal'")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({
            "void WebSocketMessagingController.sendMessage(ChatMessageRequest, Principal)"
    })
    void testSendMessage_givenMessagingService_whenUserPrincipalWithNameIsPrincipal()
            throws MessagingException {
        // Arrange
        doNothing()
                .when(simpMessagingTemplate)
                .convertAndSendToUser(Mockito.<String>any(), Mockito.<String>any(), Mockito.<Object>any());

        ChatMessageRequest request = new ChatMessageRequest();
        request.setContent("Not all who wander are lost");
        request.setConversationId(1L);
        request.setMessageType(MessageType.TEXT);
        request.setTimestamp(10L);

        // Act
        webSocketMessagingController.sendMessage(request, new UserPrincipal("principal"));

        // Assert
        verify(simpMessagingTemplate)
                .convertAndSendToUser(eq("principal"), eq("/queue/errors"), isA(Object.class));
    }

    /**
     * Test {@link WebSocketMessagingController#sendMessage(ChatMessageRequest, Principal)}.
     *
     * <ul>
     *   <li>Given {@code Received WebSocket message from user {} for conversation {}}.
     * </ul>
     *
     * <p>Method under test: {@link WebSocketMessagingController#sendMessage(ChatMessageRequest,
     * Principal)}
     */
    @Test
    @DisplayName(
            "Test sendMessage(ChatMessageRequest, Principal); given 'Received WebSocket message from user {} for conversation {}'")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({
            "void WebSocketMessagingController.sendMessage(ChatMessageRequest, Principal)"
    })
    void testSendMessage_givenReceivedWebSocketMessageFromUserForConversation()
            throws MessagingException {
        // Arrange
        when(messagingService.sendMessage(
                Mockito.<Long>any(),
                Mockito.<User>any(),
                Mockito.<String>any(),
                Mockito.<MessageType>any()))
                .thenThrow(new RuntimeException());
        when(messagingService.canUserAccessConversation(Mockito.<Long>any(), Mockito.<User>any()))
                .thenReturn(true);
        doNothing()
                .when(simpMessagingTemplate)
                .convertAndSendToUser(Mockito.<String>any(), Mockito.<String>any(), Mockito.<Object>any());

        ChatMessageRequest request = new ChatMessageRequest();
        request.setContent("Not all who wander are lost");
        request.setConversationId(1L);
        request.setMessageType(MessageType.TEXT);
        request.setTimestamp(10L);

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
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
        user.setUniversityDomain("User not found in WebSocket authentication context");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();
        user.setEmail("Received WebSocket message from user {} for conversation {}");

        // Act
        webSocketMessagingController.sendMessage(
                request, new UsernamePasswordAuthenticationToken(user, "Credentials"));

        // Assert
        verify(messagingService).canUserAccessConversation(eq(1L), isA(User.class));
        verify(messagingService)
                .sendMessage(
                        eq(1L), isA(User.class), eq("Not all who wander are lost"), eq(MessageType.TEXT));
        verify(simpMessagingTemplate)
                .convertAndSendToUser(
                        eq("Received WebSocket message from user {} for conversation {}"),
                        eq("/queue/errors"),
                        isA(Object.class));
    }

    /**
     * Test {@link WebSocketMessagingController#sendMessage(ChatMessageRequest, Principal)}.
     *
     * <ul>
     *   <li>Then throw {@link RuntimeException}.
     * </ul>
     *
     * <p>Method under test: {@link WebSocketMessagingController#sendMessage(ChatMessageRequest,
     * Principal)}
     */
    @Test
    @DisplayName("Test sendMessage(ChatMessageRequest, Principal); then throw RuntimeException")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({
            "void WebSocketMessagingController.sendMessage(ChatMessageRequest, Principal)"
    })
    void testSendMessage_thenThrowRuntimeException() throws MessagingException {
        // Arrange
        doThrow(new RuntimeException())
                .when(simpMessagingTemplate)
                .convertAndSendToUser(Mockito.<String>any(), Mockito.<String>any(), Mockito.<Object>any());

        ChatMessageRequest request = new ChatMessageRequest();
        request.setContent("Not all who wander are lost");
        request.setConversationId(1L);
        request.setMessageType(MessageType.TEXT);
        request.setTimestamp(10L);

        // Act and Assert
        assertThrows(
                RuntimeException.class,
                () -> webSocketMessagingController.sendMessage(request, new UserPrincipal("principal")));
        verify(simpMessagingTemplate)
                .convertAndSendToUser(eq("principal"), eq("/queue/errors"), isA(Object.class));
    }

    /**
     * Test {@link WebSocketMessagingController#sendMessage(ChatMessageRequest, Principal)}.
     *
     * <ul>
     *   <li>When {@link ChatMessageRequest} (default constructor) MessageType is {@code null}.
     *   <li>Then calls {@link MessagingService#sendMessage(Long, User, String, MessageType)}.
     * </ul>
     *
     * <p>Method under test: {@link WebSocketMessagingController#sendMessage(ChatMessageRequest,
     * Principal)}
     */
    @Test
    @DisplayName(
            "Test sendMessage(ChatMessageRequest, Principal); when ChatMessageRequest (default constructor) MessageType is 'null'; then calls sendMessage(Long, User, String, MessageType)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({
            "void WebSocketMessagingController.sendMessage(ChatMessageRequest, Principal)"
    })
    void testSendMessage_whenChatMessageRequestMessageTypeIsNull_thenCallsSendMessage()
            throws MessagingException {
        // Arrange
        when(messagingService.sendMessage(
                Mockito.<Long>any(),
                Mockito.<User>any(),
                Mockito.<String>any(),
                Mockito.<MessageType>any()))
                .thenThrow(new RuntimeException());
        when(messagingService.canUserAccessConversation(Mockito.<Long>any(), Mockito.<User>any()))
                .thenReturn(true);
        doNothing()
                .when(simpMessagingTemplate)
                .convertAndSendToUser(Mockito.<String>any(), Mockito.<String>any(), Mockito.<Object>any());

        ChatMessageRequest request = new ChatMessageRequest();
        request.setContent("Not all who wander are lost");
        request.setConversationId(1L);
        request.setMessageType(null);
        request.setTimestamp(10L);

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
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
        user.setUniversityDomain("User not found in WebSocket authentication context");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();
        user.setEmail(null);

        // Act
        webSocketMessagingController.sendMessage(
                request, new UsernamePasswordAuthenticationToken(user, "Credentials"));

        // Assert
        verify(messagingService).canUserAccessConversation(eq(1L), isA(User.class));
        verify(messagingService)
                .sendMessage(
                        eq(1L), isA(User.class), eq("Not all who wander are lost"), eq(MessageType.TEXT));
        verify(simpMessagingTemplate)
                .convertAndSendToUser(isNull(), eq("/queue/errors"), isA(Object.class));
    }

    /**
     * Test {@link WebSocketMessagingController#sendMessage(ChatMessageRequest, Principal)}.
     *
     * <ul>
     *   <li>When {@link User} (default constructor) Email is {@code null}.
     *   <li>Then calls {@link MessagingService#sendMessage(Long, User, String, MessageType)}.
     * </ul>
     *
     * <p>Method under test: {@link WebSocketMessagingController#sendMessage(ChatMessageRequest,
     * Principal)}
     */
    @Test
    @DisplayName(
            "Test sendMessage(ChatMessageRequest, Principal); when User (default constructor) Email is 'null'; then calls sendMessage(Long, User, String, MessageType)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({
            "void WebSocketMessagingController.sendMessage(ChatMessageRequest, Principal)"
    })
    void testSendMessage_whenUserEmailIsNull_thenCallsSendMessage() throws MessagingException {
        // Arrange
        when(messagingService.sendMessage(
                Mockito.<Long>any(),
                Mockito.<User>any(),
                Mockito.<String>any(),
                Mockito.<MessageType>any()))
                .thenThrow(new RuntimeException());
        when(messagingService.canUserAccessConversation(Mockito.<Long>any(), Mockito.<User>any()))
                .thenReturn(true);
        doNothing()
                .when(simpMessagingTemplate)
                .convertAndSendToUser(Mockito.<String>any(), Mockito.<String>any(), Mockito.<Object>any());

        ChatMessageRequest request = new ChatMessageRequest();
        request.setContent("Not all who wander are lost");
        request.setConversationId(1L);
        request.setMessageType(MessageType.TEXT);
        request.setTimestamp(10L);

        User user = new User();
        user.setAccountExpirationDate(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountExpiresAt(
                LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setAccountLocked(true);
        user.setActive(true);
        user.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
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
        user.setUniversityDomain("User not found in WebSocket authentication context");
        user.setUpdatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.updateLastLogin();
        user.setEmail(null);

        // Act
        webSocketMessagingController.sendMessage(
                request, new UsernamePasswordAuthenticationToken(user, "Credentials"));

        // Assert
        verify(messagingService).canUserAccessConversation(eq(1L), isA(User.class));
        verify(messagingService)
                .sendMessage(
                        eq(1L), isA(User.class), eq("Not all who wander are lost"), eq(MessageType.TEXT));
        verify(simpMessagingTemplate)
                .convertAndSendToUser(isNull(), eq("/queue/errors"), isA(Object.class));
    }
}
