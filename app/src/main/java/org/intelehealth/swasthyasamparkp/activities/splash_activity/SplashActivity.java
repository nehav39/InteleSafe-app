package org.intelehealth.swasthyasamparkp.activities.splash_activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.intelehealth.swasthyasamparkp.R;
import org.intelehealth.swasthyasamparkp.activities.chooseLanguageActivity.ChooseLanguageActivity;
import org.intelehealth.swasthyasamparkp.activities.homeActivity.HomeActivity;
import org.intelehealth.swasthyasamparkp.activities.loginActivity.LoginActivity;
import org.intelehealth.swasthyasamparkp.dataMigration.SmoothUpgrade;
import org.intelehealth.swasthyasamparkp.fcm.util.FCMUtils;
import org.intelehealth.swasthyasamparkp.services.fcm_service.TokenRefreshUtils;
import org.intelehealth.swasthyasamparkp.utilities.Logger;
import org.intelehealth.swasthyasamparkp.utilities.SessionManager;

import java.util.List;
import java.util.Locale;


public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_TIMER = 6 * 1000; // 7 sec
    SessionManager sessionManager = null;
    ProgressDialog TempDialog;
    int i = 5;

    FCMUtils fcmUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_activity);
//        Getting App language through the session manager
        sessionManager = new SessionManager(SplashActivity.this);

        String appLanguage = sessionManager.getAppLanguage();

        if (!appLanguage.equalsIgnoreCase("")) {
            Locale locale = new Locale(appLanguage);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
        fcmUtils = new FCMUtils(SplashActivity.this);
        fcmUtils.fcm();
        // refresh the fcm token
        TokenRefreshUtils.refreshToken(this);
        checkPerm();
    }

    private void checkPerm() {
        PermissionListener permissionlistener = new PermissionListener() {

            @Override
            public void onPermissionGranted() {
//                Toast.makeText(SplashActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
//                Timer t = new Timer();
//                t.schedule(new splash(), 2000);

                //Hiding Data migration progress bar
//                TempDialog = new ProgressDialog(SplashActivity.this);
//                TempDialog.setMessage("Data migrating...");
//                TempDialog.setCancelable(false);
//                TempDialog.setProgress(i);
//                TempDialog.show();

                if (sessionManager.isMigration()) {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            nextActivity();
                        }
                    }, SPLASH_TIMER);

                } else {
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            SmoothUpgrade smoothUpgrade = new SmoothUpgrade(SplashActivity.this);
                            boolean smoothupgrade = smoothUpgrade.checkingDatabase();
                            if (smoothupgrade) {
//                                TempDialog.dismiss();
                                nextActivity();
                            } else {
//                                TempDialog.dismiss();
                                nextActivity();
                            }

                        }
                    }, SPLASH_TIMER);
                }

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(SplashActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };
        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
//                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void nextActivity() {
        boolean setup = sessionManager.isSetupComplete();

        String LOG_TAG = "SplashActivity";
        Logger.logD(LOG_TAG, String.valueOf(setup));
        if (setup) {

            Logger.logD(LOG_TAG, "Starting login");
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();

        } else {

            if (sessionManager.isFirstTimeLaunch()) {
                Logger.logD(LOG_TAG, "Starting setup");
                Intent intent = new Intent(this, ChooseLanguageActivity.class);
                startActivity(intent);
                finish();
            } else {

                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("from", "splash");
                intent.putExtra("username", "");
                intent.putExtra("password", "");
                startActivity(intent);
                finish();

            }
        }
    }

    @Override
    protected void onDestroy() {
//        TempDialog.dismiss();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}