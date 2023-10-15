package com.example.myapplication;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4ClassRunner.class)
public class SuccessEspressoTest {

    public ActivityScenario<LoginActivity> scenario;
    FirebaseAuth auth;

    @Before
    public void setup() {
        scenario = ActivityScenario.launch(LoginActivity.class);
        auth = FirebaseAuth.getInstance();
    }

    @Test
    public void testWelcomeMessageDisplayed() {
        // Enter valid email and password, then click the "Login" button
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText("connormacintyre14@gmail.com"));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("connor123"));
        Espresso.closeSoftKeyboard(); // Close the keyboard
        Espresso.onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click());

        // Introduce a delay (e.g., 3 seconds) to wait for SuccessActivity
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the welcome message is displayed on the SuccessActivity
        Espresso.onView(ViewMatchers.withId(R.id.welcomeMessage))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testLogoutButton() {
        // Enter valid email and password, then click the "Login" button
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText("connormacintyre14@gmail.com"));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("connor123"));
        Espresso.closeSoftKeyboard(); // Close the keyboard
        Espresso.onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click());

        // Introduce a delay to wait for SuccessActivity
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click the "Logout" button and verify that the user is logged out
        Espresso.onView(ViewMatchers.withId(R.id.logoutButton)).perform(ViewActions.click());

        // Introduce a delay to wait for LoginActivity
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Verify that the LoginActivity is displayed
        Espresso.onView(ViewMatchers.withId(R.id.loginButton)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
