package com.drivecom.group;


import com.drivecom.utils.InternalUserModel;

import java.util.ArrayList;

public interface GroupPresenterInterface {
    void invokeInvitationsSending(String groupId, boolean isFromNearbyUsers, ArrayList<InternalUserModel> invitedUsers);

    void subscribeMemberChanges();

    void unsubscribeMemberChanges();

    void subscribeForGroupDelete();

    void unsubscribeForGroupDelete();

    void subscribeInvitationChanges();

    void unsubscribeInvitationChanges();

    void updateGroup(String groupId, String userId);
}