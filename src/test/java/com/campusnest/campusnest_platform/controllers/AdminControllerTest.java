package com.campusnest.campusnest_platform.controllers;

import com.campusnest.campusnest_platform.enums.UserRole;
import com.campusnest.campusnest_platform.models.User;
import com.campusnest.campusnest_platform.repository.UserRepository;
import com.campusnest.campusnest_platform.response.UserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminController adminController;

    private ObjectMapper objectMapper;
    private User adminUser;
    private User studentUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        objectMapper = new ObjectMapper();
        
        adminUser = createAdminUser();
        studentUser = createStudentUser();
    }

    private User createAdminUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("admin@university.edu");
        user.setFirstName("Admin");
        user.setLastName("User");
        user.setRole(UserRole.ADMIN);
        user.setActive(true);
        user.setEmailVerified(true);
        user.setCreatedAt(Instant.now());
        return user;
    }

    private User createStudentUser() {
        User user = new User();
        user.setId(2L);
        user.setEmail("student@university.edu");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(UserRole.STUDENT);
        user.setActive(true);
        user.setEmailVerified(true);
        user.setCreatedAt(Instant.now());
        return user;
    }

    // ========== ROLE-BASED PERMISSION TESTS ==========

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_WithAdminRole_ReturnsOk() throws Exception {
        List<User> users = Arrays.asList(adminUser, studentUser);
        
        when(userRepository.findAll()).thenReturn(users);

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.users").isArray())
                .andExpect(jsonPath("$.users.length()").value(2))
                .andExpect(jsonPath("$.total_count").value(2));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getAllUsers_WithStudentRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_WithAdminRole_ReturnsOk() throws Exception {
        when(userRepository.findById(2L)).thenReturn(Optional.of(studentUser));

        mockMvc.perform(get("/api/admin/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.user.id").value("2"))
                .andExpect(jsonPath("$.user.email").value("s***@university.edu"))
                .andExpect(jsonPath("$.user.first_name").value("John"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getUserById_WithStudentRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/users/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_UserNotFound_ReturnsNotFound() throws Exception {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/admin/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserRole_WithAdminRole_ReturnsOk() throws Exception {
        when(userRepository.findById(2L)).thenReturn(Optional.of(studentUser));
        when(userRepository.save(any(User.class))).thenReturn(studentUser);

        Map<String, String> roleUpdate = Map.of("role", "ADMIN");

        mockMvc.perform(put("/api/admin/users/2/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User role updated successfully"))
                .andExpect(jsonPath("$.user").exists());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void updateUserRole_WithStudentRole_ReturnsForbidden() throws Exception {
        Map<String, String> roleUpdate = Map.of("role", "ADMIN");

        mockMvc.perform(put("/api/admin/users/2/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleUpdate)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserRole_InvalidRole_ReturnsBadRequest() throws Exception {
        when(userRepository.findById(2L)).thenReturn(Optional.of(studentUser));

        Map<String, String> invalidRoleUpdate = Map.of("role", "INVALID_ROLE");

        mockMvc.perform(put("/api/admin/users/2/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRoleUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid role: INVALID_ROLE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserRole_UserNotFound_ReturnsNotFound() throws Exception {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Map<String, String> roleUpdate = Map.of("role", "ADMIN");

        mockMvc.perform(put("/api/admin/users/999/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleUpdate)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserStatus_WithAdminRole_ReturnsOk() throws Exception {
        when(userRepository.findById(2L)).thenReturn(Optional.of(studentUser));
        when(userRepository.save(any(User.class))).thenReturn(studentUser);

        Map<String, Boolean> statusUpdate = Map.of("active", false);

        mockMvc.perform(put("/api/admin/users/2/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User status updated successfully"))
                .andExpect(jsonPath("$.user").exists());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void updateUserStatus_WithStudentRole_ReturnsForbidden() throws Exception {
        Map<String, Boolean> statusUpdate = Map.of("active", false);

        mockMvc.perform(put("/api/admin/users/2/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserStatus_UserNotFound_ReturnsNotFound() throws Exception {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        Map<String, Boolean> statusUpdate = Map.of("active", false);

        mockMvc.perform(put("/api/admin/users/999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSystemStats_WithAdminRole_ReturnsOk() throws Exception {
        when(userRepository.count()).thenReturn(10L);
        when(userRepository.countByActiveTrue()).thenReturn(8L);
        when(userRepository.countByEmailVerifiedTrue()).thenReturn(9L);

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.stats.total_users").value(10))
                .andExpect(jsonPath("$.stats.active_users").value(8))
                .andExpect(jsonPath("$.stats.verified_users").value(9))
                .andExpect(jsonPath("$.stats.inactive_users").value(2))
                .andExpect(jsonPath("$.stats.unverified_users").value(1));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void getSystemStats_WithStudentRole_ReturnsForbidden() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getSystemStats_WithoutAuthentication_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isUnauthorized());
    }

    // ========== EDGE CASE TESTS ==========

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserRole_ValidRoleValues_ReturnsOk() throws Exception {
        when(userRepository.findById(2L)).thenReturn(Optional.of(studentUser));
        when(userRepository.save(any(User.class))).thenReturn(studentUser);

        // Test STUDENT role
        Map<String, String> studentRoleUpdate = Map.of("role", "STUDENT");
        mockMvc.perform(put("/api/admin/users/2/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentRoleUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Test ADMIN role
        Map<String, String> adminRoleUpdate = Map.of("role", "ADMIN");
        mockMvc.perform(put("/api/admin/users/2/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminRoleUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserRole_CaseInsensitive_ReturnsOk() throws Exception {
        when(userRepository.findById(2L)).thenReturn(Optional.of(studentUser));
        when(userRepository.save(any(User.class))).thenReturn(studentUser);

        // Test lowercase role
        Map<String, String> lowercaseRoleUpdate = Map.of("role", "admin");
        mockMvc.perform(put("/api/admin/users/2/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(lowercaseRoleUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserStatus_BooleanValues_ReturnsOk() throws Exception {
        when(userRepository.findById(2L)).thenReturn(Optional.of(studentUser));
        when(userRepository.save(any(User.class))).thenReturn(studentUser);

        // Test setting to inactive
        Map<String, Boolean> inactiveUpdate = Map.of("active", false);
        mockMvc.perform(put("/api/admin/users/2/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inactiveUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // Test setting to active
        Map<String, Boolean> activeUpdate = Map.of("active", true);
        mockMvc.perform(put("/api/admin/users/2/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(activeUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    // ========== USER ROLE VALIDATION TESTS ==========

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminEndpoints_RequireAdminRole_Success() throws Exception {
        // This test verifies that all admin endpoints require ADMIN role
        // and that @PreAuthorize("hasRole('ADMIN')") annotation is working
        
        when(userRepository.findAll()).thenReturn(Arrays.asList(adminUser, studentUser));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(studentUser));
        when(userRepository.save(any(User.class))).thenReturn(studentUser);
        when(userRepository.count()).thenReturn(2L);
        when(userRepository.countByActiveTrue()).thenReturn(2L);
        when(userRepository.countByEmailVerifiedTrue()).thenReturn(2L);

        // Test all admin endpoints work with ADMIN role
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/users/1"))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/admin/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("role", "STUDENT"))))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/admin/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("active", true))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    void adminEndpoints_DenyStudentRole_ReturnsForbidden() throws Exception {
        // This test verifies that STUDENT role is denied access to all admin endpoints
        
        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/users/1"))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/admin/users/1/role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("role", "ADMIN"))))
                .andExpect(status().isForbidden());

        mockMvc.perform(put("/api/admin/users/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("active", false))))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/stats"))
                .andExpect(status().isForbidden());
    }
}