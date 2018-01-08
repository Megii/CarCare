package com.drivecom.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.drivecom.R;

public class SimpleProgressDialogInitializer {

    private String progressText;
    private AlertDialog dialog;

    public SimpleProgressDialogInitializer(Context context, String progressText) {
        this.progressText = progressText;
        initializeDialog(context);
    }

    public void initializeDialog(Context context) {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_simple_progress, null);
        TextView messageTextView = dialogView.findViewById(R.id.dialog_simple_progress_message);
        messageTextView.setText(progressText);
        AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.DialogStyle);
        alertBuilder
                .setView(dialogView)
                .setCancelable(false);
        dialog = alertBuilder.create();
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    public void show() {
        dialog.show();
    }

    public void hide() {
        dialog.hide();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}
