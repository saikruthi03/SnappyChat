package com.example.vsaik.snapchat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by vsaik on 12/6/2016.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
         //   Intent serviceIntent = new Intent(context, GetMessagesService.class);
        //    context.startService(serviceIntent);
        //}
        Log.d(BootReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        context.startService(new Intent(context, GetMessagesService.class));;
    }
}

