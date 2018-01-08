package com.drivecom.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.drivecom.R;

public class ExitGroupDialogInitializer {

    public static final int DIALOG_TYPE_DELETE_GROUP = 10;
    public static final int DIALOG_TYPE_LEAVE_GROUP = 20;

    private Context context;
    private ExitGroupDialogInitializerListener listener;
    private int dialogType;
    private AlertDialog dialog;

    public ExitGroupDialogInitializer(Context context, ExitGroupDialogInitializerListener listener, int dialogType) {
        this.context = context;
        this.listener = listener;
        this.dialogType = dialogType;
    }

    public void showExitGroupDialog() {
        View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_exit_group, null);
        TextView textView = dialogView.findViewById(R.id.dialog_exit_group_info_text);
        String title;
        String positiveButton;
        if (dialogType == DIALOG_TYPE_DELETE_GROUP) {
            textView.setText(context.getString(R.string.dialog_delete_group_question_message));
            title = context.getString(R.string.dialog_delete_group_title);
            positiveButton = context.getString(R.string.dialog_delete_group_positive_button);
        } else {
            textView.setText(context.getString(R.string.dialog_leave_group_question_message));
            title = context.getString(R.string.dialog_leave_group_title);
            positiveButton = context.getString(R.string.dialog_leave_group_positive_button);
        }
        android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.DialogStyle);
        alertBuilder
                .setView(dialogView)
                .setCancelable(false)
                .setTitle(title)
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(positiveButton, null);
        dialog = alertBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(getPositiveButtonOnClickListener());
            }
        });
        dialog.show();
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    private View.OnClickListener getPositiveButtonOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
                getProgressBar().setVisibility(View.VISIBLE);

                getInfoTextView().setVisibility(View.VISIBLE);
                if (dialogType == DIALOG_TYPE_DELETE_GROUP) {
                    getInfoTextView().setText(context.getString(R.string.dialog_delete_group_progress_message));
                    listener.deleteGroup();
                } else if (dialogType == DIALOG_TYPE_LEAVE_GROUP) {
                    getInfoTextView().setText(context.getString(R.string.dialog_leave_group_progress_message));
                    listener.leaveGroup();
                }
            }
        };
    }

    private ProgressBar getProgressBar() {
        return dialog.findViewById(R.id.dialog_exit_group_progress_bar);
    }

    private TextView getInfoTextView() {
        return dialog.findViewById(R.id.dialog_exit_group_info_text);
    }

    public int getDialogType() {
        return dialogType;
    }

    public void onExitGroupSuccess() {
        dialog.dismiss();
    }

    public void onExitGroupFailure() {
        getProgressBar().setVisibility(View.GONE);
        getInfoTextView().setText(context.getString(R.string.sending_error));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(context.getString(R.string.dialog_try_again));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
    }

    public interface ExitGroupDialogInitializerListener {
        void leaveGroup();

        void deleteGroup();
    }
}
