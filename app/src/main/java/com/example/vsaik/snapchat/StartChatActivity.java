package com.example.vsaik.snapchat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class StartChatActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_INTENT = 1016;
    private static final int SELECT_PHOTO = 1017;
    private Bitmap capturedImage = null;
    private Context context= null;
    private EditText chatText = null;
    String friend = "";

    List<ChatMessage> chatMessages = null;
    TextView friendText;
    ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_click);

       imageButton = (ImageButton)findViewById(R.id.backButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent back = new Intent(StartChatActivity.this,ChatActivity.class);
                startActivity(back);
            }
        });
        friendText = (TextView)findViewById(R.id.Friend);
        if(getIntent() != null){
            String friend = getIntent().getStringExtra("friend");
            friendText.setText(friend);
            Log.d("TAG",friend);
            getInfo(friend);
            //update the name on toolbar
        }

        context = this;
        onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getInfo(friend);


        chatText = (EditText) findViewById(R.id.chatText);
        final Button sendChat = (Button) findViewById(R.id.sendChat);
        sendChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                sendChat();
            }
        });
        final Button capturePicture = (Button) findViewById(R.id.clickPicture);
        if(capturedImage != null ) {
            capturePicture.setBackgroundResource(R.drawable.click3);
            capturePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    capturedImage = null;
                    Toast.makeText(getApplicationContext(),"ClearedImage",Toast.LENGTH_SHORT).show();
                    ;
                }
            });
        }
        else {
            capturePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    final LinearLayout layout = new LinearLayout(context);

                    ImageView camera = new ImageView(context);
                    camera.setImageResource(R.drawable.click);

                    camera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST_INTENT);
                            //alert.setCancelable(true);
                        }
                    });
                    ImageView gallery = new ImageView(context);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                    camera.setLayoutParams(params);
                    gallery.setLayoutParams(params);
                    gallery.setImageResource(R.drawable.photos);
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

    }

    private void sendChat() {
        //image - capturedImage
        //chat text - chatText
        //name
        //friend name - friend
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

    private void getInfo(String friend) {
        chatMessages = getChatMessages(friend);


        ListView listActiveFriends = (ListView) findViewById(R.id.chat_listView);
        final RelativeLayout chatClick = (RelativeLayout) findViewById(R.id.chat_click);
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);

        CustomChatMessageAdapter adapter = new CustomChatMessageAdapter(this, R.layout.chat_message, chatMessages,chatClick,expandedImageView);
        listActiveFriends.setAdapter(adapter);
    }

    private List<ChatMessage> getChatMessages(String friend){

        ChatMessage itemhim = new ChatMessage(R.drawable.click,0,"this is chat message from him","HIM");
        ChatMessage item = new ChatMessage(R.drawable.click,0,"this is chat message from me","ME");
        //   ChatMessage imagehim = new ChatMessage(R.drawable.click,R.drawable.common_plus_signin_btn_icon_dark,null,"HIM");
        //   ChatMessage imageme = new ChatMessage(R.drawable.click,R.drawable.common_plus_signin_btn_icon_dark,null,"ME");

        chatMessages.add(item);
        chatMessages.add(itemhim);
        chatMessages.add(item);
        // chatMessages.add(imagehim);
        chatMessages.add(itemhim);
        //  chatMessages.add(imageme);
        chatMessages.add(itemhim);
        //  chatMessages.add(imagehim);
        chatMessages.add(item);
        chatMessages.add(itemhim);
        //  chatMessages.add(imageme);
        chatMessages.add(item);
        chatMessages.add(itemhim);
        //  chatMessages.add(imagehim);
        chatMessages.add(itemhim);
        chatMessages.add(itemhim);
        chatMessages.add(item);
        chatMessages.add(itemhim);
        chatMessages.add(item);
        //  chatMessages.add(imagehim);
        chatMessages.add(itemhim);
        //  chatMessages.add(imageme);
        chatMessages.add(itemhim);
        chatMessages.add(itemhim);
        return chatMessages;
    }
}