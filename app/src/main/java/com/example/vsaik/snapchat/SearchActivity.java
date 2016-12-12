package com.example.vsaik.snapchat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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


public class SearchActivity extends AppCompatActivity {

    private List<Friend> friends;
    private String myName = UserDetails.getEmail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initButtons();
    }

    private void initButtons() {

        final EditText text = (EditText) findViewById(R.id.search_text);
        final RadioButton friend = (RadioButton) findViewById(R.id.search_friend);
        final RadioButton interest = (RadioButton) findViewById(R.id.search_interest);
        Button search = (Button) findViewById(R.id.search_init);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friends = new ArrayList<Friend>();
                if(friend.isChecked()){
                    new SearchFriends("friend",text.getText().toString()).execute();
                }
                else if(interest.isChecked()){
                    new SearchFriends("interest",text.getText().toString()).execute();
                }
                else{
                    new SearchFriends("email",text.getText().toString()).execute();
                }

            }
        });

    }

    private void populateFriends() {

       /* Friend myfriend = new Friend("Name","VIEW");
        Friend notmyfriend = new Friend("Name","ADD");
        friends.add(myfriend);
        friends.add(notmyfriend);*/
        ListView listView = (ListView) findViewById(R.id.list_friends);

        if(friends != null && friends.size() > 0) {
            CustomFriendViewAdapter adapter = new CustomFriendViewAdapter(myName,this, R.layout.friend_individual, friends,"add");
            listView.setAdapter(adapter);
        }else{
            listView.setAdapter(null);
        }

        //setOnItemClickListener(this);
    }

    class SearchFriends extends AsyncTask<Void,Void,Void>{

        private String method = "";
        private String search = "";

        private JSONArray friendsJSON = null;

        public SearchFriends(String method,String search){
            this.method = method;
            this.search = search;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            HashMap<String,String> hashMap = new HashMap<String,String>();
            hashMap.put("search",search);
            hashMap.put("Method","GET");
            try{
                if("friend".equalsIgnoreCase(method)) {
                    hashMap.put("URL",Constants.URL+"/search_user_username");
                    GetData post = new GetData(hashMap);
                    friendsJSON = new JSONArray(post.doInBackground());
                }
                else if("interest".equalsIgnoreCase(method)) {
                    hashMap.put("URL", Constants.URL + "/search_user_interests");
                    GetData post = new GetData(hashMap);
                    friendsJSON = new JSONArray(post.doInBackground());
                }
                else{
                    hashMap.put("URL", Constants.URL + "/search_user_email");
                    GetData post = new GetData(hashMap);
                    friendsJSON = new JSONArray(post.doInBackground());
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
            if(friendsJSON != null) {
                int size = friendsJSON.length();
                for (int i = 0; i < size; i++) {
                    Friend item = null;
                    try {
                        JSONObject object = friendsJSON.getJSONObject(i);

                        item = new Friend(object.getString("username"), "ADD");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    friends.add(item);
                }
            }
            populateFriends();
        }
    }
}
