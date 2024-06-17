package com.android.mauro_castillo_d424_capstone.UI;

import static android.content.Context.NOTIFICATION_SERVICE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.android.mauro_castillo_d424_capstone.R;

import java.util.Objects;

public class MyReceiver extends BroadcastReceiver {

    private static int notificationId;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);

        String notificationType = intent.getStringExtra("notification_type");
        String vacationName = intent.getStringExtra("vacationName");

        String channel_id = "test";
        switch (Objects.requireNonNull(notificationType)) {
            case "vacation_start":
                createNotificationChannel(context, channel_id);
                Notification builder = new NotificationCompat.Builder(context, channel_id)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Vacation Start Notification")
                        .setContentText(vacationName + " will begin today")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
                notificationManager.notify(notificationId++, builder);
                Toast.makeText(context, vacationName + " will begin today", Toast.LENGTH_LONG).show();
                break;
            case "vacation_end":
                createNotificationChannel(context, channel_id);
                Notification builder1 = new NotificationCompat.Builder(context, channel_id)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Vacation End Notification")
                        .setContentText(vacationName + " will end today")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
                Toast.makeText(context, vacationName + " will end today", Toast.LENGTH_LONG).show();
                notificationManager.notify(notificationId++, builder1);
                break;
            case "excursion_start":
                String excursionName = intent.getStringExtra("excursionName");
                createNotificationChannel(context, channel_id);
                Notification builder2 = new NotificationCompat.Builder(context, channel_id)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Excursion Start Notification")
                        .setContentText(excursionName + " will begin today")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT).build();
                Toast.makeText(context, excursionName + " will begin today", Toast.LENGTH_LONG).show();
                notificationManager.notify(notificationId++, builder2);
                break;
        }
    }

    private void createNotificationChannel(Context context, String CHANNEL_ID) {
        CharSequence name = "mychannelname";
        String description = "mychanneldescription";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, name, importance);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel.setDescription(description);
        }
        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
    }
}
