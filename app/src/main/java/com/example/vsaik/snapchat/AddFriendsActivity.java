package com.example.vsaik.snapchat;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AddFriendsActivity extends AppCompatActivity {

    private String myName = UserDetails.getEmail();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initButtons();
    }

    private void initButtons() {
        final EditText text = (EditText) findViewById(R.id.search_text);

        Button search = (Button) findViewById(R.id.addFriends);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    new SearchFriends("add",text.getText().toString()).execute();


            }
        });
    }

    class SearchFriends extends AsyncTask<Void,Void,Void> {

        private boolean errFlag = false;
        private String method = "";
        private String search = "";

        public SearchFriends(String method,String search){
            this.method = method;
            this.search = search;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            HashMap<String,String> hashMap = new HashMap<String,String>();
            hashMap.put("username",myName);
            hashMap.put("friend_username",search);
            hashMap.put("Method","POST");
            try{

                    hashMap.put("URL",Constants.URL+"/request_friend");

                PostData post = new PostData(hashMap);
                post.doInBackground();
            }
            catch (Exception e){
                //Log.d("EXCEPTION","Exception in friendsJSON "+e.getCause());
                errFlag = true;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(errFlag)
                reportError();
            else
                showSuccess();
        }
    }

    private void reportError(){
       // Toast.makeText(getApplicationContext(),"Failed to add",Toast.LENGTH_SHORT).show();
    }

    private void showSuccess(){
        Toast.makeText(getApplicationContext(),"Added",Toast.LENGTH_SHORT).show();
    }

}
