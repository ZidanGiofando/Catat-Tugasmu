package com.example.dailytask.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.dailytask.R;
import com.example.dailytask.fragments.DashboardFragment;
import com.example.dailytask.fragments.ProfileFragment;
import com.example.dailytask.fragments.TaskFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        initView();
        setupBottomNavigation();
        fabAddTask.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
        });
        // Fragment pertama yang ditampilkan
        if (savedInstanceState == null) {

            loadFragment(new DashboardFragment());

            bottomNavigation.setSelectedItemId(R.id.menu_dashboard);

        }

    }

    /**
     * Inisialisasi View
     */
    private void initView() {

        bottomNavigation = findViewById(R.id.bottomNavigation);
        fabAddTask = findViewById(R.id.fabAddTask);
    }

    /**
     * Bottom Navigation
     */
    private void setupBottomNavigation() {

        bottomNavigation.setOnItemSelectedListener(item -> {

            int id = item.getItemId();

            if (id == R.id.menu_dashboard) {

                loadFragment(new DashboardFragment());

                return true;

            }

            if (id == R.id.menu_task) {

                loadFragment(new TaskFragment());

                return true;

            }

            if (id == R.id.menu_profile) {

                loadFragment(new ProfileFragment());

                return true;

            }

            return false;

        });

    }

    /**
     * Ganti Fragment
     */
    private void loadFragment(@NonNull Fragment fragment) {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameContainer, fragment)
                .commit();

    }

}