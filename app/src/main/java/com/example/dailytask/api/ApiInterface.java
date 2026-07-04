package com.example.dailytask.api;

import com.example.dailytask.models.BaseResponse;
import com.example.dailytask.models.Statistics;
import com.example.dailytask.models.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    /**
     * Mengambil semua task berdasarkan Firebase UID
     */
    @GET("tasks.php")
    Call<BaseResponse<List<Task>>> getTasks(
            @Query("user_uid") String userUid
    );

    /**
     * Mengambil detail task
     */
    @GET("task_detail.php")
    Call<BaseResponse<Task>> getTaskDetail(
            @Query("id") int id,
            @Query("user_uid") String userUid
    );

    /**
     * Mengambil statistik dashboard
     */
    @GET("statistics.php")
    Call<BaseResponse<Statistics>> getStatistics(
            @Query("user_uid") String userUid
    );

    /**
     * Tambah Task
     */
    @FormUrlEncoded
    @POST("task_create.php")
    Call<BaseResponse<Object>> addTask(
            @Field("user_uid") String userUid,
            @Field("title") String title,
            @Field("description") String description,
            @Field("deadline") String deadline,
            @Field("priority") String priority
    );

    /**
     * Update Task
     * user_uid wajib dikirim agar server dapat memastikan task yang
     * diupdate benar-benar milik user yang sedang login.
     */
    @FormUrlEncoded
    @POST("task_update.php")
    Call<BaseResponse<Object>> updateTask(
            @Field("id") int id,
            @Field("user_uid") String userUid,
            @Field("title") String title,
            @Field("description") String description,
            @Field("deadline") String deadline,
            @Field("priority") String priority
    );

    /**
     * Hapus Task
     * user_uid wajib dikirim agar user lain tidak bisa menghapus
     * task milik user lain hanya dengan menebak id.
     */
    @FormUrlEncoded
    @POST("task_delete.php")
    Call<BaseResponse<Object>> deleteTask(
            @Field("id") int id,
            @Field("user_uid") String userUid
    );

    /**
     * Tandai Task Selesai
     * user_uid wajib dikirim untuk validasi kepemilikan task.
     */
    @FormUrlEncoded
    @POST("task_complete.php")
    Call<BaseResponse<Object>> completeTask(
            @Field("id") int id,
            @Field("user_uid") String userUid
    );

}