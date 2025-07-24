// File: src/test/java/com/knowledgenest/Login/LoginServiceTest.java

package com.knowledgenest;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.knowledgenest.Login.LoginService;
import com.knowledgenest.Login.SignUpService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @Mock
    FirebaseAuth firebaseAuth;

    @Mock
    Task<AuthResult> mockTask;

    @Mock
    OnCompleteListener<AuthResult> mockListener;

    @InjectMocks
    LoginService loginService;

    SignUpService signUpService;

    @BeforeEach
    void setup() {
        signUpService = new SignUpService(firebaseAuth);
    }

    // ✅ Parameterized email validation
    @ParameterizedTest
    @CsvSource({
            "user@example.com, true",
            "invalid, false",
            "user@domain, false",
            "'', false",
            "abc@xyz.com, true"
    })
    void testEmailValidation(String email, boolean expectedValid) {
        boolean result = loginService.isEmailValid(email);
        assert result == expectedValid;
    }

    // ✅ Parameterized password validation
    @ParameterizedTest
    @CsvSource({
            "123456, true",
            "abc, false",
            "'', false",
            "abcdef, true"
    })
    void testPasswordValidation(String password, boolean expectedValid) {
        boolean result = loginService.isPasswordValid(password);
        assert result == expectedValid;
    }

    // ✅ Test login triggers Firebase call
    @Test
    void testLoginFirebaseCall() {
        when(firebaseAuth.signInWithEmailAndPassword("test@example.com", "123456"))
                .thenReturn(mockTask);

        loginService.login("test@example.com", "123456", mockListener);

        verify(firebaseAuth).signInWithEmailAndPassword("test@example.com", "123456");
        verify(mockTask).addOnCompleteListener(mockListener);
    }

    // ✅ Test signup triggers Firebase call
    @Test
    void testSignupFirebaseCall() {
        when(firebaseAuth.createUserWithEmailAndPassword("test@example.com", "123456"))
                .thenReturn(mockTask);

        signUpService.signup("test@example.com", "123456", mockListener);

        verify(firebaseAuth).createUserWithEmailAndPassword("test@example.com", "123456");
        verify(mockTask).addOnCompleteListener(mockListener);
    }
}