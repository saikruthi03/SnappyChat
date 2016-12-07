package com.example.vsaik.snapchat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ArrayList<User> userList = new ArrayList<>();
    public HashMap<String,Object> updateV = new HashMap<>();
    static HashMap<String,User> usenames =  new HashMap<>();
    String currentUser;
    ArrayList<UserDetails> userL = new ArrayList<>();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    public static HashMap<String,String> userMap= new HashMap<String,String>();
    String path;
    private GoogleApiClient mGoogleApiClient;
    String imgDecodableString = "No Image";
    private Context context ;
    DatabaseReference myRef = mDatabase.getReference(Constants.dataBase);
    // TextView nickName;
    EditText nickName;
    EditText email;
    EditText password;
    EditText profession;
    EditText aboutMe;
    EditText location;
    RadioGroup privacy;
    RadioButton selectedButton;
    RadioButton privacyButton;
    RadioButton publicButton;
    RadioButton friendsButton;
    Button button;
    Button selectImage;
    ImageView imageView ;
    PushUser pushUser = new PushUser();
    String userId= " ";
    private int PICK_IMAGE_REQUEST = 1;


    ImageButton signOut ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View contentView = (View)findViewById(R.id.timeLine);
        contentView.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeLeft() {
                Intent main = new Intent(MainActivity.this,MainScreen.class);
                startActivity(main);
            }

        });
        privacyButton = (RadioButton) findViewById(R.id.radioPrivate);
        friendsButton = (RadioButton) findViewById(R.id.radioFriends);
        publicButton = (RadioButton) findViewById(R.id.radioPublic);
        nickName = (EditText)findViewById(R.id.nameText);
        nickName.setText(" ");
        email=(EditText)findViewById(R.id.emailText);
        email.setText(" ");
        password= (EditText) findViewById(R.id.passwordText);
        password.setText(" ");
        profession=(EditText)findViewById(R.id.professionText);
        profession.setText(" ");
        aboutMe=(EditText)findViewById(R.id.aboutMeText);
        aboutMe.setText(" ");
        location=(EditText)findViewById(R.id.locationText);
        location.setText(" ");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fetchData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Toast.makeText(this,"THIS IS USER PROFILE",Toast.LENGTH_LONG).show();
        imageView = (ImageView) findViewById(R.id.imageView);
        button = (Button) findViewById(R.id.signUp);
        addListenerOnButton();
        selectImage = (Button) findViewById(R.id.selectPic);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }});
        signOut = (ImageButton)findViewById(R.id.signout);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }});
        onStart();


    }
    String visibilty = " ";
    public void addListenerOnButton() {

        privacy = (RadioGroup) findViewById(R.id.radioPrivacy);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = privacy.getCheckedRadioButtonId();

                selectedButton = (RadioButton) findViewById(selectedId);
                if(friendsButton.isChecked())
                    visibilty=Constants.Friends;
                else if(publicButton.isChecked())
                    visibilty=Constants.Public;
                else
                    visibilty=Constants.Private;
                DatabaseReference myRef1 = myRef.child(Constants.dataBase).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                UserDetails.setEmail(email.getText().toString());
                UserDetails.setNickname(nickName.getText().toString());
                UserDetails.setPhoneNumber(password.getText().toString());
                UserDetails.setInterests(" ");
                UserDetails.setAboutMe(location.getText().toString());
                UserDetails.setLocation(aboutMe.getText().toString());
                UserDetails.setProfilePicUrl(imgDecodableString);
                UserDetails.setImage(ImageUtils.getBitmapFromBase64(UserDetails.getProfilePicUrl()));
                UserDetails.setVisibilty(visibilty);
                UserDetails.setProfession(profession.getText().toString());
                User user = new User();
                user.setUserId(UserDetails.getUserId());
                user.setEmail(UserDetails.getEmail());
                user.setNickname(UserDetails.getNickname());
                user.setPhoneNumber(UserDetails.getPhoneNumber());
                user.setInterests(UserDetails.getInterests());
                user.setAboutMe(UserDetails.getAboutMe());
                user.setLocation(UserDetails.getLocation());
                user.setProfilePicUrl(imgDecodableString);
                user.setVisibilty(UserDetails.getVisibilty());
                myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                userMap = new HashMap<String, String>();
                userMap.put("URL",Constants.URL+"/insert_user?");
                userMap.put("email",UserDetails.getEmail());
                userMap.put("username",UserDetails.getEmail());
                userMap.put("fullname",UserDetails.getNickname());
                userMap.put("interests",UserDetails.getInterests());
                userMap.put("about",UserDetails.getAboutMe());
                userMap.put("account_type",visibilty);
                userMap.put("is_active_timeline","false");
                userMap.put("thumbnail_profile_pic",imgDecodableString);
                userMap.put("isActive","true");
                pushUser.insertData(userMap);
                Intent mainScreen = new Intent(MainActivity.this,MainScreen.class);
                startActivity(mainScreen);

            }

        });

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
    }

    @Override
    protected void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
        context = this;
        fetchData();
    }


    public void fetchData(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    try {
                        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(postSnapshot.getKey())) {
                            if(!user.getNickname().equals("")){
                                nickName.setText(user.getNickname());
                            }
                            if(!user.getEmail().equals("")){
                                email.setText(user.getEmail());
                            }
                            if(!user.getProfilePicUrl().equals("")){
                                imgDecodableString = user.getProfilePicUrl();
                                imageView.setImageBitmap(ImageUtils.getBitmapFromBase64(imgDecodableString));
                            }
                            if(!user.getPhoneNumber().equals("")){
                                password.setText(user.getPhoneNumber());
                            }
                            if(!user.getLocation().equals("")){
                                location.setText(user.getLocation());
                            }
                            if(!user.getProfession().equals("")){
                                profession.setText(user.getProfession());
                            }
                            if(!user.getAboutMe().equals("")){
                                aboutMe.setText(user.getAboutMe());
                            }
                            if(!user.getVisibilty().equals("") && user.getVisibilty().equals(" ")){
                                friendsButton.setChecked(false);
                                privacyButton.setChecked(false);
                                publicButton.setChecked(false);
                            }else if(!user.getVisibilty().equals("") && user.getVisibilty().equals(Constants.Friends)){
                                friendsButton.setChecked(true);
                                privacyButton.setChecked(false);
                                publicButton.setChecked(false);
                            }else if(!user.getVisibilty().equals("") && user.getVisibilty().equals(Constants.Private)){
                                friendsButton.setChecked(false);
                                privacyButton.setChecked(true);
                                publicButton.setChecked(false);
                            }else if(!user.getVisibilty().equals("") && user.getVisibilty().equals(Constants.Public)){
                                friendsButton.setChecked(false);
                                privacyButton.setChecked(false);
                                publicButton.setChecked(true);
                            }


                        }

                    }catch(NullPointerException ex){
                        ex.printStackTrace();
                    }

                    userList.add(user);
                    usenames.put(postSnapshot.getKey(),user);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }

    public void signOut(){

        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                        Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                        //Intent i=new Intent(getApplicationContext(),MainActivity.class);
                        //startActivity(i);
                    }
                });
        LoginActivity.curUser= "NA";
        userMap = new HashMap<>();
        userMap.put("URL",Constants.URL+"'/is_active_false_user?");
        userMap.put("email",UserDetails.getEmail());
        userMap.put("username",UserDetails.getEmail());
        userMap.put("fullname",UserDetails.getNickname());
        userMap.put("interests",UserDetails.getInterests());
        userMap.put("about",UserDetails.getAboutMe());
        userMap.put("account_type",visibilty);
        userMap.put("is_active_timeline","false");
        userMap.put("thumbnail_profile_pic",imgDecodableString);
        userMap.put("isActive","false");
        PushUser pushUser = new PushUser();
        pushUser.insertData(userMap);
        mAuth.signOut();
        UserDetails.setUserId("");
        UserDetails.setEmail("");
        UserDetails.setNickname("");
        UserDetails.setPhoneNumber("");
        UserDetails.setInterests("");
        UserDetails.setAboutMe("");
        UserDetails.setLocation("");
        UserDetails.setProfilePicUrl("");
        UserDetails.setVisibilty("");
        UserDetails.setProfession("");
        Intent mainScreen = new Intent(MainActivity.this,LoginActivity.class);
        startActivity(mainScreen);
    }


    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                    && null != data) {

                Uri selectedImage = data.getData();
                imageView.setImageURI(selectedImage);
                Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                bm =ImageUtils.compressEx(bm);
                imgDecodableString=  ImageUtils.getStringImage(bm);
                UserDetails.setImage(bm);
                FirebaseDatabase.getInstance()
                        .getReference(Constants.dataBase)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child("profilePicUrl").setValue(imgDecodableString);


            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.getMessage();
            Toast.makeText(this,  e.getLocalizedMessage(), Toast.LENGTH_LONG)
                    .show();
        }

    }



}

