package com.campusnest.campusnest_platform.models;

import com.campusnest.campusnest_platform.enums.ListingStatus;
import com.campusnest.campusnest_platform.enums.ListingType;
import com.campusnest.campusnest_platform.enums.PropertyType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "housing_listings")
@Getter
@Setter
public class HousingListing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingType listingType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PropertyType propertyType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ListingStatus status = ListingStatus.ACTIVE;

    @Column(precision = 10, scale = 2)
    private BigDecimal pricePerMonth;

    @Column(precision = 10, scale = 2)
    private BigDecimal pricePerNight;

    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country = "USA";

    @Column(columnDefinition = "TEXT")
    private String nearbyUniversities;

    private Integer bedrooms;
    private Integer bathrooms;
    private Integer squareFootage;

    private Boolean furnished;
    private Boolean utilitiesIncluded;
    private Boolean parkingAvailable;
    private Boolean petsAllowed;

    private LocalDate availableFrom;
    private LocalDate availableUntil;

    private Integer minStayDays;
    private Integer maxStayDays;

    @Column(columnDefinition = "TEXT")
    private String amenities;

    @Column(columnDefinition = "TEXT")
    private String houseRules;

    @Column(columnDefinition = "TEXT")
    private String additionalInfo;

    private String contactEmail;
    private String contactPhone;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    private Instant expiresAt;
}