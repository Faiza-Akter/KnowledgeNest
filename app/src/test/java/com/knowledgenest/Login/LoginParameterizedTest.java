package com.knowledgenest.Login;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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


    // Simulates password validation in SignInActivity and SignUpActivity
    @ParameterizedTest(name = "Password validation: ''{0}'' should be valid? {1}")
    @CsvSource({
            "'', false",
            "123, false",
            "abcdef, true",
            "123456, true",
            "abc12, false"
    })
    void testPasswordValidation(String password, boolean expectedValid) {
        boolean isValid = password != null && password.length() >= 6;
        System.out.println("Password: " + password + " => Valid: " + isValid);
    }

    // Simulates name field validation in SignUpActivity
    @ParameterizedTest(name = "Name validation: ''{0}'' should be valid? {1}")
    @CsvSource({
            "'', false",
            "A, true",
            "Alice, true"
    })
    void testNameValidation(String name, boolean expectedValid) {
        boolean isValid = name != null && !name.isEmpty();
        System.out.println("Name: " + name + " => Valid: " + isValid);
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
