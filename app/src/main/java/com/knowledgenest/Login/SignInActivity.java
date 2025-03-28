package com.knowledgenest.Login;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.knowledgenest.MainActivity;
import com.knowledgenest.R;
import com.knowledgenest.databinding.ActivitySignInBinding;
import com.knowledgenest.databinding.ActivitySignUpBinding;

public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;

    FirebaseAuth auth;
    FirebaseDatabase database;
    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding= ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        loadingDialog= new Dialog(SignInActivity.this);
        loadingDialog.setContentView(R.layout.loading_dialog);

        if (loadingDialog.getWindow() !=null){

            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            loadingDialog.setCancelable(false);
        }


        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = binding.edtEmail.getText().toString();
                String password = binding.edtPassword.getText().toString();

                 if (email.isEmpty()){

                    binding.edtEmail.setError("Enter your email");

                }else if (password.isEmpty()){

                    binding.edtPassword.setError("Enter your password");
                }
                else {
                    signIn(email,password);
                }
            }
        });

        binding.createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SignInActivity.this,ForgetPasswordActivity.class);
                startActivity(intent);

            }
        });

        if (auth.getCurrentUser() != null){

            Intent intent = new Intent(SignInActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void signIn(String email, String password) {
        loadingDialog.show();

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){

                            if (auth.getCurrentUser().isEmailVerified()){

                                loadingDialog.dismiss();
                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();

                            }
                            else {

                                loadingDialog.dismiss();
                                Toast.makeText(SignInActivity.this, "Your email id is not verified so verify first to login", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{

                            Toast.makeText(SignInActivity.this,task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


}