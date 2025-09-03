package com.campusnest.campusnest_platform.controllers;

import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.requests.LoginRequest;
import com.campusnest.campusnest_platform.requests.LogoutRequest;
import com.campusnest.campusnest_platform.requests.RefreshTokenRequest;
import com.campusnest.campusnest_platform.requests.ResendVerificationRequest;
import com.campusnest.campusnest_platform.response.*;
import com.campusnest.campusnest_platform.requests.RegisterRequest;
import com.campusnest.campusnest_platform.services.AuthService;
import com.campusnest.campusnest_platform.services.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {
    @Autowired
    private AuthService authService;
    
    @Autowired
    private EmailVerificationService emailVerificationService;


    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        log.info("Registration attempt for email: {}",
                maskEmailForLogs(request.getEmail()));
        RegisterResponse response = authService.registerUser(request);

        if (response.getSuccess()) {
            log.info("Registration successful for email: {}",
                    maskEmailForLogs(request.getEmail()));
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            log.info("Registration failed for email: {} - {}",
                    maskEmailForLogs(request.getEmail()), response.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info("Login attempt for email: {}",
                maskEmailForLogs(request.getEmail()));
        LoginResponse response = authService.login(request);

        if (response.getSuccess()) {
            log.info("Login successful for email: {}",
                    maskEmailForLogs(request.getEmail()));
            return ResponseEntity.ok(response);
        } else {
            log.info("Login failed for email: {} - {}",
                    maskEmailForLogs(request.getEmail()), response.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    @GetMapping("/verify-email")
    public ResponseEntity<VerificationResponse> verifyEmail(@RequestParam String token) {
        log.info("Email verification attempt with token: {}",
                token.substring(0, Math.min(token.length(), 10)) + "...");

        try {
            User user = emailVerificationService.verifyToken(token);

            log.info("Email verification successful for user: {}",
                    maskEmailForLogs(user.getEmail()));

            VerificationResponse response = VerificationResponse.builder()
                    .success(true)
                    .message("Email verified successfully! You can now log in to your account.")
                    .email(maskEmailForLogs(user.getEmail()))
                    .verificationStatus(user.getVerificationStatus().name())
                    .redirectUrl("http://localhost:8080/login")
                    .build();

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.warn("Email verification failed: {}", e.getMessage());

            VerificationResponse response = VerificationResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .redirectUrl("https://yourapp.com/verification-error") // Error page URL
                    .build();

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ResendVerificationResponse> resendVerification(
            @RequestBody ResendVerificationRequest request) {

        log.info("Resend verification attempt for email: {}",
                maskEmailForLogs(request.getEmail()));

        try {
            emailVerificationService.sendVerificationEmail(request.getEmail());

            ResendVerificationResponse response = ResendVerificationResponse.builder()
                    .success(true)
                    .message("Verification email sent! Please check your inbox.")
                    .email(maskEmailForLogs(request.getEmail()))
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to resend verification email for: {}",
                    maskEmailForLogs(request.getEmail()), e);

            ResendVerificationResponse response = ResendVerificationResponse.builder()
                    .success(false)
                    .message("Failed to send verification email. Please try again later.")
                    .build();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @RequestBody @Valid RefreshTokenRequest request) {

        log.info("Token refresh attempt with device: {}",
                request.getDeviceInfo() != null ? request.getDeviceInfo().getDeviceType() : "Unknown");

        try {
            RefreshTokenResponse response = authService.refreshAccessToken(request);

            if (response.getSuccess()) {
                log.info("Token refresh successful");
                return ResponseEntity.ok(response);
            } else {
                log.warn("Token refresh failed: {}", response.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

        } catch (Exception e) {
            log.error("Token refresh error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RefreshTokenResponse.invalidToken());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@RequestBody @Valid LogoutRequest request) {
        log.info("Logout attempt - logoutAllDevices: {}", request.isLogoutAllDevices());
        
        try {
            LogoutResponse response = authService.logout(request);
            
            if (response.getSuccess()) {
                log.info("Logout successful - {} tokens invalidated", response.getTokensInvalidated());
            } else {
                log.info("Logout completed - {}", response.getMessage());
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Logout error: {}", e.getMessage());
            return ResponseEntity.ok(LogoutResponse.alreadyLoggedOut()); // Always return success for logout
        }
    }

    private String maskEmailForLogs(String email) {
        if (email == null) return "null";
        int atIndex = email.indexOf("@");
        return atIndex > 0 ? email.substring(0, 1) + "***" + email.substring(atIndex) : email;
    }
}
