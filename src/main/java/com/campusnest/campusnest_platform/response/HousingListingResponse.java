package com.campusnest.campusnest_platform.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HousingListingResponse {
    
    private Long id;
    
    private String title;
    
    private String description;
    
    private BigDecimal price;
    
    private String address;
    
    private String city;
    
    private Integer bedrooms;
    
    private Integer bathrooms;
    
    private LocalDate availableFrom;
    
    private LocalDate availableTo;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private OwnerInfo owner;
    
    private List<ImageInfo> images;
    
    private Boolean isFavorited; // Will be set based on current user context
    
    private Integer favoriteCount;
    
    @Data
    public static class OwnerInfo {
        private Long id;
        private String firstName;
        private String lastName;
        private String email; // Masked for privacy in public responses
        private String universityDomain;
    }
    
    @Data
    public static class ImageInfo {
        private Long id;
        private String s3Key;
        private String imageUrl; // Signed URL - will be generated when needed
        private Boolean isPrimary;
        private Integer displayOrder;
    }
}