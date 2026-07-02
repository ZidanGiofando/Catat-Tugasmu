package com.example.dailytask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailytask.R;
import com.example.dailytask.utils.PreferenceManager;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2000;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        preferenceManager = new PreferenceManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (preferenceManager.isLoggedIn()) {

                startActivity(new Intent(
                        SplashActivity.this,
                        MainActivity.class
                ));

            } else {

                startActivity(new Intent(
                        SplashActivity.this,
                        LoginActivity.class
                ));

            }

            finish();

        }, SPLASH_DELAY);

    }

}