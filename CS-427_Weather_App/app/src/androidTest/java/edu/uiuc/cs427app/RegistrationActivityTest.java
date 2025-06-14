package edu.uiuc.cs427app;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class RegistrationActivityTest {

    private ActivityScenario<RegistrationActivity> activityScenario;

    /**
     * Initial set Up
     */
    
    @Before
    public void setUp() {
        activityScenario = ActivityScenario.launch(RegistrationActivity.class);
    }

    /**
     * release resource once test finishes;
     */
    
    @After
    public void tearDown() {
        if (activityScenario != null) {
            activityScenario.close();
        }
    }
     /**
     * Test if the registration is successful
     */
    @Test
    public void testRegistrationSuccess() {

        onView(withId(R.id.userIDTextBox)).perform(typeText("ValidUser"), closeSoftKeyboard());
        onView(withId(R.id.usernameTextBox)).perform(typeText("ValidUsername"), closeSoftKeyboard());
        onView(withId(R.id.passwordTextBox)).perform(typeText("ValidPassword"), closeSoftKeyboard());
        onView(withId(R.id.spinner2)).perform(click());
        onView(withText("Dark")).perform(click());
        onView(withId(R.id.RegisterButton)).perform(click());
        
        DatabaseHelper databaseHelper = new DatabaseHelper(InstrumentationRegistry.getInstrumentation().getTargetContext());
        UserBO registeredUser = databaseHelper.getUSer("ValidUser", "ValidPassword");
        assertNotNull("Registered user should not be null", registeredUser);
    }


}
