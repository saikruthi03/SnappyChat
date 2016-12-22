package com.example.vsaik.snapchat;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ExpandedMenuView;
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

import org.json.JSONArray;
import org.json.JSONObject;

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
    String myName = "";
    String friendName = "";
    private Context context= null;
    private EditText chatText = null;
    String friend = "";
    JSONArray responseFetch = null;
    ListView listActiveFriends = null;
    List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
    TextView friendText;
    ImageButton imageButton;
    Thread mThread;
    Button capturePicture = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_click);




        if(getIntent() != null){
            friend = getIntent().getStringExtra("friend");
            friendName = getIntent().getStringExtra("fullname");
            Log.d("TAG",friend);
            //getInfo(friend);
            //update the name on toolbar
        }
        myName = UserDetails.getEmail();
        //friend = "friend";
        friendText = (TextView)findViewById(R.id.Friend);

        friendText.setText(friendName);

        context = this;
        onStart();
    }

    @Override
    protected void onStart() {
        super.onStart();
        onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mThread != null) {
                mThread.stop();
            }
        }
        catch(Exception e){
            Log.d("ERR","Failed to stop thread");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        imageButton = (ImageButton)findViewById(R.id.backButton);
        capturePicture = (Button) findViewById(R.id.clickPicture);
        listActiveFriends = (ListView) findViewById(R.id.chat_listView);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back = new Intent(StartChatActivity.this,ChatActivity.class);
                startActivity(back);
            }
        });
        Button del = (Button) findViewById(R.id.terminateChat);
        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listActiveFriends.setAdapter(null);
                Log.d("TAG","on delete");
                RetrieveChatData chatDel = new RetrieveChatData(myName, friend, "DEL");
                chatDel.execute();
            }
        });

        getInfo(friend);
        FetchChat fetchChat = new FetchChat(friend);
        mThread = new Thread(fetchChat);
        mThread.start();

        chatText = (EditText) findViewById(R.id.chatText);
        final Button sendChat = (Button) findViewById(R.id.sendChat);
        sendChat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("TAG","On click send chat");
                sendChat();
            }
        });
        capturePicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(capturedImage == null ) {

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
                    else {
                        capturedImage = null;
                        capturePicture.setBackgroundResource(R.drawable.click);
                    }
                }
            });


    }

    private void sendChat() {

        RetrieveChatData chatpush = new RetrieveChatData(myName, friend, "POST");

        String[] params = new String[2];
        params[0] = (chatText.getText().length() > 0 ) ? "text" : "image";
        params[1] = chatText.getText().toString()+"";
        chatText.setText("");
        if(capturedImage != null)
            params[1] = ImageUtils.getStringImage(capturedImage);
        capturedImage = null;
        capturePicture.setBackgroundResource(R.drawable.click);
        chatpush.execute(params);
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
        if(capturedImage != null) {
            capturePicture.setBackgroundResource(R.drawable.click3);
        }
    }

    private void getInfo(String friend) {


        RetrieveChatData chatFetch = new RetrieveChatData(myName, friend, "GET");
        chatFetch.execute();
    }
    private void updateChat(){

        chatMessages = new ArrayList<ChatMessage>();
        if(responseFetch != null ) {
            int size = responseFetch.length();
            for (int i = 0; i < size; i++) {
                ChatMessage item = null;
                try {
                    JSONObject object = responseFetch.getJSONObject(i);
                    String data = object.getString("data");
                    if (data != null && data.length() < 100) {
                        if (myName.equalsIgnoreCase(object.getString("sender"))) {
                            item = new ChatMessage(null, data, "ME");
                        } else {
                            item = new ChatMessage(null, data, "HIM");
                        }
                    } else {
                        Bitmap chatImage = ImageUtils.getBitmapFromBase64(data);
                        if (myName.equalsIgnoreCase(object.getString("sender"))) {
                            item = new ChatMessage(chatImage, "", "ME");
                        } else {
                            item = new ChatMessage(chatImage, "", "HIM");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                chatMessages.add(item);
            }
        }
        if(chatMessages != null &&  chatMessages.size() > 0) {

            final RelativeLayout chatClick = (RelativeLayout) findViewById(R.id.chatWindow);
            final ImageView expandedImageView = (ImageView) findViewById(
                    R.id.expanded_image);

            CustomChatMessageAdapter adapter = new CustomChatMessageAdapter(friendDP, myDP, this, R.layout.chat_message, chatMessages, chatClick, expandedImageView);
            listActiveFriends.setAdapter(adapter);
        }

    }
    class FetchChat extends Thread{

        String name;
        int oldCount;

        public FetchChat(String name){
            this.name = name;
        }

        @Override
        public void run() {
            super.run();
            while(1==1){
                if(responseFetch != null ) {
                    oldCount = responseFetch.length();
                    JSONArray responseFetchThread = null;
                    HashMap<String, String> hashMap = new HashMap<String, String>();
                    hashMap.put("username", myName);
                    hashMap.put("receiver", friend);
                    hashMap.put("URL", Constants.URL + "/get_chat");
                    hashMap.put("Method", "GET");
                    GetData fecth = new GetData(hashMap);
                    try {
                        String res = fecth.doInBackground();
                        res = res.substring(res.indexOf("["));
                        responseFetchThread = new JSONArray(res);
                        Log.e("RESPONSE", responseFetchThread.toString());
                        if (oldCount != responseFetchThread.length()) {
                            RetrieveChatData chatFetch = new RetrieveChatData(myName, friend, "GET");
                            chatFetch.execute();

                        }



                    } catch (Exception e) {
                        Log.e("RESPONSE- ERROR", e.toString());
                    }
                }
                try{
                    Thread.sleep(1000);
                }catch
                        (Exception e){
                    Log.e("THREAD- ERROR", e.toString());
                }



            }
        }
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

                    friendDP = BitmapFactory.decodeResource(getResources(), ImageUtils.getRandomImage());

                    if(UserDetails.getImage() != null)
                        myDP = UserDetails.getImage();
                    else {
                        myDP = BitmapFactory.decodeResource(getResources(), R.drawable.com_facebook_button_send_icon_white);
                    }
                    HashMap<String,String> hashMap = new HashMap<String, String>();
                    hashMap.put("username",myName);
                    hashMap.put("receiver",friend);
                    hashMap.put("URL",Constants.URL+"/get_chat");
                    hashMap.put("Method","GET");
                    GetData fecth = new GetData(hashMap);
                    try {
                        String res = fecth.doInBackground();
                        res = res.substring(res.indexOf("["));
                        responseFetch = new JSONArray(res);
                        Log.e("RESPONSE",responseFetch.toString());
                    }
                    catch(Exception e){
                        Log.e("RESPONSE- ERROR",e.toString());
                    }
                }
                else if("POST".equalsIgnoreCase(op)){
                    Log.d("background tasks -- ", Arrays.toString(string));
                    responseFetch = null;
                    HashMap<String,String> hashMap =  new HashMap<String,String>();
                    hashMap.put("sender",myName);
                    hashMap.put("receiver",friend);
                    hashMap.put("URL",Constants.URL+"/insert_notification");
                    hashMap.put("type",string[0]);
                    hashMap.put("data",string[1]);
                    hashMap.put("Method","POST");
                    PostData post = new PostData(hashMap);
                    String response = post.doInBackground();

                    HashMap<String,String> hashMap2 = new HashMap<String, String>();
                    hashMap2.put("username",myName);
                    hashMap2.put("receiver",friend);
                    hashMap2.put("URL",Constants.URL+"/get_chat");
                    hashMap2.put("Method","GET");
                    GetData fecth = new GetData(hashMap2);
                    try {
                        String res = fecth.doInBackground();
                        res = res.substring(res.indexOf("["));
                        responseFetch = new JSONArray(res);
                        Log.e("RESPONSE",responseFetch.toString());
                    }
                    catch(Exception e){
                        Log.e("RESPONSE- ERROR",e.toString());
                    }

                    Log.d("RESPONSE",response);
                }
                else if("DEL".equalsIgnoreCase(op)){
                    Log.d("background tasks -- ", Arrays.toString(string));

                    HashMap<String,String> hashMap =  new HashMap<String,String>();
                    hashMap.put("username",myName);
                    hashMap.put("receiver",friend);
                    hashMap.put("URL",Constants.URL+"/delete_friend_chat");

                    hashMap.put("Method","GET");
                    GetData fecth = new GetData(hashMap);
                    String response = fecth.doInBackground();
                    Log.d("RESPONSE",response);
                    responseFetch = null;
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


               // capturedImage = null;

            updateChat();
        }

    }
}