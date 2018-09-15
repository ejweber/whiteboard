package com.weberster.whiteboard;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

public class MainActivity extends Activity implements ColorPickerDialogListener {
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

        final ToggleButton embossToggle = findViewById(R.id.emboss);
        embossToggle.setOnCheckedChangeListener(new EmbossToggleClick());

        final ToggleButton blurToggle = findViewById(R.id.blur);
        blurToggle.setOnCheckedChangeListener(new BlurToggleClick());

        final SeekBar widthBar = findViewById(R.id.brush_width);
        widthBar.setOnSeekBarChangeListener(new SeekBarChanged());
        widthBar.setMax(PaintView.MAX_WIDTH);
        widthBar.setProgress(PaintView.DEFAULT_WIDTH);

        final Button clearButton = findViewById(R.id.clear);
        clearButton.setOnClickListener(new ClearButtonClick(embossToggle, blurToggle, widthBar));
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

    class EmbossToggleClick implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            paintView.setEmboss(isChecked);
        }
    }

    class BlurToggleClick implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            paintView.setBlur(isChecked);
        }
    }

    class ClearButtonClick implements View.OnClickListener {
        private ToggleButton embossToggle;
        private ToggleButton blurToggle;
        private SeekBar widthBar;

        ClearButtonClick(ToggleButton embossToggle, ToggleButton blurToggle, SeekBar widthBar) {
            this.embossToggle = embossToggle;
            this.blurToggle = blurToggle;
            this.widthBar = widthBar;
        }

        @Override
        public void onClick(View v) {
            paintView.clear();
            embossToggle.setChecked(false);
            blurToggle.setChecked(false);
            widthBar.setProgress(PaintView.DEFAULT_WIDTH);
        }
    }

    class SeekBarChanged implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int width, boolean fromUser) {
            paintView.setWidth(width);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // nothing to do
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // nothing to do
        }
    }
}
