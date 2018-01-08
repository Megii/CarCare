package com.drivecom.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.drivecom.group.GroupFragment;
import com.drivecom.models.GroupModel;
import com.drivecom.nearby.NearbyFragment;
import com.drivecom.notifications.NotificationsFragment;
import com.drivecom.utils.InternalUserModel;

import java.util.ArrayList;

public class TabLayoutPagerAdapter extends FragmentStatePagerAdapter {

    private int state;
    private static final int numberOfTabs = 2;
    private NearbyFragment nearbyFragment;
    private ArrayList<InternalUserModel> selectedUsers;
    private GroupFragment groupFragment;
    private Boolean groupCreatedByUser;
    private final InternalUserModel currentUser;
    private GroupModel groupModel;
    private NotificationsFragment notificationsFragment;
    private Boolean isAfterInvitation;

    public TabLayoutPagerAdapter(FragmentManager fm,
                                 int state,
                                 @Nullable GroupModel groupModel,
                                 @Nullable ArrayList<InternalUserModel> selectedUsers,
                                 @Nullable Boolean groupCreatedByUser,
                                 @Nullable Boolean isAfterInvitation,
                                 InternalUserModel currentUser) {
        super(fm);
        this.state = state;
        this.groupModel = groupModel;
        this.selectedUsers = selectedUsers;
        this.groupCreatedByUser = groupCreatedByUser;
        this.currentUser = currentUser;
        this.isAfterInvitation = isAfterInvitation;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if (state == MainActivity.STATE_NEARBY) {
                    nearbyFragment = new NearbyFragment();
                    return nearbyFragment;
                } else {
                    groupFragment = new GroupFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(NearbyFragment.NEARBY_USERS_TAG, selectedUsers);
                    bundle.putBoolean(GroupFragment.GROUP_CREATED_BY_USER_TAG, groupCreatedByUser);
                    bundle.putParcelable(GroupFragment.CURRENT_USER_TAG, currentUser);
                    bundle.putString(GroupFragment.GROUP_ID_TAG, groupModel.groupId);
                    bundle.putString(GroupFragment.GROUP_NAME_TAG, groupModel.name);
                    bundle.putBoolean(GroupFragment.AFTER_INVITATION, isAfterInvitation);
                    groupFragment.setArguments(bundle);
                    return groupFragment;
                }

            case 1:
                notificationsFragment = new NotificationsFragment();
                return notificationsFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }

    public NearbyFragment getNearbyFragment() {
        return nearbyFragment;
    }

    public GroupFragment getGroupFragment() {
        return groupFragment;
    }

    public NotificationsFragment getNotificationsFragment() {
        return notificationsFragment;
    }
}
