package com.weberster.whiteboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.os.CountDownTimer;
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
import java.util.Timer;
import java.util.TimerTask;

public class PaintView extends View {

    public static int DEFAULT_WIDTH = 20;
    public static int MAX_WIDTH = 200;
    public static final int DEFAULT_COLOR = Color.RED;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
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
    private DashPathEffect mDash;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private FileOutputStream fileStream;
    private ObjectOutputStream objectStream;

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
        mDash = new DashPathEffect(new float[]{100, 100}, 0);
        paths = new ArrayList<FingerPath>(); // TODO: HAVE OPENFINGERPATHFILE HANDLE THIS
    }

    public void openFingerPathFile() {
        try {
            FileInputStream inFileStream = getContext().openFileInput("finger_paths.java");
            ObjectInputStream inObjectStream = new ObjectInputStream(inFileStream);
            Log.d("Unhandled", "Opened successsfully");
            paths = (ArrayList<FingerPath>)inObjectStream.readObject();
            inFileStream.close();
            inObjectStream.close();
        } catch (Exception e) {
            Log.e("Unhandled", "Error while opening finger_paths.java for reading", e);
        }
    }

    private void writePathsToFile() {
        StringBuilder debugString = new StringBuilder();
        for (FingerPath path : paths) {
            debugString.append(path.toString());
            debugString.append("\n");
        }
            Log.d("SavedPaths", debugString.toString());
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

    public void setDash(boolean isSet) { dash = isSet; }

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
        foregroundColor = DEFAULT_COLOR;
        backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        redrawAll();
        blur = false;
        dash = false;
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
            mPaint.setPathEffect(mDash);
        if (fp.blur)
            mPaint.setMaskFilter(mBlur);
        mCanvas.drawPath(fp.getPath(), mPaint);
    }

    private void redrawAll() {
        mCanvas.drawColor(backgroundColor);
        for (FingerPath fp : paths) {
            processFingerPath(fp);
        }
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
        if (paths.isEmpty()) // if no paths were loaded from file
            return;
        Timer timer = new Timer();
        TimerTask timertask = new TimerTask() { // inline anonymous class
            private int pathIndex;
            {
                pathIndex = 0;
                mPath = paths.get(pathIndex);
                mPath.recreateFromBeginning();
            }

            @Override
            public void run() {
                boolean isDone = mPath.recreateMore();
                invalidate();
                if (isDone) {
                    pathIndex += 1;
                    if (pathIndex >= paths.size()) {
                        this.cancel();
                    }
                    else
                        mPath = paths.get(pathIndex);
                        mPath.recreateFromBeginning();
                }
            }
        };
        speed *= 1000; // convert speed to ms
        timer.schedule(timertask, 0, (long)speed);
    }
}
