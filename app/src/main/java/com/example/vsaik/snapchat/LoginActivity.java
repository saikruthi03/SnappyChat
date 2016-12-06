package com.example.vsaik.snapchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    ProgressDialog pd;
    private static final String TAG = "EmailPassword";
    static HashMap<String,UserDetails> userArrayList = new HashMap<>();
    public List<String> userId = new ArrayList<>();
    public boolean redirect= false;
    private TextView mStatusTextView;
    private TextView mDetailTextView;
    private EditText mEmailField;
    private EditText mPasswordField;
    public static String curUser = "NA";
    LoginButton fbLogin;
    ProgressDialog progressDialog;

    public static FirebaseAuth mAuth = null;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CallbackManager mCallbackManager;
    private GoogleApiClient mGoogleApiClient;
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = mDatabase.getReference("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Current User", "Current User Id:" + curUser);

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login);
        mEmailField = (EditText) findViewById(R.id.editText);
        mPasswordField = (EditText) findViewById(R.id.editText2);
        findViewById(R.id.loginbutton).setOnClickListener(this);
        findViewById(R.id.signupbutton).setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                   // updateUI(user);
                    curUser = user.getUid();
                } else {
                    //updateUI(user);
                }

            }
        };

        mCallbackManager = CallbackManager.Factory.create();
        fbLogin = (LoginButton) findViewById(R.id.fb_login_button);
        fbLogin.setReadPermissions("email", "public_profile");
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

            }
        });
        SignInButton googlesignInButton = (SignInButton)findViewById(R.id.google_signin_button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient= new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        googlesignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent=Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent,1004);
            }
        });


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
                    userArrayList.put(postSnapshot.getKey(),postSnapshot.getValue(UserDetails.class));
                    userId.add(postSnapshot.getKey());
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

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication Failed!!",
                                    Toast.LENGTH_SHORT).show();
                        }

                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,"Login Success",Toast.LENGTH_SHORT).show();
                            startCameraActivity(mAuth.getCurrentUser().getUid());
                        }
                    }
                });
    }




    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {

        if (user != null) {
            curUser = user.getUid();
            mEmailField.setText(user.getEmail());
        } else {

        }
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.signupbutton) {
            Intent create = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivityForResult(create, 200);
        } else if (i == R.id.loginbutton) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        showProgressDialog();
        if (requestCode == 101) {
            if (resultCode == RESULT_CANCELED) {

            }
        }
        if(requestCode==1004)
        {
            GoogleSignInResult result=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                updateUI(null);
            }
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),"Google Login Failed",Toast.LENGTH_SHORT).show();
                        }else {
                            final FirebaseUser mUser = mAuth.getCurrentUser();
                            for(String user:userId){
                                if(user.equals(mUser.getUid())){
                                    redirect = true;
                                    break;
                                }
                            }
                            myRef.child("users").child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.hasChild("email")) {
                                        if(redirect){
                                            progressDialog.dismiss();
                                            startCameraActivity(mUser.getUid());
                                        }else{
                                            DatabaseReference myRef1 = myRef.child("users").child(mUser.getUid());
                                            UserDetails user = new UserDetails(" ",mUser.getEmail()," "," ",""," "," ",mUser.getUid()," "," ");
                                            myRef.child(mUser.getUid()).setValue(user);
                                            userId.add(mUser.getUid());
                                            progressDialog.dismiss();
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }

                                       }else {
                                        if(redirect){
                                            startCameraActivity(mUser.getUid());
                                        }else{
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                        }

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
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }else{
                            final FirebaseUser fbUser = mAuth.getCurrentUser();
                            for(String user:userId){
                                if(user.equals(fbUser.getUid())){
                                    redirect = true;
                                }
                            }
                            myRef.child("USERS").child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.hasChild("emailid")) {
                                        Toast.makeText(LoginActivity.this, " Successfully Signed In ", Toast.LENGTH_SHORT).show();
                                       if(redirect) {
                                           startCameraActivity(fbUser.getUid());
                                       }else{
                                           DatabaseReference myref1 = myRef.child("users").child(fbUser.getUid());
                                           UserDetails user = new UserDetails(fbUser.getDisplayName(),fbUser.getEmail(),fbUser.getPhotoUrl().toString()," ",""," "," ",fbUser.getUid()," "," ");
                                           myRef.child(fbUser.getUid()).setValue(user);
                                           progressDialog.dismiss();
                                           Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                           startActivity(intent);
                                       }
                                    }else {
//                                        Intent intent = new Intent(EmailPasswordActivity.this, MainActivity.class);
//                                        startActivity(intent);
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

    private void startCameraActivity(String userId) {
        Intent passer = new Intent(this,MainScreen.class);
        passer.putExtra("UserId",userId);
        startActivity(passer);
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}


