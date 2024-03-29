package org.intelehealth.intelesafe.activities.homeActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.Objects;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.app.AppConstants;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(context));
        SharedPreferences.Editor sharedPrefEditor = prefs.edit();

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, HomeActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingI_10 = PendingIntent.getActivity(context, 1250,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pendingI_17 = PendingIntent.getActivity(context, 1260,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "Daily Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Daily Notification");
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
        NotificationCompat.Builder b = new NotificationCompat.Builder(context, "default");

        if (intent.getAction().equalsIgnoreCase(AppConstants.ACTION_ONE)) {
            b.setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_cloud_upload)
//                .setTicker("{Time to watch some cool stuff!}")
                    .setContentTitle("Daily Check-in: 10am")
                    .setContentText("It's time for daily check-in")
                    .setContentInfo("INFO")
                    .setContentIntent(pendingI_10);
        }
        else {

            b.setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_cloud_upload)
//                .setTicker("{Time to watch some cool stuff!}")
                    .setContentTitle("Daily Check-in: 17pm")
                    .setContentText("It's time for daily check-in")
                    .setContentInfo("INFO")
                    .setContentIntent(pendingI_17);
        }

        if (nm != null) {
           // int i = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
            nm.notify(1, b.build());
            Calendar nextNotifyTime = Calendar.getInstance();
            nextNotifyTime.add(Calendar.DATE, 1);


            sharedPrefEditor.putLong("nextNotifyTime", nextNotifyTime.getTimeInMillis());
            sharedPrefEditor.apply();
        }

    }
}
