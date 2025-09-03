package com.campusnest.campusnest_platform.controllers;

import com.campusnest.campusnest_platform.enums.ListingType;
import com.campusnest.campusnest_platform.enums.PropertyType;
import com.campusnest.campusnest_platform.models.HousingListing;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.requests.CreateHousingListingRequest;
import com.campusnest.campusnest_platform.response.HousingListingResponse;
import com.campusnest.campusnest_platform.services.HousingListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/housing")
@RequiredArgsConstructor
@Slf4j
public class HousingController {

    private final HousingListingService housingListingService;

    @PostMapping("/listings")
    public ResponseEntity<HousingListingResponse> createListing(
            @RequestBody @Valid CreateHousingListingRequest request,
            @AuthenticationPrincipal User user) {
        
        log.info("Creating housing listing for user: {}", user.getEmail());
        
        try {
            HousingListing listing = mapToHousingListing(request);
            HousingListing createdListing = housingListingService.createListing(listing, user);
            
            HousingListingResponse response = HousingListingResponse.fromEntity(createdListing);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Error creating housing listing: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/listings")
    public ResponseEntity<Page<HousingListingResponse>> getAllListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) ListingType type,
            @RequestParam(required = false) PropertyType propertyType,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String university,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<HousingListing> listings;
        
        try {
            if (search != null && !search.trim().isEmpty()) {
                listings = housingListingService.searchListings(search.trim(), pageable);
            } else if (city != null && university != null) {
                listings = housingListingService.findByCityAndUniversity(city, university, pageable);
            } else if (minPrice != null && maxPrice != null) {
                listings = housingListingService.findByPriceRange(minPrice, maxPrice, pageable);
            } else if (startDate != null && endDate != null) {
                listings = housingListingService.findAvailableInDateRange(startDate, endDate, pageable);
            } else if (type != null) {
                listings = housingListingService.getListingsByType(type, pageable);
            } else if (propertyType != null) {
                listings = housingListingService.getListingsByPropertyType(propertyType, pageable);
            } else {
                listings = housingListingService.getActiveListings(pageable);
            }
            
            Page<HousingListingResponse> response = listings.map(HousingListingResponse::fromEntity);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching housing listings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/listings/{id}")
    public ResponseEntity<HousingListingResponse> getListingById(@PathVariable Long id) {
        log.info("Fetching housing listing with ID: {}", id);
        
        try {
            Optional<HousingListing> listing = housingListingService.getListingById(id);
            
            if (listing.isPresent()) {
                HousingListingResponse response = HousingListingResponse.fromEntity(listing.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("Error fetching housing listing: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/my-listings")
    public ResponseEntity<List<HousingListingResponse>> getMyListings(
            @AuthenticationPrincipal User user) {
        
        log.info("Fetching listings for user: {}", user.getEmail());
        
        try {
            List<HousingListing> listings = housingListingService.getListingsByUser(user);
            List<HousingListingResponse> response = listings.stream()
                    .map(HousingListingResponse::fromEntity)
                    .toList();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error fetching user's housing listings: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/listings/{id}")
    public ResponseEntity<HousingListingResponse> updateListing(
            @PathVariable Long id,
            @RequestBody @Valid CreateHousingListingRequest request,
            @AuthenticationPrincipal User user) {
        
        log.info("Updating housing listing {} for user: {}", id, user.getEmail());
        
        try {
            HousingListing updatedListing = mapToHousingListing(request);
            HousingListing result = housingListingService.updateListing(id, updatedListing, user);
            
            HousingListingResponse response = HousingListingResponse.fromEntity(result);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Update listing failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Error updating housing listing: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/listings/{id}")
    public ResponseEntity<Void> deleteListing(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        log.info("Deleting housing listing {} for user: {}", id, user.getEmail());
        
        try {
            housingListingService.deleteListing(id, user);
            return ResponseEntity.noContent().build();
            
        } catch (IllegalArgumentException e) {
            log.warn("Delete listing failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Error deleting housing listing: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/listings/{id}/deactivate")
    public ResponseEntity<Void> deactivateListing(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        log.info("Deactivating housing listing {} for user: {}", id, user.getEmail());
        
        try {
            housingListingService.deactivateListing(id, user);
            return ResponseEntity.ok().build();
            
        } catch (IllegalArgumentException e) {
            log.warn("Deactivate listing failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Error deactivating housing listing: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/listings/{id}/activate")
    public ResponseEntity<Void> activateListing(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        log.info("Activating housing listing {} for user: {}", id, user.getEmail());
        
        try {
            housingListingService.activateListing(id, user);
            return ResponseEntity.ok().build();
            
        } catch (IllegalArgumentException e) {
            log.warn("Activate listing failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Error activating housing listing: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private HousingListing mapToHousingListing(CreateHousingListingRequest request) {
        HousingListing listing = new HousingListing();
        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setListingType(request.getListingType());
        listing.setPropertyType(request.getPropertyType());
        listing.setPricePerMonth(request.getPricePerMonth());
        listing.setPricePerNight(request.getPricePerNight());
        listing.setAddress(request.getAddress());
        listing.setCity(request.getCity());
        listing.setState(request.getState());
        listing.setZipCode(request.getZipCode());
        listing.setCountry(request.getCountry());
        listing.setNearbyUniversities(request.getNearbyUniversities());
        listing.setBedrooms(request.getBedrooms());
        listing.setBathrooms(request.getBathrooms());
        listing.setSquareFootage(request.getSquareFootage());
        listing.setFurnished(request.getFurnished());
        listing.setUtilitiesIncluded(request.getUtilitiesIncluded());
        listing.setParkingAvailable(request.getParkingAvailable());
        listing.setPetsAllowed(request.getPetsAllowed());
        listing.setAvailableFrom(request.getAvailableFrom());
        listing.setAvailableUntil(request.getAvailableUntil());
        listing.setMinStayDays(request.getMinStayDays());
        listing.setMaxStayDays(request.getMaxStayDays());
        listing.setAmenities(request.getAmenities());
        listing.setHouseRules(request.getHouseRules());
        listing.setAdditionalInfo(request.getAdditionalInfo());
        listing.setContactEmail(request.getContactEmail());
        listing.setContactPhone(request.getContactPhone());
        return listing;
    }
}