package com.campusnest.campusnest_platform.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutRequest {
    
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
    
    private DeviceInfo deviceInfo;
    
    private boolean logoutAllDevices = false;
}