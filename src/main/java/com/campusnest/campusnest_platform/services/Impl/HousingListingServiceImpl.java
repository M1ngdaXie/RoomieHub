package com.campusnest.campusnest_platform.services.Impl;

import com.campusnest.campusnest_platform.enums.ListingStatus;
import com.campusnest.campusnest_platform.enums.ListingType;
import com.campusnest.campusnest_platform.enums.PropertyType;
import com.campusnest.campusnest_platform.models.HousingListing;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.repository.HousingListingRepository;
import com.campusnest.campusnest_platform.services.HousingListingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HousingListingServiceImpl implements HousingListingService {

    private final HousingListingRepository housingListingRepository;

    @Override
    public HousingListing createListing(HousingListing listing, User user) {
        log.info("Creating new housing listing for user: {}", user.getEmail());
        
        listing.setUser(user);
        listing.setStatus(ListingStatus.ACTIVE);
        
        // Set expiry date to 60 days from now if not provided
        if (listing.getExpiresAt() == null) {
            listing.setExpiresAt(Instant.now().plus(60, ChronoUnit.DAYS));
        }
        
        // Set contact email to user's email if not provided
        if (listing.getContactEmail() == null || listing.getContactEmail().isEmpty()) {
            listing.setContactEmail(user.getEmail());
        }
        
        HousingListing savedListing = housingListingRepository.save(listing);
        log.info("Created housing listing with ID: {}", savedListing.getId());
        
        return savedListing;
    }

    @Override
    public HousingListing updateListing(Long listingId, HousingListing updatedListing, User user) {
        log.info("Updating housing listing {} for user: {}", listingId, user.getEmail());
        
        HousingListing existingListing = housingListingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        
        if (!existingListing.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("User does not have permission to update this listing");
        }
        
        // Update fields
        existingListing.setTitle(updatedListing.getTitle());
        existingListing.setDescription(updatedListing.getDescription());
        existingListing.setPropertyType(updatedListing.getPropertyType());
        existingListing.setPricePerMonth(updatedListing.getPricePerMonth());
        existingListing.setPricePerNight(updatedListing.getPricePerNight());
        existingListing.setAddress(updatedListing.getAddress());
        existingListing.setCity(updatedListing.getCity());
        existingListing.setState(updatedListing.getState());
        existingListing.setZipCode(updatedListing.getZipCode());
        existingListing.setNearbyUniversities(updatedListing.getNearbyUniversities());
        existingListing.setBedrooms(updatedListing.getBedrooms());
        existingListing.setBathrooms(updatedListing.getBathrooms());
        existingListing.setSquareFootage(updatedListing.getSquareFootage());
        existingListing.setFurnished(updatedListing.getFurnished());
        existingListing.setUtilitiesIncluded(updatedListing.getUtilitiesIncluded());
        existingListing.setParkingAvailable(updatedListing.getParkingAvailable());
        existingListing.setPetsAllowed(updatedListing.getPetsAllowed());
        existingListing.setAvailableFrom(updatedListing.getAvailableFrom());
        existingListing.setAvailableUntil(updatedListing.getAvailableUntil());
        existingListing.setMinStayDays(updatedListing.getMinStayDays());
        existingListing.setMaxStayDays(updatedListing.getMaxStayDays());
        existingListing.setAmenities(updatedListing.getAmenities());
        existingListing.setHouseRules(updatedListing.getHouseRules());
        existingListing.setAdditionalInfo(updatedListing.getAdditionalInfo());
        existingListing.setContactEmail(updatedListing.getContactEmail());
        existingListing.setContactPhone(updatedListing.getContactPhone());
        
        return housingListingRepository.save(existingListing);
    }

    @Override
    public void deleteListing(Long listingId, User user) {
        log.info("Deleting housing listing {} for user: {}", listingId, user.getEmail());
        
        HousingListing listing = housingListingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        
        if (!listing.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("User does not have permission to delete this listing");
        }
        
        housingListingRepository.delete(listing);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HousingListing> getListingById(Long listingId) {
        return housingListingRepository.findById(listingId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousingListing> getListingsByUser(User user) {
        return housingListingRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HousingListing> getActiveListings(Pageable pageable) {
        return housingListingRepository.findByStatusOrderByCreatedAtDesc(ListingStatus.ACTIVE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HousingListing> getListingsByType(ListingType listingType, Pageable pageable) {
        return housingListingRepository.findByListingTypeAndStatusOrderByCreatedAtDesc(
                listingType, ListingStatus.ACTIVE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HousingListing> getListingsByPropertyType(PropertyType propertyType, Pageable pageable) {
        return housingListingRepository.findByPropertyTypeAndStatusOrderByCreatedAtDesc(
                propertyType, ListingStatus.ACTIVE, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HousingListing> searchListings(String searchTerm, Pageable pageable) {
        return housingListingRepository.searchListings(ListingStatus.ACTIVE, searchTerm, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HousingListing> findByCityAndUniversity(String city, String universityDomain, Pageable pageable) {
        return housingListingRepository.findByCityAndUniversityDomain(
                ListingStatus.ACTIVE, city, universityDomain, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HousingListing> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        // Convert monthly prices to nightly for comparison
        BigDecimal minPriceNight = minPrice.divide(BigDecimal.valueOf(30), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal maxPriceNight = maxPrice.divide(BigDecimal.valueOf(30), 2, java.math.RoundingMode.HALF_UP);
        
        return housingListingRepository.findByPriceRange(
                ListingStatus.ACTIVE, minPrice, maxPrice, minPriceNight, maxPriceNight, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HousingListing> findAvailableInDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return housingListingRepository.findAvailableInDateRange(
                ListingStatus.ACTIVE, startDate, endDate, pageable);
    }

    @Override
    public void deactivateListing(Long listingId, User user) {
        log.info("Deactivating housing listing {} for user: {}", listingId, user.getEmail());
        
        HousingListing listing = housingListingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        
        if (!listing.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("User does not have permission to modify this listing");
        }
        
        listing.setStatus(ListingStatus.INACTIVE);
        housingListingRepository.save(listing);
    }

    @Override
    public void activateListing(Long listingId, User user) {
        log.info("Activating housing listing {} for user: {}", listingId, user.getEmail());
        
        HousingListing listing = housingListingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        
        if (!listing.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("User does not have permission to modify this listing");
        }
        
        listing.setStatus(ListingStatus.ACTIVE);
        housingListingRepository.save(listing);
    }

    @Override
    public void expireOldListings() {
        log.info("Running scheduled task to expire old listings");
        
        Instant now = Instant.now();
        List<HousingListing> expiredListings = housingListingRepository.findAll()
                .stream()
                .filter(listing -> listing.getStatus() == ListingStatus.ACTIVE)
                .filter(listing -> listing.getExpiresAt() != null && listing.getExpiresAt().isBefore(now))
                .toList();
        
        for (HousingListing listing : expiredListings) {
            listing.setStatus(ListingStatus.EXPIRED);
            housingListingRepository.save(listing);
            log.info("Expired listing with ID: {}", listing.getId());
        }
        
        log.info("Expired {} listings", expiredListings.size());
    }
}