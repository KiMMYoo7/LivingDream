package com.example.livingdream;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {

    private LineChart bmiChart;
    private TextView calorieResultSummary; // TextView for calorie results
    private DatabaseHelper databaseHelper;
    private String currentUsername;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(getActivity());

        // Retrieve the logged-in username from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", null);

        if (currentUsername == null) {
            // If no user is logged in, notify and close the fragment
            Toast.makeText(getContext(), "Please log in first", Toast.LENGTH_SHORT).show();
            requireActivity().finish(); // Close the activity or handle logout
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Initialize the BMI chart
        bmiChart = view.findViewById(R.id.bmi_chart);
        initializeChart();

        // Initialize the calorie result TextView
        calorieResultSummary = view.findViewById(R.id.calorie_result_summary);

        // Load the calorie data and display it in the TextView
        loadCalorieData();

        // Find the "Open BMI Calculation" button and set its click listener
        Button openBMICalculationButton = view.findViewById(R.id.open_bmi_calculation_button);
        openBMICalculationButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BMICalculationActivity.class);
            startActivity(intent);
        });

        // Find the "Calorie Deficit Calculator" button and set its click listener
        Button calorieDeficitButton = view.findViewById(R.id.button_calculate_calories); // Correct ID from XML

        calorieDeficitButton.setOnClickListener(v -> {
            // Navigate to CalorieDeficitActivity
            Intent intent = new Intent(getActivity(), CalorieDeficitActivity.class);
            startActivity(intent);
        });

        return view;
    }

    private void initializeChart() {
        bmiChart.getDescription().setEnabled(false);
        bmiChart.setDrawGridBackground(false);
        updateChart();
    }

    private void updateChart() {
        List<Entry> entries = new ArrayList<>();

        // Fetch BMI data for the current user from the database
        Cursor cursor = databaseHelper.getBMIData(currentUsername);

        if (cursor != null) {
            int i = 0;
            while (cursor.moveToNext()) {
                float bmi = cursor.getFloat(cursor.getColumnIndexOrThrow("bmi"));
                entries.add(new Entry(i, bmi)); // Use 'i' as the x-value (could be timestamp or another unique value)
                i++;
            }
            cursor.close();
        }

        LineDataSet dataSet = new LineDataSet(entries, "BMI Progress");
        LineData lineData = new LineData(dataSet);
        bmiChart.setData(lineData);
        bmiChart.invalidate(); // Refresh chart
    }

    // Load calorie data from the database and display it in the TextView
    private void loadCalorieData() {
        Cursor cursor = databaseHelper.getCalorieData(currentUsername);
        if (cursor != null && cursor.moveToFirst()) {
            int maintainCalories = cursor.getInt(cursor.getColumnIndex("maintain_calories"));
            int mildLossCalories = cursor.getInt(cursor.getColumnIndex("mild_loss_calories"));
            int extremeLossCalories = cursor.getInt(cursor.getColumnIndex("extreme_loss_calories"));

            // Display the results in the TextView
            calorieResultSummary.setText("Maintain Weight: " + maintainCalories +
                    " Calories/day\nMild Weight Loss: " + mildLossCalories +
                    " Calories/day\nExtreme Weight Loss: " + extremeLossCalories + " Calories/day");
        } else {
            calorieResultSummary.setText("No calorie result available");
        }
        if (cursor != null) {
            cursor.close();
        }
    }
}
