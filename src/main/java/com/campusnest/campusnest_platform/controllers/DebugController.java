package com.campusnest.campusnest_platform.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "http://localhost:3000")
public class DebugController {
    
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> debugTest() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Debug endpoint works");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/housing-test/{id}")
    public ResponseEntity<Map<String, Object>> debugHousingTest(@PathVariable Long id, Authentication auth) {
        Map<String, Object> response = new HashMap<>();
        response.put("requestedId", id);
        response.put("hasAuth", auth != null);
        response.put("authName", auth != null ? auth.getName() : null);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/search-test")
    public ResponseEntity<Map<String, Object>> debugSearchTest(@RequestBody Map<String, Object> request, Authentication auth) {
        Map<String, Object> response = new HashMap<>();
        response.put("receivedRequest", request);
        response.put("hasAuth", auth != null);
        response.put("authName", auth != null ? auth.getName() : null);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/search-housing-request-test")
    public ResponseEntity<Map<String, Object>> debugSearchHousingRequestTest(
            @RequestBody com.campusnest.campusnest_platform.requests.SearchHousingListingRequest request, 
            Authentication auth) {
        Map<String, Object> response = new HashMap<>();
        response.put("received", true);
        response.put("requestCity", request.getCity());
        response.put("requestMinPrice", request.getMinPrice());
        response.put("hasAuth", auth != null);
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}