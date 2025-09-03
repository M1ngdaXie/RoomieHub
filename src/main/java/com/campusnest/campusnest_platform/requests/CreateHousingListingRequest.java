package com.campusnest.campusnest_platform.requests;

import com.campusnest.campusnest_platform.enums.ListingType;
import com.campusnest.campusnest_platform.enums.PropertyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateHousingListingRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String description;
    
    @NotNull(message = "Listing type is required")
    private ListingType listingType;
    
    @NotNull(message = "Property type is required")
    private PropertyType propertyType;
    
    private BigDecimal pricePerMonth;
    private BigDecimal pricePerNight;
    
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country = "USA";
    
    private String nearbyUniversities;
    
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer squareFootage;
    
    private Boolean furnished;
    private Boolean utilitiesIncluded;
    private Boolean parkingAvailable;
    private Boolean petsAllowed;
    
    private LocalDate availableFrom;
    private LocalDate availableUntil;
    
    private Integer minStayDays;
    private Integer maxStayDays;
    
    private String amenities;
    private String houseRules;
    private String additionalInfo;
    
    private String contactEmail;
    private String contactPhone;
}