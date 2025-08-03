package com.knowledgenest.Login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.knowledgenest.Activity.MainActivity;
import com.knowledgenest.R;
import com.knowledgenest.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Setup loading dialog
        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
        }

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.edtEmail.getText().toString().trim();
                String password = binding.edtPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    binding.edtEmail.setError("Enter your email");
                    binding.edtEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    binding.edtEmail.setError("Enter a valid email");
                    binding.edtEmail.requestFocus();
                } else if (password.isEmpty()) {
                    binding.edtPassword.setError("Enter your password");
                    binding.edtPassword.requestFocus();
                } else if (password.length() < 6) {
                    binding.edtPassword.setError("Password must be at least 6 characters");
                    binding.edtPassword.requestFocus();
                } else {
                    if (!isNetworkAvailable()) {
                        Toast.makeText(SignInActivity.this,
                                "No internet connection",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    signIn(email, password);
                }
            }
        });

        binding.createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                finish();
            }
        });

        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, ForgetPasswordActivity.class));
            }
        });

        // Auto-login if already verified
        if (auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified()) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            finish();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void signIn(String email, String password) {
        loadingDialog.show();

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (auth.getCurrentUser().isEmailVerified()) {
                                loadingDialog.dismiss();
                                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                finish();
                            } else {
                                loadingDialog.dismiss();
                                Toast.makeText(SignInActivity.this,
                                        "Please verify your email first. Check your inbox.",
                                        Toast.LENGTH_LONG).show();
                                auth.signOut();
                            }
                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(SignInActivity.this,
                                    "Login failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}