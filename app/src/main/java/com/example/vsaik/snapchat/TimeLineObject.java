package com.example.vsaik.snapchat;

import java.util.List;

/**
 * Created by jay on 12/5/16.
 */

public class TimeLineObject {

    public ObjectStatusInfo statusInfo;

    public ObjectImageInfo imageInfo;

    public UserInfo userInfo;

    public  TimeLineObject(){
        statusInfo = new ObjectStatusInfo();
        imageInfo = new ObjectImageInfo();
        userInfo = new UserInfo();
    }

    public boolean hasStatus(){
        return statusInfo.status != null && statusInfo.status.length() > 0;
    }

    class ObjectStatusInfo{
        String status;

    }

    class ObjectImageInfo{
        int image;
        String caption;
    }

    class UserInfo{
        int display_pic;
        String name;
        int likes;
        List<String> comments;
        String date;
    }

}
