package com.example.vsaik.snapchat;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendActivity extends AppCompatActivity {

    String myName = "jay";
    List<Friend> friends;
    String method = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        onStart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initButtons();


    }
    private void initButtons() {


        final RadioButton friendVanilla = (RadioButton) findViewById(R.id.friendVanilla);
        final RadioButton friendRequests = (RadioButton) findViewById(R.id.friendRequests);
        final RadioButton friendWaiting = (RadioButton) findViewById(R.id.friendWaiting);

        friendVanilla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method = "friendVanilla";
                new SearchFriends("friend").execute();
            }
        });
        friendRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method = "friendRequests";
                new SearchFriends("requests").execute();
            }
        });
        friendWaiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                method = "friendWaiting";
                new SearchFriends("waiting").execute();
            }
        });

    }

    private void populateFriends() {

       /* Friend myfriend = new Friend("Name","VIEW");
        Friend notmyfriend = new Friend("Name","ADD");
        friends.add(myfriend);
        friends.add(notmyfriend);*/
        if(method.length() > 0){
            CustomFriendViewAdapter adapter = new CustomFriendViewAdapter(myName,this, R.layout.friend_individual, friends,method);
            ListView listView = (ListView) findViewById(R.id.friendsView);
            listView.setAdapter(adapter);
        }
        else {
            CustomFriendViewAdapter adapter = new CustomFriendViewAdapter(this, R.layout.friend_individual, friends);
            ListView listView = (ListView) findViewById(R.id.friendsView);
            listView.setAdapter(adapter);
        }
        //setOnItemClickListener(this);
    }

    class SearchFriends extends AsyncTask<Void,Void,Void> {

        private String method = "";

        private JSONArray friendsJSON = null;

        public SearchFriends(String method){
            this.method = method;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            HashMap<String,String> hashMap = new HashMap<String,String>();
            hashMap.put("username",myName);
            hashMap.put("Method","GET");
            try{
                if("friend".equalsIgnoreCase(method)) {
                    hashMap.put("URL",Constants.URL+"/get_added_friends");
                    PostData post = new PostData(hashMap);
                    friendsJSON = new JSONArray(post.doInBackground());
                }
                else if("request".equalsIgnoreCase(method)) {
                    hashMap.put("URL", Constants.URL + "/get_unadded_friends");
                    PostData post = new PostData(hashMap);
                    friendsJSON = new JSONArray(post.doInBackground());
                }
                else{//waiting requests api --- Tooo DOO
                    /*hashMap.put("URL", Constants.URL + "/search_user_email");
                    PostData post = new PostData(hashMap);
                    friendsJSON = new JSONArray(post.doInBackground());*/
                }
            }
            catch (Exception e){
                Log.d("EXCEPTION","Exception in friendsJSON "+e.getCause());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            int size = friendsJSON.length();
            for(int i = 0 ; i < size ; i++) {
                Friend item = null;
                try {

                    JSONObject object = friendsJSON.getJSONObject(i);
                    item = new Friend(object.getString("data"), "ADD");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                friends.add(item);
            }
            populateFriends();
        }
    }
}

