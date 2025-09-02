package com.campusnest.campusnest_platform.services;

import com.campusnest.campusnest_platform.models.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


public interface EmailVerificationService {


    void sendVerificationEmail(String email);

    User verifyToken(String token);
}
