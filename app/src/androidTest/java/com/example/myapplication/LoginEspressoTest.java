package com.example.myapplication;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.myapplication.registration.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LoginEspressoTest {

    public ActivityScenario<LoginActivity> scenario;
    FirebaseAuth auth;

    @Before
    public void setup() {
        scenario = ActivityScenario.launch(LoginActivity.class);
        auth = FirebaseAuth.getInstance();
    }

    @Test
    public void testPasswordRecoverButton() {
        // Test the "Password Recovery" button
        Espresso.onView(ViewMatchers.withId(R.id.passwordRecoverButton)).perform(ViewActions.click());

        // Verify that the PasswordRecoveryActivity is displayed
        Espresso.onView(ViewMatchers.withId(R.id.passwordReset)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testRegisterButton() {
        // Test the "Register" button
        Espresso.onView(ViewMatchers.withId(R.id.registerButton)).perform(ViewActions.click());

        // Verify that the RegisterActivity is displayed
        Espresso.onView(ViewMatchers.withId(R.id.passwordRequirement)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testLoginButtonWithEmptyFields() {
        // Click the "Login" button with empty email and password fields
        Espresso.onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click());

        // Verify that the error message is displayed
        Espresso.onView(ViewMatchers.withId(R.id.errorMessage))
                .check(ViewAssertions.matches(ViewMatchers.withText("Both email and password are required")))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testLoginButtonWithValidCredentials() {
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

        // Verify that the SuccessActivity has been opened by checking if a logout button is displayed
        Espresso.onView(ViewMatchers.withId(R.id.icon_log_out)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }


    @Test
    public void testLoginButtonWithInvalidCredentials() throws InterruptedException {
        // Enter invalid email and password, then click the "Login" button
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText("invalid@example.com"));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("invalidpassword"));
        Espresso.closeSoftKeyboard(); // Close the keyboard
        Espresso.onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click());
        Thread.sleep(2000);

        // Verify that the error message is displayed
        Espresso.onView(ViewMatchers.withId(R.id.errorMessage))
                .check(ViewAssertions.matches(ViewMatchers.withText("Login failed. Check your credentials")))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}

