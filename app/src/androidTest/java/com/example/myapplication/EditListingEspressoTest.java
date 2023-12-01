package com.example.myapplication;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;

import com.example.myapplication.registration.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

public class EditListingEspressoTest {
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
        // Verify that the SuccessActivity is displayed then click listings page
        Espresso.onView(ViewMatchers.withId(R.id.instructions)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        Espresso.onView(ViewMatchers.withId(R.id.userListing)).perform(ViewActions.click());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(ViewMatchers.withId(R.id.listingsListView)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testClickedListing() {
        login();
        Espresso.onData(Matchers.anything()) // This matches any data in the adapter
                .inAdapterView(ViewMatchers.withId(R.id.listingsListView)) // Use the ID of your ListView
                .atPosition(0) // Change this to the desired position
                .perform(ViewActions.click());

        // Introduce a delay to allow the EditDeleteListingActivity to load
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(ViewMatchers.withId(R.id.saveButton)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
