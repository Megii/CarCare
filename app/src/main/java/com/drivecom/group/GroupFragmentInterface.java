package com.drivecom.group;

import com.drivecom.models.UserModel;
import com.drivecom.utils.InternalUserModel;

import java.util.ArrayList;

public interface GroupFragmentInterface {
    void sendInvitations(boolean isFromNearbyUsers, ArrayList<InternalUserModel> invitedUsers);

    void addNewUsers(ArrayList<InternalUserModel> invitedUsers);

    void onInvitationsSendingSuccess(boolean isFromNearbyUsers, ArrayList<InternalUserModel> invitedUsers);

    void onInvitationsSendingFailure(boolean isFromNearbyUsers);

    void onMemberAdded(String elementKey, UserModel newNearbyUser);

    void onMemberChanged(String elementKey, UserModel changedNearbyUser);

    void onMemberRemoved(String elementKey, String userId);

    void onGroupDeleted();

    void onInvitationAccepted(String userId);

    void onInvitationRejected(String userId);

    void onUpgradeGroupUsers(ArrayList<InternalUserModel> users);

    void onUpgradeGroupUsersError();
}
