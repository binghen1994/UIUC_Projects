package edu.uiuc.cs427app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uiuc.cs427app.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String DEFAULT_SPINNER_MESSAGE = "Pick location";

   private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private Spinner locationSpinner;
    private Button saveButton;
    private DatabaseHelper databaseHelper;
    private List<String> userLocations;
    public static final String SHARED_PREFS = "shared_prefs";
    SharedPreferences sharedpreferences;
    String userId = null;


    // Enum to indicate the current action (add or delete a location)
    enum CurrentAction {
        add, delete
    }

    // Used to indicate whether a user is adding or deleting a location from the database
    private CurrentAction currAction = MainActivity.CurrentAction.add;

    /**
     * Initializes the activity, sets up the UI, and binds listeners.
     *
     * @param savedInstanceState Previous state data (if any).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get theme preference from saved data
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String userTheme = sharedpreferences.getString("userTheme", "defaultTheme");

        // Change theme according to user choice

        if ("Dark".equals(userTheme)) {
            setTheme(R.style.Theme_Dark);
        } else {
            setTheme(R.style.Theme_Light);
        }
        setContentView(R.layout.activity_main);

        // Get Logged In User ID from session.
        userId = sharedpreferences.getString("LOGIN-ID", null);
        Log.d("MainActivity onCreate", "Log In User ID : " + userId);

        databaseHelper = new DatabaseHelper(MainActivity.this);
        populateUserLocations();

        // Initializing the UI components
        // The list of locations should be customized per user (change the implementation so that
        // buttons are added to layout
        this.setTitle(getString(R.string.app_name) + " - " + getIntent().getStringExtra("login"));

        locationSpinner = findViewById(R.id.locationSpinner);

        /**
         * Lambda function for OnClickListener of buttonAddLocation.
         * When the button is clicked, it populates the locationSpinner with available locations,
         * toggles the visibility of location UI to VISIBLE, and sets the current action to add a location.
         */
        findViewById(R.id.buttonAddLocation).setOnClickListener(v ->
                {
                    populateLocationSpinner();
                    toggleLocationUI(View.VISIBLE);
                    currAction = CurrentAction.add;
                });

        /**
         * Lambda function for OnClickListener of buttonRemoveLocation.
         * When the button is clicked, it populates the locationSpinner with existing user locations,
         * toggles the visibility of location UI to VISIBLE, and sets the current action to delete a location.
         */
        findViewById(R.id.buttonRemoveLocation).setOnClickListener(v ->
            {
                populateExistingLocationSpinner();
                toggleLocationUI(View.VISIBLE);
                currAction = CurrentAction.delete;
            });

        /**
         * Lambda function for OnClickListener of saveButton.
         * When the button is clicked, it calls the saveLocation() method to save or remove the selected location,
         * depending on the current action.
         */
        findViewById(R.id.saveButton).setOnClickListener(v -> saveLocation());

        /**
         * Lambda function for OnClickListener of buttonLogOut.
         * When the button is clicked, it navigates to the LoginActivity, and clears the session data.
         */
        findViewById(R.id.buttonLogOut).setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            // Clear Session data
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.clear();
            editor.apply();
        });

        saveButton = findViewById(R.id.saveButton);

        populateLocationSpinner();
    }


    /**
     * Populates the Spinner with the location data.
     */
    private void populateLocationSpinner() {

        List<String> availableLocations = new ArrayList<>(Arrays.asList(DEFAULT_SPINNER_MESSAGE));
        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this);
        availableLocations.addAll(databaseHelper.getCityList());

        // Remove user's already added locations from the available locations
        availableLocations.removeAll(userLocations);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, availableLocations);
        locationSpinner.setAdapter(adapter);
    }

    /**
     * Populates the Spinner with the existing locations data.
     */
    private void populateExistingLocationSpinner() {
        List<String> availableLocations = userLocations;

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, availableLocations);
        locationSpinner.setAdapter(adapter);
    }

    /**
     * Handles saving the selected location and hiding the UI elements.
     */
    private void saveLocation() {

        String selectedLocation = locationSpinner.getSelectedItem().toString();

        if (!DEFAULT_SPINNER_MESSAGE.equals(selectedLocation)) {
            if (currAction == CurrentAction.add) {
                databaseHelper.addLocation(userId, selectedLocation);
                Log.d("savedLocation", "location: " + selectedLocation);
            } else {
                databaseHelper.deleteLocation(userId, selectedLocation);
                Log.d("removedLocation", "location: " + selectedLocation);
            }
            populateUserLocations();
        }

        // Hide Spinner and Save button after saving
        toggleLocationUI(View.GONE);
    }

    /**
     * Toggles the visibility of UI elements related to adding a new location.
     *
     * @param visibility One of {@link View#VISIBLE}, {@link View#INVISIBLE}, or {@link View#GONE}.
     */
    private void toggleLocationUI(int visibility) {
        locationSpinner.setVisibility(visibility);
        saveButton.setVisibility(visibility);
    }

    /**
     * Dynamically populates the list of locations based on the user's saved locations.
     * For each location, a linear layout containing a TextView (for location name) and
     * a Button (for showing details) is added to the main layout.
     */
    private void populateUserLocations() {
        userLocations = databaseHelper.getUserLocations(userId);
        Log.d("populateUserLocations", "locations : " + userLocations);

        // Reference the container in which dynamic location entries will be added
        LinearLayout locationsContainer = findViewById(R.id.locationsContainer);

        // Clear the existing location views from the container
        locationsContainer.removeAllViews();

        // Fetch the updated list of user locations from the database
        userLocations = databaseHelper.getUserLocations(userId);

        // Iterate over the list of user locations and create dynamic entries
        for (String location : userLocations) {
            LinearLayout entry = createLocationEntry(location);
            locationsContainer.addView(entry);
        }
    }


    /**
     * Creates a LinearLayout entry representing a user location with associated details button.
     *
     * @param location The name of the location.
     * @return A LinearLayout containing a TextView for the location name and a Button to view details.
     */
    private LinearLayout createLocationEntry(String location) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(layoutParams);

        TextView textView = new TextView(this);
        textView.setText(location);
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        linearLayout.addView(textView);

        /**
         * Add Weather button
         */
        Button buttonWeather = new Button(this);
        buttonWeather.setText("Weather");
        buttonWeather.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        /**
         * Lambda function for OnClickListener of details button in each location entry.
         * When the button is clicked, it navigates to the DetailsActivity, passing the location name as an extra.
         */
        buttonWeather.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
            intent.putExtra("selectedCity", location);
            startActivity(intent);
        });
        linearLayout.addView(buttonWeather);

        /**
         * Add Map button
         */

        Button buttonMap = new Button(this);
        buttonMap.setText("Map");
        buttonMap.setTag(location);
        buttonMap.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        /**
         * Lambda function for OnClickListener of details button in each location entry.
         * When the button is clicked, it navigates to the DetailsActivity, passing the location name as an extra.
         */
        buttonMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            intent.putExtra("selectedCity", location);
            startActivity(intent);
        });
        linearLayout.addView(buttonMap);


        return linearLayout;
    }
}

