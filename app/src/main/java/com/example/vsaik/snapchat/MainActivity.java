package com.example.vsaik.snapchat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
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

import org.json.JSONArray;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    String imgDecodableString = " ";
    private Context context ;
    DatabaseReference myRef = mDatabase.getReference(Constants.dataBase);

    TextView userName;
    EditText interests;
    EditText profession;
    EditText aboutMe;
boolean addInterests= false;
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

boolean signout = false;
    ImageButton signOut ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View contentView = (View)findViewById(R.id.timeLine);
        contentView.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeTop() {
                Intent main = new Intent(MainActivity.this,MainScreen.class);
                startActivity(main);
            }

        });
        privacyButton = (RadioButton) findViewById(R.id.radioPrivate);
        friendsButton = (RadioButton) findViewById(R.id.radioFriends);
        publicButton = (RadioButton) findViewById(R.id.radioPublic);
        userName = (TextView) findViewById(R.id.username);
        userName.setText(UserDetails.getUserName());
        interests= (EditText) findViewById(R.id.interestsText);
        interests.setText(UserDetails.getInterests());
        profession=(EditText)findViewById(R.id.professionText);
        profession.setText(UserDetails.getProfession());
        aboutMe=(EditText)findViewById(R.id.aboutMeText);
        aboutMe.setText(UserDetails.getAboutMe());


        try {
            if (!UserDetails.getVisibilty().equals("") && UserDetails.getVisibilty().equals(" ")) {
                friendsButton.setChecked(true);
                privacyButton.setChecked(false);
                publicButton.setChecked(false);
            } else if (!(UserDetails.getVisibilty().equals("")) && UserDetails.getVisibilty().equals(Constants.Friends)) {
                friendsButton.setChecked(true);
                privacyButton.setChecked(false);
                publicButton.setChecked(false);
            } else if (!(UserDetails.getVisibilty().equals("")) && UserDetails.getVisibilty().equals(Constants.Private)) {
                friendsButton.setChecked(false);
                privacyButton.setChecked(true);
                publicButton.setChecked(false);
            } else if (!(UserDetails.getVisibilty().equals("")) && UserDetails.getVisibilty().equals(Constants.Public)) {
                friendsButton.setChecked(false);
                privacyButton.setChecked(false);
                publicButton.setChecked(true);
            }else{
                friendsButton.setChecked(true);
                privacyButton.setChecked(false);
                publicButton.setChecked(false);
            }
        }catch(Exception ex){
            friendsButton.setChecked(true);
            privacyButton.setChecked(false);
            publicButton.setChecked(false);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //fetchData();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

       // Toast.makeText(this,"THIS IS USER PROFILE",Toast.LENGTH_LONG).show();
        imageView = (ImageView) findViewById(R.id.imageView);
        try{ if(!UserDetails.getProfilePicUrl().equals(null) && !UserDetails.getProfilePicUrl().equals(" ") && !UserDetails.getProfilePicUrl().equals("")){
            imageView.setImageBitmap(ImageUtils.decodeBase64(UserDetails.getProfilePicUrl()));
        }else{
            //Toast.makeText(this,"In else block",Toast.LENGTH_LONG).show();
        }
        }catch(Exception ex){
           // Toast.makeText(this,"In exception block",Toast.LENGTH_LONG).show();
        }
        button = (Button) findViewById(R.id.signUp);
        addListenerOnButton();
        selectImage = (Button) findViewById(R.id.selectPic);
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Inside onclick","Inside onclivk");
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
                if(!UserDetails.getInterests().equals(interests.getText().toString())){
                    addInterests = true;
                    UserDetails.setInterests(interests.getText().toString());
                }

                UserDetails.setProfession(profession.getText().toString());
                UserDetails.setAboutMe(aboutMe.getText().toString());
                try {
                    UserDetails.setProfilePicUrl(imgDecodableString);
                }catch(Exception ex){

                }

                UserDetails.setVisibilty(visibilty);
                try {
                    userMap = new HashMap<String, String>();
                    userMap.put("Method","POST");
                    userMap.put("URL", Constants.URL + "/insert_user");
                    userMap.put("email", UserDetails.getEmail());
                    userMap.put("username", UserDetails.getUserName());
                    userMap.put("fullname", UserDetails.getFullName());
                    userMap.put("interests", UserDetails.getInterests());
                    userMap.put("profession", UserDetails.getProfession());
                    userMap.put("about", UserDetails.getAboutMe());
                    userMap.put("account_type", visibilty);
                    //Toast.makeText(getApplicationContext(),"Visibilyt"+visibilty,Toast.LENGTH_SHORT).show();
                    userMap.put("is_active_timeline", "false");
                   //userMap.put("profile_pic", imgDecodableString);
                    //Toast.makeText(getApplicationContext(),"imgDecodableString"+imgDecodableString,Toast.LENGTH_SHORT).show();
                    userMap.put("isActive", "true");
                    new InsertUser(userMap).execute();
                }catch(Exception ex){
                   // Toast.makeText(getApplicationContext(),"got execptio",Toast.LENGTH_SHORT).show();
                }
                Intent mainScreen = new Intent(MainActivity.this,MainScreen.class);
                startActivity(mainScreen);

            }

        });

    }

    class InsertUser extends AsyncTask<Void,Void,Void>{
        HashMap<String,String> hashMap2 = null;

        public InsertUser(HashMap<String,String> hashMap){
            this.hashMap2 = hashMap;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            if(!signout){
            userMap = new HashMap<>();
            userMap.put("Method","GET");
            userMap.put("URL", Constants.URL + "/update_account_type");
            userMap.put("username",UserDetails.getUserName());
            userMap.put("account_type",UserDetails.getVisibilty());
            GetData fetch = new GetData(userMap);
            try {
                String res = fetch.doInBackground();
            }
            catch(Exception e){
                Log.e("RESPONSE- ERROR",e.toString());
            }
            if(addInterests) {
                addInterests = false;
                userMap = new HashMap<>();
                userMap.put("Method", "GET");
                userMap.put("URL", Constants.URL + "/add_interests");
                userMap.put("username", UserDetails.getUserName());
                userMap.put("interests", UserDetails.getInterests());
                GetData fetch2 = new GetData(userMap);
                try {
                    String res = fetch2.doInBackground();

                } catch (Exception e) {
                    Log.e("RESPONSE- ERROR", e.toString());
                }
            }
                userMap = new HashMap<>();
            userMap.put("Method", "POST");
            userMap.put("URL", Constants.URL + "/update_profile_pic");
            userMap.put("username", UserDetails.getUserName());
            userMap.put("profile_pic", imgDecodableString);
            PostData fetch3 = new PostData(userMap);
            try {
                String res = fetch3.doInBackground();

            } catch (Exception e) {
                Log.e("RESPONSE- ERROR", e.toString());
            }
            userMap = new HashMap<>();
            userMap.put("Method", "GET");
            userMap.put("URL", Constants.URL + "/update_about");
            userMap.put("username", UserDetails.getUserName());
            userMap.put("about", UserDetails.getAboutMe());
            GetData fetch4 = new GetData(userMap);
            try {
                String res = fetch4.doInBackground();

            } catch (Exception e) {
                Log.e("RESPONSE- ERROR", e.toString());
            }
            userMap = new HashMap<>();
            userMap.put("Method", "GET");
            userMap.put("URL", Constants.URL + "/update_profession");
            userMap.put("username", UserDetails.getUserName());
            userMap.put("profession", UserDetails.getProfession());
            GetData fetch5 = new GetData(userMap);
            try {
                String res = fetch5.doInBackground();

            } catch (Exception e) {
                Log.e("RESPONSE- ERROR", e.toString());
            }}else{
                userMap = new HashMap<>();
                userMap.put("Method", "GET");
                userMap.put("URL", Constants.URL + "/is_active_false_user");
                userMap.put("username", UserDetails.getUserName());
                GetData fetch5 = new GetData(userMap);
                try {
                    String res = fetch5.doInBackground();
                } catch (Exception e) {
                    Log.e("RESPONSE- ERROR", e.toString());
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
           // Toast.makeText(getApplicationContext(),"Inside on post exec",Toast.LENGTH_SHORT).show();
if(signout){
    signout = false;
    UserDetails.setUserId("");
    UserDetails.setEmail("");
    UserDetails.setUserName("");
    UserDetails.setInterests("");
    UserDetails.setAboutMe("");
    UserDetails.setLocation("");
    UserDetails.setProfilePicUrl("");
    UserDetails.setVisibilty("");
    UserDetails.setProfession("");
    Intent mainScreen = new Intent(MainActivity.this,LoginActivity.class);
    startActivity(mainScreen);
}
        }

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
    }

    @Override
    protected void onStart() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
        context = this;
        //fetchData();
    }




    public void signOut(){

        try {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception ex){

        }
        try {
            if(UserDetails.getProvider().equals("F"))
                LoginManager.getInstance().logOut();
        }catch(Exception ex){

        }
        LoginActivity.curUser= "NA";
        mAuth.signOut();
        new InsertUser(userMap).execute();
        signout = true;


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
                final InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                final Bitmap selectedImagebm = BitmapFactory.decodeStream(imageStream);
                imageView.setImageBitmap(selectedImagebm);

               // Bitmap bm  =ImageUtils.compress(selectedImagebm);
                imgDecodableString=  ImageUtils.encodeToBase64(selectedImagebm,Bitmap.CompressFormat.PNG,100);

                UserDetails.setImage(selectedImagebm);
                UserDetails.setProfilePicUrl(imgDecodableString);
                /*Toast.makeText(this, "Hello"+UserDetails.getProfilePicUrl().length()+" ",
                        Toast.LENGTH_LONG).show();*/
            } else {
                Toast.makeText(this, "You haven't picked Image"+requestCode,
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.getMessage();
            Toast.makeText(this,  e.getLocalizedMessage(), Toast.LENGTH_LONG)
                    .show();
        }

    }



}


