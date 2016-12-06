package com.example.vsaik.snapchat;

/**
 * Created by jay on 12/6/16.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by jay on 12/4/16.
 */

public class CustomFriendViewAdapter extends ArrayAdapter<Friend> {

    Context context;

    public CustomFriendViewAdapter(Context context, int resourceId,
                                   List<Friend> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView image;
        TextView name;
        TextView level;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        CustomFriendViewAdapter.ViewHolder holder = null;
        Friend rowItem = getItem(position);

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.friend_individual, null);
            holder = new CustomFriendViewAdapter.ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.friend_dp);
            holder.name = (TextView) convertView.findViewById(R.id.friend_name);
            holder.level = (TextView) convertView.findViewById(R.id.friend_level);
            convertView.setTag(holder);
        } else
            holder = (CustomFriendViewAdapter.ViewHolder) convertView.getTag();

        //holder.image.setImageResource(rowItem.imag);
        holder.name.setText(rowItem.name);
        holder.level.setText(rowItem.level);
        final String level = rowItem.level;
        final String name = rowItem.name;
        holder.level.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"clicked "+name+" : "+level,Toast.LENGTH_SHORT).show();
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
}