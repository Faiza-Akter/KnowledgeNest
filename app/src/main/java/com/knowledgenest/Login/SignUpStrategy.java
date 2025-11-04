package com.knowledgenest.Login;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpStrategy implements AuthStrategy {

    private final FirebaseAuth auth;

    public SignUpStrategy(FirebaseAuth auth) {
        this.auth = auth;
    }

    @Override
    public void execute(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(listener);
    }
}


