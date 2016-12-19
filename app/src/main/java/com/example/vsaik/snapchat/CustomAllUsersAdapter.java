package com.example.vsaik.snapchat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;

/**
 * Created by vsaik on 12/8/2016.
 */
public class CustomAllUsersAdapter extends ArrayAdapter<FriendList> {

    Context context;
    private String myName = UserDetails.getEmail();


    public CustomAllUsersAdapter(String myName,Context context, int resourceId,
                                   List<FriendList> items) {
        super(context, resourceId, items);
        Log.d("items",items.size()+"");
        this.myName = myName;
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView user;
        ImageView userdp;
        ImageButton friend;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        final FriendList rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            Log.d("inside if",rowItem.getName()+"");
            convertView = mInflater.inflate(R.layout.all_users_list, null);
            holder = new ViewHolder();
            holder.user = (TextView) convertView.findViewById(R.id.username);
            holder.userdp = (ImageView) convertView.findViewById(R.id.userdp);
            holder.friend=(ImageButton) convertView.findViewById(R.id.friend);

            convertView.setTag(holder);
            holder.user.setText(rowItem.getName());
            Log.d("name",rowItem.getName());

        } else {
           //String name = rowItem.name;
            holder = (ViewHolder) convertView.getTag();

        }
        final HashMap<String,String> myMap = new HashMap<String,String>();
        myMap.put("friend_username",rowItem.email);
        myMap.put("username",myName);
        holder.userdp.setImageResource(rowItem.status);
        holder.user.setText(rowItem.getName());
        if(rowItem.isFriend){
            holder.friend.setImageResource(R.drawable.tick);
        }
        else {
            holder.friend.setImageResource(R.drawable.add);
            holder.friend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new FriendOperation("send",myMap).execute();
                }
            });
        }
        holder.user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,IndividualTimeLineActivity.class);
                i.putExtra("profile",rowItem.email);
                context.startActivity(i);
            }
        });
        Log.d("name",rowItem.getName());
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


    class FriendOperation extends AsyncTask<Void,Void,Void> {

        private HashMap<String,String> hashMap;
        private String operation = "";
        boolean errFlag = false;
        String response = "";

        public FriendOperation(String operation,HashMap<String,String> hashMap){
            this.operation = operation;
            this.hashMap = hashMap;
        }
        @Override
        protected Void doInBackground(Void... voids) {


            try {
                if ("send".equalsIgnoreCase(operation)) {
                    hashMap.put("Method", "GET");
                    hashMap.put("URL", Constants.URL + "/request_friend");
                    GetData post = new GetData(hashMap);
                    response = post.doInBackground();
                }
            }
            catch(Exception e){
                errFlag = true;
                Log.e("ERROR","In performing friend operations"+e.getCause());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(errFlag)
                Toast.makeText(context,"Error in operation",Toast.LENGTH_SHORT);
            else
                Toast.makeText(context,"Success",Toast.LENGTH_SHORT);
        }
    }
}
