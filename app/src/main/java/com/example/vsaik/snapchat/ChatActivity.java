package com.example.vsaik.snapchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener {


    List<String> list = new ArrayList<String>();
    ListView listActiveFriends;

    List<ChatItem> activeFriends;
    private String chatSession = "";
    private Context context = null;
    private String myName = "jay";
    String[] responseFetch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        listActiveFriends = (ListView) findViewById(R.id.listView);
        context = this;
        View contentView = (View) findViewById(R.id.activity_chat);
        onStart();
        contentView.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeRight() {
                Intent main = new Intent(ChatActivity.this, MainScreen.class);
                startActivity(main);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TAg", "On resume method");
        ImageView allFriends = (ImageView) findViewById(R.id.newChat);
        allFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AllFriendsChatActivity.class);
                startActivity(i);
            }
        });
        getActiveFriends();


    }

    private void showFriends() {

        if(activeFriends != null && activeFriends.size() > 0) {
            CustomChatVewAdapter adapter = new CustomChatVewAdapter(this, R.layout.chat_list_item, activeFriends);
            listActiveFriends.setAdapter(adapter);
            listActiveFriends.setOnItemClickListener(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TAG", "On start method");
        onResume();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        chatSession = activeFriends.get(position).getTitle();
        Intent i = new Intent(this, StartChatActivity.class);
        i.putExtra("friend", chatSession);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("PAUSE", "Pausing");
    }

    private void getActiveFriends() {

        RetrieveFriends retrieveFriends = new RetrieveFriends();
        retrieveFriends.execute();

    }

    class RetrieveFriends extends
            AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            activeFriends = new ArrayList<ChatItem>();
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("username", myName);
            hashMap.put("URL", Constants.URL + "/list_chats");
            hashMap.put("Method", "GET");
            GetData fecth = new GetData(hashMap);
            try {
                String junk = fecth.doInBackground();
                //String junk = "[\"vivek\",\"anvita\",\"anushka\"]";
                Log.d("RESPONSE",junk);

                junk = junk.substring(1,junk.length()-2);
                Log.d("RESPONSE",junk);
                junk = junk.replace("\"","");
                Log.d("RESPONSE",junk);

                responseFetch = junk.split(",");
                Log.e("RESPONSE", responseFetch.toString());

            } catch (Exception e) {
                Log.e("RESPONSE- ERROR", e.toString());
            }


        if(responseFetch != null ) {
            int size = responseFetch.length;
            for (int i = 0; i < size; i++) {
                ChatItem item = null;
                try {
                    String object = responseFetch[i];
                    Log.d("FRIEND OOBJ",object.toString());
                    String friendName = object.trim();

                    HashMap<String,String> hashMap1 = new HashMap<String, String>();
                    hashMap1.put("username", friendName);
                    hashMap1.put("URL", Constants.URL + "/check_user");
                    hashMap1.put("Method", "GET");
                    GetData activeData = new GetData(hashMap1);
                    JSONArray result = new JSONArray(activeData.doInBackground());
                    if(result != null && result.length() > 0) {
                        JSONObject obj = result.getJSONObject(0);
                        Log.d("ACTIVE",obj.toString());
                        boolean level = Boolean.parseBoolean(obj.getString("isActive"));
                        item = new ChatItem(null, friendName, ImageUtils.getStatus(level));
                    }
                    else {
                        item = new ChatItem(null, friendName, ImageUtils.getStatus(false));
                    }
                    activeFriends.add(item);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showFriends();
        }
    }
}