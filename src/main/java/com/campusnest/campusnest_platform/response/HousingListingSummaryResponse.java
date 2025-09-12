package com.campusnest.campusnest_platform.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class HousingListingSummaryResponse {
    
    private Long id;
    
    private String title;
    
    private BigDecimal price;
    
    private String city;
    
    private Integer bedrooms;
    
    private Integer bathrooms;
    
    private LocalDate availableFrom;
    
    private LocalDate availableTo;
    
    private LocalDateTime createdAt;
    
    private String primaryImageUrl; // Only the main image for listings view
    
    private Boolean isFavorited;
    
    private Integer favoriteCount;
    
    private String ownerName; // First name only for privacy
}