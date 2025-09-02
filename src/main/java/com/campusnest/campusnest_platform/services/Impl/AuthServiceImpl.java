package com.campusnest.campusnest_platform.services.Impl;

import com.campusnest.campusnest_platform.enums.VerificationStatus;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.repository.UserRepository;
import com.campusnest.campusnest_platform.requests.LoginRequest;
import com.campusnest.campusnest_platform.response.LoginResponse;
import com.campusnest.campusnest_platform.requests.RegisterRequest;
import com.campusnest.campusnest_platform.response.RegisterResponse;
import com.campusnest.campusnest_platform.services.AuthService;
import com.campusnest.campusnest_platform.services.EmailVerificationService;
import com.campusnest.campusnest_platform.services.JwtTokenService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    private String extractDomain(String email) {
        return email.substring(email.indexOf("@") + 1).toLowerCase();
    }
}
