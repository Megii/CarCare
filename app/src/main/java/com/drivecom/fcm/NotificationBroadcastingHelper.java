package com.drivecom.fcm;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.drivecom.remote.group.GroupInvitationBroadcastReceiver;
import com.drivecom.remote.message.MessageBroadcastReceiver;

public class NotificationBroadcastingHelper {
    private static final String NOTIFICATION_TYPE_MESSAGE = "0";
    private static final String NOTIFICATION_TYPE_INVITATION = "1";

    public static void handleMessageDescription(Context context, @NonNull String messageDescription) {
        String[] params = messageDescription.split(",");
        String notiType = params[0];
        switch (notiType) {
            case NOTIFICATION_TYPE_MESSAGE: {
                broadcastMessageReceived(context, params);
            }
            break;
            case NOTIFICATION_TYPE_INVITATION: {
                broadcastInvitationReceived(context, params);
            }
            break;
        }
    }

    private static void broadcastInvitationReceived(Context context, String... params) {
        Intent intent = new Intent(GroupInvitationBroadcastReceiver.INVITATION_ACTION);
        intent.putExtra(GroupInvitationBroadcastReceiver.GROUP_ID_TAG, params[1]);
        intent.putExtra(GroupInvitationBroadcastReceiver.OWNER_NAME_TAG, params[2]);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private static void broadcastMessageReceived(Context context, String... params) {
        Intent intent = new Intent(MessageBroadcastReceiver.MESSAGE_ACTION);
        intent.putExtra(MessageBroadcastReceiver.MESSAGE_ID_TAG, params[1]);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}
