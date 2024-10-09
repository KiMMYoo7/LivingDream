package com.example.livingdream;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.util.Log;
import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.database.Cursor;




import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // Parameter arguments
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // Parameters
    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        // Set up the logout button
        Button logoutButton = view.findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> logoutUser());

        // Set up the menu button
        Button menuButton = view.findViewById(R.id.menu_button);
        menuButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getActivity(), menuButton);
            popupMenu.getMenuInflater().inflate(R.menu.menu_setting, popupMenu.getMenu());

            // Set up the menu item click listener
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_account_info) {
                    showAccountDetailsDialog();
                    return true;
                } else if (item.getItemId() == R.id.menu_change_password) {
                    String username = getUsername(); // Retrieve the username
                    if (username != null) {
                        showChangePasswordDialog(username); // Pass the username to the dialog method
                    } else {
                        Toast.makeText(getActivity(), "Username not found", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                } else if (item.getItemId() == R.id.menu_delete_account) {
                    showDeleteAccountConfirmation();
                    return true;
                } else {
                    return false;
                }
            });


            popupMenu.show();
        });

        return view;
    }

    private void logoutUser() {
        // Clear user session or authentication data
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Navigate to the login screen
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();  // Close the current activity
    }

    private void showAccountDetailsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Account Details");

        // Inflate and set the dialog's view
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_account_details, null);
        TextView usernameView = dialogView.findViewById(R.id.username_text_view);
        TextView emailView = dialogView.findViewById(R.id.email_text_view);
        TextView passwordView = dialogView.findViewById(R.id.password_text_view);

        // Fetch user details
        String username = getUsername();
        Log.d("SettingFragment", "Retrieved Username from SharedPreferences: " + username); // Debug log

        if (username != null) {
            DatabaseHelper db = new DatabaseHelper(getActivity());
            Cursor cursor = db.getUserDetails(username);

            if (cursor != null && cursor.moveToFirst()) {
                String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
                String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));

                // Set the user details in the TextViews
                usernameView.setText(username);
                emailView.setText(email);
                passwordView.setText(password);
                cursor.close();
            } else {
                Log.d("AccountDetailsDialog", "No user details found for username: " + username);
                Toast.makeText(getActivity(), "User details not found", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("SettingFragment", "Username not found in SharedPreferences");
            Toast.makeText(getActivity(), "Username not found", Toast.LENGTH_SHORT).show();
        }

        builder.setView(dialogView);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String getUsername() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null);
        Log.d("SettingFragment", "Fetched username from SharedPreferences: " + username); // Debug log
        return username;
    }



    private void showChangePasswordDialog(String username) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Change Password");

        // Inflate and set the dialog's view
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        EditText currentPassword = dialogView.findViewById(R.id.current_password);
        EditText newPassword = dialogView.findViewById(R.id.new_password);
        EditText confirmNewPassword = dialogView.findViewById(R.id.confirm_new_password);

        builder.setView(dialogView);

        builder.setPositiveButton("Change", (dialog, which) -> {
            String currentPass = currentPassword.getText().toString();
            String newPass = newPassword.getText().toString();
            String confirmNewPass = confirmNewPassword.getText().toString();

            DatabaseHelper db = new DatabaseHelper(getActivity());
            Cursor cursor = db.getUserDetails(username);

            if (cursor != null && cursor.moveToFirst()) {
                String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow("password"));
                cursor.close();

                if (storedPassword.equals(currentPass)) {
                    if (newPass.equals(confirmNewPass)) {
                        boolean updated = db.updatePassword(username, newPass);
                        if (updated) {
                            Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to update password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "New passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Current password is incorrect", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "User details not found", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void showDeleteAccountConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Delete Account");
        builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            String username = getUsername();
            if (username != null) {
                DatabaseHelper db = new DatabaseHelper(getActivity());
                boolean deleted = db.deleteUser(username);
                if (deleted) {
                    Toast.makeText(getActivity(), "Account deleted successfully", Toast.LENGTH_SHORT).show();
                    logoutUser();  // Log out the user after deletion
                } else {
                    Toast.makeText(getActivity(), "Failed to delete account", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), "Username not found", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
