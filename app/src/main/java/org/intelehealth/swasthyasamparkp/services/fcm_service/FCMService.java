package org.intelehealth.swasthyasamparkp.services.fcm_service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.intelehealth.apprtc.ChatActivity;
import org.intelehealth.apprtc.CompleteActivity;
import org.intelehealth.swasthyasamparkp.activities.homeActivity.HomeActivity;
import org.intelehealth.swasthyasamparkp.fcm.util.NotificationUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Deepak
 */
public class FCMService extends FirebaseMessagingService {

    private static final String TAG = FCMService.class.getSimpleName();
    private static final String PACKAGE_NAME = "org.intelehealth.swasthyasamparkp";
    private static final String ACTION_NAME = PACKAGE_NAME + ".RTC_MESSAGING_EVENT";
    private static final String RECEIVER_CLASS = PACKAGE_NAME + ".utilities.RTCMessageReceiver";

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN", s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null)
            return;

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());

            Log.d(TAG, "Notification Message Data: " + remoteMessage.getData());
            //  {nurseId=28cea4ab-3188-434a-82f0-055133090a38, doctorName=doctor1, roomId=b60263f2-5716-4047-aaf5-7c13199b7f0c}

            if (remoteMessage.getData().containsKey("actionType")) {
                if (remoteMessage.getData().get("actionType").equals("VIDEO_CALL")) {
                    Log.d(TAG, "actionType : VIDEO_CALL");
                    Intent in = new Intent(this, CompleteActivity.class);
                    String roomId = remoteMessage.getData().get("roomId");
                    String doctorName = remoteMessage.getData().get("doctorName");
                    String nurseId = remoteMessage.getData().containsKey("nurseId") ? remoteMessage.getData().get("nurseId") : "";
                    in.putExtra("roomId", roomId);
                    in.putExtra("isInComingRequest", true);
                    in.putExtra("doctorname", doctorName);
                    in.putExtra("nurseId", nurseId);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    int callState = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getCallState();
                    if (callState == TelephonyManager.CALL_STATE_IDLE) {
                        startActivity(in);
                    } else {
                        handleNotification(remoteMessage, null);
                    }

                } else if (remoteMessage.getData().get("actionType").equals("TEXT_CHAT")) {
                    try {
                        Log.d(TAG, "actionType : TEXT_CHAT");
                        String fromUUId = remoteMessage.getData().get("toUser");
                        String toUUId = remoteMessage.getData().get("fromUser");
                        String patientUUid = remoteMessage.getData().get("patientId");
                        String visitUUID = remoteMessage.getData().get("visitId");
                        String patientName = remoteMessage.getData().get("patientName");
                        JSONObject connectionInfoObject = new JSONObject();
                        connectionInfoObject.put("fromUUID", fromUUId);
                        connectionInfoObject.put("toUUID", toUUId);
                        connectionInfoObject.put("patientUUID", patientUUid);


                        Intent chatIntent = new Intent(this, ChatActivity.class);
                        chatIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        chatIntent.putExtra("patientName", patientName);
                        chatIntent.putExtra("visitUuid", visitUUID);
                        chatIntent.putExtra("patientUuid", patientUUid);
                        chatIntent.putExtra("fromUuid", fromUUId);
                        chatIntent.putExtra("toUuid", toUUId);

                        handleNotification(remoteMessage, chatIntent);


                        Intent intent = new Intent(ACTION_NAME);
                        intent.putExtra("visit_uuid", visitUUID);
                        intent.putExtra("connection_info", connectionInfoObject.toString());
                        intent.setComponent(new ComponentName(PACKAGE_NAME, RECEIVER_CLASS));
                        getApplicationContext().sendBroadcast(intent);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            } else {
                handleNotification(remoteMessage, null);
            }
        }
    }

    private void handleNotification(RemoteMessage remoteMessage, Intent intent) {
        // If the app is in background, firebase itself handles the notification
        // app is in foreground, broadcast the push message
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            if (intent == null)
                intent = new Intent(this, HomeActivity.class);
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
