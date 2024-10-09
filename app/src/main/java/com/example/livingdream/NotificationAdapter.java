package com.example.livingdream;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<AppNotification> notifications;

    // Constructor to initialize the adapter with the list of notifications
    public NotificationAdapter(List<AppNotification> notifications) {
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the layout for each notification item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppNotification notification = notifications.get(position);

        // Set the title and message
        holder.titleTextView.setText(notification.getTitle());
        holder.messageTextView.setText(notification.getMessage());

        // Only show BMI if it's greater than 0, otherwise hide it
        if (notification.getBmi() > 0) {
            holder.bmiValueTextView.setVisibility(View.VISIBLE);
            holder.bmiValueTextView.setText("BMI: " + String.format("%.2f", notification.getBmi())); // Format BMI value
        } else {
            holder.bmiValueTextView.setVisibility(View.GONE); // Hide BMI if it's 0 (calorie deficit notifications)
        }
    }



    @Override
    public int getItemCount() {
        return notifications.size();
    }

    // ViewHolder class to hold the item views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, messageTextView, bmiValueTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            bmiValueTextView = itemView.findViewById(R.id.bmiValueTextView); // Updated to reflect new ID
        }
    }
}
