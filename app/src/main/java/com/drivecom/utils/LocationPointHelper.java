package com.drivecom.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

@SuppressWarnings("deprecation")
@SuppressLint("MissingPermission")
public class LocationPointHelper {
    private static final int DEFAULT_DELAY = 300000;

    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private LocationListener locationListener;
    private boolean attached = false;

    public LocationPointHelper(Context context) {
        this.context = context;
    }

    public void startListening(@Nullable Integer intervalInMillis, final LocationPointCallback callback) {
        final int interval = intervalInMillis == null ? DEFAULT_DELAY : intervalInMillis;
        if (attached && locationListener != null && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            stopListening();
        }
        attached = true;
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    if (attached) {
                        callback.onLocationReceived(location);
                    }
                }
            }
        };

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        try {
                            if (!LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient).isLocationAvailable()) {
                                mGoogleApiClient.disconnect();
                                return;
                            }
                            LocationRequest req = new LocationRequest();
                            req.setInterval(interval);
                            req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, req, locationListener);
                        } catch (NullPointerException ex) {
                            callback.onLocationError(new RuntimeException("GPS service connection error", ex));
                            ex.printStackTrace();
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        if (mGoogleApiClient.isConnected()) {
                            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
                            mGoogleApiClient.disconnect();
                        }
                        callback.onLocationError(new RuntimeException("GPS service connection suspended"));
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        RuntimeException exception = new RuntimeException(connectionResult.getErrorMessage());
                        callback.onLocationError(new Exception("GPS service connection failed", exception));
                    }
                })
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void stopListening() {
        attached = false;
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
            }
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (attached) {
            stopListening();
        }
        super.finalize();
    }

    public interface LocationPointCallback {
        void onLocationReceived(Location location);

        void onLocationError(Throwable t);
    }
}
