package com.campusnest.campusnest_platform.repository;

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
    
    // Step 1: Fetch listings with owner only (avoid MultipleBagFetchException)
    @Query("SELECT h FROM HousingListing h " +
           "LEFT JOIN FETCH h.owner o " +
           "WHERE h.isActive = true " +
           "ORDER BY h.createdAt DESC")
    List<HousingListing> findByIsActiveTrueOrderByCreatedAtDesc();
    
    @Query("SELECT h FROM HousingListing h " +
           "LEFT JOIN FETCH h.owner o " +
           "WHERE h.owner = :owner AND h.isActive = true " +
           "ORDER BY h.createdAt DESC")
    List<HousingListing> findByOwnerAndIsActiveTrueOrderByCreatedAtDesc(@Param("owner") User owner);
    
    @Query("SELECT h FROM HousingListing h " +
           "LEFT JOIN FETCH h.owner o " +
           "WHERE h.owner = :owner " +
           "ORDER BY h.createdAt DESC")
    List<HousingListing> findByOwnerOrderByCreatedAtDesc(@Param("owner") User owner);
    
    // Step 2: Separate queries for collections to avoid MultipleBagFetchException
    @Query("SELECT DISTINCT h FROM HousingListing h " +
           "LEFT JOIN FETCH h.images " +
           "WHERE h IN :listings")
    List<HousingListing> findWithImages(@Param("listings") List<HousingListing> listings);
    
    @Query("SELECT DISTINCT h FROM HousingListing h " +
           "LEFT JOIN FETCH h.favorites " +
           "WHERE h IN :listings")
    List<HousingListing> findWithFavorites(@Param("listings") List<HousingListing> listings);
    
    @Query("SELECT h FROM HousingListing h " +
           "LEFT JOIN FETCH h.owner o " +
           "WHERE h.isActive = true " +
           "AND h.city LIKE %:city% " +
           "AND h.price BETWEEN :minPrice AND :maxPrice " +
           "AND h.availableFrom <= :availableTo " +
           "AND h.availableTo >= :availableFrom " +
           "ORDER BY h.createdAt DESC")
    List<HousingListing> findBySearchCriteria(
        @Param("city") String city,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        @Param("availableFrom") LocalDate availableFrom,
        @Param("availableTo") LocalDate availableTo
    );
    
    // Single entity fetch - fetch owner first, then collections separately
    @Query("SELECT h FROM HousingListing h " +
           "LEFT JOIN FETCH h.owner o " +
           "WHERE h.id = :id")
    HousingListing findByIdWithOwner(@Param("id") Long id);
    
    long countByIsActiveTrue();
    
    long countByOwner(User owner);
    
    // Method for paginated search by city and price range (fixed)
    @Query("SELECT h FROM HousingListing h " +
           "LEFT JOIN FETCH h.owner o " +
           "WHERE h.isActive = true " +
           "AND (:city IS NULL OR h.city LIKE %:city%) " +
           "AND (:minPrice IS NULL OR h.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR h.price <= :maxPrice) " +
           "ORDER BY h.createdAt DESC")
    Page<HousingListing> findByCityAndPriceBetween(
        @Param("city") String city,
        @Param("minPrice") BigDecimal minPrice,
        @Param("maxPrice") BigDecimal maxPrice,
        Pageable pageable
    );
}