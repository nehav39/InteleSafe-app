package org.intelehealth.swasthyasamparkp.syncModule;

import android.os.Handler;

import androidx.work.WorkManager;

import org.intelehealth.swasthyasamparkp.app.AppConstants;
import org.intelehealth.swasthyasamparkp.app.IntelehealthApplication;
import org.intelehealth.swasthyasamparkp.database.dao.SyncDAO;
import org.intelehealth.swasthyasamparkp.utilities.Logger;
import org.intelehealth.swasthyasamparkp.utilities.NotificationUtils;

public class SyncUtils {


    private static final String TAG = SyncUtils.class.getSimpleName();

    public void syncBackground() {
        SyncDAO syncDAO = new SyncDAO();
      //  ImagesPushDAO imagesPushDAO = new ImagesPushDAO();

        syncDAO.pushDataApi();
        syncDAO.pullData_Background(IntelehealthApplication.getAppContext()); //only this new function duplicate

       /* imagesPushDAO.patientProfileImagesPush();
        imagesPushDAO.obsImagesPush();
        imagesPushDAO.deleteObsImage();
*/
        NotificationUtils notificationUtils = new NotificationUtils();
        notificationUtils.clearAllNotifications(IntelehealthApplication.getAppContext());

        //Background Sync Fixes : Chaining of request in place of running background service
        WorkManager.getInstance()
                .beginWith(AppConstants.VISIT_SUMMARY_WORK_REQUEST)
                .then(AppConstants.LAST_SYNC_WORK_REQUEST)
                .enqueue();

        /*Intent intent = new Intent(IntelehealthApplication.getAppContext(), UpdateDownloadPrescriptionService.class);
        IntelehealthApplication.getAppContext().startService(intent);*/

    }

    public boolean syncForeground(String fromActivity) {
        boolean isSynced = false;
        SyncDAO syncDAO = new SyncDAO();
       // ImagesPushDAO imagesPushDAO = new ImagesPushDAO();
        Logger.logD(TAG, "Push Started");
        isSynced = syncDAO.pushDataApi();
        Logger.logD(TAG, "Push ended");


//        need to add delay for pulling the obs correctly
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Logger.logD(TAG, "Pull Started");
                syncDAO.pullData(IntelehealthApplication.getAppContext(), fromActivity);
                Logger.logD(TAG, "Pull ended");
            }
        }, 3000);

      /*  imagesPushDAO.patientProfileImagesPush();
        imagesPushDAO.obsImagesPush();
        imagesPushDAO.deleteObsImage();*/

        WorkManager.getInstance()
                .beginWith(AppConstants.VISIT_SUMMARY_WORK_REQUEST)
                .then(AppConstants.LAST_SYNC_WORK_REQUEST)
                .enqueue();
        /*Intent intent = new Intent(IntelehealthApplication.getAppContext(), UpdateDownloadPrescriptionService.class);
        IntelehealthApplication.getAppContext().startService(intent);*/

        return isSynced;
    }
}
