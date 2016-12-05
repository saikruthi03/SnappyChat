package com.example.vsaik.snapchat;


import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
/**
 * Created by vsaik on 12/4/2016.
 */
public class ContactCustomAdapter extends BaseAdapter {
    private List<ContactModel> list;
    Context context;

    ContactCustomAdapter (List<ContactModel> list, Context context){
        this.list = list;
        this.context = context;
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }
    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return list.get(arg0);
    }
    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = convertView;
        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.alluser_row, null);
        }
        TextView contactName = (TextView)v.findViewById(R.id.tvname);
        TextView contactPhone = (TextView)v.findViewById(R.id.tvphone);


        ContactModel msg = list.get(position);
        contactName.setText(msg.getName());
        contactPhone.setText(msg.getPhoneNo());

        return v;
    }

}
