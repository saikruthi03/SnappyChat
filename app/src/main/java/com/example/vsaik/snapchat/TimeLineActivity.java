package com.example.vsaik.snapchat;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TimeLineActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private String myName = "";
    private ListView listTimeLine = null;
    private Context context= null;
    private List<String> friends = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);
        myName = UserDetails.getEmail();

        listTimeLine = (ListView) findViewById(R.id.list_time_line);
        context = this;
        View contentView = (View)findViewById(R.id.activity_time_line);

        TextView myTimeLine = (TextView) findViewById(R.id.myTimeLine);
        myTimeLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TimeLineActivity.this,IndividualTimeLineActivity.class);
                i.putExtra("profile",myName);
                startActivity(i);
            }
        });

        contentView.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeRight() {
                Intent main = new Intent(TimeLineActivity.this,MainScreen.class);
                startActivity(main);
            }
        });
        onStart();

        Spinner spinner = (Spinner) findViewById(R.id.planets_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setSelection(0);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDummyData();
    }

    private void initDummyData(){
    }

    private void populateFriends(){

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);

        if(friends != null && friends.size() > 0) {
            arrayAdapter.addAll(friends);
            listTimeLine.setAdapter(arrayAdapter);

        }
        else{
            listTimeLine.setAdapter(null);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("PAUSE","Pausing");
    }


    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if (parent.getItemAtPosition(pos).toString().equals("Search")) {
            Intent mainScreen = new Intent(TimeLineActivity.this, SearchActivity.class);
            startActivity(mainScreen);
        } else if (parent.getItemAtPosition(pos).toString().equals("Friends")) {
            Intent mainScreen = new Intent(TimeLineActivity.this, FriendActivity.class);
            startActivity(mainScreen);
        } else if (parent.getItemAtPosition(pos).toString().equals("ALL")) {
            Intent mainScreen = new Intent(TimeLineActivity.this, ListAllFriendsActivity.class);
            startActivity(mainScreen);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    class TimeLineFriends extends AsyncTask<Void,Void,Void> {


        private JSONArray friendsJSON = null;


        @Override
        protected Void doInBackground(Void... voids) {
            HashMap<String,String> hashMap = new HashMap<String,String>();
            hashMap.put("username",myName);
            hashMap.put("Method","GET");
            try{
                hashMap.put("URL",Constants.URL+"/get_timeline");

                GetData post = new GetData(hashMap);
                friendsJSON = new JSONArray(post.doInBackground());
                //friendsJSON = new JSONArray("[{username:kalanag,friend_username:jay}]");
            }
            catch (Exception e){
                Log.d("EXCEPTION","Exception in friendsJSON "+e.getCause());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            friends = new ArrayList<String>();
            if(friendsJSON != null) {

                int size = friendsJSON.length();
                for (int i = 0; i < size; i++) {
                    String item = null;
                    try {
                        JSONObject object = friendsJSON.getJSONObject(i);
                        String name = object.getString("username");
                        if(name != null && name.length() > 0 ){
                            if(!name.equalsIgnoreCase(myName)){
                                name =object.getString("username");
                                item = name;
                            }

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(item != null)
                        friends.add(item);
                }
            }
            populateFriends();
        }
    }
}
