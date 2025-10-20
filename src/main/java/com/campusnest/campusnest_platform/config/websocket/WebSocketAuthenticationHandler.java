package com.campusnest.campusnest_platform.config.websocket;

import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.repository.user.UserRepository;
import com.campusnest.campusnest_platform.services.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class WebSocketAuthenticationHandler implements ChannelInterceptor {

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            authenticateUser(accessor);
        }
        
        return message;
    }

    private void authenticateUser(StompHeaderAccessor accessor) {
        try {
            // Try to get Authorization header
            List<String> authHeaders = accessor.getNativeHeader("Authorization");
            if (authHeaders == null || authHeaders.isEmpty()) {
                rejectConnection("No authorization token provided. Please include 'Authorization: Bearer <token>' header.");
                return;
            }
            
            String authHeader = authHeaders.get(0);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                rejectConnection("Invalid authorization format. Expected 'Bearer <token>', got: " + 
                    (authHeader != null ? authHeader.substring(0, Math.min(20, authHeader.length())) + "..." : "null"));
                return;
            }
            
            String token = authHeader.substring(7);
            if (token == null || token.trim().isEmpty()) {
                rejectConnection("Empty authorization token. Please provide a valid JWT token.");
                return;
            }
            
            // Validate token format before processing
            if (token.length() < 20 || !token.contains(".")) {
                rejectConnection("Invalid token format. JWT tokens should have 3 parts separated by dots.");
                return;
            }
            
            String email;
            try {
                email = jwtTokenService.extractEmail(token);
            } catch (Exception tokenError) {
                String errorMsg = getTokenErrorMessage(tokenError);
                rejectConnection("Token validation failed: " + errorMsg);
                return;
            }
            
            if (email == null) {
                rejectConnection("Token does not contain a valid email address.");
                return;
            }
            
            if (!jwtTokenService.isTokenValid(token, email)) {
                rejectConnection("Token has expired or is invalid. Please login again to get a new token.");
                return;
            }
            
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (!userOptional.isPresent()) {
                rejectConnection("User account not found for email: " + maskEmail(email));
                return;
            }
            
            User user = userOptional.get();
            
            // Check account status
            if (!user.isEnabled()) {
                rejectConnection("Your account has been disabled. Please contact support.");
                return;
            }
            if (!user.isAccountNonLocked()) {
                rejectConnection("Your account is temporarily locked. Please contact support.");
                return;
            }
            if (!user.isAccountNonExpired()) {
                rejectConnection("Your account has expired. Please contact support.");
                return;
            }
            if (!user.isCredentialsNonExpired()) {
                rejectConnection("Your credentials have expired. Please login again.");
                return;
            }
            
            // Success! Set authenticated user
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
            );
            accessor.setUser(authentication);
            log.info("✅ WebSocket authenticated user: {} with role: {}", 
                    maskEmail(user.getEmail()), user.getRole().name());
            
        } catch (Exception e) {
            log.error("❌ Unexpected WebSocket authentication error: {}", e.getMessage());
            rejectConnection("Authentication system error. Please try again or contact support.");
        }
    }
    
    private void rejectConnection(String userFriendlyMessage) {
        log.warn("🚫 WebSocket connection REJECTED: {}", userFriendlyMessage);
        throw new IllegalArgumentException("WebSocket Authentication Failed: " + userFriendlyMessage);
    }
    
    private String getTokenErrorMessage(Exception tokenError) {
        String errorMsg = tokenError.getMessage();
        
        if (errorMsg.contains("JSON format")) {
            return "Token appears to be corrupted or truncated.";
        } else if (errorMsg.contains("expired")) {
            return "Token has expired. Please login again.";
        } else if (errorMsg.contains("signature")) {
            return "Token signature is invalid.";
        } else if (errorMsg.contains("malformed")) {
            return "Token format is malformed.";
        } else {
            return "Token is invalid or corrupted.";
        }
    }

    private String maskEmail(String email) {
        if (email == null) return "null";
        int atIndex = email.indexOf("@");
        return atIndex > 0 ? email.substring(0, 1) + "***" + email.substring(atIndex) : email;
    }
}