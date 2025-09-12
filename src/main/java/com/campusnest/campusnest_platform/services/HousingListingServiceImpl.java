package com.campusnest.campusnest_platform.services;

import com.campusnest.campusnest_platform.models.HousingListing;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.repository.HousingListingRepository;
import com.campusnest.campusnest_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HousingListingServiceImpl implements HousingListingService {

    @Autowired
    private HousingListingRepository housingListingRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public HousingListing createListing(HousingListing listing, String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + ownerEmail));
        
        listing.setOwner(owner);
        listing.setIsActive(true);
        listing.setCreatedAt(LocalDateTime.now());
        listing.setUpdatedAt(LocalDateTime.now());
        
        return housingListingRepository.save(listing);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HousingListing> findById(Long id) {
        return housingListingRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousingListing> findAllActive() {
        return housingListingRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    @Override
    public HousingListing updateListing(Long id, HousingListing updatedListing, String requesterEmail) {
        verifyOwnershipOrAdmin(id, requesterEmail);
        
        HousingListing existingListing = housingListingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found: " + id));
        
        // Update fields
        existingListing.setTitle(updatedListing.getTitle());
        existingListing.setDescription(updatedListing.getDescription());
        existingListing.setPrice(updatedListing.getPrice());
        existingListing.setAddress(updatedListing.getAddress());
        existingListing.setCity(updatedListing.getCity());
        existingListing.setBedrooms(updatedListing.getBedrooms());
        existingListing.setBathrooms(updatedListing.getBathrooms());
        existingListing.setAvailableFrom(updatedListing.getAvailableFrom());
        existingListing.setAvailableTo(updatedListing.getAvailableTo());
        existingListing.setUpdatedAt(LocalDateTime.now());
        
        return housingListingRepository.save(existingListing);
    }

    @Override
    public void deleteListing(Long id, String requesterEmail) {
        verifyOwnershipOrAdmin(id, requesterEmail);
        
        HousingListing listing = housingListingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found: " + id));
        
        // Soft delete - set isActive to false
        listing.setIsActive(false);
        listing.setUpdatedAt(LocalDateTime.now());
        housingListingRepository.save(listing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousingListing> findByOwner(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + ownerEmail));
        
        return housingListingRepository.findByOwnerOrderByCreatedAtDesc(owner);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousingListing> findActiveByOwner(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + ownerEmail));
        
        return housingListingRepository.findByOwnerAndIsActiveTrueOrderByCreatedAtDesc(owner);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousingListing> searchListings(String city, BigDecimal minPrice, BigDecimal maxPrice,
                                              LocalDate availableFrom, LocalDate availableTo) {
        return housingListingRepository.findBySearchCriteria(city, minPrice, maxPrice, availableFrom, availableTo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousingListing> searchByCity(String city) {
        return housingListingRepository.findByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .filter(listing -> listing.getCity().toLowerCase().contains(city.toLowerCase()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousingListing> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return housingListingRepository.findByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .filter(listing -> listing.getPrice().compareTo(minPrice) >= 0 && 
                                 listing.getPrice().compareTo(maxPrice) <= 0)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOwnerOrAdmin(Long listingId, String requesterEmail) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElse(null);
        
        if (requester == null) {
            return false;
        }
        
        // Admin can access everything
        if (requester.isAdmin()) {
            return true;
        }
        
        // Check ownership
        return isOwner(listingId, requesterEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOwner(Long listingId, String requesterEmail) {
        Optional<HousingListing> listing = housingListingRepository.findById(listingId);
        
        return listing.isPresent() && 
               listing.get().getOwner().getEmail().equals(requesterEmail);
    }

    @Override
    @Transactional(readOnly = true)
    public void verifyOwnershipOrAdmin(Long listingId, String requesterEmail) {
        if (!isOwnerOrAdmin(listingId, requesterEmail)) {
            throw new RuntimeException("Access denied. User is not owner or admin for listing: " + listingId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousingListing> findAll() {
        return housingListingRepository.findAll();
    }

    @Override
    public HousingListing toggleListingStatus(Long id, String adminEmail) {
        User admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + adminEmail));
        
        if (!admin.isAdmin()) {
            throw new RuntimeException("Access denied. Admin role required.");
        }
        
        HousingListing listing = housingListingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found: " + id));
        
        listing.setIsActive(!listing.getIsActive());
        listing.setUpdatedAt(LocalDateTime.now());
        
        return housingListingRepository.save(listing);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalActiveListings() {
        return housingListingRepository.countByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalListingsByOwner(String ownerEmail) {
        User owner = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + ownerEmail));
        
        return housingListingRepository.countByOwner(owner);
    }
}