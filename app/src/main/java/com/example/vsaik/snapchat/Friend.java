package com.example.vsaik.snapchat;

/**
 * Created by jay on 12/6/16.
 */
public class Friend {
    String image;
    String name;
    String level;
    String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
Friend(){

}
    Friend(String name, String level) {
        this.image = image;
        this.name = name;
        this.level = level;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
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