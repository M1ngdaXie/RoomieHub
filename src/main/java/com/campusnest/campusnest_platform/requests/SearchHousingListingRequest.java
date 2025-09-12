package com.campusnest.campusnest_platform.requests;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SearchHousingListingRequest {
    
    private String city;
    
    @DecimalMin(value = "0.0", message = "Minimum price must be 0 or greater")
    private BigDecimal minPrice;
    
    @DecimalMax(value = "50000.0", message = "Maximum price must be $50,000 or less")
    private BigDecimal maxPrice;
    
    private LocalDate availableFrom;
    
    private LocalDate availableTo;
    
    private Integer minBedrooms;
    
    private Integer maxBedrooms;
    
    private Integer minBathrooms;
    
    private Integer maxBathrooms;
    
    // Pagination parameters
    private Integer page = 0;
    
    private Integer size = 20;
    
    // Sorting parameters
    private String sortBy = "createdAt";
    
    private String sortDirection = "desc";
}