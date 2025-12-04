package com.knowledgenest.Login;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class AuthManager {

    private static AuthManager instance;
    private final FirebaseAuth auth;
    private AuthStrategy strategy;

    // Singleton: private constructor
    private AuthManager() {
        auth = FirebaseAuth.getInstance();
    }

    // Singleton: global instance
    public static synchronized AuthManager getInstance() {
        if (instance == null) instance = new AuthManager();
        return instance;
    }

    public FirebaseAuth getAuth() {
        return auth;
    }

    // Set desired strategy (login or signup)
    public void setStrategy(AuthStrategy strategy) {
        this.strategy = strategy;
    }

    // Execute using the selected strategy
    public void execute(String email, String password, OnCompleteListener<AuthResult> listener) {
        if (strategy != null) strategy.execute(email, password, listener);
    }
}
