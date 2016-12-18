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

        String url = hashMap.get("URL");
        String response = "";
        String method = hashMap.get("Method");
        hashMap.remove("Method");
        hashMap.remove("URL");
        HttpURLConnection conn = null;

        try {

            if("POST".equalsIgnoreCase(method)) {

                URL ur = new URL(url);
                conn = (HttpURLConnection) ur.openConnection();
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(getPostDataString(hashMap).toString());

               /* conn.setDoInput(true);
                conn.setDoOutput(true);*/

                writer.flush();
                writer.close();
                os.close();

            }
            else{
                Log.d("ERROR","use get data class instead of this, unsupported operations");
                url += "?"+getGetDataString(hashMap);
                Log.d("REQUEST",url);
                URL ur = new URL(url);
                conn = (HttpURLConnection) ur.openConnection();
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(true);

            }

            int responseCode=conn.getResponseCode();
            Log.d("RESP",response);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    Log.d("RESP",line);
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

    private String getGetDataString(HashMap<String, String> params) throws UnsupportedEncodingException {

        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }

}