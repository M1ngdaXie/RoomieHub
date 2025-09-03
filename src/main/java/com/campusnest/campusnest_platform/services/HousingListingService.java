package com.campusnest.campusnest_platform.services;

import com.campusnest.campusnest_platform.enums.ListingStatus;
import com.campusnest.campusnest_platform.enums.ListingType;
import com.campusnest.campusnest_platform.enums.PropertyType;
import com.campusnest.campusnest_platform.models.HousingListing;
import com.campusnest.campusnest_platform.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HousingListingService {
    
    HousingListing createListing(HousingListing listing, User user);
    
    HousingListing updateListing(Long listingId, HousingListing updatedListing, User user);
    
    void deleteListing(Long listingId, User user);
    
    Optional<HousingListing> getListingById(Long listingId);
    
    List<HousingListing> getListingsByUser(User user);
    
    Page<HousingListing> getActiveListings(Pageable pageable);
    
    Page<HousingListing> getListingsByType(ListingType listingType, Pageable pageable);
    
    Page<HousingListing> getListingsByPropertyType(PropertyType propertyType, Pageable pageable);
    
    Page<HousingListing> searchListings(String searchTerm, Pageable pageable);
    
    Page<HousingListing> findByCityAndUniversity(String city, String universityDomain, Pageable pageable);
    
    Page<HousingListing> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);
    
    Page<HousingListing> findAvailableInDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);
    
    void deactivateListing(Long listingId, User user);
    
    void activateListing(Long listingId, User user);
    
    void expireOldListings();
}