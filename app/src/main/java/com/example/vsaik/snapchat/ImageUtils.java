package com.example.vsaik.snapchat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by jay on 12/4/16.
 */

public class ImageUtils {

    static int i = 0;
    public static Bitmap compress(Bitmap original){
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            original.compress(Bitmap.CompressFormat.PNG, 30, out);
            Bitmap compressed = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            original = null;
            out = null;
            return compressed;
        }
        catch(Exception e){
            Log.d("MEMORY","Out of Memory errors");
        }
        return null;
    }

    public static Bitmap getBitmapFromBase64(String base64Encoded){
       try{
           byte[] decodedString = Base64.decode(base64Encoded, Base64.DEFAULT);
           Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
           if(bitmap == null) {
               Log.d("Base64", "Might be using a json response as string");

            return getBitmapFromJSON(base64Encoded);
        }
        return bitmap;
    }
    catch(Exception e){
        Log.d("MEMORY","Out of Memory errors");
    }
    return null;
    }
    public static Bitmap getBitmapFromBase64WithRotation(String base64Encoded,int angle){
        try{
            byte[] decodedString = Base64.decode(base64Encoded, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    width, height, matrix, true);
            return bitmap;
        }
        catch(Exception e){
            Log.d("MEMORY","Out of Memory errors");
        }
        return null;
    }


    public static Bitmap getBitmapFromJSON(String urlSafeBitmap) {
    try{    byte[] decodedString = Base64.decode(urlSafeBitmap, Base64.URL_SAFE);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return bitmap;
    }
    catch(Exception e){
        Log.d("MEMORY","Out of Memory errors");
    }
    return null;
    }

    public static int getStatus(boolean online) {
        if(online)
            return R.drawable.online;
        else
            return R.drawable.offline;
    }

    public static int getPrivacyImage(String privacy) {
        if("public".equalsIgnoreCase(privacy))
            return R.drawable.com_facebook_close;
        else if("private".equalsIgnoreCase(privacy))
            return R.drawable.offline;
        return R.drawable.photos;
    }

    public static String getStringImage(Bitmap bitmap) {
     try{
         if (bitmap != null) {
             ByteArrayOutputStream out = new ByteArrayOutputStream();
             bitmap.compress(Bitmap.CompressFormat.PNG, 30, out);
             bitmap = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            final int lnth=bitmap.getByteCount();
            ByteBuffer dst= ByteBuffer.allocate(lnth);
            bitmap.copyPixelsToBuffer( dst);
            byte[] barray=dst.array();
            String temp = Base64.encodeToString(barray, Base64.DEFAULT);

             return temp;

        }
        return "";
    }
    catch(Exception e){
        Log.d("MEMORY","Out of Memory errors");
    }
    return "";
    }

    public static String getStringImageWithoutCompression(Bitmap bitmap) {
    try{    final int lnth=bitmap.getByteCount();
        ByteBuffer dst= ByteBuffer.allocate(lnth);
        bitmap.copyPixelsToBuffer( dst);
        byte[] barray=dst.array();
        String temp = Base64.encodeToString(barray, Base64.DEFAULT);
        return temp;
    }
    catch(Exception e){
        Log.d("MEMORY","Out of Memory errors");
    }
    return "";
    }

    public static int getRandomImage() {
        if( i > 0) {
            i--;
            return R.drawable.male;
        }
        i++;
        return R.drawable.female;
    }
}
