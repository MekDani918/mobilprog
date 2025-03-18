package com.example.notes;

import android.graphics.Paint;
import android.graphics.Path;

public class CustomPath {
    public Path path;
    public Paint paint;

    public CustomPath(int color, float strokeWidth) {
        path = new Path();
        paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }
}
