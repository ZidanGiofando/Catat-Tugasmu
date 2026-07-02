package com.example.dailytask.listener;

import com.example.dailytask.models.Task;

public interface OnTaskClickListener {

    void onTaskClick(Task task);

    void onEditClick(Task task);

    void onDeleteClick(Task task);

    void onCompleteClick(Task task);



}