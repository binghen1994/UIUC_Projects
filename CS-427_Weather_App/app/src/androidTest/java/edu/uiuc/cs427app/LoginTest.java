package edu.uiuc.cs427app;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
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
public class LoginTest {

    // Add a sleep for delay
    private final long sleepBetweenSteps = 2500;
    public ActivityScenario<LoginActivity> activityScenario;

    SharedPreferences sharedpreferences;
    String userId = null;

    // Test Data
    public static final String CORRECT_USERID_TO_BE_TYPED = "Test";
    public static final String CORRECT_PASSWORD_TO_BE_TYPED = "Test";
    public static final String WRONG_USERID_TO_BE_TYPED = "Invalid";
    public static final String WRONG_PASSWORD_TO_BE_TYPED = "Invalid";



    /**
     * Initial set Up for Test
     */
    @Before
    public void setUp() throws InterruptedException {
        Context context = ApplicationProvider.getApplicationContext();

        // get the shared preference
        sharedpreferences = context.getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE);

        activityScenario = ActivityScenario.launch(LoginActivity.class);
        activityScenario.moveToState(Lifecycle.State.RESUMED);
        Thread.sleep(sleepBetweenSteps);
    }

    /**
     *  close resource once test completes
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
     * Test Invalid login
     */
    @Test
    public void testUserErrorLogin() throws InterruptedException {

        // login should not exist in session
        userId = sharedpreferences.getString("LOGIN-ID", null);
        assertNull(userId);

        // Type wrong user name and password and click on login button
        onView(withId(R.id.loginTextBox)).perform(typeText(WRONG_USERID_TO_BE_TYPED),
                closeSoftKeyboard());
        Thread.sleep(sleepBetweenSteps);

        onView(withId(R.id.passwordTextBox)).perform(typeText(WRONG_PASSWORD_TO_BE_TYPED),
                closeSoftKeyboard());
        Thread.sleep(sleepBetweenSteps);

        onView(withId(R.id.LoginButton)).perform(click());
        Thread.sleep(sleepBetweenSteps);

        // login should not exist
        userId = sharedpreferences.getString("LOGIN-ID", null);
        assertNotEquals("Login should not be equal to Test and must be null",WRONG_USERID_TO_BE_TYPED,userId);
        assertNull(userId);
    }

    /**
     * Test successful login
     */
    @Test
    public void testUserSuccessLogin() throws InterruptedException {

        // login should not exist in session
        userId = sharedpreferences.getString("LOGIN-ID", null);
        assertNull(userId);

        // Type correct user name and password and click on login button
        onView(withId(R.id.loginTextBox)).perform(typeText(CORRECT_USERID_TO_BE_TYPED),
                closeSoftKeyboard());
        Thread.sleep(sleepBetweenSteps);

        onView(withId(R.id.passwordTextBox)).perform(typeText(CORRECT_PASSWORD_TO_BE_TYPED),
                closeSoftKeyboard());
        Thread.sleep(sleepBetweenSteps);

        onView(withId(R.id.LoginButton)).perform(click());
        Thread.sleep(sleepBetweenSteps);

        // check entered login is same as what is present in login session
        userId = sharedpreferences.getString("LOGIN-ID", null);
        assertEquals("Login Name should be Equal",CORRECT_USERID_TO_BE_TYPED,userId) ;

    }
}
