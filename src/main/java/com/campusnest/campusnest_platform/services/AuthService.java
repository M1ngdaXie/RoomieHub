package com.campusnest.campusnest_platform.services;

import com.campusnest.campusnest_platform.requests.LoginRequest;
import com.campusnest.campusnest_platform.requests.LogoutRequest;
import com.campusnest.campusnest_platform.requests.RefreshTokenRequest;
import com.campusnest.campusnest_platform.response.LoginResponse;
import com.campusnest.campusnest_platform.requests.RegisterRequest;
import com.campusnest.campusnest_platform.response.LogoutResponse;
import com.campusnest.campusnest_platform.response.RefreshTokenResponse;
import com.campusnest.campusnest_platform.response.RegisterResponse;

public interface AuthService {

    RegisterResponse registerUser(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    RefreshTokenResponse refreshAccessToken(RefreshTokenRequest request);
    
    LogoutResponse logout(LogoutRequest request);
}
