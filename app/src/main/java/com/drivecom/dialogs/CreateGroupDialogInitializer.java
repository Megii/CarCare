package com.drivecom.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.drivecom.R;
import com.drivecom.utils.InternalUserModel;

import java.util.ArrayList;

public class CreateGroupDialogInitializer {

    private Context context;
    private CreateGroupDialogListener listener;
    private ArrayList<InternalUserModel> groupUsers;
    private android.support.v7.app.AlertDialog dialog;

    public CreateGroupDialogInitializer(Context context, CreateGroupDialogListener listener, ArrayList<InternalUserModel> groupUsers) {
        this.context = context;
        this.listener = listener;
        this.groupUsers = groupUsers;
    }

    public void showCreateGroupDialog() {
        View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_create_group, null);
        ((EditText) dialogView.findViewById(R.id.dialog_create_group_name_edit_text)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() != 0) {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColorStateList(R.color.colorAccent));
                } else {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColorStateList(R.color.textDisabledDarkBackground));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.DialogStyle);
        alertBuilder
                .setView(dialogView)
                .setCancelable(false)
                .setTitle(context.getString(R.string.dialog_create_group_title))
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.dialog_create_group_positive_button, null);
        dialog = alertBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColorStateList(R.color.textDisabledDarkBackground));
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
                getTextInputLayout().setVisibility(View.GONE);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
                getProgressBar().setVisibility(View.VISIBLE);
                getInfoTextView().setText(context.getString(R.string.invitations_sending));
                getInfoTextView().setVisibility(View.VISIBLE);
                listener.createGroup(getEditText().getText().toString(), groupUsers);
            }
        };
    }

    private ProgressBar getProgressBar() {
        return dialog.findViewById(R.id.dialog_create_group_progress_bar);
    }

    private TextView getInfoTextView() {
        return dialog.findViewById(R.id.dialog_create_group_info_text);
    }

    private View getTextInputLayout() {
        return dialog.findViewById(R.id.dialog_create_group_name_text_input_layout);
    }

    private EditText getEditText() {
        return dialog.findViewById(R.id.dialog_create_group_name_edit_text);
    }

    public void onGroupCreatingSuccess() {
        dialog.dismiss();
    }

    public void onGroupCreatingFailure() {
        getProgressBar().setVisibility(View.GONE);
        getInfoTextView().setText(context.getString(R.string.sending_error));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(context.getString(R.string.dialog_try_again));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
    }

    public interface CreateGroupDialogListener {
        void createGroup(String groupName, ArrayList<InternalUserModel> groupUsers);
    }
}
