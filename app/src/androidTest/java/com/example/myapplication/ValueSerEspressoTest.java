package com.example.myapplication;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ValueSerEspressoTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.myapplication", appContext.getPackageName());
    }

    //work
    public ActivityScenario<ValuationService> scenario;

    @Before
    public void setup() {
        scenario = ActivityScenario.launch(ValuationService.class);
        scenario.onActivity(activity -> {
        });
    }


    @Test
    public void testTotalPriceText() {
        onView(withId(R.id.tvTotalValue)).check(matches(isDisplayed()));
        onView(withId(R.id.Buy)).check(matches(isDisplayed()));
        onView(withId(R.id.Sell)).check(matches(isDisplayed()));
    }

    @Test
    public void testSellButtonUpdates() throws InterruptedException {
        //check input the name and value
        onView(withId(R.id.etItemName)).perform(typeText("Apple"));
        onView(withId(R.id.etItemValue)).perform(typeText("10.0"));
        Espresso.closeSoftKeyboard();

        //click sell button
        onView(withId(R.id.Sell)).perform(click());

        Thread.sleep(1000);

        //click yes in dialog
        onView(withText("Yes")).check(matches(isDisplayed())).perform(click());
    }
    //end
}