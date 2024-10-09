package com.example.livingdream;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.example.livingdream.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseHelper = new DatabaseHelper(this);

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = binding.loginEmail.getText().toString();
                String password = binding.loginPassword.getText().toString();

                if (email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please insert email properly", Toast.LENGTH_SHORT).show();
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LoginActivity.this, "Please insert a valid email address", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "All fields are mandatory", Toast.LENGTH_SHORT).show();
                } else {
                    // Check if the email is already registered
                    Boolean isEmailRegistered = databaseHelper.checkEmail(email);
                    Log.d("LoginActivity", "Is email registered: " + isEmailRegistered); // Debug log

                    if (isEmailRegistered) {
                        // Check credentials
                        Boolean checkCredentials = databaseHelper.checkEmailPassword(email, password);
                        Log.d("LoginActivity", "Credentials check passed: " + checkCredentials); // Debug log

                        if (checkCredentials) {
                            // Retrieve the username from the database using the email
                            Cursor cursor = databaseHelper.getUserDetailsByEmail(email);
                            if (cursor != null && cursor.moveToFirst()) {
                                String username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
                                cursor.close();

                                // Store username in SharedPreferences
                                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("username", username); // Store the username
                                editor.apply();

                                // Debug log to check if username is stored correctly
                                Log.d("LoginActivity", "Stored username in SharedPreferences: " + username);

                            } else {
                                Log.d("LoginActivity", "Cursor is null or unable to move to first");
                            }

                            Toast.makeText(LoginActivity.this, "Login Successfully!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Wrong password, please try again", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // If the email is not registered
                        Toast.makeText(LoginActivity.this, "This email is not registered yet.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }
}
