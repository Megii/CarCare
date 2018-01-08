package com.drivecom.dialogs.player;


import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.drivecom.remote.file.FirebaseStorageManager;
import com.drivecom.remote.file.StorageManager;
import com.drivecom.utils.FileManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PlayerPresenter implements PlayerPresenterInterface,MediaPlayer.OnCompletionListener{

    private PlayerViewInterface playerView;
    private MediaPlayer player = null;
    private FileManager fileManager;
    private long duration = 0;
    private Handler timerHandler;
    private Runnable updateRunnable;

    public PlayerPresenter(PlayerViewInterface playerView, FileManager fileManager){
        this.playerView = playerView;
        this.fileManager = fileManager;
    }

    private void updateTimeCounter() {
        long currentDuration = player.getCurrentPosition();
        //long  countDown = duration-currentDuration;
        String hms = toTimeFormat(currentDuration);

        int progress = (int)((double)currentDuration/duration *100.0);
        playerView.onPlayingProgress(progress,hms);
        timerHandler.postDelayed(updateRunnable, 1000);
    }

    private String toTimeFormat(long timeInMillis) {

        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(timeInMillis) % TimeUnit.HOURS.toMinutes(1));
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(timeInMillis) % TimeUnit.MINUTES.toSeconds(1));
        return String.format(Locale.ENGLISH, "%01d:%02d", minutes, seconds);
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

    public void invokeRecordingDownload(String url){
        playerView.onDownloadStarted();
        deleteDownload();
        FirebaseStorageManager firebaseStorageManager = new FirebaseStorageManager();
        firebaseStorageManager.downloadToFile(fileManager.getFileForDownload(), url, new StorageManager.DownloadCallback() {
            @Override
            public void onDownloadSuccess(File resourceFile, String resourceUrl) {
                play();
            }

            @Override
            public void onDownloadProgress(long bytesTransferred, long totalBytes, Double percent) {
                playerView.onDownloadProgress(percent.intValue());
            }

            @Override
            public void onDownloadFailure(Exception ex) {
                playerView.onDownloadFailure();
            }
        });
    }

    @Override
    public void play() {
        if (player == null) {
            player = new MediaPlayer();
            FileInputStream fs = null;
            try {
                fs = new FileInputStream(fileManager.getFilePath(false));
                player.setDataSource(fs.getFD());
                player.setOnCompletionListener(this);
                player.prepare();
                player.start();
            } catch (Exception e) {
                playerView.onPlayingError();
                deleteDownload();
                e.printStackTrace();
            } finally {
                if (fs != null) {
                    try {
                        fs.close();
                    } catch (IOException ex) {
                        playerView.onPlayingError();
                        deleteDownload();
                        ex.printStackTrace();
                    }
                }
            }
        } else {
            player.start();
        }

        if(duration==0){
            duration = player.getDuration();
        }
        playerView.onPlayingStarted( toTimeFormat(duration));
        turnOnUpdater();
    }

    @Override
    public void stop() {
        resetData();
    }

    @Override
    public void resetData(){
        turnOffTimer();
        if (player!=null) {
            player.stop();
            player.release();
            player = null;
        }
        deleteDownload();
    }

    private void deleteDownload() {
        fileManager.removeFile(false);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        turnOffTimer();
        playerView.onPlayingEnd();
    }
}
