package com.campusnest.campusnest_platform.services;

import com.campusnest.campusnest_platform.enums.VerificationStatus;
import com.campusnest.campusnest_platform.models.RefreshToken;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.repository.RefreshTokenRepository;
import com.campusnest.campusnest_platform.requests.DeviceInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private JwtTokenService jwtTokenService;

    private User testUser;
    private DeviceInfo deviceInfo;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtTokenService, "jwtSecret", "test-secret-key-that-is-long-enough-for-hmac256");
        ReflectionTestUtils.setField(jwtTokenService, "accessTokenExpiration", 900);
        ReflectionTestUtils.setField(jwtTokenService, "refreshTokenExpiration", 604800);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("john.doe@university.edu");
        testUser.setUniversityDomain("university.edu");
        testUser.setVerificationStatus(VerificationStatus.EMAIL_VERIFIED);
        testUser.setEmailVerified(true);

        deviceInfo = DeviceInfo.builder()
                .deviceId("test-device-123")
                .deviceType("web")
                .userAgent("Mozilla/5.0")
                .build();
    }

    @Test
    void generateAccessToken_Success_ReturnsValidToken() {
        String token = jwtTokenService.generateAccessToken(testUser);

        assertThat(token).isNotNull();
        assertThat(token).isNotBlank();
        assertThat(token.split("\\.")).hasSize(3);
        assertThat(token).startsWith("eyJ");
    }

    @Test
    void generateAccessToken_ContainsUserClaims() {
        String token = jwtTokenService.generateAccessToken(testUser);

        String userId = jwtTokenService.getUserIdFromToken(token);
        assertThat(userId).isEqualTo("1");
    }

    @Test
    void validateAccessToken_ValidToken_ReturnsTrue() {
        String token = jwtTokenService.generateAccessToken(testUser);

        boolean isValid = jwtTokenService.validateAccessToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    void validateAccessToken_InvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.jwt.token";

        boolean isValid = jwtTokenService.validateAccessToken(invalidToken);

        assertThat(isValid).isFalse();
    }

    @Test
    void validateAccessToken_MalformedToken_ReturnsFalse() {
        String malformedToken = "not-a-jwt-token";

        boolean isValid = jwtTokenService.validateAccessToken(malformedToken);

        assertThat(isValid).isFalse();
    }

    @Test
    void validateAccessToken_ExpiredToken_ReturnsFalse() {
        ReflectionTestUtils.setField(jwtTokenService, "accessTokenExpiration", -1);
        
        String expiredToken = jwtTokenService.generateAccessToken(testUser);
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        boolean isValid = jwtTokenService.validateAccessToken(expiredToken);
        
        assertThat(isValid).isFalse();
    }

    @Test
    void generateRefreshToken_Success_ReturnsToken() {
        RefreshToken savedRefreshToken = new RefreshToken();
        savedRefreshToken.setToken("generated-uuid-token");
        savedRefreshToken.setUser(testUser);
        savedRefreshToken.setDeviceId("test-device-123");
        savedRefreshToken.setDeviceType("web");

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(savedRefreshToken);

        String refreshToken = jwtTokenService.generateRefreshToken(testUser, deviceInfo);

        assertThat(refreshToken).isEqualTo("generated-uuid-token");
        verify(refreshTokenRepository).deleteByUserAndDeviceId(testUser, "test-device-123");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void generateRefreshToken_NullDeviceInfo_HandlesGracefully() {
        RefreshToken savedRefreshToken = new RefreshToken();
        savedRefreshToken.setToken("generated-uuid-token");
        savedRefreshToken.setUser(testUser);

        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(savedRefreshToken);

        String refreshToken = jwtTokenService.generateRefreshToken(testUser, null);

        assertThat(refreshToken).isEqualTo("generated-uuid-token");
        verify(refreshTokenRepository).deleteByUserAndDeviceId(testUser, null);
        verify(refreshTokenRepository).save(argThat(token ->
                token.getDeviceId() == null &&
                "Unknown".equals(token.getDeviceType())
        ));
    }

    @Test
    void generateRefreshToken_DeletesExistingTokens() {
        RefreshToken savedRefreshToken = new RefreshToken();
        savedRefreshToken.setToken("new-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(savedRefreshToken);

        jwtTokenService.generateRefreshToken(testUser, deviceInfo);

        verify(refreshTokenRepository).deleteByUserAndDeviceId(testUser, "test-device-123");
    }

    @Test
    void getUserIdFromToken_ValidToken_ReturnsUserId() {
        String token = jwtTokenService.generateAccessToken(testUser);

        String userId = jwtTokenService.getUserIdFromToken(token);

        assertThat(userId).isEqualTo("1");
    }

    @Test
    void getUserIdFromToken_ValidatesToken() {
        String token = jwtTokenService.generateAccessToken(testUser);

        jwtTokenService.getUserIdFromToken(token);

        assertThat(jwtTokenService.validateAccessToken(token)).isTrue();
    }

    @Test
    void getUserIdFromToken_InvalidToken_ThrowsException() {
        String invalidToken = "invalid.jwt.token";

        assertThatThrownBy(() -> jwtTokenService.getUserIdFromToken(invalidToken))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getAccessTokenExpiration_ReturnsConfiguredValue() {
        Long expiration = jwtTokenService.getAccessTokenExpiration();

        assertThat(expiration).isEqualTo(900L);
    }

    @Test
    void generateAccessToken_WithNullUser_ThrowsException() {
        assertThatThrownBy(() -> jwtTokenService.generateAccessToken(null))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void generateRefreshToken_SavesCorrectTokenData() {
        RefreshToken savedToken = new RefreshToken();
        savedToken.setToken("saved-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(savedToken);

        jwtTokenService.generateRefreshToken(testUser, deviceInfo);

        verify(refreshTokenRepository).save(argThat(token ->
                token.getUser().equals(testUser) &&
                token.getDeviceId().equals("test-device-123") &&
                token.getDeviceType().equals("web") &&
                token.getExpiryDate().isAfter(Instant.now()) &&
                token.getToken() != null
        ));
    }

    @Test
    void generateAccessToken_WithDifferentUsers_GeneratesDifferentTokens() {
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("jane.doe@university.edu");
        user2.setUniversityDomain("university.edu");
        user2.setVerificationStatus(VerificationStatus.EMAIL_VERIFIED);
        user2.setEmailVerified(true);

        String token1 = jwtTokenService.generateAccessToken(testUser);
        String token2 = jwtTokenService.generateAccessToken(user2);

        assertThat(token1).isNotEqualTo(token2);
        assertThat(jwtTokenService.getUserIdFromToken(token1)).isEqualTo("1");
        assertThat(jwtTokenService.getUserIdFromToken(token2)).isEqualTo("2");
    }
}