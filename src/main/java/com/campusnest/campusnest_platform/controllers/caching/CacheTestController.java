package com.campusnest.campusnest_platform.controllers.caching;

import com.campusnest.campusnest_platform.models.HousingListing;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.services.HousingListingService;
import com.campusnest.campusnest_platform.services.MessagingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/test/cache")
@Slf4j
public class CacheTestController {
    
    @Autowired
    private HousingListingService housingListingService;
    
    @Autowired
    private MessagingService messagingService;
    
    @Autowired
    private CacheManager cacheManager;
    
    @GetMapping("/housing/{id}")
    public ResponseEntity<Map<String, Object>> testHousingCache(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        long start = System.currentTimeMillis();
        Optional<HousingListing> listing = housingListingService.findById(id);
        long duration = System.currentTimeMillis() - start;
        
        response.put("listingFound", listing.isPresent());
        response.put("queryTime", duration + "ms");
        response.put("timestamp", System.currentTimeMillis());
        
        if (listing.isPresent()) {
            response.put("listingTitle", listing.get().getTitle());
        }
        
        log.info("Housing cache test - Query took: {}ms", duration);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> testUnreadCountCache(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        if (authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            
            long start = System.currentTimeMillis();
            long unreadCount = messagingService.getTotalUnreadMessageCount(user);
            long duration = System.currentTimeMillis() - start;
            
            response.put("unreadCount", unreadCount);
            response.put("queryTime", duration + "ms");
            response.put("timestamp", System.currentTimeMillis());
            response.put("userId", user.getId());
            
            log.info("Unread count cache test - Query took: {}ms", duration);
        } else {
            response.put("error", "User not found in authentication context");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/cache-info")
    public ResponseEntity<Map<String, Object>> getCacheInfo() {
        Map<String, Object> response = new HashMap<>();
        
        // List all available caches
        response.put("availableCaches", cacheManager.getCacheNames());
        
        // Check if specific caches exist
        response.put("housingListingsCache", cacheManager.getCache("housing-listings") != null);
        response.put("housingSearchCache", cacheManager.getCache("housing-search") != null);
        response.put("unreadCountsCache", cacheManager.getCache("unread-counts") != null);
        response.put("conversationsCache", cacheManager.getCache("conversations") != null);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/clear-cache/{cacheName}")
    public ResponseEntity<Map<String, Object>> clearCache(@PathVariable String cacheName) {
        Map<String, Object> response = new HashMap<>();
        
        if (cacheManager.getCache(cacheName) != null) {
            cacheManager.getCache(cacheName).clear();
            response.put("success", true);
            response.put("message", "Cache '" + cacheName + "' cleared successfully");
            log.info("Manually cleared cache: {}", cacheName);
        } else {
            response.put("success", false);
            response.put("message", "Cache '" + cacheName + "' not found");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/clear-all-caches")
    public ResponseEntity<Map<String, Object>> clearAllCaches() {
        Map<String, Object> response = new HashMap<>();
        
        cacheManager.getCacheNames().forEach(cacheName -> {
            cacheManager.getCache(cacheName).clear();
        });
        
        response.put("success", true);
        response.put("message", "All caches cleared successfully");
        response.put("clearedCaches", cacheManager.getCacheNames());
        
        log.info("Manually cleared all caches");
        return ResponseEntity.ok(response);
    }
}