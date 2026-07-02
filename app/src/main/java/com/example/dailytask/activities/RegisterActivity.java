package com.example.dailytask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dailytask.R;
import com.example.dailytask.utils.PreferenceManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilEmail, tilPassword, tilConfirmPassword;

    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;

    private MaterialButton btnRegister;

    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();

        setupListener();
    }

    private void initView() {

        tilName = findViewById(R.id.tilName);
        tilEmail = findViewById(R.id.tilEmail);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        btnRegister = findViewById(R.id.btnRegister);

        progressBar = findViewById(R.id.progressBar);

        firebaseAuth = FirebaseAuth.getInstance();

        preferenceManager = new PreferenceManager(this);

    }

    private void setupListener() {

        btnRegister.setOnClickListener(v -> validateInput());

        findViewById(R.id.txtLogin).setOnClickListener(v -> {

            finish();

        });

    }

    private void validateInput() {

        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            tilName.setError("Nama lengkap wajib diisi");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("Email wajib diisi");
            etEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Format email tidak valid");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("Password wajib diisi");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            tilPassword.setError("Password minimal 6 karakter");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Konfirmasi password tidak sama");
            etConfirmPassword.requestFocus();
            return;
        }

        registerUser(name, email, password);

    }

    private void registerUser(String name, String email, String password) {

        progressBar.setVisibility(View.VISIBLE);

        btnRegister.setEnabled(false);

        btnRegister.setText("Sedang Mendaftar...");

        firebaseAuth.createUserWithEmailAndPassword(email, password)

                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        if (user != null) {

                            UserProfileChangeRequest profile =
                                    new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name)
                                            .build();

                            user.updateProfile(profile)
                                    .addOnCompleteListener(profileTask -> {

                                        saveUserSession(user);

                                    });

                        }

                    } else {

                        progressBar.setVisibility(View.GONE);

                        btnRegister.setEnabled(true);

                        btnRegister.setText("DAFTAR");

                        Toast.makeText(
                                this,
                                task.getException() != null
                                        ? task.getException().getMessage()
                                        : "Registrasi gagal",
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

    private void saveUserSession(FirebaseUser user) {

        preferenceManager.saveUser(

                user.getUid(),

                user.getDisplayName(),

                user.getEmail(),

                user.getPhotoUrl() == null
                        ? ""
                        : user.getPhotoUrl().toString()

        );

        progressBar.setVisibility(View.GONE);

        btnRegister.setEnabled(true);

        btnRegister.setText("DAFTAR");

        Toast.makeText(
                this,
                "Registrasi berhasil",
                Toast.LENGTH_SHORT
        ).show();

        startActivity(
                new Intent(
                        RegisterActivity.this,
                        MainActivity.class
                )
        );

        finishAffinity();

    }

}

