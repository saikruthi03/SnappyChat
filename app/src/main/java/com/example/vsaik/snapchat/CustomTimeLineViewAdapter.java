package com.example.vsaik.snapchat;

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
 * Created by jay on 12/5/16.
 */

public class CustomTimeLineViewAdapter extends ArrayAdapter<TimeLineObject> {

    Context context;

    public CustomTimeLineViewAdapter(Context context, int resourceId,
                                     List<TimeLineObject> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    private class ViewHolder {
        ImageView profilePic;
        TextView profileName;
        TextView status;
        TextView date;
        TextView caption;
        ImageView time_line_image;
    }

    @Override
    public int getViewTypeCount() {
        Toast.makeText(context,getCount()+"",Toast.LENGTH_SHORT).show();
        int i =getCount();
        return i;
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
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.profileName = (TextView) convertView.findViewById(R.id.profileName);
            holder.profilePic = (ImageView) convertView.findViewById(R.id.profilePic);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.caption = (TextView) convertView.findViewById(R.id.caption);
            holder.status = (TextView) convertView.findViewById(R.id.status);
            holder.time_line_image = (ImageView) convertView.findViewById(R.id.time_line_image);
            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();
        if(rowItem.hasStatus()) {
            holder.status.setText(rowItem.statusInfo.status);

        }
        else{
            holder.time_line_image.setImageResource(rowItem.imageInfo.image);
            holder.caption.setText(rowItem.imageInfo.caption);
        }
        holder.date.setText(rowItem.userInfo.date);
        holder.profileName.setText(rowItem.userInfo.name);
        holder.profilePic.setImageResource(rowItem.userInfo.display_pic);

        return convertView;
    }
}

