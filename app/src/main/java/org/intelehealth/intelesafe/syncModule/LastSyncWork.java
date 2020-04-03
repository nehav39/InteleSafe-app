package org.intelehealth.intelesafe.syncModule;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.intelehealth.intelesafe.app.IntelehealthApplication;
import org.intelehealth.intelesafe.utilities.Logger;
import org.intelehealth.intelesafe.utilities.SessionManager;

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
        Logger.logD(TAG, "result job");

        Intent in = new Intent();
        in.setAction("lasysync");
        IntelehealthApplication.getAppContext().sendBroadcast(in);

        return Result.success();
    }
}