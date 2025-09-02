package com.campusnest.campusnest_platform.controllers;

import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.requests.LoginRequest;
import com.campusnest.campusnest_platform.requests.ResendVerificationRequest;
import com.campusnest.campusnest_platform.response.LoginResponse;
import com.campusnest.campusnest_platform.requests.RegisterRequest;
import com.campusnest.campusnest_platform.response.RegisterResponse;
import com.campusnest.campusnest_platform.response.ResendVerificationResponse;
import com.campusnest.campusnest_platform.response.VerificationResponse;
import com.campusnest.campusnest_platform.services.AuthService;
import com.campusnest.campusnest_platform.services.EmailVerificationService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private String maskEmailForLogs(String email) {
        if (email == null) return "null";
        int atIndex = email.indexOf("@");
        return atIndex > 0 ? email.substring(0, 1) + "***" + email.substring(atIndex) : email;
    }
}
