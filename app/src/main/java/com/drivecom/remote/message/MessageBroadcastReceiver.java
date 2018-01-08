package com.drivecom.remote.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class MessageBroadcastReceiver extends BroadcastReceiver {
    public static final String MESSAGE_ACTION = "com.drivecom.action.MESSAGE_RECEIVED";
    public static final String MESSAGE_ID_TAG = "messageId";

    @Override
    public void onReceive(Context context, Intent intent) {
        String messageId = intent.getStringExtra(MESSAGE_ID_TAG);
        onMessageReceived(messageId);
    }

    public abstract void onMessageReceived(String messageId);
}
