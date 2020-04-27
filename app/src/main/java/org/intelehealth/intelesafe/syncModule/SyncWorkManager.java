package org.intelehealth.intelesafe.syncModule;

import android.content.Context;
import android.support.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.crashlytics.android.Crashlytics;

import org.intelehealth.intelesafe.utilities.Logger;
import org.intelehealth.intelesafe.utilities.SessionManager;

public class SyncWorkManager extends Worker {

    private SessionManager sessionManager = null;
    private String TAG = SyncWorkManager.class.getSimpleName();

    public SyncWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        sessionManager = new SessionManager(context);
    }


    @NonNull
    @Override
    public Result doWork() {
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            Crashlytics.getInstance().core.logException(e);
            Logger.logE(TAG, "Exception in doWork method", e);
        }
        Logger.logD(TAG, "doWork");

        SyncUtils syncUtils = new SyncUtils();
        syncUtils.syncBackground();

        return Result.success();
    }
}

