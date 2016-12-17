package com.example.vsaik.snapchat;

/**
 * Created by jay on 12/6/16.
 */
public class Friend {
    int image;
    String name;
    String level;
    int status;
    String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
Friend(){

}
    Friend(int image , String name, String level,int status) {
        this.image = image;
        this.name = name;
        this.level = level;
        this.status = status;
    }


    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}