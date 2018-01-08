package com.drivecom.nearby;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.drivecom.R;
import com.drivecom.dialogs.recorder.RecordingDialogInitializer;
import com.drivecom.main.MainActivityInterface;
import com.drivecom.utils.InternalUserModel;
import com.drivecom.utils.SnackBarCreator;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NearbyFragment extends Fragment implements NearbyFragmentInterface {

    public static final String FRAGENT_TAG = "NearbyFragmentTag";
    public static final String NEARBY_USERS_TAG = "NearbyUsersTag";

    @BindView(R.id.fragment_nearby_root_layout)
    CoordinatorLayout rootLayout;
    @BindView(R.id.fragment_nearby_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_nearby_fab)
    FloatingActionButton fab;
    @BindView(R.id.fragment_nearby_empty_message_text)
    View emptyMessage;

    private NearbyListAdapter adapter;
    private RecordingDialogInitializer recordingDialogInitializer;
    private NearbyPresenterInterface presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nearby,
                container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        presenter = new NearbyPresenter(this);

        initializeRecyclerView();
        initializeFab();
    }

    @Override
    public void onResume() {
        super.onResume();
        emptyMessage.setVisibility(View.VISIBLE);
        presenter.subscribeNearbyChanges();
    }

    private void initializeRecyclerView() {
        adapter = new NearbyListAdapter(getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2) {
            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void initializeFab() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivityInterface().isNetworkAvailable()) {
                    if (getSelectedUsers().size() != 0) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || getActivityInterface().checkForMicrophonePermissions()) {
                            startRecording();
                        }
                    } else {
                        showSnackBar(R.string.users_number_empty, false, null, null, null);
                    }
                } else {
                    showSnackBar(R.string.network_error, false, null, null, null);
                }
            }
        });
    }

    public void startRecording() {
        recordingDialogInitializer = new RecordingDialogInitializer(getContext(), new RecordingDialogInitializer.RecordingDialogListener() {
            @Override
            public void onRecordingError() {
                showSnackBar(R.string.recorder_error, false, null, null, null);
            }
        }, getSelectedUsers());
        getActivityInterface().setNotificationsPopupEnabled(false);
        recordingDialogInitializer.showRecordingDialog();
        recordingDialogInitializer.getDialog().setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                getActivityInterface().setNotificationsPopupEnabled(true);
            }
        });
    }

    public ArrayList<InternalUserModel> getSelectedUsers() {
        return adapter.getSelectedUsers();
    }

    public void showSnackBar(int messageResource, boolean isIndefinite, @Nullable final Runnable actionRunnable, @Nullable Integer actionLabelResource, @Nullable final Runnable onDismissRunnable) {
        Snackbar snackbar = new SnackBarCreator().getSnackBar(getContext(), rootLayout, messageResource, isIndefinite, actionRunnable, actionLabelResource, onDismissRunnable);
        snackbar.show();
    }

    @Override
    public void onStop() {
        if (recordingDialogInitializer != null) {
            recordingDialogInitializer.onStop();
        }
        super.onStop();
    }

    private MainActivityInterface getActivityInterface() {
        return (MainActivityInterface) getActivity();
    }

    private void updateRecyclerViewVisibility(boolean isVisible) {
        if (isVisible) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyMessage.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            emptyMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNearbyElementAdded(final String elementKey, final com.drivecom.models.UserModel newNearbyUser) {
        if (adapter.getItemPositionById(newNearbyUser.id) == -1) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.println(Log.ASSERT, "NearbyFragment", "onNearbyElementAdded: " + newNearbyUser.model);
                    try {
                        int position = Integer.valueOf(elementKey);
                        adapter.addItem(position, new InternalUserModel(newNearbyUser.model, newNearbyUser.id, newNearbyUser.nr, newNearbyUser.token, newNearbyUser.groupId));
                        updateRecyclerViewVisibility(true);
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onNearbyElementChanged(final String elementKey, final com.drivecom.models.UserModel changedNearbyUser) {
        if (adapter.getItemPositionById(changedNearbyUser.id) != -1) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.println(Log.ASSERT, "NearbyFragment", "onNearbyElementChanged: " + changedNearbyUser.model);
                    try {
                        int position = Integer.valueOf(elementKey);
                        adapter.updateItem(position, new InternalUserModel(changedNearbyUser.model, changedNearbyUser.id, changedNearbyUser.nr, changedNearbyUser.token, changedNearbyUser.groupId));
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onNearbyElementRemoved(final String elementKey, final String itemId) {
        if (adapter.getItemPositionById(itemId) != -1) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Log.println(Log.ASSERT, "NearbyFragment", "onNearbyElementRemoved: " + elementKey);
                    try {
                        int position = Integer.valueOf(elementKey);
                        adapter.deleteItem(position);
                        if (adapter.getItemCount() == 0) {
                            updateRecyclerViewVisibility(false);
                        }
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onPause() {
        adapter.clearAdapters();
        presenter.unsubscribeNearbyChanges();
        super.onPause();
    }
}
