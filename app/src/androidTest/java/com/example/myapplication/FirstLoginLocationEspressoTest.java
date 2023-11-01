package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class FirstLoginLocationEspressoTest {
    @Rule
    public ActivityScenarioRule<LoginActivity> mActivityRule = new ActivityScenarioRule<>(LoginActivity.class);

    @Before
    public void loginUser() {
        onView(withId(R.id.email)).perform(typeText("test@test.com"), closeSoftKeyboard());
        onView(withId(R.id.password)).perform(typeText("admintest123"), closeSoftKeyboard());
        onView(withId(R.id.loginButton)).perform(click());

        // Add delay or IdlingResource to ensure login process completes before subsequent tests
        try {
            Thread.sleep(2000);  // wait for 2 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void locationIconClickTest() {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        // Get the current user's UID
        String userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();

        // Assuming you have set up the Firebase SDK in your test environment
        DatabaseReference userAddressRef = FirebaseDatabase.getInstance()
                .getReference("User")
                .child(userId)
                .child("addresses");

        // Click on the location icon (assuming you have the correct view id)
        onView(withId(R.id.automatic_location_icon)).perform(click());

        // Wait for 2 seconds
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click the confirm button (assuming you have the correct view id)
        onView(withId(R.id.confirm_location_button)).perform(click());

        // Verify and delete 0th address
        userAddressRef.child("0").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 0th address exists
                    userAddressRef.child("0").removeValue(); // Delete it
                    // Pass the test
                    assertTrue(true);
                } else {
                    // 0th address doesn't exist
                    // Fail the test
                    fail();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });
    }


}
