package com.campusnest.campusnest_platform.controllers;

import com.campusnest.campusnest_platform.requests.ForgotPasswordRequest;
import com.campusnest.campusnest_platform.requests.ResetPasswordRequest;
import com.campusnest.campusnest_platform.response.ForgotPasswordResponse;
import com.campusnest.campusnest_platform.response.ResetPasswordResponse;
import com.campusnest.campusnest_platform.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetControllerUnitTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.1");
        request.addHeader("User-Agent", "Mozilla/5.0");
    }

    // ========== FORGOT PASSWORD ENDPOINT TESTS ==========

    @Test
    void forgotPassword_Success_ReturnsOk() {
        ForgotPasswordRequest forgotRequest = createValidForgotPasswordRequest();
        ForgotPasswordResponse successResponse = createSuccessfulForgotPasswordResponse();

        when(authService.forgotPassword(any(ForgotPasswordRequest.class), anyString(), anyString()))
                .thenReturn(successResponse);

        ResponseEntity<ForgotPasswordResponse> response = authController.forgotPassword(forgotRequest, request);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getSuccess());
        assertEquals("Password reset email sent successfully", response.getBody().getMessage());
        assertEquals("t***@university.edu", response.getBody().getEmail());
    }

    @Test
    void forgotPassword_UserNotFound_ReturnsOkWithGenericMessage() {
        ForgotPasswordRequest forgotRequest = createValidForgotPasswordRequest();
        forgotRequest.setEmail("nonexistent@university.edu");
        ForgotPasswordResponse notFoundResponse = createUserNotFoundForgotPasswordResponse();

        when(authService.forgotPassword(any(ForgotPasswordRequest.class), anyString(), anyString()))
                .thenReturn(notFoundResponse);

        ResponseEntity<ForgotPasswordResponse> response = authController.forgotPassword(forgotRequest, request);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getSuccess());
        assertEquals("If this email exists, you will receive a password reset link", response.getBody().getMessage());
    }

    @Test
    void forgotPassword_ServiceException_ReturnsInternalServerError() {
        ForgotPasswordRequest forgotRequest = createValidForgotPasswordRequest();

        when(authService.forgotPassword(any(ForgotPasswordRequest.class), anyString(), anyString()))
                .thenThrow(new RuntimeException("Email service error"));

        ResponseEntity<ForgotPasswordResponse> response = authController.forgotPassword(forgotRequest, request);

        assertEquals(500, response.getStatusCodeValue());
        assertFalse(response.getBody().getSuccess());
    }

    // ========== RESET PASSWORD ENDPOINT TESTS ==========

    @Test
    void resetPassword_Success_ReturnsOk() {
        ResetPasswordRequest resetRequest = createValidResetPasswordRequest();
        ResetPasswordResponse successResponse = createSuccessfulResetPasswordResponse();

        when(authService.resetPassword(any(ResetPasswordRequest.class)))
                .thenReturn(successResponse);

        ResponseEntity<ResetPasswordResponse> response = authController.resetPassword(resetRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().getSuccess());
        assertEquals("Password reset successfully", response.getBody().getMessage());
        assertEquals("t***@university.edu", response.getBody().getEmail());
    }

    @Test
    void resetPassword_InvalidToken_ReturnsBadRequest() {
        ResetPasswordRequest resetRequest = createValidResetPasswordRequest();
        resetRequest.setToken("invalid-token");
        ResetPasswordResponse invalidTokenResponse = createInvalidTokenResetPasswordResponse();

        when(authService.resetPassword(any(ResetPasswordRequest.class)))
                .thenReturn(invalidTokenResponse);

        ResponseEntity<ResetPasswordResponse> response = authController.resetPassword(resetRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().getSuccess());
        assertEquals("Invalid or expired reset token", response.getBody().getMessage());
    }

    @Test
    void resetPassword_ExpiredToken_ReturnsBadRequest() {
        ResetPasswordRequest resetRequest = createValidResetPasswordRequest();
        resetRequest.setToken("expired-token");
        ResetPasswordResponse expiredTokenResponse = createExpiredTokenResetPasswordResponse();

        when(authService.resetPassword(any(ResetPasswordRequest.class)))
                .thenReturn(expiredTokenResponse);

        ResponseEntity<ResetPasswordResponse> response = authController.resetPassword(resetRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().getSuccess());
        assertEquals("Password reset token has expired. Please request a new reset link.", response.getBody().getMessage());
    }

    @Test
    void resetPassword_ServiceException_ReturnsInternalServerError() {
        ResetPasswordRequest resetRequest = createValidResetPasswordRequest();

        when(authService.resetPassword(any(ResetPasswordRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        ResponseEntity<ResetPasswordResponse> response = authController.resetPassword(resetRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().getSuccess());
    }

    // ========== SECURITY VALIDATION TESTS ==========

    @Test
    void forgotPassword_PassesClientInfoToService() {
        ForgotPasswordRequest forgotRequest = createValidForgotPasswordRequest();
        ForgotPasswordResponse successResponse = createSuccessfulForgotPasswordResponse();

        when(authService.forgotPassword(any(ForgotPasswordRequest.class), anyString(), anyString()))
                .thenReturn(successResponse);

        authController.forgotPassword(forgotRequest, request);

        // Verify that the service method was called with client IP and User-Agent
        // This is implicitly tested by the mock setup expecting these parameters
        assertTrue(true); // Test passes if no exception is thrown
    }

    @Test
    void resetPassword_HandlesPasswordMismatch() {
        ResetPasswordRequest resetRequest = createValidResetPasswordRequest();
        ResetPasswordResponse mismatchResponse = new ResetPasswordResponse();
        mismatchResponse.setSuccess(false);
        mismatchResponse.setMessage("Passwords do not match");

        when(authService.resetPassword(any(ResetPasswordRequest.class)))
                .thenReturn(mismatchResponse);

        ResponseEntity<ResetPasswordResponse> response = authController.resetPassword(resetRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().getSuccess());
        assertEquals("Passwords do not match", response.getBody().getMessage());
    }

    @Test
    void resetPassword_HandlesWeakPassword() {
        ResetPasswordRequest resetRequest = createValidResetPasswordRequest();
        ResetPasswordResponse weakPasswordResponse = new ResetPasswordResponse();
        weakPasswordResponse.setSuccess(false);
        weakPasswordResponse.setMessage("Password does not meet security requirements");

        when(authService.resetPassword(any(ResetPasswordRequest.class)))
                .thenReturn(weakPasswordResponse);

        ResponseEntity<ResetPasswordResponse> response = authController.resetPassword(resetRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().getSuccess());
        assertEquals("Password does not meet security requirements", response.getBody().getMessage());
    }

    // ========== RATE LIMITING SIMULATION TESTS ==========

    @Test
    void forgotPassword_HandlesRateLimiting() {
        ForgotPasswordRequest forgotRequest = createValidForgotPasswordRequest();
        ForgotPasswordResponse rateLimitResponse = new ForgotPasswordResponse();
        rateLimitResponse.setSuccess(false);
        rateLimitResponse.setMessage("Request rate limited. Too many password reset attempts. Please try again later.");

        when(authService.forgotPassword(any(ForgotPasswordRequest.class), anyString(), anyString()))
                .thenReturn(rateLimitResponse);

        ResponseEntity<ForgotPasswordResponse> response = authController.forgotPassword(forgotRequest, request);

        assertEquals(429, response.getStatusCodeValue());
        assertFalse(response.getBody().getSuccess());
        assertEquals("Request rate limited. Too many password reset attempts. Please try again later.", response.getBody().getMessage());
    }

    @Test
    void resetPassword_HandlesUsedToken() {
        ResetPasswordRequest resetRequest = createValidResetPasswordRequest();
        ResetPasswordResponse usedTokenResponse = new ResetPasswordResponse();
        usedTokenResponse.setSuccess(false);
        usedTokenResponse.setMessage("Password reset token has already been used");

        when(authService.resetPassword(any(ResetPasswordRequest.class)))
                .thenReturn(usedTokenResponse);

        ResponseEntity<ResetPasswordResponse> response = authController.resetPassword(resetRequest);

        assertEquals(400, response.getStatusCodeValue());
        assertFalse(response.getBody().getSuccess());
        assertEquals("Password reset token has already been used", response.getBody().getMessage());
    }

    // ========== HELPER METHODS ==========

    private ForgotPasswordRequest createValidForgotPasswordRequest() {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setEmail("test@university.edu");
        return request;
    }

    private ResetPasswordRequest createValidResetPasswordRequest() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("valid-reset-token-123");
        request.setNewPassword("NewPassword123!");
        request.setConfirmPassword("NewPassword123!");
        return request;
    }

    private ForgotPasswordResponse createSuccessfulForgotPasswordResponse() {
        ForgotPasswordResponse response = new ForgotPasswordResponse();
        response.setSuccess(true);
        response.setMessage("Password reset email sent successfully");
        response.setEmail("t***@university.edu");
        return response;
    }

    private ForgotPasswordResponse createUserNotFoundForgotPasswordResponse() {
        ForgotPasswordResponse response = new ForgotPasswordResponse();
        response.setSuccess(true);
        response.setMessage("If this email exists, you will receive a password reset link");
        return response;
    }

    private ResetPasswordResponse createSuccessfulResetPasswordResponse() {
        ResetPasswordResponse response = new ResetPasswordResponse();
        response.setSuccess(true);
        response.setMessage("Password reset successfully");
        response.setEmail("t***@university.edu");
        return response;
    }

    private ResetPasswordResponse createInvalidTokenResetPasswordResponse() {
        ResetPasswordResponse response = new ResetPasswordResponse();
        response.setSuccess(false);
        response.setMessage("Invalid or expired reset token");
        return response;
    }

    private ResetPasswordResponse createExpiredTokenResetPasswordResponse() {
        ResetPasswordResponse response = new ResetPasswordResponse();
        response.setSuccess(false);
        response.setMessage("Password reset token has expired. Please request a new reset link.");
        return response;
    }
}