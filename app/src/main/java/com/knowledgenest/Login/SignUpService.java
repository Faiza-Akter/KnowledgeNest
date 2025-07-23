package com.knowledgenest.Login;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpService {

    private final FirebaseAuth auth;

    public SignUpService(FirebaseAuth auth) {
        this.auth = auth;
    }

    public void signup(String email, String password, OnCompleteListener<AuthResult> listener) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }
}