package com.example.vsaik.snapchat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class StartChatActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_INTENT = 1016;
    private static final int SELECT_PHOTO = 1017;
    private Bitmap capturedImage = null;
    Bitmap friendDP = null;
    Bitmap myDP = null;
    private Context context= null;
    private EditText chatText = null;
    String friend = "";

    List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
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
            friend = "jay";
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
                Log.d("TAG","On click send chat");
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

        String myName = "jay";
        String f = "friend";
        RetrieveChatData chatpush = new RetrieveChatData(myName, f, "POST");

        String[] params = new String[5];
        params[0] = (chatText.getText().length() > 0 ) ? "Text" : "Image";
        params[1] = chatText.getText().toString()+"";
        if(capturedImage != null)
        params[2] = capturedImage.toString()+"";

        chatpush.execute(params);
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

        String myName = "jay";
        RetrieveChatData chatFetch = new RetrieveChatData(myName, friend, "GET");
        chatFetch.execute();
    }
    private void updateChat(){

        ListView listActiveFriends = (ListView) findViewById(R.id.chat_listView);
        final RelativeLayout chatClick = (RelativeLayout) findViewById(R.id.chat_click);
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image);

        CustomChatMessageAdapter adapter = new CustomChatMessageAdapter(friendDP, myDP, this, R.layout.chat_message, chatMessages,chatClick,expandedImageView);
        listActiveFriends.setAdapter(adapter);
    }


    class RetrieveChatData extends AsyncTask<String,Void,Void>{

        private String myName = "";
        private String friend = "";
        private String op = "";

        public RetrieveChatData(String myName,String friend,String op){
            this.op = op;
            this.myName = myName;
            this.friend = friend;
        }

        @Override
        protected Void doInBackground(String... string) {

            Log.d("background tasks -- ", Arrays.toString(string)+" : "+op+" : "+myName+ " : "+friend);
            if(myName.length() > 0 && friend.length() > 0) {
                if("GET".equalsIgnoreCase(op)){
                    friendDP = BitmapFactory.decodeResource(getResources(), R.drawable.com_facebook_button_icon_blue);
                    myDP = BitmapFactory.decodeResource(getResources(), R.drawable.com_facebook_button_send_icon_white);

                    ChatMessage itemhim = new ChatMessage(null,"this is chat message from him","HIM");
                    ChatMessage item = new ChatMessage(null,"this is chat message from me","ME");
                    Bitmap chatImage = BitmapFactory.decodeResource(getResources(),R.drawable.common_google_signin_btn_icon_dark_pressed);
                    ChatMessage imagehim = new ChatMessage(chatImage,"","HIM");

                    chatMessages.add(item);
                    chatMessages.add(itemhim);
                    chatMessages.add(item);
                    chatMessages.add(itemhim);
                    chatMessages.add(itemhim);
                    chatMessages.add(item);
                    chatMessages.add(imagehim);
                    chatMessages.add(item);
                    chatMessages.add(itemhim);
                    chatMessages.add(itemhim);
                    chatMessages.add(itemhim);
                    chatMessages.add(imagehim);
                    chatMessages.add(itemhim);
                    chatMessages.add(item);
                    chatMessages.add(itemhim);
                    chatMessages.add(itemhim);
                    chatMessages.add(itemhim);
                }
                else if("POST".equalsIgnoreCase(op)){
                    Log.d("background tasks -- ", Arrays.toString(string));

                    HashMap<String,String> hashMap =  new HashMap<String,String>();
                    hashMap.put("myName",myName);
                    hashMap.put("friend",friend);
                    hashMap.put("URL",Constants.URL+"/insert_notification");
                    hashMap.put("type",string[0]);
                    hashMap.put("msg",string[1]);
                    hashMap.put("image",string[2]);
                    hashMap.put("method","POST");
                    PostData post = new PostData(hashMap);
                    String response = post.doInBackground();
                    Log.d("TAG",response);
                   // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();




                }

            }
            /*returner.put("chatMessages",chatMessages);
            returner.put("myDP",myDP);
            returner.put("friendDP",friendDP);*/

            return null;
        }

        @Override
        protected void onPostExecute(Void hashMap) {
            super.onPostExecute(hashMap);

            if("POST".equalsIgnoreCase(op)){
                Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
            }
            else{
                updateChat();
            }
        }

    }
}