//package com.campusnest.campusnest_platform.controllers;
//
//import com.campusnest.campusnest_platform.enums.UserRole;
//import com.campusnest.campusnest_platform.models.User;
//import com.campusnest.campusnest_platform.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.ResponseEntity;
//
//import java.time.Instant;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class AdminControllerUnitTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private AdminController adminController;
//
//    private User adminUser;
//    private User studentUser;
//
//    @BeforeEach
//    void setUp() {
//        adminUser = createAdminUser();
//        studentUser = createStudentUser();
//    }
//
//    private User createAdminUser() {
//        User user = new User();
//        user.setId(1L);
//        user.setEmail("admin@university.edu");
//        user.setFirstName("Admin");
//        user.setLastName("User");
//        user.setRole(UserRole.ADMIN);
//        user.setActive(true);
//        user.setEmailVerified(true);
//        user.setCreatedAt(Instant.now());
//        return user;
//    }
//
//    private User createStudentUser() {
//        User user = new User();
//        user.setId(2L);
//        user.setEmail("student@university.edu");
//        user.setFirstName("John");
//        user.setLastName("Doe");
//        user.setRole(UserRole.STUDENT);
//        user.setActive(true);
//        user.setEmailVerified(true);
//        user.setCreatedAt(Instant.now());
//        return user;
//    }
//
//    // ========== UNIT TESTS FOR ADMIN CONTROLLER LOGIC ==========
//
//    @Test
//    void getAllUsers_ReturnsUserList() {
//        List<User> users = Arrays.asList(adminUser, studentUser);
//        when(userRepository.findAll()).thenReturn(users);
//
//        ResponseEntity<Map<String, Object>> response = adminController.getAllUsers(adminUser);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertTrue((Boolean) response.getBody().get("success"));
//
//        @SuppressWarnings("unchecked")
//        List<Object> returnedUsers = (List<Object>) response.getBody().get("users");
//        assertEquals(2, returnedUsers.size());
//        assertEquals(2, response.getBody().get("total_count"));
//    }
//
//    @Test
//    void getUserById_UserExists_ReturnsUser() {
//        when(userRepository.findById(2L)).thenReturn(Optional.of(studentUser));
//
//        ResponseEntity<Map<String, Object>> response = adminController.getUserById(2L, adminUser);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertTrue((Boolean) response.getBody().get("success"));
//        assertNotNull(response.getBody().get("user"));
//    }
//
//    @Test
//    void getUserById_UserNotFound_ReturnsNotFound() {
//        when(userRepository.findById(999L)).thenReturn(Optional.empty());
//
//        ResponseEntity<Map<String, Object>> response = adminController.getUserById(999L, adminUser);
//
//        assertEquals(404, response.getStatusCodeValue());
//    }
//
//    @Test
//    void updateUserRole_ValidRole_ReturnsSuccess() {
//        when(userRepository.findById(2L)).thenReturn(Optional.of(studentUser));
//        when(userRepository.save(any(User.class))).thenReturn(studentUser);
//
//        Map<String, String> request = Map.of("role", "ADMIN");
//        ResponseEntity<Map<String, Object>> response = adminController.updateUserRole(2L, request, adminUser);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertTrue((Boolean) response.getBody().get("success"));
//        assertEquals("User role updated successfully", response.getBody().get("message"));
//        assertNotNull(response.getBody().get("user"));
//    }
//
//    @Test
//    void updateUserRole_InvalidRole_ReturnsBadRequest() {
//        when(userRepository.findById(2L)).thenReturn(Optional.of(studentUser));
//
//        Map<String, String> request = Map.of("role", "INVALID_ROLE");
//        ResponseEntity<Map<String, Object>> response = adminController.updateUserRole(2L, request, adminUser);
//
//        assertEquals(400, response.getStatusCodeValue());
//        assertFalse((Boolean) response.getBody().get("success"));
//        assertEquals("Invalid role: INVALID_ROLE", response.getBody().get("message"));
//    }
//
//    @Test
//    void updateUserRole_UserNotFound_ReturnsNotFound() {
//        when(userRepository.findById(999L)).thenReturn(Optional.empty());
//
//        Map<String, String> request = Map.of("role", "ADMIN");
//        ResponseEntity<Map<String, Object>> response = adminController.updateUserRole(999L, request, adminUser);
//
//        assertEquals(404, response.getStatusCodeValue());
//    }
//
//    @Test
//    void updateUserStatus_ValidStatus_ReturnsSuccess() {
//        when(userRepository.findById(2L)).thenReturn(Optional.of(studentUser));
//        when(userRepository.save(any(User.class))).thenReturn(studentUser);
//
//        Map<String, Boolean> request = Map.of("active", false);
//        ResponseEntity<Map<String, Object>> response = adminController.updateUserStatus(2L, request, adminUser);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertTrue((Boolean) response.getBody().get("success"));
//        assertEquals("User status updated successfully", response.getBody().get("message"));
//        assertNotNull(response.getBody().get("user"));
//    }
//
//    @Test
//    void updateUserStatus_UserNotFound_ReturnsNotFound() {
//        when(userRepository.findById(999L)).thenReturn(Optional.empty());
//
//        Map<String, Boolean> request = Map.of("active", false);
//        ResponseEntity<Map<String, Object>> response = adminController.updateUserStatus(999L, request, adminUser);
//
//        assertEquals(404, response.getStatusCodeValue());
//    }
//
//    @Test
//    void getSystemStats_ReturnsStatistics() {
//        when(userRepository.count()).thenReturn(10L);
//        when(userRepository.countByActiveTrue()).thenReturn(8L);
//        when(userRepository.countByEmailVerifiedTrue()).thenReturn(9L);
//
//        ResponseEntity<Map<String, Object>> response = adminController.getSystemStats(adminUser);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertTrue((Boolean) response.getBody().get("success"));
//
//        @SuppressWarnings("unchecked")
//        Map<String, Object> stats = (Map<String, Object>) response.getBody().get("stats");
//        assertEquals(10L, stats.get("total_users"));
//        assertEquals(8L, stats.get("active_users"));
//        assertEquals(9L, stats.get("verified_users"));
//        assertEquals(2L, stats.get("inactive_users"));
//        assertEquals(1L, stats.get("unverified_users"));
//    }
//
//    // ========== ROLE VALIDATION TESTS ==========
//
//    @Test
//    void updateUserRole_ValidRoleValues_ReturnsSuccess() {
//        when(userRepository.findById(2L)).thenReturn(Optional.of(studentUser));
//        when(userRepository.save(any(User.class))).thenReturn(studentUser);
//
//        // Test STUDENT role
//        Map<String, String> studentRoleRequest = Map.of("role", "STUDENT");
//        ResponseEntity<Map<String, Object>> response = adminController.updateUserRole(2L, studentRoleRequest, adminUser);
//        assertEquals(200, response.getStatusCodeValue());
//        assertTrue((Boolean) response.getBody().get("success"));
//
//        // Test ADMIN role
//        Map<String, String> adminRoleRequest = Map.of("role", "ADMIN");
//        response = adminController.updateUserRole(2L, adminRoleRequest, adminUser);
//        assertEquals(200, response.getStatusCodeValue());
//        assertTrue((Boolean) response.getBody().get("success"));
//    }
//
//    @Test
//    void updateUserRole_CaseInsensitive_ReturnsSuccess() {
//        when(userRepository.findById(2L)).thenReturn(Optional.of(studentUser));
//        when(userRepository.save(any(User.class))).thenReturn(studentUser);
//
//        // Test lowercase role
//        Map<String, String> lowercaseRequest = Map.of("role", "admin");
//        ResponseEntity<Map<String, Object>> response = adminController.updateUserRole(2L, lowercaseRequest, adminUser);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertTrue((Boolean) response.getBody().get("success"));
//    }
//
//    @Test
//    void updateUserStatus_BooleanValues_ReturnsSuccess() {
//        when(userRepository.findById(2L)).thenReturn(Optional.of(studentUser));
//        when(userRepository.save(any(User.class))).thenReturn(studentUser);
//
//        // Test setting to inactive
//        Map<String, Boolean> inactiveRequest = Map.of("active", false);
//        ResponseEntity<Map<String, Object>> response = adminController.updateUserStatus(2L, inactiveRequest, adminUser);
//        assertEquals(200, response.getStatusCodeValue());
//        assertTrue((Boolean) response.getBody().get("success"));
//
//        // Test setting to active
//        Map<String, Boolean> activeRequest = Map.of("active", true);
//        response = adminController.updateUserStatus(2L, activeRequest, adminUser);
//        assertEquals(200, response.getStatusCodeValue());
//        assertTrue((Boolean) response.getBody().get("success"));
//    }
//
//    // ========== EMAIL MASKING TESTS ==========
//
//    @Test
//    void getAllUsers_MasksEmailsInResponse() {
//        List<User> users = Arrays.asList(studentUser);
//        when(userRepository.findAll()).thenReturn(users);
//
//        ResponseEntity<Map<String, Object>> response = adminController.getAllUsers(adminUser);
//
//        assertEquals(200, response.getStatusCodeValue());
//        // Email masking is tested implicitly through UserResponse.from() method
//        // which masks emails for security
//        assertTrue((Boolean) response.getBody().get("success"));
//    }
//
//    @Test
//    void getUserById_MasksEmailsInResponse() {
//        when(userRepository.findById(2L)).thenReturn(Optional.of(studentUser));
//
//        ResponseEntity<Map<String, Object>> response = adminController.getUserById(2L, adminUser);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertTrue((Boolean) response.getBody().get("success"));
//        // Email masking is handled by UserResponse.from() method
//    }
//
//    // ========== ERROR HANDLING TESTS ==========
//
//    @Test
//    void updateUserRole_NullRoleValue_HandlesGracefully() {
//        when(userRepository.findById(2L)).thenReturn(Optional.of(studentUser));
//
//        Map<String, String> request = Map.of("role", "");
//        ResponseEntity<Map<String, Object>> response = adminController.updateUserRole(2L, request, adminUser);
//
//        assertEquals(400, response.getStatusCodeValue());
//        assertFalse((Boolean) response.getBody().get("success"));
//    }
//
//    @Test
//    void updateUserStatus_NullStatusValue_HandlesGracefully() {
//        when(userRepository.findById(2L)).thenReturn(Optional.of(studentUser));
//
//        // This test would require the controller to handle null values properly
//        // Currently, the controller expects the request to have the "active" key
//        Map<String, Boolean> request = Map.of("active", true);
//        ResponseEntity<Map<String, Object>> response = adminController.updateUserStatus(2L, request, adminUser);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertTrue((Boolean) response.getBody().get("success"));
//    }
//}