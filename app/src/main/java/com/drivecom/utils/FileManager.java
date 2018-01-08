package com.drivecom.utils;


import android.content.Context;
import android.util.Log;

import java.io.File;

public class FileManager {

    private static final String UPLOAD_FILE = "upload_file";
    private static final String DOWNLOAD_FILE = "download_file";

    private Context context;

    public FileManager(Context context) {
        this.context = context;
    }

    private File createContainer(String path, String dirPath) {
        String filePath = path + (!dirPath.equals("") ? "/" + dirPath : "");

        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
            Log.d("FileSaver", "Directory created");
        } else
            Log.d("FileSaver", "Directory exists");

        return file;
    }

    public File getFileForDownload() {
        createContainer(context.getFilesDir().getAbsolutePath(), DOWNLOAD_FILE);
        File file = new File(getFilePath(false));
        return file.getAbsoluteFile();
    }

    public File getFileForUpload() {
        return new File(getFilePath(true));
    }

    public String saveFileForUpload() {
        File file = createContainer(context.getFilesDir().getAbsolutePath(), UPLOAD_FILE);
        String path = file.getAbsolutePath() + ".mp4";
        return path;
    }

    public String getFilePath(boolean isUpload) {
        if (isUpload) {
            return context.getFilesDir().getAbsolutePath() + "/" + UPLOAD_FILE + ".mp4";

        } else {
            return context.getFilesDir().getAbsolutePath() + "/" + DOWNLOAD_FILE + ".mp4";
        }
    }

    public void removeFile(boolean isUpload) {
        String path = getFilePath(isUpload);
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }
}
