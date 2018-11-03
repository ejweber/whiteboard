package com.weberster.whiteboard;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;
import android.widget.Button;

public class MainMenuFragment extends Fragment {
    private OnMainMenuInteractionListener listener;
    private View view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_menu, container, false);
        return view;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final ToggleButton dashToggle = view.findViewById(R.id.dash);
        dashToggle.setOnCheckedChangeListener(new DashToggleClick());

        final ToggleButton blurToggle = view.findViewById(R.id.blur);
        blurToggle.setOnCheckedChangeListener(new BlurToggleClick());

        final SeekBar widthBar = view.findViewById(R.id.brush_width);
        widthBar.setOnSeekBarChangeListener(new SeekBarChanged());
        widthBar.setMax(PaintView.MAX_WIDTH);
        widthBar.setProgress(PaintView.DEFAULT_WIDTH);

        final Button clearButton = view.findViewById(R.id.clear);
        clearButton.setOnClickListener(new ClearButtonClick(dashToggle, blurToggle, widthBar));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMainMenuInteractionListener) {
            listener = (OnMainMenuInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMainMenuInteractionListener");
        }
    }

    class DashToggleClick implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            listener.onDashButtonClick(isChecked);
        }
    }

    class BlurToggleClick implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            listener.onBlurButtonClick(isChecked);
        }
    }

    class SeekBarChanged implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int width, boolean fromUser) {
            //listener.onWidthSet(width);
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

    class ClearButtonClick implements View.OnClickListener {
        private ToggleButton dashtoggle;
        private ToggleButton blurToggle;
        private SeekBar widthBar;

        ClearButtonClick(ToggleButton dashToggle, ToggleButton blurToggle, SeekBar widthBar) {
            this.dashtoggle = dashToggle;
            this.blurToggle = blurToggle;
            this.widthBar = widthBar;
        }

        @Override
        public void onClick(View v) {
            listener.onClearButtonClick();
            dashtoggle.setChecked(false);
            blurToggle.setChecked(false);
            widthBar.setProgress(PaintView.DEFAULT_WIDTH);
        }
    }

    public interface OnMainMenuInteractionListener {
        void onDashButtonClick(boolean isChecked);
        void onBlurButtonClick(boolean isChecked);
        void onClearButtonClick();
        void onWidthSet(int width);
    }
}