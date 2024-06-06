package com.loptr.kherod.uygdl.utlities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.loptr.kherod.uygdl.R;

public class NotificationUtil {
    Context context;

    public NotificationUtil(Context context) {
        this.context = context;
    }

    public  void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // create channel notification if the device is greater the or equal 26 (oreo) as it`s required
            // and make it priority high so the notification is popping up to the user
            createChannel();
        }
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_checked);
        Log.i("ab_do" , "showNotification");
        NotificationCompat.Builder buildNotification = new NotificationCompat.Builder(context , context.getString(R.string.my_notification_channel_id)); // builder to build the notification
        buildNotification.setContentTitle(context.getString(R.string.congra));
        buildNotification.setContentText(context.getString(R.string.adsRemoved));
        buildNotification.setSmallIcon(R.drawable.ic_email);
        buildNotification.setLargeIcon(largeIcon);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) buildNotification.setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        Notification notification = buildNotification.build();
        notificationManager.notify(1, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        Log.i("ab_do" , "createChannel");
        NotificationChannel notificationChannel = new NotificationChannel(context.getString(R.string.my_notification_channel_id), "Notifications", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
    }

}

