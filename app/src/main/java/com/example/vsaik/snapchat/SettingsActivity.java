package com.example.vsaik.snapchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {


    RadioGroup privacy;
    RadioButton selectedButton;
    RadioButton privacyButton;
    RadioButton publicButton;
    RadioButton friendsButton;
    Button button;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ArrayList<UserDetails> userList = new ArrayList<>();
    static HashMap<String,UserDetails> usenames =  new HashMap<>();
    public HashMap<String,Object> updateV = new HashMap<>();
    String currentUser;
    ArrayList<UserDetails> userL = new ArrayList<>();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String path;
    DatabaseReference myRef = mDatabase.getReference("users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String visibilty = "";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        privacyButton = (RadioButton) findViewById(R.id.radioPrivate);
        friendsButton = (RadioButton) findViewById(R.id.radioFriends);
        publicButton = (RadioButton) findViewById(R.id.radioPublic);
        fetchDteails();
        //if (mAuth.getCurrentUser()!=null){
           // currentUser = mAuth.getCurrentUser().getUid();
         //   visibilty=  usenames.get(mAuth.getCurrentUser().getUid()).getVisibilty();
       // }
       // if(!visibilty.equals("") && visibilty.equals("FriendsOnly")){
        //    friendsButton.setChecked(true);
       // }else if(!visibilty.equals("") && visibilty.equals("Private")){
       //     privacyButton.setChecked(true);
       // }else if(!visibilty.equals("") && visibilty.equals("Public")){
       //     publicButton.setChecked(true);
       // }
        button = (Button) findViewById(R.id.button);
        addListenerOnButton();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    public void addListenerOnButton() {

        privacy = (RadioGroup) findViewById(R.id.radioPrivacy);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
           int selectedId = privacy.getCheckedRadioButtonId();
           selectedButton = (RadioButton) findViewById(selectedId);

            DatabaseReference data =  myRef.child(mAuth.getCurrentUser().getUid());
                if(friendsButton.isChecked())
                    updateV.put("visibilty","FriendsOnly");
                else if(publicButton.isChecked())
                    updateV.put("visibilty","Public");
                else
                    updateV.put("visibilty","Private");

                data.updateChildren(updateV);
                Intent logout = new Intent(SettingsActivity.this,MainActivity.class);
                startActivity(logout);

           }

       });

    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchDteails();
    }
public void fetchDteails(){
    myRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            userList.clear();
            for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                UserDetails user = postSnapshot.getValue(UserDetails.class);
                try {
                    if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(postSnapshot.getKey())) {
                        if(!user.getVisibilty().equals("") && user.getVisibilty().equals("FriendsOnly")){
                            friendsButton.setChecked(true);
                        }else if(!user.getVisibilty().equals("") && user.getVisibilty().equals("Private")){
                            privacyButton.setChecked(true);
                        }else if(!user.getVisibilty().equals("") && user.getVisibilty().equals("Public")){
                            publicButton.setChecked(true);
                        }
                    }
                }catch(NullPointerException ex){
                    ex.printStackTrace();
                }
                userList.add(user);
                usenames.put(postSnapshot.getKey(),postSnapshot.getValue(UserDetails.class));
            }

        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    });
}

}
