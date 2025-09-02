package com.campusnest.campusnest_platform.validation;

import com.campusnest.campusnest_platform.requests.DeviceInfo;
import com.campusnest.campusnest_platform.requests.LoginRequest;
import com.campusnest.campusnest_platform.requests.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AuthValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void validRegisterRequest_ShouldPassValidation() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("john.doe@stanford.edu");
        request.setPassword("SecurePass123!");
        request.setFirstName("John");
        request.setLastName("Doe");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "test@", "@domain.com", "", " "})
    void invalidEmails_ShouldFailValidation(String email) {
        RegisterRequest request = new RegisterRequest();
        request.setEmail(email);
        request.setPassword("SecurePass123!");
        request.setFirstName("John");
        request.setLastName("Doe");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("email");
    }

    @ParameterizedTest
    @ValueSource(strings = {"short", "nouppercase123", "NOLOWERCASE123", "NoNumbers"})
    void weakPasswords_ShouldFailValidation(String password) {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@stanford.edu");
        request.setPassword(password);
        request.setFirstName("John");
        request.setLastName("Doe");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("password");
    }

    @Test
    void missingRequiredFields_ShouldFailValidation() {
        RegisterRequest request = new RegisterRequest();
        // All fields empty

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).hasSize(4); // email, password, firstName, lastName
        
        assertThat(violations).extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .containsExactlyInAnyOrder("email", "password", "firstName", "lastName");
    }

    @Test
    void validLoginRequest_ShouldPassValidation() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@stanford.edu");
        request.setPassword("Password123!");
        request.setRememberMe(false);
        
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setDeviceId("device-123");
        deviceInfo.setDeviceType("web");
        deviceInfo.setUserAgent("Mozilla/5.0");
        request.setDeviceInfo(deviceInfo);

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void loginRequestWithoutPassword_ShouldFailValidation() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@stanford.edu");
        request.setPassword(""); // Empty password
        request.setRememberMe(false);

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("password");
    }

    @Test
    void loginRequestWithInvalidEmail_ShouldFailValidation() {
        LoginRequest request = new LoginRequest();
        request.setEmail("invalid-email");
        request.setPassword("Password123!");
        request.setRememberMe(false);

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("email");
    }

    @Test
    void deviceInfo_ShouldBeOptionalForLogin() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@stanford.edu");
        request.setPassword("Password123!");
        request.setRememberMe(false);
        request.setDeviceInfo(null); // No device info

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    void rememberMe_ShouldDefaultToFalse() {
        LoginRequest request = new LoginRequest();
        assertThat(request.getRememberMe()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "ThisIsAReallyLongFirstNameThatExceedsFiftyCharactersLimit"})
    void invalidFirstName_ShouldFailValidation(String firstName) {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@stanford.edu");
        request.setPassword("SecurePass123!");
        request.setFirstName(firstName);
        request.setLastName("Doe");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("firstName");
    }

    @ParameterizedTest
    @ValueSource(strings = {"D", "ThisIsAReallyLongLastNameThatExceedsFiftyCharactersLimit"})
    void invalidLastName_ShouldFailValidation(String lastName) {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@stanford.edu");
        request.setPassword("SecurePass123!");
        request.setFirstName("John");
        request.setLastName(lastName);

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);
        assertThat(violations).isNotEmpty();
        assertThat(violations).extracting(ConstraintViolation::getPropertyPath)
                .extracting(Object::toString)
                .contains("lastName");
    }
}