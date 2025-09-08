package com.campusnest.campusnest_platform.services;

import com.campusnest.campusnest_platform.requests.ForgotPasswordRequest;
import com.campusnest.campusnest_platform.requests.LoginRequest;
import com.campusnest.campusnest_platform.requests.LogoutRequest;
import com.campusnest.campusnest_platform.requests.RefreshTokenRequest;
import com.campusnest.campusnest_platform.requests.ResetPasswordRequest;
import com.campusnest.campusnest_platform.response.ForgotPasswordResponse;
import com.campusnest.campusnest_platform.response.LoginResponse;
import com.campusnest.campusnest_platform.requests.RegisterRequest;
import com.campusnest.campusnest_platform.response.LogoutResponse;
import com.campusnest.campusnest_platform.response.RefreshTokenResponse;
import com.campusnest.campusnest_platform.response.RegisterResponse;
import com.campusnest.campusnest_platform.response.ResetPasswordResponse;

public interface AuthService {

    RegisterResponse registerUser(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    RefreshTokenResponse refreshAccessToken(RefreshTokenRequest request);
    
    LogoutResponse logout(LogoutRequest request);
    
    ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request, String ipAddress, String userAgent);
    
    ResetPasswordResponse resetPassword(ResetPasswordRequest request);
}
