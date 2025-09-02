package com.campusnest.campusnest_platform.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @JsonProperty("remember_me")
    private Boolean rememberMe = false; // For longer refresh token expiry

    @JsonProperty("device_info")
    private DeviceInfo deviceInfo; // Optional: for security tracking
}
