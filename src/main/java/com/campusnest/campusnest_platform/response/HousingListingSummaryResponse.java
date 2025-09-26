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
    
    public static HousingListingSummaryResponse fromHousingListing(com.campusnest.campusnest_platform.models.HousingListing listing) {
        if (listing == null) return null;
        
        HousingListingSummaryResponse response = new HousingListingSummaryResponse();
        response.setId(listing.getId());
        response.setTitle(listing.getTitle());
        response.setPrice(listing.getPrice());
        response.setCity(listing.getCity());
        response.setBedrooms(listing.getBedrooms());
        response.setBathrooms(listing.getBathrooms());
        response.setAvailableFrom(listing.getAvailableFrom());
        response.setAvailableTo(listing.getAvailableTo());
        response.setCreatedAt(listing.getCreatedAt());
        response.setOwnerName(listing.getOwner().getFirstName());
        return response;
    }
}