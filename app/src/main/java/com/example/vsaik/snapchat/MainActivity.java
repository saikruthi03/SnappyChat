package com.example.vsaik.snapchat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener  {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ArrayList<UserDetails> userList = new ArrayList<>();
    static HashMap<String,UserDetails> usenames =  new HashMap<>();
    String currentUser;
    ArrayList<UserDetails> userL = new ArrayList<>();
    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    //FirebaseStorage storage = FirebaseStorage.getInstance();
    String path;
    private Context context ;
    DatabaseReference myRef = mDatabase.getReference("users");
    TextView nickName;

Button signOut ;

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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //spinner code
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        List<String> categories = new ArrayList<String>();
        categories.add("Select");
        categories.add("Settings");
        categories.add("Notifications");
        categories.add("SignOut");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        fetchData();
//spinner code end

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        nickName = (TextView)findViewById(R.id.nickName);

        Toast.makeText(this,"THIS IS USER PROFILE",Toast.LENGTH_LONG).show();
       // signOut =(Button) findViewById(R.id.signoutbutton);
      //  signOut.setOnClickListener(this);
        //if (mAuth.getCurrentUser()!=null){
           // if(!usenames.isEmpty())
           //  nickName.setText(FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser()));
        //}
        onStart();


    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
       // if (i == R.id.signoutbutton) {
        //    mAuth.signOut();
        //    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        //    startActivity(intent);
      //  }

    }

    @Override
    protected void onStart() {
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
                            nickName.setText(user.getNickname());
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


    @Override
    public void onBackPressed() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        if(item.equals("Settings")){
            Intent settings = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(settings);
        }
        if(item.equals("SignOut")){
            mAuth.signOut();
            Intent logout = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(logout);
        }
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }
}
