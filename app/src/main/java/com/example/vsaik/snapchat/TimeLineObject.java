package com.example.vsaik.snapchat;

import android.graphics.Bitmap;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jay on 12/5/16.
 */

public class TimeLineObject {

    public String id;
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
        Bitmap image;
        String caption;
    }

    class UserInfo{
        Bitmap display_pic;
        String name;

        public UserInfo(){
            likes = new ArrayList<String>();
            comments = new ArrayList<JSONObject>();
        }
        List<String> likes;
        List<JSONObject> comments;
        String date;
    }

}
