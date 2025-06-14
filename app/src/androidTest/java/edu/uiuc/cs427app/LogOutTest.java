package edu.uiuc.cs427app;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LogOutTest {

    // Add a sleep for delay
    private final long sleepBetweenSteps = 2500;
    public ActivityScenario<MainActivity> activityScenario;
    private DatabaseHelper databaseHelper;
    SharedPreferences sharedpreferences;
    String userId = null;

    // Test Data
    private final String testUserId = "TestUser";

    /**
     * Initial set Up for Test
     * @throws InterruptedException
     */
    @Before
    public void setUp() throws InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();
        databaseHelper = new DatabaseHelper(context);
        databaseHelper.populateCities();

        // get the shared preference
        sharedpreferences = context.getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);

        UserBO testUser = new UserBO();
        testUser.setUserId(testUserId);
        databaseHelper.addUSer(testUser);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("LOGIN-ID", testUserId);
        editor.apply();

        activityScenario = ActivityScenario.launch(MainActivity.class);
        activityScenario.moveToState(Lifecycle.State.RESUMED);
        Thread.sleep(sleepBetweenSteps);
    }

    /**
     * close resource once test completes
     */
    @After
    public void tearDown() {

        // Reset the shared preferences
        if (sharedpreferences != null) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.remove("LOGIN-ID");
            editor.apply();
        }

        if (activityScenario != null) {
            activityScenario.close();
        }
    }

    /**
     * Test log out functionality
     * @throws InterruptedException
     */
    @Test
    public void testLogOut() throws InterruptedException {
        // login should exist in session
        userId = sharedpreferences.getString("LOGIN-ID", null);
        assertNotNull(userId);

        // Click on Log Out button on Main Page
        onView(withId(R.id.buttonLogOut)).perform(click());
        Thread.sleep(sleepBetweenSteps);

        // login should not exist in session
        userId = sharedpreferences.getString("LOGIN-ID", null);
        assertNull(userId);

    }
}
