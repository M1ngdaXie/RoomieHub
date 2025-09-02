package com.campusnest.campusnest_platform.services;

import com.campusnest.campusnest_platform.requests.LoginRequest;
import com.campusnest.campusnest_platform.response.LoginResponse;
import com.campusnest.campusnest_platform.requests.RegisterRequest;
import com.campusnest.campusnest_platform.response.RegisterResponse;

public interface AuthService {

    RegisterResponse registerUser(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
