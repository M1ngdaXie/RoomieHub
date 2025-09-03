package com.campusnest.campusnest_platform.controllers;

import com.campusnest.campusnest_platform.enums.ListingType;
import com.campusnest.campusnest_platform.enums.PropertyType;
import com.campusnest.campusnest_platform.models.HousingListing;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.services.HousingListingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HousingController.class)
class HousingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HousingListingService housingListingService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private HousingListing testListing;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@stanford.edu");
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setUniversityDomain("stanford.edu");
        testUser.setEmailVerified(true);

        testListing = new HousingListing();
        testListing.setId(1L);
        testListing.setUser(testUser);
        testListing.setTitle("Cozy Studio Near Campus");
        testListing.setDescription("A beautiful studio apartment near Stanford campus");
        testListing.setListingType(ListingType.OFFER);
        testListing.setPropertyType(PropertyType.STUDIO);
        testListing.setPricePerMonth(new BigDecimal("1200.00"));
        testListing.setCity("Palo Alto");
        testListing.setState("CA");
    }

    @Test
    void getAllListings_ShouldReturnPageOfListings() throws Exception {
        Page<HousingListing> page = new PageImpl<>(Arrays.asList(testListing));
        when(housingListingService.getActiveListings(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/housing/listings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Cozy Studio Near Campus"))
                .andExpect(jsonPath("$.content[0].listingType").value("OFFER"))
                .andExpect(jsonPath("$.content[0].propertyType").value("STUDIO"));
    }

    @Test
    void getListingById_ShouldReturnListing() throws Exception {
        when(housingListingService.getListingById(1L)).thenReturn(Optional.of(testListing));

        mockMvc.perform(get("/api/housing/listings/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Cozy Studio Near Campus"))
                .andExpect(jsonPath("$.city").value("Palo Alto"));
    }

    @Test
    void getListingById_WhenNotFound_ShouldReturn404() throws Exception {
        when(housingListingService.getListingById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/housing/listings/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createListing_ShouldCreateAndReturnListing() throws Exception {
        when(housingListingService.createListing(any(HousingListing.class), any(User.class)))
                .thenReturn(testListing);

        String requestBody = """
                {
                    "title": "Cozy Studio Near Campus",
                    "description": "A beautiful studio apartment near Stanford campus",
                    "listingType": "OFFER",
                    "propertyType": "STUDIO",
                    "pricePerMonth": 1200.00,
                    "city": "Palo Alto",
                    "state": "CA"
                }
                """;

        mockMvc.perform(post("/api/housing/listings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Cozy Studio Near Campus"));
    }
}