package com.example.vsaik.snapchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

public class TimeLineActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listTimeLine = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);

        listTimeLine = (ListView) findViewById(R.id.list_time_line);
        onStart();
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
}
