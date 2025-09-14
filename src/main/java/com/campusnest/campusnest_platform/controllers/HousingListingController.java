package com.campusnest.campusnest_platform.controllers;

import com.campusnest.campusnest_platform.models.HousingListing;
import com.campusnest.campusnest_platform.models.ListingImage;
import com.campusnest.campusnest_platform.repository.ListingImageRepository;
import com.campusnest.campusnest_platform.requests.CreateHousingListingRequest;
import com.campusnest.campusnest_platform.requests.SearchHousingListingRequest;
import com.campusnest.campusnest_platform.requests.UpdateHousingListingRequest;
import com.campusnest.campusnest_platform.response.HousingListingResponse;
import com.campusnest.campusnest_platform.response.HousingListingSummaryResponse;
import com.campusnest.campusnest_platform.services.HousingListingService;
import com.campusnest.campusnest_platform.services.S3Service;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/housing")
@CrossOrigin(origins = "http://localhost:3000")
public class HousingListingController {

    @Autowired
    private HousingListingService housingListingService;

    @Autowired
    private S3Service s3Service;
    
    @Autowired
    private ListingImageRepository listingImageRepository;

    // Create a new housing listing
    @PostMapping
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<?> createListing(@Valid @RequestBody CreateHousingListingRequest request,
                                         BindingResult bindingResult,
                                         Authentication authentication) {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body(getValidationErrors(bindingResult));
            }

            if (!request.isAvailableToAfterAvailableFrom()) {
                Map<String, String> error = new HashMap<>();
                error.put("availableTo", "Available to date must be after available from date");
                return ResponseEntity.badRequest().body(error);
            }

            // Convert request to entity
            HousingListing listing = new HousingListing();
            listing.setTitle(request.getTitle());
            listing.setDescription(request.getDescription());
            listing.setPrice(request.getPrice());
            listing.setAddress(request.getAddress());
            listing.setCity(request.getCity());
            listing.setBedrooms(request.getBedrooms());
            listing.setBathrooms(request.getBathrooms());
            listing.setAvailableFrom(request.getAvailableFrom());
            listing.setAvailableTo(request.getAvailableTo());

            // Create the listing
            HousingListing savedListing = housingListingService.createListing(listing, authentication.getName());

            // Handle image associations if provided
            if (request.getS3Keys() != null && !request.getS3Keys().isEmpty()) {
                associateImages(savedListing, request.getS3Keys());
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(savedListing, authentication.getName()));

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get all active listings (public endpoint)
    @GetMapping
    public ResponseEntity<List<HousingListingSummaryResponse>> getAllActiveListings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        try {
            List<HousingListing> listings = housingListingService.findAllActive();
            
            // Apply pagination manually for now
            int start = page * size;
            int end = Math.min(start + size, listings.size());
            
            if (start >= listings.size()) {
                return ResponseEntity.ok(List.of());
            }
            
            List<HousingListing> paginatedListings = listings.subList(start, end);
            List<HousingListingSummaryResponse> response = paginatedListings.stream()
                    .map(listing -> convertToSummaryResponse(listing, authentication != null ? authentication.getName() : null))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get listing by ID (public endpoint)
    @GetMapping("/{id}")
    public ResponseEntity<HousingListingResponse> getListingById(@PathVariable Long id,
                                                               Authentication authentication) {
        try {
            Optional<HousingListing> listing = housingListingService.findById(id);
            
            if (listing.isEmpty() || !listing.get().getIsActive()) {
                return ResponseEntity.notFound().build();
            }

            HousingListingResponse response = convertToResponse(listing.get(), 
                    authentication != null ? authentication.getName() : null);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Update listing (owner or admin only)
    @PutMapping("/{id}")
    @PreAuthorize("@housingListingService.isOwnerOrAdmin(#id, authentication.name)")
    public ResponseEntity<?> updateListing(@PathVariable Long id,
                                         @Valid @RequestBody UpdateHousingListingRequest request,
                                         BindingResult bindingResult,
                                         Authentication authentication) {
        try {
            if (bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().body(getValidationErrors(bindingResult));
            }

            if (!request.isAvailableToAfterAvailableFrom()) {
                Map<String, String> error = new HashMap<>();
                error.put("availableTo", "Available to date must be after available from date");
                return ResponseEntity.badRequest().body(error);
            }

            // Convert request to entity
            HousingListing updatedListing = new HousingListing();
            updatedListing.setTitle(request.getTitle());
            updatedListing.setDescription(request.getDescription());
            updatedListing.setPrice(request.getPrice());
            updatedListing.setAddress(request.getAddress());
            updatedListing.setCity(request.getCity());
            updatedListing.setBedrooms(request.getBedrooms());
            updatedListing.setBathrooms(request.getBathrooms());
            updatedListing.setAvailableFrom(request.getAvailableFrom());
            updatedListing.setAvailableTo(request.getAvailableTo());

            HousingListing saved = housingListingService.updateListing(id, updatedListing, authentication.getName());

            // Handle image updates if provided
            if (request.getS3Keys() != null) {
                updateImages(saved, request.getS3Keys());
            }

            return ResponseEntity.ok(convertToResponse(saved, authentication.getName()));

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Soft delete listing (owner or admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("@housingListingService.isOwnerOrAdmin(#id, authentication.name)")
    public ResponseEntity<?> deleteListing(@PathVariable Long id,
                                         Authentication authentication) {
        try {
            housingListingService.deleteListing(id, authentication.getName());
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Listing deactivated successfully");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get user's own listings
    @GetMapping("/my-listings")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<List<HousingListingSummaryResponse>> getMyListings(
            @RequestParam(defaultValue = "false") boolean includeInactive,
            Authentication authentication) {
        try {
            List<HousingListing> listings;
            
            if (includeInactive) {
                listings = housingListingService.findByOwner(authentication.getName());
            } else {
                listings = housingListingService.findActiveByOwner(authentication.getName());
            }

            List<HousingListingSummaryResponse> response = listings.stream()
                    .map(listing -> convertToSummaryResponse(listing, authentication.getName()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Search listings with filters
    @PostMapping("/search")
    public ResponseEntity<List<HousingListingSummaryResponse>> searchListings(
            @Valid @RequestBody SearchHousingListingRequest request,
            Authentication authentication) {
        try {
            List<HousingListing> listings;

            // Use the comprehensive search if all parameters are provided
            if (request.getCity() != null && request.getMinPrice() != null && 
                request.getMaxPrice() != null && request.getAvailableFrom() != null && 
                request.getAvailableTo() != null) {
                
                listings = housingListingService.searchListings(
                    request.getCity(),
                    request.getMinPrice(),
                    request.getMaxPrice(),
                    request.getAvailableFrom(),
                    request.getAvailableTo()
                );
            } else if (request.getCity() != null) {
                listings = housingListingService.searchByCity(request.getCity());
            } else if (request.getMinPrice() != null && request.getMaxPrice() != null) {
                listings = housingListingService.searchByPriceRange(request.getMinPrice(), request.getMaxPrice());
            } else {
                listings = housingListingService.findAllActive();
            }

            // Apply additional filtering for bedroom/bathroom criteria
            if (request.getMinBedrooms() != null || request.getMaxBedrooms() != null ||
                request.getMinBathrooms() != null || request.getMaxBathrooms() != null) {
                listings = listings.stream()
                        .filter(listing -> filterByBedBath(listing, request))
                        .collect(Collectors.toList());
            }

            // Apply pagination and sorting
            listings = applySorting(listings, request.getSortBy(), request.getSortDirection());
            listings = applyPagination(listings, request.getPage(), request.getSize());

            List<HousingListingSummaryResponse> response = listings.stream()
                    .map(listing -> convertToSummaryResponse(listing, 
                            authentication != null ? authentication.getName() : null))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Admin endpoint to get all listings (including inactive)
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HousingListingSummaryResponse>> getAllListingsForAdmin(
            Authentication authentication) {
        try {
            List<HousingListing> listings = housingListingService.findAll();
            
            List<HousingListingSummaryResponse> response = listings.stream()
                    .map(listing -> convertToSummaryResponse(listing, authentication.getName()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Admin endpoint to toggle listing status
    @PostMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleListingStatus(@PathVariable Long id,
                                                Authentication authentication) {
        try {
            HousingListing listing = housingListingService.toggleListingStatus(id, authentication.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Listing status updated successfully");
            response.put("isActive", listing.getIsActive());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Get statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics(Authentication authentication) {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalActiveListings", housingListingService.getTotalActiveListings());
            
            if (authentication != null) {
                stats.put("userListings", housingListingService.getTotalListingsByOwner(authentication.getName()));
            }
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper methods
    private Map<String, String> getValidationErrors(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage()));
        return errors;
    }

    private void associateImages(HousingListing listing, List<String> s3Keys) {
        if (s3Keys == null || s3Keys.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < s3Keys.size(); i++) {
            ListingImage image = new ListingImage();
            image.setListing(listing);
            image.setS3Key(s3Keys.get(i));
            image.setDisplayOrder(i + 1);
            image.setIsPrimary(i == 0); // First image is primary by default
            
            listingImageRepository.save(image);
        }
    }

    @Transactional
    private void updateImages(HousingListing listing, List<String> s3Keys) {
        // Delete existing images for this listing
        listingImageRepository.deleteByListing(listing);
        
        // Associate new images if provided
        if (s3Keys != null && !s3Keys.isEmpty()) {
            associateImages(listing, s3Keys);
        }
    }

    private boolean filterByBedBath(HousingListing listing, SearchHousingListingRequest request) {
        if (request.getMinBedrooms() != null && listing.getBedrooms() < request.getMinBedrooms()) {
            return false;
        }
        if (request.getMaxBedrooms() != null && listing.getBedrooms() > request.getMaxBedrooms()) {
            return false;
        }
        if (request.getMinBathrooms() != null && listing.getBathrooms() < request.getMinBathrooms()) {
            return false;
        }
        if (request.getMaxBathrooms() != null && listing.getBathrooms() > request.getMaxBathrooms()) {
            return false;
        }
        return true;
    }

    private List<HousingListing> applySorting(List<HousingListing> listings, String sortBy, String sortDirection) {
        return listings.stream()
                .sorted((l1, l2) -> {
                    int comparison = 0;
                    switch (sortBy != null ? sortBy : "createdAt") {
                        case "price":
                            comparison = l1.getPrice().compareTo(l2.getPrice());
                            break;
                        case "bedrooms":
                            comparison = l1.getBedrooms().compareTo(l2.getBedrooms());
                            break;
                        case "bathrooms":
                            comparison = l1.getBathrooms().compareTo(l2.getBathrooms());
                            break;
                        case "city":
                            comparison = l1.getCity().compareTo(l2.getCity());
                            break;
                        default: // createdAt
                            comparison = l1.getCreatedAt().compareTo(l2.getCreatedAt());
                            break;
                    }
                    return "desc".equals(sortDirection) ? -comparison : comparison;
                })
                .collect(Collectors.toList());
    }

    private List<HousingListing> applyPagination(List<HousingListing> listings, Integer page, Integer size) {
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;
        
        int start = pageNum * pageSize;
        int end = Math.min(start + pageSize, listings.size());
        
        if (start >= listings.size()) {
            return List.of();
        }
        
        return listings.subList(start, end);
    }

    private HousingListingResponse convertToResponse(HousingListing listing, String currentUserEmail) {
        HousingListingResponse response = new HousingListingResponse();
        response.setId(listing.getId());
        response.setTitle(listing.getTitle());
        response.setDescription(listing.getDescription());
        response.setPrice(listing.getPrice());
        response.setAddress(listing.getAddress());
        response.setCity(listing.getCity());
        response.setBedrooms(listing.getBedrooms());
        response.setBathrooms(listing.getBathrooms());
        response.setAvailableFrom(listing.getAvailableFrom());
        response.setAvailableTo(listing.getAvailableTo());
        response.setIsActive(listing.getIsActive());
        response.setCreatedAt(listing.getCreatedAt());
        response.setUpdatedAt(listing.getUpdatedAt());

        // Set owner info
        HousingListingResponse.OwnerInfo ownerInfo = new HousingListingResponse.OwnerInfo();
        ownerInfo.setId(listing.getOwner().getId());
        ownerInfo.setFirstName(listing.getOwner().getFirstName());
        ownerInfo.setLastName(listing.getOwner().getLastName());
        ownerInfo.setEmail(maskEmail(listing.getOwner().getEmail()));
        ownerInfo.setUniversityDomain(listing.getOwner().getUniversityDomain());
        response.setOwner(ownerInfo);

        // Set images info
        if (listing.getImages() != null) {
            List<HousingListingResponse.ImageInfo> imageInfos = listing.getImages().stream()
                    .map(image -> {
                        HousingListingResponse.ImageInfo imageInfo = new HousingListingResponse.ImageInfo();
                        imageInfo.setId(image.getId());
                        imageInfo.setS3Key(image.getS3Key());
                        imageInfo.setIsPrimary(image.getIsPrimary());
                        imageInfo.setDisplayOrder(image.getDisplayOrder());
                        try {
                            imageInfo.setImageUrl(s3Service.getSignedImageUrl(image.getS3Key()));
                        } catch (Exception e) {
                            imageInfo.setImageUrl(null);
                        }
                        return imageInfo;
                    })
                    .collect(Collectors.toList());
            response.setImages(imageInfos);
        }

        // Set favorites info
        if (listing.getFavorites() != null) {
            response.setFavoriteCount(listing.getFavorites().size());
            
            if (currentUserEmail != null) {
                response.setIsFavorited(listing.getFavorites().stream()
                        .anyMatch(fav -> fav.getUser().getEmail().equals(currentUserEmail)));
            } else {
                response.setIsFavorited(false);
            }
        } else {
            response.setFavoriteCount(0);
            response.setIsFavorited(false);
        }

        return response;
    }

    private HousingListingSummaryResponse convertToSummaryResponse(HousingListing listing, String currentUserEmail) {
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

        // Set primary image
        if (listing.getImages() != null && !listing.getImages().isEmpty()) {
            Optional<ListingImage> primaryImage = listing.getImages().stream()
                    .filter(ListingImage::getIsPrimary)
                    .findFirst();
            
            if (primaryImage.isEmpty()) {
                primaryImage = listing.getImages().stream().findFirst();
            }
            
            if (primaryImage.isPresent()) {
                try {
                    response.setPrimaryImageUrl(s3Service.getSignedImageUrl(primaryImage.get().getS3Key()));
                } catch (Exception e) {
                    response.setPrimaryImageUrl(null);
                }
            }
        }

        // Set favorites info
        if (listing.getFavorites() != null) {
            response.setFavoriteCount(listing.getFavorites().size());
            
            if (currentUserEmail != null) {
                response.setIsFavorited(listing.getFavorites().stream()
                        .anyMatch(fav -> fav.getUser().getEmail().equals(currentUserEmail)));
            } else {
                response.setIsFavorited(false);
            }
        } else {
            response.setFavoriteCount(0);
            response.setIsFavorited(false);
        }

        return response;
    }

    private String maskEmail(String email) {
        if (email == null || email.length() < 3) {
            return email;
        }
        
        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            return email;
        }
        
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        
        if (username.length() <= 2) {
            return "*".repeat(username.length()) + domain;
        }
        
        return username.charAt(0) + "*".repeat(username.length() - 2) + username.charAt(username.length() - 1) + domain;
    }
}