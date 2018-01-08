package com.drivecom.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        log("onTokenRefresh");
    }

    private void log(String message) {
        Log.println(Log.ASSERT, "FCM", message);
    }
}
