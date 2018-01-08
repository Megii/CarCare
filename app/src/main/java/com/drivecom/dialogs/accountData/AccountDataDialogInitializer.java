package com.drivecom.dialogs.accountData;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.drivecom.R;
import com.drivecom.models.UserModel;

public class AccountDataDialogInitializer implements AccountDataViewInterface {

    private AlertDialog dialog;
    private AccountDataPresenterInterface presenter;
    private Context context;
    private View progressBar;
    private TextView infoText;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText registrationNumberEditText;
    private String accountEmail;
    private String accountName;
    private String accountRegistrationNumber;
    private UserModel currentUser;
    private AccountDataDialogListener listener;

    public AccountDataDialogInitializer(Context context, AccountDataDialogListener listener, String currentEmail, UserModel currentUser) {
        this.context = context;
        this.listener = listener;
        accountEmail = currentEmail;
        accountName = currentUser.model;
        accountRegistrationNumber = currentUser.nr;
        this.currentUser = currentUser;
        presenter = new AccountDataPresenter(this);
    }

    public void showAccountDataDialog() {
        View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_account_data, null);
        initializeEmailEditText(dialogView);
        initializeNameEditText(dialogView);
        initializeRegistrationNumberEditText(dialogView);
        progressBar = dialogView.findViewById(R.id.dialog_account_data_progress_bar);
        infoText = dialogView.findViewById(R.id.dialog_account_data_info_text);
        android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.DialogStyle);
        alertBuilder
                .setView(dialogView)
                .setCancelable(false)
                .setTitle(context.getString(R.string.dialog_account_data_title))
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.dialog_account_data_positive_button, null);
        dialog = alertBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(getPositiveButtonOnClickListener());
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColorStateList(R.color.textDisabledDarkBackground));
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
                emailEditText.setVisibility(View.GONE);
                nameEditText.setVisibility(View.GONE);
                registrationNumberEditText.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                infoText.setText(context.getString(R.string.dialog_account_data_progress_message));
                infoText.setVisibility(View.VISIBLE);
                saveData();
            }
        };
    }

    private void initializeEmailEditText(View dialogView) {
        emailEditText = dialogView.findViewById(R.id.dialog_account_data_email_edit_text);
        emailEditText.setText(accountEmail);
    }

    private void initializeNameEditText(View dialogView) {
        nameEditText = dialogView.findViewById(R.id.dialog_account_data_name_edit_text);
        nameEditText.setText(accountName);
        nameEditText.addTextChangedListener(getOnTextChangedListener());
    }

    private void initializeRegistrationNumberEditText(View dialogView) {
        registrationNumberEditText = dialogView.findViewById(R.id.dialog_account_data_registration_number_edit_text);
        registrationNumberEditText.setText(accountRegistrationNumber);
        registrationNumberEditText.addTextChangedListener(getOnTextChangedListener());
    }


    private void saveData() {
        presenter.invokeAccountDataChangesSaving(currentUser, nameEditText.getText().toString(), registrationNumberEditText.getText().toString());
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
    }

    private TextWatcher getOnTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!nameEditText.getText().toString().equals(accountName) || !registrationNumberEditText.getText().toString().equals(accountRegistrationNumber)) {
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
        };
    }

    @Override
    public void onAccountDataChangesSavingSuccess() {
        dialog.dismiss();
        listener.onAccountDataChange();
    }

    @Override
    public void onAccountDataChangesSavingFailure() {
        progressBar.setVisibility(View.GONE);
        infoText.setText(context.getString(R.string.sending_error));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(context.getString(R.string.dialog_try_again));
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
    }

    public interface AccountDataDialogListener {
        void onAccountDataChange();
    }
}
