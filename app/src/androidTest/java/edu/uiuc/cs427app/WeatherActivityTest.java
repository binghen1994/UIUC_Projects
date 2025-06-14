package edu.uiuc.cs427app;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.not;

import java.util.regex.Pattern;


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
public class WeatherActivityTest {
    private final long sleepBetweenSteps = 2500;
    public ActivityScenario<WeatherActivity> activityScenario;
    private DatabaseHelper databaseHelper;
    private final String testUserId = "TestUser";
    private SharedPreferences sharedPreferences;
    private String cityName = "Chicago";


    /**
     * Initial set Up
     */
    @Before
    public void setUp() throws InterruptedException {

        // set up database
        Context context = getApplicationContext();

        sharedPreferences = context.getSharedPreferences(WeatherActivity.SHARED_PREFS, Context.MODE_PRIVATE);

        databaseHelper = new DatabaseHelper(context);
        databaseHelper.populateCities();

        UserBO testUser = new UserBO();
        testUser.setUserId(testUserId);
        databaseHelper.addUSer(testUser);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("LOGIN-ID", testUserId);
        editor.apply();

        // launch first intent with city name Chicago
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), WeatherActivity.class);
        intent.putExtra("selectedCity", cityName);
        activityScenario = ActivityScenario.launch(intent);

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
     * Test weather name is same with the selected city
     */

    @Test
    public void testWeather() throws InterruptedException {

        // Use Espresso to verify the first city's name is displayed on the TextView
        onView(withId(R.id.cityDescription))
                .check(matches(withText(cityName)))
                .withFailureHandler((error, viewMatcher) -> {
                    throw new AssertionError("Location does not match the chosen city: " + cityName);
                });

        Thread.sleep(sleepBetweenSteps);

        onView(withId(R.id.date_timeDescription))
                .check(matches(notNullValue()))
                .withFailureHandler((error, viewMatcher) -> {
                    throw new AssertionError("Date and/or Time are invalid");
                });

        Thread.sleep(sleepBetweenSteps);

        onView(withId(R.id.humidityDescription))
                .check(matches(notNullValue()))
                .withFailureHandler((error, viewMatcher) -> {
                    throw new AssertionError("Humidity is invalid");
                });

        Thread.sleep(sleepBetweenSteps);

        onView(withId(R.id.temperatureDescription))
                .check(matches(notNullValue()))
                .withFailureHandler((error, viewMatcher) -> {
                    throw new AssertionError("Temperature is invalid");
                });

        Thread.sleep(sleepBetweenSteps);

        onView(withId(R.id.weatherDescription))
                .check(matches(notNullValue()))
                .withFailureHandler((error, viewMatcher) -> {
                    throw new AssertionError("Weather Description is invalid");
                });

        Thread.sleep(sleepBetweenSteps);

        onView(withId(R.id.windDescription))
                .check(matches(notNullValue()))
                .withFailureHandler((error, viewMatcher) -> {
                    throw new AssertionError("Wind Description is invalid");
                });

        Thread.sleep(sleepBetweenSteps);



        // Close first intent and launch second test city with name Los Angeles
        activityScenario.close();
        String cityName2 = "Los Angeles";

        Intent intent2 = new Intent(ApplicationProvider.getApplicationContext(), WeatherActivity.class);
        intent2.removeExtra("selectedCity");
        intent2.putExtra("selectedCity", cityName2);
        ActivityScenario.launch(intent2);

        Thread.sleep(sleepBetweenSteps);

        // Test after the intent change the name of second city is correctly shown
        onView(withId(R.id.cityDescription))
                .check(matches(not(withText(cityName))))
                .withFailureHandler((error, viewMatcher) -> {
                    throw new AssertionError("Location2 is correctly mocked: " + cityName);
                });

        // Use Espresso to verify the second city's name is displayed on the TextView with id R.id.cityDescription
        onView(withId(R.id.cityDescription))
                .check(matches(withText(cityName2)))
                .withFailureHandler((error, viewMatcher) -> {
                    throw new AssertionError("Location does not match the chosen city: " + cityName2);
                });

        Thread.sleep(sleepBetweenSteps);

        // test time and date is coorectly shown
        onView(withId(R.id.date_timeDescription))
                .check(matches(notNullValue()))
                .withFailureHandler((error, viewMatcher) -> {
                    throw new AssertionError("Date and/or Time are invalid");
                });

        Thread.sleep(sleepBetweenSteps);
        
        // test humidity is coorectly shown
        onView(withId(R.id.humidityDescription))
                .check(matches(notNullValue()))
                .withFailureHandler((error, viewMatcher) -> {
                    throw new AssertionError("Humidity is invalid");
                });

        Thread.sleep(sleepBetweenSteps);

        // test temperature is coorectly shown
        onView(withId(R.id.temperatureDescription))
                .check(matches(notNullValue()))
                .withFailureHandler((error, viewMatcher) -> {
                    throw new AssertionError("Temperature is invalid");
                });

        Thread.sleep(sleepBetweenSteps);
        
         // test weatherDescription is coorectly shown
        onView(withId(R.id.weatherDescription))
                .check(matches(notNullValue()))
                .withFailureHandler((error, viewMatcher) -> {
                    throw new AssertionError("Weather Description is invalid");
                });

        Thread.sleep(sleepBetweenSteps);

        // test windDescription is coorectly shown
        onView(withId(R.id.windDescription))
                .check(matches(notNullValue()))
                .withFailureHandler((error, viewMatcher) -> {
                    throw new AssertionError("Wind Description is invalid");
                });

        Thread.sleep(sleepBetweenSteps);
    }
}
