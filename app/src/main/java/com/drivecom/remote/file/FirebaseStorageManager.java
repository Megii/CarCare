package com.drivecom.remote.file;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class FirebaseStorageManager implements StorageManager {
    private static final String FILENAME_FORMAT = "temp%s.%s";
    private static final String MPEG4_FILE_TYPE = "mp4";

    @Override
    public void uploadFile(File file2, final UploadCallback uploadCallback) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(file2);
        final String remoteFileName = createUniqueFileName();
        StorageReference riversRef = storageRef.child("voices/" + remoteFileName);
        UploadTask uploadTask = riversRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                uploadCallback.onUploadFailure(exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                uploadCallback.onUploadSuccess(remoteFileName, downloadUrl.toString());
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                Double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                uploadCallback.onUploadProgress(taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount(), progress);
            }
        });
    }

    @Override
    public void downloadToFile(final File file, final String url, final DownloadCallback downloadCallback) {
        StorageReference httpsReference = FirebaseStorage.getInstance().getReferenceFromUrl(url);
        httpsReference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                downloadCallback.onDownloadSuccess(file, url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                downloadCallback.onDownloadFailure(e);
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                downloadCallback.onDownloadProgress(taskSnapshot.getBytesTransferred(), taskSnapshot.getTotalByteCount(), progress);
            }
        });
    }

    private String createUniqueFileName() {
        Long timestamp = System.currentTimeMillis();
        return String.format(FILENAME_FORMAT, timestamp.toString(), MPEG4_FILE_TYPE);
    }
}
