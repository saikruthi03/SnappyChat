package com.example.vsaik.snapchat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class ListAllFriendsActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener {


    List<String> list = new ArrayList<String>();
    ListView listAllUsers;

    List<Friend> allUsers;
    private String timeLine = "";
    private Context context = null;
    private String myName = "jay";
    JSONArray responseFetch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_all_friends);
        listAllUsers = (ListView) findViewById(R.id.listViewUsers);
        context = this;
        View contentView = (View) findViewById(R.id.activity_all_users);
        onStart();
        contentView.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeRight() {
               // Intent main = new Intent(ChatActivity.this, MainScreen.class);
              //  startActivity(main);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getAllUsers();
        }

    private void showUsers() {

        if(allUsers != null && allUsers.size() > 0) {
            CustomAllUsersAdapter adapter = new CustomAllUsersAdapter(UserDetails.getNickname(),this, R.layout.all_users_list, allUsers);
            listAllUsers.setAdapter(adapter);
            listAllUsers.setOnItemClickListener(this);
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

        timeLine = allUsers.get(position).getName();
        Intent i = new Intent(this, TimeLineObject.class);
        i.putExtra("friend", timeLine);
        startActivity(i);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("PAUSE", "Pausing");
    }

    private void getAllUsers() {
        allUsers = new ArrayList<Friend>();
        RetrieveUsers retrieveusers = new RetrieveUsers();
        retrieveusers.execute();

    }

    class RetrieveUsers extends
            AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("URL", Constants.URL + "/list_other_users");
            hashMap.put("Method", "GET");
            allUsers = new ArrayList<>();
            GetData fecth = new GetData(hashMap);
            try {
                responseFetch = new JSONArray(fecth.doInBackground());
                Log.e("RESPONSE", responseFetch.toString());

            } catch (Exception e) {
                Log.e("RESPONSE- ERROR", e.toString());
            }


            if(responseFetch != null ) {
                int size = responseFetch.length();
                for (int i = 0; i < size; i++) {
                    Friend item = new Friend();
                    try {
                        JSONObject object = responseFetch.getJSONObject(i);
                        Log.d("FRIEND OOBJ",object.toString());
                        String friendName = "";
                        item.setName(object.getString("fullname"));
                        Log.d("Helooooo",item.getName());
                        item.setImage(object.getString("thumbnail_profile_pic"));
                        item.setUserName(object.getString("username"));
                        item.setLevel("Add");
                        allUsers.add(item);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            hashMap = new HashMap<String, String>();
            hashMap.put("URL", Constants.URL + "/get_added_friends");
            hashMap.put("username",myName);
            hashMap.put("Method", "GET");
            GetData fecthFrnds = new GetData(hashMap);
            try {
                responseFetch = new JSONArray(fecthFrnds.doInBackground());
                Log.e("RESPONSE", responseFetch.toString());

            } catch (Exception e) {
                Log.e("RESPONSE- ERROR", e.toString());
            }
            Log.d("Response",responseFetch.length()+"");
            if(responseFetch != null ) {
                int size = responseFetch.length();
                for (int i = 0; i < size; i++) {
                    Friend item = new Friend();
                    try {
                        JSONObject object = responseFetch.getJSONObject(i);
                        Log.d("FRIEND OOBJ",object.toString());
                        String friendName = "";
                        item.setName(object.getString("friend_username"));
                       // item.setImage(object.getString("thumbnail_profile_pic"));
                        item.setUserName(object.getString("username"));
                        item.setLevel("Friend");
                        allUsers.add(item);
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
            showUsers();
        }
    }

}
