package com.example.vsaik.snapchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener {


        List<String> list = new ArrayList<String>();
    ListView listActiveFriends;

    List<ChatItem> activeFriends;
        private String chatSession = "";
        private Context context= null;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat);
            listActiveFriends = (ListView) findViewById(R.id.listView);
            context = this;
            View contentView = (View)findViewById(R.id.activity_chat);
            onStart();
            contentView.setOnTouchListener(new OnSwipeTouchListener(context) {
                @Override
                public void onSwipeRight() {
                    Intent main = new Intent(ChatActivity.this,MainScreen.class);
                    startActivity(main);
                }
            });

        }

        @Override
        protected void onResume() {
            super.onResume();
            Log.d("TAg","On resume method");
            ImageView allFriends = (ImageView) findViewById(R.id.newChat);
            allFriends.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(),AllFriendsChatActivity.class);
                    startActivity(i);
                }
            });
            activeFriends = new ArrayList<ChatItem>();
            ChatItem item = new ChatItem(R.drawable.click, "Friend 1", R.drawable.greendot);
            ChatItem item2 = new ChatItem(R.drawable.click, "Friend 2 Friend 2", R.drawable.greendot);

            activeFriends.add(item);
            activeFriends.add(item2);
            activeFriends.add(item2);
            activeFriends.add(item2);

            CustomChatVewAdapter adapter = new CustomChatVewAdapter(this, R.layout.chat_list_item, activeFriends);
            listActiveFriends.setAdapter(adapter);
            listActiveFriends.setOnItemClickListener(this);
        }


        @Override
        protected void onStart() {
            super.onStart();
            Log.d("TAG","On start method");
            onResume();

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            chatSession = activeFriends.get(position).getTitle();
            Intent i = new Intent(this,StartChatActivity.class);
            i.putExtra("friend",chatSession);
            startActivity(i);
        }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("PAUSE","Pausing");
    }


}
