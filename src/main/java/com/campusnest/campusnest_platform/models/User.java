package com.campusnest.campusnest_platform.models;

import com.campusnest.campusnest_platform.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Data
@Table(name = "users")
@Getter
@Setter
public class User {
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

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @Column
    private Instant lastLoginAt;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
