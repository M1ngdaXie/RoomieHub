package com.campusnest.campusnest_platform.controllers.housing;

import com.campusnest.campusnest_platform.models.HousingListing;
import com.campusnest.campusnest_platform.services.HousingListingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/simple-housing")
@CrossOrigin(origins = "http://localhost:3000")
public class SimpleHousingController {
    
    @Autowired
    private HousingListingService housingListingService;
    
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Simple housing controller works");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<HousingListing> listing = housingListingService.findById(id);
            if (listing.isPresent()) {
                HousingListing l = listing.get();
                response.put("success", true);
                response.put("id", l.getId());
                response.put("title", l.getTitle());
                response.put("price", l.getPrice());
                response.put("city", l.getCity());
                response.put("active", l.getIsActive());
            } else {
                response.put("success", false);
                response.put("message", "Listing not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAll() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<HousingListing> listings = housingListingService.findAllActive();
            response.put("success", true);
            response.put("count", listings.size());
            response.put("listings", listings);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/search-simple")
    public ResponseEntity<Map<String, Object>> simpleSearch(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<HousingListing> listings = housingListingService.findAllActive();
            response.put("success", true);
            response.put("count", listings.size());
            response.put("request", request);
            response.put("listings", listings);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}