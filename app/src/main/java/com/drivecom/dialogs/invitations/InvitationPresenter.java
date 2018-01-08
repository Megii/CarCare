package com.drivecom.dialogs.invitations;

import com.drivecom.models.GroupModel;
import com.drivecom.remote.group.FirebaseGroupManager;
import com.drivecom.remote.group.GroupManager;

public class InvitationPresenter implements InvitationPresenterInterface {

    private InvitationViewInterface dialogView;
    private FirebaseGroupManager groupManager;

    public InvitationPresenter(InvitationViewInterface dialogView) {
        this.dialogView = dialogView;
        groupManager = new FirebaseGroupManager();
    }

    @Override
    public void acceptInvitation(String groupId) {
        groupManager.acceptGroupInvitation(groupId, new GroupManager.GroupAcceptCallback() {
            @Override
            public void onSuccess(GroupModel value) {
                dialogView.onSuccess(true);
            }

            @Override
            public void onError(Throwable t) {
                dialogView.onError();
            }
        });
    }

    @Override
    public void rejectInvitation(String groupId) {
        groupManager.rejectGroupInvitation(groupId, new GroupManager.GroupRejectCallback() {
            @Override
            public void onSuccess() {
                dialogView.onSuccess(false);
            }

            @Override
            public void onError(Throwable t) {
                dialogView.onError();
            }
        });
    }
}
