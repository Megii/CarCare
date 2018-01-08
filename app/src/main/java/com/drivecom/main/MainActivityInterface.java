package com.drivecom.main;

import com.drivecom.models.GroupModel;
import com.drivecom.utils.InternalUserModel;

import java.util.ArrayList;

public interface MainActivityInterface {
    void onGroupCreatingSuccess(GroupModel groupModel, ArrayList<InternalUserModel> groupUsers);

    void onGroupCreatingFailure();

    void onGroupExitSuccess();

    void onGroupDeleted();

    void onGroupExitFailure(Throwable t);

    boolean checkForLocationPermissions();

    boolean checkForMicrophonePermissions();

    void onCurrentUserDataStarted();

    void onCurrentUserDataReady(String email, com.drivecom.models.UserModel userModel);

    void onCurrentUserDataError(Exception e);

    void reportLocationError(Throwable t);

    void onGroupJoinSuccess(GroupModel groupModel);

    boolean isNetworkAvailable();

    InternalUserModel getCurrentUser();

    void onGetMessageFrom(String from, String url);

    void onGetGroupUsers(GroupModel groupModel, ArrayList<InternalUserModel> groupUsers, boolean isAfterInvitation);

    void onGetGroupUsersError();

    void setNotificationsPopupEnabled(boolean isEnabled);

    void onCurrentGroupAlreadyDeleted();

    void onLogoutSuccess();
}
