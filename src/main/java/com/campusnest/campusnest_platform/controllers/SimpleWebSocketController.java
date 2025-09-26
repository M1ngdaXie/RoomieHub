package com.campusnest.campusnest_platform.controllers;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
public class SimpleWebSocketController {

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Map<String, Object> greeting(Map<String, Object> message) throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Hello, " + message.get("name") + "!");
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }

    @MessageMapping("/echo")
    @SendToUser("/queue/replies")
    public Map<String, Object> echo(Map<String, Object> message, Principal principal) {
        Map<String, Object> response = new HashMap<>();
        response.put("echo", message);
        response.put("user", principal != null ? principal.getName() : "anonymous");
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }

    @MessageMapping("/status")
    @SendToUser("/queue/status")
    public Map<String, Object> status(Principal principal) {
        Map<String, Object> response = new HashMap<>();
        response.put("connected", true);
        response.put("user", principal != null ? principal.getName() : "anonymous");
        response.put("timestamp", LocalDateTime.now().toString());
        return response;
    }
}