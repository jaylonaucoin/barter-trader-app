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
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.core.StringContains.containsString;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SearchEspressoTest {

    @Before
    public void setup() {
        ActivityScenario<LoginActivity> searchScenario = ActivityScenario.launch(LoginActivity.class);

        // Enter valid email and password, then click the "Login" button
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText("ros@gmail.com"));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("admin123"));
        Espresso.closeSoftKeyboard(); // Close the keyboard
        Espresso.onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click());

        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Espresso.onView(ViewMatchers.withId(R.id.navigation_search))
                .perform(click());
    }

    @Test
    public void testSearchByName() {
        // When the user enters a name (e.g., "Laptop") and initiates the search
        onView(withId(R.id.nameEditText)).perform(ViewActions.typeText("Laptop"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.searchButton)).perform(ViewActions.click());

        // Then verify that the search results activity is displayed
        onView(withId(R.id.searchResultsListView)).check(matches(isDisplayed()));

        // And verify that the search results match the specified name
        onView(withId(R.id.searchResultsListView))
                .check(matches(hasDescendant(withText("Laptop"))));
    }

    @Test
    public void testSearchByExchangePreference() {
        // When the user enters an exchange preference (e.g., "TV") and initiates the search
        onView(withId(R.id.exchangePreferenceEditText)).perform(ViewActions.typeText("TV"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.searchButton)).perform(ViewActions.click());

        // Then verify that the search results activity is displayed
        onView(withId(R.id.searchResultsListView)).check(matches(isDisplayed()));

        // And verify that the search results match the specified exchange preference
        onView(withId(R.id.searchResultsListView))
                .check(matches(hasDescendant(withText(containsString("TV")))));
    }

    @Test
    public void testSearchByCondition() {
        // When the user enters a condition (e.g., "Used (Very Good)") and initiates the search
        onView(withId(R.id.conditionSpinner)).perform(ViewActions.click());
        onView(ViewMatchers.withText("Used (Very Good)")).perform(ViewActions.click());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.searchButton)).perform(ViewActions.click());

        // Then verify that the search results activity is displayed
        onView(withId(R.id.searchResultsListView)).check(matches(isDisplayed()));

        // And verify that the search results match the specified condition
        onView(withId(R.id.searchResultsListView))
                .check(matches(hasDescendant(withText(containsString("Used (Very Good)")))));
    }

    @Test
    public void testSearchByCategory() {
        // When the user enters a category (e.g., "Electronics") and initiates the search
        onView(withId(R.id.categorySpinner)).perform(ViewActions.click());
        onView(ViewMatchers.withText("Electronics")).perform(ViewActions.click());
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.searchButton)).perform(ViewActions.click());

        // Then verify that the search results activity is displayed
        onView(withId(R.id.searchResultsListView)).check(matches(isDisplayed()));

        // And verify that the search results match the specified category
        onView(withId(R.id.searchResultsListView))
                .check(matches(hasDescendant(withText(containsString("Electronics")))));
    }

    @Test
    public void testSearchWithMultipleCriteria() {
        // When the user sets various search criteria (e.g., name, condition, exchange preference) and initiates the search
        onView(withId(R.id.nameEditText)).perform(ViewActions.typeText("Laptop"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.conditionSpinner)).perform(ViewActions.click());
        onView(ViewMatchers.withText("Used (Very Good)")).perform(ViewActions.click());
        onView(withId(R.id.exchangePreferenceEditText)).perform(ViewActions.typeText("TV"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.searchButton)).perform(ViewActions.click());

        // Then verify that the search results activity is displayed
        onView(withId(R.id.searchResultsListView)).check(matches(isDisplayed()));

        // And verify that the search results match the specified criteria
        onView(withId(R.id.searchResultsListView))
                .check(matches(hasDescendant(withText("Laptop"))));

        onView(withId(R.id.searchResultsListView))
                .check(matches(hasDescendant(withText(containsString("Used (Very Good)")))));

        onView(withId(R.id.searchResultsListView))
                .check(matches(hasDescendant(withText(containsString("TV")))));
    }


    @Test
    public void testEmptySearch() {
        // When the user initiates a search without entering any search criteria
        onView(withId(R.id.searchButton)).perform(ViewActions.click());

        // Then verify that an error message is displayed indicating that criteria are required
        onView(withId(R.id.errorMessageTextView)).check(matches(withText("Please enter search criteria")));
    }

    @Test
    public void testNoMatchingResults() {
        // When the user enters a name (e.g., "Laptop") and initiates the search
        onView(withId(R.id.nameEditText)).perform(ViewActions.typeText("abcdefghijklmnop"));
        Espresso.closeSoftKeyboard();
        onView(withId(R.id.searchButton)).perform(ViewActions.click());

        // Then verify that an error message is displayed indicating that there are no results
        onView(withId(R.id.errorMessageTextView)).check(matches(withText("No matching items found")));
    }
}
