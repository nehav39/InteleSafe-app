package org.intelehealth.intelesafe.activities.homeActivity;

import android.Manifest;
import android.accounts.Account;
//import android.accounts.AccountManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.InetAddresses;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.provider.ContactsContract;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.WorkManager;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import org.intelehealth.intelesafe.BuildConfig;
import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.activities.chooseLanguageActivity.ChooseLanguageActivity;
import org.intelehealth.intelesafe.activities.introActivity.IntroActivity;
import org.intelehealth.intelesafe.activities.loginActivity.LoginActivity;
import org.intelehealth.intelesafe.activities.physcialExamActivity.PhysicalExamActivity;
import org.intelehealth.intelesafe.activities.settingsActivity.SettingsActivity;
import org.intelehealth.intelesafe.activities.visitSummaryActivity.VisitSummaryActivity;
import org.intelehealth.intelesafe.app.AppConstants;
import org.intelehealth.intelesafe.database.InteleHealthDatabaseHelper;
import org.intelehealth.intelesafe.database.dao.EncounterDAO;
import org.intelehealth.intelesafe.database.dao.VisitsDAO;
import org.intelehealth.intelesafe.models.CheckAppUpdateRes;
import org.intelehealth.intelesafe.models.DownloadMindMapRes;
import org.intelehealth.intelesafe.models.dto.EncounterDTO;
import org.intelehealth.intelesafe.models.dto.VisitDTO;
import org.intelehealth.intelesafe.models.loginModel.LoginModel;
import org.intelehealth.intelesafe.models.loginProviderModel.LoginProviderModel;
import org.intelehealth.intelesafe.networkApiCalls.ApiClient;
import org.intelehealth.intelesafe.networkApiCalls.ApiInterface;
import org.intelehealth.intelesafe.syncModule.SyncUtils;
import org.intelehealth.intelesafe.utilities.Base64Utils;
import org.intelehealth.intelesafe.utilities.DownloadMindMaps;
import org.intelehealth.intelesafe.utilities.Logger;
import org.intelehealth.intelesafe.utilities.OfflineLogin;
import org.intelehealth.intelesafe.utilities.SessionManager;
import org.intelehealth.intelesafe.utilities.UrlModifiers;
import org.intelehealth.intelesafe.utilities.UuidDictionary;
import org.intelehealth.intelesafe.utilities.exception.DAOException;
import org.intelehealth.intelesafe.widget.materialprogressbar.CustomProgressDialog;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_CONTACTS;

/**
 * Created By: Prajwal Waingankar
 * Github: prajwalmw
 * Home Screen
 */

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int PERMISSIONS_REQUEST_CALL_PHONE = 101;
    private static final int PERMISSIONS_REQUEST_CODE = 1001;
    SessionManager sessionManager = null;
    ProgressDialog TempDialog;
    CountDownTimer CDT;
    int i = 5;
    Calendar calendar;
    HashSet<String> hashSet;
    Recycler_Home_Adapter recycler_home_adapter;
    ArrayList<Day_Date> recycler_arraylist;
//    Set<Day_Date> set;

    SQLiteDatabase db;
    CustomProgressDialog customProgressDialog;

    TextView lastSyncTextView;
    TextView lastSyncAgo;
    TextView welcomeUser;
    Button manualSyncButton;
    RelativeLayout enter_check_in, home_quarantine_guidelines, educational_videos, user_logout,
            home_quarantine_guidelines_2, educational_videos_2;
    IntentFilter filter;
    Myreceiver reMyreceive;
    SyncUtils syncUtils = new SyncUtils();
    CardView c1, c2, c3, c4, c5, ppe_cardView_request;
    private String key = null;
    private String licenseUrl = null;
    RecyclerView recyclerView;
    TextView tvNoVisit;
    Button help_watsapp;
    TextView tvMentalHelpRequest, tv_ppe_request, mental_Help_Text;
    ImageView home_popup_menu;
    String appLanguage;
    private String name;

    Context context;
    private String mindmapURL = "";
    private DownloadMindMaps mTask;

    EncounterDTO encounterDTO = new EncounterDTO();
    private boolean returning;
    String phistory = "";
    String fhistory = "";

    String fromActivity = "";
    String username = "";
    String password = "";

    String encounterAdultIntials = "";
    String encoded = null;
    Base64Utils base64Utils = new Base64Utils();
    //protected AccountManager manager;

    private int versionCode = 0;

    private CompositeDisposable disposable = new CompositeDisposable();
    List<String> stringListOBS = new ArrayList<>();
    List<String> stringEncounterUUID = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
        sessionManager = new SessionManager(HomeActivity.this);
        appLanguage = sessionManager.getAppLanguage();
        if (!appLanguage.equalsIgnoreCase("")) {
            setLocale(appLanguage);
        }
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String language = sessionManager.getAppLanguage();
        if (!language.equalsIgnoreCase("")) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }
        setTitle(getString(R.string.HomeScreen_Header_Title));
        context = HomeActivity.this;
        customProgressDialog = new CustomProgressDialog(context);
        reMyreceive = new Myreceiver();
        filter = new IntentFilter("lasysync");
        name = sessionManager.getUserName();
//        home_popup_menu = findViewById(R.id.popup_menu);
//        home_popup_menu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PopupMenu popupMenu = new PopupMenu(HomeActivity.this, view);
//                popupMenu.getMenuInflater().inflate(R.menu.home_popup_menu, popupMenu.getMenu());
//                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    public boolean onMenuItemClick(MenuItem item) {
//                        switch (item.getItemId()) {
//                            case R.id.changeLanguage:
//                                chooseLanguageMenu();
//                                return true;
//                            case R.id.logout:
//                                logoutMenu();
//                                return true;
//                            default:
//                                return false;
//                        }
//                    }
//
//                });
//                popupMenu.show();
//            }
//        });


        //manager = AccountManager.get(HomeActivity.this);

        checkAppVer();  //auto-update feature.

        if (sessionManager.getDBCLEAR().equalsIgnoreCase("")) {

            try {
                delDb();
            } catch (DAOException e) {
                e.printStackTrace();
            }
            sessionManager.setDBCLEAR("DONE");

        }

        Intent intent = this.getIntent(); // The intent was passed to the activity
        if (intent != null) {
            fromActivity = intent.getStringExtra("from");
            username = intent.getStringExtra("username");
            password = intent.getStringExtra("password");
        }


        if (fromActivity != null && !fromActivity.isEmpty() && fromActivity.equals("setup"))
        //To handle null pointer exception.
        {
            UserLoginTask(username, password);
        }


        sessionManager.setCurrentLang(getResources().getConfiguration().locale.toString());

        //Help section of watsapp...
        help_watsapp = findViewById(R.id.Help_Watsapp);
        help_watsapp.setPaintFlags(help_watsapp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        help_watsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumberWithCountryCode = "+917972269174"; //+919825989750
                String message =
                        getString(R.string.hello_my_name) + sessionManager.getUserName() +
                                /*" from " + sessionManager.getState() + */ getString(R.string.need_some_assisstance);

                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(
                                String.format("https://api.whatsapp.com/send?phone=%s&text=%s",
                                        phoneNumberWithCountryCode, message))));

        //    startVideoCall();

            }

        });

        tvMentalHelpRequest = findViewById(R.id.tv_mental_help_request);
        tvMentalHelpRequest.setOnClickListener(this);

        SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(context));
        int dayCount = mSharedPreference.getInt("dayCount", 0);

        //Notification check
        // SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        //Intent for 8am alarm is set for everyday.
        PackageManager pm = this.getPackageManager();
        ComponentName receiver = new ComponentName(this, DeviceBootReceiver.class);
        Intent alarmIntent_1 = new Intent(this, AlarmReceiver.class);
        alarmIntent_1.setAction(AppConstants.ACTION_ONE);
        PendingIntent pendingIntent_1 = PendingIntent.getBroadcast(this, 1250, alarmIntent_1, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (dayCount <= 42) {
            //region Enable Daily Notifications
            calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 8); //24 Hour Format - 8am alarm set.
            calendar.set(Calendar.MINUTE, 00);
            calendar.set(Calendar.SECOND, 0);
            // if notification time is before selected time, send notification the next day
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }
            if (manager != null) {
                manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent_1);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
//                        calendar.getTimeInMillis(), pendingIntent_1);
//            }
            }
            //To enable Boot Receiver class
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } else {
            manager.cancel(pendingIntent_1);
        }

        //Intent for 7pm alarm is set for everyday.
        Intent alarmIntent_2 = new Intent(this, AlarmReceiver.class);
        alarmIntent_2.setAction(AppConstants.ACTION_TWO);
        PendingIntent pendingIntent_2 = PendingIntent.getBroadcast(this, 1260, alarmIntent_2, PendingIntent.FLAG_UPDATE_CURRENT);

        if (dayCount <= 42) {    //added by Prajwal the equal case to give notifi in the evening as well on the last day.
            //region Enable Daily Notifications..
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 19); //24 Hour Format - 7pm alarm
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            // if notification time is before selected time, send notification the next day
            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }
            if (manager != null) {
                manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent_2);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent_2);
//            }
            }
            //comments

            //To enable Boot Receiver class
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } else {
            manager.cancel(pendingIntent_2);
        }

        //endregion

        db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();

        recyclerView = findViewById(R.id.recyclerview_data);
        tvNoVisit = findViewById(R.id.tv_no_visit);


//        set = new HashSet<Day_Date>();

        // ArrayList<String> endDate = new ArrayList<>();


        enter_check_in = findViewById(R.id.button_enter_checkin);
        home_quarantine_guidelines = findViewById(R.id.button_home_quarantine);
        home_quarantine_guidelines_2 = findViewById(R.id.button_home_quarantine_2);
        educational_videos_2 = findViewById(R.id.button_educational_videos_2);
        educational_videos = findViewById(R.id.button_educational_videos);
        welcomeUser = findViewById(R.id.welcomeUser);

        welcomeUser.setText(name);

        enter_check_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(HomeActivity.this, VisitSummaryActivity.class);
                startActivity(intent);*/

                createNewVisit();

            }
        });

        home_quarantine_guidelines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent home_quarantine = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.intelehealth.org/ppe-guidelines"));
                startActivity(home_quarantine);*/

//                Intent ppe = new Intent(HomeActivity.this, Webview.class);
//                ppe.putExtra("PPE", 1);
//                startActivity(ppe);

                watchYoutubeVideo(HomeActivity.this, "xTvd7oAEyhs" );
            }
        });

        home_quarantine_guidelines_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ppe = new Intent(HomeActivity.this, Webview.class);
                ppe.putExtra("PPE2", 1);
                startActivity(ppe);
            }
        });

        educational_videos_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ppe = new Intent(HomeActivity.this, Webview.class);
                ppe.putExtra("FAQ2", 1);
                startActivity(ppe);
            }
        });

/*
        home_quarantine_guidelines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File pdfFile_downloaded = new File(Environment.getExternalStorageDirectory() + "/Intelehealth_COVID_PDF/" + "quarantine.pdf");

                if (pdfFile_downloaded.exists()) {
                    File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Intelehealth_COVID_PDF/" + "quarantine.pdf");  // -> filename = maven.pdf
                    //Uri path = Uri.fromFile(pdfFile);
                    Uri path = FileProvider.getUriForFile
                            (context, context.getApplicationContext().getPackageName()
                                    + ".provider", pdfFile);
                    Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                    pdfIntent.setDataAndType(path, "application/pdf");
                    pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    pdfIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    pdfIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

                    try {
                        startActivity(pdfIntent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(HomeActivity.this, "No Application available to view PDF", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    customProgressDialog.show();
                    new DownloadFile().execute("http://www.mohfw.gov.in/DraftGuidelinesforhomequarantine.pdf", "quarantine.pdf");
                }


            }
        });
*/


        educational_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open youtube page.
                /*startActivity
                        (new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://www.intelehealth.org/ppe-faqs")));*/
//                Intent faq = new Intent(HomeActivity.this, Webview.class);
//                faq.putExtra("FAQ", 1);
//                startActivity(faq);
                watchYoutubeVideo(HomeActivity.this, "I47QOyX71kg" );
            }
        });
        Logger.logD(TAG, "onCreate: " + getFilesDir().toString());
        // lastSyncTextView = findViewById(R.id.lastsynctextview);
        // lastSyncAgo = findViewById(R.id.lastsyncago);
        // manualSyncButton = findViewById(R.id.manualsyncbutton);
//        manualSyncButton.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
      /*  c1 = findViewById(R.id.cardview_newpat);
        c2 = findViewById(R.id.cardview_find_patient);
        c3 = findViewById(R.id.cardview_today_patient);
        c4 = findViewById(R.id.cardview_active_patients);
        c5 = findViewById(R.id.cardview_video_libraby);*/
       /* c1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Loads the config file values and check for the boolean value of privacy key.
                ConfigUtils configUtils = new ConfigUtils(HomeActivity.this);
                if (configUtils.privacy_notice()) {
                    Intent intent = new Intent(HomeActivity.this, PrivacyNotice_Activity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomeActivity.this, IdentificationActivity.class);
                    startActivity(intent);
                }
            }
        });
        c2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, SearchPatientActivity.class);
                startActivity(intent);
            }
        });
        c3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, TodayPatientActivity.class);
                startActivity(intent);
            }
        });
        c4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ActivePatientActivity.class);
                startActivity(intent);
            }
        });
        c5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, VideoLibraryActivity.class);
                startActivity(intent);
            }
        });

        lastSyncTextView.setText(getString(R.string.last_synced) + " \n" + sessionManager.getLastSyncDateTime());*/

//        if (!sessionManager.getLastSyncDateTime().equalsIgnoreCase("- - - -")
//                && Locale.getDefault().toString().equalsIgnoreCase("en")) {
////            lastSyncAgo.setText(CalculateAgoTime());
//        }

/*
        manualSyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                AppConstants.notificationUtils.showNotifications(getString(R.string.sync), getString(R.string.syncInProgress), 1, context);

                if (isNetworkConnected()) {
                    Toast.makeText(context, getString(R.string.syncInProgress), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.failed_synced), Toast.LENGTH_LONG).show();
                }

                syncUtils.syncForeground("home");
//                if (!sessionManager.getLastSyncDateTime().equalsIgnoreCase("- - - -")
//                        && Locale.getDefault().toString().equalsIgnoreCase("en")) {
//                    lastSyncAgo.setText(sessionManager.getLastTimeAgo());
//                }
            }
        });
*/
        // WorkManager.getInstance().enqueueUniquePeriodicWork(AppConstants.UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, AppConstants.PERIODIC_WORK_REQUEST);
/*
        if (sessionManager.isFirstTimeLaunched()) {
            TempDialog = new ProgressDialog(HomeActivity.this, R.style.AlertDialogStyle); //thats how to add a style!
            TempDialog.setTitle(R.string.syncInProgress);
            TempDialog.setCancelable(false);
            TempDialog.setProgress(i);

            TempDialog.show();

            CDT = new CountDownTimer(7000, 1000) {
                public void onTick(long millisUntilFinished) {
                    TempDialog.setTitle(getString(R.string.syncInProgress));
                    TempDialog.setMessage(getString(R.string.please_wait));
                    i--;
                }

                public void onFinish() {
                    TempDialog.dismiss();
                    //Your Code ...
                    sessionManager.setFirstTimeLaunched(false);
                    sessionManager.setMigration(true);
                }
            }.start();

        }
*/
        sessionManager.setSetupComplete(false);
        sessionManager.setMigration(true);

        if (sessionManager.isReturningUser()) {
            customProgressDialog.show();
            syncUtils.syncForeground("HOME_SCREEN");
        } else {
            renderList();
        }

        // added by venu N on 01/04/2020 for new Change in Home Screen.
        user_logout = findViewById(R.id.user_logout);
        user_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(HomeActivity.this, v);
                popupMenu.getMenuInflater().inflate(R.menu.home_popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.changeLanguage:
                                chooseLanguageMenu();
                                return true;
                            case R.id.logout:
                                logoutMenu();
                                return true;
                            default:
                                return false;
                        }
                    }

                });
                popupMenu.show();
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HomeActivity.this);
//                alertDialogBuilder.setMessage(getString(R.string.logout_dialog));
//                alertDialogBuilder.setNegativeButton(R.string.generic_no, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });
//                alertDialogBuilder.setPositiveButton(R.string.generic_yes, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        logout();
//                    }
//                });
//                AlertDialog alertDialog = alertDialogBuilder.create();
//                alertDialog.show();
            }
        });
        ppe_cardView_request = findViewById(R.id.ppe_cardView_request);
        mental_Help_Text = findViewById(R.id.mental_Help_Text);
        String teleconsult_request = "";
        if (sessionManager.getPatientCountry().equals("India")) {
            mental_Help_Text.setText(getString(R.string.tele_consultant_text));
            tvMentalHelpRequest.setText(getString(R.string.teleconsult_request));
            teleconsult_request = getString(R.string.teleconsult_request);
            //  ppe_cardView_request.setVisibility(View.VISIBLE);
        } else {
            mental_Help_Text.setText(getString(R.string.Mental_health_support_text));
            tvMentalHelpRequest.setText(getString(R.string.mental_health_support));
            teleconsult_request = getString(R.string.mental_health_support);
            // ppe_cardView_request.setVisibility(View.GONE);
        }

        SpannableString content = new SpannableString(teleconsult_request);
        content.setSpan(new UnderlineSpan(), 0, teleconsult_request.length(), 0);
        tvMentalHelpRequest.setText(content);
        ppe_cardView_request.setVisibility(View.GONE);
        tv_ppe_request = findViewById(R.id.tv_ppe_request);
        String mystring = getString(R.string.ppe_req);
        SpannableString content1 = new SpannableString(mystring);
        content1.setSpan(new UnderlineSpan(), 0, mystring.length(), 0);
        tv_ppe_request.setText(content1);
        tv_ppe_request.setOnClickListener(this);
        //tv_ppe_request.setText(getString(R.string.ppe_req));

        WorkManager.getInstance().enqueueUniquePeriodicWork(AppConstants.UNIQUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, AppConstants.PERIODIC_WORK_REQUEST);

    }

    public static void watchYoutubeVideo(Context context, String id){
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + id));
        try {
            context.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            context.startActivity(webIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                startVideoCall();
            } else {
                Toast.makeText(this, "In order to get help, accept phone permission in Settings -> Permissions.", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void startVideoCall() {
        String phoneNumberWithCountryCode = "918433750434"; //+919825989750

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ((checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) ||(checkSelfPermission(CALL_PHONE) != PackageManager.PERMISSION_GRANTED))) {
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE, READ_CONTACTS}, PERMISSIONS_REQUEST_CODE);
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkPermission())
//        {
//            ActivityCompat.requestPermissions(this, new String[]{READ_CONTACTS, CALL_PHONE}, PERMISSIONS_REQUEST_CODE);
//        }
        else
        {
             Cursor cursor = context.getContentResolver ()
                .query (
                        ContactsContract.Data.CONTENT_URI,
                        new String [] { ContactsContract.Data._ID },
                        ContactsContract.RawContacts.ACCOUNT_TYPE + " = 'com.whatsapp' " +
                                "AND " + ContactsContract.Data.MIMETYPE + " = 'vnd.android.cursor.item/vnd.com.whatsapp.video.call' " +
                                "AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE '%" + phoneNumberWithCountryCode + "%'",
                        null,
                        ContactsContract.Contacts.DISPLAY_NAME
                );

                if (cursor == null) {
                    // throw an exception
                }

                long id = -1;
                while (cursor.moveToNext()) {
                    id = cursor.getLong (cursor.getColumnIndex (ContactsContract.Data._ID));
                }

                if (!cursor.isClosed ()) {
                    cursor.close ();
                }
                String voiceCallID = Long.toString(id);
                Intent intent = new Intent ();
                intent.setAction (Intent.ACTION_VIEW);
                intent.setDataAndType (Uri.parse ("content://com.android.contacts/data/" + voiceCallID), "vnd.android.cursor.item/vnd.com.whatsapp.voip.call");
                intent.setPackage ("com.whatsapp");

                startActivity (intent);

        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    public void setLocale(String appLanguage)
    {
        Locale locale = new Locale(appLanguage);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    private void chooseLanguageMenu()
    {
        Intent intent = new Intent(HomeActivity.this, ChooseLanguageActivity.class);
        startActivity(intent);
    }
    private void logoutMenu()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.logout_dialog));
        alertDialogBuilder.setNegativeButton(R.string.generic_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.setPositiveButton(R.string.generic_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                logout();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    private void delDb() throws DAOException {

        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        try {

            //Delete Patients
            db.delete("tbl_patient", "uuid != ?", new String[]{"" + sessionManager.getPersionUUID()});

            //Delete Patient Attribute
            db.delete("tbl_patient_attribute", "patientuuid != ?", new String[]{"" + sessionManager.getPersionUUID()});

            String[] strVisitsUUID = fetchVisits();

            if (strVisitsUUID.length > 0) {
                for (int j = 0; j < strVisitsUUID.length; j++) {

                    String table = "tbl_encounter";
                    String whereClause = "visituuid=?";
                    String[] whereArgs = new String[]{String.valueOf(strVisitsUUID[j])};
                    String rawQuery = "Delete from tbl_encounter where (visituuid=?)";
                    db.execSQL(rawQuery, whereArgs);

                }
            }

/////////////////////////////
            //Delete visits
            db.delete("tbl_visit", "patientuuid != ?", new String[]{"" + sessionManager.getPersionUUID()});
/////////////////////////////

            String[] strclrVisitsUUID = fetchVisits1();

            String[] strEncounterUUID = new String[0];

            if (strclrVisitsUUID.length > 0) {
                for (int j = 0; j < strclrVisitsUUID.length; j++) {

                    strEncounterUUID = fetchEncountersUUID(strclrVisitsUUID[j].toString());

                }
            }

            String[] strEncountersUUID = fetchEncounters();

            List<String> EncounterUUIDList = new ArrayList<>();

            if (strEncounterUUID.length > 0) {
                Collections.addAll(EncounterUUIDList, strEncounterUUID);
            }

            List<String> allEncountersList = new ArrayList<>();

            if (strEncountersUUID.length > 0) {
                Collections.addAll(allEncountersList, strEncountersUUID);
            }

            if (allEncountersList.size() > 0 && EncounterUUIDList.size() > 0) {
                allEncountersList.removeAll(EncounterUUIDList);

                for (int j = 0; j < allEncountersList.size(); j++) {

                    db.delete("tbl_encounter", "uuid = ?", new String[]{"" + allEncountersList.get(j)});

                }
            }

            String[] strFinalEncountersUUID = fetchEncounters();

            //Delete OBS
            String[] strOBSUUID = new String[0];

            if (strFinalEncountersUUID.length > 0) {
                for (int j = 0; j < strFinalEncountersUUID.length; j++) {

                    strOBSUUID = fetchOBSUUID(strFinalEncountersUUID[j].toString());
                }
            }

            String[] strAllOBSUUID = fetchAllOBS();

            List<String> stringUUIDList = new ArrayList<>();

            if (strOBSUUID.length > 0) {
                Collections.addAll(stringUUIDList, strOBSUUID);
            }

            List<String> stringList = new ArrayList<>();
            if (strAllOBSUUID.length > 0) {
                Collections.addAll(stringList, strAllOBSUUID);
            }

            if (stringList.size() > 0) {

                stringList.removeAll(stringUUIDList);

                for (int j = 0; j < stringList.size(); j++) {

                    db.delete("tbl_obs", "uuid = ?", new String[]{"" + stringList.get(j)});

                }
            }

            db.setTransactionSuccessful();
        } catch (SQLException s) {
            FirebaseCrashlytics.getInstance().recordException(s);
            throw new DAOException(s);
        } finally {
            db.endTransaction();
        }
    }

    private String[] fetchVisits() {

        List<String> visitList = new ArrayList<>();
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        Cursor idCursor = db.rawQuery("SELECT * FROM tbl_visit where (patientuuid != ?)", new String[]{"" + sessionManager.getPersionUUID()});

        if (idCursor.getCount() != 0) {
            while (idCursor.moveToNext()) {

                visitList.add(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));

            }
        }
        idCursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();

        return visitList.toArray(new String[0]);
    }

    private String[] fetchVisits1() {

        List<String> visitList = new ArrayList<>();
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        Cursor idCursor = db.rawQuery("SELECT * FROM tbl_visit where (patientuuid = ?)", new String[]{"" + sessionManager.getPersionUUID()});

        if (idCursor.getCount() != 0) {
            while (idCursor.moveToNext()) {

                visitList.add(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));

            }
        }
        idCursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();

        return visitList.toArray(new String[0]);
    }

    private String[] fetchAllOBS() {

        List<String> stringList = new ArrayList<>();
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        Cursor idCursor = db.rawQuery("SELECT * FROM tbl_obs", null);

        if (idCursor.getCount() != 0) {
            while (idCursor.moveToNext()) {

                stringList.add(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));

            }
        }
        idCursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();

        return stringList.toArray(new String[0]);
    }

    private String[] fetchEncounters() {

        List<String> stringUUID = new ArrayList<>();
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        Cursor idCursor = db.rawQuery("SELECT * FROM tbl_encounter", null);

        if (idCursor.getCount() != 0) {
            while (idCursor.moveToNext()) {

                stringUUID.add(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));

            }
        }
        idCursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();

        return stringUUID.toArray(new String[0]);
    }

    private String[] fetchOBSUUID(String encounterUUID) {

        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        Cursor idCursor = db.rawQuery("SELECT * FROM tbl_obs where (encounteruuid = ?)", new String[]{"" + encounterUUID});

        if (idCursor.getCount() != 0) {
            while (idCursor.moveToNext()) {

                stringListOBS.add(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));

            }
        }
        idCursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();

        return stringListOBS.toArray(new String[0]);
    }

    private String[] fetchEncountersUUID(String visitUUID) {

        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        db.beginTransaction();
        Cursor idCursor = db.rawQuery("SELECT * FROM tbl_encounter where (visituuid = ?)", new String[]{"" + visitUUID});

        if (idCursor.getCount() != 0) {
            while (idCursor.moveToNext()) {

                stringEncounterUUID.add(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));

            }
        }
        idCursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();

        return stringEncounterUUID.toArray(new String[0]);
    }

    private void renderList() {
        recycler_arraylist = new ArrayList<Day_Date>();
        String endDate = "";
        String query = "SELECT v.startdate FROM tbl_visit v, tbl_patient p WHERE " +
                "p.uuid = v.patientuuid AND v.startdate IS NOT NULL AND (v.issubmitted == 1 OR v.enddate IS NOT NULL) AND " +
                "v.patientuuid = ?";
        String[] data = {sessionManager.getPersionUUID()};

        final Cursor cursor = db.rawQuery(query, data);
        int a = 1;
        int b = 0;
        String dd = "";
        int a1;
        StringBuilder stringBuilder;
        hashSet = new HashSet<>();
        ArrayList<String> array_original_date = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    try {

                        endDate = cursor.getString(cursor.getColumnIndexOrThrow("startdate"));
                        stringBuilder = new StringBuilder(endDate);
                        a1 = stringBuilder.indexOf("T");
                        dd = stringBuilder.substring(0, a1);

                        //comment...
                        array_original_date.add(b, endDate);
                        b++;
//                        hashSet.add(new Day_Date("Day "+a, endDate));
                        //   boolean t = ;
                        hashSet.add(dd);


//                        for(int i=0; i<recycler_arraylist.size(); i++)
//                        {
//                            recycler_arraylist.get(i);
//                            String v = recycler_arraylist.get(i).getDate().toString();
//                            Log.d("MM","MM: "+v);
//
//                        }

                               /* recycler_arraylist.add(new Day_Date
                                        ("Day " + a, dd));
                                a++;*/


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        ArrayList<String> new_arraylist = new ArrayList<>();
        new_arraylist.addAll(hashSet);

        Collections.sort(new_arraylist); // added by venu N to sort the

        for (int j = 0; j < new_arraylist.size(); j++) {
            recycler_arraylist.add(new Day_Date(getString(R.string.visit) + " " + a, new_arraylist.get(j)));
            a++;
        }

        if (!recycler_arraylist.isEmpty()) {
            sessionManager.setFirstCheckin("true");
            tvNoVisit.setVisibility(View.GONE);
            recycler_home_adapter = new Recycler_Home_Adapter(context, recycler_arraylist, array_original_date);
            recycler_home_adapter.notifyDataSetChanged();

            recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(recycler_home_adapter);
        } else {
            sessionManager.setFirstCheckin("false");
            tvNoVisit.setVisibility(View.VISIBLE);
        }
    }

    private void createNewVisit() {

        sessionManager.setProviderID("28cea4ab-3188-434a-82f0-055133090a38");

        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        Date todayDate = new Date();
        String thisDate = currentDate.format(todayDate);


        String uuid = UUID.randomUUID().toString();
        EncounterDAO encounterDAO = new EncounterDAO();
        encounterDTO = new EncounterDTO();
        encounterDTO.setUuid(UUID.randomUUID().toString());
        encounterDTO.setEncounterTypeUuid(encounterDAO.getEncounterTypeUuid("ENCOUNTER_VITALS"));
        encounterDTO.setEncounterTime(thisDate);
        encounterDTO.setVisituuid(uuid);
        encounterDTO.setSyncd(false);
        encounterDTO.setProvideruuid(sessionManager.getProviderID());
        Log.d("DTO", "DTO:detail " + encounterDTO.getProvideruuid());
        encounterDTO.setVoided(0);
        encounterDTO.setPrivacynotice_value(sessionManager.getPrivacyValue());//privacy value added.

        try {
            encounterDAO.createEncountersToDB(encounterDTO);
        } catch (DAOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        InteleHealthDatabaseHelper mDatabaseHelper = new InteleHealthDatabaseHelper(HomeActivity.this);
        SQLiteDatabase sqLiteDatabase = mDatabaseHelper.getReadableDatabase();

        String CREATOR_ID = sessionManager.getCreatorID();
        returning = false;
        sessionManager.setReturning(returning);

        String[] cols = {"value"};
        Cursor cursor = sqLiteDatabase.query("tbl_obs", cols, "encounteruuid=? and conceptuuid=?",// querying for PMH
                new String[]{encounterDTO.getUuid(), UuidDictionary.RHK_MEDICAL_HISTORY_BLURB},
                null, null, null);

        if (cursor.moveToFirst()) {
            // rows present
            do {
                // so that null data is not appended
                phistory = phistory + cursor.getString(0);

            }
            while (cursor.moveToNext());
            returning = true;
            sessionManager.setReturning(returning);
        }
        cursor.close();

        Cursor cursor1 = sqLiteDatabase.query("tbl_obs", cols, "encounteruuid=? and conceptuuid=?",// querying for FH
                new String[]{encounterDTO.getUuid(), UuidDictionary.RHK_MEDICAL_HISTORY_BLURB},
                null, null, null);
        if (cursor1.moveToFirst()) {
            // rows present
            do {
                fhistory = fhistory + cursor1.getString(0);
            }
            while (cursor1.moveToNext());
            returning = true;
            sessionManager.setReturning(returning);
        }
        cursor1.close();

        // Will display data for patient as it is present in database
        // Toast.makeText(PatientDetailActivity.this,"PMH: "+phistory,Toast.LENGTH_SHORT).sƒhow();
        // Toast.makeText(PatientDetailActivity.this,"FH: "+fhistory,Toast.LENGTH_SHORT).show();

        Intent intent2 = new Intent(HomeActivity.this, PhysicalExamActivity.class);
//      String fullName = patient_new.getFirst_name() + " " + patient_new.getLast_name();
        intent2.putExtra("patientUuid", sessionManager.getPersionUUID());

        VisitDTO visitDTO = new VisitDTO();

        visitDTO.setUuid(uuid);
        visitDTO.setPatientuuid(sessionManager.getPersionUUID());
        visitDTO.setStartdate(thisDate);
        visitDTO.setVisitTypeUuid(UuidDictionary.VISIT_TELEMEDICINE);
        visitDTO.setLocationuuid(sessionManager.getLocationUuid());
        visitDTO.setSyncd(false);
        visitDTO.setCreatoruuid(sessionManager.getCreatorID());//static
        visitDTO.setIsSubmitted(0);// add to check is patient click on submitt in summary.

        VisitsDAO visitsDAO = new VisitsDAO();

        try {
            visitsDAO.insertPatientToDB(visitDTO);
        } catch (DAOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        // visitUuid = String.valueOf(visitLong);
//                localdb.close();
        intent2.putExtra("patientUuid", sessionManager.getPersionUUID());
        intent2.putExtra("visitUuid", uuid);
        intent2.putExtra("encounterUuidVitals", "");
        intent2.putExtra("encounterUuidAdultIntial", "");
        intent2.putExtra("name", sessionManager.getUserName());
        intent2.putExtra("state", "");
        intent2.putExtra("tag", "new");
        startActivity(intent2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_mental_help_request:
                /*Uri uri = Uri.parse("https://www.intelesafe.org/mental-health"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);*/
                Intent ppe = new Intent(HomeActivity.this, Webview.class);
                ppe.putExtra("HomeMental", 1);
                ppe.putExtra("HomeCardTitle", tvMentalHelpRequest.getText().toString());
                startActivity(ppe);
                break;
            case R.id.tv_ppe_request:
                showAlertToRequestPPE();
                break;
        }
    }

    /**
     * alert for request the PPE
     */
    private void showAlertToRequestPPE() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View customView = LayoutInflater.from(this).inflate(R.layout.ppe_request_alert, null);
        builder.setView(customView);
        Button btn_positive = customView.findViewById(R.id.btn_positive);
        Button btn_negative = customView.findViewById(R.id.btn_negative);
        EditText edt_city = customView.findViewById(R.id.edt_city_village);
        EditText edt_address1 = customView.findViewById(R.id.edt_address1);
        EditText edt_address2 = customView.findViewById(R.id.edt_address2);
        EditText edt_pincode = customView.findViewById(R.id.edt_pincode);
        EditText edt_ppe_qty = customView.findViewById(R.id.edt_ppe_qty);
        AlertDialog alert = builder.create();
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityStr = edt_city.getText().toString();
                String addressStr1 = edt_address1.getText().toString();
                String addressStr2 = edt_address2.getText().toString();
                String pinCodeStr = edt_pincode.getText().toString();
                String ppeQtyStr = edt_ppe_qty.getText().toString();
                if (TextUtils.isEmpty(cityStr)) {
                    edt_city.setError(getString(R.string.error_field_required));
                    edt_city.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(addressStr1)) {
                    edt_address1.setError(getString(R.string.error_field_required));
                    edt_address1.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(pinCodeStr)) {
                    edt_pincode.setError(getString(R.string.error_field_required));
                    edt_pincode.requestFocus();
                    return;
                }

                if (pinCodeStr.length() < 6) {
                    edt_pincode.setError(getString(R.string.postal_code_validation));
                    edt_pincode.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(ppeQtyStr)) {
                    edt_ppe_qty.setError(getString(R.string.error_field_required));
                    edt_ppe_qty.requestFocus();
                    return;
                }

                String finalAddressStr = addressStr1 + " " + (!TextUtils.isEmpty(addressStr2 + " ") ? addressStr2 : "") + cityStr + " " + pinCodeStr;
                String phoneNumberWithCountryCode = "+918108220025";
                String messageStr = R.string.hi_request_for_ppe + "\n" +
                        R.string.name_ppe + sessionManager.getUserName() + "\n" +
                        R.string.address_ppe + finalAddressStr + "\n" +
                        R.string.ppe_required + ppeQtyStr;

                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(
                                String.format("https://api.whatsapp.com/send?phone=%s&text=%s",
                                        phoneNumberWithCountryCode, messageStr))));

                alert.dismiss();
            }
        });
        btn_negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.show();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setVisibility(View.GONE);
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> https://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf
            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "Intelehealth_COVID_PDF");
            folder.mkdir();

            File pdfFile = new File(folder, fileName);

            try {
                pdfFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            customProgressDialog.dismiss();

            File pdfFile = new File(Environment.getExternalStorageDirectory() + "/Intelehealth_COVID_PDF/" + "quarantine.pdf");  // -> filename = maven.pdf
            //Uri path = Uri.fromFile(pdfFile);
            Uri path = FileProvider.getUriForFile
                    (context, context.getApplicationContext().getPackageName()
                            + ".provider", pdfFile);
            Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
            pdfIntent.setDataAndType(path, "application/pdf");
            pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pdfIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pdfIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            try {
                startActivity(pdfIntent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(HomeActivity.this, R.string.no_application, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String CalculateAgoTime() {
        String finalTime = "";

        String syncTime = sessionManager.getLastSyncDateTime();

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        ParsePosition pos = new ParsePosition(0);
        long then = formatter.parse(syncTime, pos).getTime();
        long now = new Date().getTime();

        long seconds = (now - then) / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        String time = "";
        long num = 0;
        if (days > 0) {
            num = days;
            time = days + " " + context.getString(R.string.day);
        } else if (hours > 0) {
            num = hours;
            time = hours + " " + context.getString(R.string.hour);
        } else if (minutes >= 0) {
            num = minutes;
            time = minutes + " " + context.getString(R.string.minute);
        }
//      <For Seconds>
//      else {
//            num = seconds;
//            time = seconds + " second";
//      }
        if (num > 1) {
            time += context.getString(R.string.s);
        }
        finalTime = time + " " + context.getString(R.string.ago);

        sessionManager.setLastTimeAgo(finalTime);

        return finalTime;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.syncOption:
//                refreshDatabases();
//                return true;
       /*     case R.id.settingsOption:
                settings();
                return true;
            case R.id.updateProtocolsOption: {

                if (!sessionManager.getLicenseKey().isEmpty()) {

                    String licenseUrl = sessionManager.getMindMapServerUrl();
                    String licenseKey = sessionManager.getLicenseKey();
                    getMindmapDownloadURL("http://" + licenseUrl + ":3004/", licenseKey);

                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    LayoutInflater li = LayoutInflater.from(this);
                    View promptsView = li.inflate(R.layout.dialog_mindmap_cred, null);
                    dialog.setTitle(getString(R.string.enter_license_key))
                            .setView(promptsView)
                            .setPositiveButton(getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Dialog d = (Dialog) dialog;

                                    EditText etURL = d.findViewById(R.id.licenseurl);
                                    EditText etKey = d.findViewById(R.id.licensekey);
                                    String url = etURL.getText().toString().trim();
                                    String key = etKey.getText().toString().trim();

                                    if (url.isEmpty()) {
                                        etURL.setError(getResources().getString(R.string.enter_server_url));
                                        etURL.requestFocus();
                                        return;
                                    }
                                    if (url.contains(":")) {
                                        etURL.setError(getResources().getString(R.string.invalid_url));
                                        etURL.requestFocus();
                                        return;
                                    }
                                    if (key.isEmpty()) {
                                        etKey.setError(getResources().getString(R.string.enter_license_key));
                                        etKey.requestFocus();
                                        return;
                                    }

                                    sessionManager.setMindMapServerUrl(url);
                                    getMindmapDownloadURL("http://" + url + ":3004/", key);

                                }
                            })
                            .setNegativeButton(getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.create().show();

                }
                return true;
            }*/
         /*   case R.id.sync:
//                pullDataDAO.pullData(this);
//                pullDataDAO.pushDataApi();
//                AppConstants.notificationUtils.showNotifications(getString(R.string.sync), getString(R.string.syncInProgress), 1, this);
                boolean isSynced = syncUtils.syncForeground();
//                boolean i = imagesPushDAO.patientProfileImagesPush();
//                boolean o = imagesPushDAO.obsImagesPush();
//                if (isSynced)
//                    AppConstants.notificationUtils.showNotifications_noProgress(getString(R.string.sync_not_available), getString(R.string.please_connect_to_internet), getApplicationContext());
//                else
//                    AppConstants.notificationUtils.showNotifications(getString(R.string.image_upload), getString(R.string.image_upload_failed), 4, this);
                return true;
                */
//            case R.id.backupOption:
//                manageBackup(true, false);  // to backup app data at any time of the day
//                return true;
//
//            case R.id.restoreOption:
//                manageBackup(false, false); // to restore app data if db is empty
//                return true;

            case R.id.logoutOption:
//                manageBackup(true, false);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage(getString(R.string.logout_dialog));
                alertDialogBuilder.setNegativeButton(R.string.generic_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alertDialogBuilder.setPositiveButton(R.string.generic_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        logout();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This method starts intent to another activity to change settings
     *
     * @return void
     */
    public void settings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Logs out the user. It removes user account using AccountManager.
     *
     * @return void
     */
    public void logout() {

        sessionManager.setSetupComplete(true);

        OfflineLogin.getOfflineLogin().setOfflineLoginStatus(false);

//        parseLogOut();

        //Commented by Venu for Account Manager Issue.
       /* AccountManager manager = AccountManager.get(HomeActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Account[] accountList = manager.getAccountsByType("io.intelehealth.openmrs");
        if (accountList.length > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (int i = 0; i < accountList.length; i++) {
                    manager.removeAccount(accountList[i], HomeActivity.this, null, null);
                }
            } else {
                for (int i = 0; i < accountList.length; i++) {
                    manager.removeAccount(accountList[i], null, null); // Legacy implementation
                }
            }
        }*/
        sessionManager.setDBCLEAR("");
        sessionManager.setPullExcutedTime("2020-01-23 22:21:48");
        sessionManager.setFirstCheckin("false");
        sessionManager.setReturningUser(false);
        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();

//        SyncUtils syncUtils = new SyncUtils();
//        syncUtils.syncBackground();
    }


    @Override
    protected void onResume() {
        registerReceiver(reMyreceive, filter);
        registerReceiver(reMyreceive, new IntentFilter("org.intelehealth.intelesafe.refreshCheck-in"));
        checkAppVer();  //auto-update feature.
//        lastSyncTextView.setText(getString(R.string.last_synced) + " \n" + sessionManager.getLastSyncDateTime());
        if (!sessionManager.getLastSyncDateTime().equalsIgnoreCase("- - - -")
                && Locale.getDefault().toString().equals("en")) {
//            lastSyncAgo.setText(CalculateAgoTime());
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(reMyreceive);
        super.onDestroy();
    }

    private boolean keyVerified(String key) {
        //TODO: Verify License Key
        return true;
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertdialogBuilder = new AlertDialog.Builder(this);
        alertdialogBuilder.setMessage(R.string.sure_to_exit);
        alertdialogBuilder.setPositiveButton(R.string.generic_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                moveTaskToBack(true);
                // finish();
            }
        });
        alertdialogBuilder.setNegativeButton(R.string.generic_no, null);

        AlertDialog alertDialog = alertdialogBuilder.create();
        alertDialog.show();

        Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        Button negativeButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);

        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        positiveButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

        negativeButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        negativeButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

    }

    public class Myreceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            lastSyncTextView.setText(getString(R.string.last_synced) + " \n" + sessionManager.getLastSyncDateTime());
//          lastSyncAgo.setText(sessionManager.getLastTimeAgo());

            if (intent != null) {
                String strAction = intent.getAction().toString();
                if (strAction.equalsIgnoreCase("org.intelehealth.intelesafe.refreshCheck-in")) {
                    renderList();
                    sessionManager.setReturningUser(false);
                    customProgressDialog.dismiss();
                }
            }
        }
    }

    private void getMindmapDownloadURL(String url, String key) {
        customProgressDialog.show();
        ApiClient.changeApiBaseUrl(url);
        ApiInterface apiService = ApiClient.createService(ApiInterface.class);
        try {
            Observable<DownloadMindMapRes> resultsObservable = apiService.DOWNLOAD_MIND_MAP_RES_OBSERVABLE(key);
            resultsObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<DownloadMindMapRes>() {
                        @Override
                        public void onNext(DownloadMindMapRes res) {
                            customProgressDialog.dismiss();
                            if (res.getMessage() != null && res.getMessage().equalsIgnoreCase("Success")) {

                                Log.e("MindMapURL", "Successfully get MindMap URL");
                                mTask = new DownloadMindMaps(context);
                                mindmapURL = res.getMindmap().trim();
                                sessionManager.setLicenseKey(key);
                                checkExistingMindMaps();

                            } else {
                                Toast.makeText(context, getResources().getString(R.string.no_protocols_found), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            customProgressDialog.dismiss();
                            Toast.makeText(context, getResources().getString(R.string.unable_to_get_proper_response), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "changeApiBaseUrl: " + e.getMessage());
            Log.e(TAG, "changeApiBaseUrl: " + e.getStackTrace());
        }
    }

    private void checkExistingMindMaps() {

        //Check is there any existing mindmaps are present, if yes then delete.

        File engines = new File(context.getFilesDir().getAbsolutePath(), "/Engines");
        Log.e(TAG, "Engines folder=" + engines.exists());
        if (engines.exists()) {
            engines.delete();
        }
        File logo = new File(context.getFilesDir().getAbsolutePath(), "/logo");
        Log.e(TAG, "Logo folder=" + logo.exists());
        if (logo.exists()) {
            logo.delete();
        }
        File physicalExam = new File(context.getFilesDir().getAbsolutePath() + "/physExam.json");
        Log.e(TAG, "physExam.json=" + physicalExam.exists());
        if (physicalExam.exists()) {
            physicalExam.delete();
        }
        File familyHistory = new File(context.getFilesDir().getAbsolutePath() + "/famHist.json");
        Log.e(TAG, "famHist.json=" + familyHistory.exists());
        if (familyHistory.exists()) {
            familyHistory.delete();
        }
        File pastMedicalHistory = new File(context.getFilesDir().getAbsolutePath() + "/patHist.json");
        Log.e(TAG, "patHist.json=" + pastMedicalHistory.exists());
        if (pastMedicalHistory.exists()) {
            pastMedicalHistory.delete();
        }
        File config = new File(context.getFilesDir().getAbsolutePath() + "/config.json");
        Log.e(TAG, "config.json=" + config.exists());
        if (config.exists()) {
            config.delete();
        }

        //Start downloading mindmaps
        mTask.execute(mindmapURL, context.getFilesDir().getAbsolutePath() + "/mindmaps.zip");
        Log.e("DOWNLOAD", "isSTARTED");

    }

    /**
     * class UserLoginTask will authenticate user using email and password.
     * Depending on server's response, user may or may not have successful login.
     * This class also uses SharedPreferences to store session ID
     */
    public void UserLoginTask(String mEmail, String mPassword) {

        UrlModifiers urlModifiers = new UrlModifiers();
        String urlString = urlModifiers.loginUrl(BuildConfig.CLEAN_URL);
        Logger.logD(TAG, "username and password" + mEmail + mPassword);
        encoded = base64Utils.encoded(mEmail, mPassword);
        sessionManager.setEncoded(encoded);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Observable<LoginModel> loginModelObservable = AppConstants.apiInterface.LOGIN_MODEL_OBSERVABLE(urlString, "Basic " + encoded);
        loginModelObservable.subscribe(new Observer<LoginModel>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LoginModel loginModel) {
                int responsCode = loginModel.hashCode();
                Boolean authencated = loginModel.getAuthenticated();
                Gson gson = new Gson();

                Logger.logD(TAG, "success" + gson.toJson(loginModel));
                sessionManager.setChwname(loginModel.getUser().getDisplay());
                sessionManager.setCreatorID(loginModel.getUser().getUuid());
                sessionManager.setSessionID(loginModel.getSessionId());
                sessionManager.setProviderID(loginModel.getUser().getPerson().getUuid());

                UrlModifiers urlModifiers = new UrlModifiers();
                String url = urlModifiers.loginUrlProvider(BuildConfig.CLEAN_URL, loginModel.getUser().getUuid());
                if (authencated) {
                    Observable<LoginProviderModel> loginProviderModelObservable = AppConstants.apiInterface.LOGIN_PROVIDER_MODEL_OBSERVABLE(url, "Basic " + encoded);
                    loginProviderModelObservable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new DisposableObserver<LoginProviderModel>() {
                                @Override
                                public void onNext(LoginProviderModel loginProviderModel) {
                                    if (loginProviderModel.getResults().size() != 0) {
                                        for (int i = 0; i < loginProviderModel.getResults().size(); i++) {
                                            Log.i(TAG, "doInBackground: " + loginProviderModel.getResults().get(i).getUuid());
                                            sessionManager.setProviderID(loginProviderModel.getResults().get(i).getUuid());
                                        }
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Logger.logD(TAG, "handle provider error" + e.getMessage());
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                }
            }

            @Override
            public void onError(Throwable e) {
                Logger.logD(TAG, "Login Failure" + e.getMessage());

            }

            @Override
            public void onComplete() {
                Logger.logD(TAG, "completed");
            }
        });

    }


    private void checkAppVer() {

        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        disposable.add((Disposable) AppConstants.apiInterface.checkAppUpdate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<CheckAppUpdateRes>() {
                    @Override
                    public void onSuccess(CheckAppUpdateRes res) {
                        int latestVersionCode = 0;
                        if (!res.getLatestVersionCode().isEmpty()) {
                            latestVersionCode = Integer.parseInt(res.getLatestVersionCode());
                        }

                        if (latestVersionCode > versionCode) {
                            android.app.AlertDialog.Builder builder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                builder = new android.app.AlertDialog.Builder(HomeActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                            } else {
                                builder = new android.app.AlertDialog.Builder(HomeActivity.this);
                            }
                            builder.setTitle(getResources().getString(R.string.new_update_available))
                                    .setCancelable(false)
                                    .setMessage(getResources().getString(R.string.update_app_note))
                                    .setPositiveButton(getResources().getString(R.string.update), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {

                                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                            } catch (ActivityNotFoundException anfe) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                            }

                                        }
                                    })

                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .setCancelable(false)
                                    .show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("Error", "" + e);
                    }
                })
        );

    }


    public void pastVisits(int position, String check_inDate) {

        String patientuuid = sessionManager.getPersionUUID();
        List<String> visitList = new ArrayList<>();
        List<String> encounterVitalList = new ArrayList<>();
        List<String> encounterAdultList = new ArrayList<>();

        String end_date = "";
        String date = "";
        String encounterlocalAdultintial = "";
        String encountervitalsLocal = null;
        String encounterIDSelection = "visituuid = ?";

        String visitSelection = "patientuuid = ?";

        String[] visitColumns = {"uuid, startdate", "enddate"};
        String visitOrderBy = "startdate";
        String query = "SELECT DISTINCT v.uuid, v.startdate, v.enddate FROM tbl_visit v WHERE " +
                "(v.issubmitted == 1 OR v.enddate IS NOT NULL) AND " +
                "v.patientuuid = ? ORDER BY v.startdate";
        String[] visitArgs = {patientuuid};

        Cursor visitCursor = db.rawQuery(query, visitArgs);
        //Cursor visitCursor = db.query("tbl_visit", visitColumns, visitSelection, visitArgs, null, null, visitOrderBy);

        if (visitCursor.getCount() < 1) {
//            neverSeen();
        } else {

            if (visitCursor.moveToLast() && visitCursor != null) {
                do {
                    EncounterDAO encounterDAO = new EncounterDAO();
                    date = visitCursor.getString(visitCursor.getColumnIndexOrThrow("startdate"));
                    end_date = visitCursor.getString(visitCursor.getColumnIndexOrThrow("enddate"));
                    String visit_id = visitCursor.getString(visitCursor.getColumnIndexOrThrow("uuid"));
                    StringBuilder stringBuilder = new StringBuilder(date);
                    int a1 = stringBuilder.indexOf("T");
                    String dateFromDB = stringBuilder.substring(0, a1);
                    //check for current check_in visits only.
                    if (dateFromDB.equals(check_inDate)) {
                        visitList.add(visit_id);

                        String[] encounterIDArgs = {visit_id};

                        Cursor encounterCursor = db.query("tbl_encounter", null, encounterIDSelection, encounterIDArgs, null, null, null);
                        if (encounterCursor != null && encounterCursor.moveToFirst()) {
                            do {
                                if (encounterDAO.getEncounterTypeUuid("ENCOUNTER_VITALS").equalsIgnoreCase(encounterCursor.getString(encounterCursor.getColumnIndexOrThrow("encounter_type_uuid")))) {
                                    encountervitalsLocal = encounterCursor.getString(encounterCursor.getColumnIndexOrThrow("uuid"));
                                    encounterVitalList.add(encountervitalsLocal);
                                }
                                if (encounterDAO.getEncounterTypeUuid("ENCOUNTER_ADULTINITIAL").equalsIgnoreCase(encounterCursor.getString(encounterCursor.getColumnIndexOrThrow("encounter_type_uuid")))) {
                                    encounterlocalAdultintial = encounterCursor.getString(encounterCursor.getColumnIndexOrThrow("uuid"));
                                    encounterAdultList.add(encounterlocalAdultintial);
                                }

                            } while (encounterCursor.moveToNext());
                        }
                        encounterCursor.close();
                    }
                    // Called when we close app on vitals screen and Didn't select any complaints

                } while (visitCursor.moveToPrevious());

                SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    Collections.reverse(visitList);
                    Collections.reverse(encounterVitalList);
                    Collections.reverse(encounterAdultList);

                    Date formatted = currentDate.parse(date);
                    String visitDate = currentDate.format(formatted);
                    OldVisit(visitDate, visitList.get(position), end_date, "", ""/*encounterVitalList.get(position)*/, encounterAdultList.get(position));
                } catch (ParseException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            }
        }
        visitCursor.close();
        finish();
    }

    private void OldVisit(final String datetime, String visit_id, String end_datetime, String visitValue, String encounterVitalslocal, String encounterAdultIntialslocal) throws ParseException {

        final Boolean past_visit;

        past_visit = true;

        Intent visitSummary = new Intent(HomeActivity.this, VisitSummaryActivity.class);

        visitSummary.putExtra("visitUuid", visit_id);
        visitSummary.putExtra("patientUuid", "" + sessionManager.getPersionUUID());
        visitSummary.putExtra("encounterUuidVitals", encounterVitalslocal);
        visitSummary.putExtra("encounterUuidAdultIntial", encounterAdultIntialslocal);
        visitSummary.putExtra("name", "" + sessionManager.getUserName());
        visitSummary.putExtra("tag", "prior");
        visitSummary.putExtra("pastVisit", past_visit);
        visitSummary.putExtra("hasPrescription", "false");
        visitSummary.putExtra("fromOldVisit", true);
        startActivity(visitSummary);
    }


}
