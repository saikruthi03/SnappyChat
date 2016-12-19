package com.example.vsaik.snapchat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllFriendsChatActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listAllFriends = null;
    private Context context;
    List<ChatItem> activeFriends = null;
    String myName = UserDetails.getEmail();
    JSONArray responseFetch = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_friends_chat);
        listAllFriends = (ListView) findViewById(R.id.listViewAll);
        context = this;
        onStart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TAG","On start method");
        onResume();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("TAg","On resume method");
        activeFriends = new ArrayList<ChatItem>();

        RetrieveFriends retrieveFriends = new RetrieveFriends();
        retrieveFriends.execute();


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        String chatSession = activeFriends.get(position).getTitle();
        String name = activeFriends.get(position).name;
        Intent i = new Intent(this, StartChatActivity.class);
        i.putExtra("friend", chatSession);
        i.putExtra("fullname",name);

        startActivity(i);
    }

    private void showFriends(){

        if(activeFriends!= null && activeFriends.size() > 0) {
            CustomChatVewAdapter adapter = new CustomChatVewAdapter(this, R.layout.chat_list_item, activeFriends);
            listAllFriends.setAdapter(adapter);
            listAllFriends.setOnItemClickListener(this);
        }
        else{
            listAllFriends.setAdapter(null);
        }
    }


    class RetrieveFriends extends
            AsyncTask<Void,Void,Void>{



        @Override
        protected Void doInBackground(Void... voids) {
            activeFriends = new ArrayList<ChatItem>();

            HashMap<String,String> hashMap = new HashMap<String, String>();
            hashMap.put("username",myName);
            hashMap.put("URL",Constants.URL+"/get_added_friends");
            hashMap.put("Method","GET");
            GetData fecth = new GetData(hashMap);
            try {
                responseFetch = new JSONArray(fecth.doInBackground());
                Log.e("RESPONSE",responseFetch.toString());

            }
            catch(Exception e){
                Log.e("RESPONSE- ERROR",e.toString());
            }

            if(responseFetch!= null) {

                int size = responseFetch.length();
                for(int i = 0 ; i < size ; i++) {
                    ChatItem item = null;
                    try {
                        JSONObject object = responseFetch.getJSONObject(i);
                        String friendName = "";
                        if (myName.equalsIgnoreCase(object.getString("friend_username"))) {
                            friendName = object.getString("username");
                        } else {
                            friendName = object.getString("friend_username");
                        }
                        HashMap<String,String> hashMap1 = new HashMap<String, String>();
                        hashMap1.put("username", friendName);
                        hashMap1.put("URL", Constants.URL + "/check_user");
                        hashMap1.put("Method", "GET");
                        GetData activeData = new GetData(hashMap1);
                        JSONArray result = new JSONArray(activeData.doInBackground());
                        if(result != null && result.length() > 0) {
                            JSONObject obj = result.getJSONObject(0);
                            Log.d("ACTIVE", obj.toString());
                            boolean level = Boolean.parseBoolean(obj.getString("isActive"));
                            item = new ChatItem(null, friendName, ImageUtils.getStatus(level));
                            item.name = obj.getString("fullname");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    activeFriends.add(item);
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
