package com.campusnest.campusnest_platform.config;

import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.repository.UserRepository;
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
            if (authHeaders != null && !authHeaders.isEmpty()) {
                String authHeader = authHeaders.get(0);
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    String token = authHeader.substring(7);
                    
                    if (token != null && !token.trim().isEmpty()) {
                        String email = jwtTokenService.extractEmail(token);
                        if (email != null && jwtTokenService.isTokenValid(token, email)) {
                            Optional<User> userOptional = userRepository.findByEmail(email);
                            
                            if (userOptional.isPresent()) {
                                User user = userOptional.get();
                                if (user.isEnabled() && user.isAccountNonLocked() && 
                                    user.isAccountNonExpired() && user.isCredentialsNonExpired()) {
                                
                                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                                        user, null, user.getAuthorities()
                                    );
                                    accessor.setUser(authentication);
                                    log.info("WebSocket authenticated user: {} with role: {}", 
                                        maskEmail(user.getEmail()), user.getRole().name());
                                    return;
                                }
                            }
                        }
                    }
                }
            }
            
            log.debug("WebSocket connection without valid authentication");
        } catch (Exception e) {
            log.error("WebSocket authentication error: {}", e.getMessage());
        }
    }

    private String maskEmail(String email) {
        if (email == null) return "null";
        int atIndex = email.indexOf("@");
        return atIndex > 0 ? email.substring(0, 1) + "***" + email.substring(atIndex) : email;
    }
}