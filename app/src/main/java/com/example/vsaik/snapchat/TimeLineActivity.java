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

        listTimeLine = (ListView) findViewById(R.id.list_time_line);
        context = this;
        View contentView = (View)findViewById(R.id.activity_time_line);
        Intent current = getIntent();
        name = current.getStringExtra("name");
        contentView.setOnTouchListener(new OnSwipeTouchListener(context) {
            @Override
            public void onSwipeRight() {
                Intent main = new Intent(TimeLineActivity.this,MainScreen.class);
                startActivity(main);
            }
        });
        onStart();

       /* Spinner spinner = (Spinner) findViewById(R.id.planets_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setSelection(0);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        TextView txtView = (TextView) findViewById(R.id.txtLink);
        txtView.setText(Html.fromHtml("<a href='#'>"+UserDetails.getNickname().toUpperCase()+"</a>"));
*/
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

        List<TimeLineObject> list_time = new ArrayList<TimeLineObject>();

        TimeLineObject ts = new TimeLineObject();
        ts.userInfo.display_pic = R.drawable.click;
        ts.userInfo.name = "Jay";
        ts.statusInfo.status = "It's no late tonight!";
        ts.userInfo.date = "3:03 AM";

        TimeLineObject ti = new TimeLineObject();
        ti.userInfo.display_pic = R.drawable.click;
        ti.userInfo.name = "Jay";
        ti.imageInfo.caption = "Go Spartans!";
        ti.imageInfo.image = R.drawable.spartan;
        ti.userInfo.date = "3:53 AM";

        list_time.add(ts);
        list_time.add(ti);


/*
        final RelativeLayout chatClick = (RelativeLayout) findViewById(R.id.time_line_object);
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_time_image);*/

        CustomTimeLineViewAdapter adapter = new CustomTimeLineViewAdapter(this, R.layout.time_line_object, list_time);
        listTimeLine.setAdapter(adapter);


        listTimeLine.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.d("PAUSE","Pausing");
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
     if(parent.getItemAtPosition(pos).toString().equals("Search")){
         Intent mainScreen = new Intent(TimeLineActivity.this,SearchActivity.class);
         startActivity(mainScreen);
    }else if(parent.getItemAtPosition(pos).toString().equals("Friends")){
         Intent mainScreen = new Intent(TimeLineActivity.this,FriendActivity.class);
         startActivity(mainScreen);
    }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
