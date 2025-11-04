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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthResult;
import com.knowledgenest.Model.UserModel;
import com.knowledgenest.R;
import com.knowledgenest.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private Dialog loadingDialog;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
        }

        authManager = AuthManager.getInstance();
        authManager.setSignupStrategy(new SignUpStrategy(authManager.getAuth()));

        binding.btnSignUp.setOnClickListener(v -> {
            String name = binding.edtName.getText().toString().trim();
            String email = binding.edtEmail.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();

            if (!validateInputs(name, email, password)) return;
            if (!isNetworkAvailable()) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }

            loadingDialog.show();
            authManager.executeSignup(email, password, task -> {
                if (task.isSuccessful()) {
                    String userId = authManager.getAuth().getCurrentUser().getUid();
                    UserModel model = new UserModel(name, email, password,
                            "https://firebasestorage.googleapis.com/v0/b/knowledge-nest-ff0a2.appspot.com/o/pp.png.png?alt=media");

                    com.google.firebase.database.FirebaseDatabase.getInstance().getReference()
                            .child("user_details").child(userId)
                            .setValue(model).addOnCompleteListener(userTask -> {
                                loadingDialog.dismiss();
                                if (userTask.isSuccessful()) {
                                    authManager.getAuth().getCurrentUser().sendEmailVerification()
                                            .addOnCompleteListener(emailTask -> {
                                                if (emailTask.isSuccessful()) {
                                                    Toast.makeText(this, "Registration successful! Verify your email.", Toast.LENGTH_LONG).show();
                                                    authManager.getAuth().signOut();
                                                    startActivity(new Intent(this, SignInActivity.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(this, "Failed to send verification email: " + emailTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(this, "Failed to save user data: " + userTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    loadingDialog.dismiss();
                    Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.alreadyAccount.setOnClickListener(v -> {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        });
    }

    private boolean validateInputs(String name, String email, String password) {
        if (name.isEmpty()) {
            binding.edtName.setError("Enter your name");
            binding.edtName.requestFocus();
            return false;
        } else if (email.isEmpty()) {
            binding.edtEmail.setError("Enter your email");
            binding.edtEmail.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.edtEmail.setError("Enter a valid email");
            binding.edtEmail.requestFocus();
            return false;
        } else if (password.isEmpty()) {
            binding.edtPassword.setError("Enter your password");
            binding.edtPassword.requestFocus();
            return false;
        } else if (password.length() < 6) {
            binding.edtPassword.setError("Password must be at least 6 characters");
            binding.edtPassword.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}


