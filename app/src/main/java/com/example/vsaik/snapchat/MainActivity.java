package com.example.vsaik.snapchat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ArrayList<UserDetails> userList = new ArrayList<>();
    public HashMap<String,Object> updateV = new HashMap<>();
    static HashMap<String,UserDetails> usenames =  new HashMap<>();
    String currentUser;
    ArrayList<UserDetails> userL = new ArrayList<>();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    String path;
    private GoogleApiClient mGoogleApiClient;
    String imgDecodableString = " ";
    private Context context ;
    DatabaseReference myRef = mDatabase.getReference("users");
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
    String userId= " ";
    // DatabaseReference myRef = mDatabase.getReference("users");

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
                //Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                //        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // startActivityForResult(galleryIntent, 1);
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, 1);
            }});
        signOut = (ImageButton)findViewById(R.id.signout);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }});
        onStart();


    }

    public void addListenerOnButton() {

        privacy = (RadioGroup) findViewById(R.id.radioPrivacy);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = privacy.getCheckedRadioButtonId();
                String visibilty = " ";
                selectedButton = (RadioButton) findViewById(selectedId);
                if(friendsButton.isChecked())
                    visibilty="FriendsOnly";
                else if(publicButton.isChecked())
                    visibilty="Public";
                else
                    visibilty="Private";
                Log.d("imgDecodableString",imgDecodableString);
                DatabaseReference myRef1 = myRef.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                UserDetails user = new UserDetails(nickName.getText().toString(),email.getText().toString(),imgDecodableString,location.getText().toString(),profession.getText().toString(),aboutMe.getText().toString()," ",FirebaseAuth.getInstance().getCurrentUser().getUid(),visibilty,password.getText().toString());
                myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                // DatabaseReference dataV =  myRef.child(mAuth.getCurrentUser().getUid());
                // if(friendsButton.isChecked())
                //     updateV.put("visibilty","FriendsOnly");
                //else if(publicButton.isChecked())
                //    updateV.put("visibilty","Public");
                //  else
                //    updateV.put("visibilty","Private");
                // dataV.updateChildren(updateV);
                // DatabaseReference dataN =  myRef.child(mAuth.getCurrentUser().getUid());
                // updateV.clear();
                //  updateV = new HashMap<String, Object>();
                // updateV.put("nickname",nickName.getText().toString());
                // dataN.updateChildren(updateV);
                // DatabaseReference dataE =  myRef.child(mAuth.getCurrentUser().getUid());
                // updateV.clear();
                //  updateV = new HashMap<String, Object>();
                //  updateV.put("email",email.getText().toString());
                // dataE.updateChildren(updateV);
                //DatabaseReference dataP =  myRef.child(mAuth.getCurrentUser().getUid());
                //updateV.clear();
                //updateV = new HashMap<String, Object>();
                //  updateV.put("phoneNumber",password.getText().toString());
                // dataP.updateChildren(updateV);

                //DatabaseReference dataPF =  myRef.child(mAuth.getCurrentUser().getUid());
                // updateV.clear();
                //updateV = new HashMap<String, Object>();
                //updateV.put("profession",profession.getText().toString());
                //dataPF.updateChildren(updateV);

                //DatabaseReference dataL =  myRef.child(mAuth.getCurrentUser().getUid());
                //updateV.clear();
                //updateV = new HashMap<String, Object>();
                //updateV.put("location",location.getText().toString());
                //dataL.updateChildren(updateV);

                // DatabaseReference dataA =  myRef.child(mAuth.getCurrentUser().getUid());
                // updateV.clear();
                //updateV = new HashMap<String, Object>();
                //updateV.put("aboutMe",aboutMe.getText().toString());
                //dataA.updateChildren(updateV);


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
                    UserDetails user = postSnapshot.getValue(UserDetails.class);
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
                                // imageView.setImageBitmap(BitmapFactory
                                //        .decodeFile(imgDecodableString));
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
                            }else if(!user.getVisibilty().equals("") && user.getVisibilty().equals("FriendsOnly")){
                                friendsButton.setChecked(true);
                                privacyButton.setChecked(false);
                                publicButton.setChecked(false);
                            }else if(!user.getVisibilty().equals("") && user.getVisibilty().equals("Private")){
                                friendsButton.setChecked(false);
                                privacyButton.setChecked(true);
                                publicButton.setChecked(false);
                            }else if(!user.getVisibilty().equals("") && user.getVisibilty().equals("Public")){
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
        mAuth.signOut();
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
            if (requestCode == 1 && resultCode == RESULT_OK
                    && null != data) {

                Uri selectedImage = data.getData();
                imageView.setImageURI(selectedImage);
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }


}
