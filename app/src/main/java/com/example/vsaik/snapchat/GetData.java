package com.example.vsaik.snapchat;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jay on 12/7/16.
 */

public class GetData {
    private HashMap<String,String> hashMap;

    public GetData(HashMap<String,String> hashMap){
        this.hashMap = hashMap;
    }


    protected String doInBackground() {
        String response = "";

        try {
            String url = hashMap.get("URL");
            String method = hashMap.get("Method");
            hashMap.remove("Method");
            hashMap.remove("URL");
            url += "?" + getGetDataString(hashMap);
            Log.d("GET",url);
            URL ur = new URL(url);
            URLConnection conn = (URLConnection) ur.openConnection();
            conn.getInputStream();
            String line;
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = br.readLine()) != null) {
                response += line;
            }
            System.out.println(response);
        }
        catch(Exception e){
            Log.d("ERROR","Error in get request"+e.getCause());
        }
        return response;
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
