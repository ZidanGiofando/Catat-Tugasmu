package com.example.dailytask.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dailytask.R;
import com.example.dailytask.api.ApiClient;
import com.example.dailytask.api.ApiInterface;
import com.example.dailytask.models.BaseResponse;
import com.example.dailytask.models.Task;
import com.example.dailytask.utils.PreferenceManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTaskActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView txtTitle, txtDescription, txtDeadline, txtTime, txtPriority, txtStatus;
    private MaterialButton btnEdit, btnDone, btnDelete;

    private ApiInterface apiInterface;
    private PreferenceManager preferenceManager;
    private int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_task);

        taskId = getIntent().getIntExtra("task_id", 0);
        if (taskId == 0) {
            finish();
            return;
        }

        initView();
        initApi();
        setupToolbar();
        setupListener();
        loadTaskDetail();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        txtTitle = findViewById(R.id.txtTitle);
        txtDescription = findViewById(R.id.txtDescription);
        txtDeadline = findViewById(R.id.txtDeadline);
        txtTime = findViewById(R.id.txtTime);
        txtPriority = findViewById(R.id.txtPriority);
        txtStatus = findViewById(R.id.txtStatus);
        btnEdit = findViewById(R.id.btnEdit);
        btnDone = findViewById(R.id.btnDone);
        btnDelete = findViewById(R.id.btnDelete);
    }

    private void initApi() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        preferenceManager = new PreferenceManager(this);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupListener() {
        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTaskActivity.class);
            intent.putExtra("is_edit", true);
            intent.putExtra("task_id", taskId);
            startActivity(intent);
        });

        btnDone.setOnClickListener(v -> completeTask());

        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    private void loadTaskDetail() {
        apiInterface.getTaskDetail(taskId, preferenceManager.getUid())
                .enqueue(new Callback<BaseResponse<Task>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Task>> call, Response<BaseResponse<Task>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            displayTask(response.body().getData());
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Task>> call, Throwable t) {
                        Toast.makeText(DetailTaskActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayTask(Task task) {
        if (task == null) return;

        txtTitle.setText(task.getTitle());
        txtDescription.setText(task.getDescription());
        
        if (task.getDeadline() != null) {
            String[] parts = task.getDeadline().split(" ");
            txtDeadline.setText(parts[0]);
            if (parts.length > 1) {
                txtTime.setText(parts[1].substring(0, 5));
            }
        }

        txtPriority.setText(task.getPriority().toUpperCase());
        if (task.getStatus() == 1) {
            txtStatus.setText("Selesai");
            txtStatus.setBackgroundResource(R.drawable.bg_status_done);
            btnDone.setVisibility(View.GONE);
        } else {
            txtStatus.setText("Belum Selesai");
            txtStatus.setBackgroundResource(R.drawable.bg_status_pending);
            btnDone.setVisibility(View.VISIBLE);
        }
    }

    private void completeTask() {
        apiInterface.completeTask(taskId, preferenceManager.getUid()).enqueue(new Callback<BaseResponse<Object>>() {
            @Override
            public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(DetailTaskActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if (response.body().isSuccess()) loadTaskDetail();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                Toast.makeText(DetailTaskActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Tugas")
                .setMessage("Apakah Anda yakin ingin menghapus tugas ini?")
                .setPositiveButton("Hapus", (dialog, which) -> deleteTask())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteTask() {
        apiInterface.deleteTask(taskId, preferenceManager.getUid()).enqueue(new Callback<BaseResponse<Object>>() {
            @Override
            public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(DetailTaskActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if (response.body().isSuccess()) finish();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                Toast.makeText(DetailTaskActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTaskDetail();
    }
}
