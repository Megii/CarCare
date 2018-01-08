package com.drivecom.dialogs.recorder;

import android.media.MediaRecorder;
import android.os.Handler;

import com.drivecom.models.UserModel;
import com.drivecom.remote.file.FirebaseStorageManager;
import com.drivecom.remote.file.StorageManager;
import com.drivecom.remote.message.FirebaseMessageManager;
import com.drivecom.remote.message.MessageManager;
import com.drivecom.utils.FileManager;
import com.drivecom.utils.InternalUserModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RecordingPresenter implements RecordingPresenterInterface, MediaRecorder.OnInfoListener {

    private static final int MAX_DURATION = 20000;

    private MediaRecorder recorder = null;
    private RecordingViewInterface recordingView;
    private FileManager fileManager;
    private long startTime;
    private long duration;
    private Handler timerHandler;
    private Runnable updateRunnable;
    private boolean recordingStarted = false;

    public RecordingPresenter(RecordingViewInterface recordingView, FileManager fileManager) {
        this.recordingView = recordingView;
        this.fileManager = fileManager;
        deleteRecording();
    }

    private void updateTimeCounter() {
        duration = (System.currentTimeMillis() - startTime);

        String hms = toTimeFormat(duration);
        recordingView.onUpdateTime(hms);
        timerHandler.postDelayed(updateRunnable, 1000);
    }

    private String toTimeFormat(long timeInMillis) {

        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % TimeUnit.HOURS.toMinutes(1));
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % TimeUnit.MINUTES.toSeconds(1));
        return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds);
    }

    private void turnOnUpdater() {
        timerHandler = new Handler();
        updateRunnable = new Runnable() {
            public void run() {
                updateTimeCounter();
            }
        };
        timerHandler.post(updateRunnable);
    }

    private void turnOffTimer() {
        if (timerHandler != null)
            timerHandler.removeCallbacks(updateRunnable);
    }

    @Override
    public void startRecording() {
        startTime = System.currentTimeMillis();
        recorder = new MediaRecorder();
        try {
            recorder.reset();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            recorder = null;
            recordingView.onRecordingError();
            return;
        }
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setOutputFile(fileManager.saveFileForUpload());
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setAudioEncodingBitRate(192000);
        recorder.setAudioSamplingRate(44100);
        recorder.setMaxDuration(MAX_DURATION);
        recorder.setOnInfoListener(this);
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException | IllegalStateException e) {
            try {
                recorder.stop();
            } catch (RuntimeException ex) {
                e.printStackTrace();
            }
            resetRecorder();
            deleteRecording();
            recordingView.onRecordingError();
            e.printStackTrace();
            return;
        }

        turnOnUpdater();
        recordingStarted = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recordingView.onRecordingStarted();
            }
        }, 1000);
    }

    private boolean stopRecording() {
        turnOffTimer();
        try {
            if (recorder != null) {
                recorder.stop();
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (recorder != null) {
                resetRecorder();
            }
        }
        recordingStarted = false;
        return true;
    }

    @Override
    public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
            timerHandler.removeCallbacks(updateRunnable);
            invokeRecordingUpload();
        }
    }

    @Override
    public void invokeRecordingUpload() {
        boolean isStoppedSuccess = stopRecording();
        if (isStoppedSuccess) {
            recordingView.onUploadStarted();
            FirebaseStorageManager firebaseStorageManager = new FirebaseStorageManager();
            firebaseStorageManager.uploadFile(fileManager.getFileForUpload(), new StorageManager.UploadCallback() {
                @Override
                public void onUploadSuccess(String filename, String resourceUrl) {
                    invokeSendingNotification(filename, resourceUrl);
                }

                @Override
                public void onUploadProgress(long bytesTransferred, long totalBytes, Double percent) {
                    recordingView.onUploadProgress(percent.intValue());
                }

                @Override
                public void onUploadFailure(Exception ex) {
                    recordingView.onUploadFailure();
                    deleteRecording();
                }
            });
        } else {
            recordingView.onUploadFailure();
            deleteRecording();
        }
    }

    private void invokeSendingNotification(String filename, String resourceUrl) {
        FirebaseMessageManager firebaseMessageManager = new FirebaseMessageManager();
        List<UserModel> externalUsers = new ArrayList<>();
        for (InternalUserModel model : recordingView.getUserList()) {
            externalUsers.add(new UserModel(model.getId(), null, null, model.getName(), null, model.getRegistrationId(), model.getToken(), model.getGroupId()));
        }

        firebaseMessageManager.sendVoiceMessage(filename, resourceUrl, externalUsers, new MessageManager.SendMessageCallback() {
            @Override
            public void onMessageSendingSuccess(String messageId) {
                recordingView.onUploadSuccess();
                deleteRecording();
            }

            @Override
            public void onMessageSendingFailure(Exception ex) {
                recordingView.onUploadFailure();
            }
        });
    }

    private void deleteRecording() {
        fileManager.removeFile(true);
    }

    private void resetRecorder() {
        if (recordingStarted) {
            recordingStarted = false;
            try {
                recorder.stop();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        recorder.reset();
        recorder.release();
        recorder = null;
    }

    public void resetData() {
        turnOffTimer();
        if (recorder != null) {
            resetRecorder();
        }
        deleteRecording();
    }
}
