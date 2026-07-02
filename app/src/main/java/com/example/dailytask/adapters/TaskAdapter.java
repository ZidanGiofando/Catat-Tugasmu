package com.example.dailytask.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailytask.R;
import com.example.dailytask.models.Task;

import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface OnTaskActionListener {
        void onTaskClick(Task task);
        void onEditClick(Task task);
        void onDeleteClick(Task task);
        void onCompleteClick(Task task);
    }

    private final Context context;
    private final List<Task> taskList;
    private final OnTaskActionListener listener;

    public TaskAdapter(Context context, List<Task> taskList, OnTaskActionListener listener) {
        this.context = context;
        this.taskList = taskList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.tvTitle.setText(task.getTitle());
        holder.tvDescription.setText(task.getDescription());
        holder.tvDeadline.setText(task.getDeadline());

        String priority = task.getPriority();
        if (priority == null) priority = "Low";
        holder.tvPriority.setText(priority.toUpperCase(Locale.ROOT));

        switch (priority.toLowerCase(Locale.ROOT)) {
            case "high":
                holder.tvPriority.setBackgroundResource(R.drawable.bg_priority_high);
                break;
            case "medium":
                holder.tvPriority.setBackgroundResource(R.drawable.bg_priority_medium);
                break;
            default:
                holder.tvPriority.setBackgroundResource(R.drawable.bg_priority_low);
                break;
        }

        // status 1 = completed, 0 = pending (as per Task.java isCompleted returns status == 1)
        if (task.getStatus() == 1) {
            holder.tvStatus.setText("Selesai");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_done);
        } else {
            holder.tvStatus.setText("Aktif");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending);
        }

        holder.cardTask.setOnClickListener(v -> {
            if (listener != null) listener.onTaskClick(task);
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEditClick(task);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(task);
        });

        holder.btnDone.setOnClickListener(v -> {
            if (listener != null) listener.onCompleteClick(task);
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CardView cardTask;
        TextView tvTitle, tvDescription, tvDeadline, tvPriority, tvStatus;
        ImageButton btnEdit, btnDelete, btnDone;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTask = itemView.findViewById(R.id.cardTask);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDeadline = itemView.findViewById(R.id.tvDeadline);
            tvPriority = itemView.findViewById(R.id.tvPriority);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnDone = itemView.findViewById(R.id.btnDone);
        }
    }
}
