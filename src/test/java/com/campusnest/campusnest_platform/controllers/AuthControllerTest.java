package com.campusnest.campusnest_platform.controllers;

import com.campusnest.campusnest_platform.requests.DeviceInfo;
import com.campusnest.campusnest_platform.requests.LoginRequest;
import com.campusnest.campusnest_platform.response.LoginResponse;
import com.campusnest.campusnest_platform.requests.RegisterRequest;
import com.campusnest.campusnest_platform.response.RegisterResponse;
import com.campusnest.campusnest_platform.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
        
        registerRequest = createValidRegisterRequest();
        loginRequest = createValidLoginRequest();
    }

    private RegisterRequest createValidRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@university.edu");
        request.setPassword("Password123!");
        request.setFirstName("John");
        request.setLastName("Doe");
        return request;
    }

    private LoginRequest createValidLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@university.edu");
        request.setPassword("Password123!");
        request.setRememberMe(false);
        
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId("test-device-123");
        deviceInfo.setDeviceType("web");
        deviceInfo.setUserAgent("Mozilla/5.0");
        request.setDeviceInfo(deviceInfo);
        return request;
    }

    @Test
    void register_Success_ReturnsCreated() throws Exception {
        RegisterResponse successResponse = createRegisterResponse(true, "Registration successful");

        when(authService.registerUser(any(RegisterRequest.class)))
                .thenReturn(successResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registration successful"));
    }

    @Test
    void register_Failure_ReturnsBadRequest() throws Exception {
        RegisterResponse failureResponse = createRegisterResponse(false, "User already exists");

        when(authService.registerUser(any(RegisterRequest.class)))
                .thenReturn(failureResponse);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User already exists"));
    }

    @Test
    void register_InvalidEmail_ReturnsBadRequest() throws Exception {
        RegisterRequest invalidRequest = createValidRegisterRequest();
        invalidRequest.setEmail("invalid-email");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_MissingFields_ReturnsBadRequest() throws Exception {
        RegisterRequest incompleteRequest = new RegisterRequest();
        incompleteRequest.setEmail("test@university.edu");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(incompleteRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_Success_ReturnsOk() throws Exception {
        LoginResponse successResponse = createSuccessfulLoginResponse();

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(successResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.access_token").exists())
                .andExpect(jsonPath("$.refresh_token").exists())
                .andExpect(jsonPath("$.token_type").value("Bearer"))
                .andExpect(jsonPath("$.expires_in").value(900))
                .andExpect(jsonPath("$.session_id").exists());
    }

    @Test
    void login_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        LoginResponse failureResponse = LoginResponse.invalidCredentials();

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(failureResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid email or password. Please try again."));
    }

    @Test
    void login_EmailNotVerified_ReturnsUnauthorized() throws Exception {
        LoginResponse emailNotVerifiedResponse = LoginResponse.emailNotVerified("test@university.edu");

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(emailNotVerifiedResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Please verify your university email before logging in. Check your inbox for verification link."));
    }

    @Test
    void login_AccountSuspended_ReturnsUnauthorized() throws Exception {
        LoginResponse suspendedResponse = LoginResponse.accountSuspended();

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(suspendedResponse);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Your account has been suspended. Please contact support for assistance."));
    }

    @Test
    void login_InvalidEmail_ReturnsBadRequest() throws Exception {
        LoginRequest invalidLoginRequest = createValidLoginRequest();
        invalidLoginRequest.setEmail("invalid-email");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidLoginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_MissingPassword_ReturnsBadRequest() throws Exception {
        LoginRequest emptyPasswordRequest = createValidLoginRequest();
        emptyPasswordRequest.setPassword("");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyPasswordRequest)))
                .andExpect(status().isBadRequest());
    }

    private RegisterResponse createRegisterResponse(boolean success, String message) {
        RegisterResponse response = new RegisterResponse();
        response.setSuccess(success);
        response.setMessage(message);
        return response;
    }

    private LoginResponse createSuccessfulLoginResponse() {
        LoginResponse response = new LoginResponse();
        response.setSuccess(true);
        response.setMessage("Login successful");
        response.setAccessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
        response.setRefreshToken("refresh-token-123");
        response.setTokenType("Bearer");
        response.setExpiresIn(900L);
        response.setSessionId("session-123");
        return response;
    }
}