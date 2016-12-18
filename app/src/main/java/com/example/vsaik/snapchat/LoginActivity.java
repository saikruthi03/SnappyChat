package com.example.vsaik.snapchat;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    ProgressDialog pd;
    private static final String TAG = "EmailPassword";
    static HashMap<String,User> userArrayList = new HashMap<>();
    public HashMap<String,String> userMap= new HashMap<String,String>();
    public List<String> userId = new ArrayList<>();
    public boolean redirect= false;
    public static String curUser = "NA";
    LoginButton fbLogin;
    ProgressDialog progressDialog;
    Context ctx=null;
    public String imgDecodableString = " ";


    public static FirebaseAuth mAuth = null;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mCallbackManager;
    private GoogleApiClient mGoogleApiClient;
    String userN = " ";
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = mDatabase.getReference(Constants.dataBase);
    FirebaseUser user;
    JSONArray responseFetch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        ctx = this;
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    curUser = user.getUid();
                } else {
                    //updateUI(user);
                }

            }
        };

        mCallbackManager = CallbackManager.Factory.create();
        Log.d(TAG, "onAuthStateChanged:signed_in:" + "helooo");
        setContentView(R.layout.activity_login);
        fbLogin = (LoginButton) findViewById(R.id.fb_login_button);
        fbLogin.setReadPermissions("email", "public_profile");
        //fbLogin.setReadPermissions("public_profile","public_profile");
        fbLogin.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {


            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(),"Oopss!!Something Went Wrong",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = new Intent(this, GetMessagesService.class);
        startService(intent);


        SignInButton googlesignInButton = (SignInButton)findViewById(R.id.google_signin_button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        try{ mGoogleApiClient= new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
            googlesignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent signInIntent=Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(signInIntent,1004);
                }
            });}catch(IllegalStateException ex){

        }catch(Exception ex){

        }

        try{ if(user.getUid() != null){
            Log.d("Exc","Exc"+user.getUid());
            userN = user.getUid();
            fetchUserDetails();
            startCameraActivity(userN);
        }}catch(Exception ex){
            Log.d("Exc","Exc"+userN);
        }
    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userArrayList.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    userArrayList.put(postSnapshot.getKey(),postSnapshot.getValue(User.class));
                    userId.add(postSnapshot.getKey());
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void fetchUserDetails(){
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    if(user.equals(postSnapshot.getKey())){
                        try {
                            User user = postSnapshot.getValue(User.class);
                            try{
                                UserDetails.setUserId(postSnapshot.getKey());
                            }catch(Exception e){

                            }
                            try{
                                UserDetails.setEmail(user.getEmail());
                            }catch(Exception e){

                            }
                            try{
                                UserDetails.setEmail(user.getNickname());
                            }catch(Exception e){

                            }

                            try{if (!user.getProfilePicUrl().isEmpty()) {
                                UserDetails.setProfilePicUrl(user.getProfilePicUrl());
                                UserDetails.setImage(ImageUtils.getBitmapFromBase64(user.getProfilePicUrl()));
                            } else {
                                UserDetails.setProfilePicUrl(imgDecodableString);
                            }}catch(Exception ex){

                            }
                            try {
                                UserDetails.setInterests(user.getInterests());
                                UserDetails.setAboutMe(user.getAboutMe());
                                UserDetails.setLocation(user.getLocation());
                                UserDetails.setVisibilty(user.getVisibilty());
                            }catch(Exception e){

                            }

                        }catch(Exception ex){

                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("In actiivty result ","In aaa");
        showProgressDialog();
        if (requestCode == 101) {
            if (resultCode == RESULT_CANCELED) {
                progressDialog.dismiss();
            }
        }
        if(requestCode==1004)
        { Log.d("In actiivty result ","1004");
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //   if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
            //   } else {
            // updateUI(null);
            // }
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Inside fire","ifffff");
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d("Inside itask not","ifffff");
                            Toast.makeText(getApplicationContext(),"Google Login Failed",Toast.LENGTH_SHORT).show();
                        }else {
                            final FirebaseUser mUser = mAuth.getCurrentUser();
                            myRef.child(Constants.dataBase).child(mUser.getUid()).addListenerForSingleValueEvent(new     ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.hasChild("email")) {
                                        Log.d("Inside iffff",mUser.getEmail());
                                        DatabaseReference myref1 = myRef.child(Constants.dataBase).child(mUser.getUid());
                                        User user = new User();
                                        user.setUserId(mUser.getUid());
                                        user.setEmail(mUser.getEmail());
                                        user.setNickname(mUser.getDisplayName());
                                        user.setUserId(mUser.getUid());
                                        user.setPhoneNumber(" ");
                                        user.setInterests(" ");
                                        user.setAboutMe(" ");
                                        user.setLocation(" ");
                                        user.setProfilePicUrl(" ");
                                        user.setVisibilty(Constants.Friends);
                                        myRef.child(mUser.getUid()).setValue(user);
                                        userId.add(mUser.getUid());
                                        UserDetails.setEmail(mUser.getEmail());
                                        UserDetails.setUserId(mUser.getUid());
                                        UserDetails.setProvider("G");
                                        UserDetails.setFullName(mUser.getDisplayName());
                                        new RetrieveUsers().execute();
                                        Log.d("after iffff","ifffff");
                                    }else {
                                        Log.d("inside elseeeeeeee","elseeeeeee");
                                        new RetrieveUsers().execute();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });

    }

    private void showProgressDialog() {
        progressDialog = ProgressDialog.show(this, "","Please Wait...", true);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        final FirebaseUser fbUser = mAuth.getCurrentUser();
                        myRef.child(Constants.dataBase).child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (!dataSnapshot.hasChild("email")) {
                                    DatabaseReference myref1 = myRef.child(Constants.dataBase).child(fbUser.getUid());
                                    String image;
                                    Log.d("Profile pic url",fbUser.getPhotoUrl()+"");
                                    try {
                                        //  image = "https://graph.facebook.com/7OxKuc7OHrfXovG69q2AmC3MCKV2/picture?type=large"
                                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fbUser.getPhotoUrl());
                                        imgDecodableString = ImageUtils.getStringImage(bitmap);
                                    }catch (IOException ex){
                                        imgDecodableString =" ";
                                    }


                                        try {
                                            User user = new User(fbUser.getDisplayName(), fbUser.getEmail(), imgDecodableString, " ", " ", " ", " ", fbUser.getUid(), " ", " ");
                                            myRef.child(fbUser.getUid()).setValue(user);
                                        }catch(Exception ex){

                                        }
                                        // setUserDetails(fbUser.getEmail(),"F",fbUser.getDisplayName(),fbUser.getUid()," "," "," ",imgDecodableString,Constants.Friends);
                                    UserDetails.setEmail(fbUser.getEmail());
                                    UserDetails.setUserId(fbUser.getUid());
                                    UserDetails.setProvider("F");
                                    UserDetails.setFullName(fbUser.getDisplayName());
                                    new RetrieveUsers().execute();

                                        //progressDialog.dismiss();

                                      //  Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                       // startActivity(intent);

                                }else {
                                    UserDetails.setEmail(fbUser.getEmail());
                                    UserDetails.setUserId(fbUser.getUid());
                                    UserDetails.setProvider("F");
                                    UserDetails.setFullName(fbUser.getDisplayName());
                                    new RetrieveUsers().execute();
//                                        Intent intent = new Intent(EmailPasswordActivity.this, MainActivity.class);
//                                        startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });





                    }
                });
    }

    private void startCameraActivity(String userId) {
        Intent passer = new Intent(this,MainScreen.class);
        passer.putExtra("UserId",userId);
        startActivity(passer);
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }






    class RetrieveUsers extends
            AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String userName =UserDetails.getEmail().split("\\@")[0];
            int size = 0;
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put("username",userName);
            UserDetails.setUserName(userName);
            hashMap.put("URL", Constants.URL + "/check_user");
            hashMap.put("Method", "GET");
            GetData fetch = new GetData(hashMap);
            try {
                try {
                    responseFetch = new JSONArray(fetch.doInBackground());
                    Log.e("RESPONSE", responseFetch.toString());

                } catch (Exception e) {
                    responseFetch = null;
                    Log.e("RESPONSE- ERROR", e.toString());
                }

            } catch (Exception e) {
                Log.e("RESPONSE- ERROR", e.toString());
            }

            if(responseFetch != null ) {
               size = responseFetch.length();
                for (int i = 0; i < size; i++) {
                    try {
                        JSONObject object = responseFetch.getJSONObject(i);
                        try {
                            UserDetails.setAboutMe(object.getString("about"));
                        }catch(Exception ex){

                        }
                        try {
                            String interests = object.getJSONArray("interests").toString();
                            String first = interests.replaceAll("\\[", "").replaceAll("\\]","").replaceAll("\"","");
                            UserDetails.setInterests(first);
                        }catch(Exception ex){

                        }

                       try {
                           UserDetails.setProfilePicUrl(object.getString("profile_pic"));
                       }catch(Exception ex){

                       }
                        try{UserDetails.setVisibilty(object.getString("account_type"));
                    }catch(Exception ex){

                    }
                      try{  UserDetails.setProfession("profession");
                      }catch(Exception ex){

                      }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

if(size == 0){
    try {
        userMap = new HashMap<String, String>();
        userMap.put("Method","POST");
        userMap.put("URL", Constants.URL + "/insert_user");
        userMap.put("email", UserDetails.getEmail());
        userMap.put("username", UserDetails.getUserName());
        userMap.put("fullname", UserDetails.getFullName());
        userMap.put("interests", " ");
        userMap.put("profession", " ");
        userMap.put("about", " ");
        userMap.put("account_type", Constants.Friends);
        userMap.put("is_active_timeline", "false");
        userMap.put("isActive", "true");
        PostData post = new PostData(userMap);
         String response = post.doInBackground();
    }catch(Exception ex){

    }
}
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(responseFetch != null){
                progressDialog.dismiss();
                startCameraActivity(UserDetails.getUserName());
            }else{
                progressDialog.dismiss();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }

        }
    }

}