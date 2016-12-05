package com.example.vsaik.snapchat;

/**
 * Created by jay on 12/3/16.
 */

public class ChatItem {
    private int imageId;
    private String title;
    private int status;

    public ChatItem(int imageId, String title, int status) {
        this.imageId = imageId;
        this.title = title;
        this.status = status;
    }
    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
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

