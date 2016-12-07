package com.example.vsaik.snapchat;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vsaik on 12/6/2016.
 */
public class DownloadAllMessages extends AsyncTask<String, Void, String> {

    Context appContext;
    OnMessagesReceived onMessagesReceived;
    List<ChatItem> messages = new ArrayList<>();
    List<ChatItem> orderedMessages = new ArrayList<>();
    public DownloadAllMessages(Context appContext) {
        this.appContext = appContext;
        onMessagesReceived = (OnMessagesReceived)appContext;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected void onPostExecute(String result) {
        //super.onPostExecute(messages);
        super.onPostExecute(result);
        if (messages.size() > 0) {
           // ChatActivity.activeFriends = messages;
            onMessagesReceived.OnMessagesReceived(messages);
        }
        else
            Toast.makeText(appContext, "Server Busy!!Please try again later", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected String doInBackground(String... params) {
        //try {
          //  URL url=new URL(params[0]);
          //  String query = url.getQuery();
          //  Map<String, String> map = getQueryMap(query);
          //  HttpURLConnection con= (HttpURLConnection) url.openConnection();
          //  con.setRequestMethod("GET");
           // con.connect();
          //  int statuscode=con.getResponseCode();
          //  if(statuscode==HttpURLConnection.HTTP_OK)
          //  {
           //     BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
             //   StringBuilder sb=new StringBuilder();
           //     String line=br.readLine();
            //    while(line!=null)
              //  {
                //    sb.append(line);
                  //  line=br.readLine();
                //}
                //String json=sb.toString();
                //Log.d("JSON",json);
                //JSONObject root=new JSONObject(json);
               // JSONArray array_rows=root.getJSONArray("rows");
               // Log.d("JSON","array_rows:"+array_rows);
                //JSONObject object_rows=array_rows.getJSONObject(0);
                //Log.d("JSON","object_rows:"+object_rows);
               // JSONArray array_elements=object_rows.getJSONArray("elements");
               // Log.d("JSON","array_elements:"+array_elements);
                //JSONObject  object_elements=array_elements.getJSONObject(0);
               // Log.d("JSON","object_elements:"+object_elements);
                int numberOfMessages = 10;//first row sends the number of messages and then all messages should be sent
               Log.d("adding messages","messages");
                for(int i=1;i<=5;i++){
                    ChatItem chatItem = new ChatItem(R.drawable.click,"Friend"+i,R.drawable.greendot,true," ");
                    messages.add(chatItem);
                }
                for(int i=6;i<=10;i++){
                    ChatItem chatItem = new ChatItem(R.drawable.click,"Friend"+i,R.drawable.greendot,false,"2");
                    messages.add(chatItem);
                }
                  Log.d("Messages",messages.size()+"");
                //JSONObject object_duration=object_elements.getJSONObject("duration");
               // JSONObject object_distance=object_elements.getJSONObject("distance");

               // Log.d("JSON","object_duration:"+object_duration);
               // String first = object_duration.getString("value")+","+object_distance.getString("value")+","+map.get("mode");

                return null;

           // }
        //} catch (MalformedURLException e) {
          //  Log.d("error", "error1");
        //} catch (IOException e) {
          //  Log.d("error", "error2");
        //} catch (JSONException e) {
          //  Log.d("error","error3");
        //}catch(Exception e){

//        }


       // return null;
    }

    public static Map<String, String> getQueryMap(String query)
    {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params)
        {
            String key = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(key, value);
        }
        return map;
    }

}


interface OnMessagesReceived{
    void OnMessagesReceived(List<ChatItem> activeFriends);
}
