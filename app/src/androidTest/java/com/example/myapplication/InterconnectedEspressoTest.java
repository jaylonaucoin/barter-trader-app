package com.example.myapplication;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class InterconnectedEspressoTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testProviderNavigation() throws InterruptedException {
        // Login
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .perform(ViewActions.typeText("roshanplayzmc@gmail.com"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.typeText("admin123"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.loginButton))
                .perform(click());

        Thread.sleep(2000);

        // Navigate through bottom navigation bar
        Espresso.onView(ViewMatchers.withId(R.id.navigation_chat))
                .perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.navigation_post))
                .perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.navigation_listings))
                .perform(click());

        // Interact with user profile and address icons
        Espresso.onView(ViewMatchers.withId(R.id.icon_user_profile))
                .perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.closeButton))
                .perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.icon_address))
                .perform(click());
        Espresso.onView(withContentDescription("Navigate up")).perform(click());
        // Logout process
        Espresso.onView(ViewMatchers.withId(R.id.icon_log_out))
                .perform(click());

        Thread.sleep(1000);

        Espresso.onView(withText("Yes"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        // Check if we are back at LoginActivity
        Espresso.onView(ViewMatchers.withId(R.id.loginButton))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testReceiverNavigation() throws InterruptedException {
        // Login
        Espresso.onView(ViewMatchers.withId(R.id.email))
                .perform(ViewActions.typeText("ros@gmail.com"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.typeText("admin123"), ViewActions.closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.loginButton))
                .perform(click());

        Thread.sleep(2000);

        // Navigate through bottom navigation bar
        Espresso.onView(ViewMatchers.withId(R.id.navigation_chat))
                .perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.navigation_search))
                .perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.navigation_listings))
                .perform(click());

        // Interact with user profile and address icons
        Espresso.onView(ViewMatchers.withId(R.id.icon_user_profile))
                .perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.closeButton))
                .perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.icon_address))
                .perform(click());
        Espresso.onView(withContentDescription("Navigate up")).perform(click());
        // Logout process
        Espresso.onView(ViewMatchers.withId(R.id.icon_log_out))
                .perform(click());

        Thread.sleep(1000);

        Espresso.onView(withText("Yes"))
                .inRoot(isDialog())
                .check(matches(isDisplayed()))
                .perform(click());

        // Check if we are back at LoginActivity
        Espresso.onView(ViewMatchers.withId(R.id.loginButton))
                .check(matches(isDisplayed()));
    }
}

