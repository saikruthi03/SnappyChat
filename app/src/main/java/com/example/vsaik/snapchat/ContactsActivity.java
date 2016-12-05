package com.example.vsaik.snapchat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import static android.Manifest.permission.READ_CONTACTS;

public class ContactsActivity extends AppCompatActivity {
    private ListView listView;
    private List<ContactModel> list = new ArrayList<ContactModel>();
    int count=0;                                 // count the number of contacts
    private static final int REQUEST_READ_CONTACTS = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);


        listView = (ListView) findViewById(R.id.list);

        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            getContacts();
        }else{
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                    }

    }

public void getContacts(){
    Cursor phones = getContentResolver().query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
            null, null);
    while (phones.moveToNext()) {
        count++;

        String name = phones
                .getString(phones
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        String phoneNumber = phones
                .getString(phones
                        .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        ContactModel objContact = new ContactModel();       // creating an instance for contactmodel
        objContact.setName(name);
        objContact.setPhoneNo(phoneNumber);
        list.add(objContact);                                               // adding contacts into the list
    }
    phones.close();
    System.out.println(count);                                               // total numbers of contacts

    listView.setAdapter(new ContactCustomAdapter(list, this));   // setting listview with an adapter
}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getContacts();
            }
        }

    }

}


