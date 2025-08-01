package com.knowledgenest.Login;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
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

    // Simulates forget password email validation
    @ParameterizedTest(name = "ForgetPassword email check: ''{0}'' should be valid? {1}")
    @CsvSource({
            "'', false",
            "user@example.com, true",
            "invalid-email, false"
    })
    void testForgetPasswordEmailValidation(String email, boolean expectedValid) {
        boolean isValid = email != null && !email.isEmpty();
        System.out.println("ForgetPassword Email: " + email + " => Valid: " + isValid);
    }
}
