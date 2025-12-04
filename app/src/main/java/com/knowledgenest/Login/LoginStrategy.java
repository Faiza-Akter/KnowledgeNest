package com.knowledgenest.Login;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginStrategy implements AuthStrategy {

    private final FirebaseAuth auth;

    public LoginStrategy(FirebaseAuth auth) {
        this.auth = auth;
    }

    @Override
    public void execute(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }
}


