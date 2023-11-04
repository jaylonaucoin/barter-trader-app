package com.example.myapplication;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class UserListingUnitTest {

    @Mock
    FirebaseAuth mockAuth;
    @Mock
    FirebaseDatabase mockFirebaseDB;
    @Mock
    DatabaseReference mockFirebaseDBRef;
    @Mock
    DataSnapshot mockDataSnapshot;
    @Mock
    DataSnapshot mockListingSnapshot;

    private UserListingActivity userListingActivity;

    @Before
    public void setup() {
        // Create an instance of UserListingActivity and set mock dependencies
        userListingActivity = new UserListingActivity();
        userListingActivity.auth = mockAuth;
        userListingActivity.firebaseDB = mockFirebaseDB;
        userListingActivity.firebaseDBRef = mockFirebaseDBRef;
    }

    @Test
    public void testIsCurrentUserListing() {
        // Mock data
        Mockito.when(mockListingSnapshot.child("User ID").getValue(String.class)).thenReturn("currentUser");

        // Create a mock FirebaseUser
        FirebaseUser mockUser = Mockito.mock(FirebaseUser.class);

        // Set the mock user as the current user
        Mockito.when(mockAuth.getCurrentUser()).thenReturn(mockUser);

        // Test the isCurrentUserListing method
        boolean result = userListingActivity.isCurrentUserListing(mockListingSnapshot, "currentUser");

        // Verify that the expected methods were called
        Mockito.verify(mockListingSnapshot).child("User ID").getValue(String.class);
        Mockito.verify(mockAuth).getCurrentUser();

        // Assert the result
        assertTrue(result);
    }

    @Test
    public void testIsNotCurrentUserListing() {
        // Mock data
        Mockito.when(mockListingSnapshot.child("User ID").getValue(String.class)).thenReturn("otherUser");

        // Create a mock FirebaseUser
        FirebaseUser mockUser = mock(FirebaseUser.class);

        // Set the mock user as the current user
        Mockito.when(mockAuth.getCurrentUser()).thenReturn(mockUser);

        // Test the isCurrentUserListing method
        boolean result = userListingActivity.isCurrentUserListing(mockListingSnapshot, "currentUser");

        // Verify that the expected methods were called
        Mockito.verify(mockListingSnapshot).child("User ID").getValue(String.class);
        Mockito.verify(mockAuth).getCurrentUser();

        // Assert the result
        assertFalse(result);
    }
}