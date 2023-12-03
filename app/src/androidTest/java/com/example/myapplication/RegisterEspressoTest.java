package com.example.myapplication;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.example.myapplication.registration.RegisterActivity;
import com.google.firebase.auth.FirebaseAuth;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterEspressoTest {

    public ActivityScenario<RegisterActivity> scenario;
    FirebaseAuth auth;

    @Before
    public void setup() {
        scenario = ActivityScenario.launch(RegisterActivity.class);
        auth = FirebaseAuth.getInstance();
    }

    @Test
    public void testEmailAlreadyInUse() throws InterruptedException {
        // Assign the user to the same email and test
        onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText("existinguser@example.com"));
        onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("password123"));
        onView(ViewMatchers.withId(R.id.confirmPassword)).perform(ViewActions.typeText("password123"));
        onView(ViewMatchers.withId(R.id.firstName)).perform(ViewActions.typeText("John"));
        onView(ViewMatchers.withId(R.id.lastName)).perform(ViewActions.typeText("Doe"));
        Espresso.closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.registerButton)).perform(ViewActions.click());
        Thread.sleep(2000);  // Sleep for 2 seconds

        // Check for an error message indicating that the email is already in use
        onView(withId(R.id.errorMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.errorMessage)).check(matches(withText("Email is already in use!")));
    }

    @Test
    public void testIncompleteRegistration() {
        // Attempt to register with incomplete data
        onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText("test@example.com"));
        onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("password123"));
        onView(ViewMatchers.withId(R.id.confirmPassword)).perform(ViewActions.typeText("password123"));
        Espresso.closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.registerButton)).perform(ViewActions.click());

        // Check for the error message directly in the UI
        onView(withId(R.id.errorMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.errorMessage)).check(matches(withText("Please fill out all fields!")));
    }

    @Test
    public void testInvalidEmailFormat() {
        // Enter an invalid email format and attempt registration
        onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText("invalid.email"));
        onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("password123"));
        onView(ViewMatchers.withId(R.id.confirmPassword)).perform(ViewActions.typeText("password123"));
        onView(ViewMatchers.withId(R.id.firstName)).perform(ViewActions.typeText("John"));
        onView(ViewMatchers.withId(R.id.lastName)).perform(ViewActions.typeText("Doe"));
        Espresso.closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.registerButton)).perform(ViewActions.click());

        // Check for the error message directly in the UI
        onView(withId(R.id.errorMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.errorMessage)).check(matches(withText("Please enter a valid email address!")));
    }

    @Test
    public void testPasswordsDoNotMatch() {
        // Enter valid registration data but with mismatched passwords
        onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText("test@example.com"));
        onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("password123"));
        onView(ViewMatchers.withId(R.id.confirmPassword)).perform(ViewActions.typeText("password456"));
        onView(ViewMatchers.withId(R.id.firstName)).perform(ViewActions.typeText("John"));
        onView(ViewMatchers.withId(R.id.lastName)).perform(ViewActions.typeText("Doe"));
        Espresso.closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.registerButton)).perform(ViewActions.click());

        // Check for the error message directly in the UI
        onView(withId(R.id.errorMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.errorMessage)).check(matches(withText("Passwords do not match!")));
    }

    @Test
    public void testShortPassword() {
        // Enter valid registration data with a short password
        onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText("test@example.com"));
        onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("pass"));
        onView(ViewMatchers.withId(R.id.confirmPassword)).perform(ViewActions.typeText("pass"));
        onView(ViewMatchers.withId(R.id.firstName)).perform(ViewActions.typeText("John"));
        onView(ViewMatchers.withId(R.id.lastName)).perform(ViewActions.typeText("Doe"));
        Espresso.closeSoftKeyboard();
        onView(ViewMatchers.withId(R.id.registerButton)).perform(ViewActions.click());

        // Check for the error message directly in the UI
        onView(withId(R.id.errorMessage)).check(matches(isDisplayed()));
        onView(withId(R.id.errorMessage)).check(matches(withText("Password must be at least 6 characters!")));
    }
}
