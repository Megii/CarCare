package com.drivecom.initialization;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.drivecom.R;
import com.drivecom.main.MainActivity;
import com.drivecom.utils.ErrorMessageProvider;
import com.drivecom.utils.SnackBarCreator;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InitializationActivity extends AppCompatActivity implements InitializationActivityInterface {

    private static final int GPS_PERMISSION = 15;
    private static final int SETTINGS_REQUEST = 25;

    @BindView(R.id.initialization_root_layout)
    protected View rootLayout;
    @BindView(R.id.initialization_login_layout)
    protected View loginLayout;
    @BindView(R.id.initialization_registration_layout)
    protected View registrationLayout;
    @BindView(R.id.initialization_login_email_edit_text)
    protected EditText loginEmailEditText;
    @BindView(R.id.initialization_login_password_edit_text)
    protected EditText loginPasswordEditText;
    @BindView(R.id.initialization_login_button)
    protected View loginButton;
    @BindView(R.id.initialization_login_button_text)
    protected TextView loginButtonText;
    @BindView(R.id.initialization_login_small_register_card_view)
    protected View smallRegisterButton;
    @BindView(R.id.initialization_registration_scroll_view)
    protected ScrollView registrationScrollView;
    @BindView(R.id.initialization_registration_email_edit_text)
    protected EditText registrationEmailEditText;
    @BindView(R.id.initialization_registration_password_edit_text)
    protected EditText registrationPasswordEditText;
    @BindView(R.id.initialization_registration_password_confirmation_edit_text)
    protected EditText registrationPasswordConfirmationEditText;
    @BindView(R.id.initialization_registration_password_confirmation_text_input)
    protected TextInputLayout registrationPasswordConfirmationTextInputLayout;
    @BindView(R.id.initialization_registration_name_edit_text)
    protected EditText registrationNameEditText;
    @BindView(R.id.initialization_registration_registration_number_edit_text)
    protected EditText registrationNumberEditText;
    @BindView(R.id.initialization_register_button)
    protected View registerButton;
    @BindView(R.id.initialization_register_button_text)
    protected TextView registerButtonText;
    @BindView(R.id.initialization_registration_small_login_card_view)
    protected View smallLoginButton;
    @BindView(R.id.initialization_progress_layout)
    protected View progressLayout;
    @BindView(R.id.initialization_progress_bar)
    protected View progressBar;
    @BindView(R.id.initialization_progress_text)
    protected TextView progressText;

    private InitializationPresenterInterface presenter;
    private AlertDialog dialog;
    private Boolean isRegistration = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialization);
        ButterKnife.bind(this);
        presenter = new InitializationPresenter(this);
        if (presenter.isUserLoggedIn()) {
            startMainActivity();
        } else {
            initializeLoginLayout();
            initializeRegisterLayout();
        }
    }

    private void initializeLoginLayout() {
        loginButton.setEnabled(false);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRegistration = false;
                if (isNetworkAvailable()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkForLocationPermissions()) {
                        startLoginAction();
                    }
                } else {
                    showSnackBar(R.string.network_error, false, null, null, null);
                }
            }
        });
        smallRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToRegistrationLayout();
            }
        });
        loginEmailEditText.addTextChangedListener(loginTextWatcher(loginEmailEditText));
        loginPasswordEditText.addTextChangedListener(loginTextWatcher(loginPasswordEditText));
    }

    private TextWatcher loginTextWatcher(final EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().contains(" ")) {
                    editText.setText(charSequence.toString().replaceAll("\\s+", ""));
                    editText.setSelection(charSequence.length() - 1);
                }
                if (checkIfLoginEditTextsAreFilled()) {
                    loginButton.setEnabled(true);
                    loginButtonText.setTextColor(ContextCompat.getColor(InitializationActivity.this, R.color.textPrimaryDarkBackground));
                } else {
                    loginButton.setEnabled(false);
                    loginButtonText.setTextColor(ContextCompat.getColor(InitializationActivity.this, R.color.textDisabledDarkBackground));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    private void initializeRegisterLayout() {
        removeRegistrationEditTextsFocus();
        registerButton.setEnabled(false);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRegistration = true;
                if (isNetworkAvailable()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkForLocationPermissions()) {
                        startRegistrationAction();
                    }
                } else {
                    showSnackBar(R.string.network_error, false, null, null, null);
                }
            }
        });
        smallLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToLoginLayout();
            }
        });
        registrationEmailEditText.addTextChangedListener(registrationTextWatcher(registrationEmailEditText));
        registrationPasswordEditText.addTextChangedListener(registrationTextWatcher(registrationPasswordEditText));
        registrationPasswordConfirmationEditText.addTextChangedListener(registrationTextWatcher(registrationPasswordConfirmationEditText));
        registrationNumberEditText.addTextChangedListener(registrationTextWatcher(registrationNumberEditText));
        registrationNameEditText.addTextChangedListener(registrationTextWatcher(registrationNameEditText));
    }

    private void removeRegistrationEditTextsFocus() {
        registrationScrollView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
        registrationScrollView.setFocusable(true);
        registrationScrollView.setFocusableInTouchMode(true);
        registrationScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.requestFocusFromTouch();
                return false;
            }
        });
    }

    private void startLoginAction() {
        String email = loginEmailEditText.getText().toString();
        String password = loginPasswordEditText.getText().toString();
        presenter.invokeLogin(email, password);
    }

    private boolean checkIfLoginEditTextsAreFilled() {
        return !loginEmailEditText.getText().toString().equals("")
                && !loginPasswordEditText.getText().toString().equals("");
    }

    private void startRegistrationAction() {
        String email = registrationEmailEditText.getText().toString();
        String password = registrationPasswordEditText.getText().toString();
        String number = registrationNumberEditText.getText().toString();
        String name = registrationNameEditText.getText().toString();
        presenter.invokeRegistration(email, password, number, name);
    }

    private TextWatcher registrationTextWatcher(final EditText editText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().contains(" ")) {
                    editText.setText(charSequence.toString().replaceAll("\\s+", ""));
                    editText.setSelection(charSequence.length() - 1);
                }
                if (!registrationPasswordConfirmationEditText.getText().toString().equals(registrationPasswordEditText.getText().toString())) {
                    registrationPasswordConfirmationTextInputLayout.setError(getString(R.string.password_confirm_error_message));
                } else {
                    registrationPasswordConfirmationTextInputLayout.setError(null);
                }

                if (checkIfRegistrationEditTextsAreFilled()) {
                    registerButton.setEnabled(true);
                    registerButtonText.setTextColor(ContextCompat.getColor(InitializationActivity.this, R.color.textPrimaryDarkBackground));
                } else {
                    registerButton.setEnabled(false);
                    registerButtonText.setTextColor(ContextCompat.getColor(InitializationActivity.this, R.color.textDisabledDarkBackground));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    private boolean checkForLocationPermissions() {
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GPS_PERMISSION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (isRegistration) {
                        startRegistrationAction();
                    } else {
                        startLoginAction();
                    }
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                } else {
                    showLocationPermissionDeniedDialog();
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
                        startInstalledAppDetailsActivity(InitializationActivity.this, SETTINGS_REQUEST);
                    }
                });
        dialog = alertBuilder.create();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SETTINGS_REQUEST) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkForLocationPermissions()) {
                if (isRegistration) {
                    startRegistrationAction();
                } else {
                    startLoginAction();
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRegistrationStart() {
        registrationLayout.setVisibility(View.INVISIBLE);
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressText.setText(getString(R.string.progress_register_message));
    }

    @Override
    public void onRegistrationSuccess() {
        progressBar.setVisibility(View.GONE);
        progressText.setText(getString(R.string.progress_register_success_message));
        startMainActivityWithDelay();
    }

    @Override
    public void onRegistrationFailure(Exception e) {
        progressBar.setVisibility(View.GONE);
        String message = ErrorMessageProvider.provideRegisterErrorMessageViaExceptionType(
                getResources(), e, getString(R.string.progress_error_message));
        progressText.setText(message);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressLayout.setVisibility(View.GONE);
                registrationLayout.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }

    @Override
    public void onLoginStart() {
        loginLayout.setVisibility(View.INVISIBLE);
        progressLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        progressText.setText(getString(R.string.progress_login_message));
    }

    @Override
    public void onLoginSuccess() {
        progressBar.setVisibility(View.GONE);
        progressText.setText(getString(R.string.progress_login_success_message));
        startMainActivityWithDelay();
    }

    @Override
    public void onLoginFailure(Exception e) {
        progressBar.setVisibility(View.GONE);
        String message = ErrorMessageProvider.provideLoginErrorMessageViaExceptionType(
                getResources(), e, getString(R.string.progress_error_message));
        progressText.setText(message);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
            }
        }, 1000);
    }

    private void startMainActivity() {
        Intent intent = new Intent(InitializationActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void startMainActivityWithDelay() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startMainActivity();
            }
        }, 1000);
    }

    private boolean checkIfRegistrationEditTextsAreFilled() {
        return !registrationEmailEditText.getText().toString().equals("")
                && !registrationPasswordEditText.getText().toString().equals("")
                && !registrationNameEditText.getText().toString().equals("")
                && !registrationNumberEditText.getText().toString().equals("")
                && registrationPasswordEditText.getText().toString().equals(registrationPasswordConfirmationEditText.getText().toString());
    }

    private void switchToLoginLayout() {
        registrationLayout.setVisibility(View.INVISIBLE);
        loginLayout.setVisibility(View.VISIBLE);
    }

    private void switchToRegistrationLayout() {
        loginLayout.setVisibility(View.INVISIBLE);
        registrationLayout.setVisibility(View.VISIBLE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void showSnackBar(int messageResource, boolean isIndefinite, @Nullable final Runnable actionRunnable, @Nullable Integer actionLabelResource, @Nullable final Runnable onDismissRunnable) {
        Snackbar snackbar = new SnackBarCreator().getSnackBar(this, rootLayout, messageResource, isIndefinite, actionRunnable, actionLabelResource, onDismissRunnable);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(this, R.color.cardview_dark_background));
        snackbar.show();
    }
}
