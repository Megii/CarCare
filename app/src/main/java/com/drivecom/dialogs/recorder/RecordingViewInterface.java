package com.drivecom.dialogs.recorder;

import com.drivecom.utils.InternalUserModel;

import java.util.List;

public interface RecordingViewInterface {
    void onRecordingStarted();

    void onUpdateTime(String time);

    void onRecordingError();

    void onUploadStarted();

    void onUploadSuccess();

    void onUploadProgress(int percent);

    void onUploadFailure();

    List<InternalUserModel> getUserList();
}
