package com.example.vsaik.snapchat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class IndividualTimeLineActivity extends Activity implements AdapterView.OnItemClickListener {

    private String myName;
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
        myName = UserDetails.getEmail();
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
        List<String> likes = new ArrayList<String>();
        likes.add("Jay");
        likes.add("Vivek");
        likes.add("Zu Zu");
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
                    List<JSONObject> comments = new ArrayList<JSONObject>();
                    JSONArray array = object.getJSONArray("comments");
                    int index = 0;
                    ts.userInfo.likes = likes;
                    ts.id = object.getString("s_id");

                    while(array != null && array.length() > 0 && index < array.length()){
                        comments.add(new JSONObject("{\"name\":\"dummy\",\"comments\":\"my first comment\"}"));

                        index ++;
                    }
                    ts.userInfo.comments = comments;


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
            final TimeLineObject rowItem = getItem(position);

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

            final String id = rowItem.id;
            String name = rowItem.userInfo.name;
            holder.time_line_image.setImageBitmap(rowItem.imageInfo.image);
            holder.date.setText(rowItem.userInfo.date);
            holder.profileName.setText(rowItem.userInfo.name);

                final List<JSONObject> list = rowItem.userInfo.comments;
                final List<String> likesList = rowItem.userInfo.likes;

            int commentsSize = 0;
            if(rowItem.userInfo != null && rowItem.userInfo.comments != null){
                commentsSize = rowItem.userInfo.comments.size();
            }
            int likesSize = 0;
            if(rowItem.userInfo != null && rowItem.userInfo.likes != null){
                likesSize = rowItem.userInfo.likes.size();
            }
            holder.likes.setText("Likes ("+likesSize+")");
            holder.likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(likesList.size() > 0) {
                        AlertDialog.Builder builderSingle = new AlertDialog.Builder(context);
                        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice);
                        arrayAdapter.addAll(likesList);
                        builderSingle.setAdapter(arrayAdapter, null);
                        builderSingle.show();
                    }
                }
            });

            holder.comments.setText("Comments ("+commentsSize+")");
            holder.comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(context);
                    final LinearLayout layout = new LinearLayout(context);
                    if(list.size() > 0) {
                        CommentsAdapter commentsAdapter = new CommentsAdapter(context, R.layout.comments_list, list);
                        adb.setAdapter(commentsAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                    }
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3);
                    final EditText text = new EditText(context);
                    text.setLayoutParams(params);

                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
                    Button addComment = new Button(context);
                    addComment.setText("Comment");
                    addComment.setLayoutParams(params2);
                    layout.addView(text);
                    layout.addView(addComment);

                    final AlertDialog d = adb.setView(layout).create();
                    d.show();
                    addComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            HashMap<String,String> hashMap = new HashMap<String, String>();
                            hashMap.put("URL",Constants.URL+"/add_comments");
                            hashMap.put("s_id",id);
                            hashMap.put("Method","POST");
                            hashMap.put("comments",text.getText().toString());
                            hashMap.put("username",myName);
                            PushContent pushContent = new PushContent(hashMap);
                            pushContent.execute();
                            d.dismiss();

                        }
                    });

                    // (That new View is just there to have something inside the dialog that can grow big enough to cover the whole screen.)
                   /* WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(d.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT - 500;
                    lp.height = WindowManager.LayoutParams.MATCH_PARENT - 1000;*/
                  //  d.getWindow().setAttributes(lp);
                }
            });
            return convertView;
        }
    }

    class PushContent extends
            AsyncTask<Void, Void, Void> {

        boolean err = false;
        HashMap<String, String> hashMap = null;

        public PushContent(HashMap hashMap) {
            this.hashMap = hashMap;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            PostData fecth = new PostData(hashMap);
            try {
                fecth.doInBackground();
            } catch (Exception e) {
                Log.e("RESPONSE- ERROR", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (err)
                reportError();
            else {
                onStart();
            }

        }
    }
    private void reportError(){
        Toast.makeText(this,"Error encountered",Toast.LENGTH_SHORT).show();
        onStart();
    }

    class CommentsAdapter extends  ArrayAdapter<JSONObject>{


        public CommentsAdapter(Context context, int resource, List<JSONObject> objects) {
            super(context, resource, objects);
        }

        private class ViewHolder {

            TextView name;
            TextView comment;

        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            JSONObject comment = getItem(position);
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.comments_list, null);
                holder = new CommentsAdapter.ViewHolder();

                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.comment = (TextView) convertView.findViewById(R.id.comment);
                convertView.setTag(holder);
            } else
                holder = (CommentsAdapter.ViewHolder) convertView.getTag();

            if(comment != null){
                try {
                    holder.name.setText(comment.getString("name"));
                    holder.comment.setText(comment.getString("comments"));
                }
                catch (Exception e){
                    Log.d("TAG","Exception in comments, loaded static data");
                    holder.name.setText("dummy name");
                    holder.comment.setText("dummy comment");
                }
            }
            return convertView;
        }
        @Override
        public int getViewTypeCount() {
            return getCount();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }
}
