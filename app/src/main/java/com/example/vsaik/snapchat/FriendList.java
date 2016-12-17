package com.example.vsaik.snapchat;

/**
 * Created by jay on 12/16/16.
 */
public class FriendList {
    int privacy;
    int status;
    boolean isFriend;
    String name;
    String email;

    public FriendList()
    {

    }

    public String getName(){
        return name;
    }

    public FriendList(int privacy, int status, boolean isFriend , String name, String email) {
        this.email = email;
        this.isFriend = isFriend;
        this.name = name;
        this.privacy = privacy;
        this.status = status;
    }


}
