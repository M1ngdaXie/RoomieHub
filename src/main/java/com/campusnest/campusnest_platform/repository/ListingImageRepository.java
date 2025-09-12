package com.campusnest.campusnest_platform.repository;

import com.campusnest.campusnest_platform.models.HousingListing;
import com.campusnest.campusnest_platform.models.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListingImageRepository extends JpaRepository<ListingImage, Long> {
    List<ListingImage> findByListingOrderByDisplayOrder(HousingListing listing);
    
    Optional<ListingImage> findByListingAndIsPrimaryTrue(HousingListing listing);
    
    void deleteByListing(HousingListing listing);
}