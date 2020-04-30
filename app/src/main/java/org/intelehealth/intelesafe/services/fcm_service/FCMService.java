package org.intelehealth.intelesafe.services.fcm_service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.intelehealth.intelesafe.activities.homeActivity.HomeActivity;
import org.intelehealth.intelesafe.fcm.util.NotificationUtils;

/**
 * Created by Deepak
 */
public class FCMService extends FirebaseMessagingService {

    private static final String TAG = FCMService.class.getSimpleName();

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage);
        }
    }

    private void handleNotification(RemoteMessage remoteMessage) {
        // If the app is in background, firebase itself handles the notification
        // app is in foreground, broadcast the push message
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            Intent intent = new Intent(this, HomeActivity.class);
            showNotificationMessage(getApplicationContext(), remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(), "", intent);
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        NotificationUtils notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }
}
