package com.campusnest.campusnest_platform.controllers;

import com.campusnest.campusnest_platform.enums.MessageType;
import com.campusnest.campusnest_platform.models.Conversation;
import com.campusnest.campusnest_platform.models.Message;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.requests.ChatMessageRequest;
import com.campusnest.campusnest_platform.requests.TypingIndicatorRequest;
import com.campusnest.campusnest_platform.services.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.security.Principal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * WebSocket Controller API Tests
 * Tests individual WebSocket API methods directly using ultra think approach
 * Ignores Lombok issues and focuses on WebSocket business logic
 */
@ExtendWith(MockitoExtension.class)
public class WebSocketControllerApiTest {

    @Mock
    private MessagingService messagingService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private WebSocketMessagingController controller;

    private User testUser;
    private Principal testPrincipal;
    private Conversation testConversation;
    private Message testMessage;

    @BeforeEach
    void setUp() {
        // Create test user (mocking the parts that would fail due to Lombok)
        testUser = mock(User.class);
        when(testUser.getId()).thenReturn(1L);
        when(testUser.getEmail()).thenReturn("test@utah.edu");
        when(testUser.getFirstName()).thenReturn("Test");
        when(testUser.getLastName()).thenReturn("User");
        when(testUser.isEnabled()).thenReturn(true);
        when(testUser.isAccountNonLocked()).thenReturn(true);
        when(testUser.isAccountNonExpired()).thenReturn(true);
        when(testUser.isCredentialsNonExpired()).thenReturn(true);

        // Create test principal
        testPrincipal = new UsernamePasswordAuthenticationToken(testUser, null);

        // Create test conversation  
        testConversation = mock(Conversation.class);
        when(testConversation.getId()).thenReturn(1L);

        // Create test message
        testMessage = mock(Message.class);
        when(testMessage.getId()).thenReturn(1L);
        when(testMessage.getContent()).thenReturn("Test message");
        when(testMessage.getSentAt()).thenReturn(LocalDateTime.now());
    }

    /**
     * Test 1: Chat Send Message API - Successful message sending
     */
    @Test
    void testChatSendMessageApi_Success() {
        // Given
        ChatMessageRequest request = new ChatMessageRequest();
        // Mock the request methods to avoid Lombok issues
        ChatMessageRequest mockRequest = mock(ChatMessageRequest.class);
        when(mockRequest.getConversationId()).thenReturn(1L);
        when(mockRequest.getContent()).thenReturn("Hello World!");
        when(mockRequest.getMessageType()).thenReturn(MessageType.TEXT);

        // Mock service responses
        when(messagingService.canUserAccessConversation(1L, testUser)).thenReturn(true);
        when(messagingService.sendMessage(1L, testUser, "Hello World!", MessageType.TEXT)).thenReturn(testMessage);
        when(messagingService.getConversationWithParticipants(1L, testUser)).thenReturn(testConversation);

        // When
        assertDoesNotThrow(() -> controller.sendMessage(mockRequest, testPrincipal));

        // Then
        verify(messagingService).canUserAccessConversation(1L, testUser);
        verify(messagingService).sendMessage(1L, testUser, "Hello World!", MessageType.TEXT);
        verify(messagingService).getConversationWithParticipants(1L, testUser);
    }

    /**
     * Test 2: Chat Send Message API - Unauthorized access
     */
    @Test
    void testChatSendMessageApi_UnauthorizedAccess() {
        // Given
        ChatMessageRequest mockRequest = mock(ChatMessageRequest.class);
        when(mockRequest.getConversationId()).thenReturn(1L);
        when(mockRequest.getContent()).thenReturn("Unauthorized message");

        // Mock unauthorized access
        when(messagingService.canUserAccessConversation(1L, testUser)).thenReturn(false);

        // When
        assertDoesNotThrow(() -> controller.sendMessage(mockRequest, testPrincipal));

        // Then
        verify(messagingService).canUserAccessConversation(1L, testUser);
        verify(messagingService, never()).sendMessage(anyLong(), any(), anyString(), any());
        verify(messagingTemplate, never()).convertAndSendToUser(anyString(), anyString(), any());
    }

    /**
     * Test 3: Chat Send Message API - Service exception handling
     */
    @Test
    void testChatSendMessageApi_ServiceException() {
        // Given
        ChatMessageRequest mockRequest = mock(ChatMessageRequest.class);
        when(mockRequest.getConversationId()).thenReturn(1L);
        when(mockRequest.getContent()).thenReturn("Error message");

        when(messagingService.canUserAccessConversation(1L, testUser)).thenReturn(true);
        when(messagingService.sendMessage(anyLong(), any(), anyString(), any()))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When
        assertDoesNotThrow(() -> controller.sendMessage(mockRequest, testPrincipal));

        // Then
        verify(messagingTemplate).convertAndSendToUser(
                eq("test@utah.edu"),
                eq("/queue/errors"),
                argThat(message -> message.toString().contains("Failed to send message"))
        );
    }

    /**
     * Test 4: Chat Send Message API - Default message type
     */
    @Test
    void testChatSendMessageApi_DefaultMessageType() {
        // Given
        ChatMessageRequest mockRequest = mock(ChatMessageRequest.class);
        when(mockRequest.getConversationId()).thenReturn(1L);
        when(mockRequest.getContent()).thenReturn("Default type message");
        when(mockRequest.getMessageType()).thenReturn(null); // Null message type

        when(messagingService.canUserAccessConversation(1L, testUser)).thenReturn(true);
        when(messagingService.sendMessage(1L, testUser, "Default type message", MessageType.TEXT)).thenReturn(testMessage);
        when(messagingService.getConversationWithParticipants(1L, testUser)).thenReturn(testConversation);

        // When
        assertDoesNotThrow(() -> controller.sendMessage(mockRequest, testPrincipal));

        // Then - Should default to TEXT message type
        verify(messagingService).sendMessage(1L, testUser, "Default type message", MessageType.TEXT);
    }

    /**
     * Test 5: Typing Indicator API - Start typing
     */
    @Test
    void testTypingIndicatorApi_StartTyping() {
        // Given
        TypingIndicatorRequest mockRequest = mock(TypingIndicatorRequest.class);
        when(mockRequest.getConversationId()).thenReturn(1L);
        when(mockRequest.getIsTyping()).thenReturn(true);

        when(messagingService.canUserAccessConversation(1L, testUser)).thenReturn(true);
        when(messagingService.getConversationWithParticipants(1L, testUser)).thenReturn(testConversation);

        // When
        assertDoesNotThrow(() -> controller.handleTypingIndicator(mockRequest, testPrincipal));

        // Then
        verify(messagingService).canUserAccessConversation(1L, testUser);
        verify(messagingService).getConversationWithParticipants(1L, testUser);
    }

    /**
     * Test 6: Typing Indicator API - Stop typing
     */
    @Test
    void testTypingIndicatorApi_StopTyping() {
        // Given
        TypingIndicatorRequest mockRequest = mock(TypingIndicatorRequest.class);
        when(mockRequest.getConversationId()).thenReturn(1L);
        when(mockRequest.getIsTyping()).thenReturn(false);

        when(messagingService.canUserAccessConversation(1L, testUser)).thenReturn(true);
        when(messagingService.getConversationWithParticipants(1L, testUser)).thenReturn(testConversation);

        // When
        assertDoesNotThrow(() -> controller.handleTypingIndicator(mockRequest, testPrincipal));

        // Then
        verify(messagingService).canUserAccessConversation(1L, testUser);
        verify(messagingService).getConversationWithParticipants(1L, testUser);
    }

    /**
     * Test 7: Typing Indicator API - Unauthorized access
     */
    @Test
    void testTypingIndicatorApi_UnauthorizedAccess() {
        // Given
        TypingIndicatorRequest mockRequest = mock(TypingIndicatorRequest.class);
        when(mockRequest.getConversationId()).thenReturn(1L);
        when(mockRequest.getIsTyping()).thenReturn(true);

        when(messagingService.canUserAccessConversation(1L, testUser)).thenReturn(false);

        // When
        assertDoesNotThrow(() -> controller.handleTypingIndicator(mockRequest, testPrincipal));

        // Then
        verify(messagingService).canUserAccessConversation(1L, testUser);
        verify(messagingService, never()).getConversationWithParticipants(anyLong(), any());
    }

    /**
     * Test 8: Typing Indicator API - Exception handling
     */
    @Test
    void testTypingIndicatorApi_ExceptionHandling() {
        // Given
        TypingIndicatorRequest mockRequest = mock(TypingIndicatorRequest.class);
        when(mockRequest.getConversationId()).thenReturn(1L);
        when(mockRequest.getIsTyping()).thenReturn(true);

        when(messagingService.canUserAccessConversation(1L, testUser)).thenReturn(true);
        when(messagingService.getConversationWithParticipants(1L, testUser))
                .thenThrow(new RuntimeException("Service unavailable"));

        // When
        assertDoesNotThrow(() -> controller.handleTypingIndicator(mockRequest, testPrincipal));

        // Then - Should handle exception gracefully without sending error messages
        verify(messagingTemplate, never()).convertAndSendToUser(anyString(), contains("/queue/errors"), any());
    }

    /**
     * Test 9: Join Conversation API - Success
     */
    @Test
    void testJoinConversationApi_Success() {
        // Given
        Long conversationId = 1L;

        when(messagingService.canUserAccessConversation(conversationId, testUser)).thenReturn(true);

        // When
        assertDoesNotThrow(() -> controller.joinConversation(conversationId, testPrincipal));

        // Then
        verify(messagingService).canUserAccessConversation(conversationId, testUser);
        verify(messagingService).markMessagesAsRead(conversationId, testUser);
        verify(messagingTemplate).convertAndSendToUser(
                eq("test@utah.edu"),
                eq("/queue/conversation-joined/1"),
                eq("Successfully joined conversation")
        );
    }

    /**
     * Test 10: Join Conversation API - Unauthorized access
     */
    @Test
    void testJoinConversationApi_UnauthorizedAccess() {
        // Given
        Long conversationId = 1L;

        when(messagingService.canUserAccessConversation(conversationId, testUser)).thenReturn(false);

        // When
        assertDoesNotThrow(() -> controller.joinConversation(conversationId, testPrincipal));

        // Then
        verify(messagingService).canUserAccessConversation(conversationId, testUser);
        verify(messagingService, never()).markMessagesAsRead(anyLong(), any());
        verify(messagingTemplate, never()).convertAndSendToUser(anyString(), anyString(), any());
    }

    /**
     * Test 11: Join Conversation API - Service exception
     */
    @Test
    void testJoinConversationApi_ServiceException() {
        // Given
        Long conversationId = 1L;

        when(messagingService.canUserAccessConversation(conversationId, testUser)).thenReturn(true);
        doThrow(new RuntimeException("Database error")).when(messagingService).markMessagesAsRead(conversationId, testUser);

        // When
        assertDoesNotThrow(() -> controller.joinConversation(conversationId, testPrincipal));

        // Then - Should handle exception gracefully
        verify(messagingService).markMessagesAsRead(conversationId, testUser);
    }

    /**
     * Test 12: Leave Conversation API - Success
     */
    @Test
    void testLeaveConversationApi_Success() {
        // Given
        Long conversationId = 1L;

        // When
        assertDoesNotThrow(() -> controller.leaveConversation(conversationId, testPrincipal));

        // Then
        verify(messagingTemplate).convertAndSendToUser(
                eq("test@utah.edu"),
                eq("/queue/conversation-left/1"),
                eq("Left conversation")
        );
    }

    /**
     * Test 13: Leave Conversation API - Messaging template exception
     */
    @Test
    void testLeaveConversationApi_MessagingException() {
        // Given
        Long conversationId = 1L;

        doThrow(new RuntimeException("Messaging error")).when(messagingTemplate)
                .convertAndSendToUser(anyString(), anyString(), anyString());

        // When
        assertDoesNotThrow(() -> controller.leaveConversation(conversationId, testPrincipal));

        // Then - Should handle exception gracefully
        verify(messagingTemplate).convertAndSendToUser(
                eq("test@utah.edu"),
                eq("/queue/conversation-left/1"),
                eq("Left conversation")
        );
    }

    /**
     * Test 14: Connection Status API
     */
    @Test
    void testConnectionStatusApi() {
        // When
        String result = controller.getConnectionStatus(testPrincipal);

        // Then
        assertEquals("Connected as Test User", result);
    }

    /**
     * Test 15: Send Message API - Null principal handling
     */
    @Test
    void testSendMessageApi_NullPrincipal() {
        // Given
        ChatMessageRequest mockRequest = mock(ChatMessageRequest.class);
        when(mockRequest.getConversationId()).thenReturn(1L);
        when(mockRequest.getContent()).thenReturn("Test message");

        // When & Then
        assertThrows(RuntimeException.class, () -> controller.sendMessage(mockRequest, null));
    }

    /**
     * Test 16: Send Message API - Invalid principal type
     */
    @Test
    void testSendMessageApi_InvalidPrincipalType() {
        // Given
        ChatMessageRequest mockRequest = mock(ChatMessageRequest.class);
        when(mockRequest.getConversationId()).thenReturn(1L);
        when(mockRequest.getContent()).thenReturn("Test message");

        Principal invalidPrincipal = () -> "invalid";

        // When & Then
        assertThrows(RuntimeException.class, () -> controller.sendMessage(mockRequest, invalidPrincipal));
    }

    /**
     * Test 17: Typing Indicator API - Null conversation ID
     */
    @Test
    void testTypingIndicatorApi_NullConversationId() {
        // Given
        TypingIndicatorRequest mockRequest = mock(TypingIndicatorRequest.class);
        when(mockRequest.getConversationId()).thenReturn(null);
        when(mockRequest.getIsTyping()).thenReturn(true);

        // When
        assertDoesNotThrow(() -> controller.handleTypingIndicator(mockRequest, testPrincipal));

        // Then - Should handle null conversation ID gracefully
        verify(messagingService, never()).canUserAccessConversation(any(), any());
    }

    /**
     * Test 18: Join Conversation API - Null conversation ID
     */
    @Test
    void testJoinConversationApi_NullConversationId() {
        // When & Then
        assertDoesNotThrow(() -> controller.joinConversation(null, testPrincipal));
    }

    /**
     * Test 19: Message API Performance Test
     */
    @Test
    void testMessageApiPerformance() {
        // Given
        ChatMessageRequest mockRequest = mock(ChatMessageRequest.class);
        when(mockRequest.getConversationId()).thenReturn(1L);
        when(mockRequest.getContent()).thenReturn("Performance test message");
        when(mockRequest.getMessageType()).thenReturn(MessageType.TEXT);

        when(messagingService.canUserAccessConversation(1L, testUser)).thenReturn(true);
        when(messagingService.sendMessage(1L, testUser, "Performance test message", MessageType.TEXT)).thenReturn(testMessage);
        when(messagingService.getConversationWithParticipants(1L, testUser)).thenReturn(testConversation);

        // When - Send multiple messages rapidly
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            controller.sendMessage(mockRequest, testPrincipal);
        }
        long endTime = System.currentTimeMillis();

        // Then
        long executionTime = endTime - startTime;
        System.out.println("API Performance Test - 10 messages processed in: " + executionTime + "ms");
        
        // Verify all calls were made
        verify(messagingService, times(10)).sendMessage(1L, testUser, "Performance test message", MessageType.TEXT);
        
        assertTrue(executionTime < 5000, "API should handle 10 messages in under 5 seconds");
    }

    /**
     * Test 20: Comprehensive API Workflow Test
     */
    @Test
    void testComprehensiveApiWorkflow() {
        // Given - Set up all mocks for a complete workflow
        Long conversationId = 1L;
        
        ChatMessageRequest mockMessageRequest = mock(ChatMessageRequest.class);
        when(mockMessageRequest.getConversationId()).thenReturn(conversationId);
        when(mockMessageRequest.getContent()).thenReturn("Workflow test message");
        when(mockMessageRequest.getMessageType()).thenReturn(MessageType.TEXT);
        
        TypingIndicatorRequest mockTypingRequest = mock(TypingIndicatorRequest.class);
        when(mockTypingRequest.getConversationId()).thenReturn(conversationId);
        when(mockTypingRequest.getIsTyping()).thenReturn(true);

        // Mock all service calls
        when(messagingService.canUserAccessConversation(conversationId, testUser)).thenReturn(true);
        when(messagingService.sendMessage(conversationId, testUser, "Workflow test message", MessageType.TEXT)).thenReturn(testMessage);
        when(messagingService.getConversationWithParticipants(conversationId, testUser)).thenReturn(testConversation);

        // When - Execute complete workflow
        assertDoesNotThrow(() -> {
            // 1. Join conversation
            controller.joinConversation(conversationId, testPrincipal);
            
            // 2. Start typing
            controller.handleTypingIndicator(mockTypingRequest, testPrincipal);
            
            // 3. Send message
            controller.sendMessage(mockMessageRequest, testPrincipal);
            
            // 4. Stop typing
            when(mockTypingRequest.getIsTyping()).thenReturn(false);
            controller.handleTypingIndicator(mockTypingRequest, testPrincipal);
            
            // 5. Check status
            String status = controller.getConnectionStatus(testPrincipal);
            assertEquals("Connected as Test User", status);
            
            // 6. Leave conversation
            controller.leaveConversation(conversationId, testPrincipal);
        });

        // Then - Verify entire workflow executed
        verify(messagingService, times(3)).canUserAccessConversation(conversationId, testUser);
        verify(messagingService).markMessagesAsRead(conversationId, testUser);
        verify(messagingService).sendMessage(conversationId, testUser, "Workflow test message", MessageType.TEXT);
        verify(messagingService, times(2)).getConversationWithParticipants(conversationId, testUser);
        
        verify(messagingTemplate).convertAndSendToUser(eq("test@utah.edu"), eq("/queue/conversation-joined/1"), anyString());
        verify(messagingTemplate).convertAndSendToUser(eq("test@utah.edu"), eq("/queue/conversation-left/1"), anyString());
        
        System.out.println("Comprehensive API workflow test completed successfully");
    }
}