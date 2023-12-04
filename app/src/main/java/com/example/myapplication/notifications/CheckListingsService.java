package com.example.myapplication.notifications;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CheckListingsService extends Service {
    private NotificationsManager notificationsManager = new NotificationsManager();
    private FirebaseDatabase firebaseDB;
    private DatabaseReference firebaseDBRef;
    private FirebaseAuth auth;

    private boolean isFirstRun = false;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {

                        firebaseDB = FirebaseDatabase.getInstance();
                        firebaseDBRef = firebaseDB.getReference("Listings");
                        auth = FirebaseAuth.getInstance();
                        String currentUserId = auth.getCurrentUser().getUid();
                        firebaseDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                isFirstRun = true;
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        firebaseDBRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                                if (isFirstRun) {
                                    if(!isCurrentUserListing(dataSnapshot, currentUserId)){
                                        notificationsManager.sendMessage("Open for more information.");
                                    }

                                }


                            }



                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }


                        });
                    }
                }
        ).start();
        return super.onStartCommand(intent, flags, startId);
    }





    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private boolean isCurrentUserListing(DataSnapshot dataSnapshot, String currentUserId) {
        String userId = dataSnapshot.child("User ID").getValue(String.class);
        return userId != null && userId.equals(currentUserId);
    }


}
