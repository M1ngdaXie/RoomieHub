package com.campusnest.campusnest_platform.integration;

import com.campusnest.campusnest_platform.enums.ListingStatus;
import com.campusnest.campusnest_platform.enums.ListingType;
import com.campusnest.campusnest_platform.enums.PropertyType;
import com.campusnest.campusnest_platform.models.HousingListing;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.repository.HousingListingRepository;
import com.campusnest.campusnest_platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class HousingListingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HousingListingRepository housingListingRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@stanford.edu");
        testUser.setPassword("hashedpassword");
        testUser.setUniversityDomain("stanford.edu");
        testUser.setEmailVerified(true);
        testUser = userRepository.save(testUser);

        // Create test housing listing
        HousingListing listing = new HousingListing();
        listing.setUser(testUser);
        listing.setTitle("Cozy Studio Near Campus");
        listing.setDescription("A beautiful studio apartment near Stanford campus");
        listing.setListingType(ListingType.OFFER);
        listing.setPropertyType(PropertyType.STUDIO);
        listing.setStatus(ListingStatus.ACTIVE);
        listing.setPricePerMonth(new BigDecimal("1200.00"));
        listing.setCity("Palo Alto");
        listing.setState("CA");
        listing.setZipCode("94301");
        listing.setCountry("USA");
        listing.setBedrooms(0);
        listing.setBathrooms(1);
        listing.setFurnished(true);
        listing.setContactEmail(testUser.getEmail());
        
        housingListingRepository.save(listing);
    }

    @Test
    void getAllListings_ShouldReturnActiveListings() throws Exception {
        mockMvc.perform(get("/api/housing/listings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Cozy Studio Near Campus"))
                .andExpect(jsonPath("$.content[0].listingType").value("OFFER"))
                .andExpect(jsonPath("$.content[0].propertyType").value("STUDIO"))
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));
    }

    @Test
    void getListingsByType_ShouldFilterCorrectly() throws Exception {
        mockMvc.perform(get("/api/housing/listings?type=OFFER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].listingType").value("OFFER"));
    }

    @Test
    void searchListings_ShouldFindRelevantResults() throws Exception {
        mockMvc.perform(get("/api/housing/listings?search=studio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].title").value("Cozy Studio Near Campus"));
    }
}