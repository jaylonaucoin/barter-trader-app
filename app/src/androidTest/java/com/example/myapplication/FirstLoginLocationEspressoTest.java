package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.example.myapplication.registration.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    @Rule
    public GrantPermissionRule grantPermissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION);

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

    @Test
    public void manualAddressTest() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        assertNotNull("User must be logged in", currentUser);

        String userId = currentUser.getUid();
        DatabaseReference userAddressRef = mDatabase.child("User").child(userId).child("addresses");

        // Click the autocomplete fragment to activate the search bar
        onView(withId(R.id.autocomplete_fragment)).perform(click());

        // Type "Dalhousie University" into the search bar
        onView(withId(com.google.android.libraries.places.R.id.places_autocomplete_search_bar))
                .perform(click(), typeText("Dalhousie University"), closeSoftKeyboard());

        // We wait for the suggestions to load. This is not best practice.
        // Use IdlingResource in real tests instead of Thread.sleep.
        try {
            Thread.sleep(2000); // This sleep is to wait for suggestions to come up.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click the first suggestion that comes up
        onView(withId(com.google.android.libraries.places.R.id.places_autocomplete_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Wait for 2 seconds
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Click the confirm button
        onView(withId(R.id.confirm_location_button)).perform(click());

        // Now get the address at the 0th index
        userAddressRef.child("0").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot dataSnapshot = task.getResult();
                if (dataSnapshot.exists()) {
                    String address = dataSnapshot.child("address").getValue(String.class);
                    if ("1355 Oxford St, Halifax, NS B3H 3Z1, Canada".equals(address)) {
                        // If the address matches, proceed with deleting it
                        userAddressRef.child("0").removeValue();
                    } else {
                        fail("The 0th address does not match the expected address.");
                    }
                } else {
                    fail("No address found at index 0.");
                }
            } else {
                fail("Failed to fetch address from Firebase.");
            }
        });
    }
}
