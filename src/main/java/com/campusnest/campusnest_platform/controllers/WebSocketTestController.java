package com.campusnest.campusnest_platform.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@Slf4j
public class WebSocketTestController {

    @MessageMapping("/test")
    @SendToUser("/queue/test-response")
    public Map<String, Object> handleTestMessage(Map<String, Object> message, Principal principal) {
        String userName = principal != null ? principal.getName() : "anonymous";
        log.info("Received WebSocket test message from user: {}", userName);
        log.info("Message content: {}", message);
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "WebSocket connection working!");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("user", userName);
        response.put("receivedMessage", message);
        
        return response;
    }

    @MessageMapping("/broadcast-test")
    @SendTo("/topic/broadcast")
    public Map<String, Object> handleBroadcastTest(Map<String, Object> message, Principal principal) {
        String userName = principal != null ? principal.getName() : "anonymous";
        log.info("Broadcasting test message from user: {}", userName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "broadcast");
        response.put("from", userName);
        response.put("message", message.get("content"));
        response.put("timestamp", LocalDateTime.now().toString());
        
        return response;
    }

    @MessageMapping("/ping")
    @SendToUser("/queue/pong")
    public Map<String, Object> handlePing(Principal principal) {
        String userName = principal != null ? principal.getName() : "anonymous";
        log.info("Ping received from user: {}", userName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("type", "pong");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("user", userName);
        
        return response;
    }
}