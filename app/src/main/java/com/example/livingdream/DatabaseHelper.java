package com.example.livingdream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import android.util.Log;
import java.util.List;
import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SignLog.db";
    private static final int DATABASE_VERSION = 15; // Increment this to trigger onUpgrade

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the users table if it doesn't exist
        db.execSQL("CREATE TABLE IF NOT EXISTS users(username TEXT PRIMARY KEY, email TEXT UNIQUE, password TEXT)");

        // Create the bmi_data table with a username column if it doesn't exist
        db.execSQL("CREATE TABLE IF NOT EXISTS bmi_data(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "bmi REAL, " +
                "timestamp INTEGER, " +
                "FOREIGN KEY (username) REFERENCES users(username))");

        // Create the notifications table with a username column
        db.execSQL("CREATE TABLE IF NOT EXISTS notifications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +  // Add the username column here
                "title TEXT, " +
                "message TEXT, " +
                "bmi REAL, " +
                "timestamp INTEGER, " +
                "FOREIGN KEY (username) REFERENCES users(username))");

        // Create the calorie_data table if it doesn't exist
        db.execSQL("CREATE TABLE IF NOT EXISTS calorie_data(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "maintain_calories INTEGER, " +
                "mild_loss_calories INTEGER, " +
                "extreme_loss_calories INTEGER, " +
                "timestamp INTEGER, " +
                "FOREIGN KEY (username) REFERENCES users(username))");
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "onUpgrade called from version " + oldVersion + " to " + newVersion);

        // Drop the old 'notifications' table if it exists and recreate it with the 'username' column
        if (oldVersion < newVersion) {
            // Drop the old notifications table
            db.execSQL("DROP TABLE IF EXISTS notifications");
            Log.d("DatabaseHelper", "Dropped 'notifications' table");

            // Recreate the notifications table with the username column
            db.execSQL("CREATE TABLE IF NOT EXISTS notifications (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT, " +
                    "title TEXT, " +
                    "message TEXT, " +
                    "bmi REAL, " +
                    "timestamp INTEGER, " +
                    "FOREIGN KEY (username) REFERENCES users(username))");
            Log.d("DatabaseHelper", "Recreated 'notifications' table with 'username' column");
        }
    }





    // Insert new user data
    public boolean insertData(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("email", email);
        contentValues.put("password", password);
        long result = db.insert("users", null, contentValues);

        return result != -1;
    }

    // Check if the username already exists
    public boolean checkUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase(); // Use readable database
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close(); // Close the cursor to avoid memory leaks
        return exists;
    }


    public boolean checkEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close(); // Always close the cursor to avoid memory leaks
        return exists;
    }


    // Check if the email and password match
    public boolean checkEmailPassword(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase(); // Use readable database
        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email = ? AND password = ?", new String[]{email, password});
        boolean match = cursor.getCount() > 0;
        cursor.close(); // Close the cursor to avoid memory leaks
        return match;
    }

    // Update the user's password
    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("password", newPassword);

        int rowsAffected = db.update("users", contentValues, "username = ?", new String[]{username});
        Log.d("UpdatePassword", "Rows affected: " + rowsAffected);
        return rowsAffected > 0;
    }

    // Fetch user details by username


    // Fetch user details by email
    public Cursor getUserDetailsByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE email = ?", new String[]{email});
    }

    // Delete a user by username
    public boolean deleteUser(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete("users", "username = ?", new String[]{username});
        Log.d("DeleteAccount", "Rows deleted: " + rowsDeleted);
        return rowsDeleted > 0;
    }


    public Cursor getUserDetails(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
    }

    public boolean insertBMIData(String username, float bmi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username); // Ensure username is included
        contentValues.put("bmi", bmi);
        contentValues.put("timestamp", System.currentTimeMillis());
        long result = db.insert("bmi_data", null, contentValues);
        return result != -1;
    }

    public Cursor getBMIData(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM bmi_data WHERE username = ? ORDER BY timestamp ASC", new String[]{username});
    }

    public void insertNotification(String username, String title, String message, float bmi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username); // Add username here
        values.put("title", title);
        values.put("message", message);
        values.put("bmi", bmi);
        values.put("timestamp", System.currentTimeMillis());

        db.insert("notifications", null, values);
        db.close();
    }



    public List<AppNotification> getAllNotifications() {
        List<AppNotification> notifications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT title, message, bmi FROM notifications", null);

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String message = cursor.getString(cursor.getColumnIndex("message"));
                float bmi = cursor.getFloat(cursor.getColumnIndex("bmi"));
                long timestamp = cursor.getLong(cursor.getColumnIndex("timestamp")); // Retrieve the timestamp

                // Pass all four arguments to the AppNotification constructor
                AppNotification notification = new AppNotification(title, message, bmi, timestamp);
                notifications.add(notification);
            } while (cursor.moveToNext());
        }


        cursor.close(); // Always close the cursor to avoid memory leaks
        return notifications;
    }

    // Insert calorie results into the database
    public boolean insertCalorieData(String username, int maintainCalories, int mildLossCalories, int extremeLossCalories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("maintain_calories", maintainCalories);
        contentValues.put("mild_loss_calories", mildLossCalories);
        contentValues.put("extreme_loss_calories", extremeLossCalories);
        contentValues.put("timestamp", System.currentTimeMillis());

        long result = db.insert("calorie_data", null, contentValues);
        db.close(); // Make sure to close the database connection
        return result != -1;  // Return true if insertion was successful
    }


    // Retrieve calorie results for a user from the database
    public Cursor getCalorieData(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM calorie_data WHERE username = ? ORDER BY timestamp DESC LIMIT 1";
        return db.rawQuery(query, new String[]{username});
    }

    // Insert calorie deficit notification into the database
    public void insertCalorieNotification(String username, String title, String message) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username); // Add username here
        values.put("title", title);
        values.put("message", message);
        values.put("bmi", 0); // Set BMI to 0 for calorie deficit notifications
        values.put("timestamp", System.currentTimeMillis());

        db.insert("notifications", null, values);
        db.close();
    }



    // Fetch all BMI notifications
    public List<AppNotification> getBMIAllNotifications(String username) {
        List<AppNotification> notifications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Modify query to filter by the username
        Cursor cursor = db.rawQuery("SELECT title, message, bmi, timestamp FROM notifications WHERE bmi > 0 AND username = ? ORDER BY timestamp DESC", new String[]{username});

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String message = cursor.getString(cursor.getColumnIndex("message"));
                float bmi = cursor.getFloat(cursor.getColumnIndex("bmi"));
                long timestamp = cursor.getLong(cursor.getColumnIndex("timestamp"));

                AppNotification notification = new AppNotification(title, message, bmi, timestamp);
                notifications.add(notification);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return notifications;
    }


    // Fetch all Calorie Deficit notifications
    public List<AppNotification> getCalorieAllNotifications(String username) {
        List<AppNotification> notifications = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Modify query to filter by the username
        Cursor cursor = db.rawQuery("SELECT title, message, timestamp FROM notifications WHERE bmi = 0 AND username = ? ORDER BY timestamp DESC", new String[]{username});

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String message = cursor.getString(cursor.getColumnIndex("message"));
                long timestamp = cursor.getLong(cursor.getColumnIndex("timestamp"));

                AppNotification notification = new AppNotification(title, message, 0, timestamp);
                notifications.add(notification);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return notifications;
    }
}
