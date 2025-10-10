package com.campusnest.campusnest_platform.services;

import com.campusnest.campusnest_platform.models.HousingListing;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.repository.HousingListingRepository;
import com.campusnest.campusnest_platform.repository.ListingImageRepository;
import com.campusnest.campusnest_platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service("housingListingService")
@Transactional
public class HousingListingServiceImpl implements HousingListingService {

    @Autowired
    private HousingListingRepository housingListingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ListingImageRepository listingImageRepository;

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
    @Cacheable(value = "housing-listings", key = "#id") // TEMPORARILY DISABLED DUE TO REDIS SERIALIZATION ISSUES
    public Optional<HousingListing> findById(Long id) {
        // Step 1: Fetch listing with owner (avoiding MultipleBagFetchException)
        HousingListing listing = housingListingRepository.findByIdWithOwner(id);
        if (listing == null) {
            return Optional.empty();
        }
        
        // Step 2: Fetch images and favorites separately (following Baeldung's multiple queries approach)
        List<HousingListing> singleList = List.of(listing);
        housingListingRepository.findWithImages(singleList);
        housingListingRepository.findWithFavorites(singleList);
        
        return Optional.of(listing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousingListing> findAllActive() {
        return housingListingRepository.findByIsActiveTrueOrderByCreatedAtDesc();
    }

    @Override
    @CachePut(value = "housing-listings", key = "#id")
    @CacheEvict(value = "housing-search", allEntries = true)
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
    @CacheEvict(value = {"housing-listings", "housing-search"}, key = "#id", allEntries = true)
    public void deleteListing(Long id, String requesterEmail) {
        verifyOwnershipOrAdmin(id, requesterEmail);
        
        HousingListing listing = housingListingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found: " + id));
        
        // Soft delete - set isActive to false
        listing.setIsActive(false);
        listing.setUpdatedAt(LocalDateTime.now());
        housingListingRepository.save(listing);
        
        // Note: Images are kept for data integrity and potential recovery
        // For hard delete (permanent removal), use hardDeleteListing method
    }
    
    // Optional: Add hard delete method for complete removal
    public void hardDeleteListing(Long id, String requesterEmail) {
        verifyOwnershipOrAdmin(id, requesterEmail);
        
        HousingListing listing = housingListingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found: " + id));
        
        // Delete all associated images first
        listingImageRepository.deleteByListing(listing);
        
        // Hard delete the listing
        housingListingRepository.delete(listing);
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
        // Use optimized database query instead of in-memory filtering
        return housingListingRepository.findActiveByCityContainingIgnoreCase(city);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HousingListing> searchByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        // Use optimized database query instead of in-memory filtering
        return housingListingRepository.findActiveByPriceBetween(minPrice, maxPrice);
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
    @Cacheable(value = "housing-listings", key = "#id")
    public HousingListing getById(Long id){
        return housingListingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found: " + id));
    }

    @Cacheable(value = "housing-search",
            key = "#city + ':' + #minPrice + ':' + #maxPrice + ':' + #pageable.pageNumber")
    public Page<HousingListing> searchListings(String city, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return housingListingRepository.findByCityAndPriceBetween(city, minPrice, maxPrice, pageable);
    }


    @CachePut(value = "housing-listings", key = "#result.id")
    public HousingListing saveOrUpdate(HousingListing listing) {
        return housingListingRepository.save(listing);
    }

    @CacheEvict(value = "housing-listings", key = "#id")
    public void delete(Long id) {
        housingListingRepository.deleteById(id);
        clearSearchCache();
    }
    @CacheEvict(value = "housing-search", allEntries = true)
    public void clearSearchCache() {

    }

}