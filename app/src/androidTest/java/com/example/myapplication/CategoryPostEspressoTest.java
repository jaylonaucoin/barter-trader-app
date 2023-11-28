package com.example.myapplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CategoryPostEspressoTest {
    public ActivityScenario<LoginActivity> scenario;
    FirebaseAuth auth;

    @Before
    public void setup() {
        scenario = ActivityScenario.launch(LoginActivity.class);
        auth = FirebaseAuth.getInstance();
    }

    @Before
    public void loginUser() {
        onView(withId(R.id.email)).perform(typeText("roshanplayzmc@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("admin123"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        // Add delay or IdlingResource to ensure login process completes before subsequent tests
        try {
            Thread.sleep(2000);  // wait for 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Before
    public void clickPost() {
        onView(withId(R.id.postGoodsButton)).perform(click());

        // Add delay or IdlingResource to ensure login process completes before subsequent tests
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(ViewMatchers.withId(R.id.condition)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void selectCategoryClothing() {


        // Perform actions to select a different category (for example, Clothing)
        onView(withId(R.id.category)).perform(click());

        // Check if the selected category is displayed or not
        Espresso.onView(ViewMatchers.withId(R.id.category))
                .check(ViewAssertions.matches(ViewMatchers.withText("Furniture")));
    }

}