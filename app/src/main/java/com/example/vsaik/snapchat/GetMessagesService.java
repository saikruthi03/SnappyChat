package com.example.vsaik.snapchat;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by vsaik on 12/6/2016.
 */
public class GetMessagesService extends Service {

        private Looper mServiceLooper;
        private ServiceHandler mServiceHandler;

        // Handler that receives messages from the thread
        private final class ServiceHandler extends Handler {
            public ServiceHandler(Looper looper) {
                super(looper);
            }
            @Override
            public void handleMessage(Message msg) {
                notification();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    // Restore interrupt status.
                    Thread.currentThread().interrupt();
                }
                // Stop the service using the startId, so that we don't stop
                // the service in the middle of handling another job
                stopSelf(msg.arg1);
            }
        }

        @Override
        public void onCreate() {
            HandlerThread thread = new HandlerThread("ServiceStartArguments",
                    Process.THREAD_PRIORITY_BACKGROUND);
            thread.start();

            // Get the HandlerThread's Looper and use it for our Handler
            mServiceLooper = thread.getLooper();
            mServiceHandler = new ServiceHandler(mServiceLooper);
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;
            mServiceHandler.sendMessage(msg);

            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
            // We don't provide binding, so return null
            return null;
        }

        @Override
        public void onDestroy() {
            Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        }
        public void notification(){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.notify)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");
        Intent resultIntent = new Intent(getApplicationContext(), ChatActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(ChatActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(123, mBuilder.build());
    }

}


