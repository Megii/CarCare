package com.drivecom.main;


import com.drivecom.models.GroupModel;
import com.drivecom.models.VoiceMessageModel;
import com.drivecom.utils.InternalUserModel;
import com.drivecom.utils.LocationPointHelper;

import java.util.ArrayList;

public interface MainActivityPresenterInterface extends LocationPointHelper.LocationPointCallback {

    void invokeGroupCreating(String name, ArrayList<InternalUserModel> groupUsers);

    void invokeGroupDeleting(String groupId);

    void invokeGroupLeaving(String groupId);

    void getMessageFrom(VoiceMessageModel message);

    void getCurrentUser();

    void getGroupUsers(GroupModel groupModel, boolean isAfterInvitation);

    void logout();

    void logout(String groupId, boolean isOwner);

    void onUserDataInGroupChanged();
}
