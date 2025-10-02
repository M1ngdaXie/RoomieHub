package com.campusnest.campusnest_platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Principal;
import java.util.List;

// @Configuration  // ← DISABLED - THIS WAS A MASSIVE SECURITY VULNERABILITY!
// This class was bypassing ALL WebSocket authentication and allowing anyone to connect
// as a fake "test-user" without any JWT validation. DO NOT RE-ENABLE!
public class WebSocketSecurityConfig {

    public ChannelInterceptor createAuthChannelInterceptor() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // For now, create a simple test user for WebSocket connections
                    // This allows us to test WebSocket functionality without JWT complications
                    
                    // Create a simple test principal
                    Principal testPrincipal = new UsernamePasswordAuthenticationToken(
                        "test-user", 
                        null, 
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    
                    // Set user in the accessor for this WebSocket session
                    accessor.setUser(testPrincipal);
                }
                
                return message;
            }
        };
    }
}