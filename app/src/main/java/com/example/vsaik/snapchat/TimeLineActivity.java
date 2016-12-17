package com.example.vsaik.snapchat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TimeLineActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

    private String name = "";
    private ListView listTimeLine = null;
    private Context context= null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);
        name = UserDetails.getEmail();
        listTimeLine = (ListView) findViewById(R.id.list_time_line);
        context = this;
        View contentView = (View)findViewById(R.id.activity_time_line);

        TextView myTimeLine = (TextView) findViewById(R.id.myTimeLine);
        myTimeLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(TimeLineActivity.this,IndividualTimeLineActivity.class);
                i.putExtra("profile",name);
                startActivity(i);
            }
        });

        contentView.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeRight() {
                Intent main = new Intent(TimeLineActivity.this,MainScreen.class);
                startActivity(main);
            }
        });
        onStart();

        Spinner spinner = (Spinner) findViewById(R.id.planets_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setSelection(0);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        onResume();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDummyData();
    }

    private void initDummyData(){






    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("PAUSE","Pausing");
    }


    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if (parent.getItemAtPosition(pos).toString().equals("Search")) {
            Intent mainScreen = new Intent(TimeLineActivity.this, SearchActivity.class);
            startActivity(mainScreen);
        } else if (parent.getItemAtPosition(pos).toString().equals("Friends")) {
            Intent mainScreen = new Intent(TimeLineActivity.this, FriendActivity.class);
            startActivity(mainScreen);
        } else if (parent.getItemAtPosition(pos).toString().equals("ALL")) {
            Intent mainScreen = new Intent(TimeLineActivity.this, ListAllFriendsActivity.class);
            startActivity(mainScreen);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
