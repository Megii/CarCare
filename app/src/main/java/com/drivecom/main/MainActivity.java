package com.drivecom.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.drivecom.R;
import com.drivecom.dialogs.AboutUsDialogInitializer;
import com.drivecom.dialogs.CreateGroupDialogInitializer;
import com.drivecom.dialogs.ExitGroupDialogInitializer;
import com.drivecom.dialogs.SimpleProgressDialogInitializer;
import com.drivecom.dialogs.TermsOfUseDialogInitializer;
import com.drivecom.dialogs.accountData.AccountDataDialogInitializer;
import com.drivecom.fcm.NotificationBroadcastingHelper;
import com.drivecom.initialization.InitializationActivity;
import com.drivecom.models.GroupModel;
import com.drivecom.models.VoiceMessageModel;
import com.drivecom.remote.group.FirebaseGroupManager;
import com.drivecom.remote.group.GroupInvitationBroadcastReceiver;
import com.drivecom.remote.group.GroupManager;
import com.drivecom.remote.message.FirebaseMessageManager;
import com.drivecom.remote.message.MessageBroadcastReceiver;
import com.drivecom.remote.message.MessageManager;
import com.drivecom.utils.InternalUserModel;
import com.drivecom.utils.LocationPointHelper;
import com.drivecom.utils.SmoothActionBarDrawerToggle;
import com.drivecom.utils.SnackBarCreator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements MainActivityInterface,
        NavigationView.OnNavigationItemSelectedListener,
        CreateGroupDialogInitializer.CreateGroupDialogListener,
        ExitGroupDialogInitializer.ExitGroupDialogInitializerListener {

    public static final int STATE_NEARBY = 1;
    public static final int STATE_GROUP = 2;

    private static final int SETTINGS_REQUEST = 25;
    private static final int RECORD_AUDIO_PERMISSION = 10;
    private static final int GPS_PERMISSION = 15;

    @BindView(R.id.main_activity_coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.unread_dot)
    View unreadDot;

    private SmoothActionBarDrawerToggle toggle;

    private MainActivityPresenter presenter;
    private TabLayoutPagerAdapter adapter;
    private CreateGroupDialogInitializer createGroupDialogInitializer;
    private ExitGroupDialogInitializer exitGroupDialogInitializer;
    private AccountDataDialogInitializer accountDataDialogInitializer;
    private int state;
    private com.drivecom.models.UserModel currentUser;
    private String currentEmail;
    private LocationPointHelper locationHelper;
    private SimpleProgressDialogInitializer simpleProgressDialogInitializer;
    private MessageBroadcastReceiver messageReceiver;
    private MessageManager messageManager;
    private GroupInvitationBroadcastReceiver invitationReceiver;
    private FirebaseGroupManager groupManager;
    private boolean notificationsPopupEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        presenter = new MainActivityPresenter(this);
        setSupportActionBar(toolbar);
        initializeNavigationDrawer();
        initializeTabLayout();
        locationHelper = new LocationPointHelper(this);
        initializeMessageReceiver();
        initializeInvitationsReceiver();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkForNotificationClicks(intent);
    }

    public void initializeMessageReceiver() {
        messageManager = new FirebaseMessageManager();
        messageReceiver = new MessageBroadcastReceiver() {
            @Override
            public void onMessageReceived(String messageId) {
                messageManager.getMessageById(messageId, new MessageManager.GetMessageCallback() {
                    @Override
                    public void onMessageSuccess(String messageId, VoiceMessageModel message) {
                        if (tabLayout != null && !isFinishing()) {
                            presenter.getMessageFrom(message);
                        }
                    }

                    @Override
                    public void onMessageFailure(String messageId, Exception ex) {

                    }
                });
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver,
                new IntentFilter(MessageBroadcastReceiver.MESSAGE_ACTION));
    }

    private void checkForNotificationClicks(Intent intent) {
        String action = intent.getAction();
        if ("notificationClick".equals(action)) {
            String tag = intent.getStringExtra("tag");
            if (tag != null) {
                NotificationBroadcastingHelper.handleMessageDescription(this, tag);
            }
        }
    }

    @Override
    public void onGetMessageFrom(String from, String url) {
        if (tabLayout != null && !isFinishing()) {
            if (!notificationsPopupEnabled) {
                unreadDot.setVisibility(View.VISIBLE);
            }
            adapter.getNotificationsFragment().onMessageReceive(notificationsPopupEnabled, from, url);
        }
    }

    private void initializeInvitationsReceiver() {
        groupManager = new FirebaseGroupManager();
        invitationReceiver = new GroupInvitationBroadcastReceiver() {
            @Override
            public void onGroupInvitationReceived(String groupId, String ownerName) {
                if (state == STATE_NEARBY) {
                    groupManager.getGroupById(groupId, new GroupManager.GroupCallback() {
                        @Override
                        public void onGroupSuccess(GroupModel groupModel) {
                            if (tabLayout != null && !isFinishing()) {
                                if (!notificationsPopupEnabled) {
                                    unreadDot.setVisibility(View.VISIBLE);
                                }
                                adapter.getNotificationsFragment().onInvitationReceive(notificationsPopupEnabled, groupModel);
                            }
                        }

                        @Override
                        public void onGroupAlreadyDeleted() {

                        }

                        @Override
                        public void onGroupError(Throwable t) {

                        }
                    });
                }
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(invitationReceiver,
                new IntentFilter(GroupInvitationBroadcastReceiver.INVITATION_ACTION));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentUser == null) {
            presenter.getCurrentUser();
        } else {
            if (state == STATE_NEARBY) {
                locationHelper.startListening(null, presenter);
            }
        }
    }

    private void initializeNavigationDrawer() {
        toggle = new SmoothActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                notificationsPopupEnabled = true;
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                notificationsPopupEnabled = false;

            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setNearbyState() {
        state = STATE_NEARBY;
        updateTabLayout(null, null, null, null);
        invalidateOptionsMenu();
        if (currentUser != null) {
            locationHelper.startListening(null, presenter);
            currentUser.groupId = null;
        }
    }

    private void setGroupState(GroupModel groupModel, ArrayList<InternalUserModel> groupUsers, boolean isAfterInvitation) {
        locationHelper.stopListening();
        presenter.onLocationReceived(null);
        state = STATE_GROUP;
        currentUser.groupId = groupModel.groupId;
        updateTabLayout(groupModel, getUserListWithoutUser(groupUsers), groupModel.owner.equals(currentUser.id), isAfterInvitation);
        invalidateOptionsMenu();
    }

    private ArrayList<InternalUserModel> getUserListWithoutUser(ArrayList<InternalUserModel> groupUsers) {
        ArrayList<InternalUserModel> users = new ArrayList<>();
        for (InternalUserModel user : groupUsers) {
            if (!user.getId().equals(currentUser.id)) {
                users.add(user);
            }
        }
        return users;
    }

    private void initializeTabLayout() {
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        changeTabsFont(tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 1 && unreadDot.getVisibility() == View.VISIBLE) {
                    unreadDot.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void updateTabLayout(@Nullable GroupModel groupModel, @Nullable ArrayList<InternalUserModel> selectedUsers, @Nullable Boolean groupCreatedByUser, @Nullable Boolean isAfterInvitation) {
        if(tabLayout.getVisibility()!=View.VISIBLE){
            tabLayout.setVisibility(View.VISIBLE);
        }

        if (tabLayout.getTabCount() != 0) {
            tabLayout.removeAllTabs();
        }

        String groupName = null;
        if (groupModel != null) {
            groupName = groupModel.name;
        }
        setTabsName(groupName);

        adapter = new TabLayoutPagerAdapter(getSupportFragmentManager(),
                state,
                groupModel,
                selectedUsers,
                groupCreatedByUser,
                isAfterInvitation,
                new InternalUserModel(currentUser.model, currentUser.id, currentUser.nr, currentUser.token, currentUser.groupId)
        );

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.clearOnPageChangeListeners();
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        notificationsPopupEnabled = true;
    }

    private void setTabsName(@Nullable String groupName) {
        if (state == STATE_NEARBY) {
            tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.nearby)));
        } else {
            tabLayout.addTab(tabLayout.newTab().setText(groupName));
        }
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.notifications)));
    }

    private void changeTabsFont(TabLayout tabLayout) {
        ViewGroup viewGroup = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = viewGroup.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup viewGroupTab = (ViewGroup) viewGroup.getChildAt(j);
            int tabChildsCount = viewGroupTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = viewGroupTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getAssets(), "Roboto-Medium.ttf"));
                }
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (state == STATE_NEARBY) {
                if (item.getItemId() == R.id.action_add) {
                    item.setVisible(true);
                } else if (item.getItemId() == R.id.action_delete_group) {
                    item.setVisible(false);
                } else if (item.getItemId() == R.id.action_leave_group) {
                    item.setVisible(false);
                }
            } else if (state == STATE_GROUP) {
                if (item.getItemId() == R.id.action_add) {
                    item.setVisible(false);
                } else {
                    if (adapter.getGroupFragment().isGroupCreatedByUser()) {
                        if (item.getItemId() == R.id.action_delete_group) {
                            item.setVisible(true);
                        }
                        if (item.getItemId() == R.id.action_leave_group) {
                            item.setVisible(false);
                        }
                    } else {
                        if (item.getItemId() == R.id.action_delete_group) {
                            item.setVisible(false);
                        }
                        if (item.getItemId() == R.id.action_leave_group) {
                            item.setVisible(true);
                        }
                    }

                }
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isNetworkAvailable()) {
            int id = item.getItemId();
            if (id == R.id.action_add) {
                ArrayList<InternalUserModel> selectedUsers = adapter.getNearbyFragment().getSelectedUsers();
                if (selectedUsers.size() > 0 && selectedUsers.size() <= 5) {
                    notificationsPopupEnabled = false;
                    createGroupDialogInitializer = new CreateGroupDialogInitializer(this, this, selectedUsers);
                    createGroupDialogInitializer.showCreateGroupDialog();
                    createGroupDialogInitializer.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            notificationsPopupEnabled = true;
                        }
                    });
                } else if (selectedUsers.size() == 0) {
                    adapter.getNearbyFragment().showSnackBar(R.string.users_number_empty, false, null, null, null);
                } else {
                    adapter.getNearbyFragment().showSnackBar(R.string.users_number_too_big, false, null, null, null);
                }
                return true;

            } else if (id == R.id.action_delete_group) {
                notificationsPopupEnabled = false;
                exitGroupDialogInitializer = new ExitGroupDialogInitializer(this, this, ExitGroupDialogInitializer.DIALOG_TYPE_DELETE_GROUP);
                exitGroupDialogInitializer.showExitGroupDialog();
                exitGroupDialogInitializer.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        notificationsPopupEnabled = true;
                    }
                });
                return true;
            } else if (id == R.id.action_leave_group) {
                notificationsPopupEnabled = false;
                exitGroupDialogInitializer = new ExitGroupDialogInitializer(this, this, ExitGroupDialogInitializer.DIALOG_TYPE_LEAVE_GROUP);
                exitGroupDialogInitializer.showExitGroupDialog();
                exitGroupDialogInitializer.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        notificationsPopupEnabled = true;
                    }
                });
                return true;
            }
        } else {
            showNetworkConnectionError();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean checkForLocationPermissions() {
        boolean needToAskForLocation = (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED);
        if (needToAskForLocation) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMISSION);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean checkForMicrophonePermissions() {
        boolean needToAskForLocation = (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED);
        if (needToAskForLocation) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GPS_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (state == STATE_NEARBY) {
                        locationHelper.startListening(null, presenter);
                    }
                } else {
                    showLocationPermissionDeniedDialog();
                }
            }
            case RECORD_AUDIO_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (state == STATE_NEARBY) {
                        adapter.getNearbyFragment().startRecording();
                    } else {
                        adapter.getGroupFragment().startRecording();
                    }
                } else {
                    showMicrophonePermissionDeniedDialog();
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showLocationPermissionDeniedDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_message, null);
        TextView textView = dialogView.findViewById(R.id.dialog_message_text);
        textView.setText(getText(R.string.gps_denied_dialog_content));
        android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(this, R.style.DialogStyle);
        alertBuilder
                .setView(dialogView)
                .setCancelable(false)
                .setTitle(getString(R.string.permissions_denied_dialog_title))
                .setNegativeButton(R.string.dialog_exit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setPositiveButton(R.string.permissions_denied_dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startInstalledAppDetailsActivity(MainActivity.this, SETTINGS_REQUEST);
                    }
                });
        android.support.v7.app.AlertDialog dialog = alertBuilder.create();
        dialog.show();
    }

    private void showMicrophonePermissionDeniedDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_message, null);
        TextView textView = dialogView.findViewById(R.id.dialog_message_text);
        textView.setText(getText(R.string.microphone_denied_dialog_content));
        android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(this, R.style.DialogStyle);
        alertBuilder
                .setView(dialogView)
                .setCancelable(false)
                .setTitle(getString(R.string.permissions_denied_dialog_title))
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.permissions_denied_dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startInstalledAppDetailsActivity(MainActivity.this, SETTINGS_REQUEST);
                    }
                });
        android.support.v7.app.AlertDialog dialog = alertBuilder.create();
        dialog.show();
    }

    public static void startInstalledAppDetailsActivity(Activity context, int requestCode) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivityForResult(i, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_REQUEST) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                checkForLocationPermissions();
            }
        }
    }

    @Override
    public void createGroup(String groupName, ArrayList<InternalUserModel> groupUsers) {
        presenter.invokeGroupCreating(groupName, groupUsers);
    }

    @Override
    public void onGroupCreatingSuccess(GroupModel groupModel, ArrayList<InternalUserModel> selectedUsers) {
        currentUser.groupId = groupModel.groupId;
        setGroupState(groupModel, selectedUsers, false);
        createGroupDialogInitializer.onGroupCreatingSuccess();
        adapter.getGroupFragment().showSnackBar(R.string.group_created_message, false, null, null, null);
    }

    @Override
    public void onGroupCreatingFailure() {
        createGroupDialogInitializer.onGroupCreatingFailure();
    }

    @Override
    public void leaveGroup() {
        presenter.invokeGroupLeaving(adapter.getGroupFragment().getGroupId());
    }

    @Override
    public void deleteGroup() {
        adapter.getGroupFragment().unsubscribeListeners();
        presenter.invokeGroupDeleting(adapter.getGroupFragment().getGroupId());
    }

    @Override
    public void onGroupExitSuccess() {
        exitGroupDialogInitializer.onExitGroupSuccess();
        setNearbyState();
        if (exitGroupDialogInitializer.getDialogType() == ExitGroupDialogInitializer.DIALOG_TYPE_LEAVE_GROUP) {
            adapter.getNearbyFragment().showSnackBar(R.string.group_left_message, false, null, null, null);
        } else {
            adapter.getNearbyFragment().showSnackBar(R.string.group_delete_message, false, null, null, null);
        }
    }

    @Override
    public void onGroupExitFailure(Throwable t) {
        exitGroupDialogInitializer.onExitGroupFailure();
        adapter.getGroupFragment().subscribeListeners();
    }

    @Override
    public void onGroupDeleted() {
        setNearbyState();
        adapter.getNearbyFragment().showSnackBar(R.string.group_has_been_deleted, false, null, null, null);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_about_us) {
            toggle.runWhenIdle(new Runnable() {
                @Override
                public void run() {
                    item.setChecked(false);
                    notificationsPopupEnabled = false;
                    AboutUsDialogInitializer aboutUsDialogInitializer = new AboutUsDialogInitializer(MainActivity.this);
                    aboutUsDialogInitializer.showAboutUsDialog();
                    aboutUsDialogInitializer.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            notificationsPopupEnabled = true;
                        }
                    });

                }
            });
        } else if (id == R.id.nav_terms_of_use) {
            toggle.runWhenIdle(new Runnable() {
                @Override
                public void run() {
                    item.setChecked(false);
                    notificationsPopupEnabled = false;
                    TermsOfUseDialogInitializer termsOfUseDialogInitializer = new TermsOfUseDialogInitializer(MainActivity.this);
                    termsOfUseDialogInitializer.showAboutUsDialog();
                    termsOfUseDialogInitializer.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            notificationsPopupEnabled = true;
                        }
                    });
                }
            });
        } else if (id == R.id.nav_log_out) {
            toggle.runWhenIdle(new Runnable() {
                @Override
                public void run() {
                    item.setChecked(false);
                    if (isNetworkAvailable()) {
                        if (adapter.getGroupFragment() != null) {
                            presenter.logout(currentUser.groupId, adapter.getGroupFragment().isOwner());
                        } else {
                            presenter.logout();
                        }
                    } else {
                        showNetworkConnectionError();
                    }
                }
            });
        } else if (id == R.id.nav_account_data) {
            toggle.runWhenIdle(new Runnable() {
                @Override
                public void run() {
                    item.setChecked(false);
                    if (isNetworkAvailable()) {
                        notificationsPopupEnabled = false;
                        accountDataDialogInitializer = new AccountDataDialogInitializer(MainActivity.this, new AccountDataDialogInitializer.AccountDataDialogListener() {
                            @Override
                            public void onAccountDataChange() {
                                if (locationHelper != null && state == STATE_NEARBY) {
                                    locationHelper.stopListening();
                                    locationHelper.startListening(null, presenter);
                                }
                            }
                        }, currentEmail, currentUser);
                        accountDataDialogInitializer.showAccountDataDialog();
                        accountDataDialogInitializer.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                notificationsPopupEnabled = true;
                            }
                        });
                    } else {
                        showNetworkConnectionError();
                    }
                }
            });
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    private void startInitializationActivity() {
        Intent intent = new Intent(MainActivity.this, InitializationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCurrentUserDataStarted() {
        showSimpleProgressDialog();
    }

    @Override
    public void onCurrentUserDataReady(final String email, com.drivecom.models.UserModel userModel) {
        currentUser = userModel;
        currentEmail = email;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideSimpleProgressDialog();
                setDrawerHeaderText(email);
                if (currentUser.groupId == null) {
                    setNearbyState();
                } else {
                    presenter.initializeCurrentGroup(currentUser.groupId);
                }
                checkForNotificationClicks(getIntent());
            }
        });
    }

    private void showSimpleProgressDialog() {
        notificationsPopupEnabled = false;
        simpleProgressDialogInitializer = new SimpleProgressDialogInitializer(this, getString(R.string.dialog_simple_loading));
        simpleProgressDialogInitializer.show();
        simpleProgressDialogInitializer.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                notificationsPopupEnabled = true;
            }
        });
    }

    private void hideSimpleProgressDialog() {
        if (simpleProgressDialogInitializer != null) {
            simpleProgressDialogInitializer.dismiss();
            simpleProgressDialogInitializer = null;
        }
    }

    @Override
    public void onGroupJoinSuccess(GroupModel groupModel) {
        presenter.getGroupUsers(groupModel, true);
    }

    @Override
    public void onGetGroupUsers(GroupModel groupModel, ArrayList<InternalUserModel> groupUsers, boolean isAfterInvitation) {
        setGroupState(groupModel, groupUsers, isAfterInvitation);
    }

    @Override
    public void onGetGroupUsersError() {

    }

    private void setDrawerHeaderText(String email) {
        View viewById = findViewById(R.id.nav_header_text);
        if (viewById != null && viewById instanceof TextView) {
            ((TextView) viewById).setText(email);
        }
    }

    @Override
    public void onCurrentUserDataError(Exception e) {
        hideSimpleProgressDialog();
        Toast.makeText(this, "Error while accessing data:" + e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void reportLocationError(Throwable t) {

    }

    @Override
    public void setNotificationsPopupEnabled(boolean isEnabled) {
        notificationsPopupEnabled = isEnabled;
    }

    @Override
    public void onCurrentGroupAlreadyDeleted() {
        Toast.makeText(this, R.string.group_already_deleted, Toast.LENGTH_LONG).show();
        setNearbyState();
    }

    @Override
    public void onLogoutSuccess() {
        startInitializationActivity();
    }

    @Override
    public boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void showNetworkConnectionError() {
        showSnackBar(R.string.network_error, false, null, null, null);
    }

    public void showSnackBar(int messageResource, boolean isIndefinite, @Nullable final Runnable actionRunnable, @Nullable Integer actionLabelResource, @Nullable final Runnable onDismissRunnable) {
        Snackbar snackbar = new SnackBarCreator().getSnackBar(this, coordinatorLayout, messageResource, isIndefinite, actionRunnable, actionLabelResource, onDismissRunnable);
        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationHelper.stopListening();
        presenter.onLocationReceived(null);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(invitationReceiver);
        super.onDestroy();
    }

    @Override
    public InternalUserModel getCurrentUser() {
        return new InternalUserModel(currentUser.model, currentUser.id, currentUser.nr, currentUser.token, currentUser.groupId);
    }
}
