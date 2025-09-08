package com.campusnest.campusnest_platform.models;

import com.campusnest.campusnest_platform.enums.UserRole;
import com.campusnest.campusnest_platform.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String universityDomain; // Extract from email: "stanford.edu"

    @Column(nullable = false)
    private Boolean emailVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING_EMAIL;

    @Column(nullable = false)
    private Boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.STUDENT;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Column
    private Instant lastLoginAt;
    
    // Additional fields for UserDetails account management
    @Column(nullable = false)
    private Boolean accountLocked = false;
    
    @Column
    private Instant passwordExpiresAt;
    
    @Column
    private Instant accountExpiresAt;

    // ========== UserDetails Implementation ==========
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + role.name())
        );
    }

    @Override
    public String getUsername() {
        return email; // Email is the username
    }

    @Override
    public String getPassword() {
        return password; // Return the encrypted password
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountExpiresAt == null || accountExpiresAt.isAfter(Instant.now());
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return passwordExpiresAt == null || passwordExpiresAt.isAfter(Instant.now());
    }

    @Override
    public boolean isEnabled() {
        return active && emailVerified;
    }

    // ========== Utility Methods ==========

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public void updateLastLogin() {
        this.lastLoginAt = Instant.now();
    }
    
    public void lockAccount() {
        this.accountLocked = true;
    }
    
    public void unlockAccount() {
        this.accountLocked = false;
    }
    
    public void setPasswordExpirationDate(Instant expirationDate) {
        this.passwordExpiresAt = expirationDate;
    }
    
    public void setAccountExpirationDate(Instant expirationDate) {
        this.accountExpiresAt = expirationDate;
    }
    
    public boolean isAdmin() {
        return role != null && role == UserRole.ADMIN;
    }
    
    public boolean isStudent() {
        return role != null && role == UserRole.STUDENT;
    }
}
