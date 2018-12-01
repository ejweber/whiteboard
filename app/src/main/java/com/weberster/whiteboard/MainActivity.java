package com.weberster.whiteboard;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;

import com.weberster.whiteboard.MainMenuFragment.OnMainMenuInteractionListener;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

public class MainActivity extends FragmentActivity implements ColorPickerDialogListener,
        OnMainMenuInteractionListener, PaintView.OnPaintViewAction,
        PlaybackFragment.OnPlaybackInteractionListener {
    private PaintView paintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paintView = findViewById(R.id.paint_view);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);

        PlaybackFragment playbackFragment = new PlaybackFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, playbackFragment).commit();

        paintView.openFingerPathFile();
    }

    @Override
    protected void onStart() {
        super.onStart();
        paintView.playBack(0.01);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        switch(dialogId) {
            case R.integer.foreground_picker_id:
                paintView.setForeground(color);
                break;
            case R.integer.background_picker_id:
                paintView.setBackground(color);
                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        // placeholder due to required implementation
    }

    @Override
    public void onColorPickerButtonClick(int dialogId, boolean showAlphaBool) {
        int color;
        if (dialogId == R.integer.foreground_picker_id) {
            color = paintView.getForegroundColor();
        } else {
            color = paintView.getBackgroundColor();
        }
        ColorPickerDialog.newBuilder()
                    .setDialogId(dialogId)
                    .setColor(color)
                    .setShowAlphaSlider(showAlphaBool)
                    .show(MainActivity.this);
    }

    @Override
    public void onDashButtonClick(boolean isChecked) {
        paintView.setDash(isChecked);
    }

    @Override
    public void onBlurButtonClick(boolean isChecked) {
        paintView.setBlur(isChecked);
    }

    @Override
    public void onWidthSet(int width) {
        paintView.setWidth(width);
    }

    @Override
    public void onClearButtonClick() {
        paintView.clear();
    }

    @Override
    public void onPlaybackComplete() {
        MainMenuFragment mainMenuFragment = new MainMenuFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mainMenuFragment).commit();
        paintView.allowTouch();
    }

    @Override
    public void onPauseButtonClick(boolean isChecked, double speed) {
        if (isChecked) {
            paintView.cancelPlayback();
        }
        else {
            paintView.playBack(speed);
        }
    }

    @Override
    public void onSkipButtonClick() {
        paintView.cancelPlayback();
        paintView.redrawAll();
        onPlaybackComplete();
    }

    @Override
    public void onSpeedSet(double speed) {
        paintView.cancelPlayback();
        paintView.playBack(speed);
    }

    @Override
    public void onSpeedBarTouch() {
        paintView.cancelPlayback();
    }
}
