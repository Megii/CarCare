package com.drivecom.dialogs.player;


public interface PlayerViewInterface {

    void onPlayingProgress(int percent, String time);

    void onPlayingStarted(String length);

    void onPlayingEnd();

    void onPlayingError();

    void onDownloadStarted();

    void onDownloadFailure();

    void onDownloadProgress(int percent);
}
