package com.knowledgenest.Login;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

public class LoginParameterizedTest {

    // Simulates email validation in SignInActivity and SignUpActivity
    @ParameterizedTest(name = "Email validation: ''{0}'' should be valid? {1}")
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

        System.out.println("Email: " + email + " => Valid: " + isValid);
    }


    //  Value Source - Passwords
    @ParameterizedTest(name = "Valid password: {0}")
    @ValueSource(strings = {"abcdef", "123456", "mypassword"})
    void testPasswordLengthShouldBeValid(String password) {
        boolean isValid = password != null && password.length() >= 6;
        assertEquals(true, isValid);
    }

    //  Method Source - name validation
    @ParameterizedTest(name = "Name validation: {0} should be valid? {1}")
    @MethodSource("provideNamesForValidation")
    void testNameValidation(String name, boolean expectedValid) {
        boolean isValid = name != null && !name.isEmpty();
        assertEquals(expectedValid, isValid);
    }

    static Stream<Arguments> provideNamesForValidation() {
        return Stream.of(
                Arguments.of("", false),
                Arguments.of("Alice", true),
                Arguments.of("A", true),
                Arguments.of(null, false)
        );
    }

    //CSV File Source - Forget Password email (file should be in src/test/resources/emails.csv)
    @ParameterizedTest(name = "Forget Password email: {0} expected valid? {1}")
    @CsvFileSource(resources = "/emails.csv", numLinesToSkip = 1)
    void testForgetPasswordEmailValidation(String email, boolean expectedValid) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        boolean isValid = email != null && !email.isEmpty() && email.matches(emailRegex);
        assertEquals(expectedValid, isValid);
    }
    @ParameterizedTest(name = "Email validation: ''{0}'' should be valid? {1}")
    @CsvFileSource(resources = "/email_validation.csv", numLinesToSkip = 0)
    void testEmailValidationWithCsvFile(String email, boolean expectedValid) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        boolean isValid = email != null && !email.isEmpty() && email.matches(emailRegex);
        assertEquals(expectedValid, isValid);
    }
}
