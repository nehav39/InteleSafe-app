package org.intelehealth.intelesafe.syncModule;

import android.content.Context;
import androidx.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

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
        Logger.logD(TAG, "result job");

        SyncUtils syncUtils = new SyncUtils();
        syncUtils.syncBackground();

        return Result.success();
    }
}

