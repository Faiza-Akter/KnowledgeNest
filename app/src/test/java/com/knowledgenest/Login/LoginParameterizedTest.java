package com.knowledgenest.Login;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class LoginParameterizedTest {

    @BeforeAll
    static void beforeAllTests() {
        System.out.println(" [BEFORE ALL] Starting ModelParameterizedTest class...");
    }

    @BeforeEach
    void beforeEachTest() {
        System.out.println(" [BEFORE EACH] Starting a new test case...");
    }

    @AfterEach
    void afterEachTest() {
        System.out.println(" [AFTER EACH] Finished test case.\n");
    }

    @AfterAll
    static void afterAllTests() {
        System.out.println(" [AFTER ALL] All tests completed for ModelParameterizedTest.");
    }


    // Email validation in SignInActivity and SignUpActivity
    @ParameterizedTest
    @CsvSource({
            "user@example.com, true",
            "invalidEmail, false",
            "'', false",
            "user@domain, false",
            "user@domain.com, true"
    })
    void testEmailValidation(String email, boolean expectedValid) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        boolean isValid = email != null && !email.isEmpty() && email.matches(emailRegex);

        assertEquals(expectedValid, isValid, "Email validation mismatch for: " + email);
        System.out.println("Email: " + email + " => Valid: " + isValid);

    }


    // ValueSource - Passwords
    @ParameterizedTest
    @ValueSource(strings = {"abcdef", "123456", "mypassword"})
    void testPasswordLengthShouldBeValid(String password) {
        boolean isValid = password != null && password.length() >= 6;
        assertEquals(true, isValid,"Password is too short");
    }

    // MethodSource - name validation
    @ParameterizedTest
    @MethodSource("provideNamesForValidation")
    void testNameValidation(String name, boolean expectedValid) {
        boolean isValid = name != null && !name.isEmpty();
        assertEquals(expectedValid, isValid,"Should not be null or empty");
    }

    static Stream<Arguments> provideNamesForValidation() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("Alice", true),
                Arguments.of("A", true),
                Arguments.of(null, false)
        );
    }

    // CsvFileSource - Forget Password email (file should be in src/test/resources/emails.csv)
    @ParameterizedTest
    @CsvFileSource(resources = "/emails.csv", numLinesToSkip = 1)
    void testForgetPasswordEmailValidation(String email, boolean expectedValid) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        boolean isValid = email != null && !email.isEmpty() && email.matches(emailRegex);
        assertEquals(expectedValid, isValid);
    }

}
