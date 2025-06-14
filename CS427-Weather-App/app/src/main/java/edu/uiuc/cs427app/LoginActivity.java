package edu.uiuc.cs427app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;


/**
 * The LoginActivity class represents the user login screen and is responsible for handling user authentication.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private AppBarConfiguration appBarConfiguration;
    public static final String SHARED_PREFS = "shared_prefs";
    SharedPreferences sharedpreferences;
    /**
     * Called when the activity is created. Initializes the user interface and sets up event listeners.
     *
     * @param savedInstanceState The saved state of the activity (or null if no saved state is available).
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button loginButton = findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(this);

        Button signUpButton = findViewById(R.id.SignUpButton);
        signUpButton.setOnClickListener(this);
    }

    /**
     * Handles click events on buttons in the activity.
     *
     * @param view The clicked view (button).
     */
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.LoginButton:

                // Initialize and build the database
                buildDataBase();

                TextView loginText = findViewById(R.id.loginTextBox);
                TextView passwordText = findViewById(R.id.passwordTextBox);


                String loginErrMsg = "Invalid User Id or Password.";
                String loginEmpty = "Please enter login details";
                String passwordEmpty = "Please enter password details";
                // Initializing the GUI elements
                TextView loginErrTxt = findViewById(R.id.LoginErrorText);
                String loginStrValue = loginText.getText().toString();
                String passwordStrValue = passwordText.getText().toString();

                UserBO userBO = getUser(loginStrValue, passwordStrValue);
                String userTheme = userBO.getUserTheme();
                // Validate user from USer Table in DB
                if(loginStrValue.isEmpty()){
                    loginErrTxt.setText(loginEmpty);
                } else if (passwordStrValue.isEmpty()) {
                    loginErrTxt.setText(passwordEmpty);
                } else if(!loginStrValue.equals(userBO.getUserId())){
                    loginErrTxt.setText(loginErrMsg);
                } else{
                    loginErrTxt.setText("");
                    intent = new Intent(this, MainActivity.class);
                    intent.putExtra("login", loginStrValue);
                    intent.putExtra("password", passwordStrValue);
                    startActivity(intent);

                    // configure shared preferences and save user id for future use.
                    sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("LOGIN-ID", loginStrValue);
                    editor.putString("userTheme", userTheme);
                    editor.apply();

                }
                break;

            case R.id.SignUpButton:
                intent = new Intent(this, RegistrationActivity.class);
                startActivity(intent);
                break;
        }

    }

    /**
     * Get User Details based on user id and password from Database.
     *
     * @param loginStrValue    The user ID.
     * @param passwordStrValue The user's password.
     * @return A UserBO object containing the user's details.
     */
    // Get User Details based on user id and password from Database
    private  UserBO getUser(String loginStrValue, String passwordStrValue) {
        DatabaseHelper databaseHelper = new DatabaseHelper(LoginActivity.this);
        UserBO userBO = databaseHelper.getUSer(loginStrValue, passwordStrValue);
        return userBO;
    }
    /**
     * Build or Initialize the database with two test users: "Test" and "Test1".
     */
    // Build or Initialize Data base with two user - Test and Test1
    private void buildDataBase() {
        UserBO userBO = getUser("Test","Test");
        if(userBO == null || userBO.getUserId() == null || userBO.getUserId().isEmpty()){
            addTestUSer("Test","Dark");
            addTestUSer("Test1","Light");
        }else{
            deleteTestUser();
            addTestUSer("Test","Dark");
            addTestUSer("Test1","Light");
        }

    }


    // Add a user to DB for database initialization

    /**
     * Add a test user to the database for database initialization.
     *
     * @param userId The user ID.
     * @param theme  The user's selected theme.
     */
    private void addTestUSer(String userId, String theme) {
        DatabaseHelper databaseHelper = new DatabaseHelper(LoginActivity.this);
        UserBO userBO = new UserBO();

        try {
            userBO.setUserId(userId);
            userBO.setPassword("Test");
            userBO.setUserName("Test");
            userBO.setUserTheme(theme);
            boolean success = databaseHelper.addUSer(userBO);
            System.out.println("INSERT Result " + success);

        }catch (Exception ex){
            System.err.println("Error Creating User " + ex.getMessage());
        }
        databaseHelper.addTestUSerLocMapping();
        databaseHelper.populateCities();
    }
    /**
     * Clean the database to remove test data during initialization.
     */
    // CLean Data base to remove Test data at initialtion
    private void deleteTestUser(){
        DatabaseHelper databaseHelper = new DatabaseHelper(LoginActivity.this);
        databaseHelper.deleteTestUser("Test");
        databaseHelper.deleteTestUser("Test1");
    }


}
