package com.knowledgenest.Login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.knowledgenest.R;
import com.knowledgenest.databinding.ActivityForgetPasswordBinding;

public class ForgetPasswordActivity extends AppCompatActivity {

    private ActivityForgetPasswordBinding binding;
    private Dialog loadingDialog;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = AuthManager.getInstance().getAuth();

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.loading_dialog);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
        }

        binding.btnForgotPass.setOnClickListener(v -> {
            String email = binding.email.getText().toString().trim();
            if (email.isEmpty()) {
                binding.email.setError("Enter email");
            } else {
                forgotPassword(email);
            }
        });

        binding.login.setOnClickListener(v -> {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        });
    }

    private void forgotPassword(String email) {
        loadingDialog.show();
        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            loadingDialog.dismiss();
            if (task.isSuccessful()) {
                Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show();
                onBackPressed();
            } else {
                Toast.makeText(this, task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

