package com.knowledgenest.Login;

import com.google.firebase.auth.FirebaseAuth;

public class AuthManager {

    private static AuthManager instance;
    private FirebaseAuth auth;

    private AuthStrategy loginStrategy;
    private AuthStrategy signupStrategy;


    private AuthManager() {
        auth = FirebaseAuth.getInstance();
    }

    public static synchronized AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    public void setLoginStrategy(AuthStrategy loginStrategy) {
        this.loginStrategy = loginStrategy;
    }

    public void setSignupStrategy(AuthStrategy signupStrategy) {
        this.signupStrategy = signupStrategy;
    }

    public void executeLogin(String email, String password, com.google.android.gms.tasks.OnCompleteListener<com.google.firebase.auth.AuthResult> listener) {
        if (loginStrategy != null) loginStrategy.execute(email, password, listener);
    }

    public void executeSignup(String email, String password, com.google.android.gms.tasks.OnCompleteListener<com.google.firebase.auth.AuthResult> listener) {
        if (signupStrategy != null) signupStrategy.execute(email, password, listener);
    }
}
