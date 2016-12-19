package com.example.vsaik.snapchat;

/**
 * Created by jay on 12/6/16.
 */

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

import java.util.HashMap;
import java.util.List;

/**
 * Created by jay on 12/4/16.
 */

public class CustomFriendViewAdapter extends ArrayAdapter<Friend> {

    Context context;
    private String showChat= "";
    private String myName = UserDetails.getUserName();
    public CustomFriendViewAdapter(Context context, int resourceId,
                                   List<Friend> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    public CustomFriendViewAdapter(String myName,Context context, int resourceId,
                                   List<Friend> items,String showChat) {
        super(context, resourceId, items);
        this.myName = myName;
        this.context = context;
        this.showChat = showChat;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView image;
        TextView name;
        ImageButton level;
        ImageButton chat;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Friend rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.friend_individual, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.friend_dp);
            holder.name = (TextView) convertView.findViewById(R.id.friend_name);
            holder.level = (ImageButton) convertView.findViewById(R.id.status);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.name.setText(rowItem.name);
        final String name = rowItem.name;

        //final String level = rowItem.level;

        final HashMap<String,String> myMap = new HashMap<String,String>();
        myMap.put("friend_username",name);
        myMap.put("username",myName);

        if("search".equalsIgnoreCase(showChat)){
            holder.image.setBackgroundResource(rowItem.status);
            holder.name.setText(rowItem.name);
            holder.chat = (ImageButton) convertView.findViewById(R.id.showChat);
            holder.chat.setBackgroundResource(R.drawable.add);
            holder.level.setBackgroundResource(0);

            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new FriendOperation("sendReq",myMap).execute();
                }
            });
            return convertView;
        }

        holder.chat = (ImageButton) convertView.findViewById(R.id.showChat);
        holder.chat.setBackgroundResource(0);
        if("friendVanilla".equalsIgnoreCase(showChat)) {

            holder.chat = (ImageButton) convertView.findViewById(R.id.showChat);
            holder.chat.setBackgroundResource(R.drawable.chats);
            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("CHAT","Clicked chat");
                    Intent i = new Intent(context,StartChatActivity.class);
                    i.putExtra("friend",name);
                    context.startActivity(i);
                }
            });
            holder.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context,TimeLineActivity.class);
                    i.putExtra("friend",name);
                    context.startActivity(i);
                }
            });


        }
        else if("friendRequests".equalsIgnoreCase(showChat)) {

            holder.chat = (ImageButton) convertView.findViewById(R.id.showChat);
            holder.chat.setBackgroundResource(R.drawable.tick);
            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("FRIEND WAITING","username "+myName + " : "+"friend_username " +name);

                    new FriendOperation("accept",myMap).execute();
                }
            });


        }


        //holder.chat.

        holder.image.setBackgroundResource(rowItem.status);


        holder.level.setBackgroundResource(android.R.drawable.ic_menu_delete);

        holder.level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FriendOperation("cancel",myMap).execute();
            }
        });
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
                if ("accept".equalsIgnoreCase(operation)) {
                    hashMap.put("Method","GET");
                    hashMap.put("URL", Constants.URL + "/accept_friend");
                    GetData post = new GetData(hashMap);
                    response = post.doInBackground();
                } else if ("cancel".equalsIgnoreCase(operation)) {
                    hashMap.put("URL", Constants.URL + "/delete_friend");
                    GetData post = new GetData(hashMap);
                    response = post.doInBackground();
                }else if ("sendReq".equalsIgnoreCase(operation)) {
                    hashMap.put("Method","POST");
                    hashMap.put("URL", Constants.URL + "/request_friend");
                    PostData post = new PostData(hashMap);
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