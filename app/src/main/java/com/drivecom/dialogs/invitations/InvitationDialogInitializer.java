package com.drivecom.dialogs.invitations;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.drivecom.R;
import com.drivecom.models.GroupModel;

public class InvitationDialogInitializer implements InvitationViewInterface {

    private Context context;
    private InvitationPresenterInterface presenter;
    private InvitationDialogInitializerListener listener;
    private TextView messageTextView;
    private TextView infoTextView;
    private ProgressBar progressBar;
    private AlertDialog dialog;
    private GroupModel groupModel;

    public InvitationDialogInitializer(Context context, InvitationDialogInitializerListener listener, GroupModel groupModel) {
        this.context = context;
        this.listener = listener;
        this.groupModel = groupModel;
        presenter = new InvitationPresenter(this);
    }

    public void showInvitationDialog() {
        View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_invitation, null);
        messageTextView = dialogView.findViewById(R.id.dialog_invitation_message_text);
        String message = context.getString(R.string.dialog_invitation_question_message) + " <b>" + groupModel.name + "</b>?";
        messageTextView.setText(Html.fromHtml(message));
        infoTextView = dialogView.findViewById(R.id.dialog_invitation_info_text);
        progressBar = dialogView.findViewById(R.id.dialog_invitation_progress_bar);
        android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.DialogStyle);
        alertBuilder
                .setView(dialogView)
                .setCancelable(false)
                .setTitle(R.string.dialog_invitation_title)
                .setNegativeButton(R.string.dialog_invitation_neutral_button, null)
                .setPositiveButton(R.string.dialog_invitation_positive_button, null);
        dialog = alertBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        answerOnInvitation(true);
                    }
                });
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        answerOnInvitation(false);
                    }
                });
            }
        });
        dialog.show();
    }

    public AlertDialog getAlertDialog() {
        return dialog;
    }

    private void answerOnInvitation(boolean isAccept) {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
        messageTextView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        if (isAccept) {
            infoTextView.setText(context.getString(R.string.dialog_invitation_accept_progress_message));
            presenter.acceptInvitation(groupModel.groupId);
        } else {
            infoTextView.setText(context.getString(R.string.dialog_invitation_reject_progress_message));
            presenter.rejectInvitation(groupModel.groupId);
        }
    }

    @Override
    public void onSuccess(boolean isAccept) {
        dialog.dismiss();
        if (isAccept) {
            listener.onGroupJoinSuccess(groupModel);
        } else {
            listener.onInvitationReject(groupModel.groupId);
        }
    }

    @Override
    public void onError() {
        progressBar.setVisibility(View.GONE);
        infoTextView.setText(context.getString(R.string.error));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
                infoTextView.setVisibility(View.GONE);
                messageTextView.setVisibility(View.VISIBLE);
            }
        }, 1000);

    }

    public interface InvitationDialogInitializerListener {
        void onGroupJoinSuccess(GroupModel groupModel);

        void onInvitationReject(String groupId);
    }
}
