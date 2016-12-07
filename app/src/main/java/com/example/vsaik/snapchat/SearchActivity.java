package com.example.vsaik.snapchat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity {

    private List<Friend> friends;

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

        final RadioButton friend = (RadioButton) findViewById(R.id.search_friend);
        final RadioButton interest = (RadioButton) findViewById(R.id.search_interest);
        Button search = (Button) findViewById(R.id.search_init);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"friend : "+friend.isChecked()+" interest : " +
                        interest.isChecked(),Toast.LENGTH_SHORT).show();
                populateFriends();
            }
        });

    }

    private void populateFriends() {
        friends = new ArrayList<Friend>();
        Friend myfriend = new Friend("Name","VIEW");
        Friend notmyfriend = new Friend("Name","ADD");
        friends.add(myfriend);
        friends.add(notmyfriend);
        CustomFriendViewAdapter adapter = new CustomFriendViewAdapter(this, R.layout.friend_individual, friends);
        ListView listView = (ListView) findViewById(R.id.list_friends);
        listView.setAdapter(adapter);
        //setOnItemClickListener(this);
    }

}
