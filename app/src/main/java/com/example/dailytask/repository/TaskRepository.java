package com.example.dailytask.repository;

import com.example.dailytask.api.ApiClient;
import com.example.dailytask.api.ApiInterface;
import com.example.dailytask.models.BaseResponse;
import com.example.dailytask.models.Statistics;
import com.example.dailytask.models.Task;

import java.util.List;

import retrofit2.Call;

public class TaskRepository {

    private final ApiInterface api;

    public TaskRepository() {
        api = ApiClient
                .getClient()
                .create(ApiInterface.class);
    }

    /**
     * Mengambil semua task user
     */
    public Call<BaseResponse<List<Task>>> getTasks(String userUid) {
        return api.getTasks(userUid);
    }

    /**
     * Detail task
     */
    public Call<BaseResponse<Task>> getTaskDetail(int id, String userUid) {
        return api.getTaskDetail(id, userUid);
    }

    /**
     * Statistik dashboard
     */
    public Call<BaseResponse<Statistics>> getStatistics(String userUid) {
        return api.getStatistics(userUid);
    }

    /**
     * Tambah task
     */
    public Call<BaseResponse<Object>> addTask(
            String userUid,
            String title,
            String description,
            String deadline,
            String priority
    ) {

        return api.addTask(
                userUid,
                title,
                description,
                deadline,
                priority
        );
    }

    /**
     * Update task
     */
    public Call<BaseResponse<Object>> updateTask(
            int id,
            String userUid,
            String title,
            String description,
            String deadline,
            String priority
    ) {

        return api.updateTask(
                id,
                userUid,
                title,
                description,
                deadline,
                priority
        );
    }

    /**
     * Hapus task
     */
    public Call<BaseResponse<Object>> deleteTask(int id, String userUid) {
        return api.deleteTask(id, userUid);
    }

    /**
     * Tandai selesai
     */
    public Call<BaseResponse<Object>> completeTask(int id, String userUid) {
        return api.completeTask(id, userUid);
    }

}