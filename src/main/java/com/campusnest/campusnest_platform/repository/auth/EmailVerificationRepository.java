package com.campusnest.campusnest_platform.repository.auth;

import com.campusnest.campusnest_platform.models.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    void deleteByEmail(String email);

    Optional<EmailVerification> findByToken(String token);

}
