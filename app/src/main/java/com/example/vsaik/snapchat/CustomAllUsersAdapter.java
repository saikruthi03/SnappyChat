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

import java.util.HashMap;
import java.util.List;

/**
 * Created by vsaik on 12/8/2016.
 */
public class CustomAllUsersAdapter extends ArrayAdapter<Friend> {

    Context context;
    private String myName = UserDetails.getEmail();


    public CustomAllUsersAdapter(String myName,Context context, int resourceId,
                                   List<Friend> items) {
        super(context, resourceId, items);
        Log.d("items",items.size()+"");
        this.myName = myName;
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        TextView user;
        ImageView image;
        ImageButton friend;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Friend rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            Log.d("inside if",rowItem.getName()+"");
            convertView = mInflater.inflate(R.layout.all_users_list, null);
            holder = new ViewHolder();
            holder.user = (TextView) convertView.findViewById(R.id.username);
            holder.image = (ImageView) convertView.findViewById(R.id.userdp);
            holder.friend=(ImageButton) convertView.findViewById(R.id.friend);

            convertView.setTag(holder);
            holder.user.setText(rowItem.getName());
            Log.d("name",rowItem.getName());

           /* try{ if(!rowItem.getImage().isEmpty()){
                holder.image.setImageBitmap(ImageUtils.getBitmapFromBase64(rowItem.getImage()));
            }}catch(Exception ex){

            }*/

        } else {
            Log.d("inside else",rowItem.getName()+"");
           //String name = rowItem.name;
            holder = (ViewHolder) convertView.getTag();
            holder.user.setText(rowItem.getName());
            Log.d("name",rowItem.getName());

            /*try{ if(!rowItem.getImage().isEmpty()){
                holder.image.setImageBitmap(ImageUtils.getBitmapFromBase64(rowItem.getImage()));
            }}catch(Exception ex){

            }*/


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
