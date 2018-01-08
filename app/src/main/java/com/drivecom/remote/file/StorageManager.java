package com.drivecom.remote.file;

import java.io.File;

public interface StorageManager {
    void uploadFile(File file, UploadCallback uploadCallback);

    void downloadToFile(File file, String url, DownloadCallback downloadCallback);

    interface UploadCallback {
        void onUploadSuccess(String filename, String resourceUrl);
        void onUploadProgress(long bytesTransferred, long totalBytes, Double percent);
        void onUploadFailure(Exception ex);
    }

    interface DownloadCallback {
        void onDownloadSuccess(File resourceFile, String resourceUrl);
        void onDownloadProgress(long bytesTransferred, long totalBytes, Double percent);
        void onDownloadFailure(Exception ex);
    }
}
