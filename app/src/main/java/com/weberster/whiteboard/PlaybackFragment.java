package com.weberster.whiteboard;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

public class PlaybackFragment extends Fragment {
    View view;
    OnPlaybackInteractionListener listener;
    SeekBar speedBar;
    ToggleButton pauseToggle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_playback_control, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        pauseToggle = view.findViewById(R.id.pause);
        pauseToggle.setOnCheckedChangeListener(new PauseToggleClick());

        final Button skipButton = view.findViewById(R.id.skip);
        skipButton.setOnClickListener(new SkipButtonClick());

        speedBar = view.findViewById(R.id.speed_bar);
        speedBar.setOnSeekBarChangeListener(new PlaybackFragment.SeekBarChanged());
        speedBar.setProgress(50);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPlaybackInteractionListener) {
            listener = (OnPlaybackInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnMainMenuInteractionListener");
        }
    }

    class PauseToggleClick implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int speed = speedBar.getProgress();
            double doubleSpeed = speedIntToDouble(speed);
            listener.onPauseButtonClick(isChecked, doubleSpeed);
        }
    }

    class SkipButtonClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            listener.onSkipButtonClick();
        }
    }

    class SeekBarChanged implements SeekBar.OnSeekBarChangeListener {
        int intSpeed;

        SeekBarChanged() {
            intSpeed = 50;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int intSpeed, boolean fromUser) {
               this.intSpeed = intSpeed;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            listener.onSpeedBarTouch();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (!pauseToggle.isChecked())
                listener.onSpeedSet(speedIntToDouble(intSpeed));
        }
    }

    private static double speedIntToDouble(int intSpeed) {
        double percent = (double)intSpeed/100.0;
        Log.d("percent", Double.toString(percent));
        double doublespeed = percent * (PaintView.MAX_SPEED - PaintView.MIN_SPEED) + PaintView.MIN_SPEED;
        Log.d("calculated", Double.toString(doublespeed));
        return doublespeed;
    }

    public interface OnPlaybackInteractionListener {
        void onPauseButtonClick(boolean isChecked, double speed);
        void onSkipButtonClick();
        void onSpeedSet(double speed);
        void onSpeedBarTouch();
    }
}
