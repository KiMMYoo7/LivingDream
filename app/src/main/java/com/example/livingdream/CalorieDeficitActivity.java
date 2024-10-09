package com.example.livingdream;

import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.widget.Toast;

public class CalorieDeficitActivity extends AppCompatActivity {

    DatabaseHelper databaseHelper;
    String currentUsername;
    EditText weightInput, heightInput;
    RadioGroup activityLevelGroup, genderGroup;
    Button calculateButton;
    TextView caloriesMaintain, caloriesMildLoss, caloriesExtremeLoss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_deficit);  // Ensure this layout exists

        databaseHelper = new DatabaseHelper(this);

        // Retrieve the logged-in username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", null);

        // Initialize UI components
        weightInput = findViewById(R.id.input_weight);
        heightInput = findViewById(R.id.input_height);
        activityLevelGroup = findViewById(R.id.radioGroup_activity_level);
        genderGroup = findViewById(R.id.radioGroup_gender);  // New gender selection group
        calculateButton = findViewById(R.id.button_calculate_calories);
        caloriesMaintain = findViewById(R.id.calories_to_maintain);
        caloriesMildLoss = findViewById(R.id.calories_mild_loss);
        caloriesExtremeLoss = findViewById(R.id.calories_extreme_loss);

        // Set up click listener for the Calculate button
        calculateButton.setOnClickListener(v -> calculateCalorieDeficit());
    }

    private void calculateCalorieDeficit() {
        // Ensure inputs are not empty
        if (weightInput.getText().toString().isEmpty() || heightInput.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter valid weight and height values", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get user inputs
        double weight = Double.parseDouble(weightInput.getText().toString());
        double height = Double.parseDouble(heightInput.getText().toString());

        // Determine activity level multiplier based on user selection
        int selectedActivityId = activityLevelGroup.getCheckedRadioButtonId();
        double activityMultiplier;

        if (selectedActivityId == R.id.radio_sedentary) {
            activityMultiplier = 1.2;
        } else if (selectedActivityId == R.id.radio_lightly_active) {
            activityMultiplier = 1.375;
        } else if (selectedActivityId == R.id.radio_moderately_active) {
            activityMultiplier = 1.55;
        } else if (selectedActivityId == R.id.radio_very_active) {
            activityMultiplier = 1.725;
        } else if (selectedActivityId == R.id.radio_super_active) {
            activityMultiplier = 1.9;
        } else {
            activityMultiplier = 1.2; // Default to sedentary if no option is selected
        }

        // Determine BMR based on gender
        int selectedGenderId = genderGroup.getCheckedRadioButtonId();
        double bmr;

        if (selectedGenderId == R.id.radio_male) {
            bmr = 10 * weight + 6.25 * height - 5 * 25 + 5;  // For men
        } else if (selectedGenderId == R.id.radio_female) {
            bmr = 10 * weight + 6.25 * height - 5 * 25 - 161;  // For women
        } else {
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }

        // Calculate calorie requirements
        int maintenanceCalories = (int) (bmr * activityMultiplier);
        int mildDeficitCalories = maintenanceCalories - 250;
        int extremeDeficitCalories = maintenanceCalories - 500;

        // Display results in TextViews
        caloriesMaintain.setText("Maintain Weight: " + maintenanceCalories + " Calories/day");
        caloriesMildLoss.setText("Mild Weight Loss: " + mildDeficitCalories + " Calories/day");
        caloriesExtremeLoss.setText("Extreme Weight Loss: " + extremeDeficitCalories + " Calories/day");

        // Save the result to the database
        boolean isInserted = databaseHelper.insertCalorieData(currentUsername, maintenanceCalories, mildDeficitCalories, extremeDeficitCalories);

        if (isInserted) {
            String calorieMessage = "Maintain Weight: " + maintenanceCalories + " Calories/day\n" +
                    "Mild Weight Loss: " + mildDeficitCalories + " Calories/day\n" +
                    "Extreme Weight Loss: " + extremeDeficitCalories + " Calories/day";

            // Insert notification
            databaseHelper.insertCalorieNotification(currentUsername, "New Calorie Deficit Added", calorieMessage);
            Toast.makeText(this, "Calorie deficit added to inbox", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to save calorie data", Toast.LENGTH_SHORT).show();
        }
    }
}

