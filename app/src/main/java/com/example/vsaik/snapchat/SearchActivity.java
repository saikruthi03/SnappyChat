package com.example.vsaik.snapchat;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
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
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        initButtons();
    }

    private void initButtons() {

        final EditText text = (EditText) findViewById(R.id.search_text);
        final TextView friend = (TextView) findViewById(R.id.search_friend);
        final TextView interest = (TextView) findViewById(R.id.search_interest);
        friends = new ArrayList<Friend>();
        friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String search = text.getText().toString();

                    friend.setBackgroundColor(getResources().getColor(R.color.fillhighlight));
                    interest.setBackgroundColor(getResources().getColor(R.color.fill));
                    if(search != null && search.length() > 0)
                        new SearchFriends("friend",search).execute();
                }
        });
        interest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = text.getText().toString();

                interest.setBackgroundColor(getResources().getColor(R.color.fillhighlight));
                friend.setBackgroundColor(getResources().getColor(R.color.fill));
                if(search != null && search.length() > 0)
                    new SearchFriends("interest",search).execute();

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
            CustomFriendViewAdapter adapter = new CustomFriendViewAdapter(myName,this, R.layout.friend_individual, friends,"search");
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
            friends = new ArrayList<Friend>();
            if(friendsJSON != null) {
                int size = friendsJSON.length();
                for (int i = 0; i < size; i++) {
                    Friend item = null;
                    String friendName;
                    try {
                        JSONObject object = friendsJSON.getJSONObject(i);
                        friendName = object.getString("username");
                        HashMap<String,String> hashMap1 = new HashMap<String, String>();
                        hashMap1.put("username", friendName);
                        hashMap1.put("URL", Constants.URL + "/check_user");
                        hashMap1.put("Method", "GET");
                        GetData activeData = new GetData(hashMap1);
                        JSONArray result = new JSONArray(activeData.doInBackground());
                        JSONObject obj ;
                        if(result != null && result.length() > 0) {
                            obj = result.getJSONObject(0);
                            Log.d("ACTIVE", obj.toString());
                            String fullname = obj.getString("fullname");
                            boolean level = Boolean.parseBoolean(obj.getString("isActive"));
                            item = new Friend(0,friendName, "ADD",ImageUtils.getStatus(level));
                            item.fullname = obj.getString("fullname");
                            friends.add(item);
                        }
                       /* else{
                            item = new Friend(0,friendName, "ADD",ImageUtils.getStatus(false));
                            item.fullname = friendName;
                            friends.add(item);
                        }
*/


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            populateFriends();
        }
    }
}
