package com.campusnest.campusnest_platform.repository.user;

import com.campusnest.campusnest_platform.models.HousingListing;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.models.UserFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {
    List<UserFavorite> findByUserOrderByCreatedAtDesc(User user);
    
    Optional<UserFavorite> findByUserAndListing(User user, HousingListing listing);
    
    boolean existsByUserAndListing(User user, HousingListing listing);
    
    void deleteByUserAndListing(User user, HousingListing listing);
    
    long countByListing(HousingListing listing);
}