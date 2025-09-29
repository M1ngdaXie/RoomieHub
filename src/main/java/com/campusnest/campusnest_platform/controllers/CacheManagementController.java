package com.campusnest.campusnest_platform.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/cache")
@CrossOrigin(origins = "http://localhost:3000")
public class CacheManagementController {
    
    @Autowired
    private CacheManager cacheManager;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @PostMapping("/clear-all")
    public ResponseEntity<Map<String, Object>> clearAllCaches() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Clear Spring Cache Manager caches
            cacheManager.getCacheNames().forEach(cacheName -> {
                if (cacheManager.getCache(cacheName) != null) {
                    cacheManager.getCache(cacheName).clear();
                }
            });
            
            // Clear all Redis keys (be careful with this in production!)
            Set<String> keys = redisTemplate.keys("*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
            }
            
            response.put("success", true);
            response.put("message", "All caches and Redis keys cleared successfully");
            response.put("clearedCaches", cacheManager.getCacheNames());
            response.put("clearedRedisKeys", keys != null ? keys.size() : 0);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/clear-housing-cache")
    public ResponseEntity<Map<String, Object>> clearHousingCache() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Clear housing-related caches
            if (cacheManager.getCache("housing-listings") != null) {
                cacheManager.getCache("housing-listings").clear();
            }
            if (cacheManager.getCache("housing-search") != null) {
                cacheManager.getCache("housing-search").clear();
            }
            
            // Clear housing-related Redis keys
            Set<String> housingKeys = redisTemplate.keys("housing-listings::*");
            if (housingKeys != null && !housingKeys.isEmpty()) {
                redisTemplate.delete(housingKeys);
            }
            
            response.put("success", true);
            response.put("message", "Housing caches cleared successfully");
            response.put("clearedRedisKeys", housingKeys != null ? housingKeys.size() : 0);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getCacheStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Get all Redis keys
            Set<String> allKeys = redisTemplate.keys("*");
            Set<String> housingKeys = redisTemplate.keys("housing-listings::*");
            
            response.put("availableCaches", cacheManager.getCacheNames());
            response.put("totalRedisKeys", allKeys != null ? allKeys.size() : 0);
            response.put("housingKeys", housingKeys != null ? housingKeys.size() : 0);
            response.put("allRedisKeys", allKeys);
            
        } catch (Exception e) {
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
}