package com.example.myapplication;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class LocationHelper {

    private final Context context;
    private final FusedLocationProviderClient locationClient;
    private final LocationCallback locationCallback;

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1000; // Permission request code

    private boolean shouldFetchLocationAfterPermission = false;

    public LocationHelper(Context context, LocationCallback callback) {
        this.context = context;
        this.locationClient = LocationServices.getFusedLocationProviderClient(context);
        this.locationCallback = callback;
    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            shouldFetchLocationAfterPermission = true;
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fetchLastKnownLocation();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && shouldFetchLocationAfterPermission) {
                fetchLastKnownLocation();
                shouldFetchLocationAfterPermission = false;
            }
        }
    }

    private void fetchLastKnownLocation() {
        try {
            locationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            locationCallback.onLocationRetrieved(location);
                        }
                    })
                    .addOnFailureListener(e -> Log.e("LocationError", "Error trying to get last GPS location", e));
        } catch (SecurityException e) {
            Log.e("LocationError", "SecurityException", e);
        }
    }

    public interface LocationCallback {
        void onLocationRetrieved(Location location);
    }
}
