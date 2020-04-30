package org.intelehealth.intelesafe.fcm.util;

import android.content.Context;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class FCMUtils {

    private Context context;

    public FCMUtils(Context context) {
        this.context = context;
    }

    public void fcm() {
        // retrieve_current_token
        FirebaseInstanceId.getInstance().getInstanceId()
            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Log.w("", "getInstanceId failed", task.getException());
                        return;
                    }

                    // TODO: TO check the token
                    // Get new Instance ID token
                    String token = task.getResult().getToken();
//                    Log.d("", token);
//                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
    }

    /**
     * Just to clear previous notifications
     */
    public void clearNotification() {
        // register GCM registration complete receiver
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(context);
    }
}
