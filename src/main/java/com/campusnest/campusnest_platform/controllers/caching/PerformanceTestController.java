package com.campusnest.campusnest_platform.controllers.caching;

import com.campusnest.campusnest_platform.models.HousingListing;
import com.campusnest.campusnest_platform.repository.housing.HousingListingRepository;
import com.campusnest.campusnest_platform.services.HousingListingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/performance-test")
@CrossOrigin(origins = "http://localhost:3000")
@Slf4j
public class PerformanceTestController {

    @Autowired
    private HousingListingService housingListingService;
    
    @Autowired
    private HousingListingRepository housingListingRepository;
    
    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/cache-vs-nocache/{id}")
    public ResponseEntity<Map<String, Object>> testCachePerformance(@PathVariable Long id,
                                                                   @RequestParam(defaultValue = "5") int iterations) {
        Map<String, Object> results = new HashMap<>();
        
        log.info("🧪 PERFORMANCE TEST START - ID: {}, Iterations: {}", id, iterations);
        
        // Test 1: With Cache (using service layer)
        List<Long> cachedTimes = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            Optional<HousingListing> listing = housingListingService.findById(id);
            long endTime = System.nanoTime();
            long durationMs = (endTime - startTime) / 1_000_000;
            cachedTimes.add(durationMs);
            log.info("  🟢 Cached call {}: {}ms (found: {})", i+1, durationMs, listing.isPresent());
        }
        
        // Clear cache before direct database test
        if (cacheManager.getCache("housing-listings") != null) {
            cacheManager.getCache("housing-listings").clear();
            log.info("  🗑️ Cache cleared for direct database test");
        }
        
        // Test 2: Direct Database (bypassing cache)
        List<Long> directTimes = new ArrayList<>();
        for (int i = 0; i < iterations; i++) {
            long startTime = System.nanoTime();
            
            // Direct database query using the same logic as service
            HousingListing listing = housingListingRepository.findByIdWithOwner(id);
            if (listing != null) {
                List<HousingListing> singleList = List.of(listing);
                housingListingRepository.findWithImages(singleList);
                housingListingRepository.findWithFavorites(singleList);
            }
            
            long endTime = System.nanoTime();
            long durationMs = (endTime - startTime) / 1_000_000;
            directTimes.add(durationMs);
            log.info("  🔴 Direct call {}: {}ms (found: {})", i+1, durationMs, listing != null);
        }
        
        // Calculate statistics
        double cachedAvg = cachedTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        double directAvg = directTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);
        
        long cachedMin = cachedTimes.stream().mapToLong(Long::longValue).min().orElse(0);
        long cachedMax = cachedTimes.stream().mapToLong(Long::longValue).max().orElse(0);
        long directMin = directTimes.stream().mapToLong(Long::longValue).min().orElse(0);
        long directMax = directTimes.stream().mapToLong(Long::longValue).max().orElse(0);
        
        double speedupFactor = directAvg / cachedAvg;
        double improvementPercent = ((directAvg - cachedAvg) / directAvg) * 100;
        
        // Compile results
        results.put("testId", id);
        results.put("iterations", iterations);
        results.put("timestamp", System.currentTimeMillis());
        
        Map<String, Object> cachedStats = new HashMap<>();
        cachedStats.put("times", cachedTimes);
        cachedStats.put("average", Math.round(cachedAvg * 100.0) / 100.0);
        cachedStats.put("min", cachedMin);
        cachedStats.put("max", cachedMax);
        
        Map<String, Object> directStats = new HashMap<>();
        directStats.put("times", directTimes);
        directStats.put("average", Math.round(directAvg * 100.0) / 100.0);
        directStats.put("min", directMin);
        directStats.put("max", directMax);
        
        results.put("withCache", cachedStats);
        results.put("directDatabase", directStats);
        results.put("speedupFactor", Math.round(speedupFactor * 100.0) / 100.0);
        results.put("improvementPercent", Math.round(improvementPercent * 100.0) / 100.0);
        
        // Determine if caching is effective
        String conclusion;
        if (speedupFactor > 2.0) {
            conclusion = "🚀 Cache is highly effective! " + speedupFactor + "x faster";
        } else if (speedupFactor > 1.5) {
            conclusion = "✅ Cache provides good performance benefit: " + speedupFactor + "x faster";
        } else if (speedupFactor > 1.1) {
            conclusion = "⚠️ Cache provides minimal benefit: " + speedupFactor + "x faster";
        } else {
            conclusion = "❌ Cache is not effective - similar performance to direct database";
        }
        
        results.put("conclusion", conclusion);
        
        log.info("🧪 PERFORMANCE TEST RESULTS:");
        log.info("  📊 Cached Average: {}ms (min: {}ms, max: {}ms)", cachedAvg, cachedMin, cachedMax);
        log.info("  📊 Direct Average: {}ms (min: {}ms, max: {}ms)", directAvg, directMin, directMax);
        log.info("  📈 Speedup Factor: {}x", speedupFactor);
        log.info("  📈 Improvement: {}%", improvementPercent);
        log.info("  🎯 {}", conclusion);
        
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/warmup-cache/{id}")
    public ResponseEntity<Map<String, Object>> warmupCache(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        log.info("🔥 WARMING UP CACHE for ID: {}", id);
        
        long startTime = System.nanoTime();
        Optional<HousingListing> listing = housingListingService.findById(id);
        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;
        
        response.put("cacheWarmedUp", true);
        response.put("id", id);
        response.put("found", listing.isPresent());
        response.put("warmupTime", durationMs + "ms");
        
        log.info("🔥 Cache warmed up in {}ms", durationMs);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/clear-cache")
    public ResponseEntity<Map<String, Object>> clearCache() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (cacheManager.getCache("housing-listings") != null) {
                cacheManager.getCache("housing-listings").clear();
                response.put("success", true);
                response.put("message", "Housing listings cache cleared");
                log.info("🗑️ Housing listings cache manually cleared");
            } else {
                response.put("success", false);
                response.put("message", "Cache not found");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            log.error("❌ Error clearing cache: {}", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test-sequence/{id}")
    public ResponseEntity<Map<String, Object>> testSequence(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> sequence = new ArrayList<>();
        
        log.info("🎬 TESTING CACHE SEQUENCE for ID: {}", id);
        
        // Step 1: Clear cache
        if (cacheManager.getCache("housing-listings") != null) {
            cacheManager.getCache("housing-listings").clear();
            log.info("  1️⃣ Cache cleared");
        }
        
        // Step 2: First call (should be cache miss)
        long startTime = System.nanoTime();
        Optional<HousingListing> listing1 = housingListingService.findById(id);
        long endTime = System.nanoTime();
        long duration1 = (endTime - startTime) / 1_000_000;
        
        Map<String, Object> call1 = new HashMap<>();
        call1.put("call", 1);
        call1.put("expected", "cache miss");
        call1.put("duration", duration1 + "ms");
        call1.put("found", listing1.isPresent());
        sequence.add(call1);
        log.info("  1️⃣ First call (cache miss): {}ms", duration1);
        
        // Step 3: Second call (should be cache hit)
        startTime = System.nanoTime();
        Optional<HousingListing> listing2 = housingListingService.findById(id);
        endTime = System.nanoTime();
        long duration2 = (endTime - startTime) / 1_000_000;
        
        Map<String, Object> call2 = new HashMap<>();
        call2.put("call", 2);
        call2.put("expected", "cache hit");
        call2.put("duration", duration2 + "ms");
        call2.put("found", listing2.isPresent());
        sequence.add(call2);
        log.info("  2️⃣ Second call (cache hit): {}ms", duration2);
        
        // Step 4: Third call (should be cache hit)
        startTime = System.nanoTime();
        Optional<HousingListing> listing3 = housingListingService.findById(id);
        endTime = System.nanoTime();
        long duration3 = (endTime - startTime) / 1_000_000;
        
        Map<String, Object> call3 = new HashMap<>();
        call3.put("call", 3);
        call3.put("expected", "cache hit");
        call3.put("duration", duration3 + "ms");
        call3.put("found", listing3.isPresent());
        sequence.add(call3);
        log.info("  3️⃣ Third call (cache hit): {}ms", duration3);
        
        // Analysis
        double speedup2 = (double) duration1 / duration2;
        double speedup3 = (double) duration1 / duration3;
        
        response.put("id", id);
        response.put("sequence", sequence);
        response.put("speedup2x", Math.round(speedup2 * 100.0) / 100.0);
        response.put("speedup3x", Math.round(speedup3 * 100.0) / 100.0);
        
        String analysis;
        if (speedup2 > 2.0 || speedup3 > 2.0) {
            analysis = "✅ Cache is working effectively!";
        } else if (speedup2 > 1.3 || speedup3 > 1.3) {
            analysis = "⚠️ Cache provides some benefit but may not be configured optimally";
        } else {
            analysis = "❌ Cache appears to not be working - all calls similar timing";
        }
        
        response.put("analysis", analysis);
        
        log.info("🎬 SEQUENCE ANALYSIS:");
        log.info("  📊 Call 1 (miss): {}ms", duration1);
        log.info("  📊 Call 2 (hit?): {}ms ({}x speedup)", duration2, speedup2);
        log.info("  📊 Call 3 (hit?): {}ms ({}x speedup)", duration3, speedup3);
        log.info("  🎯 {}", analysis);
        
        return ResponseEntity.ok(response);
    }
}