package com.campusnest.campusnest_platform.repository;

import com.campusnest.campusnest_platform.models.HousingListing;
import com.campusnest.campusnest_platform.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface HousingListingRepository extends JpaRepository<HousingListing, Long> {
    List<HousingListing> findByIsActiveTrueOrderByCreatedAtDesc();
    
    List<HousingListing> findByOwnerAndIsActiveTrueOrderByCreatedAtDesc(User owner);
    
    List<HousingListing> findByOwnerOrderByCreatedAtDesc(User owner);
    
    @Query("SELECT h FROM HousingListing h WHERE h.isActive = true " +
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
    
    long countByIsActiveTrue();
    
    long countByOwner(User owner);
}