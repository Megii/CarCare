package com.drivecom.dialogs.player;


public interface PlayerPresenterInterface {

    void play();

    void stop();

    void resetData();

    void invokeRecordingDownload(String url);
}
