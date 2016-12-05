package com.example.vsaik.snapchat;

/**
 * Created by vsaik on 12/4/2016.
 */
public class ChatMessage {
    private int displayImage;
    private String chatText;
    private int chatImage;
    private String user;

    public ChatMessage(int displayImage, int chatImage,String chatText,String user){
        this.displayImage = displayImage;
        this.chatImage = chatImage;
        this.chatText = chatText;
        this.user = user;
    }

    public int getDisplayImage() {
        return displayImage;
    }

    public void setDisplayImage(int displayImage) {
        this.displayImage = displayImage;
    }

    public String getChatText() {
        return chatText;
    }

    public void setChatText(String chatText) {
        this.chatText = chatText;
    }

    public int getChatImage() {
        return chatImage;
    }

    public void setChatImage(int chatImage) {
        this.chatImage = chatImage;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
