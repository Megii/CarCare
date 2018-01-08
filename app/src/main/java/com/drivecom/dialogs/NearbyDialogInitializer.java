package com.drivecom.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.drivecom.R;
import com.drivecom.group.GroupFragmentInterface;
import com.drivecom.models.CoordinatesModel;
import com.drivecom.nearby.NearbyListAdapter;
import com.drivecom.remote.FirebaseUserDataProvider;
import com.drivecom.remote.UserDataProvider;
import com.drivecom.utils.InternalUserModel;
import com.drivecom.utils.LocationPointHelper;

import java.util.ArrayList;

public class NearbyDialogInitializer
        implements NearbyListAdapter.OnItemHighlightListener,
        UserDataProvider.NearbyChangedCallback {

    private AlertDialog dialog;
    private NearbyListAdapter adapter;
    private GroupFragmentInterface listener;
    private Context context;
    private ArrayList<InternalUserModel> currentUsers;
    private UserDataProvider userManager;
    private RecyclerView recyclerView;
    private LocationPointHelper locationHelper;

    public NearbyDialogInitializer(Context context, GroupFragmentInterface listener, ArrayList<InternalUserModel> currentUsers) {
        this.context = context;
        this.listener = listener;
        this.currentUsers = currentUsers;
        locationHelper = new LocationPointHelper(context);
    }

    public void showNearbyDialog() {
        View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_nearby, null);
        initializeRecyclerView(dialogView);
        android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.DialogStyle);
        alertBuilder
                .setView(dialogView)
                .setCancelable(false)
                .setTitle(context.getString(R.string.dialog_nearby_list_title))
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        unsubscribeNearbyChanges();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.dialog_invite, null);
        dialog = alertBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColorStateList(R.color.textDisabledDarkBackground));
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(getPositiveButtonOnClickListener());
            }
        });
        dialog.show();
        downloadNearbyList();
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    private View.OnClickListener getPositiveButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInvitations();
            }
        };
    }

    private void sendInvitations() {
        unsubscribeNearbyChanges();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        getProgressBar().setVisibility(View.VISIBLE);
        getInfoTextView().setText(context.getString(R.string.invitations_sending));
        getInfoTextView().setVisibility(View.VISIBLE);
        listener.sendInvitations(true, adapter.getSelectedUsers());
    }

    private void initializeRecyclerView(View dialogView) {
        adapter = new NearbyListAdapter(context, this, getEmptySlots());
        recyclerView = dialogView.findViewById(R.id.dialog_nearby_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void downloadNearbyList() {
        getProgressBar().setVisibility(View.VISIBLE);
        getInfoTextView().setVisibility(View.VISIBLE);
        getInfoTextView().setText(context.getString(R.string.nearby_list_downloading));
        subscribeNearbyChanges();
    }

    public void subscribeNearbyChanges() {
        locationHelper.startListening(null, new LocationPointHelper.LocationPointCallback() {
            @Override
            public void onLocationReceived(Location location) {
                updateCoords(location);
            }

            @Override
            public void onLocationError(Throwable t) {

            }
        });
        if (userManager == null) {
            userManager = new FirebaseUserDataProvider();
        }
        userManager.subscribeNearbyChanges(this);
    }

    private void updateCoords(Location location) {
        CoordinatesModel model = location == null ? new CoordinatesModel(null, null) : new CoordinatesModel(location.getLatitude(), location.getLongitude());
        userManager.updateCurrentUserCoords(model);
    }

    public void unsubscribeNearbyChanges() {
        if (locationHelper != null) {
            locationHelper.stopListening();
        }
        if (userManager != null) {
            userManager.unsubscribeNearbyChanges(this);
            updateCoords(null);
        }
    }

    @Override
    public void onNearbyElementAdded(final String elementKey, final com.drivecom.models.UserModel newNearbyUser) {
        if (!isInGroup(newNearbyUser.id)) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.println(Log.ASSERT, "NearbyFragment", "onNearbyElementAdded: " + newNearbyUser.model);
                    try {
                        int lastPosition = adapter.getItemCount();
                        adapter.addItem(lastPosition, new InternalUserModel(newNearbyUser.model, newNearbyUser.id, newNearbyUser.nr, newNearbyUser.token, newNearbyUser.groupId));
                        updateRecyclerViewVisibility(true);
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    private boolean isInGroup(String userId) {
        for (InternalUserModel user : currentUsers) {
            if (user != null && user.getId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onNearbyElementChanged(final String elementKey, final com.drivecom.models.UserModel changedNearbyUser) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.println(Log.ASSERT, "NearbyFragment", "onNearbyElementChanged: " + changedNearbyUser.model);
                try {
                    int position = adapter.getItemPositionById(changedNearbyUser.id);
                    if (position != -1) {
                        adapter.updateItem(position, new InternalUserModel(changedNearbyUser.model, changedNearbyUser.id, changedNearbyUser.nr, changedNearbyUser.token, changedNearbyUser.groupId));
                    }
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onNearbyElementRemoved(final String elementKey, final String itemId) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.println(Log.ASSERT, "NearbyFragment", "onNearbyElementRemoved: " + elementKey);
                try {
                    int position = adapter.getItemPositionById(itemId);
                    if (position != -1) {
                        adapter.deleteItem(position);
                    }
                    if (adapter.getItemCount() == 0) {
                        updateRecyclerViewVisibility(false);
                    }
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    private void updateRecyclerViewVisibility(boolean isVisible) {
        if (isVisible) {
            recyclerView.setVisibility(View.VISIBLE);
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
            ProgressBar progressBar = getProgressBar();
            progressBar.setVisibility(View.GONE);
            getInfoTextView().setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            getInfoTextView().setVisibility(View.VISIBLE);
            getInfoTextView().setText(context.getString(R.string.empty_nearby_list));
            ProgressBar progressBar = getProgressBar();
            progressBar.setVisibility(View.VISIBLE);
            dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
        }
    }

    private int getEmptySlots() {
        int emptySlots = 0;
        for (int i = 0; i < currentUsers.size(); i++) {
            if (currentUsers.get(i) == null) {
                emptySlots++;
            }
        }
        return emptySlots;
    }

    public void onSendInvitationsSuccess() {
        dialog.dismiss();
    }

    public void onSendInvitationsFailure() {
        getProgressBar().setVisibility(View.GONE);
        getInfoTextView().setText(context.getString(R.string.download_error));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(context.getString(R.string.dialog_try_again));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemHighlight(int itemsLeft) {
        if (adapter.getSelectedUsers().size() > 0) {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColorStateList(R.color.colorAccent));
        } else {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColorStateList(R.color.textDisabledDarkBackground));
        }
    }

    private ProgressBar getProgressBar() {
        return dialog.findViewById(R.id.dialog_nearby_progress_bar);
    }

    private TextView getInfoTextView() {
        return dialog.findViewById(R.id.dialog_nearby_info_text);
    }

    public void onStop() {
        unsubscribeNearbyChanges();
        dialog.dismiss();
    }
}

