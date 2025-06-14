package edu.uiuc.cs427app;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


/**
 * The DetailsActivity class displays detailed information about a specific city's weather.
 * It allows users to switch between light and dark themes based on their preferences and
 * retrieve information about a selected city's weather.
 */
public class DetailsActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String SHARED_PREFS = "shared_prefs";
    SharedPreferences sharedpreferences;
    String userId = null;

    /**
     * Called when the activity is created. Initializes the user interface and retrieves
     * the user's theme preference and login session data.
     *
     * @param savedInstanceState The saved state of the activity (or null if no saved state is available).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get theme preference from saved data
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String userTheme = sharedpreferences.getString("userTheme", "defaultTheme");

        Log.i("userTheme", userTheme);
        // Change theme according to user choice
        if ("Dark".equals(userTheme)) {
            setTheme(R.style.Theme_Dark);;
        } else {
            setTheme(R.style.Theme_Light);;
        }

        setContentView(R.layout.activity_details);

        // Get Logged In User ID from session.
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        userId = sharedpreferences.getString("LOGIN-ID", null);
        System.out.print("Log In User ID : " + userId);

        // Set Title
        Resources res = getResources();
        String text = getString(R.string.app_name) + " - " + userId;
        this.setTitle(text);

        // Process the Intent payload that has opened this Activity and show the information accordingly
        String cityName = getIntent().getStringExtra("city").toString();
        String welcome = "Welcome to the "+cityName;
        String cityWeatherInfo = "Detailed information about the weather of "+cityName;

        // Initializing the GUI elements
        TextView welcomeMessage = findViewById(R.id.welcomeText);
        TextView cityInfoMessage = findViewById(R.id.cityInfo);

        welcomeMessage.setText(welcome);
        cityInfoMessage.setText(cityWeatherInfo);
        // Get the weather information from a Service that connects to a weather server and show the results

        Button buttonMap = findViewById(R.id.mapButton);
        buttonMap.setOnClickListener(this);

    }

    /**
     * Handles click events on buttons in the activity.
     * @param view The clicked view (button).
     */
    @Override
    public void onClick(View view) {
        //Implement this (create an Intent that goes to a new Activity, which shows the map)
    }
}

