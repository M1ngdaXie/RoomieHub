package com.campusnest.campusnest_platform.services;


import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.campusnest.campusnest_platform.models.RefreshToken;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.repository.RefreshTokenRepository;
import com.campusnest.campusnest_platform.requests.DeviceInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtTokenService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-token.expiration}") // 15 minutes default
    private int accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration:604800}") // 7 days default
    private int refreshTokenExpiration;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("university_domain", user.getUniversityDomain());
        claims.put("verification_status", user.getVerificationStatus().name());
        claims.put("email_verified", user.getEmailVerified());

        return JWT.create()
                .withSubject(user.getId().toString())
                .withClaim("claims", claims)
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(Date.from(Instant.now().plusSeconds(accessTokenExpiration)))
                .withIssuer("campusnest-platform")
                .sign(Algorithm.HMAC256(jwtSecret));
    }
    public String generateRefreshToken(User user, DeviceInfo deviceInfo) {
        // Delete existing refresh tokens for this user/device combo
        refreshTokenRepository.deleteByUserAndDeviceId(user,
                deviceInfo != null ? deviceInfo.getDeviceId() : null);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(refreshTokenExpiration));
        refreshToken.setDeviceId(deviceInfo != null ? deviceInfo.getDeviceId() : null);
        refreshToken.setDeviceType(deviceInfo != null ? deviceInfo.getDeviceType() : "Unknown");

        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        return saved.getToken();
    }

    public boolean validateAccessToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer("campusnest-platform")
                    .build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
    public String getUserIdFromToken(String token) {
        validateAccessToken(token);
        return JWT.decode(token).getSubject();
    }

    public Long getAccessTokenExpiration() {
        return (long) accessTokenExpiration;
    }

}
