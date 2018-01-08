package com.drivecom.group;

import com.drivecom.models.GroupModel;
import com.drivecom.models.UserModel;
import com.drivecom.remote.FirebaseUserDataProvider;
import com.drivecom.remote.group.FirebaseGroupManager;
import com.drivecom.remote.group.GroupManager;
import com.drivecom.utils.InternalUserModel;

import java.util.ArrayList;

public class GroupPresenter implements
        GroupPresenterInterface,
        GroupManager.MemberChangedCallback,
        GroupManager.DeleteCallback,
        GroupManager.InvitationChangesCallback {

    private GroupFragmentInterface groupFragment;
    private FirebaseGroupManager firebaseGroupManager;
    private FirebaseUserDataProvider userManager;

    public GroupPresenter(GroupFragmentInterface groupFragment, String groupId) {
        this.groupFragment = groupFragment;
        firebaseGroupManager = new FirebaseGroupManager();
        firebaseGroupManager.setCurrentGroupId(groupId);
        userManager = new FirebaseUserDataProvider();
    }

    @Override
    public void updateGroup(final String groupId, final String userId) {
        firebaseGroupManager.getGroupById(groupId, new GroupManager.GroupCallback() {
            @Override
            public void onGroupSuccess(GroupModel groupModel) {
                ArrayList<InternalUserModel> groupUsers = new ArrayList<>();
                for (UserModel userModel : groupModel.members) {

                    if (userModel != null && !userModel.id.equals(userId)) {
                        if (userModel.id.equals(groupModel.owner)) {
                            InternalUserModel owner = new InternalUserModel(userModel.model, userModel.id, userModel.nr, userModel.token, groupId, true, true);
                            if (groupUsers.size() == 0) {
                                groupUsers.add(owner);
                            } else {
                                groupUsers.set(0, owner);
                            }

                        } else {
                            Boolean wasSend = groupModel.invited.get(userModel.id).wasSend;
                            Boolean wasAccepted = groupModel.invited.get(userModel.id).wasAccepted;
                            groupUsers.add(new InternalUserModel(userModel.model, userModel.id, userModel.nr, userModel.token, groupModel.groupId, wasSend, wasAccepted));
                        }
                    }
                }
                groupFragment.onUpgradeGroupUsers(groupUsers);
            }

            @Override
            public void onGroupAlreadyDeleted() {
                groupFragment.onUpgradeGroupUsersError();
            }

            @Override
            public void onGroupError(Throwable t) {

            }
        });
    }

    @Override
    public void invokeInvitationsSending(String groupId, final boolean isFromNearbyUsers, final ArrayList<InternalUserModel> invitedUsers) {
        firebaseGroupManager.sendGroupInvitation(invitedUsers);
        groupFragment.onInvitationsSendingSuccess(isFromNearbyUsers, invitedUsers);
    }

    @Override
    public void subscribeMemberChanges() {
        firebaseGroupManager.subscribeMembersChanges(this);
    }

    @Override
    public void onMemberAdded(String elementKey, UserModel newNearbyUser) {
        groupFragment.onMemberAdded(elementKey, newNearbyUser);
    }

    @Override
    public void onMemberChanged(String elementKey, UserModel changedNearbyUser) {
        groupFragment.onMemberChanged(elementKey, changedNearbyUser);
    }

    @Override
    public void onMemberRemoved(String elementKey, String userId) {
        groupFragment.onMemberRemoved(elementKey, userId);
    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void unsubscribeMemberChanges() {
        firebaseGroupManager.unsubscribeMembersChanges(this);
    }

    @Override
    public void subscribeForGroupDelete() {
        firebaseGroupManager.subscribeForGroupDelete(this);
    }

    @Override
    public void onGroupDeleted() {
        groupFragment.onGroupDeleted();
    }

    @Override
    public void onGroupDeletionError(Throwable t) {

    }

    @Override
    public void unsubscribeForGroupDelete() {
        firebaseGroupManager.unsubscribeForGroupDelete(this);
    }

    @Override
    public void subscribeInvitationChanges() {
        firebaseGroupManager.subscribeInvitationsChanges(this);
    }

    @Override
    public void onInvitationAccepted(String userId) {
        groupFragment.onInvitationAccepted(userId);
    }

    @Override
    public void onInvitationRejected(String userId) {
        groupFragment.onInvitationRejected(userId);
    }

    @Override
    public void unsubscribeInvitationChanges() {
        firebaseGroupManager.unsubscribeInvitationsChanges(this);
    }
}
