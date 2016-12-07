package com.example.vsaik.snapchat;

import android.graphics.Bitmap;

/**
 * Created by jay on 12/3/16.
 */

public class ChatItem {
    private Bitmap image;
    private String title;
    private int status;

    public ChatItem(Bitmap image, String title, int status) {
        this.image = image;
        this.title = title;
        this.status = status;
    }
    public Bitmap getImage() {
        return image;
    }
    public void setImage(Bitmap image) {
        this.image = image;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }
}

