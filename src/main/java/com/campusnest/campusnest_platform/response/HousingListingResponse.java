package com.campusnest.campusnest_platform.response;

import com.campusnest.campusnest_platform.enums.ListingStatus;
import com.campusnest.campusnest_platform.enums.ListingType;
import com.campusnest.campusnest_platform.enums.PropertyType;
import com.campusnest.campusnest_platform.models.HousingListing;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
public class HousingListingResponse {
    
    private Long id;
    private String title;
    private String description;
    private ListingType listingType;
    private PropertyType propertyType;
    private ListingStatus status;
    
    private BigDecimal pricePerMonth;
    private BigDecimal pricePerNight;
    
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    
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
    
    private Instant createdAt;
    private Instant updatedAt;
    
    // User information (limited for privacy)
    private String ownerName;
    private String ownerUniversity;
    
    public static HousingListingResponse fromEntity(HousingListing listing) {
        HousingListingResponse response = new HousingListingResponse();
        response.setId(listing.getId());
        response.setTitle(listing.getTitle());
        response.setDescription(listing.getDescription());
        response.setListingType(listing.getListingType());
        response.setPropertyType(listing.getPropertyType());
        response.setStatus(listing.getStatus());
        response.setPricePerMonth(listing.getPricePerMonth());
        response.setPricePerNight(listing.getPricePerNight());
        response.setAddress(listing.getAddress());
        response.setCity(listing.getCity());
        response.setState(listing.getState());
        response.setZipCode(listing.getZipCode());
        response.setCountry(listing.getCountry());
        response.setNearbyUniversities(listing.getNearbyUniversities());
        response.setBedrooms(listing.getBedrooms());
        response.setBathrooms(listing.getBathrooms());
        response.setSquareFootage(listing.getSquareFootage());
        response.setFurnished(listing.getFurnished());
        response.setUtilitiesIncluded(listing.getUtilitiesIncluded());
        response.setParkingAvailable(listing.getParkingAvailable());
        response.setPetsAllowed(listing.getPetsAllowed());
        response.setAvailableFrom(listing.getAvailableFrom());
        response.setAvailableUntil(listing.getAvailableUntil());
        response.setMinStayDays(listing.getMinStayDays());
        response.setMaxStayDays(listing.getMaxStayDays());
        response.setAmenities(listing.getAmenities());
        response.setHouseRules(listing.getHouseRules());
        response.setAdditionalInfo(listing.getAdditionalInfo());
        response.setContactEmail(listing.getContactEmail());
        response.setContactPhone(listing.getContactPhone());
        response.setCreatedAt(listing.getCreatedAt());
        response.setUpdatedAt(listing.getUpdatedAt());
        
        // Set owner information (limited for privacy)
        if (listing.getUser() != null) {
            response.setOwnerName(listing.getUser().getFirstName());
            response.setOwnerUniversity(listing.getUser().getUniversityDomain());
        }
        
        return response;
    }
}