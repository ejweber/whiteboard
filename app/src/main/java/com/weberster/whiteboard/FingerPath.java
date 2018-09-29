package com.weberster.whiteboard;

import android.graphics.Path;

public class FingerPath {

    public int color;
    public boolean dash;
    public boolean blur;
    public int strokeWidth;
    public Path path;

    public FingerPath(int color, boolean dash, boolean blur, int strokeWidth, Path path) {
        this.color = color;
        this.dash = dash;
        this.blur = blur;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }

}
