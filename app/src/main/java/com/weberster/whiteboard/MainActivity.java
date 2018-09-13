package com.weberster.whiteboard;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.jaredrummler.android.colorpicker.ColorPickerDialog;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;

public class MainActivity extends Activity implements ColorPickerDialogListener {
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
        foregroundButton.setOnClickListener(new ColorPickerButtonClick());

        final Button backgroundButton = findViewById(R.id.background);
        backgroundButton.setOnClickListener(new ColorPickerButtonClick());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal:
                paintView.normal();
                return true;
            case R.id.emboss:
                paintView.emboss();
                return true;
            case R.id.blur:
                paintView.blur();
                return true;
            //case R.id.clear:
            //    paintView.clear();
            //    return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        //TODO: actually do something with selected color
    }

    @Override
    public void onDialogDismissed(int dialogId) {
        // placeholder due to required implementation
    }

    class ColorPickerButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ColorPickerDialog.newBuilder()
                    .show(MainActivity.this);
        }
    }
}
