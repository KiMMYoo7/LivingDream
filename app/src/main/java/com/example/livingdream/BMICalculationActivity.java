package com.example.livingdream;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class BMICalculationActivity extends AppCompatActivity {

    private EditText heightInput, weightInput;
    private TextView bmiResultText;
    private DatabaseHelper databaseHelper;
    private String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bmicalculation_activity);

        heightInput = findViewById(R.id.height_input);
        weightInput = findViewById(R.id.weight_input);
        bmiResultText = findViewById(R.id.bmi_result_text);
        Button calculateButton = findViewById(R.id.calculate_bmi_button);
        databaseHelper = new DatabaseHelper(this);

        // Retrieve the logged-in username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        currentUsername = sharedPreferences.getString("username", null);

        if (currentUsername == null) {
            // If no user is logged in, notify and close the activity
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Button click listener to calculate BMI
        calculateButton.setOnClickListener(v -> calculateBMI());
    }

    private void calculateBMI() {
        String heightStr = heightInput.getText().toString();
        String weightStr = weightInput.getText().toString();

        if (!heightStr.isEmpty() && !weightStr.isEmpty()) {
            try {
                // Parse inputs to floats
                float height = Float.parseFloat(heightStr);
                float weight = Float.parseFloat(weightStr);

                // Log the input values to check them
                Log.d("BMICalculation", "Height (input): " + height);
                Log.d("BMICalculation", "Weight (input): " + weight);

                // Ensure height is in meters (if entered in cm, convert to m)
                if (height > 10) { // Assume input > 10 means it's in cm, not m
                    height = height / 100;
                    Log.d("BMICalculation", "Converted Height to meters: " + height);
                }

                // Calculate BMI
                float bmi = weight / (height * height);
                Log.d("BMICalculation", "Calculated BMI: " + bmi);

                String bmiClassification = getBMIClassification(bmi);

                // Display BMI classification
                bmiResultText.setText(String.format("Your BMI: %.2f\n%s", bmi, bmiClassification));

                // Store BMI in the database with the current user's username
                boolean success = databaseHelper.insertBMIData(currentUsername, bmi);

                if (success) {
                    Toast.makeText(this, "BMI data saved successfully!", Toast.LENGTH_SHORT).show();

                    // Send notification with the calculated BMI
                    sendBMICalculationNotification(bmi);

                    // Save notification to inbox with the calculated BMI
                    saveNotificationToInbox("New BMI Added", "Your calculated BMI is: " + String.format("%.2f", bmi), bmi);
                } else {
                    Toast.makeText(this, "Failed to save BMI data!", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid input. Please enter valid height and weight.", Toast.LENGTH_SHORT).show();
                Log.e("BMICalculation", "Error parsing height or weight", e);
            }
        } else {
            bmiResultText.setText("Please enter both height and weight");
        }
    }


    private String getBMIClassification(float bmi) {
        if (bmi < 18.5) return "Underweight";
        else if (bmi >= 18.5 && bmi < 24.9) return "Normal weight";
        else if (bmi >= 25 && bmi < 29.9) return "Overweight";
        else return "Obesity";
    }

    private void sendBMICalculationNotification(float bmi) {
        // Create the notification with the calculated BMI
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "BMI_CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_notification) // Ensure this icon exists in the drawable folder
                .setContentTitle("New BMI Added")
                .setContentText("Your calculated BMI is: " + String.format("%.2f", bmi))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        // Initialize NotificationManagerCompat correctly
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Use a unique ID for each notification
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, builder.build());
    }



    private void saveNotificationToInbox(String title, String message, float bmi) {
        // Ensure that the correct BMI value is being passed
        Log.d("BMICalculation", "Saving notification with BMI: " + bmi);

        // Retrieve the logged-in user's username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null); // 'username' is the key

        if (username != null) {
            // Now call the insertNotification() method with the username
            databaseHelper.insertNotification(username, title, message, bmi);
        } else {
            Log.e("BMICalculation", "Username not found in SharedPreferences");
        }
    }

}
