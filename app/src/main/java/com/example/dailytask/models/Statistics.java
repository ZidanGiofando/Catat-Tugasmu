package com.example.dailytask.models;

import com.google.gson.annotations.SerializedName;

public class Statistics {

    @SerializedName("total")
    private int total;

    @SerializedName("completed")
    private int completed;

    @SerializedName("pending")
    private int pending;

    public int getTotal() {
        return total;
    }

    public int getCompleted() {
        return completed;
    }

    public int getPending() {
        return pending;
    }
}