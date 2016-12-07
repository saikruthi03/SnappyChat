package com.example.vsaik.snapchat;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by jay on 12/5/16.
 */

public class PostData {

    private HashMap<String,String> hashMap;

    public PostData(HashMap<String,String> hashMap){
        this.hashMap = hashMap;
    }


    protected String doInBackground() {

        URL url;
        String response = "";
        try {
            url = new URL(hashMap.get("URL"));
            hashMap.remove("URL");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");

            if("POST".equalsIgnoreCase(hashMap.get("Method"))) {
                conn.setRequestMethod("POST");

            }
            else{
                conn.setRequestMethod("GET");
            }
            hashMap.remove("Method");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(hashMap).toString());

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            Log.d("RESP",response);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("RESP",response);

        return response;

    }

    private JSONObject getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        JSONObject result = new JSONObject(params);
        boolean first = true;
        Log.d("REQUEST",result.toString());
        return result;
    }

}
