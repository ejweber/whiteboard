package com.weberster.whiteboard;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import com.weberster.whiteboard.MainMenuFragment.OnMainMenuInteractionListener;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

public class MainActivity extends FragmentActivity implements ColorPickerDialogListener,
        OnMainMenuInteractionListener {
    private static final int FOREGROUND_PICKER_ID = 0;
    private static final int BACKGROUND_PICKER_ID = 1;
    private PaintView paintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paintView = findViewById(R.id.paint_view);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);

        final Button foregroundButton = findViewById(R.id.foreground);
        foregroundButton.setOnClickListener(new ColorPickerButtonClick(FOREGROUND_PICKER_ID));

        final Button backgroundButton = findViewById(R.id.background);
        backgroundButton.setOnClickListener(new ColorPickerButtonClick(BACKGROUND_PICKER_ID));

        paintView.openFingerPathFile();
        paintView.playBack(0.01);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        switch(dialogId) {
            case FOREGROUND_PICKER_ID:
                paintView.setForeground(color);
                break;
            case BACKGROUND_PICKER_ID:
                paintView.setBackground(color);
                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        // placeholder due to required implementation
    }

    class ColorPickerButtonClick implements View.OnClickListener {
        private int dialogId;
        private boolean showAlphaBool;
        private int color;

        ColorPickerButtonClick(int dialogId) {
            this.dialogId = dialogId;
            showAlphaBool = (dialogId == MainActivity.FOREGROUND_PICKER_ID);
        }

        @Override
        public void onClick(View v) {
            if (dialogId == MainActivity.FOREGROUND_PICKER_ID) {
                color = paintView.getForegroundColor();
            }
            else {
                color = paintView.getBackgroundColor();
            }
            ColorPickerDialog.newBuilder()
                    .setDialogId(dialogId)
                    .setColor(color)
                    .setShowAlphaSlider(showAlphaBool)
                    .show(MainActivity.this);
        }
    }

    @Override
    public void onDashButtonClick(boolean isChecked) {
        paintView.setDash(isChecked);
    }

    @Override
    public void onBlurButtonClick(boolean isChecked) {
        //paintView.setBlur(isChecked);
    }

    @Override
    public void onWidthSet(int width) {
        //paintView.setWidth(width);
    }

    @Override
    public void onClearButtonClick() {
        paintView.clear();
    }


}
