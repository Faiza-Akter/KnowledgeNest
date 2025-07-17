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
import com.knowledgenest.Model.UserModel;
import com.knowledgenest.R;
import com.knowledgenest.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
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

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.edtName.getText().toString().trim();
                String email = binding.edtEmail.getText().toString().trim();
                String password = binding.edtPassword.getText().toString().trim();

                if (name.isEmpty()) {
                    binding.edtName.setError("Enter your name");
                    binding.edtName.requestFocus();
                } else if (email.isEmpty()) {
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
                        Toast.makeText(SignUpActivity.this,
                                "No internet connection",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    signup(name, email, password);
                }
            }
        });

        binding.alreadyAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void signup(String name, String email, String password) {
        loadingDialog.show();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId = auth.getCurrentUser().getUid();
                            UserModel model = new UserModel(
                                    name,
                                    email,
                                    password,
                                    "https://firebasestorage.googleapis.com/v0/b/knowledge-nest-ff0a2.firebasestorage.app/o/pp.png.png?alt=media&token=e279cead-3799-4059-8e50-5070d8d9c561"
                            );

                            database.getReference().child("user_details").child(userId)
                                    .setValue(model)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                auth.getCurrentUser().sendEmailVerification()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                loadingDialog.dismiss();
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(SignUpActivity.this,
                                                                            "Registration successful! Please verify your email",
                                                                            Toast.LENGTH_LONG).show();
                                                                    auth.signOut();
                                                                    startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(SignUpActivity.this,
                                                                            "Failed to send verification email: " + task.getException().getMessage(),
                                                                            Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                loadingDialog.dismiss();
                                                Toast.makeText(SignUpActivity.this,
                                                        "Failed to save user data: " + task.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            loadingDialog.dismiss();
                            Toast.makeText(SignUpActivity.this,
                                    "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}