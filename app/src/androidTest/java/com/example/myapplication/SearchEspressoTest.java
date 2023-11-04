package com.example.myapplication;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.assertion.ViewAssertions.matches;

import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.core.StringContains.containsString;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SearchEspressoTest {

    private ActivityScenario<SearchActivity> searchScenario;

    @Before
    public void setup() {
        searchScenario = ActivityScenario.launch(SearchActivity.class);
    }

    @Test
    public void testSearchByName() {
        // Given that the user is on the SearchActivity

        // When the user enters a name (e.g., "Laptop") and initiates the search
        onView(withId(R.id.nameEditText)).perform(ViewActions.typeText("Laptop"));
        onView(withId(R.id.searchButton)).perform(ViewActions.click());

        // Then verify that the search results activity is displayed
        onView(withId(R.id.searchResultsListView)).check(matches(isDisplayed()));

        // And verify that the search results match the specified name
        onView(withId(R.id.searchResultsListView))
                .check(matches(hasDescendant(withText("Laptop"))));
    }

    @Test
    public void testSearchWithMultipleCriteria() {
        // Given that the user is on the SearchActivity

        // When the user sets various search criteria (e.g., name, condition, exchange preference) and initiates the search
        onView(withId(R.id.nameEditText)).perform(ViewActions.typeText("Laptop"));
        onView(withId(R.id.conditionSpinner)).perform(ViewActions.click());
        onView(ViewMatchers.withText("Used (Very Good)")).perform(ViewActions.click());
        onView(withId(R.id.exchangePreferenceEditText)).perform(ViewActions.typeText("TV"));
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
        // Given that the user is on the SearchActivity

        // When the user initiates a search without entering any search criteria
        onView(withId(R.id.searchButton)).perform(ViewActions.click());

        // Then verify that an error message is displayed indicating that criteria are required
        onView(withId(R.id.errorMessageTextView)).check(matches(withText("Please enter search criteria")));
    }

    @Test
    public void testNoMatchingResults() {
        // Given that the user is on the SearchActivity

        // When the user enters a name (e.g., "Laptop") and initiates the search
        onView(withId(R.id.nameEditText)).perform(ViewActions.typeText("abcdefghijklmnop"));
        onView(withId(R.id.searchButton)).perform(ViewActions.click());

        // Then verify that an error message is displayed indicating that there are no results
        onView(withId(R.id.errorMessageTextView)).check(matches(withText("No matching items found")));
    }
}
