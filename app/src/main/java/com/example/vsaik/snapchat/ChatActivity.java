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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener, OnMessagesReceived, OnInMessagesReceived{

    ListView listActiveFriends;
    public List<ChatItem> activeFriends ;
    private String chatSession = "";
    private Context context= null;
    ListView listView;
    ImageView newConv;
    ImageView back;
    TextView appName;
    TextView userName;
    private static final int CAMERA_REQUEST_INTENT = 1016;
    private static final int SELECT_PHOTO = 1017;
    private Bitmap capturedImage = null;
    List<String> list = new ArrayList<String>();
    List<ChatMessage> chatMessages = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // toolbar.setVisibility(View.VISIBLE);
       // setSupportActionBar(toolbar);
        //if(getSupportActionBar() != null){
         //   getSupportActionBar().show();
       // }

        listActiveFriends = (ListView) findViewById(R.id.listView);
        context = this;
        activeFriends = new ArrayList<ChatItem>();
        new DownloadAllMessages(this).execute();
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
        Log.d("TAG","On resume method");
        ImageView allFriends = (ImageView) findViewById(R.id.newChat);
        allFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),AllFriendsChatActivity.class);
                startActivity(i);
            }
        });
       // activeFriends = new ArrayList<ChatItem>();
        //new DownloadAllMessages(context).execute();
        //ChatItem item = new ChatItem(R.drawable.click, "Friend 1", R.drawable.greendot);
        //ChatItem item2 = new ChatItem(R.drawable.click, "Friend 2 Friend 2", R.drawable.greendot);
        //activeFriends.add(item);
        //activeFriends.add(item2);
        //activeFriends.add(item2);
        //activeFriends.add(item2);
    }

    public void setAllMessages(List<ChatItem> activeFriends){
        this.activeFriends = activeFriends;
        if(activeFriends.size() > 0) {
            CustomChatVewAdapter adapter = new CustomChatVewAdapter(context, R.layout.chat_list_item, activeFriends);
            listActiveFriends.setAdapter(adapter);
            listActiveFriends.setOnItemClickListener(this);
        }
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
        startChatSession(chatSession);
    }

    private void startChatSession(final String name){

        setContentView(R.layout.activity_chat_click);
        final Button capturePicture = (Button) findViewById(R.id.clickPicture);
        if(capturedImage != null ) {
            capturePicture.setBackgroundResource(R.drawable.click3);
            capturePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    capturedImage = null;
                    Toast.makeText(getApplicationContext(),"ClearedImage",Toast.LENGTH_SHORT).show();
                    startChatSession(name);
                }
            });
        }
        else {
            capturePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    final LinearLayout layout = new LinearLayout(context);

                    ImageView camera = new ImageView(context);
                    camera.setImageResource(R.drawable.click);

                    camera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_INTENT);
                        }
                    });
                    ImageView gallery = new ImageView(context);

                    // gallery.setImageResource(R.drawable.common_plus_signin_btn_text_light);
                    gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                        }
                    });
                    layout.addView(camera);
                    layout.addView(gallery);
                    alert.setView(layout);

                    alert.show();

                }
            });
        }
        chatMessages = new ArrayList<ChatMessage>();
        new DownloadIndividualMessages(this).execute();
       // ChatMessage itemhim = new ChatMessage(R.drawable.click,0,"this is chat message from him","HIM");
       // ChatMessage item = new ChatMessage(R.drawable.click,0,"this is chat message from me","ME");
        //   ChatMessage imagehim = new ChatMessage(R.drawable.click,R.drawable.common_plus_signin_btn_icon_dark,null,"HIM");
        //   ChatMessage imageme = new ChatMessage(R.drawable.click,R.drawable.common_plus_signin_btn_icon_dark,null,"ME");

       // chatMessages.add(item);
       // chatMessages.add(itemhim);
       // chatMessages.add(item);
        // chatMessages.add(imagehim);
       // chatMessages.add(itemhim);
        //  chatMessages.add(imageme);
       // chatMessages.add(itemhim);
        //  chatMessages.add(imagehim);
       // chatMessages.add(item);
       // chatMessages.add(itemhim);
        //  chatMessages.add(imageme);
       // chatMessages.add(item);
       // chatMessages.add(itemhim);
        //  chatMessages.add(imagehim);
       // chatMessages.add(itemhim);
        //chatMessages.add(itemhim);
        //chatMessages.add(item);
        //chatMessages.add(itemhim);
        //chatMessages.add(item);
        //  chatMessages.add(imagehim);
       // chatMessages.add(itemhim);
        //  chatMessages.add(imageme);
        //chatMessages.add(itemhim);
        //chatMessages.add(itemhim);

           }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("PAUSE","Pausing");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_INTENT && resultCode == Activity.RESULT_OK) {
            capturedImage = (Bitmap) data.getExtras().get("data");
        }
        else if (requestCode == SELECT_PHOTO && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                capturedImage = BitmapFactory.decodeStream(imageStream);
            }
            catch(Exception e) {
                Log.d("GALLERY","Exception in gallery result intent");
            }
        }
    }


   @Override
   public void OnMessagesReceived(List<ChatItem> activeFriends){
       setAllMessages(activeFriends);
   }

@Override
    public void OnInMessagesReceived(List<ChatMessage> allMessages){
    chatMessages = allMessages;
    listView = (ListView) findViewById(R.id.chat_listView);
    final RelativeLayout chatClick = (RelativeLayout) findViewById(R.id.chat_click);
    final ImageView expandedImageView = (ImageView) findViewById(
            R.id.expanded_image);

    CustomChatMessageAdapter adapter = new CustomChatMessageAdapter(this, R.layout.chat_message, chatMessages,chatClick,expandedImageView);
    listView.setAdapter(adapter);
}

}
