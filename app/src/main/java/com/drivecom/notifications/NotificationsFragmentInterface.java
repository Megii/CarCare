package com.drivecom.notifications;


import com.drivecom.models.GroupModel;
import com.drivecom.utils.InternalUserModel;

import java.util.ArrayList;

public interface NotificationsFragmentInterface {

    void onMessageClicked(int position, String userName, String url);

    void onInvitationClicked(int position, GroupModel groupModel);
}
