package com.example.vsaik.snapchat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AllFriendsChatActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listAllFriends = null;
    private Context context;
    List<ChatItem> activeFriends = null;
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
        ChatItem item = new ChatItem(R.drawable.click, "Active Friend 1", R.drawable.greendot);
        ChatItem item2 = new ChatItem(R.drawable.click, "Active Friend 2", R.drawable.greendot);

        activeFriends.add(item);
        activeFriends.add(item2);
        activeFriends.add(item2);
        activeFriends.add(item2);

        CustomChatVewAdapter adapter = new CustomChatVewAdapter(this, R.layout.chat_list_item, activeFriends);
        listAllFriends.setAdapter(adapter);
        listAllFriends.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        String chatSession = activeFriends.get(position).getTitle();
        Intent i = new Intent(this,StartChatActivity.class);
        i.putExtra("friend",chatSession);
        startActivity(i);
    }

}
