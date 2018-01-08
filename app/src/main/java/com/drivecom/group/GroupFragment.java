package com.drivecom.group;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.drivecom.R;
import com.drivecom.dialogs.GroupUserStatusDialogInitializer;
import com.drivecom.dialogs.NearbyDialogInitializer;
import com.drivecom.dialogs.recorder.RecordingDialogInitializer;
import com.drivecom.main.MainActivityInterface;
import com.drivecom.models.UserModel;
import com.drivecom.nearby.NearbyFragment;
import com.drivecom.utils.InternalUserModel;
import com.drivecom.utils.SnackBarCreator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GroupFragment extends Fragment implements GroupFragmentInterface {

    public static final String FRAGMENT_TAG = "GroupFragmentTag";

    public static final String GROUP_CREATED_BY_USER_TAG = "GroupCreatedByUserTag";
    public static final String CURRENT_USER_TAG = "currusr";
    public static final String GROUP_ID_TAG = "GroupIdTag";
    public static final String GROUP_NAME_TAG = "GroupName";
    public static final String AFTER_INVITATION = "AfterInvitation";

    public static final int USER_STATUS_ACTIVE = 2;
    public static final int USER_STATUS_INVITATION_REJECTED = 4;
    public static final int USER_STATUS_WAITING = 6;
    public static final int USER_STATUS_LEFT = 8;

    @BindView(R.id.fragment_group_root_layout)
    protected CoordinatorLayout rootLayout;
    @BindView(R.id.fragment_group_fab)
    protected View fab;

    private GroupPresenterInterface presenter;
    private ArrayList<GroupUser> groupUsers;
    private ArrayList<View> buttons;
    private ArrayList<ImageView> statusViews;
    private ArrayList<TextView> userNameTextViews;
    private ArrayList<TextView> userNameRegistrationNumberTextViews;
    private ArrayList<ImageView> addImageViews;
    private NearbyDialogInitializer nearbyDialogInitializer;
    private boolean groupCreatedByUser;
    private String groupId;
    private boolean afterInvitation;
    private RecordingDialogInitializer recordingDialogInitializer;
    private GroupUserStatusDialogInitializer groupUserStatusDialogInitializer;
    private InternalUserModel currentUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group,
                container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getInitialData();
        presenter = new GroupPresenter(this, groupId);
        if (afterInvitation) {
            presenter.updateGroup(groupId, currentUser.getId());
        } else {

            initializeViews();
            initializeList();
            subscribeListeners();
        }
    }

    @Override
    public void onUpgradeGroupUsers(ArrayList<InternalUserModel> users) {
        groupUsers = new ArrayList<>();
        groupUsers.add(new GroupUser(currentUser.getName(), currentUser.getId(), currentUser.getRegistrationId(), null, currentUser.getGroupId(), -1));
        for (InternalUserModel user : users) {
            groupUsers.add(new GroupUser(user.getName(), user.getId(), user.getRegistrationId(), user.getToken(), user.getGroupId(), getUserStatusFromAccepted(user.getWasSend(), user.getWasAccepted())));
        }
        int emptySlots = 6 - groupUsers.size();
        for (int i = 0; i < emptySlots; i++) {
            groupUsers.add(null);
        }
        initializeViews();
        initializeList();
        subscribeListeners();
    }

    @Override
    public void onUpgradeGroupUsersError() {
        getActivityInterface().onCurrentGroupAlreadyDeleted();
    }

    private void getInitialData() {
        Bundle bundle = this.getArguments();
        groupUsers = new ArrayList<>();

        if (bundle != null) {
            groupCreatedByUser = bundle.getBoolean(GROUP_CREATED_BY_USER_TAG);
            currentUser = bundle.getParcelable(CURRENT_USER_TAG);
            groupId = bundle.getString(GROUP_ID_TAG);
            afterInvitation = bundle.getBoolean(AFTER_INVITATION, false);
            if (!afterInvitation) {
                ArrayList<InternalUserModel> users = bundle.getParcelableArrayList(NearbyFragment.NEARBY_USERS_TAG);
                groupUsers = new ArrayList<>();
                groupUsers.add(new GroupUser(currentUser.getName(), currentUser.getId(), currentUser.getRegistrationId(), null, currentUser.getGroupId(), -1));
                for (InternalUserModel user : users) {
                    groupUsers.add(new GroupUser(user.getName(), user.getId(), user.getRegistrationId(), user.getToken(), user.getGroupId(), getUserStatusFromAccepted(user.getWasSend(), user.getWasAccepted())));
                }
                int emptySlots = 6 - groupUsers.size();
                for (int i = 0; i < emptySlots; i++) {
                    groupUsers.add(null);
                }
            }
        }
    }

    private int getUserStatusFromAccepted(Boolean wasSend, Boolean wasAccepted) {
        if (wasSend) {
            if (wasAccepted == null) {
                return USER_STATUS_WAITING;
            } else if (wasAccepted) {
                return USER_STATUS_ACTIVE;
            } else {
                return USER_STATUS_INVITATION_REJECTED;
            }
        }
        return -1;
    }

    private void initializeViews() {
        buttons = new ArrayList<>();
        userNameTextViews = new ArrayList<>();
        userNameRegistrationNumberTextViews = new ArrayList<>();
        addImageViews = new ArrayList<>();
        statusViews = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            buttons.add(getActivity().findViewById(
                    getResources().getIdentifier(
                            "fragment_group_card_view_" + (i),
                            "id",
                            getActivity().getPackageName()
                    )));
            if (i != 0 && isGroupCreatedByUser()) {
                final int pos = i;
                buttons.get(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (groupUsers.get(pos) != null) {
                            groupUserStatusDialogInitializer = new GroupUserStatusDialogInitializer(getContext(), GroupFragment.this, groupUsers.get(pos));
                            groupUserStatusDialogInitializer.showGroupUserStatusDialog();
                        } else {
                            if (getActivityInterface().isNetworkAvailable()) {
                                showNearbyDialog();
                            } else {
                                showSnackBar(R.string.network_error, false, null, null, null);
                            }
                        }
                    }
                });
            } else {
                buttons.get(i).setClickable(false);
            }
            statusViews.add((ImageView) getActivity().findViewById(getResources().getIdentifier(
                    "fragment_group_user_status_" + (i),
                    "id",
                    getActivity().getPackageName()
            )));
            if (i != 0 && groupUsers.get(i) != null) {
                statusViews.get(i).setVisibility(View.VISIBLE);
                int color = R.color.blue;
                if (groupUsers.get(i).getGroupStatus() == USER_STATUS_ACTIVE) {
                    color = R.color.green;
                } else if (groupUsers.get(i).getGroupStatus() == USER_STATUS_WAITING) {
                    color = R.color.blue;
                } else if (groupUsers.get(i).getGroupStatus() == USER_STATUS_INVITATION_REJECTED) {
                    color = R.color.red;
                }

                statusViews.get(i).setColorFilter(ContextCompat.getColor(getContext(), color));
            }

            userNameTextViews.add((TextView) getActivity().findViewById(getResources().getIdentifier(
                    "fragment_group_user_name_" + (i),
                    "id",
                    getActivity().getPackageName()
            )));

            userNameRegistrationNumberTextViews.add((TextView) getActivity().findViewById(getResources().getIdentifier(
                    "fragment_group_user_registration_number_" + (i),
                    "id",
                    getActivity().getPackageName()
            )));

            addImageViews.add((ImageView) getActivity().findViewById(getResources().getIdentifier(
                    "fragment_group_user_add_image_" + (i),
                    "id",
                    getActivity().getPackageName()
            )));
        }

        initializeFab();
    }

    private void initializeFab() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivityInterface().isNetworkAvailable()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || getActivityInterface().checkForMicrophonePermissions()) {
                        startRecording();
                    }
                } else {
                    showSnackBar(R.string.network_error, false, null, null, null);
                }
            }
        });
    }

    public void startRecording() {
        ArrayList<InternalUserModel> activeUsersList = getActiveUsers();
        if (activeUsersList.size() != 0) {
            getActivityInterface().setNotificationsPopupEnabled(false);
            recordingDialogInitializer = new RecordingDialogInitializer(getContext(), new RecordingDialogInitializer.RecordingDialogListener() {
                @Override
                public void onRecordingError() {
                    showSnackBar(R.string.recorder_error, false, null, null, null);
                }
            }, activeUsersList);
            recordingDialogInitializer.showRecordingDialog();
            recordingDialogInitializer.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    getActivityInterface().setNotificationsPopupEnabled(true);
                }
            });
        } else {
            showSnackBar(R.string.message_recording_no_active_users_message, false, null, null, null);
        }
    }

    private ArrayList<InternalUserModel> getActiveUsers() {
        ArrayList<InternalUserModel> activeUsersList = new ArrayList<>();
        for (GroupUser user : groupUsers) {
            if (user != null && user.groupStatus == USER_STATUS_ACTIVE) {
                activeUsersList.add(user);
            }
        }
        return activeUsersList;
    }

    private void initializeList() {
        if (groupUsers != null) {
            for (int i = 0; i < 6; i++) {
                if (groupUsers.get(i) != null) {
                    userNameTextViews.get(i).setText(groupUsers.get(i).getName());
                    userNameTextViews.get(i).setVisibility(View.VISIBLE);
                    userNameRegistrationNumberTextViews.get(i).setText(groupUsers.get(i).getRegistrationId());
                    userNameRegistrationNumberTextViews.get(i).setVisibility(View.VISIBLE);
                    addImageViews.get(i).setVisibility(View.GONE);
                } else {
                    userNameTextViews.get(i).setVisibility(View.GONE);
                    userNameRegistrationNumberTextViews.get(i).setVisibility(View.GONE);
                    if (isGroupCreatedByUser()) {
                        addImageViews.get(i).setVisibility(View.VISIBLE);
                    } else {
                        addImageViews.get(i).setVisibility(View.GONE);
                    }

                }
            }
        }
    }

    private void showNearbyDialog() {
        getActivityInterface().setNotificationsPopupEnabled(false);
        nearbyDialogInitializer = new NearbyDialogInitializer(getContext(), this, new ArrayList<InternalUserModel>(groupUsers));
        nearbyDialogInitializer.showNearbyDialog();
        nearbyDialogInitializer.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                nearbyDialogInitializer.unsubscribeNearbyChanges();
                getActivityInterface().setNotificationsPopupEnabled(true);
            }
        });
    }

    @Override
    public void sendInvitations(boolean isFromNearbyUsers, final ArrayList<InternalUserModel> invitedUsers) {
        presenter.invokeInvitationsSending(groupId, isFromNearbyUsers, invitedUsers);
    }

    @Override
    public void onInvitationsSendingSuccess(boolean isFromNearbyUsers, final ArrayList<InternalUserModel> invitedUsers) {
        if (isFromNearbyUsers) {
            nearbyDialogInitializer.onSendInvitationsSuccess();
        } else {
            groupUserStatusDialogInitializer.onInvitationResendSuccess();
            for (int i = 0; i < groupUsers.size(); i++) {
                if (groupUsers.get(i) != null && groupUsers.get(i).getId().equals(invitedUsers.get(0).getId())) {
                    statusViews.get(i).setColorFilter(ContextCompat.getColor(getContext(), R.color.blue));
                    statusViews.get(i).setVisibility(View.VISIBLE);
                    groupUsers.get(i).setGroupStatus(USER_STATUS_WAITING);
                }
            }
        }
    }

    @Override
    public void onMemberAdded(String elementKey, UserModel newNearbyUser) {
        if (!checkIfOnList(newNearbyUser.id)) {
            InternalUserModel newUser = new InternalUserModel(newNearbyUser.model, newNearbyUser.id, newNearbyUser.nr, newNearbyUser.token, newNearbyUser.groupId, true, null);
            ArrayList<InternalUserModel> itemList = new ArrayList<>();
            itemList.add(newUser);
            addNewUsers(itemList);
        }
    }

    @Override
    public void onMemberChanged(String elementKey, UserModel changedNearbyUser) {
        for (int i = 0; i < groupUsers.size(); i++) {
            if (groupUsers.get(i) != null && groupUsers.get(i).getId().equals(changedNearbyUser.id)) {
                GroupUser previousGroupUser = groupUsers.get(i);
                groupUsers.set(i, new GroupUser(
                        changedNearbyUser.model,
                        changedNearbyUser.id,
                        changedNearbyUser.nr,
                        changedNearbyUser.token,
                        changedNearbyUser.groupId,
                        previousGroupUser.groupStatus)
                );
                userNameTextViews.get(i).setText(groupUsers.get(i).getName());
                userNameRegistrationNumberTextViews.get(i).setText(groupUsers.get(i).getRegistrationId());
                break;
            }
        }
    }

    public void onInvitationsSendingFailure(boolean isFromNearbyUsers) {
        if (isFromNearbyUsers) {
            nearbyDialogInitializer.onSendInvitationsFailure();
        } else {
            groupUserStatusDialogInitializer.onInvitationResendFailure();
        }
    }

    @Override
    public void addNewUsers(final ArrayList<InternalUserModel> invitedUsers) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (getContext() != null) {
                        int startIndex = 6 - getEmptySlots();
                        int stopIndex = startIndex + invitedUsers.size();
                        for (int i = startIndex; i < stopIndex; i++) {
                            InternalUserModel user = invitedUsers.get(i - startIndex);
                            groupUsers.set(i, new GroupUser(user.getName(), user.getId(), user.getRegistrationId(), user.getToken(), user.getGroupId(), USER_STATUS_WAITING));
                            statusViews.get(i).setColorFilter(ContextCompat.getColor(getContext(), R.color.blue));
                            statusViews.get(i).setVisibility(View.VISIBLE);
                            userNameTextViews.get(i).setText(user.getName());
                            userNameTextViews.get(i).setVisibility(View.VISIBLE);
                            userNameRegistrationNumberTextViews.get(i).setText(user.getRegistrationId());
                            userNameRegistrationNumberTextViews.get(i).setVisibility(View.VISIBLE);
                            addImageViews.get(i).setVisibility(View.GONE);

                        }
                    }
                }
            });
        }
    }

    private boolean checkIfOnList(String id) {
        for (int i = 0; i < groupUsers.size(); i++) {
            if (groupUsers.get(i) != null && groupUsers.get(i).getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private int getEmptySlots() {
        int emptySlots = 0;
        for (int i = 0; i < groupUsers.size(); i++) {
            if (groupUsers.get(i) == null) {
                emptySlots++;
            }
        }
        return emptySlots;
    }


    @Override
    public void onInvitationAccepted(String userId) {
        for (int i = 0; i < groupUsers.size(); i++) {
            if (groupUsers.get(i) != null && groupUsers.get(i).getId().equals(userId)) {
                groupUsers.get(i).setGroupStatus(USER_STATUS_ACTIVE);
                statusViews.get(i).setColorFilter(ContextCompat.getColor(getContext(), R.color.green));
                break;
            }
        }
    }

    @Override
    public void onInvitationRejected(String userId) {
        for (int i = 0; i < groupUsers.size(); i++) {
            if (groupUsers.get(i) != null && groupUsers.get(i).getId().equals(userId)) {
                groupUsers.get(i).setGroupStatus(USER_STATUS_INVITATION_REJECTED);
                statusViews.get(i).setColorFilter(ContextCompat.getColor(getContext(), R.color.red));
                break;
            }
        }
    }

    @Override
    public void onMemberRemoved(String elementKey, String userId) {
        for (int i = 0; i < groupUsers.size(); i++) {
            if (groupUsers.get(i) != null && groupUsers.get(i).getId().equals(userId)) {
                groupUsers.get(i).setGroupStatus(USER_STATUS_LEFT);
                statusViews.get(i).setColorFilter(ContextCompat.getColor(getContext(), R.color.red));
                break;
            }
        }
    }

    public boolean isOwner() {
        return groupCreatedByUser;
    }

    @Override
    public void onGroupDeleted() {
        getActivityInterface().onGroupDeleted();
    }

    public boolean isGroupCreatedByUser() {
        return groupCreatedByUser;
    }

    public String getGroupId() {
        return groupId;
    }

    public void showSnackBar(int messageResource, boolean isIndefinite, @Nullable final Runnable actionRunnable, @Nullable Integer actionLabelResource, @Nullable final Runnable onDismissRunnable) {
        Snackbar snackbar = new SnackBarCreator().getSnackBar(getContext(), rootLayout, messageResource, isIndefinite, actionRunnable, actionLabelResource, onDismissRunnable);
        snackbar.show();
    }

    public static class GroupUser extends InternalUserModel {

        private int groupStatus;

        public GroupUser(String name, String id, String registrationId, String token, String groupId, int groupStatus) {
            super(name, id, registrationId, token, groupId);
            this.groupStatus = groupStatus;
        }

        public int getGroupStatus() {
            return groupStatus;
        }

        public void setGroupStatus(int groupStatus) {
            this.groupStatus = groupStatus;
        }
    }

    @Override
    public void onStop() {
        if (recordingDialogInitializer != null) {
            recordingDialogInitializer.onStop();
        }
        if (nearbyDialogInitializer != null) {
            nearbyDialogInitializer.onStop();
        }
        unsubscribeListeners();
        super.onStop();
    }

    public void unsubscribeListeners() {
        presenter.unsubscribeForGroupDelete();
        presenter.unsubscribeInvitationChanges();
        presenter.unsubscribeMemberChanges();
    }

    public void subscribeListeners() {
        presenter.subscribeForGroupDelete();
        presenter.subscribeInvitationChanges();
        presenter.subscribeMemberChanges();
    }


    private MainActivityInterface getActivityInterface() {
        return (MainActivityInterface) getActivity();
    }
}
