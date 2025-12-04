package com.knowledgenest.Login;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;

public interface AuthStrategy {
    void execute(String email, String password, OnCompleteListener<AuthResult> listener);
}

