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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailytask.R;
import com.example.dailytask.activities.AddTaskActivity;
import com.example.dailytask.activities.DetailTaskActivity;
import com.example.dailytask.adapters.TaskAdapter;
import com.example.dailytask.api.ApiClient;
import com.example.dailytask.api.ApiInterface;
import com.example.dailytask.models.BaseResponse;
import com.example.dailytask.models.Statistics;
import com.example.dailytask.models.Task;
import com.example.dailytask.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment implements TaskAdapter.OnTaskActionListener {

    private TextView tvGreeting;
    private TextView tvSubtitle;

    private TextView tvTotalTask;
    private TextView tvActiveTask;
    private TextView tvDoneTask;

    private RecyclerView rvRecentTask;
    private TaskAdapter adapter;
    private final List<Task> taskList = new ArrayList<>();

    private PreferenceManager preferenceManager;
    private ApiInterface apiInterface;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(
                R.layout.fragment_dashboard,
                container,
                false
        );
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        initView(view);

        loadUserData();

        loadStatistics();

        initRecyclerView();
        
        loadRecentTasks();

    }

    /**
     * Inisialisasi View
     */
    private void initView(View view) {

        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvSubtitle = view.findViewById(R.id.tvSubtitle);

        tvTotalTask = view.findViewById(R.id.tvTotalTask);
        tvActiveTask = view.findViewById(R.id.tvActiveTask);
        tvDoneTask = view.findViewById(R.id.tvDoneTask);

        rvRecentTask = view.findViewById(R.id.rvRecentTask);

        preferenceManager = new PreferenceManager(requireContext());

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

    }

    /**
     * Menampilkan nama user
     */
    private void loadUserData() {

        String name = preferenceManager.getName();

        if (name == null || name.trim().isEmpty()) {

            tvGreeting.setText("Halo 👋");

        } else {

            tvGreeting.setText("Halo, " + name + " 👋");

        }

        tvSubtitle.setText("Selamat datang kembali");

    }

    /**
     * Mengambil statistik dari server
     */
    private void loadStatistics() {

        String uid = preferenceManager.getUid();

        apiInterface.getStatistics(uid)
                .enqueue(new Callback<BaseResponse<Statistics>>() {

                    @Override
                    public void onResponse(
                            Call<BaseResponse<Statistics>> call,
                            Response<BaseResponse<Statistics>> response) {

                        if (!response.isSuccessful() || response.body() == null) {
                            return;
                        }

                        BaseResponse<Statistics> result = response.body();

                        if (!result.isSuccess()) {
                            return;
                        }

                        Statistics statistics = result.getData();

                        if (statistics == null) {
                            return;
                        }

                        tvTotalTask.setText(String.valueOf(statistics.getTotal()));
                        tvActiveTask.setText(String.valueOf(statistics.getPending()));
                        tvDoneTask.setText(String.valueOf(statistics.getCompleted()));
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<Statistics>> call, Throwable t) {
                    }

                });

    }

    /**
     * Persiapan RecyclerView
     */
    private void initRecyclerView() {
        adapter = new TaskAdapter(requireContext(), taskList, this);
        rvRecentTask.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvRecentTask.setAdapter(adapter);
    }

    private void loadRecentTasks() {
        String uid = preferenceManager.getUid();
        apiInterface.getTasks(uid).enqueue(new Callback<BaseResponse<List<Task>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<Task>>> call, Response<BaseResponse<List<Task>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    taskList.clear();
                    List<Task> allTasks = response.body().getData();
                    if (allTasks != null) {
                        // Limit to 5 recent tasks
                        int limit = Math.min(allTasks.size(), 5);
                        for (int i = 0; i < limit; i++) {
                            taskList.add(allTasks.get(i));
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<List<Task>>> call, Throwable t) {
            }
        });
    }

    @Override
    public void onTaskClick(Task task) {
        Intent intent = new Intent(requireContext(), DetailTaskActivity.class);
        intent.putExtra("task_id", task.getId());
        startActivity(intent);
    }

    @Override
    public void onEditClick(Task task) {
        Intent intent = new Intent(requireContext(), AddTaskActivity.class);
        intent.putExtra("is_edit", true);
        intent.putExtra("task_id", task.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Task task) {
        apiInterface.deleteTask(task.getId()).enqueue(new Callback<BaseResponse<Object>>() {
            @Override
            public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    loadStatistics();
                    loadRecentTasks();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {}
        });
    }

    @Override
    public void onCompleteClick(Task task) {
        apiInterface.completeTask(task.getId()).enqueue(new Callback<BaseResponse<Object>>() {
            @Override
            public void onResponse(Call<BaseResponse<Object>> call, Response<BaseResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    loadStatistics();
                    loadRecentTasks();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<Object>> call, Throwable t) {}
        });
    }
}
