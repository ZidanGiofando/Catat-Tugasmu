package com.example.dailytask.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.dailytask.R;
import com.example.dailytask.activities.LoginActivity;
import com.example.dailytask.api.ApiClient;
import com.example.dailytask.api.ApiInterface;
import com.example.dailytask.models.BaseResponse;
import com.example.dailytask.models.Statistics;
import com.example.dailytask.utils.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView txtName, txtEmail, txtTotal, txtDone, txtPending;
    private PreferenceManager preferenceManager;
    private ApiInterface apiInterface;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        loadUserData();
        loadStatistics();
        setupListener(view);
    }

    private void initView(View view) {
        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtTotal = view.findViewById(R.id.txtTotal);
        txtDone = view.findViewById(R.id.txtDone);
        txtPending = view.findViewById(R.id.txtPending);

        preferenceManager = new PreferenceManager(requireContext());
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    private void loadUserData() {
        txtName.setText(preferenceManager.getName());
        txtEmail.setText(preferenceManager.getEmail());
    }

    private void loadStatistics() {
        String uid = preferenceManager.getUid();
        apiInterface.getStatistics(uid).enqueue(new Callback<BaseResponse<Statistics>>() {
            @Override
            public void onResponse(Call<BaseResponse<Statistics>> call, Response<BaseResponse<Statistics>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Statistics stats = response.body().getData();
                    if (stats != null) {
                        txtTotal.setText(String.valueOf(stats.getTotal()));
                        txtDone.setText(String.valueOf(stats.getCompleted()));
                        txtPending.setText(String.valueOf(stats.getPending()));
                    }
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Statistics>> call, Throwable t) {
                // Silently fail or show toast
            }
        });
    }

    private void setupListener(View view) {
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            logout();
        });

        view.findViewById(R.id.cardEditProfile).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Fitur Edit Profil segera hadir", Toast.LENGTH_SHORT).show();
        });

        view.findViewById(R.id.cardAbout).setOnClickListener(v -> {
            Toast.makeText(requireContext(), "DailyTask v1.0", Toast.LENGTH_SHORT).show();
        });
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        preferenceManager.logout();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
