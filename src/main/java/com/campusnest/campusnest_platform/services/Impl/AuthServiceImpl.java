package com.campusnest.campusnest_platform.services.Impl;

import com.campusnest.campusnest_platform.enums.VerificationStatus;
import com.campusnest.campusnest_platform.models.RefreshToken;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.repository.RefreshTokenRepository;
import com.campusnest.campusnest_platform.repository.UserRepository;
import com.campusnest.campusnest_platform.requests.DeviceInfo;
import com.campusnest.campusnest_platform.requests.LoginRequest;
import com.campusnest.campusnest_platform.requests.LogoutRequest;
import com.campusnest.campusnest_platform.requests.RefreshTokenRequest;
import com.campusnest.campusnest_platform.response.LoginResponse;
import com.campusnest.campusnest_platform.requests.RegisterRequest;
import com.campusnest.campusnest_platform.response.LogoutResponse;
import com.campusnest.campusnest_platform.response.RefreshTokenResponse;
import com.campusnest.campusnest_platform.response.RegisterResponse;
import com.campusnest.campusnest_platform.response.UserResponse;
import com.campusnest.campusnest_platform.services.AuthService;
import com.campusnest.campusnest_platform.services.EmailVerificationService;
import com.campusnest.campusnest_platform.services.JwtTokenService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private UniversityValidator universityService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenService jwtTokenService;
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public RegisterResponse registerUser(RegisterRequest request) {

        try {
            // 1. Validate university domain
            String domain = extractDomain(request.getEmail());

            if (!universityService.isValidUniversityDomain(domain)) {
                return RegisterResponse.invalidUniversity(request.getEmail(), domain);
            }

            // 2. Check if user already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                return RegisterResponse.userAlreadyExists(request.getEmail());
            }

            // 3. Create user entity
            User user = createUserFromRequest(request, domain);
            User savedUser = userRepository.save(user);

            // 4. Send verification email
            String universityName = universityService.validateAndGetName(domain);

            try {
                emailVerificationService.sendVerificationEmail(savedUser.getEmail());
                return RegisterResponse.success(savedUser, universityName);

            } catch (RuntimeException e) {
                // User created but email failed - still success but warn user
                log.warn("Email verification failed for user: {}", savedUser.getEmail(), e);
                return RegisterResponse.successWithEmailWarning(savedUser, universityName);
            }

        } catch (Exception e) {
            log.error("Registration failed for email: {}", request.getEmail(), e);
            throw new RuntimeException("Registration failed. Please try again.");
        }
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try{
            User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
            if(user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())){
                return LoginResponse.invalidCredentials();
            }
            if (!user.getActive()) {
                return LoginResponse.accountSuspended();
            }

            // 4. Check email verification
            if (!user.getEmailVerified()) {
                return LoginResponse.emailNotVerified(request.getEmail());
            }
            String accessToken = jwtTokenService.generateAccessToken(user);
            String refreshToken = jwtTokenService.generateRefreshToken(user, request.getDeviceInfo());
            String sessionId = UUID.randomUUID().toString();

            // Create response BEFORE updating lastLoginAt to preserve firstLogin check
            LoginResponse response = LoginResponse.success(
                    accessToken,
                    refreshToken,
                    user,
                    jwtTokenService.getAccessTokenExpiration(),
                    sessionId
            );

            // 6. Update user login info AFTER creating response
            user.setLastLoginAt(Instant.now());
            userRepository.save(user);
            
            return response;

        } catch (Exception e) {
            return LoginResponse.builder()
                    .success(false)
                    .message("Login failed. Please try again.")
                    .build();
        }
    }

    private User createUserFromRequest(RegisterRequest request, String domain) {
        User user = new User();
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setFirstName(request.getFirstName().trim());
        user.setCreatedAt(Instant.now());
        user.setLastName(request.getLastName().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setUniversityDomain(domain);
        user.setVerificationStatus(VerificationStatus.PENDING_EMAIL);
        user.setEmailVerified(false);
        user.setActive(true);
        return user;
    }

    @Override
    public RefreshTokenResponse refreshAccessToken(RefreshTokenRequest request) {
        try {
            RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

            // Check expiration
            if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
                refreshTokenRepository.delete(refreshToken);
                return RefreshTokenResponse.invalidToken();
            }

            User user = refreshToken.getUser();

            // Check user status
            if (!user.getActive() || !user.getEmailVerified()) {
                return RefreshTokenResponse.accountIssue();
            }

            // Optional: Validate device consistency
            if (request.getDeviceInfo() != null && refreshToken.getDeviceId() != null) {
                if (!refreshToken.getDeviceId().equals(request.getDeviceInfo().getDeviceId())) {
                    log.warn("Device mismatch for refresh token - possible security issue");
                    // Could reject or just log warning
                }
            }

            // Generate new tokens (TOKEN ROTATION)
            String newAccessToken = jwtTokenService.generateAccessToken(user);
            String newRefreshToken = jwtTokenService.generateRefreshToken(user, request.getDeviceInfo());

            // Delete old refresh token (important for security)
            refreshTokenRepository.delete(refreshToken);

            // Update last login
            user.setLastLoginAt(Instant.now());
            userRepository.save(user);

            return RefreshTokenResponse.success(
                    newAccessToken,
                    newRefreshToken,
                    UserResponse.from(user),
                    jwtTokenService.getAccessTokenExpiration(),
                    UUID.randomUUID().toString()
            );

        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            return RefreshTokenResponse.invalidToken();
        }
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        try {
            String refreshTokenValue = request.getRefreshToken();
            boolean logoutAllDevices = request.isLogoutAllDevices();
            
            if (logoutAllDevices) {
                return logoutAllDevices(refreshTokenValue, request.getDeviceInfo());
            } else {
                return logoutSingleDevice(refreshTokenValue, request.getDeviceInfo());
            }
            
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            // Always return success for logout from user perspective
            return LogoutResponse.alreadyLoggedOut();
        }
    }
    
    private LogoutResponse logoutSingleDevice(String refreshTokenValue, DeviceInfo deviceInfo) {
        try {
            RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                    .orElse(null);
            
            if (refreshToken == null) {
                log.info("Logout attempt with non-existent or already expired token");
                return LogoutResponse.alreadyLoggedOut();
            }
            
            // Optional: Validate device consistency for additional security
            if (deviceInfo != null && refreshToken.getDeviceId() != null) {
                if (!refreshToken.getDeviceId().equals(deviceInfo.getDeviceId())) {
                    log.warn("Device mismatch during logout - possible security issue. Token: {}, Request: {}",
                            refreshToken.getDeviceId(), deviceInfo.getDeviceId());
                }
            }
            
            // Delete the refresh token
            refreshTokenRepository.delete(refreshToken);
            
            log.info("User logged out successfully: {}", 
                    maskEmailForLogs(refreshToken.getUser().getEmail()));
                    
            return LogoutResponse.success();
            
        } catch (Exception e) {
            log.error("Error during single device logout: {}", e.getMessage());
            return LogoutResponse.alreadyLoggedOut();
        }
    }
    
    private LogoutResponse logoutAllDevices(String refreshTokenValue, DeviceInfo deviceInfo) {
        try {
            // First find the user from the provided refresh token
            RefreshToken currentToken = refreshTokenRepository.findByToken(refreshTokenValue)
                    .orElse(null);
                    
            if (currentToken == null) {
                log.info("Logout all devices attempt with non-existent token");
                return LogoutResponse.alreadyLoggedOut();
            }
            
            User user = currentToken.getUser();
            
            // Find all refresh tokens for this user
            List<RefreshToken> userTokens = refreshTokenRepository.findByUser(user);
            
            if (userTokens.isEmpty()) {
                return LogoutResponse.alreadyLoggedOut();
            }
            
            // Delete all refresh tokens for this user
            refreshTokenRepository.deleteAll(userTokens);
            
            log.info("User logged out from all devices: {} - {} tokens invalidated", 
                    maskEmailForLogs(user.getEmail()), userTokens.size());
                    
            return LogoutResponse.successAllDevices(userTokens.size());
            
        } catch (Exception e) {
            log.error("Error during logout all devices: {}", e.getMessage());
            return LogoutResponse.alreadyLoggedOut();
        }
    }
    
    private String maskEmailForLogs(String email) {
        if (email == null) return "null";
        int atIndex = email.indexOf("@");
        return atIndex > 0 ? email.substring(0, 1) + "***" + email.substring(atIndex) : email;
    }

    private String extractDomain(String email) {
        return email.substring(email.indexOf("@") + 1).toLowerCase();
    }
}
