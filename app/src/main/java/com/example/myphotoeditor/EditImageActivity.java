package com.example.myphotoeditor;

import android.util.Log;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import ja.burhanrashid52.photoeditor.OnPhotoEditorListener;
import ja.burhanrashid52.photoeditor.ViewType;

import com.example.myphotoeditor.base.BaseActivity;
import com.example.myphotoeditor.tools.EditingToolsAdapter.OnItemSelected;
import com.example.myphotoeditor.tools.ToolType;

public class EditImageActivity extends BaseActivity
        implements OnPhotoEditorListener, OnItemSelected {

    @Override
    public void onToolSelected(ToolType toolType) {
    }

    @Override
    public void onAddViewListener(ViewType viewType, int numberOfAddedViews) {
        Log.d(TAG, "onAddViewListener() called with: viewType = [" + viewType + "], numberOfAddedViews = [" + numberOfAddedViews + "]");
    }

    @Override
    public void onEditTextChangeListener(@Nullable View view, @Nullable String s, int i) {

    }

    @Override
    public void onRemoveViewListener(@Nullable ViewType viewType, int i) {

    }

    @Override
    public void onStartViewChangeListener(@Nullable ViewType viewType) {

    }

    @Override
    public void onStopViewChangeListener(@Nullable ViewType viewType) {

    }

    @Override
    public void onTouchSourceImage(@Nullable MotionEvent motionEvent) {

    }


    private static final String TAG = "EditImageActivity";

    public static final String FILE_PROVIDER_AUTHORITY = "com.example.myphotoeditor.fileprovider";
    private static final int CAMERA_REQUEST = 52;
    private static final int PICK_REQUEST = 53;
    public static final String ACTION_NEXTGEN_EDIT = "action_nextgen_edit";
    public static final String PINCH_TEXT_SCALABLE_INTENT_KEY = "PINCH_TEXT_SCALABLE";
}
