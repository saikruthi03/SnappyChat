package com.example.vsaik.snapchat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{


    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

    DatabaseReference myRef = mDatabase.getReference("users");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference;


    EditText mEmailField;
    EditText mPasswordField;
    ImageView profilePic;
    Button selectPic;
    EditText nickName;
    EditText aboutMe;
    EditText profession;
    EditText location;
    String email1="";
    String pwd="";
    String nickNm="";
    String pfs="";
    String aMe="";
    String lct = "";
    String imageUrl = "";

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    // [START declare_auth_listener]
    private FirebaseAuth.AuthStateListener mAuthListener;
    // [END declare_auth_listener]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        nickName = (EditText) findViewById(R.id.nameText);
        mEmailField = (EditText) findViewById(R.id.emailText);
        mPasswordField = (EditText) findViewById(R.id.passwordText);
        selectPic = (Button)findViewById(R.id.selectPic);
        findViewById(R.id.signUp).setOnClickListener(this);
        //findViewById(R.id.cancel_buttonSU).setOnClickListener(this);
        selectPic.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    private void createAccount(String email, String password) {
        nickNm = nickName.getText().toString();
        email = mEmailField.getText().toString();
        pwd = mPasswordField.getText().toString();



        Log.d("SignUp", "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("SignUp", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Authentication failed!!",
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            Intent create = new Intent();
                            setResult(Activity.RESULT_OK,create);
                                                       UserDetails user = new UserDetails(nickNm,email1,pfs," "," "," ",mAuth.getCurrentUser().getUid().toString(),"FriendsOnly"," ");
                            myRef.child(mAuth.getCurrentUser().getUid().toString()).setValue(user);
                            finish();
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

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.signUp) {
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if(i == R.id.selectPic){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"),900);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 900)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                if (data != null)
                {
                    try
                    {

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
                        byte[] databyte = baos.toByteArray();
                        Uri fileUri = data.getData();

                        String path = "FireMemes/" + UUID.randomUUID() + ".png";
                        // StorageReference storageReference = storage.getReference(path);
                        storageReference = storage.getReference();
                        StorageReference storageReference1 = storageReference.child(path);
                        imageUrl = path;
                        if(!(path.equals(""))) Picasso.with(getApplicationContext()).load(path).into(profilePic);
                        //StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("text", OverlayText.getText().toString()).build();
                        UploadTask uploadTask = storageReference1.putFile(fileUri);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity.this, "Upload Failed",Toast.LENGTH_LONG ).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Uri uri = taskSnapshot.getDownloadUrl();
                                DatabaseReference pic = myRef.child("users").child(mAuth.getCurrentUser().getUid().toString()).child("profilePicUrl");
                                pic.setValue(imageUrl.toString());
                                // imageUrl = uri.toString();

                                //else imageUrl.setVisibility(View.GONE);
                                //if(!imageUrl.equals("")) Picasso.with(SignUp.this).load(imageUrl).into(profilePic);

                                //long time= System.currentTimeMillis();
                                //Msg msg = new Msg(EmailPasswordActivity.curUser, imageURL, "", time+"");
                                //msg.id = EmailPasswordActivity.curUser +((int)(Math.random()*100000));
                                //myRef.child(EmailPasswordActivity.curUser).child(msg.id).setValue(msg);
                            }
                        });



                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                }
            } else if (resultCode == Activity.RESULT_CANCELED)
            {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    public void onBackPressed() {

    }

}
