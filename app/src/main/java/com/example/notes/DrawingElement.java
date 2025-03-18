package com.example.notes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.Nullable;

public class DrawingElement extends View {
    private ArrayList<CustomPath> paths = new ArrayList<>();
    private CustomPath currentPath;
    private int currentColor = Color.BLACK;
    private float currentStrokeWidth = 8f;

    public DrawingElement(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        //canvas.drawLine(50,50,200,200, paint);

        for (CustomPath customPath : paths) {
            canvas.drawPath(customPath.path, customPath.paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new CustomPath(currentColor, currentStrokeWidth);
                currentPath.path.moveTo(x, y);
                paths.add(currentPath);
                break;
            case MotionEvent.ACTION_MOVE:
                currentPath.path.lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        invalidate();
        return true;
    }

    public Bitmap saveAsBitmap(){
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return bitmap;
    }
    public void saveToFile(File file) throws IOException {
        Bitmap bitmap = saveAsBitmap();
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream); // Save as PNG
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public void setColor(int color){
        currentColor = color;
    }
    public void setStrokeWidth(int strokeWidth){
        currentStrokeWidth = strokeWidth;
    }
}

