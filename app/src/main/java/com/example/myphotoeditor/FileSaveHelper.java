package com.example.myphotoeditor;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.MutableLiveData;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Exception;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.jvm.Throws;

/**
 * Общий контракт этого класса заключается в том, чтобы
 * создать файл на устройстве.
 * <p>
 * Как его использовать -
 * Вызвать [FileSaveHelper.createFile]
 * если файл создан, вы получите путь к файлу и Uri.
 * и после того, как вы закончите с файлом, вызовите [FileSaveHelper.notifyThatFileIsNowPubliclyAvailable]
 * <p>
 * Помните! для выключения исполнителя вызовите [FileSaveHelper.addObserver] или
 * создайте объект с [FileSaveHelper]
 */

public class FileSaveHelper implements LifecycleObserver {
    private ContentResolver mContentResolver;
    private ExecutorService executor;
    private MutableLiveData<FileMeta> fileCreatedResult;
    private OnFileCreateResult resultListener;
    private Observer<FileMeta> observer;

    public FileSaveHelper(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
        executor = Executors.newSingleThreadExecutor();
        fileCreatedResult = new MutableLiveData<FileMeta>();
        observer = new Observer<FileMeta>() {
            @Override
            public void onChanged(FileMeta fileMeta) {
                if (resultListener != null) {
                    resultListener.onFileCreateResult(
                            fileMeta.isCreated,
                            fileMeta.filePath,
                            fileMeta.error,
                            fileMeta.uri
                    );
                }
            }
        };
    }

    public FileSaveHelper(AppCompatActivity activity) {
        this(activity.getContentResolver());
        addObserver(activity);
    }

    private void addObserver(LifecycleOwner lifecycleOwner) {
        fileCreatedResult.observe(lifecycleOwner, observer);
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void release() {
        if (executor != null) {
            executor.shutdownNow();
        }
    }

    /**
     * The effects of this method are
     * 1- insert new Image File data in MediaStore.Images column
     * 2- create File on Disk.
     *
     * @param fileNameToSave fileName
     * @param listener       result listener
     */
    public void createFile(String fileNameToSave, OnFileCreateResult listener) {
        resultListener = listener;
        if (executor != null) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    Cursor cursor = null;
                    try {
                        ContentValues newImageDetails = new ContentValues();
                        Uri imageCollection = buildUriCollection(newImageDetails);
                        Uri editedImageUri = getEditedImageUri(fileNameToSave, newImageDetails, imageCollection);

                        cursor = mContentResolver.query(
                                editedImageUri,
                                new String[]{MediaStore.Images.Media.DATA},
                                null,
                                null,
                                null
                        );
                        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        String filePath = cursor.getString(columnIndex);

                        updateResult(true, filePath, null, editedImageUri, newImageDetails);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        updateResult(false, null, ex.getMessage(), null, null);
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                    }
                }
            });
        }
    }



    @Throws(exceptionClasses = IOException.class)
    private Uri getEditedImageUri(String fileNameToSave, ContentValues newImageDetails, Uri imageCollection) throws IOException {
        newImageDetails.put(MediaStore.Images.Media.DISPLAY_NAME, fileNameToSave);
        Uri editedImageUri = mContentResolver.insert(imageCollection, newImageDetails);
        OutputStream outputStream = mContentResolver.openOutputStream(editedImageUri);
        if (outputStream != null) {
            outputStream.close();
        }
        return editedImageUri;
    }

    @SuppressLint("InlinedApi")
    private Uri buildUriCollection(ContentValues newImageDetails) {
        Uri imageCollection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
            newImageDetails.put(MediaStore.Images.Media.IS_PENDING, 1);
        } else {
            imageCollection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        return imageCollection;
    }

    @SuppressLint("InlinedApi")
    public void notifyThatFileIsNowPubliclyAvailable(ContentResolver contentResolver) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (executor != null) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        FileMeta value = fileCreatedResult.getValue();
                        if (value != null) {
                            value.imageDetails.clear();
                            value.imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0);
                            contentResolver.update(value.uri, value.imageDetails, null, null);
                        }
                    }
                });
            }
        }
    }

    private class FileMeta {
        public boolean isCreated;
        public String filePath;
        public Uri uri;
        public String error;
        public ContentValues imageDetails;
        public FileMeta(boolean isCreated, String filePath, Uri uri, String error, ContentValues imageDetails) {
            this.isCreated = isCreated;
            this.filePath = filePath;
            this.uri = uri;
            this.error = error;
            this.imageDetails = imageDetails;
        }
    }

    public interface OnFileCreateResult {
        /**
         * @param created whether file creation is success or failure
         * @param filePath filepath on disk. null in case of failure
         * @param error in case file creation is failed . it would represent the cause
         * @param uri Uri to the newly created file. null in case of failure
         */
        void onFileCreateResult(boolean created, String filePath, String error, Uri uri);
    }

    private void updateResult(boolean result, String filePath, String error, Uri uri, ContentValues newImageDetails) {
        fileCreatedResult.postValue(new FileMeta(result, filePath, uri, error, newImageDetails));
    }

    public static boolean isSdkHigherThan28() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

}
