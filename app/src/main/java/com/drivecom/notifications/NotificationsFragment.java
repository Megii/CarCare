package com.drivecom.notifications;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.drivecom.R;
import com.drivecom.dialogs.invitations.InvitationDialogInitializer;
import com.drivecom.dialogs.player.PlayerDialogInitializer;
import com.drivecom.main.MainActivityInterface;
import com.drivecom.models.GroupModel;
import com.drivecom.utils.InternalMessageModel;
import com.drivecom.utils.SnackBarCreator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationsFragment extends android.support.v4.app.Fragment
        implements NotificationsFragmentInterface,
        InvitationDialogInitializer.InvitationDialogInitializerListener {

    private InvitationsAdapter invitationsAdapter;
    private MessagesAdapter messagesAdapter;
    private NotificationsPresenterInterface presenter;
    private PlayerDialogInitializer playerDialogInitializer;
    private InvitationDialogInitializer invitationDialogInitializer;

    @BindView(R.id.fragment_notifications_root_layout)
    protected CoordinatorLayout rootLayout;
    @BindView(R.id.fragment_notifications_invitations_recycler_view)
    protected ListView invitationsListView;
    @BindView(R.id.fragment_notifications_messages_recycler_view)
    protected ListView messagesListView;
    @BindView(R.id.fragment_notifications_invitations_header)
    protected View invitationsHeader;
    @BindView(R.id.fragment_notifications_messages_header)
    protected View messagesHeader;
    @BindView(R.id.fragment_notifications_divider)
    protected View divider;
    @BindView(R.id.fragment_notifications_empty_message_text)
    protected View emptyMessageText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notifications,
                container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeInvitations();
        initializeMessages();
    }

    private void initializeInvitations() {
        invitationsAdapter = new InvitationsAdapter(getContext(), this);
        invitationsListView.setAdapter(invitationsAdapter);
    }

    public void onInvitationReceive(boolean showPopup, GroupModel groupModel) {
        if (!isInvitationInNotifications(groupModel.groupId)) {
            invitationsAdapter.addInvitation(groupModel, showPopup);
            invitationsHeader.setVisibility(View.VISIBLE);
            emptyMessageText.setVisibility(View.GONE);
            if (messagesAdapter.getCount() != 0 && divider.getVisibility() != View.VISIBLE) {
                divider.setVisibility(View.VISIBLE);
            }
            setListViewHeight(invitationsListView, invitationsAdapter.getCount());
        }

        if (showPopup) {
            showInvitation(groupModel);
        }
    }

    public boolean isInvitationInNotifications(String groupId) {
        return invitationsAdapter.getGroupById(groupId) != -1;
    }

    private void showInvitation(GroupModel groupModel) {
        getMainActivityInterface().setNotificationsPopupEnabled(false);
        invitationDialogInitializer = new InvitationDialogInitializer(getContext(), this, groupModel);
        invitationDialogInitializer.showInvitationDialog();
        invitationDialogInitializer.getAlertDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (getMainActivityInterface() != null) {
                    getMainActivityInterface().setNotificationsPopupEnabled(true);
                }
            }
        });
    }

    @Override
    public void onGroupJoinSuccess(GroupModel groupModel) {
        getMainActivityInterface().onGroupJoinSuccess(groupModel);
    }

    @Override
    public void onInvitationReject(String groupId) {
        invitationsAdapter.deleteInvitation(groupId);
        if (invitationsAdapter.getCount() == 0) {
            invitationsHeader.setVisibility(View.GONE);
            if (messagesAdapter.getCount() == 0) {
                emptyMessageText.setVisibility(View.VISIBLE);
                divider.setVisibility(View.GONE);
            }
        }
        setListViewHeight(invitationsListView, invitationsAdapter.getCount());
    }

    private void initializeMessages() {
        messagesAdapter = new MessagesAdapter(getContext(), this);
        messagesListView.setAdapter(messagesAdapter);
    }

    public void onMessageReceive(boolean showPopup, String from, String url) {
        messagesAdapter.addMessage(new InternalMessageModel(url, from), showPopup);
        messagesHeader.setVisibility(View.VISIBLE);
        emptyMessageText.setVisibility(View.GONE);
        if (invitationsAdapter.getCount() != 0 && divider.getVisibility() != View.VISIBLE) {
            divider.setVisibility(View.VISIBLE);
        }
        setListViewHeight(messagesListView, messagesAdapter.getCount());
        if (showPopup) {
            playMessage(from, url);
        }
    }

    private void playMessage(String userName, String url) {
        getMainActivityInterface().setNotificationsPopupEnabled(false);
        playerDialogInitializer = new PlayerDialogInitializer(getContext(), userName, url);
        playerDialogInitializer.showPlayerDialog();
        playerDialogInitializer.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getMainActivityInterface().setNotificationsPopupEnabled(true);
            }
        });
    }

    @Override
    public void onMessageClicked(int position, String userName, String url) {
        if (getMainActivityInterface().isNetworkAvailable()) {
            playMessage(userName, url);
            messagesAdapter.setWasItemClicked(position);
        } else {
            showSnackBar(R.string.network_error, false, null, null, null);
        }
    }

    private void setListViewHeight(ListView listView, int size) {
        int itemHeight = (int) getResources().getDimension(R.dimen.notifications_list_size);
        int height = size * itemHeight;
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Override
    public void onInvitationClicked(int position, GroupModel groupModel) {
        if (getMainActivityInterface().isNetworkAvailable()) {
            showInvitation(groupModel);
            invitationsAdapter.setWasItemClicked(position);
        } else {
            showSnackBar(R.string.network_error, false, null, null, null);
        }
    }

    public void showSnackBar(int messageResource, boolean isIndefinite, @Nullable final Runnable actionRunnable, @Nullable Integer actionLabelResource, @Nullable final Runnable onDismissRunnable) {
        Snackbar snackbar = new SnackBarCreator().getSnackBar(getContext(), rootLayout, messageResource, isIndefinite, actionRunnable, actionLabelResource, onDismissRunnable);
        snackbar.show();
    }

    @Override
    public void onStop() {
        if (playerDialogInitializer != null) {
            playerDialogInitializer.onStop();
        }
        super.onStop();
    }

    private MainActivityInterface getMainActivityInterface() {
        return (MainActivityInterface) getActivity();
    }
}
