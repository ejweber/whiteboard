package com.weberster.whiteboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class PaintView extends View {

    public static int DEFAULT_WIDTH = 20;
    public static int MAX_WIDTH = 200;
    public static final int DEFAULT_COLOR = Color.RED;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    public static final double MIN_SPEED = 0.05; // higher numbers are slower
    public static final double MAX_SPEED = 0.001; // zero is illegal
    private static final float TOUCH_TOLERANCE = 4;
    public static final float DEFAULT_DASH_SPACE = 100;
    private float mX, mY;
    private FingerPath mPath;
    private Paint mPaint;
    private ArrayList<FingerPath> paths;
    private int foregroundColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private boolean blur;
    private boolean dash;
    private MaskFilter mBlur;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private OnPaintViewAction listener;
    private int playbackLocation;
    private Timer playbackTimer;
    private boolean canTouch;
    private float pausedX, pausedY;

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);
        mBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);
        paths = new ArrayList<>(); // TODO: HAVE OPENFINGERPATHFILE HANDLE THIS
        listener = (OnPaintViewAction) context; // TODO: check for errors
        playbackLocation = 0;
        canTouch = false;
        pausedX = pausedY = -1;
    }

    public void openFingerPathFile() {
        try {
            FileInputStream inFileStream = getContext().openFileInput("finger_paths.java");
            ObjectInputStream inObjectStream = new ObjectInputStream(inFileStream);
            paths = (ArrayList<FingerPath>)inObjectStream.readObject();
            inFileStream.close();
            inObjectStream.close();
        } catch (Exception e) {
            Log.e("Unhandled", "Error while opening finger_paths.java for reading", e);
        }
    }

    private void writePathsToFile() {
        StringBuilder debugString = new StringBuilder();
        FileOutputStream fileStream;
        ObjectOutputStream objectStream;
        for (FingerPath path : paths) {
            debugString.append(path.toString());
            debugString.append("\n");
        }
        try {
            fileStream = getContext().openFileOutput("finger_paths.java",
                    Context.MODE_PRIVATE);
            objectStream = new ObjectOutputStream(fileStream);
            objectStream.writeObject(paths);
            objectStream.close();
            fileStream.close();
        }
        catch (Exception e) {
            Log.e("Unhandled", "Error while writing array list", e);
        }
    }

    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(backgroundColor); // start with default background color

        foregroundColor = DEFAULT_COLOR;
        strokeWidth = DEFAULT_WIDTH;
    }

    public void setWidth(int newWidth) {
        strokeWidth = newWidth;
    }

    public void setBackground(int newColor) {
        backgroundColor = newColor;
        redrawAll();
        invalidate();
    }

    public void setForeground(int newColor) {
        foregroundColor = newColor;
    }

    public void setDash(boolean isSet) {
        dash = isSet;
    }

    public void setBlur(boolean isSet) {
        blur = isSet;
    }

    public int getForegroundColor() {
        return foregroundColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void clear() {
        // foregroundColor = DEFAULT_COLOR;
        // backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        mPath = null;
        redrawAll();
        // blur = false;
        // dash = false;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        if (mPath != null) {processFingerPath(mPath);}
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private void processFingerPath(FingerPath fp) {
        if (fp.getPath() == null) // processFingerPath may be called when path doesn't exist
            return;
        mPaint.setColor(fp.color);
        mPaint.setStrokeWidth(fp.strokeWidth);
        mPaint.setMaskFilter(null);
        mPaint.setPathEffect(null);
        if (fp.dash)
            mPaint.setPathEffect(calculateDash(fp.strokeWidth));
        if (fp.blur)
            mPaint.setMaskFilter(mBlur);
        mCanvas.drawPath(fp.getPath(), mPaint);
    }

    public void redrawAll() {
        mCanvas.drawColor(backgroundColor);
        for (FingerPath fp : paths) {
            processFingerPath(fp);
        }
        invalidate();
    }

    private void touchStart(float x, float y) {
        mPath = new FingerPath(foregroundColor, dash, blur, strokeWidth);
        paths.add(mPath);
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            float[] points = new float[] {mX, mY, (x + mX) /2, (y + mY) / 2};
            mPath.quadTo(points[0], points[1], points[2], points[3]);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!canTouch) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    pausedX = event.getX(); // remember where finger is when paused
                    pausedY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    pausedX = -1;
                    pausedY = -1;
            }
            return true; // don't allow PaintView interactions during playback
        }

        float x = event.getX();
        float y = event.getY();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                writePathsToFile(); // TODO: think about where this should actually go
                break;
        }
        return true;
    }

    public void playBack(double speed) {
        if (paths.isEmpty()) { // if no paths were loaded from file
            listener.onPlaybackComplete();
            allowTouch();
            return;
        }
        playbackTimer = new Timer();
        TimerTask timertask = new TimerTask() { // inline anonymous class
            {
                Log.d("Playback Location", Integer.toString(playbackLocation));
                mPath = paths.get(playbackLocation);
                mPath.recreateFromBeginning();
            }

            @Override
            public void run() {
                boolean isDone = mPath.recreateMore();
                postInvalidate();  // invalidate can't be called from non-ui thread
                if (isDone) {
                    playbackLocation += 1;
                    Log.d("Playback Location", Integer.toString(playbackLocation));
                    if (playbackLocation >= paths.size()) {
                        this.cancel();
                        listener.onPlaybackComplete();
                        if (pausedX >= 0) {
                            touchStart(pausedX, pausedY);
                            postInvalidate();
                        }
                        allowTouch();
                    }
                    else {
                        mPath = paths.get(playbackLocation);
                        mPath.recreateFromBeginning();}
                }
            }
        };
        speed *= 1000; // convert speed to ms
        playbackTimer.schedule(timertask, 0, (long)speed);
    }

    public void cancelPlayback() {
        playbackTimer.cancel();
        paths.get(playbackLocation).reset();
    }

    public void allowTouch() {
        canTouch = true;
    }

    public interface OnPaintViewAction {
        void onPlaybackComplete();
    }

    public DashPathEffect calculateDash(int strokeWidth) {
        float[] pathArray = new float[2];
        pathArray[0] = 100;
        pathArray[1] = DEFAULT_DASH_SPACE + (strokeWidth - DEFAULT_WIDTH) / (float)1.5;
        return new DashPathEffect(pathArray, 0);
    }
}
