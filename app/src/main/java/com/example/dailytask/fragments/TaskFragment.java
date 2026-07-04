package com.example.dailytask.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.widget.Toast;

import com.example.dailytask.R;
import com.example.dailytask.activities.AddTaskActivity;
import com.example.dailytask.activities.DetailTaskActivity;
import com.example.dailytask.adapters.TaskAdapter;
import com.example.dailytask.api.ApiClient;
import com.example.dailytask.api.ApiInterface;
import com.example.dailytask.models.BaseResponse;
import com.example.dailytask.models.Task;
import com.example.dailytask.utils.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskFragment extends Fragment
        implements TaskAdapter.OnTaskActionListener {

    //==============================
    // View
    //==============================

    private RecyclerView rvTask;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fabAddTask;
    private TextInputEditText etSearch;

    //==============================
    // Adapter & Data
    //==============================

    private TaskAdapter adapter;
    private final List<Task> taskList = new ArrayList<>();

    //==============================
    // API
    //==============================

    private ApiInterface apiInterface;
    private PreferenceManager preferenceManager;

    public TaskFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {

        return inflater.inflate(
                R.layout.fragment_task,
                container,
                false
        );

    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        super.onViewCreated(view, savedInstanceState);

        initView(view);

        initRecyclerView();

        initApi();

        setupListener();

        loadTasks();

    }

    /**
     * Inisialisasi seluruh View
     */
    private void initView(View view) {

        rvTask = view.findViewById(R.id.rvTask);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        fabAddTask = view.findViewById(R.id.fabAddTask);
        etSearch = view.findViewById(R.id.etSearch);

    }

    /**
     * Inisialisasi RecyclerView
     */
    private void initRecyclerView() {

        adapter = new TaskAdapter(
                requireContext(),
                taskList,
                this
        );

        rvTask.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        rvTask.setHasFixedSize(true);

        rvTask.setAdapter(adapter);

    }

    /**
     * Inisialisasi API & Preference
     */
    private void initApi() {

        apiInterface = ApiClient
                .getClient()
                .create(ApiInterface.class);

        preferenceManager =
                new PreferenceManager(requireContext());

    }

    /**
     * Listener
     */
    private void setupListener() {

        fabAddTask.setOnClickListener(v -> {

            Intent intent = new Intent(
                    requireContext(),
                    AddTaskActivity.class
            );

            startActivity(intent);

        });

        swipeRefresh.setOnRefreshListener(this::loadTasks);

    }

    /**
     * Mengambil seluruh task dari server
     */
    private void loadTasks() {

        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        String userUid = preferenceManager.getUid();

        apiInterface.getTasks(userUid)
                .enqueue(new Callback<BaseResponse<List<Task>>>() {

                    @Override
                    public void onResponse(
                            Call<BaseResponse<List<Task>>> call,
                            Response<BaseResponse<List<Task>>> response
                    ) {

                        progressBar.setVisibility(View.GONE);
                        swipeRefresh.setRefreshing(false);

                        if (!response.isSuccessful() || response.body() == null) {

                            Toast.makeText(
                                    requireContext(),
                                    "Gagal mengambil data",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        BaseResponse<List<Task>> result = response.body();

                        if (!result.isSuccess()) {

                            Toast.makeText(
                                    requireContext(),
                                    result.getMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }

                        taskList.clear();

                        if (result.getData() != null) {

                            taskList.addAll(result.getData());

                        }

                        adapter.notifyDataSetChanged();

                        if (taskList.isEmpty()) {

                            tvEmpty.setVisibility(View.VISIBLE);

                        } else {

                            tvEmpty.setVisibility(View.GONE);

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<BaseResponse<List<Task>>> call,
                            Throwable t
                    ) {

                        progressBar.setVisibility(View.GONE);
                        swipeRefresh.setRefreshing(false);

                        Toast.makeText(
                                requireContext(),
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

    /**
     * Menghapus task
     */
    private void deleteTask(int taskId) {

        progressBar.setVisibility(View.VISIBLE);

        apiInterface.deleteTask(taskId, preferenceManager.getUid())
                .enqueue(new Callback<BaseResponse<Object>>() {

                    @Override
                    public void onResponse(
                            Call<BaseResponse<Object>> call,
                            Response<BaseResponse<Object>> response
                    ) {

                        progressBar.setVisibility(View.GONE);

                        if (!response.isSuccessful() || response.body() == null) {

                            Toast.makeText(
                                    requireContext(),
                                    "Gagal menghapus task",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;

                        }

                        BaseResponse<Object> result = response.body();

                        Toast.makeText(
                                requireContext(),
                                result.getMessage(),
                                Toast.LENGTH_SHORT
                            ).show();

                        if (result.isSuccess()) {

                            loadTasks();

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<BaseResponse<Object>> call,
                            Throwable t
                    ) {

                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(
                                requireContext(),
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

    /**
     * Menandai task menjadi selesai
     */
    private void completeTask(int taskId) {

        progressBar.setVisibility(View.VISIBLE);

        apiInterface.completeTask(taskId, preferenceManager.getUid())
                .enqueue(new Callback<BaseResponse<Object>>() {

                    @Override
                    public void onResponse(
                            Call<BaseResponse<Object>> call,
                            Response<BaseResponse<Object>> response
                    ) {

                        progressBar.setVisibility(View.GONE);

                        if (!response.isSuccessful() || response.body() == null) {

                            Toast.makeText(
                                    requireContext(),
                                    "Gagal menyelesaikan task",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;

                        }

                        BaseResponse<Object> result = response.body();

                        Toast.makeText(
                                requireContext(),
                                result.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();

                        if (result.isSuccess()) {

                            loadTasks();

                        }

                    }

                    @Override
                    public void onFailure(
                            Call<BaseResponse<Object>> call,
                            Throwable t
                    ) {

                        progressBar.setVisibility(View.GONE);

                        Toast.makeText(
                                requireContext(),
                                t.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();

                    }

                });

    }

    //==================================================
    // CALLBACK TASK ADAPTER
    //==================================================

    /**
     * Ketika Card Task diklik
     */
    @Override
    public void onTaskClick(Task task) {

        Intent intent = new Intent(
                requireContext(),
                DetailTaskActivity.class
        );

        intent.putExtra("task_id", task.getId());

        startActivity(intent);

    }

    /**
     * Tombol Edit
     */
    @Override
    public void onEditClick(Task task) {

        Intent intent = new Intent(
                requireContext(),
                AddTaskActivity.class
        );

        intent.putExtra("is_edit", true);
        intent.putExtra("task_id", task.getId());

        startActivity(intent);

    }

    /**
     * Tombol Delete
     */
    @Override
    public void onDeleteClick(Task task) {

        new AlertDialog.Builder(requireContext())
                .setTitle("Hapus Task")
                .setMessage("Yakin ingin menghapus \"" + task.getTitle() + "\"?")
                .setPositiveButton("Hapus", (dialog, which) -> {

                    deleteTask(task.getId());

                })
                .setNegativeButton("Batal", null)
                .show();

    }

    /**
     * Tombol Complete
     */
    @Override
    public void onCompleteClick(Task task) {

        if (task.isCompleted()) {

            Toast.makeText(
                    requireContext(),
                    "Task sudah selesai",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Selesaikan Task")
                .setMessage("Tandai task ini sebagai selesai?")
                .setPositiveButton("Ya", (dialog, which) -> {

                    completeTask(task.getId());

                })
                .setNegativeButton("Batal", null)
                .show();

    }

}
