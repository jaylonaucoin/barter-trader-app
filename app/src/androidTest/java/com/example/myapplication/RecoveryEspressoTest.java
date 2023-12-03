package com.example.myapplication;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.example.myapplication.registration.PasswordRecoveryActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(AndroidJUnit4ClassRunner.class)
public class RecoveryEspressoTest {

    public ActivityScenario<PasswordRecoveryActivity> scenario;
    FirebaseAuth auth;

    @Before
    public void setup() {
        scenario = ActivityScenario.launch(PasswordRecoveryActivity.class);
        auth = FirebaseAuth.getInstance();
    }

    @Test
    public void testLoginButton() {
        // Test the login button
        Espresso.onView(ViewMatchers.withId(R.id.loginReturnButton)).perform(ViewActions.click());

        // Verify that the LoginActivity is displayed
        Espresso.onView(ViewMatchers.withId(R.id.registerText)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testResetPasswordButtonWithEmptyEmail() {
        // Test the "Reset Password" button with an empty email
        Espresso.onView(ViewMatchers.withId(R.id.sendResetLinkButton)).perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.errorMessage))
                .check(ViewAssertions.matches(ViewMatchers.withText("Please enter your email address")))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }


    @Test
    public void testResetPasswordButtonWithValidEmail() {
        // Test the "Reset Password" button with a valid email
        Espresso.onView(ViewMatchers.withId(R.id.emailEditText)).perform(ViewActions.typeText("test@example.com"));
        Espresso.closeSoftKeyboard();
        Espresso.onView(ViewMatchers.withId(R.id.sendResetLinkButton)).perform(ViewActions.click());
    }
}
