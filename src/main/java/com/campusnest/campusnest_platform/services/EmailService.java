package com.campusnest.campusnest_platform.services;


public interface EmailService {
    void sendVerificationEmail(String email, String verificationUrl);
}
