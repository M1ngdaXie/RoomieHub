package com.campusnest.campusnest_platform.services.Impl;

import com.campusnest.campusnest_platform.enums.VerificationStatus;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.repository.UserRepository;
import com.campusnest.campusnest_platform.requests.DeviceInfo;
import com.campusnest.campusnest_platform.requests.LoginRequest;
import com.campusnest.campusnest_platform.response.LoginResponse;
import com.campusnest.campusnest_platform.requests.RegisterRequest;
import com.campusnest.campusnest_platform.response.RegisterResponse;
import com.campusnest.campusnest_platform.services.EmailVerificationService;
import com.campusnest.campusnest_platform.services.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailVerificationService emailVerificationService;

    @Mock
    private UniversityValidator universityValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .email("john.doe@university.edu")
                .password("Password123!")
                .firstName("John")
                .lastName("Doe")
                .build();

        loginRequest = LoginRequest.builder()
                .email("john.doe@university.edu")
                .password("Password123!")
                .rememberMe(false)
                .deviceInfo(DeviceInfo.builder()
                        .deviceId("test-device")
                        .deviceType("web")
                        .userAgent("Mozilla/5.0")
                        .build())
                .build();

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("john.doe@university.edu");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setPassword("encoded-password");
        testUser.setUniversityDomain("university.edu");
        testUser.setEmailVerified(true);
        testUser.setActive(true);
        testUser.setVerificationStatus(VerificationStatus.EMAIL_VERIFIED);
        testUser.setCreatedAt(Instant.now());
    }

    @Test
    void registerUser_Success_ReturnsSuccessResponse() {
        when(universityValidator.isValidUniversityDomain("university.edu")).thenReturn(true);
        when(userRepository.existsByEmail("john.doe@university.edu")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(universityValidator.validateAndGetName("university.edu")).thenReturn("University Name");
        doNothing().when(emailVerificationService).sendVerificationEmail("john.doe@university.edu");

        RegisterResponse response = authService.registerUser(registerRequest);

        assertThat(response.getSuccess()).isTrue();
        assertThat(response.getMessage()).contains("Registration successful");
        verify(userRepository).save(any(User.class));
        verify(emailVerificationService).sendVerificationEmail("john.doe@university.edu");
    }

    @Test
    void registerUser_InvalidUniversity_ReturnsFailureResponse() {
        when(universityValidator.isValidUniversityDomain("university.edu")).thenReturn(false);

        RegisterResponse response = authService.registerUser(registerRequest);

        assertThat(response.getSuccess()).isFalse();
        verify(userRepository, never()).save(any());
        verify(emailVerificationService, never()).sendVerificationEmail(any());
    }

    @Test
    void registerUser_UserAlreadyExists_ReturnsFailureResponse() {
        when(universityValidator.isValidUniversityDomain("university.edu")).thenReturn(true);
        when(userRepository.existsByEmail("john.doe@university.edu")).thenReturn(true);

        RegisterResponse response = authService.registerUser(registerRequest);

        assertThat(response.getSuccess()).isFalse();
        verify(userRepository, never()).save(any());
        verify(emailVerificationService, never()).sendVerificationEmail(any());
    }

    @Test
    void registerUser_EmailServiceFails_ReturnsSuccessWithWarning() {
        when(universityValidator.isValidUniversityDomain("university.edu")).thenReturn(true);
        when(userRepository.existsByEmail("john.doe@university.edu")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(universityValidator.validateAndGetName("university.edu")).thenReturn("University Name");
        doThrow(new RuntimeException("Email service unavailable"))
                .when(emailVerificationService).sendVerificationEmail("john.doe@university.edu");

        RegisterResponse response = authService.registerUser(registerRequest);

        assertThat(response.getSuccess()).isTrue();
        verify(userRepository).save(any(User.class));
        verify(emailVerificationService).sendVerificationEmail("john.doe@university.edu");
    }

    @Test
    void registerUser_DatabaseError_ThrowsException() {
        when(universityValidator.isValidUniversityDomain("university.edu")).thenReturn(true);
        when(userRepository.existsByEmail("john.doe@university.edu")).thenReturn(false);
        when(passwordEncoder.encode("Password123!")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database error"));

        assertThatThrownBy(() -> authService.registerUser(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Registration failed. Please try again.");
    }

    @Test
    void login_Success_ReturnsSuccessResponse() {
        when(userRepository.findByEmail("john.doe@university.edu")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", "encoded-password")).thenReturn(true);
        when(jwtTokenService.generateAccessToken(testUser)).thenReturn("access-token");
        when(jwtTokenService.generateRefreshToken(eq(testUser), any(DeviceInfo.class))).thenReturn("refresh-token");
        when(jwtTokenService.getAccessTokenExpiration()).thenReturn(900L);
        when(userRepository.save(testUser)).thenReturn(testUser);

        LoginResponse response = authService.login(loginRequest);

        assertThat(response.getSuccess()).isTrue();
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getExpiresIn()).isEqualTo(900L);
        verify(userRepository).save(testUser);
        assertThat(testUser.getLastLoginAt()).isNotNull();
    }

    @Test
    void login_UserNotFound_ReturnsInvalidCredentials() {
        when(userRepository.findByEmail("john.doe@university.edu")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void login_WrongPassword_ReturnsInvalidCredentials() {
        when(userRepository.findByEmail("john.doe@university.edu")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", "encoded-password")).thenReturn(false);

        LoginResponse response = authService.login(loginRequest);

        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Invalid email or password");
        verify(jwtTokenService, never()).generateAccessToken(any());
        verify(jwtTokenService, never()).generateRefreshToken(any(), any());
    }

    @Test
    void login_AccountSuspended_ReturnsAccountSuspended() {
        testUser.setActive(false);
        when(userRepository.findByEmail("john.doe@university.edu")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", "encoded-password")).thenReturn(true);

        LoginResponse response = authService.login(loginRequest);

        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getMessage()).contains("suspended");
        verify(jwtTokenService, never()).generateAccessToken(any());
        verify(jwtTokenService, never()).generateRefreshToken(any(), any());
    }

    @Test
    void login_EmailNotVerified_ReturnsEmailNotVerified() {
        testUser.setEmailVerified(false);
        when(userRepository.findByEmail("john.doe@university.edu")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", "encoded-password")).thenReturn(true);

        LoginResponse response = authService.login(loginRequest);

        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getMessage()).contains("verify your university email");
        verify(jwtTokenService, never()).generateAccessToken(any());
        verify(jwtTokenService, never()).generateRefreshToken(any(), any());
    }

    @Test
    void login_JwtServiceError_ReturnsGenericError() {
        when(userRepository.findByEmail("john.doe@university.edu")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", "encoded-password")).thenReturn(true);
        when(jwtTokenService.generateAccessToken(testUser)).thenThrow(new RuntimeException("JWT error"));

        LoginResponse response = authService.login(loginRequest);

        assertThat(response.getSuccess()).isFalse();
        assertThat(response.getMessage()).isEqualTo("Login failed. Please try again.");
    }

    @Test
    void login_EmailCaseInsensitive_Success() {
        loginRequest.setEmail("JOHN.DOE@UNIVERSITY.EDU");
        when(userRepository.findByEmail("john.doe@university.edu")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", "encoded-password")).thenReturn(true);
        when(jwtTokenService.generateAccessToken(testUser)).thenReturn("access-token");
        when(jwtTokenService.generateRefreshToken(eq(testUser), any(DeviceInfo.class))).thenReturn("refresh-token");
        when(jwtTokenService.getAccessTokenExpiration()).thenReturn(900L);
        when(userRepository.save(testUser)).thenReturn(testUser);

        LoginResponse response = authService.login(loginRequest);

        assertThat(response.getSuccess()).isTrue();
        verify(userRepository).findByEmail("john.doe@university.edu");
    }

    @Test
    void login_EmailWithWhitespace_Success() {
        loginRequest.setEmail("  john.doe@university.edu  ");
        when(userRepository.findByEmail("john.doe@university.edu")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Password123!", "encoded-password")).thenReturn(true);
        when(jwtTokenService.generateAccessToken(testUser)).thenReturn("access-token");
        when(jwtTokenService.generateRefreshToken(eq(testUser), any(DeviceInfo.class))).thenReturn("refresh-token");
        when(jwtTokenService.getAccessTokenExpiration()).thenReturn(900L);
        when(userRepository.save(testUser)).thenReturn(testUser);

        LoginResponse response = authService.login(loginRequest);

        assertThat(response.getSuccess()).isTrue();
        verify(userRepository).findByEmail("john.doe@university.edu");
    }
}