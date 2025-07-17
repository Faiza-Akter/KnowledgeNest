package com.knowledgenest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.knowledgenest.Login.SignInActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enables edge-to-edge rendering
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);

        // Applies window insets to avoid UI overlapping with system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Use Handler with Looper for modern and safe delayed tasks
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Move to login screen after 1.5 seconds
            Intent intent = new Intent(SplashScreenActivity.this, SignInActivity.class);
            startActivity(intent);
            finish(); // close splash so it's not in back stack
        }, 1500); // 1.5 seconds
    }
}
