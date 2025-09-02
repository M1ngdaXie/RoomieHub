package com.campusnest.campusnest_platform.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerificationResponse {
    
    @JsonProperty("success")
    private Boolean success;
    
    @JsonProperty("message")
    private String message;
    
    @JsonProperty("email")
    private String email;
    
    @JsonProperty("verification_status")
    private String verificationStatus;
    
    @JsonProperty("redirect_url")
    private String redirectUrl;
    
    @JsonProperty("timestamp")
    @Builder.Default
    private Instant timestamp = Instant.now();
}