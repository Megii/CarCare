package com.drivecom.dialogs.player;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.drivecom.R;
import com.drivecom.utils.FileManager;

public class PlayerDialogInitializer implements PlayerViewInterface {

    private Context context;
    private PlayerPresenterInterface presenter;
    private android.support.v7.app.AlertDialog dialog;
    private TextView progressText;
    private TextView lengthText;
    private ProgressBar progressBar;
    private TextView infoTextView;
    private String userName;
    private String url;

    public PlayerDialogInitializer(Context context, String userName, String url) {
        this.context = context;
        this.url = url;
        this.userName = userName;
        presenter = new PlayerPresenter(this, new FileManager(context));
    }

    public void showPlayerDialog() {
        View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_player, null);
        lengthText = dialogView.findViewById(R.id.dialog_player_length_text);
        progressText = dialogView.findViewById(R.id.dialog_player_progress_text);
        infoTextView = dialogView.findViewById(R.id.dialog_player_info_text);
        progressBar = dialogView.findViewById(R.id.dialog_player_progress_bar);
        android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(context, R.style.DialogStyle);
        alertBuilder
                .setView(dialogView)
                .setCancelable(false)
                .setTitle(context.getString(R.string.dialog_message_title))
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        presenter.stop();
                        dialog.dismiss();
                    }
                }).setPositiveButton(context.getString(R.string.dialog_try_again), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                presenter.invokeRecordingDownload(url);
            }
        });
        dialog = alertBuilder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
            }
        });
        dialog.show();
        presenter.invokeRecordingDownload(url);
    }

    public AlertDialog getDialog() {
        return dialog;
    }

    @Override
    public void onDownloadStarted() {
        infoTextView.setGravity(Gravity.LEFT);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.GONE);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.GONE);
        lengthText.setVisibility(View.GONE);
        progressText.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        String message = context.getResources().getString(R.string.dialog_player_progress_message) + " (0)";
        infoTextView.setText(message);
    }

    @Override
    public void onDownloadFailure() {
        progressBar.setVisibility(View.GONE);
        infoTextView.setText(context.getString(R.string.download_error));
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
    }

    @Override
    public void onDownloadProgress(int percent) {
        progressBar.setProgress(percent);
        String message = context.getResources().getString(R.string.dialog_player_progress_message) + " (" + percent + "%)";
        infoTextView.setText(message);
    }

    @Override
    public void onPlayingProgress(int progress, String time) {
        progressBar.setProgress(progress);
        progressText.setText(time);
    }

    @Override
    public void onPlayingStarted(String length) {
        progressBar.setProgress(0);
        infoTextView.setText(userName + ":");
        infoTextView.setGravity(Gravity.CENTER);
        progressText.setVisibility(View.VISIBLE);
        progressText.setText("0:00");
        lengthText.setText(length);
        lengthText.setVisibility(View.VISIBLE);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
    }

    @Override
    public void onPlayingError() {
        lengthText.setVisibility(View.GONE);
        progressText.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        infoTextView.setText(context.getString(R.string.player_error));
        infoTextView.setVisibility(View.VISIBLE);
        infoTextView.setGravity(Gravity.LEFT);
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 1000);
    }

    @Override
    public void onPlayingEnd() {
        dialog.dismiss();
    }

    public void onStop() {
        presenter.resetData();
    }
}
