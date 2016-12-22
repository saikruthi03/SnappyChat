package com.example.vsaik.snapchat;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by vsaik on 12/6/2016.
 */
public class GetMessagesService extends Service {

    public int uniqueNumber = 0;
    public HashMap<String,String> liveData = new HashMap<>();
    final static String GROUP_KEY_EMAILS = "snappy_chat";
    JSONArray responseFetch = null;
    List<String> type = new ArrayList<>();
    List<String> data = new ArrayList<>();
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                           new DoBackgroundTask().execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000); //execute in every 50000 ms

            return START_STICKY;

    }
    private class DoBackgroundTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            String response = "";
           data= new ArrayList<>();
            type= new ArrayList<>();
            liveData = new HashMap<>();
            liveData.put("Method", "GET");
            liveData.put("URL", Constants.URL + "/get_users_live");
            liveData.put("username", UserDetails.getUserName());
            GetData fetch3 = new GetData(liveData);
            try {
                response = fetch3.doInBackground();
                Log.e("RESPONSE- ERROR-get", response);
            } catch (Exception e) {
                Log.e("RESPONSE- ERROR-get", e.toString());
            }
            int size = response.length();
            try{  if(size>0){
                responseFetch = new JSONArray(response);
                size = responseFetch.length();
                for (int i = 0; i < size; i++) {
                    try {
                        JSONObject object = responseFetch.getJSONObject(i);
                        data.add(object.getString("data"));
                        type.add(object.getString("type"));
                    }catch(Exception ex){

                    }

                }

            }}catch(Exception ex){

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
if(data.size() > 0 && type.size() > 0){
    for(int i=0;i<data.size();i++){
        notification(data.get(i),type.get(i));
    }
}
        }

    }

    public void notification(String data,String type){
        uniqueNumber++;
        Toast.makeText(getApplicationContext(),"inside app context", Toast.LENGTH_LONG).show();
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.notify)
                        .setContentTitle(type.toUpperCase())
                        .setContentText(data)
                        .setGroup(GROUP_KEY_EMAILS);

        Intent resultIntent = new Intent(getApplicationContext(), ChatActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(ChatActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(uniqueNumber, mBuilder.build());
    }

}




