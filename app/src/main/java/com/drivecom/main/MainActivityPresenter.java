package com.drivecom.main;


import android.location.Location;

import com.drivecom.models.CoordinatesModel;
import com.drivecom.models.GroupModel;
import com.drivecom.models.UserModel;
import com.drivecom.models.VoiceMessageModel;
import com.drivecom.remote.FirebaseUserDataProvider;
import com.drivecom.remote.UserDataProvider;
import com.drivecom.remote.group.FirebaseGroupManager;
import com.drivecom.remote.group.GroupManager;
import com.drivecom.utils.InternalUserModel;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

public class MainActivityPresenter implements MainActivityPresenterInterface {

    private final UserDataProvider userManager;
    private final GroupManager groupManager;
    private MainActivityInterface mainActivity;

    public MainActivityPresenter(MainActivityInterface mainActivity) {
        this.mainActivity = mainActivity;
        this.userManager = new FirebaseUserDataProvider();
        this.groupManager = new FirebaseGroupManager();
    }

    @Override
    public void invokeGroupCreating(final String name, final ArrayList<InternalUserModel> groupUsers) {
        GroupModel groupModel = groupManager.createGroup(name, convert(mainActivity.getCurrentUser()), convertInternalUserModelListToUserModelList(groupUsers));
        for (InternalUserModel user : groupUsers) {
            user.setWasAccepted(null);
            user.setWasSend(true);
        }
        mainActivity.onGroupCreatingSuccess(groupModel, groupUsers);
    }

    private ArrayList<UserModel> convertInternalUserModelListToUserModelList(ArrayList<InternalUserModel> internalUserModelList) {
        ArrayList<UserModel> list = new ArrayList<>();
        for (InternalUserModel internalModel : internalUserModelList) {
            list.add(convert(internalModel));
        }
        return list;
    }

    private UserModel convert(InternalUserModel internalModel) {
        return new UserModel(internalModel.getId(), "", null, internalModel.getName(), null, internalModel.getRegistrationId(), internalModel.getToken(), internalModel.getGroupId());
    }

    @Override
    public void invokeGroupDeleting(String groupId) {
        groupManager.setCurrentGroupId(groupId);
        groupManager.deleteGroup(new GroupManager.DeleteCallback() {
            @Override
            public void onGroupDeleted() {
                mainActivity.onGroupExitSuccess();
            }

            @Override
            public void onGroupDeletionError(Throwable t) {
                mainActivity.onGroupExitFailure(t);
            }
        });

    }

    @Override
    public void invokeGroupLeaving(String groupId) {
        groupManager.leaveGroup(new GroupManager.LeaveCallback() {
            @Override
            public void onLeaveSuccess() {
                mainActivity.onGroupExitSuccess();
            }

            @Override
            public void onLeaveError(Throwable t) {
                mainActivity.onGroupExitFailure(t);
            }
        });
    }

    @Override
    public void getMessageFrom(final VoiceMessageModel message) {
        userManager.getMessageFrom(new UserDataProvider.GetMessageFromCallback() {
            @Override
            public void onGetUserNameSuccess(String name) {
                mainActivity.onGetMessageFrom(name, message.url);
            }

            @Override
            public void onGetUserNameFailure(Exception e) {

            }
        }, message.from);
    }

    public void initializeCurrentGroup(String groupId) {
        groupManager.getGroupById(groupId, new GroupManager.GroupCallback() {
            @Override
            public void onGroupSuccess(GroupModel groupModel) {
                getGroupUsers(groupModel, false);
            }

            @Override
            public void onGroupAlreadyDeleted() {
                userManager.joinGroup(null);
                mainActivity.onCurrentGroupAlreadyDeleted();
            }

            @Override
            public void onGroupError(Throwable t) {

            }
        });
    }

    @Override
    public void getGroupUsers(GroupModel groupModel, boolean isAfterInvitation) {
        ArrayList<InternalUserModel> groupUsers = new ArrayList<>();
        for (UserModel userModel : groupModel.members) {
            if (userModel == null) {
                continue; //can be null if one of items were deleted
            }
            if (userModel.id.equals(groupModel.owner)) {
                groupUsers.add(new InternalUserModel(userModel.model, userModel.id, userModel.nr, userModel.token, groupModel.groupId, true, true));
            } else {
                Boolean wasSend = groupModel.invited.get(userModel.id).wasSend;
                Boolean wasAccepted = groupModel.invited.get(userModel.id).wasAccepted;
                groupUsers.add(new InternalUserModel(userModel.model, userModel.id, userModel.nr, userModel.token, groupModel.groupId, wasSend, wasAccepted));
            }
        }
        groupManager.setCurrentGroupId(groupModel.groupId);
        mainActivity.onGetGroupUsers(groupModel, groupUsers, isAfterInvitation);
    }

    @Override
    public void getCurrentUser() {
        mainActivity.onCurrentUserDataStarted();
        userManager.getCurrentUserData(new UserDataProvider.UserDataCallback() {
            @Override
            public void onUserDataSuccess(String email, com.drivecom.models.UserModel userModel) {
                String token = FirebaseInstanceId.getInstance().getToken();
                if (token != null && !token.equals(userModel.token)) {
                    userModel.token = token;
                    userManager.updateCurrentUserToken(token);
                }
                mainActivity.onCurrentUserDataReady(email, userModel);
            }

            @Override
            public void onUserDataFailure(Exception e) {
                mainActivity.onCurrentUserDataError(e);
            }
        });
    }

    @Override
    public void logout() {
        userManager.updateCurrentUserCoords(null);
        userManager.logout();
        mainActivity.onLogoutSuccess();
    }

    @Override
    public void logout(String groupId, boolean isOwner) {
        if (isOwner) {
            groupManager.setCurrentGroupId(groupId);
            groupManager.deleteGroup(new GroupManager.DeleteCallback() {
                @Override
                public void onGroupDeleted() {
                    logout();
                }

                @Override
                public void onGroupDeletionError(Throwable t) {

                }
            });
        } else {
            groupManager.leaveGroup(new GroupManager.LeaveCallback() {
                @Override
                public void onLeaveSuccess() {
                    logout();
                }

                @Override
                public void onLeaveError(Throwable t) {

                }
            });
        }
    }

    @Override
    public void onLocationReceived(Location location) {
        CoordinatesModel model = location == null ? new CoordinatesModel(null, null) : new CoordinatesModel(location.getLatitude(), location.getLongitude());
        userManager.updateCurrentUserCoords(model);
    }

    @Override
    public void onUserDataInGroupChanged() {
        userManager.updateCurrentUserCoords(new CoordinatesModel(0.0, 0.0));
    }

    @Override
    public void onLocationError(Throwable t) {
        mainActivity.reportLocationError(t);
    }
}
