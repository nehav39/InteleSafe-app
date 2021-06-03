package org.intelehealth.intelesafe.activities.splash_activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;


import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;
import java.util.Locale;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.activities.chooseLanguageActivity.ChooseLanguageActivity;
import org.intelehealth.intelesafe.activities.homeActivity.HomeActivity;
import org.intelehealth.intelesafe.dataMigration.SmoothUpgrade;
import org.intelehealth.intelesafe.fcm.util.FCMUtils;
import org.intelehealth.intelesafe.services.fcm_service.TokenRefreshUtils;
import org.intelehealth.intelesafe.utilities.Logger;
import org.intelehealth.intelesafe.utilities.SessionManager;

import org.intelehealth.intelesafe.activities.loginActivity.LoginActivity;


public class SplashActivity extends AppCompatActivity {
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
                    }, 2000);

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
                    }, 2000);
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
                        Manifest.permission.GET_ACCOUNTS,
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
