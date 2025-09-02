package com.campusnest.campusnest_platform.repository;

import com.campusnest.campusnest_platform.models.RefreshToken;
import com.campusnest.campusnest_platform.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUserAndDeviceId(User user, String deviceId);
    void deleteByUser(User user); // For logout all devices
}
