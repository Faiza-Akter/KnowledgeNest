package com.knowledgenest.Login;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginAssertionTest {

    private LoginService loginService;

    @BeforeEach
    public void setUp() {
        loginService = new LoginService(null); // No Firebase dependency needed for these tests
    }

    @Test
    public void testEmailValidation() {
        String validEmail = "test@example.com";
        String invalidEmail = "invalidemail";
        String emptyEmail = "";
        String nullEmail = null;

        assertTrue(loginService.isEmailValid(validEmail), "Valid email should pass");
        assertFalse(loginService.isEmailValid(invalidEmail), "Invalid email should fail");
        assertFalse(loginService.isEmailValid(emptyEmail), "Empty email should fail");
        assertFalse(loginService.isEmailValid(nullEmail), "Null email should fail");

        // Equality assertions
        assertNotEquals(validEmail, invalidEmail, "Valid and invalid emails should differ");
        assertEquals(validEmail.toLowerCase(), validEmail.toLowerCase(), "Email lowercases match");

        // Object checks
        assertNotNull(validEmail);
        assertNull(nullEmail);
    }

    @Test
    public void testPasswordValidation() {
        String validPassword = "abcdef";
        String shortPassword = "abc";
        String emptyPassword = "";
        String nullPassword = null;

        assertTrue(loginService.isPasswordValid(validPassword), "Password with 6 chars should pass");
        assertFalse(loginService.isPasswordValid(shortPassword), "Short password should fail");
        assertFalse(loginService.isPasswordValid(emptyPassword), "Empty password should fail");
        assertFalse(loginService.isPasswordValid(nullPassword), "Null password should fail");

        // Length assertion
        assertEquals(6, validPassword.length(), "Password length should be exactly 6");

        // Not same object check (Strings with same content can be different objects)
        assertNotSame(validPassword, new String("abcdef"), "Different string objects");
    }

    @Test
    public void testCombinedValidation() {
        String email = "valid@example.com";
        String password = "123456";

        assertAll("Check combined email and password validations",
                () -> assertTrue(loginService.isEmailValid(email)),
                () -> assertTrue(loginService.isPasswordValid(password))
        );
    }


}
