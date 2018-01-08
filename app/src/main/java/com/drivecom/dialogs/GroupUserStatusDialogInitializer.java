package com.drivecom.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.drivecom.R;
import com.drivecom.group.GroupFragment;
import com.drivecom.group.GroupFragmentInterface;
import com.drivecom.utils.InternalUserModel;

import java.util.ArrayList;

public class GroupUserStatusDialogInitializer {

    private Context context;
    private GroupFragmentInterface listener;
    private GroupFragment.GroupUser user;
    private AlertDialog dialog;
    private ProgressBar progressBar;
    private TextView infoText;

    public GroupUserStatusDialogInitializer(Context context, GroupFragmentInterface listener, GroupFragment.GroupUser user) {
        this.context = context;
        this.listener = listener;
        this.user = user;
    }

    public void showGroupUserStatusDialog() {
        View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_group_user_status, null);
        progressBar = dialogView.findViewById(R.id.dialog_group_user_status_progress_bar);
        infoText = dialogView.findViewById(R.id.dialog_group_user_status_info_text);
        infoText.setText(getMessage());
        String negativeButton;
        if (user.getGroupStatus() == GroupFragment.USER_STATUS_ACTIVE) {
            negativeButton = context.getString(R.string.dialog_ok);
        } else {
            negativeButton = context.getString(R.string.dialog_cancel);
        }
        android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.DialogStyle);
        alertBuilder
                .setView(dialogView)
                .setCancelable(false)
                .setTitle(user.getName())
                .setNegativeButton(negativeButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.dialog_try_again, null);
        dialog = alertBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if (user.getGroupStatus() == GroupFragment.USER_STATUS_ACTIVE) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
                } else {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sendInvitation();
                        }
                    });
                }
            }
        });
        dialog.show();
    }

    private String getMessage() {
        String message = "";
        switch (user.getGroupStatus()) {
            case GroupFragment.USER_STATUS_ACTIVE:
                message = context.getString(R.string.group_user_active_status);
                break;
            case GroupFragment.USER_STATUS_INVITATION_REJECTED:
                message = context.getString(R.string.group_user_invitation_rejected_status) + " " + context.getString(R.string.dialog_group_user_status_send_invitation_message);
                break;
            case GroupFragment.USER_STATUS_LEFT:
                message = context.getString(R.string.group_user_left) + " " + context.getString(R.string.dialog_group_user_status_send_invitation_message);
                break;
            case GroupFragment.USER_STATUS_WAITING:
                message = context.getString(R.string.group_user_waiting_status) + " " + context.getString(R.string.dialog_group_user_status_send_invitation_message);
                break;
        }
        return message;
    }

    private void sendInvitation() {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
        infoText.setText(context.getString(R.string.dialog_group_user_status_progress_message));
        progressBar.setVisibility(View.VISIBLE);
        ArrayList<InternalUserModel> list = new ArrayList<>();
        list.add(new InternalUserModel(user.getName(), user.getId(), user.getRegistrationId(), user.getToken(), null));
        listener.sendInvitations(false, list);
    }

    public void onInvitationResendSuccess() {
        dialog.dismiss();
    }

    public void onInvitationResendFailure() {
        if (user.getGroupStatus() != GroupFragment.USER_STATUS_ACTIVE) {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
        }
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
        infoText.setText(context.getString(R.string.sending_error));
        progressBar.setVisibility(View.GONE);
    }
}
