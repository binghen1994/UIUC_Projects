package edu.uiuc.cs427app;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({

        // List of Test classes
        RegistrationActivityTest.class,
        WeatherActivityTest.class,
        LoginTest.class,
        LogOutTest.class,
        MainActivityTest.class,
        MapActivityTest.class,
        MockLocationTest.class
        })

public class AllTest {
        // Run all Test together.
}
