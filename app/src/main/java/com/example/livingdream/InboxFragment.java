package com.example.livingdream;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InboxFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseHelper db;

    public InboxFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the correct layout file that contains the RecyclerView
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView_notifications); // Make sure this ID matches the XML

        // Initialize DatabaseHelper instance
        db = new DatabaseHelper(getContext());

        // Set LayoutManager to RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Retrieve the logged-in user's username from SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null); // 'username' is the key

        if (username != null) {
            // Fetch all notifications for the logged-in user and pass them to the adapter
            List<AppNotification> notifications = fetchAllNotifications(username);
            NotificationAdapter adapter = new NotificationAdapter(notifications);
            recyclerView.setAdapter(adapter);
        }
    }

    // Fetch all notifications (both BMI and Calorie Deficit) for the logged-in user and combine them
    private List<AppNotification> fetchAllNotifications(String username) {
        List<AppNotification> allNotifications = new ArrayList<>();

        // Fetch both BMI and Calorie Deficit notifications for the specific user
        List<AppNotification> bmiNotifications = db.getBMIAllNotifications(username);
        List<AppNotification> calorieNotifications = db.getCalorieAllNotifications(username);

        // Combine both lists into one
        allNotifications.addAll(bmiNotifications);
        allNotifications.addAll(calorieNotifications);

        // Optionally, sort the notifications by timestamp (latest first)
        Collections.sort(allNotifications, new Comparator<AppNotification>() {
            @Override
            public int compare(AppNotification o1, AppNotification o2) {
                return Long.compare(o2.getTimestamp(), o1.getTimestamp()); // Descending order
            }
        });

        // Return the combined list
        return allNotifications;
    }
}
