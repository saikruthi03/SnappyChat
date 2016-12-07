package com.example.vsaik.snapchat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Created by jay on 12/4/16.
 */

public class ImageUtils {

    public static Bitmap compress(Bitmap original){
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        original.compress(Bitmap.CompressFormat.PNG, 30, out);
        Bitmap compressed = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
        original = null;
        out = null;
        return compressed;
    }

    public static Bitmap getBitmapFromBase64(String base64Encoded){
        byte[] decodedString = Base64.decode(base64Encoded, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        if(bitmap == null) {
            Log.d("Base64", "Might be using a json response as string");
            return getBitmapFromJSON(base64Encoded);
        }
        return bitmap;
    }

    public static Bitmap getBitmapFromJSON(String urlSafeBitmap) {
        byte[] decodedString = Base64.decode(urlSafeBitmap, Base64.URL_SAFE);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return bitmap;
    }

    public static int getStatus(String online) {
        if("ONLINE".equalsIgnoreCase(online))
            return R.drawable.greendot;
        return 0;
    }

    public static String getStringImage(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String temp = Base64.encodeToString(b, Base64.DEFAULT);
            return temp;
        }
        return "";
    }

}
