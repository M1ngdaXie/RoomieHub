package com.campusnest.campusnest_platform.repository;

import com.campusnest.campusnest_platform.enums.ListingStatus;
import com.campusnest.campusnest_platform.enums.ListingType;
import com.campusnest.campusnest_platform.enums.PropertyType;
import com.campusnest.campusnest_platform.models.HousingListing;
import com.campusnest.campusnest_platform.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface HousingListingRepository extends JpaRepository<HousingListing, Long> {
    
    // Find listings by user
    List<HousingListing> findByUserOrderByCreatedAtDesc(User user);
    
    // Find active listings
    Page<HousingListing> findByStatusOrderByCreatedAtDesc(ListingStatus status, Pageable pageable);
    
    // Find by listing type
    Page<HousingListing> findByListingTypeAndStatusOrderByCreatedAtDesc(
            ListingType listingType, ListingStatus status, Pageable pageable);
    
    // Find by property type
    Page<HousingListing> findByPropertyTypeAndStatusOrderByCreatedAtDesc(
            PropertyType propertyType, ListingStatus status, Pageable pageable);
    
    // Find by city and university domain
    @Query("SELECT hl FROM HousingListing hl WHERE hl.status = :status " +
           "AND hl.city = :city " +
           "AND hl.user.universityDomain = :universityDomain " +
           "ORDER BY hl.createdAt DESC")
    Page<HousingListing> findByCityAndUniversityDomain(
            @Param("status") ListingStatus status,
            @Param("city") String city,
            @Param("universityDomain") String universityDomain,
            Pageable pageable);
    
    // Find by price range
    @Query("SELECT hl FROM HousingListing hl WHERE hl.status = :status " +
           "AND (hl.pricePerMonth BETWEEN :minPrice AND :maxPrice " +
           "OR hl.pricePerNight BETWEEN :minPriceNight AND :maxPriceNight) " +
           "ORDER BY hl.createdAt DESC")
    Page<HousingListing> findByPriceRange(
            @Param("status") ListingStatus status,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minPriceNight") BigDecimal minPriceNight,
            @Param("maxPriceNight") BigDecimal maxPriceNight,
            Pageable pageable);
    
    // Find available within date range
    @Query("SELECT hl FROM HousingListing hl WHERE hl.status = :status " +
           "AND hl.availableFrom <= :endDate " +
           "AND (hl.availableUntil IS NULL OR hl.availableUntil >= :startDate) " +
           "ORDER BY hl.createdAt DESC")
    Page<HousingListing> findAvailableInDateRange(
            @Param("status") ListingStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
    
    // Search listings
    @Query("SELECT hl FROM HousingListing hl WHERE hl.status = :status " +
           "AND (LOWER(hl.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(hl.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(hl.address) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(hl.city) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY hl.createdAt DESC")
    Page<HousingListing> searchListings(
            @Param("status") ListingStatus status,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);
}