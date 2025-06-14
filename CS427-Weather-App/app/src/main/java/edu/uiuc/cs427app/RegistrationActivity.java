package edu.uiuc.cs427app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;

// Define the RegistrationActivity class, which is an AppCompatActivity and implements View.OnClickListener.
/**
 * The RegistrationActivity class is responsible for handling user registration.
 * It allows users to input their details and select a theme, and upon successful registration,
 * the user is added to the database.
 */
public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * Called when the activity is first created. This method initializes the user interface
     * and sets up event listeners for buttons and the theme spinner.
     *
     * @param savedInstanceState The previously saved instance state, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration); // Set the activity layout.

        // Find the RegisterButton in the layout and set an OnClickListener.
        Button rButton = findViewById(R.id.RegisterButton);
        rButton.setOnClickListener(this);

        // Find the spinner and set up its adapter to populate it with theme options.
        Spinner spinner = findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.themes, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter1);

        // Set up a listener to capture the selected theme from the spinner.
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedTheme = spinner.getSelectedItem().toString();
                // You can use 'selectedTheme' in your code as needed.
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where nothing is selected (optional).
            }
        });

        // Find the BackButton in the layout and set an OnClickListener.
        Button backButton = findViewById(R.id.BackButton);
        backButton.setOnClickListener(this);
    }


    /**
     * Handles click events for buttons. Depending on which button is clicked,
     * this method performs the corresponding action, such as registering a user
     * or navigating back to the login screen.
     *
     * @param view The view that was clicked (e.g., RegisterButton or BackButton).
     */

    @Override
    public void onClick(View view) {
        // Implement click actions based on the clicked view (e.g., RegisterButton or BackButton).
        Intent intent;
        switch (view.getId()) {
            case R.id.RegisterButton:
                // Find various input fields and retrieve their values.
                TextView userIdText = findViewById(R.id.userIDTextBox);
                TextView userText = findViewById(R.id.usernameTextBox);
                TextView passText = findViewById(R.id.passwordTextBox);
                Spinner themeText = (Spinner) findViewById(R.id.spinner2);

                // Define error messages for invalid input.
                String userIDErrMsg = "Invalid User Id";
                String userErrMsg = "Invalid Username";
                String passErrMsg = "Invalid Password";
                String themeErrMsg = "Please select theme";

                // Retrieve input values as strings.
                String userIDStrValue = userIdText.getText().toString();
                String userStrValue = userText.getText().toString();
                String passStrValue = passText.getText().toString();
                String themeStrValue = themeText.getSelectedItem().toString();

                // Check for empty or invalid input.
                boolean a = themeStrValue.equals("Select Theme");
                boolean b = userIDStrValue.equals("");
                boolean c = userStrValue.equals("");
                boolean d = passStrValue.equals("");

                // Show error messages for invalid input.
                if (a || b || c || d) {
                    if (a) {
                        Toast.makeText(this, themeErrMsg, Toast.LENGTH_LONG).show();
                    }
                    if (b) {
                        Toast.makeText(this, userIDErrMsg, Toast.LENGTH_LONG).show();
                    }
                    if (c) {
                        Toast.makeText(this, userErrMsg, Toast.LENGTH_LONG).show();
                    }
                    if (d) {
                        Toast.makeText(this, passErrMsg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Create a UserBO object, set its attributes, and add it to the database.
                    UserBO user1 = new UserBO();
                    user1.userId = userIDStrValue;
                    user1.userName = userStrValue;
                    user1.password = passStrValue;
                    user1.userTheme = themeStrValue;
                    DatabaseHelper database1 = new DatabaseHelper(RegistrationActivity.this);
                    database1.addUSer(user1);
                    Toast.makeText(this, "User Successfully Added!", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.BackButton:
                // Navigate to the LoginActivity when the BackButton is clicked.
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
        }
    }
}
