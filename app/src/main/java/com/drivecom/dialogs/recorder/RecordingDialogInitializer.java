package com.drivecom.dialogs.recorder;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.drivecom.R;
import com.drivecom.utils.FileManager;
import com.drivecom.utils.InternalUserModel;

import java.util.ArrayList;
import java.util.List;

public class RecordingDialogInitializer implements RecordingViewInterface {

    private Context context;
    private RecordingDialogListener listener;
    private List<InternalUserModel> userList;
    private android.support.v7.app.AlertDialog dialog;
    private RecordingPresenterInterface presenter;
    private ImageView recordingButton;
    private TextView timeTextView;
    private TextView infoTextView;
    private ProgressBar progressBar;

    public RecordingDialogInitializer(Context context, RecordingDialogListener listener, ArrayList<InternalUserModel> userList) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
        this.presenter = new RecordingPresenter(this, new FileManager(context));
    }

    public void showRecordingDialog() {
        View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_recording, null);
        recordingButton = dialogView.findViewById(R.id.dialog_recording_button);
        timeTextView = dialogView.findViewById(R.id.dialog_recording_time_text);
        infoTextView = dialogView.findViewById(R.id.dialog_recording_info_text);
        progressBar = dialogView.findViewById(R.id.dialog_recording_progress_bar);
        android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.DialogStyle);
        alertBuilder
                .setView(dialogView)
                .setCancelable(false)
                .setTitle(context.getString(R.string.dialog_message_title))
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        presenter.resetData();
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.dialog_send, null);
        dialog = alertBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(getPositiveButtonOnClickListener());
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColorStateList(R.color.textDisabledDarkBackground));
                presenter.startRecording();
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
                presenter.invokeRecordingUpload();
            }
        };
    }

    @Override
    public List<InternalUserModel> getUserList() {
        return userList;
    }

    @Override
    public void onUpdateTime(String time) {
        timeTextView.setText(time);
    }

    @Override
    public void onRecordingStarted() {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(context.getResources().getColorStateList(R.color.colorAccent));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
    }

    @Override
    public void onUploadStarted() {
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        timeTextView.setVisibility(View.GONE);
        recordingButton.setVisibility(View.GONE);
        infoTextView.setText(context.getString(R.string.dialog_recording_progress_message));
        infoTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUploadSuccess() {
        presenter.resetData();
        dialog.dismiss();
    }

    @Override
    public void onUploadProgress(int percent) {
        progressBar.setProgress(percent);
        String message = context.getResources().getString(R.string.dialog_recording_progress_message) + " (" + percent + "%)";
        infoTextView.setText(message);
    }

    @Override
    public void onUploadFailure() {
        progressBar.setVisibility(View.GONE);
        infoTextView.setText(context.getString(R.string.sending_error));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(context.getString(R.string.dialog_try_again));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
    }

    @Override
    public void onRecordingError() {
        dialog.dismiss();
        listener.onRecordingError();
    }

    public void onStop() {
        presenter.resetData();
        dialog.dismiss();
    }

    public interface RecordingDialogListener {
        void onRecordingError();
    }
}
