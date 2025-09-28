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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.knowledgenest.Activity.MainActivity;
import com.knowledgenest.R;
import com.knowledgenest.databinding.ActivitySignInBinding;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private Dialog loadingDialog;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
        }

        authManager = AuthManager.getInstance();
        authManager.setLoginStrategy(new LoginStrategy(authManager.getAuth()));

        binding.btnSignIn.setOnClickListener(v -> {
            String email = binding.edtEmail.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();

            if (!validateInputs(email, password)) return;
            if (!isNetworkAvailable()) {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
                return;
            }

            loadingDialog.show();
            authManager.executeLogin(email, password, task -> {
                loadingDialog.dismiss();
                if (task.isSuccessful()) {
                    if (authManager.getAuth().getCurrentUser().isEmailVerified()) {
                        startActivity(new Intent(SignInActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Please verify your email first", Toast.LENGTH_LONG).show();
                        authManager.getAuth().signOut();
                    }
                } else {
                    Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        binding.createAccount.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        });

        binding.forgotPassword.setOnClickListener(v -> {
            startActivity(new Intent(this, ForgetPasswordActivity.class));
        });

        if (authManager.getAuth().getCurrentUser() != null &&
                authManager.getAuth().getCurrentUser().isEmailVerified()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    private boolean validateInputs(String email, String password) {
        if (email.isEmpty()) {
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
