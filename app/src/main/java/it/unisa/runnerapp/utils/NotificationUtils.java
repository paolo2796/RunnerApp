package it.unisa.runnerapp.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;

import testapp.com.runnerapp.R;

public class NotificationUtils
{
    public static void sendNotification(Context ctx,String title,String content,int iconId)
    {
        Notification.Builder builder=new Notification.Builder(ctx);
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setSmallIcon(iconId);

        builder.setColor(ctx.getResources().getColor(R.color.colorAccent));
        NotificationManager notificationManager=(NotificationManager)ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());
    }
}
