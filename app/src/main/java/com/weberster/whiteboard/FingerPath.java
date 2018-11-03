package com.weberster.whiteboard;

import android.graphics.Path;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class FingerPath implements Serializable {
    public int color;
    public boolean dash;
    public boolean blur;
    public int strokeWidth;
    private ArrayList<float[]> pathPoints;
    private transient Path path; // transient means it will not be serialized
    private transient int index; // remember where in the path recreation we are

    public FingerPath(int color, boolean dash, boolean blur, int strokeWidth) {
        this.color = color;
        this.dash = dash;
        this.blur = blur;
        this.strokeWidth = strokeWidth;
        pathPoints = new ArrayList<>();
        this.path = new Path();
    }

    public void moveTo(float x, float y) {
        path.moveTo(x, y);
        pathPoints.add(new float[] {x, y});
    }

    public void quadTo(float x1, float y1, float x2, float y2) {
        path.quadTo(x1, y1, x2, y2);
        pathPoints.add(new float[] {x1, y1, x2, y2});
    }

    public void reset() {
        path.reset();
    }

    public void lineTo(float x, float y) {
        path.lineTo(x, y);
    }

    public Path getPath() {
        return path;
    }

    public void recreateWholePath() {
        path = new Path();
        float [] initPoints = pathPoints.remove(0);
        path.moveTo(initPoints[0], initPoints[1]);
        for (float[] pointSet : pathPoints) {
            path.quadTo(pointSet[0], pointSet[1], pointSet[2], pointSet[3]);
        }
        pathPoints.add(0, initPoints); // add initPoints back in to preserve pathPoints
    }

    public boolean recreateMore() {
        float[] pointSet = pathPoints.get(index);
        path.quadTo(pointSet[0], pointSet[1], pointSet[2], pointSet[3]);
        index += 1; // move 1 step along path
        return (index >= pathPoints.size());
    }

    public void recreateFromBeginning() {
        path = new Path();
        float [] initPoints = pathPoints.get(0);
        index = 1; // next time we should move the path to the point at position 1
        path.moveTo(initPoints[0], initPoints[1]);
    }

    public String toString() {
        String string = Integer.toString(this.color);
        string += " " + Boolean.toString(this.dash);
        string += " " + Boolean.toString(this.blur);
        string += " " + Integer.toString(this.strokeWidth);
        string += " " + Integer.toString(this.pathPoints.size());
        for (float[] pointSet : pathPoints) {
            string += " " + Arrays.toString(pointSet);
        }
        return string;
    }
}