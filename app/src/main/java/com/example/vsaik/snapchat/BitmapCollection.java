package com.example.vsaik.snapchat;

import android.graphics.Bitmap;

/**
 * Created by jay on 12/5/16.
 */

public class BitmapCollection {
    private Bitmap bitmap;
    private int x;
    private int y;



    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
