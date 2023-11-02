package com.example.myapplication;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

import com.example.myapplication.R;
import com.example.myapplication.SearchActivity;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class SearchEspressoTest {

    public ActivityScenario<SearchActivity> scenario;

    @Before
    public void setup() {
        scenario = ActivityScenario.launch(SearchActivity.class);
    }

    @Test
    public void testSearchByProductType() {
        // Given that the user is on the SearchActivity
        // Perform any necessary setup steps

        // When the user enters a product type (e.g., "Laptop") and initiates the search
        Espresso.onView(ViewMatchers.withId(R.id.productTypeEditText)).perform(ViewActions.typeText("Laptop"));
        Espresso.onView(ViewMatchers.withId(R.id.searchButton)).perform(ViewActions.click());

        // Then verify that the search results activity is displayed
        Espresso.onView(ViewMatchers.withId(R.id.searchResultsActivity)).check(matches(ViewMatchers.isDisplayed()));

        // And verify that the search results match the specified product type
        Espresso.onView(ViewMatchers.withId(R.id.searchResultsList))
                .check(matches(hasDescendant(ViewMatchers.withText("Laptop"))));
    }

    @Test
    public void testSearchWithMultipleCriteria() {
        // Given that the user is on the SearchActivity
        // Perform any necessary setup steps

        // When the user sets various search criteria (e.g., product type, location, price range) and initiates the search
        Espresso.onView(ViewMatchers.withId(R.id.productTypeEditText)).perform(ViewActions.typeText("Electronics"));
        Espresso.onView(ViewMatchers.withId(R.id.locationEditText)).perform(ViewActions.typeText("Local Area"));
        Espresso.onView(ViewMatchers.withId(R.id.searchButton)).perform(ViewActions.click());

        // Then verify that the search results activity is displayed
        Espresso.onView(ViewMatchers.withId(R.id.searchResultsActivity)).check(matches(ViewMatchers.isDisplayed()));

        // And verify that the search results match the specified criteria
        Espresso.onView(withId(R.id.searchResultsList))
                .check(ViewAssertions.matches(hasDescendant(withText("Laptop"))))
                    .check(ViewAssertions.matches(hasDescendant(withText("Electronics"))));
    }

    @Test
    public void testEmptySearch() {
        // Given that the user is on the SearchActivity
        // Perform any necessary setup steps

        // When the user initiates a search without entering any search criteria
        Espresso.onView(ViewMatchers.withId(R.id.searchButton)).perform(ViewActions.click());

        // Then verify that an error message is displayed indicating that criteria are required
        Espresso.onView(ViewMatchers.withId(R.id.errorMessageTextView)).check(matches(ViewMatchers.withText("Please enter search criteria")));
    }
}
