package com.drivecom.remote.group;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class GroupInvitationBroadcastReceiver extends BroadcastReceiver {
    public static final String INVITATION_ACTION = "com.drivecom.action.INVITATION_RECEIVED";
    public static final String GROUP_ID_TAG = "groupId";
    public static final String OWNER_NAME_TAG = "owner";

    @Override
    public void onReceive(Context context, Intent intent) {
        String groupId = intent.getStringExtra(GROUP_ID_TAG);
        String owner = intent.getStringExtra(OWNER_NAME_TAG);
        onGroupInvitationReceived(groupId, owner);
    }

    public abstract void onGroupInvitationReceived(String groupId, String ownerName);
}
