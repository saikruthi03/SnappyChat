package com.example.vsaik.snapchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class IndividualTimeLineActivity extends Activity implements AdapterView.OnItemClickListener {

    private ListView listTimeLine = null;
    private Context context= null;
    private String name = UserDetails.getEmail();
    List<TimeLineObject> list_time = null;
    JSONArray responseFetch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_time_line);
        Intent current = getIntent();
        list_time = new ArrayList<TimeLineObject>();

        if(current != null){
            name = current.getStringExtra("profile");
        }

        listTimeLine = (ListView) findViewById(R.id.listMyTimeLine);
        context = this;
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
        populateTimeLine(name);
    }

    private void populateTimeLine(String name) {
        HashMap<String,String> hashMap = new HashMap<String,String>();
        hashMap.put("URL",Constants.URL+"/list_timeline");
        hashMap.put("Method","GET");
        hashMap.put("username",name);
        GetTimeLineData timeData = new GetTimeLineData(hashMap);
        timeData.execute();
    }

    private void showImages(){
        listTimeLine.setAdapter(null);
        if(list_time != null && list_time.size() > 0) {
            CustomTimeLineViewAdapter adapter = new CustomTimeLineViewAdapter(this, R.layout.time_line_object, list_time);
            listTimeLine.setAdapter(adapter);
            listTimeLine.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    class GetTimeLineData extends AsyncTask<String,Void,Void> {

      private HashMap<String,String> hashMap;

        public GetTimeLineData(HashMap<String,String> hashMap){
          this.hashMap = hashMap;
        }

        @Override
        protected Void doInBackground(String... string) {


            GetData fecth = new GetData(hashMap);
            try {
                String res = fecth.doInBackground();

                responseFetch = new JSONArray(res);
                Log.e("RESPONSE",responseFetch.toString());
            }
            catch(Exception e){
                Log.e("RESPONSE- ERROR",e.toString());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void hashMap) {
            super.onPostExecute(hashMap);
            updateTimeLine();
        }

    }

    private void updateTimeLine() {
        list_time = new ArrayList<TimeLineObject>();
        if (responseFetch != null) {
            int size = responseFetch.length();
            for (int i = 0; i < size; i++) {
                TimeLineObject ts = new TimeLineObject();
                try {
                    JSONObject object = responseFetch.getJSONObject(i);
                    ts.userInfo.name = name;
                    ts.userInfo.date = object.getString("timestamp");
                    String picture = object.getString("pictures");
                    ts.imageInfo.image = ImageUtils.getBitmapFromBase64WithRotation(picture,-90);

                } catch (Exception e) {
                    Log.e("TIMELINE", "Error in json parse ");
                }
                list_time.add(ts);

            }
        }
        showImages();
    }

    class CustomTimeLineViewAdapter extends ArrayAdapter<TimeLineObject> {

        Context context;

        public CustomTimeLineViewAdapter(Context context, int resourceId,
                                         List<TimeLineObject> items) {
            super(context, resourceId, items);
            this.context = context;
        }

        private class ViewHolder {
            TextView likes;
            TextView profileName;
            TextView status;
            TextView date;
            TextView comments;
            ImageView time_line_image;
        }

        @Override
        public int getViewTypeCount() {
            return getCount();
        }

        @Override
        public int getItemViewType(int position) {

            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            TimeLineObject rowItem = getItem(position);

            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.time_line_object, null);
                holder = new ViewHolder();
                holder.profileName = (TextView) convertView.findViewById(R.id.profileName);
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.time_line_image = (ImageView) convertView.findViewById(R.id.time_line_image);
                holder.likes = (TextView) convertView.findViewById(R.id.likes);
                holder.comments = (TextView) convertView.findViewById(R.id.comments);
                convertView.setTag(holder);
            } else
                holder = (ViewHolder) convertView.getTag();

            holder.time_line_image.setImageBitmap(rowItem.imageInfo.image);
            holder.date.setText(rowItem.userInfo.date);
            holder.profileName.setText(rowItem.userInfo.name);
            holder.likes.setText("0 Likes");
            holder.comments.setText("0 Comments");
            return convertView;
        }
    }

}
