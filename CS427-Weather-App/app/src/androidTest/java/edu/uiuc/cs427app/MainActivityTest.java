package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.anything;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
    private final long sleepBetweenSteps = 2500;
    private final String testUserId = "TestUser";
    public ActivityScenario<MainActivity> activityScenario;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;


    /**
     * Initial set Up
     */
    @Before
    public void setUp() throws InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();

        sharedPreferences = context.getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);

        databaseHelper = new DatabaseHelper(context);
        databaseHelper.populateCities();

        UserBO testUser = new UserBO();
        testUser.setUserId(testUserId);
        databaseHelper.addUSer(testUser);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LOGIN-ID", testUserId);
        editor.apply();

        activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.moveToState(Lifecycle.State.RESUMED);
        Thread.sleep(sleepBetweenSteps);
    }

    /**
     * release resource once test finishes;
     */
    @After
    public void tearDown() {
        // Remove the test user from the database
        databaseHelper.deleteTestUser(testUserId);

        // Reset the shared preferences
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("LOGIN-ID");
            editor.apply();
        }

        if (activityScenario != null) {
            activityScenario.close();
        }
    }

    /**
     * Test add a location functionality
     */
    @Test
    public void testAddLocationFunctionality() throws InterruptedException {
        // Fetch initial locations for the user
        List<String> initialLocations = databaseHelper.getUserLocations(testUserId);
        assertEquals("Initial locations list should be empty", 0, initialLocations.size());

        // Add location
        onView(withId(R.id.buttonAddLocation)).perform(click());
        Thread.sleep(sleepBetweenSteps);

        // Select an item in the locationSpinner
        onView(withId(R.id.locationSpinner)).perform(click());
        Espresso.onData(anything()).atPosition(1).perform(click());
        Thread.sleep(sleepBetweenSteps);

        // Click on the Save Button
        onView(withId(R.id.saveButton)).perform(click());
        Thread.sleep(sleepBetweenSteps);

        // Fetch locations for the user after adding a new location
        List<String> updatedLocations = databaseHelper.getUserLocations(testUserId);
        assertEquals("Locations list should have one entry after adding a location", 1, updatedLocations.size());
    }

    /**
     * Test remove a location functionality
     */
    @Test
    public void testRemoveLocationFunctionality() throws InterruptedException {
        // Fetch initial locations for the user
        List<String> initialLocations = databaseHelper.getUserLocations(testUserId);
        assertEquals("Initial locations list should be empty", 0, initialLocations.size());

        // Add location
        onView(withId(R.id.buttonAddLocation)).perform(click());
        Thread.sleep(sleepBetweenSteps);

        // Select an item in the locationSpinner
        onView(withId(R.id.locationSpinner)).perform(click());
        Espresso.onData(anything()).atPosition(1).perform(click());
        Thread.sleep(sleepBetweenSteps);

        // Click on the Save Button
        onView(withId(R.id.saveButton)).perform(click());
        Thread.sleep(sleepBetweenSteps);

        // Fetch locations for the user after adding a new location
        List<String> updatedLocations = databaseHelper.getUserLocations(testUserId);
        assertEquals("Locations list should have one entry after adding a location", 1, updatedLocations.size());

        // Delete location
        onView(withId(R.id.buttonRemoveLocation)).perform(click());
        Thread.sleep(sleepBetweenSteps);

        // Click on the Save Button
        onView(withId(R.id.saveButton)).perform(click());
        Thread.sleep(sleepBetweenSteps);

        // Fetch locations for the user after deleting a location
        updatedLocations = databaseHelper.getUserLocations(testUserId);
        assertEquals("Locations list should have no entries after deleting a location", 0, updatedLocations.size());
    }
}