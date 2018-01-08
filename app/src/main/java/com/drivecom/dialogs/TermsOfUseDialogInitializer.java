package com.drivecom.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.drivecom.R;

public class TermsOfUseDialogInitializer {

    private Context context;
    private AlertDialog dialog;

    public TermsOfUseDialogInitializer(Context context) {
        this.context = context;
    }

    public void showAboutUsDialog() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_terms_of_use, null);
        AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.DialogStyle);
        alertBuilder
                .setView(dialogView)
                .setCancelable(true)
                .setTitle(R.string.terms_of_use)
                .setNegativeButton(R.string.dialog_ok, null);
        dialog = alertBuilder.create();
        dialog.show();
    }

    public AlertDialog getDialog() {
        return dialog;
    }
}
