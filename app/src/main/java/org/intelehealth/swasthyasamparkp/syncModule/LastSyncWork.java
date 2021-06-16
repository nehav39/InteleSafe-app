package org.intelehealth.swasthyasamparkp.syncModule;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;


import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.intelehealth.swasthyasamparkp.app.IntelehealthApplication;
import org.intelehealth.swasthyasamparkp.utilities.Logger;
import org.intelehealth.swasthyasamparkp.utilities.SessionManager;

public class LastSyncWork extends Worker {

    private SessionManager sessionManager = null;
    private String TAG = VisitSummaryWork.class.getSimpleName();

    public LastSyncWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        sessionManager = new SessionManager(context);
    }


    @NonNull
    @Override
    public Result doWork() {

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Logger.logE(TAG, "Exception in doWork method", e);
        }
        Logger.logD(TAG, "doWork");

        Intent in = new Intent();
        in.setAction("lasysync");
        IntelehealthApplication.getAppContext().sendBroadcast(in);

        return Result.success();
    }
}