package com.example.myapplication;

import static androidx.test.espresso.action.ViewActions.click;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.myapplication.registration.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

@RunWith(AndroidJUnit4.class)
public class UserListingEspressoTest {
    public ActivityScenario<LoginActivity> scenario;
    FirebaseAuth auth;

    @Before
    public void setup() {
        scenario = ActivityScenario.launch(LoginActivity.class);
        auth = FirebaseAuth.getInstance();
    }

    @Test
    public void login() {
        // Enter valid email and password, then click the "Login" button
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText("roshanplayzmc@gmail.com"));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("admin123"));
        Espresso.closeSoftKeyboard(); // Close the keyboard
        Espresso.onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click());

        // Introduce a delay wait for SuccessActivity
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Verify that the SuccessActivity is displayed by checking if the log out button exists
        Espresso.onView(ViewMatchers.withId(R.id.icon_log_out)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
    @Test
    public void testListingsDisplayed() {
        // Enter valid email and password, then click the "Login" button
        login();
        Espresso.onView(ViewMatchers.withId(R.id.navigation_listings))
                .perform(click());

        Espresso.onView(ViewMatchers.withId(R.id.rvListings)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}