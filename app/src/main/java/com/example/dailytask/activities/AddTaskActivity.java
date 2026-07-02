package com.example.dailytask.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.chip.ChipGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dailytask.R;
import com.example.dailytask.api.ApiClient;
import com.example.dailytask.api.ApiInterface;
import com.example.dailytask.models.BaseResponse;
import com.example.dailytask.models.Task;
import com.example.dailytask.utils.PreferenceManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTaskActivity extends AppCompatActivity {

//==================================================
// VIEW
//==================================================

    private MaterialToolbar toolbar;

    private TextInputEditText etTitle;
    private TextInputEditText etDescription;
    private TextInputEditText etDeadline;
    private TextInputEditText etTime;

    private ChipGroup chipPriority;

    private Chip chipHigh;
    private Chip chipMedium;
    private Chip chipLow;

    private MaterialButton btnSave;

    private ProgressBar progressBar;

    //==================================================
    // API
    //==================================================

    private ApiInterface apiInterface;
    private PreferenceManager preferenceManager;

    //==================================================
    // DATA
    //==================================================

    private final Calendar calendar = Calendar.getInstance();

    private boolean isEdit = false;
    private int taskId = 0;
    /**
     * isEdit = true  -> Edit Task
     * isEdit = false -> Tambah Task
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_task);

        initView();

        initApi();

        getIntentData();

        setupToolbar();

    }

    /**
     * Inisialisasi seluruh View
     */
    private void initView() {

        toolbar = findViewById(R.id.toolbar);

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDeadline = findViewById(R.id.etDeadline);
        etTime = findViewById(R.id.etTime);

        chipPriority = findViewById(R.id.chipPriority);

        chipHigh = findViewById(R.id.chipHigh);
        chipMedium = findViewById(R.id.chipMedium);
        chipLow = findViewById(R.id.chipLow);

        btnSave = findViewById(R.id.btnSave);

        progressBar = findViewById(R.id.progressBar);

    }

    /**
     * Inisialisasi API
     */
    private void initApi() {

        apiInterface = ApiClient
                .getClient()
                .create(ApiInterface.class);

        preferenceManager = new PreferenceManager(this);

    }

    /**
     * Mengecek apakah Activity dibuka
     * untuk Tambah atau Edit
     */
    private void getIntentData() {

        if (getIntent() == null)
            return;

        isEdit = getIntent().getBooleanExtra("is_edit", false);

        taskId = getIntent().getIntExtra("task_id", 0);

        if (isEdit) {
            loadTaskDetail();
        }

    }
    /**
     * Toolbar
     */
    private void setupToolbar() {

        toolbar.setTitle(
                isEdit ?
                        "Edit Tugas" :
                        "Tambah Tugas"
        );

        toolbar.setNavigationOnClickListener(v -> finish());

        setupDatePicker();

        setupTimePicker();

        setupButton();

    }

    /**
     * Date Picker
     */
    private void setupDatePicker() {

        etDeadline.setOnClickListener(v -> {

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(

                    AddTaskActivity.this,

                    (view, y, m, d) -> {

                        calendar.set(Calendar.YEAR, y);
                        calendar.set(Calendar.MONTH, m);
                        calendar.set(Calendar.DAY_OF_MONTH, d);

                        SimpleDateFormat sdf =
                                new SimpleDateFormat(
                                        "yyyy-MM-dd",
                                        Locale.getDefault()
                                );

                        etDeadline.setText(
                                sdf.format(calendar.getTime())
                        );

                    },

                    year,
                    month,
                    day

            );

            dialog.show();

        });

    }

    /**
     * Time Picker
     */
    private void setupTimePicker() {

        etTime.setOnClickListener(v -> {

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog dialog = new TimePickerDialog(

                    AddTaskActivity.this,

                    (view, h, m) -> {

                        String time = String.format(
                                Locale.getDefault(),
                                "%02d:%02d",
                                h,
                                m
                        );

                        etTime.setText(time);

                    },

                    hour,
                    minute,
                    true

            );

            dialog.show();

        });

    }

    /**
     * Tombol Simpan
     */
    private void setupButton() {

        btnSave.setOnClickListener(v -> {

            if (isEdit) {

                updateTask();

            } else {

                createTask();

            }

        });

    }

    /**
     * Load Task Detail for Edit Mode
     */
    private void loadTaskDetail() {
        showLoading();
        apiInterface.getTaskDetail(taskId, preferenceManager.getUid())
                .enqueue(new Callback<BaseResponse<Task>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Task>> call, Response<BaseResponse<Task>> response) {
                        hideLoading();
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Task task = response.body().getData();
                            if (task != null) {
                                etTitle.setText(task.getTitle());
                                etDescription.setText(task.getDescription());
                                
                                if (task.getDeadline() != null) {
                                    String[] parts = task.getDeadline().split(" ");
                                    etDeadline.setText(parts[0]);
                                    if (parts.length > 1) {
                                        etTime.setText(parts[1].substring(0, 5));
                                    }
                                }

                                if ("High".equalsIgnoreCase(task.getPriority())) chipHigh.setChecked(true);
                                else if ("Low".equalsIgnoreCase(task.getPriority())) chipLow.setChecked(true);
                                else chipMedium.setChecked(true);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Task>> call, Throwable t) {
                        hideLoading();
                        Toast.makeText(AddTaskActivity.this, "Gagal memuat detail: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Tambah Task
     */
    private void createTask() {
        if (!validateInput()) return;

        showLoading();

        String uid = preferenceManager.getUid();
        String title = etTitle.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String deadline = etDeadline.getText().toString().trim() + " " + etTime.getText().toString().trim() + ":00";
        String priority = getSelectedPriority();

        apiInterface.addTask(uid, title, desc, deadline, priority)
                .enqueue(new Callback<BaseResponse<Object>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                        hideLoading();
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(AddTaskActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().isSuccess()) {
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                        hideLoading();
                        Toast.makeText(AddTaskActivity.this, "Gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Update Task
     */
    private void updateTask() {
        if (!validateInput()) return;

        showLoading();

        String title = etTitle.getText().toString().trim();
        String desc = etDescription.getText().toString().trim();
        String deadline = etDeadline.getText().toString().trim() + " " + etTime.getText().toString().trim() + ":00";
        String priority = getSelectedPriority();

        apiInterface.updateTask(taskId, title, desc, deadline, priority)
                .enqueue(new Callback<BaseResponse<Object>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                        hideLoading();
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(AddTaskActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.body().isSuccess()) {
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {
                        hideLoading();
                        Toast.makeText(AddTaskActivity.this, "Gagal: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Validasi seluruh input
     */
    private boolean validateInput() {

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String deadline = etDeadline.getText().toString().trim();
        String time = etTime.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {

            etTitle.setError("Judul tidak boleh kosong");
            etTitle.requestFocus();
            return false;

        }

        if (TextUtils.isEmpty(description)) {

            etDescription.setError("Deskripsi tidak boleh kosong");
            etDescription.requestFocus();
            return false;

        }

        if (TextUtils.isEmpty(deadline)) {

            Toast.makeText(
                    this,
                    "Silakan pilih tanggal deadline",
                    Toast.LENGTH_SHORT
            ).show();

            return false;

        }

        if (TextUtils.isEmpty(time)) {

            Toast.makeText(
                    this,
                    "Silakan pilih waktu deadline",
                    Toast.LENGTH_SHORT
            ).show();

            return false;

        }

        return true;

    }

    /**
     * Mengambil prioritas yang dipilih
     */
    private String getSelectedPriority() {

        if (chipHigh.isChecked()) {

            return "High";

        }

        if (chipMedium.isChecked()) {

            return "Medium";

        }

        if (chipLow.isChecked()) {

            return "Low";

        }

        return "Medium";

    }

    /**
     * Menampilkan loading
     */
    private void showLoading() {

        progressBar.setVisibility(View.VISIBLE);

        btnSave.setEnabled(false);

    }

    /**
     * Menyembunyikan loading
     */
    private void hideLoading() {

        progressBar.setVisibility(View.GONE);

        btnSave.setEnabled(true);

    }

}
