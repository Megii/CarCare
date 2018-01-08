package com.drivecom.fcm;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        log("onMessageReceived " + remoteMessage.toString());
        handleMessageViaType(remoteMessage);
    }

    private void handleMessageViaType(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null && remoteMessage.getNotification().getTag() != null) {
            NotificationBroadcastingHelper.handleMessageDescription(this, remoteMessage.getNotification().getTag());
        }
    }


    private void log(String message) {
        Log.println(Log.ASSERT, "FCM", message);
    }
}
