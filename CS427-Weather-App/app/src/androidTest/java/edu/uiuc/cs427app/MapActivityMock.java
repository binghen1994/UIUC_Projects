package edu.uiuc.cs427app;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4.class)
public class MapActivityMock {
    private final long sleepBetweenSteps = 2500;
    public ActivityScenario<MapActivity> activityScenario;
    private DatabaseHelper databaseHelper;
    private final String testUserId = "TestUser";
    private SharedPreferences sharedPreferences;
    private String cityName = "Chicago";

    @Before
    public void setUp() throws InterruptedException {

        // Initialize the Intent for the initial launch with "Chicago"
        Intent mockIntent = mock(Intent.class);
        mockIntent.setClass(ApplicationProvider.getApplicationContext(), MapActivity.class);
        // Set up initial behavior for the Intent
        mockIntent.putExtra("selectedCity", cityName);

        // Launch the initial activity
        activityScenario = ActivityScenario.launch(mockIntent);

        Thread.sleep(sleepBetweenSteps);
    }

    @Test
    public void testMapLocationName() throws InterruptedException {
        // Use Espresso to verify the city's name is displayed on the TextView with id R.id.city_info_text
        onView(withId(R.id.city_info_text))
                .check(matches(withText(cityName)))
                .withFailureHandler((error, viewMatcher) -> {
                    throw new AssertionError("Map location does not match the chosen city: " + cityName);
                });

        Thread.sleep(sleepBetweenSteps);

        // Close the initial activity
        activityScenario.close();

        // Set up mock Intent for the second launch with "Los Angeles"
        Intent mockIntent2 = mock(Intent.class);

        // Set up initial behavior for the mockIntent2
        String cityName2 = "Los Angeles";
        when(mockIntent2.getStringExtra("selectedCity")).thenReturn(cityName2);

        // Launch the second activity
        activityScenario = ActivityScenario.launch(MapActivity.class, mockIntent2.getExtras());

        Thread.sleep(sleepBetweenSteps);
    }


}