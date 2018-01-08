package com.drivecom.remote.group;

import com.drivecom.models.GroupModel;
import com.drivecom.models.UserModel;
import com.drivecom.utils.InternalUserModel;

import java.util.ArrayList;

public interface GroupManager {
    GroupModel createGroup(String name, UserModel currentUser, ArrayList<UserModel> members) throws IllegalStateException;

    void setCurrentGroupId(String groupId);

    void deleteGroup(DeleteCallback deleteCallback) throws IllegalStateException;

    void sendGroupInvitation(ArrayList<InternalUserModel> invitedUsers) throws IllegalStateException;

    void acceptGroupInvitation(String groupId, GroupAcceptCallback callback) throws IllegalStateException;

    void rejectGroupInvitation(String declineGroupId, GroupRejectCallback callback) throws IllegalStateException;

    void getGroupById(String groupId, GroupCallback callback);

    void subscribeMembersChanges(MemberChangedCallback callback) throws IllegalStateException;

    void unsubscribeMembersChanges(MemberChangedCallback callback) throws IllegalStateException;

    void subscribeForGroupDelete(DeleteCallback callback) throws IllegalStateException;

    void unsubscribeForGroupDelete(DeleteCallback callback) throws IllegalStateException;

    void subscribeInvitationsChanges(InvitationChangesCallback callback) throws IllegalStateException;

    void unsubscribeInvitationsChanges(InvitationChangesCallback callback) throws IllegalStateException;

    void leaveGroup(LeaveCallback callback) throws IllegalStateException;

    interface LeaveCallback {
        void onLeaveSuccess();

        void onLeaveError(Throwable t);
    }

    interface GroupCallback {
        void onGroupSuccess(GroupModel groupModel);

        void onGroupAlreadyDeleted();

        void onGroupError(Throwable t);
    }

    interface DeleteCallback {
        void onGroupDeleted();

        void onGroupDeletionError(Throwable t);
    }

    interface GroupAcceptCallback {
        void onSuccess(GroupModel value);

        void onError(Throwable t);
    }

    interface GroupRejectCallback {
        void onSuccess();

        void onError(Throwable t);
    }

    interface MemberChangedCallback {
        void onMemberAdded(String elementKey, UserModel newNearbyUser);

        void onMemberChanged(String elementKey, UserModel changedNearbyUser);

        void onMemberRemoved(String elementKey, String userId);

        void onError(Throwable t);
    }

    interface InvitationChangesCallback {
        void onInvitationAccepted(String userId);

        void onInvitationRejected(String userId);

        void onError(Throwable t);
    }
}
