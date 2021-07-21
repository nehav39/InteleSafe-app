package org.intelehealth.swasthyasamparkp.activities.visitSummaryActivity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringUtils;
import org.intelehealth.swasthyasamparkp.BuildConfig;
import org.intelehealth.swasthyasamparkp.R;
import org.intelehealth.swasthyasamparkp.activities.additionalDocumentsActivity.AdditionalDocumentsActivity;
import org.intelehealth.swasthyasamparkp.activities.complaintNodeActivity.ComplaintNodeActivity;
import org.intelehealth.swasthyasamparkp.activities.familyHistoryActivity.FamilyHistoryActivity;
import org.intelehealth.swasthyasamparkp.activities.homeActivity.HomeActivity;
import org.intelehealth.swasthyasamparkp.activities.homeActivity.Webview;
import org.intelehealth.swasthyasamparkp.activities.pastMedicalHistoryActivity.PastMedicalHistoryActivity;
import org.intelehealth.swasthyasamparkp.activities.patientSurveyActivity.PatientSurveyActivity;
import org.intelehealth.swasthyasamparkp.activities.physcialExamActivity.PhysicalExamActivity;
import org.intelehealth.swasthyasamparkp.activities.vitalActivity.VitalsActivity;
import org.intelehealth.swasthyasamparkp.app.AppConstants;
import org.intelehealth.swasthyasamparkp.database.dao.EncounterDAO;
import org.intelehealth.swasthyasamparkp.database.dao.ImagesDAO;
import org.intelehealth.swasthyasamparkp.database.dao.ObsDAO;
import org.intelehealth.swasthyasamparkp.database.dao.SyncDAO;
import org.intelehealth.swasthyasamparkp.database.dao.VisitAttributeListDAO;
import org.intelehealth.swasthyasamparkp.database.dao.VisitsDAO;
import org.intelehealth.swasthyasamparkp.knowledgeEngine.Node;
import org.intelehealth.swasthyasamparkp.models.ClsDoctorDetails;
import org.intelehealth.swasthyasamparkp.models.Patient;
import org.intelehealth.swasthyasamparkp.models.dto.ObsDTO;
import org.intelehealth.swasthyasamparkp.services.DownloadService;
import org.intelehealth.swasthyasamparkp.syncModule.SyncUtils;
import org.intelehealth.swasthyasamparkp.utilities.DateAndTimeUtils;
import org.intelehealth.swasthyasamparkp.utilities.FileUtils;
import org.intelehealth.swasthyasamparkp.utilities.LocaleManager;
import org.intelehealth.swasthyasamparkp.utilities.Logger;
import org.intelehealth.swasthyasamparkp.utilities.NetworkConnection;
import org.intelehealth.swasthyasamparkp.utilities.SessionManager;
import org.intelehealth.swasthyasamparkp.utilities.UuidDictionary;
import org.intelehealth.swasthyasamparkp.utilities.exception.DAOException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Created By: Prajwal Waingankar
 * Github: prajwalmw
 */
public class VisitSummaryActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = VisitSummaryActivity.class.getSimpleName();
    private WebView mWebView;
    private LinearLayout mLayout;
    TextView Help_Link_Whatsapp, download_Watsapp;
    TextView tvMentalHelpRequest, prescriptionDataFormat;
    String mHeight, mWeight, mBMI, mBP, mPulse, mTemp, mSPO2, mresp;

    boolean uploaded = false;
    boolean downloaded = false;

    Context context, context1;

    String patientUuid;
    String visitUuid;
    String state;
    String patientName;
    String intentTag;
    String visitUUID;
    String medicalAdvice_string = "";
    String medicalAdvice_HyperLink = "";
    String isSynedFlag = "";

    SQLiteDatabase db;

    Patient patient = new Patient();
    ObsDTO complaint = new ObsDTO();
    ObsDTO famHistory = new ObsDTO();
    ObsDTO patHistory = new ObsDTO();
    ObsDTO phyExam = new ObsDTO();
    ObsDTO height = new ObsDTO();
    ObsDTO weight = new ObsDTO();
    ObsDTO pulse = new ObsDTO();
    ObsDTO bpSys = new ObsDTO();
    ObsDTO bpDias = new ObsDTO();
    ObsDTO temperature = new ObsDTO();
    ObsDTO spO2 = new ObsDTO();
    ObsDTO resp = new ObsDTO();

    String diagnosisReturned = "";
    String rxReturned = "";
    String testsReturned = "";
    String adviceReturned = "";
    String doctorName = "";
    String additionalReturned = "";
    String followUpDate = "";

    ImageButton editVitals;
    ImageButton editComplaint;
    ImageView editPhysical;
    ImageButton editFamHist;
    ImageButton editMedHist;
    ImageButton editAddDocs;

    TextView nameView;
    TextView idView;
    TextView visitView;
    TextView heightView;
    TextView weightView;
    TextView pulseView;
    TextView bpView;
    TextView tempView;
    TextView spO2View;
    TextView bmiView;
    TextView complaintView;
    TextView famHistView;
    TextView patHistView;
    WebView physFindingsWebView;
    TextView mDoctorTitle;
    TextView mDoctorName;
    TextView mCHWname;
    //    //    Respiratory added by mahiti dev team
    TextView respiratory;
    TextView respiratoryText;
    TextView tempfaren;
    TextView tempcel;
    String medHistory;
    String baseDir;
    String filePathPhyExam;
    File obsImgdir;

    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;

    Button uploadButton;
    Button teleconsultationButton;
    TextView download_presc_Watsapp;
    //Button downloadButton;
    ArrayList<String> physicalExams;

    CardView diagnosisCard;
    CardView prescriptionCard;
    CardView medicalAdviceCard;
    CardView requestedTestsCard;
    CardView additionalCommentsCard;
    CardView followUpDateCard, cardView_prescription, download_cardview;

    TextView diagnosisTextView;
    TextView prescriptionTextView;
    TextView medicalAdviceTextView;
    TextView requestedTestsTextView;
    TextView additionalCommentsTextView;
    TextView followUpDateTextView;
    //added checkbox flag .m
    CheckBox flag;

    Boolean isPastVisit = false;
    Boolean isReceiverRegistered = false;
    Boolean isVisitSpecialityExists = false;

    public static final String FILTER = "io.intelehealth.client.activities.visit_summary_activity.REQUEST_PROCESSED";

    NetworkChangeReceiver receiver;
    private boolean isConnected = false;
    private Menu mymenu;
    MenuItem internetCheck = null;
    MenuItem endVisit_click = null;

    private boolean flag_upload = false;

    private RecyclerView mAdditionalDocsRecyclerView;
    private RecyclerView.LayoutManager mAdditionalDocsLayoutManager;

    private RecyclerView mPhysicalExamsRecyclerView;
    private RecyclerView.LayoutManager mPhysicalExamsLayoutManager;

    public static String prescriptionHeader1;
    public static String prescriptionHeader2;
    SharedPreferences mSharedPreference;
    boolean hasLicense = false;
    String mFileName = "config.json";
    public static String prescription1;
    public static String prescription2;
    SessionManager sessionManager;
    String encounterUuid;
    String encounterVitals;
    String encounterUuidAdultIntial;

    ProgressBar mProgressBar;
    TextView mProgressText;

    ImageButton additionalDocumentsDownlaod;
    ImageButton onExaminationDownload;

    DownloadPrescriptionService downloadPrescriptionService;
    private TextView additionalImageDownloadText;
    private TextView physcialExaminationDownloadText;

    ImageView ivPrescription;
    private String hasPrescription = "";
    private boolean isRespiratory = false;

    SyncUtils syncUtils = new SyncUtils();
    private boolean self;
    private CardView mAlertMessageCardView;
    private TextView mAlertMessageTextView;

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_visit_summary, menu);
        MenuItem menuItem = menu.findItem(R.id.summary_endVisit);

        internetCheck = menu.findItem(R.id.internet_icon);
        MenuItemCompat.getActionView(internetCheck);

        isNetworkAvailable(this);
        sessionManager = new SessionManager(this);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        mCHWname = findViewById(R.id.chw_details);
        mCHWname.setText(sessionManager.getChwname()); //session manager provider
        //Added Prescription Title from config.Json dynamically through sharedPreferences
        prescriptionHeader1 = sharedPreferences.getString("prescriptionTitle1", "");
        prescriptionHeader2 = sharedPreferences.getString("prescriptionTitle2", "");

        if (isPastVisit) menuItem.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }*/

    private BroadcastReceiver broadcastReceiverForIamgeDownlaod = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            onResume();
            physicalDoumentsUpdates();

        }
    };

    public void registerBroadcastReceiverDynamically() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("MY_BROADCAST_IMAGE_DOWNLAOD");
        registerReceiver(broadcastReceiverForIamgeDownlaod, filter);
    }

    public void registerDownloadPrescription() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("downloadprescription");
        registerReceiver(downloadPrescriptionService, filter);
    }


    @Override
    public void onBackPressed() {
        //do nothing
        //Use the buttons on the screen to navigate
        if (isFromOldVisit) finish();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiverForIamgeDownlaod);
        if (downloadPrescriptionService != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(downloadPrescriptionService);
        }
        super.onDestroy();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.summary_home: {
                if (flag_upload || isFromOldVisit) {
                    Intent i = new Intent(this, HomeActivity.class);
                    i.putExtra("from", "visit");
                    i.putExtra("username", "");
                    i.putExtra("password", "");
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle(R.string.click_submit);
                    alertDialogBuilder.setMessage(R.string.upload_before_home);

                    alertDialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                    Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                    positiveButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

                }
//                NavUtils.navigateUpFromSameTask(this);

                return true;
            }
            case R.id.summary_print: {
                try {
                    doWebViewPrint();
                } catch (ParseException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
                return true;
            }
            case R.id.summary_sms: {
                //     VisitSummaryActivityPermissionsDispatcher.sendSMSWithCheck(this);
                return true;
            }
            case R.id.summary_endVisit: {
                //meera
                if (downloaded) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage(R.string.end_visit_msg);
                    alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialogBuilder.setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            endVisit();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                } else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage(R.string.error_no_data);
                    alertDialogBuilder.setNeutralButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                    positiveButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

                }
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    boolean isFromOldVisit = false;
    TextView mental_visit_help_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sessionManager = new SessionManager(getApplicationContext());
        final Intent intent = this.getIntent(); // The intent was passed to the activity
        if (intent != null) {
            patientUuid = intent.getStringExtra("patientUuid");
            visitUuid = intent.getStringExtra("visitUuid");
            encounterVitals = intent.getStringExtra("encounterUuidVitals");
            encounterUuidAdultIntial = intent.getStringExtra("encounterUuidAdultIntial");
            mSharedPreference = this.getSharedPreferences(
                    "visit_summary", Context.MODE_PRIVATE);
            patientName = intent.getStringExtra("name");
            intentTag = intent.getStringExtra("tag");
            isPastVisit = intent.getBooleanExtra("pastVisit", false);
            hasPrescription = intent.getStringExtra("hasPrescription");
            isFromOldVisit = intent.getBooleanExtra("fromOldVisit", false);

            Set<String> selectedExams = sessionManager.getVisitSummary(patientUuid);
            if (physicalExams == null) physicalExams = new ArrayList<>();
            physicalExams.clear();
            if (selectedExams != null && !selectedExams.isEmpty()) {
                physicalExams.addAll(selectedExams);
            }
            self = intent.getBooleanExtra("self", false);


        }
        registerBroadcastReceiverDynamically();
        registerDownloadPrescription();
        if (!sessionManager.getLicenseKey().isEmpty())
            hasLicense = true;

        //Check for license key and load the correct config file
        try {
            JSONObject obj = null;
            if (hasLicense) {
                obj = new JSONObject(FileUtils.readFileRoot(mFileName, this)); //Load the config file

            } else {
                obj = new JSONObject(String.valueOf(FileUtils.encodeJSON(this, mFileName)));
            }
            prescription1 = obj.getString("presciptionHeader1");

            prescription2 = obj.getString("presciptionHeader2");

            //For AFI we are not using Respiratory Value
            if (obj.getBoolean("mResp")) {
                isRespiratory = true;
            } else {
                isRespiratory = false;
            }

        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        //setTitle(getString(R.string.title_activity_patient_summary));
        setTitle(sessionManager.getUserFirstName() + ": " + getString(R.string.assessment_visit_title)); // only First Name of User will Show here.

        db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_summary);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle(getString(R.string.visit_Summary_cardview));
        mLayout = findViewById(R.id.summary_layout);
        context = getApplicationContext();
        context1 = VisitSummaryActivity.this;

        mAlertMessageCardView = findViewById(R.id.cardView_alert);
        mAlertMessageTextView = findViewById(R.id.alert_tv);
        if (getIntent().hasExtra("AlertType")) {
            int type = getIntent().getIntExtra("AlertType", 1);
            mAlertMessageCardView.setVisibility(View.VISIBLE);
            mAlertMessageTextView.setText(getIntent().getStringExtra("MessageToPatient"));
            if (type == 1) { // healthy
                findViewById(R.id.alert_label_tv).setVisibility(View.GONE);
                findViewById(R.id.spec_1_v).setVisibility(View.GONE);
                mAlertMessageCardView.setCardBackgroundColor(getResources().getColor(R.color.green));
                // mAlertMessageTextView.setTextColor(getResources().getColor(R.color.white));
                //mAlertStatusImageView.setImageResource(R.drawable.healthy_icon);
            } else if (type == 2) { // mild
                mAlertMessageCardView.setCardBackgroundColor(getResources().getColor(R.color.amber));
                //mAlertMessageTextView.setTextColor(getResources().getColor(R.color.white));
                //mAlertStatusImageView.setImageResource(R.drawable.mild_icon);
            } else if (type == 3) { // moderate
                mAlertMessageCardView.setCardBackgroundColor(getResources().getColor(R.color.scale_2));
                //mAlertMessageTextView.setTextColor(getResources().getColor(R.color.white));
                //mAlertStatusImageView.setImageResource(R.drawable.moderate_icon);
            } else if (type == 4) { // severe
                mAlertMessageCardView.setCardBackgroundColor(getResources().getColor(R.color.red));
                //mAlertMessageTextView.setTextColor(getResources().getColor(R.color.white));
                //mAlertStatusImageView.setImageResource(R.drawable.severe_icon);
            }
        } else {
            mAlertMessageCardView.setVisibility(View.GONE);
        }

        //we can remove by data binding
        mAdditionalDocsRecyclerView = findViewById(R.id.recy_additional_documents);
        mPhysicalExamsRecyclerView = findViewById(R.id.recy_physexam);

        diagnosisCard = findViewById(R.id.cardView_diagnosis);
        prescriptionCard = findViewById(R.id.cardView_rx);
        medicalAdviceCard = findViewById(R.id.cardView_medical_advice);
        requestedTestsCard = findViewById(R.id.cardView_tests);
        additionalCommentsCard = findViewById(R.id.cardView_additional_comments);
        followUpDateCard = findViewById(R.id.cardView_follow_up_date);
        mDoctorTitle = findViewById(R.id.title_doctor);
        mDoctorName = findViewById(R.id.doctor_details);
        mDoctorTitle.setVisibility(View.GONE);
        mDoctorName.setVisibility(View.GONE);
        prescriptionDataFormat = findViewById(R.id.textView_content_prescription);
        cardView_prescription = findViewById(R.id.cardView_prescription);
        download_cardview = findViewById(R.id.download_cardview);
        download_presc_Watsapp = findViewById(R.id.download_presc_Watsapp);

        diagnosisTextView = findViewById(R.id.textView_content_diagnosis);
        prescriptionTextView = findViewById(R.id.textView_content_rx);
        medicalAdviceTextView = findViewById(R.id.textView_content_medical_advice);
        requestedTestsTextView = findViewById(R.id.textView_content_tests);
        additionalCommentsTextView = findViewById(R.id.textView_content_additional_comments);
        followUpDateTextView = findViewById(R.id.textView_content_follow_up_date);

        Help_Link_Whatsapp = findViewById(R.id.Help_Watsapp);
        Help_Link_Whatsapp.setPaintFlags(Help_Link_Whatsapp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        download_presc_Watsapp = findViewById(R.id.download_presc_Watsapp);
        download_presc_Watsapp.setPaintFlags(download_presc_Watsapp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Help_Link_Whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String phoneNumberWithCountryCode = "+917972269174";
//                String message =  getResources().getString(R.string.hello_my_name) + sessionManager.getUserName() +
//                        /*" from " + sessionManager.getState() + */ getResources().getString(R.string.need_some_assisstance);
//                startActivity(new Intent(Intent.ACTION_VIEW,
//                        Uri.parse(
//                                String.format("https://api.whatsapp.com/send?phone=%s&text=%s",
//                                        phoneNumberWithCountryCode, message))));
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:+917314821385"));
                startActivity(intent);
            }
        });


        tvMentalHelpRequest = findViewById(R.id.tv_mental_help_request);
        tvMentalHelpRequest.setOnClickListener(this);
        ivPrescription = findViewById(R.id.iv_prescription);

        if (hasPrescription.equalsIgnoreCase("true")) {
            ivPrescription.setImageDrawable(getResources().getDrawable(R.drawable.ic_prescription_green));
        }

        baseDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        obsImgdir = new File(AppConstants.IMAGE_PATH);

        flag = findViewById(R.id.flaggedcheckbox);
        EncounterDAO encounterDAO = new EncounterDAO();
        String emergencyUuid = "";
        try {
            emergencyUuid = encounterDAO.getEmergencyEncounters(visitUuid, encounterDAO.getEncounterTypeUuid("EMERGENCY"));
        } catch (DAOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        if (!emergencyUuid.isEmpty() || !emergencyUuid.equalsIgnoreCase("")) {
            flag.setChecked(true);
        }

        physicalDoumentsUpdates();

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        editVitals = findViewById(R.id.imagebutton_edit_vitals);
        editComplaint = findViewById(R.id.imagebutton_edit_complaint);
        editPhysical = findViewById(R.id.imagebutton_edit_physexam);
        editFamHist = findViewById(R.id.imagebutton_edit_famhist);
        editMedHist = findViewById(R.id.imagebutton_edit_pathist);
        editAddDocs = findViewById(R.id.imagebutton_edit_additional_document);
        uploadButton = findViewById(R.id.button_upload);

        //added by Prajwal for disabling the upload on old visit
        if (isFromOldVisit) {
            uploadButton.setVisibility(View.GONE);
            editPhysical.setVisibility(View.GONE);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            uploadButton.setVisibility(View.VISIBLE);
            editPhysical.setVisibility(View.VISIBLE);
        }

        mental_visit_help_text = findViewById(R.id.mental_visit_help_text);
        String teleconsult_request = "";
        if (sessionManager.getPatientCountry().equals("India")) {
            mental_visit_help_text.setText(getString(R.string.tele_consultant_text));
            tvMentalHelpRequest.setText(getString(R.string.teleconsult_request));
            teleconsult_request = getString(R.string.teleconsult_request);

        } else {
            mental_visit_help_text.setText(getString(R.string.Mental_health_support_text));
            tvMentalHelpRequest.setText(getString(R.string.mental_health_support));
            teleconsult_request = getString(R.string.mental_health_support);

        }

        SpannableString content = new SpannableString(teleconsult_request);
        content.setSpan(new UnderlineSpan(), 0, teleconsult_request.length(), 0);
        tvMentalHelpRequest.setText(content);


        //  downloadButton = findViewById(R.id.button_download);
        teleconsultationButton = findViewById(R.id.button_teleconsultation);

        //additionalDocumentsDownlaod = findViewById(R.id.imagebutton_download_additional_document);
        onExaminationDownload = findViewById(R.id.imagebutton_download_physexam);

        //additionalDocumentsDownlaod.setVisibility(View.GONE);

        physcialExaminationDownloadText = findViewById(R.id.physcial_examination_download);
        onExaminationDownload.setVisibility(View.GONE);

        //image download for additional documents
        additionalImageDownloadText = findViewById(R.id.additional_documents_download);
        Paint p = new Paint();
        p.setColor(Color.BLUE);
        additionalImageDownloadText.setPaintFlags(p.getColor());
        additionalImageDownloadText.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);


        additionalDocumentImagesDownload();

        //image download for physcialExamination documents
        physcialExaminationDownloadText.setPaintFlags(p.getColor());
        physcialExaminationDownloadText.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        physcialExaminationImagesDownload();


        //Replaced the downlaod button instance with teleconsu instance.
/*        teleconsultationButton.setEnabled(false);
        teleconsultationButton.setVisibility(View.GONE);
        if (isPastVisit) {
            editVitals.setVisibility(View.GONE);
            editComplaint.setVisibility(View.GONE);
            editPhysical.setVisibility(View.GONE);
            editFamHist.setVisibility(View.GONE);
            editMedHist.setVisibility(View.GONE);
            editAddDocs.setVisibility(View.GONE);
            uploadButton.setVisibility(View.GONE);
            invalidateOptionsMenu();
        } else {
            String visitIDorderBy = "startdate";
            String visitIDSelection = "uuid = ?";
            String[] visitIDArgs = {visitUuid};
            final Cursor visitIDCursor = db.query("tbl_visit", null, visitIDSelection, visitIDArgs, null, null, visitIDorderBy);
            if (visitIDCursor != null && visitIDCursor.moveToFirst() && visitIDCursor.getCount() > 0) {
                visitIDCursor.moveToFirst();
                visitUUID = visitIDCursor.getString(visitIDCursor.getColumnIndexOrThrow("uuid"));
            }
            if (visitIDCursor != null) visitIDCursor.close();
            if (visitUUID != null && !visitUUID.isEmpty()) {
                addDownloadButton();

            }

        }*/
        flag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    EncounterDAO encounterDAO = new EncounterDAO();
                    encounterDAO.setEmergency(visitUuid, isChecked);
                } catch (DAOException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (uploaded) {
                    Intent i = new Intent(VisitSummaryActivity.this, HomeActivity.class);
                    i.putExtra("from", "visit");
                    i.putExtra("username", "");
                    i.putExtra("password", "");
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                } else {
                    flag_upload = true;

                    //Adding default speciality as General Physician...
                    isVisitSpecialityExists = speciality_row_exist_check(visitUuid);
                    //we are getting this visitUuid in the intent.
                    VisitAttributeListDAO speciality_attributes = new VisitAttributeListDAO();
                    boolean isUpdateVisitDone = false;
                    try {
                        if (!isVisitSpecialityExists) {
                            isUpdateVisitDone = speciality_attributes
                                    .insertVisitAttributes(visitUuid, "General Physician");
                        }
                        Log.d("Update_Special_Visit", "Update_Special_Visit: " + isUpdateVisitDone);
                    } catch (DAOException e) {
                        e.printStackTrace();
                        Log.d("Update_Special_Visit", "Update_Special_Visit: " + isUpdateVisitDone);
                    }
                    //end...


                    if (flag.isChecked()) {
                        try {
                            EncounterDAO encounterDAO = new EncounterDAO();
                            encounterDAO.setEmergency(visitUuid, true);
                        } catch (DAOException e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }
                    }
                    if (patient.getOpenmrs_id() == null || patient.getOpenmrs_id().isEmpty()) {
                        String patientSelection = "uuid = ?";
                        String[] patientArgs = {String.valueOf(patient.getUuid())};
                        String table = "tbl_patient";
                        String[] columnsToReturn = {"openmrs_id"};
                        final Cursor idCursor = db.query(table, columnsToReturn, patientSelection, patientArgs, null, null, null);


                        if (idCursor.moveToFirst()) {
                            do {
                                patient.setOpenmrs_id(idCursor.getString(idCursor.getColumnIndex("openmrs_id")));
                            } while (idCursor.moveToNext());
                        }
                        idCursor.close();
                    }

                    if (patient.getOpenmrs_id() == null || patient.getOpenmrs_id().isEmpty()) {
                    }

                    if (visitUUID == null || visitUUID.isEmpty()) {
                        String visitIDSelection = "uuid = ?";
                        String[] visitIDArgs = {visitUuid};
                        final Cursor visitIDCursor = db.query("tbl_visit", null, visitIDSelection, visitIDArgs, null, null, null);
                        if (visitIDCursor != null && visitIDCursor.moveToFirst()) {
                            visitUUID = visitIDCursor.getString(visitIDCursor.getColumnIndexOrThrow("uuid"));
                        }
                        if (visitIDCursor != null)
                            visitIDCursor.close();
                    }
                    String[] columnsToReturn = {"startdate"};
                    String visitIDorderBy = "startdate";
                    String visitIDSelection = "uuid = ?";
                    String[] visitIDArgs = {visitUuid};
                    Cursor visitIDCursor1 = db.query("tbl_visit", columnsToReturn, visitIDSelection, visitIDArgs, null, null, visitIDorderBy);
                    visitIDCursor1.moveToLast();
                    String startDateTime = visitIDCursor1.getString(visitIDCursor1.getColumnIndexOrThrow("startdate"));
                    visitIDCursor1.close();

                    if (!flag.isChecked()) {
                        //
                    }

//                new Restaurant(VisitSummaryActivity.this, getString(R.string.uploading_to_doctor_notif), Snackbar.LENGTH_LONG)
//                        .setBackgroundColor(Color.BLACK)
//                        .setTextColor(Color.WHITE)
//                        .show();
                    // to set the Visit submit successfully in local DB to Push.
                    updateVisitSubmit();
                    if (NetworkConnection.isOnline(getApplication())) {
                        //Toast.makeText(context1, getResources().getString(R.string.upload_started), Toast.LENGTH_LONG).show();
                        uploadButton.setEnabled(false);
                        uploadButton.setAlpha(0.5f);
                        uploadButton.setText(getString(R.string.upload_started));
//                    AppConstants.notificationUtils.showNotifications(getString(R.string.visit_data_upload), getString(R.string.uploading_visit_data_notif), 3, VisitSummaryActivity.this);
                        SyncDAO syncDAO = new SyncDAO();
//                    ProgressDialog pd = new ProgressDialog(VisitSummaryActivity.this);
//                    pd.setTitle(getString(R.string.syncing_visitDialog));
//                    pd.show();
//                    pd.setCancelable(false);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//                            Added the 4 sec delay and then push data.For some reason doing immediately does not work
                                //Do something after 100ms
                                SyncUtils syncUtils = new SyncUtils();
                                boolean isSynced = syncUtils.syncForeground("visitSummary");
                                if (isSynced) {
//                                AppConstants.notificationUtils.DownloadDone(patientName + " " + getString(R.string.visit_data_upload), getString(R.string.visit_uploaded_successfully), 3, VisitSummaryActivity.this);
                                    //
                                    showVisitID();
                                    endVisit();
                                    if (!isFinishing()) {
                                        showPopup();
                                    }
                                    uploadButton.setEnabled(true);
                                    uploadButton.setAlpha(1f);
                                    uploadButton.setText(getString(R.string.go_to_home));
                                    editPhysical.setVisibility(View.GONE);

                                } else {
                                    AppConstants.notificationUtils.DownloadDone(patientName + " " + getString(R.string.visit_data_failed), getString(R.string.visit_uploaded_failed), 3, VisitSummaryActivity.this);
                                }
                                uploaded = true;
//                            pd.dismiss();
//                            Toast.makeText(VisitSummaryActivity.this, getString(R.string.upload_completed), Toast.LENGTH_SHORT).show();
                            }
                        }, 4000);
                    } else {
                        AppConstants.notificationUtils.DownloadDone(patientName + " " + getString(R.string.visit_data_failed), getString(R.string.visit_uploaded_failed), 3, VisitSummaryActivity.this);
                    /*uploadButton.setEnabled(false);
                    uploadButton.setAlpha(0.5f);*/
                    }
                }
            }

        });

        if (intentTag != null && intentTag.equals("prior")) {
            uploadButton.setEnabled(false);
        }


        downloadDoctorDetails();
        queryData(String.valueOf(patientUuid));
        nameView = findViewById(R.id.textView_name_value);

        //OpenMRS Id
        idView = findViewById(R.id.textView_id_value);
        visitView = findViewById(R.id.textView_visit_value);
        if (patient.getOpenmrs_id() != null && !patient.getOpenmrs_id().isEmpty()) {
            idView.setText(patient.getOpenmrs_id());
        } else {
            idView.setText(getString(R.string.patient_not_registered));
        }

        nameView.setText(patientName);

        heightView = findViewById(R.id.textView_height_value);
        weightView = findViewById(R.id.textView_weight_value);
        pulseView = findViewById(R.id.textView_pulse_value);
        bpView = findViewById(R.id.textView_bp_value);
        tempView = findViewById(R.id.textView_temp_value);

        tempfaren = findViewById(R.id.textView_temp_faren);
        tempcel = findViewById(R.id.textView_temp);
        try {
            JSONObject obj = null;
            if (hasLicense) {
                obj = new JSONObject(FileUtils.readFileRoot(mFileName, VisitSummaryActivity.this)); //Load the config file
            } else {
                obj = new JSONObject(String.valueOf(FileUtils.encodeJSON(VisitSummaryActivity.this, mFileName)));
            }
            if (obj.getBoolean("mCelsius")) {
                tempcel.setVisibility(View.VISIBLE);
                tempfaren.setVisibility(View.GONE);
                tempView.setText(temperature.getValue());
            } else if (obj.getBoolean("mFahrenheit")) {
                tempfaren.setVisibility(View.VISIBLE);
                tempcel.setVisibility(View.GONE);
                if (temperature.getValue() != null && !temperature.getValue().isEmpty()) {
                    tempView.setText(convertCtoF(temperature.getValue()));
                }
            }
        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        spO2View = findViewById(R.id.textView_pulseox_value);
        respiratory = findViewById(R.id.textView_respiratory_value);
        respiratoryText = findViewById(R.id.textView_respiratory);
        bmiView = findViewById(R.id.textView_bmi_value);
        complaintView = findViewById(R.id.textView_content_complaint);
        famHistView = findViewById(R.id.textView_content_famhist);
        patHistView = findViewById(R.id.textView_content_pathist);
        physFindingsWebView = findViewById(R.id.textView_content_physexam);

        // Impostazioni della WebView.
        final WebSettings webSettings = physFindingsWebView.getSettings();
        // Set the font size (in sp).
        webSettings.setDefaultFontSize(13);
        physFindingsWebView.setBackgroundColor(Color.TRANSPARENT);
        physFindingsWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);

        if (isRespiratory) {
            respiratoryText.setVisibility(View.VISIBLE);
            respiratory.setVisibility(View.VISIBLE);
        } else {
            respiratoryText.setVisibility(View.GONE);
            respiratory.setVisibility(View.GONE);
        }

        heightView.setText(height.getValue());
        weightView.setText(weight.getValue());
        pulseView.setText(pulse.getValue());

        String bpText = bpSys.getValue() + "/" + bpDias.getValue();
        if (bpText.equals("/")) {
            bpView.setText("");
        } else {
            bpView.setText(bpText);
        }

        Log.d(TAG, "onCreate: " + weight.getValue());
        String mWeight = weight.getValue();
        String mHeight = height.getValue();
        if ((mHeight != null && mWeight != null) && !mHeight.isEmpty() && !mWeight.isEmpty()) {
            double numerator = Double.parseDouble(mWeight) * 10000;
            double denominator = Double.parseDouble(mHeight) * Double.parseDouble(mHeight);
            double bmi_value = numerator / denominator;
            mBMI = String.format(Locale.ENGLISH, "%.2f", bmi_value);
        } else {
            mBMI = "";
        }
        patHistory.setValue(medHistory);

        bmiView.setText(mBMI);
//        tempView.setText(temperature.getValue());
        //    Respiratory added by mahiti dev team
        respiratory.setText(resp.getValue());
        spO2View.setText(spO2.getValue());
        if (complaint.getValue() != null)
            complaintView.setText(Html.fromHtml(complaint.getValue()));
        if (famHistory.getValue() != null)
            famHistView.setText(Html.fromHtml(famHistory.getValue()));
        if (patHistory.getValue() != null)
            patHistView.setText(Html.fromHtml(patHistory.getValue()));
        if (phyExam.getValue() != null) {
            String physicalExamStr = phyExam.getValue().replaceAll("<b>General exams: </b>", self ? "<b>Self Assessment: </b>" : "<b>Doctor Visit: </b>");
            Log.v(TAG, physicalExamStr);
            physFindingsWebView.loadDataWithBaseURL(null, physicalExamStr, "text/html", "utf-8", null);//;setText(Html.fromHtml(physicalExamStr));
        }


        editVitals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(VisitSummaryActivity.this, VitalsActivity.class);
                intent1.putExtra("patientUuid", patientUuid);
                intent1.putExtra("visitUuid", visitUuid);
                intent1.putExtra("encounterUuidVitals", encounterVitals);
                intent1.putExtra("encounterUuidAdultIntial", encounterUuidAdultIntial);
                intent1.putExtra("name", patientName);
                intent1.putExtra("tag", "edit");
                startActivity(intent1);
            }
        });

        editFamHist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder famHistDialog = new AlertDialog.Builder(VisitSummaryActivity.this);
                famHistDialog.setTitle(getString(R.string.visit_summary_family_history));
                final LayoutInflater inflater = getLayoutInflater();
                View convertView = inflater.inflate(R.layout.dialog_edit_entry, null);
                famHistDialog.setView(convertView);

                final TextView famHistText = convertView.findViewById(R.id.textView_entry);
                if (famHistory.getValue() != null)
                    famHistText.setText(Html.fromHtml(famHistory.getValue()));
                famHistText.setEnabled(false);

                famHistDialog.setPositiveButton(getString(R.string.generic_manual_entry), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final AlertDialog.Builder textInput = new AlertDialog.Builder(VisitSummaryActivity.this);
                        textInput.setTitle(R.string.question_text_input);
                        final EditText dialogEditText = new EditText(VisitSummaryActivity.this);
                        if (famHistory.getValue() != null)
                            dialogEditText.setText(Html.fromHtml(famHistory.getValue()));
                        else
                            dialogEditText.setText("");
                        textInput.setView(dialogEditText);
                        textInput.setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //famHistory.setValue(dialogEditText.getText().toString());
                                famHistory.setValue(dialogEditText.getText().toString().replace("\n", "<br>"));

                                if (famHistory.getValue() != null) {
                                    famHistText.setText(Html.fromHtml(famHistory.getValue()));
                                    famHistView.setText(Html.fromHtml(famHistory.getValue()));
                                }
                                updateDatabase(famHistory.getValue(), UuidDictionary.RHK_FAMILY_HISTORY_BLURB);
                                dialog.dismiss();
                            }
                        });
                        textInput.setNegativeButton(R.string.generic_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        textInput.show();
                        dialogInterface.dismiss();
                    }
                });

                famHistDialog.setNeutralButton(getString(R.string.generic_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                famHistDialog.setNegativeButton(R.string.generic_erase_redo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Intent intent1 = new Intent(VisitSummaryActivity.this, FamilyHistoryActivity.class);
                        intent1.putExtra("patientUuid", patientUuid);
                        intent1.putExtra("visitUuid", visitUuid);
                        intent1.putExtra("encounterUuidVitals", encounterVitals);
                        intent1.putExtra("encounterUuidAdultIntial", encounterUuidAdultIntial);
                        intent1.putExtra("name", patientName);
                        intent1.putExtra("tag", "edit");
                        startActivity(intent1);
                        dialogInterface.dismiss();
                    }
                });

//                famHistDialog.show();
                AlertDialog alertDialog = famHistDialog.create();
                alertDialog.show();
                Button pb = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                pb.setTextColor(getResources().getColor((R.color.colorPrimary)));
                pb.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

                Button nb = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                nb.setTextColor(getResources().getColor((R.color.colorPrimary)));
                nb.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

                Button neutralb = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                neutralb.setTextColor(getResources().getColor((R.color.colorPrimary)));
                neutralb.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            }
        });

        editComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder complaintDialog = new AlertDialog.Builder(VisitSummaryActivity.this);
                complaintDialog.setTitle(getString(R.string.visit_summary_complaint));
                final LayoutInflater inflater = getLayoutInflater();
                View convertView = inflater.inflate(R.layout.dialog_edit_entry, null);
                complaintDialog.setView(convertView);

                final TextView complaintText = convertView.findViewById(R.id.textView_entry);
                if (complaint.getValue() != null) {
                    complaintText.setText(Html.fromHtml(complaint.getValue()));
                }
                complaintText.setEnabled(false);

                complaintDialog.setPositiveButton(getString(R.string.generic_manual_entry), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final AlertDialog.Builder textInput = new AlertDialog.Builder(VisitSummaryActivity.this);
                        textInput.setTitle(R.string.question_text_input);
                        final EditText dialogEditText = new EditText(VisitSummaryActivity.this);
                        if (complaint.getValue() != null) {
                            dialogEditText.setText(Html.fromHtml(complaint.getValue()));
                        } else {
                            dialogEditText.setText("");
                        }
                        textInput.setView(dialogEditText);
                        textInput.setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                complaint.setValue(dialogEditText.getText().toString().replace("\n", "<br>"));
                                if (complaint.getValue() != null) {
                                    complaintText.setText(Html.fromHtml(complaint.getValue()));
                                    complaintView.setText(Html.fromHtml(complaint.getValue()));
                                }
                                updateDatabase(complaint.getValue(), UuidDictionary.CURRENT_COMPLAINT);
                                dialog.dismiss();
                            }
                        });
                        textInput.setNeutralButton(R.string.generic_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        textInput.show();
                        dialogInterface.dismiss();
                    }
                });

                complaintDialog.setNegativeButton(getString(R.string.generic_erase_redo), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Deleting the old image in physcial examination
                        if (obsImgdir.exists()) {
                            ImagesDAO imagesDAO = new ImagesDAO();

                            try {
                                List<String> imageList = imagesDAO.getImages(encounterUuidAdultIntial, UuidDictionary.COMPLEX_IMAGE_PE);
                                for (String obsImageUuid : imageList) {
                                    String imageName = obsImageUuid + ".jpg";
                                    new File(obsImgdir, imageName).deleteOnExit();
                                }
                                imagesDAO.deleteConceptImages(encounterUuidAdultIntial, UuidDictionary.COMPLEX_IMAGE_PE);
                            } catch (DAOException e1) {
                                FirebaseCrashlytics.getInstance().recordException(e1);
                            }
                        }

                        Intent intent1 = new Intent(VisitSummaryActivity.this, ComplaintNodeActivity.class);
                        intent1.putExtra("patientUuid", patientUuid);
                        intent1.putExtra("visitUuid", visitUuid);
                        intent1.putExtra("encounterUuidVitals", encounterVitals);
                        intent1.putExtra("encounterUuidAdultIntial", encounterUuidAdultIntial);
                        intent1.putExtra("name", patientName);
                        intent1.putExtra("tag", "edit");
                        startActivity(intent1);
                        dialogInterface.dismiss();
                    }
                });

                complaintDialog.setNeutralButton(R.string.generic_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                //complaintDialog.show();
                AlertDialog alertDialog = complaintDialog.create();
                alertDialog.show();
                Button pb = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                pb.setTextColor(getResources().getColor((R.color.colorPrimary)));
                pb.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

                Button nb = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                nb.setTextColor(getResources().getColor((R.color.colorPrimary)));
                nb.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

                Button neutralb = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                neutralb.setTextColor(getResources().getColor((R.color.colorPrimary)));
                neutralb.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            }
        });

        editPhysical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder physicalDialog = new AlertDialog.Builder(VisitSummaryActivity.this);
                physicalDialog.setTitle(getString(R.string.visit_summary_on_examination));
                final LayoutInflater inflater = getLayoutInflater();
                View convertView = inflater.inflate(R.layout.dialog_edit_entry, null);
                physicalDialog.setView(convertView);

                final TextView physicalText = convertView.findViewById(R.id.textView_entry);
                if (phyExam.getValue() != null) {
                    String physicalExamStr = phyExam.getValue().replaceAll("<b>General exams: </b>", "<b>Self Assessment: </b>");
                    physicalText.setText(Html.fromHtml(physicalExamStr));
                }
                physicalText.setEnabled(false);

                /*physicalDialog.setPositiveButton(getString(R.string.generic_manual_entry), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final AlertDialog.Builder textInput = new AlertDialog.Builder(VisitSummaryActivity.this);
                        textInput.setTitle(R.string.question_text_input);
                        final EditText dialogEditText = new EditText(VisitSummaryActivity.this);
                        if (phyExam.getValue() != null) {
                            String physicalExamStr = phyExam.getValue().replaceAll("<b>General exams: </b>", "<b>Self Assessment: </b>");
                            dialogEditText.setText(Html.fromHtml(physicalExamStr));
                        } else
                            dialogEditText.setText("");
                        textInput.setView(dialogEditText);
                        textInput.setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                phyExam.setValue(dialogEditText.getText().toString().replace("\n", "<br>"));
                                String b = "";
                                if (phyExam.getValue() != null) {
                                    String physicalExamStr = phyExam.getValue().replaceAll("<b>General exams: </b>", self ? "<b>Self Assessment: </b>" : "<b>Doctor Visit: </b>");
                                    physicalText.setText(Html.fromHtml(physicalExamStr));
                                    physFindingsWebView.loadDataWithBaseURL(null, physicalExamStr, "text/html", "utf-8", null);//setText(Html.fromHtml(physicalExamStr));
                                    b = phyExam.getValue().replaceAll("Self Assessment: ", "<b>General exams: </b> ");
                                }

                                updateDatabase(b, UuidDictionary.PHYSICAL_EXAMINATION);
                                dialog.dismiss();
                            }
                        });
                        textInput.setNegativeButton(R.string.generic_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        textInput.show();
                        dialogInterface.dismiss();
                    }
                });*/

                physicalDialog.setNegativeButton(getString(R.string.generic_erase_redo), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (obsImgdir.exists()) {
                            ImagesDAO imagesDAO = new ImagesDAO();

                            try {
                                List<String> imageList = imagesDAO.getImages(encounterUuidAdultIntial, UuidDictionary.COMPLEX_IMAGE_PE);
                                for (String obsImageUuid : imageList) {
                                    String imageName = obsImageUuid + ".jpg";
                                    new File(obsImgdir, imageName).deleteOnExit();
                                }
                                imagesDAO.deleteConceptImages(encounterUuidAdultIntial, UuidDictionary.COMPLEX_IMAGE_PE);
                            } catch (DAOException e1) {
                                FirebaseCrashlytics.getInstance().recordException(e1);
                            }
                        }
                        Intent intent1 = new Intent(VisitSummaryActivity.this, PhysicalExamActivity.class);
                        intent1.putExtra("patientUuid", patientUuid);
                        intent1.putExtra("visitUuid", visitUuid);
                        intent1.putExtra("encounterUuidVitals", encounterVitals);
                        intent1.putExtra("encounterUuidAdultIntial", encounterUuidAdultIntial);
                        intent1.putExtra("name", patientName);
                        intent1.putExtra("tag", "edit");
                        //    intent1.putStringArrayListExtra("exams", physicalExams);
                        for (String string : physicalExams)
                            Log.i(TAG, "onClick: " + string);
                        startActivity(intent1);
                        dialogInterface.dismiss();
                    }
                });

                physicalDialog.setNeutralButton(R.string.generic_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = physicalDialog.create();
                alertDialog.show();
                Button pb = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                pb.setTextColor(getResources().getColor((R.color.colorPrimary)));
                pb.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

                Button nb = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                nb.setTextColor(getResources().getColor((R.color.colorPrimary)));
                nb.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

                Button neutralb = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                neutralb.setTextColor(getResources().getColor((R.color.colorPrimary)));
                neutralb.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            }
        });

        editMedHist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder historyDialog = new AlertDialog.Builder(VisitSummaryActivity.this);
                historyDialog.setTitle(getString(R.string.visit_summary_medical_history));
                final LayoutInflater inflater = getLayoutInflater();
                View convertView = inflater.inflate(R.layout.dialog_edit_entry, null);
                historyDialog.setView(convertView);

                final TextView historyText = convertView.findViewById(R.id.textView_entry);
                if (patHistory.getValue() != null)
                    historyText.setText(Html.fromHtml(patHistory.getValue()));
                historyText.setEnabled(false);

                historyDialog.setPositiveButton(getString(R.string.generic_manual_entry), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final AlertDialog.Builder textInput = new AlertDialog.Builder(VisitSummaryActivity.this);
                        textInput.setTitle(R.string.question_text_input);
                        final EditText dialogEditText = new EditText(VisitSummaryActivity.this);
                        if (patHistory.getValue() != null)
                            dialogEditText.setText(Html.fromHtml(patHistory.getValue()));
                        else
                            dialogEditText.setText("");
                        textInput.setView(dialogEditText);
                        textInput.setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //patHistory.setValue(dialogEditText.getText().toString());
                                patHistory.setValue(dialogEditText.getText().toString().replace("\n", "<br>"));

                                if (patHistory.getValue() != null) {
                                    historyText.setText(Html.fromHtml(patHistory.getValue()));
                                    patHistView.setText(Html.fromHtml(patHistory.getValue()));
                                }
                                updateDatabase(patHistory.getValue(), UuidDictionary.RHK_MEDICAL_HISTORY_BLURB);
                                dialog.dismiss();
                            }
                        });
                        textInput.setNegativeButton(R.string.generic_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        textInput.show();
                        dialogInterface.dismiss();
                    }
                });

                historyDialog.setNegativeButton(getString(R.string.generic_erase_redo), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent1 = new Intent(VisitSummaryActivity.this, PastMedicalHistoryActivity.class);
                        intent1.putExtra("patientUuid", patientUuid);
                        intent1.putExtra("visitUuid", visitUuid);
                        intent1.putExtra("encounterUuidVitals", encounterVitals);
                        intent1.putExtra("encounterUuidAdultIntial", encounterUuidAdultIntial);
                        intent1.putExtra("name", patientName);
                        intent1.putExtra("tag", "edit");
                        startActivity(intent1);
                        dialogInterface.dismiss();
                    }
                });

                historyDialog.setNeutralButton(R.string.generic_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

//                historyDialog.show();
                AlertDialog alertDialog = historyDialog.create();
                alertDialog.show();
                Button pb = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                pb.setTextColor(getResources().getColor((R.color.colorPrimary)));
                pb.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

                Button nb = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                nb.setTextColor(getResources().getColor((R.color.colorPrimary)));
                nb.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

                Button neutralb = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
                neutralb.setTextColor(getResources().getColor((R.color.colorPrimary)));
                neutralb.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            }
        });

        editAddDocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addDocs = new Intent(VisitSummaryActivity.this, AdditionalDocumentsActivity.class);
                addDocs.putExtra("patientUuid", patientUuid);
                addDocs.putExtra("visitUuid", visitUuid);
                addDocs.putExtra("encounterUuidVitals", encounterVitals);
                addDocs.putExtra("encounterUuidAdultIntial", encounterUuidAdultIntial);
                startActivity(addDocs);
            }
        });


        teleconsultationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder textEditInput = new AlertDialog.Builder(VisitSummaryActivity.this);
                textEditInput.setTitle("Reason for Teleconsultation?");
                final EditText dialogEditText = new EditText(VisitSummaryActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                dialogEditText.setLayoutParams(layoutParams);
                textEditInput.setView(dialogEditText);

                textEditInput.setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialogEditText.getText();

                        final AlertDialog.Builder ok_clicked = new AlertDialog.Builder(VisitSummaryActivity.this);
                        ok_clicked.setMessage("We received your request, we will get back to you as soon as possible.\n -Thank you!");
                        ok_clicked.setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(VisitSummaryActivity.this, "Request Submitted", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });

                        AlertDialog alertDialog = ok_clicked.create();
                        alertDialog.show();

                        Button pb = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        pb.setTextColor(getResources().getColor((R.color.colorPrimary)));
                        pb.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                    }
                });
                textEditInput.setNegativeButton(R.string.generic_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                //textEditInput.show();

                AlertDialog alertDialog = textEditInput.create();
                alertDialog.setView(dialogEditText, 20, 20, 20, 0);
                // alertDialog.setCancelable(false);
                alertDialog.show();
                Button pb = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                pb.setTextColor(getResources().getColor((R.color.colorPrimary)));
                pb.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

                Button nb = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                nb.setTextColor(getResources().getColor((R.color.colorPrimary)));
                nb.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            }
        });

/*
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (NetworkConnection.isOnline(getApplication())) {
                    Toast.makeText(context, getResources().getString(R.string.downloading), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, getResources().getString(R.string.prescription_not_downloaded_check_internet), Toast.LENGTH_LONG).show();
                }

                SyncUtils syncUtils = new SyncUtils();
                syncUtils.syncForeground("downloadPrescription");
//                AppConstants.notificationUtils.DownloadDone(getString(R.string.download_from_doctor), getString(R.string.prescription_downloaded), 3, VisitSummaryActivity.this);
                uploaded = true;
//                ProgressDialog pd = new ProgressDialog(VisitSummaryActivity.this);
//                pd.setTitle(getString(R.string.downloading_prescription));
//                pd.show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadPrescription();
//                        pd.dismiss();
                    }
                }, 5000);
            }
        });
*/
     /*   additionalDocumentsDownlaod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload(UuidDictionary.COMPLEX_IMAGE_AD);
            }
        }); */
        onExaminationDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload(UuidDictionary.COMPLEX_IMAGE_PE);
            }
        });

        doQuery();

        //Button click....
        download_presc_Watsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Url = BuildConfig.BASE_URL +
                        "preApi/i.jsp?v=" +
                        visitUuid + "&pid=" + patient.getOpenmrs_id();

                Intent download_intent = new Intent(Intent.ACTION_VIEW);
                download_intent.setData(Uri.parse(Url));
                startActivity(download_intent);
                Log.d("url", "url: " + Url);

               /* Intent download = new Intent(VisitSummaryActivity.this, Webview.class);
                download.putExtra("Base_Url", BuildConfig.BASE_URL);
                download.putExtra("visitUuid", visitUuid);
                download.putExtra("openMRS_id", patient.getOpenmrs_id());
                Log.d("vdownload", "visituuid: "+ visitUuid +
                        "openmrs: "+ patient.getOpenmrs_id());
                startActivity(download);*/
            }
        });
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.updateResources(base, new SessionManager(base).getAppLanguage()));
    }


    private void showPopup() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VisitSummaryActivity.this);
        alertDialogBuilder.setTitle(getResources().getString(R.string.thank_you));
        alertDialogBuilder.setMessage(getResources().getString(R.string.your_checkin_is_uploaded_successfully));

//        alertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
        alertDialogBuilder.setPositiveButton(getResources().getString(R.string.generic_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                sessionManager.setFirstCheckin("true");
                // Modified by Venu N on 06/04/2020.
               /* Intent i = new Intent(context, HomeActivity.class);
                i.putExtra("from", "visitSummary");
                i.putExtra("username", "");
                i.putExtra("password", "");
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);*/
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private String convertCtoF(String temperature) {

        String result = "";
        double a = Double.parseDouble(String.valueOf(temperature));
        Double b = a * 9 / 5 + 32;

        DecimalFormat dtime = new DecimalFormat("#.##");
        b = Double.valueOf(dtime.format(b));

        result = String.valueOf(b);
        return result;

    }

    private void showVisitID() {

        if (visitUUID != null && !visitUUID.isEmpty()) {
            String hideVisitUUID = visitUUID;
            hideVisitUUID = hideVisitUUID.substring(hideVisitUUID.length() - 4, hideVisitUUID.length());
            visitView.setText("XXXX" + hideVisitUUID);
        }

    }


    private void doQuery() {

        if (visitUUID != null && !visitUUID.isEmpty()) {

            String query = "SELECT   a.uuid, a.sync " +
                    "FROM tbl_visit a " +
                    "WHERE a.uuid = '" + visitUUID + "'";

            final Cursor cursor = db.rawQuery(query, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        isSynedFlag = cursor.getString(cursor.getColumnIndexOrThrow("sync"));
                    } while (cursor.moveToNext());
                }
            }
            if (cursor != null) {
                cursor.close();
            }

            Log.e("ISSYNCED==", isSynedFlag);

            if (!isSynedFlag.equalsIgnoreCase("0")) {
                String hideVisitUUID = visitUUID;
                hideVisitUUID = hideVisitUUID.substring(hideVisitUUID.length() - 4, hideVisitUUID.length());
                visitView.setText("XXXX" + hideVisitUUID);
            } else {
                visitView.setText(getResources().getString(R.string.visit_not_uploaded));
            }
        } else {
            if (visitUuid != null && !visitUuid.isEmpty()) {
                String hideVisitUUID = visitUuid;
                hideVisitUUID = hideVisitUUID.substring(hideVisitUUID.length() - 4, hideVisitUUID.length());
                visitView.setText("XXXX" + hideVisitUUID);
//              visitView.setText("----");
            }
        }
    }

    private void physcialExaminationImagesDownload() {
        ImagesDAO imagesDAO = new ImagesDAO();
        try {
            List<String> imageList = imagesDAO.isImageListObsExists(encounterUuidAdultIntial, UuidDictionary.COMPLEX_IMAGE_PE);
            for (String images : imageList) {
                if (imagesDAO.isLocalImageUuidExists(images))
                    physcialExaminationDownloadText.setVisibility(View.GONE);
                else
                    physcialExaminationDownloadText.setVisibility(View.VISIBLE);
            }
        } catch (DAOException e) {
            e.printStackTrace();
        }
        physcialExaminationDownloadText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload(UuidDictionary.COMPLEX_IMAGE_PE);
                physcialExaminationDownloadText.setVisibility(View.GONE);
            }
        });
    }

    private void additionalDocumentImagesDownload() {
        ImagesDAO imagesDAO = new ImagesDAO();
        try {
            List<String> imageList = imagesDAO.isImageListObsExists(encounterUuidAdultIntial, UuidDictionary.COMPLEX_IMAGE_AD);
            for (String images : imageList) {
                if (imagesDAO.isLocalImageUuidExists(images))
                    additionalImageDownloadText.setVisibility(View.GONE);
                else
                    additionalImageDownloadText.setVisibility(View.VISIBLE);
            }
        } catch (DAOException e) {
            e.printStackTrace();
        }
        additionalImageDownloadText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload(UuidDictionary.COMPLEX_IMAGE_AD);
                additionalImageDownloadText.setVisibility(View.GONE);
            }
        });

    }

    private void physicalDoumentsUpdates() {

        ImagesDAO imagesDAO = new ImagesDAO();
        ArrayList<String> fileuuidList = new ArrayList<String>();
        ArrayList<File> fileList = new ArrayList<File>();
        try {
            fileuuidList = imagesDAO.getImageUuid(encounterUuidAdultIntial, UuidDictionary.COMPLEX_IMAGE_PE);
            for (String fileuuid : fileuuidList) {
                String filename = AppConstants.IMAGE_PATH + fileuuid + ".jpg";
                if (new File(filename).exists()) {
                    fileList.add(new File(filename));
                }
            }
            HorizontalAdapter horizontalAdapter = new HorizontalAdapter(fileList, this);
            mPhysicalExamsLayoutManager = new LinearLayoutManager(VisitSummaryActivity.this, LinearLayoutManager.HORIZONTAL, false);
            mPhysicalExamsRecyclerView.setLayoutManager(mPhysicalExamsLayoutManager);
            mPhysicalExamsRecyclerView.setAdapter(horizontalAdapter);
        } catch (DAOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        } catch (Exception file) {
            Logger.logD(TAG, file.getMessage());
        }
    }

    private void startDownload(String imageType) {

        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra("patientUuid", patientUuid);
        intent.putExtra("visitUuid", visitUuid);
        intent.putExtra("encounterUuidVitals", encounterVitals);
        intent.putExtra("encounterUuidAdultIntial", encounterUuidAdultIntial);
        intent.putExtra("ImageType", imageType);
        startService(intent);

    }


    private String stringToWeb(String input) {
        String formatted = "";
        if (input != null && !input.isEmpty()) {

            String para_open = "<p style=\"font-size:11pt; margin: 0px; padding: 0px;\">";
            String para_close = "</p>";
            formatted = para_open + Node.big_bullet +
                    input.replaceAll("\n", para_close + para_open + Node.big_bullet)
                    + para_close;
        }

        return formatted;
    }

    private void doWebViewPrint() throws ParseException {
        // Create a WebView object specifically for printing
        WebView webView = new WebView(VisitSummaryActivity.this); //Use VisitSummary.this instead of this - added by Prajwal.
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
               /* Log.i("Patient WebView", "page finished loading " + url);
                createWebPrintJob(view);*/
                int webview_heightContent = view.getContentHeight();
                Log.d("variable i", "variable i: " + webview_heightContent);
                createWebPrintJob_Button(view, webview_heightContent);
                mWebView = null;
            }
        });

        String mPatientName = patient.getFirst_name() + " " + (patient.getMiddle_name() != null ? patient.getMiddle_name() : "") + " " + patient.getLast_name();
        String mPatientOpenMRSID = patient.getOpenmrs_id();
        String mPatientDob = patient.getDate_of_birth();
        String mAddress = (patient.getAddress1() != null ? patient.getAddress1() : "") + (patient.getAddress2() != null ? "\n" + patient.getAddress2() : ""); // modified by the Venu N on 02/04/2020.
        String mCityState = patient.getCity_village();
        String mPhone = patient.getPhone_number();
        String mState = patient.getState_province();
        String mCountry = patient.getCountry();

        String mSdw = patient.getSdw();
        String mOccupation = patient.getOccupation();
        String mGender = patient.getGender() != null ? patient.getGender() : ""; // Modified by venu N on 02/04/2020.

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        String[] columnsToReturn = {"startdate"};
        String visitIDorderBy = "startdate";
        String visitIDSelection = "uuid = ?";
        String[] visitIDArgs = {visitUuid};
        db = AppConstants.inteleHealthDatabaseHelper.getWritableDatabase();
        final Cursor visitIDCursor = db.query("tbl_visit", columnsToReturn, visitIDSelection, visitIDArgs, null, null, visitIDorderBy);
        visitIDCursor.moveToLast();
        String startDateTime = visitIDCursor.getString(visitIDCursor.getColumnIndexOrThrow("startdate"));
        visitIDCursor.close();
        String mDate = DateAndTimeUtils.SimpleDatetoLongDate(startDateTime);

        String mPatHist = patHistory.getValue();
        if (mPatHist == null) {
            mPatHist = "";
        }
        String mFamHist = famHistory.getValue();
        if (mFamHist == null) {
            mFamHist = "";
        }
        mHeight = height.getValue();
        mWeight = weight.getValue();
        mBP = bpSys.getValue() + "/" + bpDias.getValue();
        mPulse = pulse.getValue();
        try {
            JSONObject obj = null;
            if (hasLicense) {
                obj = new JSONObject(FileUtils.readFileRoot(mFileName, this)); //Load the config file
            } else {
                obj = new JSONObject(String.valueOf(FileUtils.encodeJSON(this, mFileName)));
            }//Load the config file

            if (obj.getBoolean("mTemperature")) {
                if (obj.getBoolean("mCelsius")) {

                    mTemp = "Temperature(C): " + temperature.getValue();

                } else if (obj.getBoolean("mFahrenheit")) {

//                    mTemp = "Temperature(F): " + temperature.getValue();
                    mTemp = "Temperature(F): " + tempView.getText().toString();
                }
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        mresp = resp.getValue();
        mSPO2 = "SpO2(%): " + spO2.getValue();
        String mComplaint = complaint.getValue();

        //Show only the headers of the complaints in the printed prescription
        String[] complaints = StringUtils.split(mComplaint, Node.bullet_arrow);
        mComplaint = "";
        String colon = ":";
        if (complaints != null) {
            for (String comp : complaints) {
                if (!comp.trim().isEmpty()) {
                    mComplaint = mComplaint + Node.big_bullet + comp.substring(0, comp.indexOf(colon)) + "<br/>";

                }
            }
            if (!mComplaint.isEmpty()) {
                mComplaint = mComplaint.substring(0, mComplaint.length() - 2);
                mComplaint = mComplaint.replaceAll("<b>", "");
                mComplaint = mComplaint.replaceAll("</b>", "");
            }
        }

        if (mPatientOpenMRSID == null) {
            mPatientOpenMRSID = getString(R.string.patient_not_registered);
        }

        String para_open = "<p style=\"font-size:11pt; margin: 0px; padding: 0px;\">";
        String para_close = "</p>";


        Calendar today = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(mPatientDob);
        dob.setTime(date);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        String rx_web = stringToWeb(rxReturned);

        String tests_web = stringToWeb(testsReturned.trim().replace("\n\n", "\n")
                .replace(Node.bullet, ""));

        //String advice_web = stringToWeb(adviceReturned);

        String advice_web = stringToWeb(medicalAdvice_string.trim().replace("\n\n", "\n"));
        Log.d("Hyperlink", "hyper_print: " + advice_web);

        String diagnosis_web = stringToWeb(diagnosisReturned);

//        String comments_web = stringToWeb(additionalReturned);

        String followUp_web = stringToWeb(followUpDate);

        String doctor_web = stringToWeb(doctorName);

        String heading = prescription1;
        String heading2 = prescription2;
        String heading3 = "<br/>";

        String bp = mBP;
        if (bp.equals("/")) bp = "";

        String address = mAddress + (mCityState != null ? " " + mCityState : "") + (mPhone != null ? " " + mPhone : "");
        System.out.println("ADDRESSS: " + mAddress);
        System.out.println("CITY: " + mCityState);
        System.out.println("PHONE: " + mPhone);

        String fam_hist = mFamHist;
        String pat_hist = mPatHist;

        if (fam_hist.trim().isEmpty()) {
            fam_hist = "No history of illness in family provided.";
        } else {
            fam_hist = fam_hist.replaceAll(Node.bullet, Node.big_bullet);
        }

        if (pat_hist.trim().isEmpty()) {
            pat_hist = "No history of patient's illness provided.";
        }

        // Generate an HTML document on the fly:
        String fontFamilyFile = "";
        if (objClsDoctorDetails != null && objClsDoctorDetails.getFontOfSign() != null) {
            Log.d("font", "font: " + objClsDoctorDetails.getFontOfSign());
            if (objClsDoctorDetails.getFontOfSign().toLowerCase().equalsIgnoreCase("youthness")) {
                fontFamilyFile = "src: url('file:///android_asset/fonts/Youthness.ttf');";
            } else if (objClsDoctorDetails.getFontOfSign().toLowerCase().equalsIgnoreCase("asem")) {
                fontFamilyFile = "src: url('file:///android_asset/fonts/Asem.otf');";
            } else if (objClsDoctorDetails.getFontOfSign().toLowerCase().equalsIgnoreCase("arty")) {
                fontFamilyFile = "src: url('file:///android_asset/fonts/Arty.otf');";
            } else if (objClsDoctorDetails.getFontOfSign().toLowerCase().equalsIgnoreCase("almondita")) {
                fontFamilyFile = "src: url('file:///android_asset/fonts/Almondita-mLZJP.ttf');";
            }
        }
        String font_face = "<style>" +
                "                @font-face {" +
                "                    font-family: \"MyFont\";" +
                fontFamilyFile +
                "                }" +
                "            </style>";

        String doctorSign = "";
        String doctrRegistartionNum = "";
        // String docDigitallySign = "";
        String doctorDetailStr = "";
        if (objClsDoctorDetails != null) {
            //  docDigitallySign = "Digitally Signed By";
            doctorSign = objClsDoctorDetails.getTextOfSign();

            doctrRegistartionNum = !TextUtils.isEmpty(objClsDoctorDetails.getRegistrationNumber()) ?
                    "Registration No: " + objClsDoctorDetails.getRegistrationNumber() : "";
            doctorDetailStr = "<div style=\"text-align:right;margin-right:0px;margin-top:3px;\">" +
                    "<span style=\"font-size:12pt; color:#212121;padding: 0px;\">" + objClsDoctorDetails.getName() + "</span><br>" +
                    "<span style=\"font-size:12pt; color:#212121;padding: 0px;\">" + "  " + objClsDoctorDetails.getQualification()
                    + ", " + objClsDoctorDetails.getSpecialization() + "</span><br>" +
                    //  "<span style=\"font-size:12pt;color:#212121;padding: 0px;\">" + (!TextUtils.isEmpty(objClsDoctorDetails.getPhoneNumber()) ?
                    //  getString(R.string.dr_phone_number) + objClsDoctorDetails.getPhoneNumber() : "") + "</span><br>" +
                    "<span style=\"font-size:12pt;color:#212121;padding: 0px;\">" + (!TextUtils.isEmpty(objClsDoctorDetails.getEmailId()) ?
                    "Email: " + objClsDoctorDetails.getEmailId() : "") + "</span><br>" +
                    "</div>";

        }

        // Modified by the Venu N on for print option as per the Prajwal note on 02/04/2020.
        String physicalExamStr = phyExam.getValue().replaceAll("<b>General exams: </b>", "");
        physicalExamStr = physicalExamStr.replaceAll("Self Assessment:", "");// Added by venu N on 02/04/2020.

        // Generate an HTML document on the fly:
        if (objClsDoctorDetails != null) {
            //this means the prescription is present since even when the doctor havent given any presc,
            //then to the Doctor Details are mandatorily passed to the app.
            if (isRespiratory) {
                String htmlDocument =
                        String.format(font_face + "<b><p id=\"heading_1\" style=\"font-size:16pt; margin: 0px; padding: 0px; text-align: center;\">%s</p>" +
                                        "<p id=\"heading_2\" style=\"font-size:12pt; margin: 0px; padding: 0px; text-align: center;\">%s</p>" +
                                        "<p id=\"heading_3\" style=\"font-size:12pt; margin: 0px; padding: 0px; text-align: center;\">%s</p>" +
                                        "<hr style=\"font-size:12pt;\">" + "<br/>" +
                                        /* doctorDetailStr +*/
                                        "<p id=\"patient_name\" style=\"font-size:12pt; margin: 0px; padding: 0px;\">%s</p></b>" +
                                        "<p id=\"patient_details\" style=\"font-size:12pt; margin: 0px; padding: 0px;\">Age: %s | Gender: %s  </p>" +

                                        //Address is commented...
                                        /* "<p id=\"address_and_contact\" style=\"font-size:12pt; margin: 0px; padding: 0px;\">Address and Contact: %s</p>" +*/

                                        "<p id=\"visit_details\" style=\"font-size:12pt; margin-top:5px; margin-bottom:0px; padding: 0px;\">Patient Id: %s | Date of visit: %s </p><br>" +

                                        //Vitals section commented
                                      /*  "<b><p id=\"vitals_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px;; padding: 0px;\">Vitals</p></b>" +
                                        "<p id=\"vitals\" style=\"font-size:12pt;margin:0px; padding: 0px;\">Height(cm): %s | Weight(kg): %s | BMI: %s | Blood Pressure: %s | Pulse(bpm): %s | %s | Respiratory Rate: %s |  %s </p><br>" +
                                */

                                   /* "<b><p id=\"patient_history_heading\" style=\"font-size:11pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Patient History</p></b>" +
                                    "<p id=\"patient_history\" style=\"font-size:11pt;margin:0px; padding: 0px;\"> %s</p><br>" +
                                    "<b><p id=\"family_history_heading\" style=\"font-size:11pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Family History</p></b>" +
                                    "<p id=\"family_history\" style=\"font-size:11pt;margin: 0px; padding: 0px;\"> %s</p><br>" +*/
                                        "<b><p id=\"complaints_heading\" style=\"font-size:15pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Presenting complaint(s)</p></b>" +
                                        para_open + "%s" + para_close + "<br><br>" +
                                        "<u><b><p id=\"diagnosis_heading\" style=\"font-size:15pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Diagnosis</p></b></u>" +
                                        "%s<br>" +
                                        "<u><b><p id=\"rx_heading\" style=\"font-size:15pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Medication(s) plan</p></b></u>" +
                                        "%s<br>" +
                                        "<u><b><p id=\"tests_heading\" style=\"font-size:15pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Recommended Investigation(s)</p></b></u>" +
                                        "%s<br>" +
                                        "<u><b><p id=\"advice_heading\" style=\"font-size:15pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">General Advice</p></b></u>" +
                                        "%s<br>" +
                                        "<u><b><p id=\"follow_up_heading\" style=\"font-size:15pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Follow Up Date</p></b></u>" +
                                        "%s<br>" +
                                        "<div style=\"text-align:right;margin-right:50px;margin-top:0px;\">" +
                                        "<span style=\"font-size:80pt;font-family: MyFont;padding: 0px;\">" + doctorSign + "</span>" +
                                        doctorDetailStr +
                                        "<p style=\"font-size:12pt; margin-top:-0px; padding: 0px;\">" + doctrRegistartionNum + "</p>" +
                                        "</div>"
                                , heading, heading2, heading3, mPatientName, age, mGender, /*mSdw address,*/ mPatientOpenMRSID, mDate/*, (!TextUtils.isEmpty(mHeight))
                                        ? mHeight : "", (!TextUtils.isEmpty(mWeight)) ? mWeight : "",
                                (!TextUtils.isEmpty(mBMI)) ? mBMI : "", (!TextUtils.isEmpty(bp)) ? bp : "", (!TextUtils.isEmpty(mPulse))
                                        ? mPulse : "", (!TextUtils.isEmpty(mTemp)) ? mTemp : "", (!TextUtils.isEmpty(mresp)) ? mresp : "",
                                (!TextUtils.isEmpty(mSPO2)) ? mSPO2 : ""*/,
                                /*pat_hist, fam_hist,*/ mComplaint, diagnosis_web, rx_web, tests_web, advice_web, followUp_web, doctor_web);
                webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);

            } else {
                //If Respiratory value is not present then this loop will be excuted...
                String htmlDocument =
                        String.format(font_face + "<b><p id=\"heading_1\" style=\"font-size:16pt; margin: 0px; padding: 0px; text-align: center;\">%s</p>" +
                                        "<p id=\"heading_2\" style=\"font-size:12pt; margin: 0px; padding: 0px; text-align: center;\">%s</p>" +
                                        "<p id=\"heading_3\" style=\"font-size:12pt; margin: 0px; padding: 0px; text-align: center;\">%s</p>" +
                                        "<hr style=\"font-size:12pt;\">" + "<br/>" +
                                        "<p id=\"patient_name\" style=\"font-size:12pt; margin: 0px; padding: 0px;\">%s</p></b>" +
                                        "<p id=\"patient_details\" style=\"font-size:12pt; margin: 0px; padding: 0px;\">Age: %s | Gender: %s </p>" +

                                        //  "<p id=\"address_and_contact\" style=\"font-size:12pt; margin: 0px; padding: 0px;\">Address and Contact: %s</p>" +

                                        "<p id=\"visit_details\" style=\"font-size:12pt; margin-top:5px; margin-bottom:0px; padding: 0px;\">Patient Id: %s | Date of visit: %s </p><br>" +

                                        //Vitals section commented...
                                       /* "<b><p id=\"vitals_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px;; padding: 0px;\">Vitals</p></b>" +
                                        "<p id=\"vitals\" style=\"font-size:12pt;margin:0px; padding: 0px;\">Height(cm): %s | Weight(kg): %s | BMI: %s | Blood Pressure: %s | Pulse(bpm): %s | %s | %s </p><br>" +
                                */

                                    /*"<b><p id=\"patient_history_heading\" style=\"font-size:11pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Patient History</p></b>" +
                                    "<p id=\"patient_history\" style=\"font-size:11pt;margin:0px; padding: 0px;\"> %s</p><br>" +
                                    "<b><p id=\"family_history_heading\" style=\"font-size:11pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Family History</p></b>" +
                                    "<p id=\"family_history\" style=\"font-size:11pt;margin: 0px; padding: 0px;\"> %s</p><br>" +*/
                                        "<b><p id=\"complaints_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Presenting complaint(s)</p></b>" +
                                        para_open + "%s" + para_close + "<br><br>" +
                                        "<u><b><p id=\"diagnosis_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Diagnosis</p></b></u>" +
                                        "%s<br>" +
                                        "<u><b><p id=\"rx_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Medication(s) plan</p></b></u>" +
                                        "%s<br>" +
                                        "<u><b><p id=\"tests_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Recommended Investigation(s)</p></b></u>" +
                                        "%s<br>" +
                                        "<u><b><p id=\"advice_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">General Advice</p></b></u>" +
                                        "%s<br>" +
                                        "<u><b><p id=\"follow_up_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Follow Up Date</p></b></u>" +
                                        "%s<br>" +
                                        "<div style=\"text-align:right;margin-right:50px;margin-top:0px;\">" +
                                        "<span style=\"font-size:80pt;font-family: MyFont;padding: 0px;\">" + doctorSign + "</span><br>" +
                                        doctorDetailStr +
                                        "<span style=\"font-size:12pt; margin-top:5px; padding: 0px;\">" + doctrRegistartionNum + "</span>" +
                                        "</div>"
                                , heading, heading2, heading3, mPatientName, age, mGender, /*mSdw address,*/ mPatientOpenMRSID, mDate/*,
                                (!TextUtils.isEmpty(mHeight)) ? mHeight : "", (!TextUtils.isEmpty(mWeight)) ? mWeight : "",
                                (!TextUtils.isEmpty(mBMI)) ? mBMI : "", (!TextUtils.isEmpty(bp)) ? bp : "", (!TextUtils.isEmpty(mPulse)) ?
                                        mPulse : "", (!TextUtils.isEmpty(mTemp)) ? mTemp : "", (!TextUtils.isEmpty(mSPO2)) ? mSPO2 : ""*/,
                                /*pat_hist, fam_hist,*/ mComplaint, diagnosis_web, rx_web, tests_web, advice_web, followUp_web, doctor_web);
                webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);

            }
        } else {
            if (isRespiratory) {
                String htmlDocument =
                        String.format("<b><p id=\"heading_1\" style=\"font-size:16pt; margin: 0px; padding: 0px; text-align: center;\">%s</p>" +
                                        "<p id=\"heading_2\" style=\"font-size:12pt; margin: 0px; padding: 0px; text-align: center;\">%s</p>" +
                                        "<p id=\"heading_3\" style=\"font-size:12pt; margin: 0px; padding: 0px; text-align: center;\">%s</p>" +
                                        "<hr style=\"font-size:12pt;\">" + "<br/>" +
                                        "<p id=\"patient_name\" style=\"font-size:12pt; margin: 0px; padding: 0px;\">%s</p></b>" +
                                        "<p id=\"patient_details\" style=\"font-size:12pt; margin: 0px; padding: 0px;\">Age: %s | Gender: %s  </p>" + // | Son/Daughter/Wife of: %s  Removed as per the Prajwal by Venu N on 02/04/2020.
                                        /* "<p id=\"address_and_contact\" style=\"font-size:12pt; margin: 0px; padding: 0px;\"><b>Address and Contact:</b> %s</p>" +*/
                                        "<b><p id=\"visit_details\" style=\"font-size:12pt; margin-top:5px; margin-bottom:0px; padding: 0px;\">User ID: %s | Date of Assessment: %s </p></b><br><br>" +
                                        "<b><p id=\"Self Assessment\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px;; padding: 0px;\">Self Assessment:</p></b>" + physicalExamStr
                                    /* +
                                   "<b><p id=\"vitals_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px;; padding: 0px;\">Vitals</p></b>" +
                                    "<p id=\"vitals\" style=\"font-size:12pt;margin:0px; padding: 0px;\">Height(cm): %s | Weight(kg): %s | BMI: %s | Blood Pressure: %s | Pulse(bpm): %s | %s | Respiratory Rate: %s |  %s </p>" +
                                    "<b><p id=\"patient_history_heading\" style=\"font-size:11pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Patient History</p></b>" +
                                    "<p id=\"patient_history\" style=\"font-size:11pt;margin:0px; padding: 0px;\"> %s</p>" +
                                    "<b><p id=\"family_history_heading\" style=\"font-size:11pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Family History</p></b>" +
                                    "<p id=\"family_history\" style=\"font-size:11pt;margin: 0px; padding: 0px;\"> %s</p>" +
                                    "<b><p id=\"complaints_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Chief Complaint(s)</p></b>" +
                                    para_open + "%s" + para_close + "<br><br>" +
                                    "<b><p id=\"diagnosis_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Diagnosis</p></b>" +
                                    "%s" +
                                    "<b><p id=\"rx_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Medication</p></b>" +
                                    "%s" +
                                    "<b><p id=\"tests_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Tests To Be Performed</p></b>" +
                                    "%s" +
                                    "<b><p id=\"advice_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">General Advice</p></b>" +
                                    "%s" +
                                    "<b><p id=\"follow_up_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Follow Up Date</p></b>" +
                                    "%s"*/
                                , heading, heading2, heading3, mPatientName, age, mGender/*, mSdw, address*/, mPatientOpenMRSID, (mDate != null ? mDate : ""), mHeight, mWeight,
                                mBMI, bp, mPulse, mTemp, mresp, mSPO2, pat_hist, fam_hist, mComplaint, diagnosis_web, rx_web, tests_web, advice_web, followUp_web, doctor_web);
                webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);
            } else {
                String htmlDocument =
                        String.format("<b><p id=\"heading_1\" style=\"font-size:16pt; margin: 0px; padding: 0px; text-align: center;\">%s</p>" +
                                        "<p id=\"heading_2\" style=\"font-size:12pt; margin: 0px; padding: 0px; text-align: center;\">%s</p>" +
                                        "<p id=\"heading_3\" style=\"font-size:12pt; margin: 0px; padding: 0px; text-align: center;\">%s</p>" +
                                        "<hr style=\"font-size:12pt;\">" + "<br/>" +
                                        "<p id=\"patient_name\" style=\"font-size:12pt; margin: 0px; padding: 0px;\">%s</p></b>" +
                                        "<p id=\"patient_details\" style=\"font-size:12pt; margin: 0px; padding: 0px;\">Age: %s | Gender: %s </p>" +  //  | Son/Daughter/Wife of: %s  modified by venu as per Prejwal on 02/04/2020.
                                        /* "<p id=\"address_and_contact\" style=\"font-size:12pt; margin: 0px; padding: 0px;\"><b>Address and Contact:</b> %s</p>" +*/
                                        "<b><p id=\"visit_details\" style=\"font-size:12pt; margin-top:5px; margin-bottom:0px; padding: 0px;\">User ID: %s | Date of Assessment: %s </p></b><br><br>" +
                                        "<b><p id=\"Self Assessment\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px;; padding: 0px;\">Self Assessment:</p></b>" + physicalExamStr
                            /*"<b><p id=\"vitals_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px;; padding: 0px;\">Vitals</p></b>" +
                            "<p id=\"vitals\" style=\"font-size:12pt;margin:0px; padding: 0px;\">Height(cm): %s | Weight(kg): %s | BMI: %s | Blood Pressure: %s | Pulse(bpm): %s | %s | %s </p>" +
                                    "<b><p id=\"patient_history_heading\" style=\"font-size:11pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Patient History</p></b>" +
                                    "<p id=\"patient_history\" style=\"font-size:11pt;margin:0px; padding: 0px;\"> %s</p>" +
                                    "<b><p id=\"family_history_heading\" style=\"font-size:11pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Family History</p></b>" +
                                    "<p id=\"family_history\" style=\"font-size:11pt;margin: 0px; padding: 0px;\"> %s</p>" +
                                    "<b><p id=\"complaints_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Chief Complaint(s)</p></b>" +
                                    para_open + "%s" + para_close + "<br><br>" +
                                    "<b><p id=\"diagnosis_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Diagnosis</p></b>" +
                                    "%s" +
                                    "<b><p id=\"rx_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Medication</p></b>" +
                                    "%s" +
                                    "<b><p id=\"tests_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Tests To Be Performed</p></b>" +
                                    "%s" +
                                    "<b><p id=\"advice_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">General Advice</p></b>" +
                                    "%s" +
                                    "<b><p id=\"follow_up_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Follow Up Date</p></b>" +
                                    "%s"*/
                                , heading, heading2, heading3, mPatientName, age, mGender/*, mSdw, address*/, mPatientOpenMRSID, (mDate != null ? mDate : ""), mHeight, mWeight,
                                mBMI, bp, mPulse, mTemp, mSPO2, pat_hist, fam_hist, mComplaint, diagnosis_web, rx_web, tests_web, advice_web, followUp_web, doctor_web);
                webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);
            }
            //end of if-else of design...
        }


        /**
         * +
         * "<b><p id=\"comments_heading\" style=\"font-size:12pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Doctor's Note</p></b>" +
         * "%s"
         */

        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager
        mWebView = webView;
    }

    /**
     * This method creates a print job using PrintManager instance and PrintAdapter Instance
     *
     * @param webView object of type WebView.
     */
    private void createWebPrintJob(WebView webView) {
        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) VisitSummaryActivity.this.getSystemService(Context.PRINT_SERVICE);
        //Used VisitSummary.this instead of this because everytime a new context is created for this activity, using this would led the OS to a confusion of which context instance to be used. Using VisitSummary.this points to the VisitSummary activity everytime - added by Prajwal.

        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

        // Create a print job with name and adapter instance
        String jobName = getString(R.string.app_name) + " Visit Summary";
        printManager.print(jobName, printAdapter,           //removed PrintJob instance as it was of no use - Prajwal.
                new PrintAttributes.Builder().build());

    }

    private void endVisit() {
        Log.d(TAG, "endVisit: ");
        if (visitUUID == null || visitUUID.isEmpty()) {
            String visitIDorderBy = "startdate";
            String visitIDSelection = "uuid = ?";
            String[] visitIDArgs = {visitUuid};
            final Cursor visitIDCursor = db.query("tbl_visit", null, visitIDSelection, visitIDArgs, null, null, visitIDorderBy);
            if (visitIDCursor != null && visitIDCursor.moveToFirst() && visitIDCursor.getCount() > 0) {
                visitIDCursor.moveToFirst();
                visitUUID = visitIDCursor.getString(visitIDCursor.getColumnIndexOrThrow("uuid"));
            }
            if (visitIDCursor != null) visitIDCursor.close();
        }
        if (visitUUID != null && !visitUUID.isEmpty()) {
            if (followUpDate != null && !followUpDate.isEmpty()) {
                AlertDialog.Builder followUpAlert = new AlertDialog.Builder(VisitSummaryActivity.this);
                followUpAlert.setMessage(getString(R.string.visit_summary_follow_up_reminder) + followUpDate);
                followUpAlert.setNeutralButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(VisitSummaryActivity.this, PatientSurveyActivity.class);
                        intent.putExtra("patientUuid", patientUuid);
                        intent.putExtra("visitUuid", visitUuid);
                        intent.putExtra("encounterUuidVitals", encounterVitals);
                        intent.putExtra("encounterUuidAdultIntial", encounterUuidAdultIntial);
                        intent.putExtra("state", state);
                        intent.putExtra("name", patientName);
                        intent.putExtra("tag", intentTag);
                        startActivity(intent);
                    }
                });
                followUpAlert.show();
            } else {
//                Intent intent = new Intent(VisitSummaryActivity.this, PatientSurveyActivity.class);
//                intent.putExtra("patientUuid", patientUuid);
//                intent.putExtra("visitUuid", visitUuid);
//                intent.putExtra("encounterUuidVitals", encounterVitals);
//                intent.putExtra("encounterUuidAdultIntial", encounterUuidAdultIntial);
//                intent.putExtra("state", state);
//                intent.putExtra("name", patientName);
//                intent.putExtra("tag", intentTag);
//                startActivity(intent);


                VisitsDAO visitsDAO = new VisitsDAO();
                try {
                    visitsDAO.updateVisitEnddate(visitUuid, AppConstants.dateAndTimeUtils.currentDateTime());
                } catch (DAOException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }

                //SyncDAO syncDAO = new SyncDAO();
                //syncDAO.pushDataApi();
//                syncUtils.syncForeground("survey"); //Sync function will work in foreground of app and
                // the Time will be changed for last sync.

//               sessionManager.removeVisitSummary(patientUuid, visitUuid);


            }
        } else {

            Log.d(TAG, "endVisit: null");
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VisitSummaryActivity.this);
            alertDialogBuilder.setMessage(getString(R.string.visit_summary_upload_reminder));
            alertDialogBuilder.setNeutralButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

        }
    }

    /**
     * update the value of submit in table for particular visit
     */
    private void updateVisitSubmit() {
        Log.d(TAG, "updateVisitSubmit: ");
        if (visitUUID == null || visitUUID.isEmpty()) {
            String visitIDSelection = "uuid = ?";
            String[] visitIDArgs = {visitUuid};
            final Cursor visitIDCursor = db.query("tbl_visit", null, visitIDSelection, visitIDArgs, null, null, null);
            if (visitIDCursor != null && visitIDCursor.moveToFirst() && visitIDCursor.getCount() > 0) {
                visitIDCursor.moveToFirst();
                visitUUID = visitIDCursor.getString(visitIDCursor.getColumnIndexOrThrow("uuid"));
            }
            if (visitIDCursor != null) visitIDCursor.close();
        }
        if (visitUUID != null && !visitUUID.isEmpty()) {

            VisitsDAO visitsDAO = new VisitsDAO();
            try {
                visitsDAO.updateVisitSubmit(visitUuid);
            } catch (DAOException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
            }

        }
    }


    /**
     * This methods retrieves patient data from database.
     *
     * @param dataString variable of type String
     * @return void
     */

    public void queryData(String dataString) {
        String patientSelection = "uuid = ?";
        String[] patientArgs = {dataString};

        String table = "tbl_patient";
        String[] columnsToReturn = {"openmrs_id", "first_name", "middle_name", "last_name",
                "date_of_birth", "address1", "address2", "city_village", "state_province", "country",
                "postal_code", "phone_number", "gender"};
        final Cursor idCursor = db.query(table, columnsToReturn, patientSelection, patientArgs, null, null, null);

        if (idCursor.moveToFirst()) {
            do {
                patient.setOpenmrs_id(idCursor.getString(idCursor.getColumnIndex("openmrs_id")));
                patient.setFirst_name(idCursor.getString(idCursor.getColumnIndex("first_name")));
                patient.setMiddle_name(idCursor.getString(idCursor.getColumnIndex("middle_name")));
                patient.setLast_name(idCursor.getString(idCursor.getColumnIndex("last_name")));
                patient.setDate_of_birth(idCursor.getString(idCursor.getColumnIndex("date_of_birth")));
                patient.setAddress1(idCursor.getString(idCursor.getColumnIndex("address1")));
                patient.setAddress2(idCursor.getString(idCursor.getColumnIndex("address2")));
                patient.setCity_village(idCursor.getString(idCursor.getColumnIndex("city_village")));
                patient.setState_province(idCursor.getString(idCursor.getColumnIndex("state_province")));
                patient.setCountry(idCursor.getString(idCursor.getColumnIndex("country")));
                patient.setPostal_code(idCursor.getString(idCursor.getColumnIndex("postal_code")));
                patient.setPhone_number(idCursor.getString(idCursor.getColumnIndex("phone_number")));
                patient.setGender(idCursor.getString(idCursor.getColumnIndex("gender")));
//                patient.setSdw(idCursor.getString(idCursor.getColumnIndexOrThrow("sdw")));
//                patient.setOccupation(idCursor.getString(idCursor.getColumnIndexOrThrow("occupation")));
//                patient.setPatient_photo(idCursor.getString(idCursor.getColumnIndex("patient_photo")));
            } while (idCursor.moveToNext());
        }
        idCursor.close();
//        PatientsDAO patientsDAO = new PatientsDAO();
//        String patientSelection1 = "patientuuid = ?";
//        String[] patientArgs1 = {patientUuid};
//        String[] patientColumns1 = {"value", "person_attribute_type_uuid"};
//        Cursor idCursor1 = db.query("tbl_patient_attribute", patientColumns1, patientSelection1, patientArgs1, null, null, null);
//        String name = "";
//        if (idCursor1.moveToFirst()) {
//            do {
//                try {
//                    name = patientsDAO.getAttributesName(idCursor1.getString(idCursor1.getColumnIndexOrThrow("person_attribute_type_uuid")));
//                } catch (DAOException e) {
//                    Crashlytics.getInstance().core.logException(e);
//                }
//
//                if (name.equalsIgnoreCase("caste")) {
//                    patient.setCaste(idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")));
//                }
//                if (name.equalsIgnoreCase("Telephone Number")) {
//                    patient.setPhone_number(idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")));
//                }
//                if (name.equalsIgnoreCase("Education Level")) {
//                    patient.setEducation_level(idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")));
//                }
//                if (name.equalsIgnoreCase("Economic Status")) {
//                    patient.setEconomic_status(idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")));
//                }
//                if (name.equalsIgnoreCase("occupation")) {
//                    patient.setOccupation(idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")));
//                }
//                if (name.equalsIgnoreCase("Son/wife/daughter")) {
//                    patient.setSdw(idCursor1.getString(idCursor1.getColumnIndexOrThrow("value")));
//                }
//
//            } while (idCursor1.moveToNext());
//        }
//        idCursor1.close();
        String[] columns = {"value", " conceptuuid"};

        try {
            String famHistSelection = "encounteruuid = ? AND conceptuuid = ?";
            String[] famHistArgs = {encounterUuidAdultIntial, UuidDictionary.RHK_FAMILY_HISTORY_BLURB};
            Cursor famHistCursor = db.query("tbl_obs", columns, famHistSelection, famHistArgs, null, null, null);
            famHistCursor.moveToLast();
            String famHistText = famHistCursor.getString(famHistCursor.getColumnIndexOrThrow("value"));
            famHistory.setValue(famHistText);
            famHistCursor.close();
        } catch (CursorIndexOutOfBoundsException e) {
            famHistory.setValue(""); // if family history does not exist
        }

        try {
            String medHistSelection = "encounteruuid = ? AND conceptuuid = ?";

            String[] medHistArgs = {encounterUuidAdultIntial, UuidDictionary.RHK_MEDICAL_HISTORY_BLURB};

            Cursor medHistCursor = db.query("tbl_obs", columns, medHistSelection, medHistArgs, null, null, null);
            medHistCursor.moveToLast();
            String medHistText = medHistCursor.getString(medHistCursor.getColumnIndexOrThrow("value"));
            patHistory.setValue(medHistText);

            if (medHistText != null && !medHistText.isEmpty()) {

                medHistory = patHistory.getValue();


                medHistory = medHistory.replace("\"", "");
                medHistory = medHistory.replace("\n", "");
                do {
                    medHistory = medHistory.replace("  ", "");
                } while (medHistory.contains("  "));
            }
            medHistCursor.close();
        } catch (CursorIndexOutOfBoundsException e) {
            patHistory.setValue(""); // if medical history does not exist
        }
//vitals display code
        String visitSelection = "encounteruuid = ? AND voided!='1'";
        String[] visitArgs = {encounterVitals};
        if (encounterVitals != null) {
            try {
                Cursor visitCursor = db.query("tbl_obs", columns, visitSelection, visitArgs, null, null, null);
                if (visitCursor != null && visitCursor.moveToFirst()) {
                    do {
                        String dbConceptID = visitCursor.getString(visitCursor.getColumnIndex("conceptuuid"));
                        String dbValue = visitCursor.getString(visitCursor.getColumnIndex("value"));
                        parseData(dbConceptID, dbValue);
                    } while (visitCursor.moveToNext());
                }
                if (visitCursor != null) {
                    visitCursor.close();
                }
            } catch (SQLException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        }
//adult intails display code
        String encounterselection = "encounteruuid = ? AND conceptuuid != ? AND conceptuuid != ? AND voided!='1'";
        String[] encounterargs = {encounterUuidAdultIntial, UuidDictionary.COMPLEX_IMAGE_AD, UuidDictionary.COMPLEX_IMAGE_PE};
        Cursor encountercursor = db.query("tbl_obs", columns, encounterselection, encounterargs, null, null, null);
        try {
            if (encountercursor != null && encountercursor.moveToFirst()) {
                do {
                    String dbConceptID = encountercursor.getString(encountercursor.getColumnIndex("conceptuuid"));
                    String dbValue = encountercursor.getString(encountercursor.getColumnIndex("value"));
                    parseData(dbConceptID, dbValue);
                } while (encountercursor.moveToNext());
            }
            if (encountercursor != null) {
                encountercursor.close();
            }
        } catch (SQLException sql) {
            FirebaseCrashlytics.getInstance().recordException(sql);
        }

        downloadPrescriptionDefault();
        downloadDoctorDetails();
    }

    /**
     * This method distinguishes between different concepts using switch case to populate the information into the relevant sections (eg:complaints, physical exam, vitals, etc.).
     *
     * @param concept_id variable of type int.
     * @param value      variable of type String.
     */
    private void parseData(String concept_id, String value) {
        switch (concept_id) {
            case UuidDictionary.CURRENT_COMPLAINT: { //Current Complaint
                complaint.setValue(value);
                break;
            }
            case UuidDictionary.PHYSICAL_EXAMINATION: { //Physical Examination
                phyExam.setValue(value);
                break;
            }
            case UuidDictionary.HEIGHT: //Height
            {
                height.setValue(value);
                break;
            }
            case UuidDictionary.WEIGHT: //Weight
            {
                weight.setValue(value);
                break;
            }
            case UuidDictionary.PULSE: //Pulse
            {
                pulse.setValue(value);
                break;
            }
            case UuidDictionary.SYSTOLIC_BP: //Systolic BP
            {
                bpSys.setValue(value);
                break;
            }
            case UuidDictionary.DIASTOLIC_BP: //Diastolic BP
            {
                bpDias.setValue(value);
                break;
            }
            case UuidDictionary.TEMPERATURE: //Temperature
            {
                temperature.setValue(value);
                break;
            }
            //    Respiratory added by mahiti dev team
            case UuidDictionary.RESPIRATORY: //Respiratory
            {
                resp.setValue(value);
                break;
            }
            case UuidDictionary.SPO2: //SpO2
            {
                spO2.setValue(value);
                break;
            }
            case UuidDictionary.TELEMEDICINE_DIAGNOSIS: {
                if (!diagnosisReturned.isEmpty()) {
                    diagnosisReturned = diagnosisReturned + ",\n" + value;
                } else {
                    diagnosisReturned = value;
                }
                if (diagnosisCard.getVisibility() != View.VISIBLE) {
                    //   diagnosisCard.setVisibility(View.VISIBLE);
                }
                diagnosisTextView.setText(diagnosisReturned);
                //checkForDoctor();
                break;
            }
            case UuidDictionary.JSV_MEDICATIONS: {
                Log.i(TAG, "parseData: val:" + value);
                Log.i(TAG, "parseData: rx" + rxReturned);
                if (!rxReturned.trim().isEmpty()) {
                    rxReturned = rxReturned + "\n" + value;
                } else {
                    rxReturned = value;
                }
                Log.i(TAG, "parseData: rxfin" + rxReturned);
                if (prescriptionCard.getVisibility() != View.VISIBLE) {
                    //  prescriptionCard.setVisibility(View.VISIBLE);
                }
                prescriptionTextView.setText(rxReturned);
                //checkForDoctor();
                break;
            }
            case UuidDictionary.MEDICAL_ADVICE: {
                if (!adviceReturned.isEmpty()) {
                    adviceReturned = adviceReturned + "\n" + value;
                    Log.d("GAME", "GAME: " + adviceReturned);
                } else {
                    adviceReturned = value;
                    Log.d("GAME", "GAME_2: " + adviceReturned);
                }
                if (medicalAdviceCard.getVisibility() != View.VISIBLE) {
                    //  medicalAdviceCard.setVisibility(View.VISIBLE);
                }
                //medicalAdviceTextView.setText(adviceReturned);
                Log.d("Hyperlink", "hyper_global: " + medicalAdvice_string);

                int j = adviceReturned.indexOf('<');
                int i = adviceReturned.lastIndexOf('>');
                if (i >= 0 && j >= 0) {
                    medicalAdvice_HyperLink = adviceReturned.substring(j, i + 1);
                } else {
                    medicalAdvice_HyperLink = "";
                }

                Log.d("Hyperlink", "Hyperlink: " + medicalAdvice_HyperLink);

                medicalAdvice_string = adviceReturned.replaceAll(medicalAdvice_HyperLink, "");
                Log.d("Hyperlink", "hyper_string: " + medicalAdvice_string);

                /*
                 * variable a contains the hyperlink sent from webside.
                 * variable b contains the string data (medical advice) of patient.
                 * */
                medicalAdvice_string = medicalAdvice_string.replace("\n\n", "\n");
                medicalAdviceTextView.setText(Html.fromHtml(medicalAdvice_HyperLink + medicalAdvice_string.replaceAll("\n", "<br><br>")));
                medicalAdviceTextView.setMovementMethod(LinkMovementMethod.getInstance());
                Log.d("hyper_textview", "hyper_textview: " + medicalAdviceTextView.getText().toString());
                //checkForDoctor();
                break;
            }
            case UuidDictionary.REQUESTED_TESTS: {
                if (!testsReturned.isEmpty()) {
                    testsReturned = testsReturned + "\n\n" + Node.bullet + " " + value;
                } else {
                    testsReturned = Node.bullet + " " + value;
                }
                if (requestedTestsCard.getVisibility() != View.VISIBLE) {
                    //  requestedTestsCard.setVisibility(View.VISIBLE);
                }
                requestedTestsTextView.setText(testsReturned);
                //checkForDoctor();
                break;
            }
            case UuidDictionary.ADDITIONAL_COMMENTS: {

                additionalCommentsCard.setVisibility(View.GONE);

                break;
            }
            case UuidDictionary.FOLLOW_UP_VISIT: {
                if (!followUpDate.isEmpty()) {
                    followUpDate = followUpDate + "," + value;
                } else {
                    followUpDate = value;
                }
                if (followUpDateCard.getVisibility() != View.VISIBLE) {
                    // followUpDateCard.setVisibility(View.VISIBLE);
                }
                followUpDateTextView.setText(followUpDate);
                //checkForDoctor();
                break;
            }


            default:
                Log.i(TAG, "parseData: " + value);
                break;

        }

        //prescription format of web...
        String htmlDocument = sms_web_prescription_format();
        prescriptionDataFormat.setText(Html.fromHtml(htmlDocument)); //prescription data collected will be shown here...
    }

    /**
     * @return htmlDocument : formatted stringof web prescription.
     */
    private String sms_web_prescription_format() {
        //prescription...
        //If prescription is provided then show cardview for
        // Prescription preview & Presription Downlaod link...
        if (objClsDoctorDetails != null) {
            cardView_prescription.setVisibility(View.VISIBLE);
            download_cardview.setVisibility(View.VISIBLE);
        } else {
            cardView_prescription.setVisibility(View.GONE);
            download_cardview.setVisibility(View.GONE);
        }

        //Check for license key and load the correct config file
        try {
            JSONObject obj = null;
            if (hasLicense) {
                obj = new JSONObject(Objects.requireNonNullElse
                        (FileUtils.readFileRoot(AppConstants.CONFIG_FILE_NAME, this),
                                String.valueOf(FileUtils.encodeJSON(this, AppConstants.CONFIG_FILE_NAME)))); //Load the config file
            } else {
                obj = new JSONObject(String.valueOf(FileUtils.encodeJSON(this, mFileName)));
            }
            prescription1 = obj.getString("presciptionHeader1");

            prescription2 = obj.getString("presciptionHeader2");

            //For AFI we are not using Respiratory Value
            if (obj.getBoolean("mResp")) {
                isRespiratory = true;
            } else {
                isRespiratory = false;
            }

        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        String heading = prescription1;
        String heading2 = prescription2;

        String mPatientName = patient.getFirst_name() + " " + ((!TextUtils.isEmpty(patient.getMiddle_name()))
                ? patient.getMiddle_name() : "") + " " + patient.getLast_name();

        Calendar today = Calendar.getInstance();
        Calendar dob = Calendar.getInstance();

        String mPatientDob = patient.getDate_of_birth();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(mPatientDob);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dob.setTime(date);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        String mGender = patient.getGender();

        String doctorDetailStr = "";
        if (objClsDoctorDetails != null) {
            doctorDetailStr =
                    "<span style=\"font-size:12pt; color:#212121;padding: 0px;\">" + objClsDoctorDetails.getName() + "</span><br>" +
                            "<span style=\"font-size:12pt; color:#212121;padding: 0px;\">" + "+911141236457" + "</span>";
        }

        String diagnosis_web = stringToWeb_sms(diagnosisReturned);
        String rx_web = stringToWeb_sms(rxReturned);

        String tests_web = stringToWeb_sms(testsReturned.trim().replace("\n\n", "\n")
                .replace(Node.bullet, ""));

        String advice_web = stringToWeb_sms(medicalAdvice_string.trim().replace("\n\n", "\n"));
        Log.d("Hyperlink", "hyper_print: " + advice_web);

        String followUpDateStr = "";
        if (followUpDate != null && followUpDate.contains(",")) {
            String[] spiltFollowDate = followUpDate.split(",");
            if (spiltFollowDate[0] != null && spiltFollowDate[0].contains("-")) {
                String remainingStr = "";
                for (int i = 1; i <= spiltFollowDate.length - 1; i++) {
                    remainingStr = ((!TextUtils.isEmpty(remainingStr)) ? remainingStr + ", " : "") + spiltFollowDate[i];
                }
                followUpDateStr = parseDateToddMMyyyy(spiltFollowDate[0]) + ", " + remainingStr;
            } else {
                followUpDateStr = followUpDate;
            }
        } else {
            followUpDateStr = followUpDate;
        }
        String followUp_web = stringToWeb_sms(followUpDateStr);

        String doctor_web = stringToWeb_sms(doctorName);

        String htmlDocument =
                String.format("<b id=\"heading_1\" style=\"font-size:5pt; margin: 0px; padding: 0px; text-align: center;\">%s</b><br>" +
                                "<b id=\"heading_2\" style=\"font-size:5pt; margin: 0px; padding: 0px; text-align: center;\">%s</b>" +
                                "<br><br>" +

                                "<b id=\"patient_name\" style=\"font-size:12pt; margin: 0px; padding: 0px;\">%s</b><br>" +
                                "<b id=\"patient_details\" style=\"font-size:12pt; margin: 0px; padding: 0px;\">Age: %s | Gender: %s  </b>" +
                                "<br><br>" +

                                "<b id=\"diagnosis_heading\" style=\"font-size:15pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Diagnosis <br>" +
                                "%s </b><br>" +
                                "<b id=\"rx_heading\" style=\"font-size:15pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Medication(s) plan <br>" +
                                "%s </b><br>" +
                                "<b id=\"tests_heading\" style=\"font-size:15pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Recommended Investigation(s) <br>" +
                                "%s " + "</b><br>" +
                                "<b id=\"advice_heading\" style=\"font-size:15pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Advice <br>" +
                                "%s" + "</b><br>" +
                                "<b id=\"follow_up_heading\" style=\"font-size:15pt;margin-top:5px; margin-bottom:0px; padding: 0px;\">Follow Up Date <br>" +
                                "%s" + "</b>"/* +*/

                        /* doctorDetailStr*/, heading, heading2, mPatientName, age, mGender,
                        (!diagnosis_web.isEmpty()) ? diagnosis_web : "- Not Provided" + "<br>",
                        (!rx_web.isEmpty()) ? rx_web : "- Not Provided" + "<br>",
                        (!tests_web.isEmpty()) ? tests_web : "- Not Provided" + "<br>",
                        (!advice_web.isEmpty()) ? advice_web : "- Not Provided" + "<br>",
                        (!followUp_web.isEmpty()) ? followUp_web : "- Not Provided" + "<br>"/*,
                        (!doctor_web.isEmpty()) ? doctor_web : "- Not Provided" + "<br>"*/);

        return htmlDocument;
    }

    ClsDoctorDetails objClsDoctorDetails;

    private void parseDoctorDetails(String dbValue) {
        Gson gson = new Gson();
        //  dbValue = dbValue.replace("{", "");
        try {
            objClsDoctorDetails = gson.fromJson(dbValue, ClsDoctorDetails.class);
            Log.e(TAG, "TEST DB: " + dbValue);
            Log.e(TAG, "TEST VISIT: " + objClsDoctorDetails);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Toast.makeText(context, getResources().getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT).show();
        }

        String doctorSign = "";
        String doctrRegistartionNum = "";
        // String docDigitallySign = "";
        String doctorDetailStr = "";
        if (objClsDoctorDetails != null) {

            //  frameLayout_doctor.setVisibility(View.VISIBLE);

            doctorSign = objClsDoctorDetails.getTextOfSign();

            doctrRegistartionNum = !TextUtils.isEmpty(objClsDoctorDetails.getRegistrationNumber()) ?
                    "Registration No: " + objClsDoctorDetails.getRegistrationNumber() : "";
            doctorDetailStr = "<div style=\"text-align:right;margin-right:0px;margin-top:3px;\">" +
                    "<span style=\"font-size:12pt; color:#448AFF;padding: 0px;\">" + (!TextUtils.isEmpty(objClsDoctorDetails.getName()) ? objClsDoctorDetails.getName() : "") + "</span><br>" +
                    "<span style=\"font-size:12pt; color:#448AFF;padding: 0px;\">" + "  " +
                    (!TextUtils.isEmpty(objClsDoctorDetails.getQualification()) ? objClsDoctorDetails.getQualification() : "") + ", "
                    + (!TextUtils.isEmpty(objClsDoctorDetails.getSpecialization()) ? objClsDoctorDetails.getSpecialization() : "") + "</span><br>" +
                    // "<span style=\"font-size:12pt;color:#448AFF;padding: 0px;\">" + (!TextUtils.isEmpty(objClsDoctorDetails.getPhoneNumber()) ? "Phone Number: " + objClsDoctorDetails.getPhoneNumber() : "") + "</span><br>" +
                    "<span style=\"font-size:12pt;color:#448AFF;padding: 0px;\">" + (!TextUtils.isEmpty(objClsDoctorDetails.getEmailId()) ? "Email: " + objClsDoctorDetails.getEmailId() : "") + "</span><br>" + (!TextUtils.isEmpty(objClsDoctorDetails.getRegistrationNumber()) ? "Registration No: " + objClsDoctorDetails.getRegistrationNumber() : "") +
                    "</div>";

            mDoctorName.setText(Html.fromHtml(doctorDetailStr).toString().trim());
        }
    }


    /**
     * @param title   variable of type String
     * @param content variable of type String
     * @param index   variable of type int
     */
    private void createNewCardView(String title, String content, int index) {
        final LayoutInflater inflater = VisitSummaryActivity.this.getLayoutInflater();
        View convertView = inflater.inflate(R.layout.card_doctor_content, null);
        TextView titleView = convertView.findViewById(R.id.textview_heading);
        TextView contentView = convertView.findViewById(R.id.textview_content);
        titleView.setText(title);
        contentView.setText(content);
        mLayout.addView(convertView, index);
    }

    /**
     * This method updates patient details to database.
     *
     * @param string    variable of type String
     * @param conceptID variable of type int
     */

    private void updateDatabase(String string, String conceptID) {
        ObsDTO obsDTO = new ObsDTO();
        ObsDAO obsDAO = new ObsDAO();
        try {
            obsDTO.setConceptuuid(String.valueOf(conceptID));
            obsDTO.setEncounteruuid(encounterUuidAdultIntial);
            obsDTO.setCreator(sessionManager.getCreatorID());
            obsDTO.setValue(string);
            obsDTO.setUuid(obsDAO.getObsuuid(encounterUuidAdultIntial, String.valueOf(conceptID)));

            obsDAO.updateObs(obsDTO);


        } catch (DAOException dao) {
            FirebaseCrashlytics.getInstance().recordException(dao);
        }

        EncounterDAO encounterDAO = new EncounterDAO();
        try {
            encounterDAO.updateEncounterSync("false", encounterUuidAdultIntial);
            encounterDAO.updateEncounterModifiedDate(encounterUuidAdultIntial);
        } catch (DAOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }


    public void callBroadcastReceiver() {
        if (!isReceiverRegistered) {
            if (receiver == null) {
                IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
                receiver = new NetworkChangeReceiver();
                registerReceiver(receiver, filter);
            }
            isReceiverRegistered = true;
        }
    }

    @Override
    public void onResume() // register the receiver here
    {
        if (downloadPrescriptionService == null) {
            registerDownloadPrescription();
        }

        super.onResume();

        callBroadcastReceiver();


        ImagesDAO imagesDAO = new ImagesDAO();
        ArrayList<String> fileuuidList = new ArrayList<String>();
        ArrayList<File> fileList = new ArrayList<File>();
        try {
            fileuuidList = imagesDAO.getImageUuid(encounterUuidAdultIntial, UuidDictionary.COMPLEX_IMAGE_AD);
            for (String fileuuid : fileuuidList) {
                String filename = AppConstants.IMAGE_PATH + fileuuid + ".jpg";
                if (new File(filename).exists()) {
                    fileList.add(new File(filename));
                }
            }
            HorizontalAdapter horizontalAdapter = new HorizontalAdapter(fileList, this);
            mAdditionalDocsLayoutManager = new LinearLayoutManager(VisitSummaryActivity.this, LinearLayoutManager.HORIZONTAL, false);
            mAdditionalDocsRecyclerView.setLayoutManager(mAdditionalDocsLayoutManager);
            mAdditionalDocsRecyclerView.setAdapter(horizontalAdapter);
        } catch (DAOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        } catch (Exception file) {
            Logger.logD(TAG, file.getMessage());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (receiver != null) {
            //LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
            unregisterReceiver(receiver);
            receiver = null;
        }
        if (downloadPrescriptionService != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(downloadPrescriptionService);
            downloadPrescriptionService = null;
        }
        isReceiverRegistered = false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_mental_help_request:
               /* Uri uri = Uri.parse("https://www.intelesafe.org/mental-health"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);*/
                Intent ppe = new Intent(VisitSummaryActivity.this, Webview.class);
                ppe.putExtra("VisitMental", 1);
                ppe.putExtra("VisitTitle", tvMentalHelpRequest.getText().toString());
                startActivity(ppe);
        }
    }


    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            isNetworkAvailable(context);
        }

    }


    public void sendSMS() {
        final AlertDialog.Builder textInput = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        textInput.setTitle(R.string.identification_screen_prompt_phone_number);
        final EditText phoneNumberEditText = new EditText(context);
        phoneNumberEditText.setInputType(InputType.TYPE_CLASS_PHONE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            phoneNumberEditText.setTextColor(getColor(R.color.gray));
        } else {
            phoneNumberEditText.setTextColor(getResources().getColor(R.color.gray));
        }

        textInput.setView(phoneNumberEditText);

        textInput.setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String body = "";
                String header = "";
                String message = "";

                String openMRSID = patient.getOpenmrs_id();
                if (openMRSID == null) {
                    openMRSID = getString(R.string.patient_not_registered);
                }

                header = "Patient Id: " + patient.getOpenmrs_id() + "\n"
                        + "Patient Name: " + patient.getFirst_name() + " " + patient.getLast_name() + "\n"
                        + "Patient DOB: " + patient.getDate_of_birth() + "\n";


                //ignore this
                if (diagnosisCard.getVisibility() == View.VISIBLE) {
                    if (!diagnosisTextView.getText().toString().trim().isEmpty())
                        body = body + getString(R.string.visit_summary_diagnosis) + ":" +
                                diagnosisTextView.getText().toString() + "\n";
                }
                if (prescriptionCard.getVisibility() == View.VISIBLE) {
                    if (!prescriptionTextView.getText().toString().trim().isEmpty())
                        body = body + getString(R.string.visit_summary_rx) + ":" +
                                prescriptionTextView.getText().toString() + "\n";
                }
                if (medicalAdviceCard.getVisibility() == View.VISIBLE) {
                    if (!medicalAdviceTextView.getText().toString().trim().isEmpty())
                        body = body + getString(R.string.visit_summary_advice) + ":" +
                                medicalAdviceTextView.getText().toString() + "\n";
                }
                if (requestedTestsCard.getVisibility() == View.VISIBLE) {
                    if (!requestedTestsTextView.getText().toString().trim().isEmpty())
                        body = body + getString(R.string.visit_summary_tests_prescribed) + ":" +
                                requestedTestsTextView.getText().toString() + "\n";
                }
//                if (additionalCommentsCard.getVisibility() == View.VISIBLE) {
//                    if (!additionalCommentsTextView.getText().toString().trim().isEmpty())
//                        body = body + getString(R.string.visit_summary_additional_comments) + ":" +
//                                additionalCommentsTextView.getText().toString() + "\n";
//                }
                if (followUpDateCard.getVisibility() == View.VISIBLE) {
                    if (!followUpDateTextView.getText().toString().trim().isEmpty())
                        body = body + getString(R.string.visit_summary_follow_up_date) + ":" +
                                followUpDateTextView.getText().toString() + "\n";
                }


                if (!phoneNumberEditText.getText().toString().trim().isEmpty()) {
                    if (!body.isEmpty()) {
                        if (body != null && body.length() > 0) {
                            body = body.substring(0, body.length() - 2);
                            message = header + body;
                        }
                        try {
                            SmsManager sm = SmsManager.getDefault();
                            String number = phoneNumberEditText.getText().toString();
                            ArrayList<String> parts = sm.divideMessage(message);

                            sm.sendMultipartTextMessage(number, null, parts, null, null);

                            Toast.makeText(getApplicationContext(), getString(R.string.sms_success),
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_sms),
                                    Toast.LENGTH_LONG).show();
                            Log.e(TAG, "onClick: " + e.getMessage());
                        }

                    } else {
                        Toast.makeText(context, getString(R.string.error_no_data), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, getString(R.string.error_phone_number), Toast.LENGTH_SHORT).show();
                }
            }
        });


        textInput.setNegativeButton(getString(R.string.generic_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        textInput.show();
    }

    public void downloadPrescription() {
        VisitsDAO visitsDAO = new VisitsDAO();
        try {
            if (visitsDAO.getDownloadedValue(visitUuid).equalsIgnoreCase("false") && uploaded) {
                String visitnote = "";
                EncounterDAO encounterDAO = new EncounterDAO();
                String encounterIDSelection = "visituuid = ? ";
                String[] encounterIDArgs = {visitUuid};
                Cursor encounterCursor = db.query("tbl_encounter", null, encounterIDSelection, encounterIDArgs, null, null, null);
                if (encounterCursor != null && encounterCursor.moveToFirst()) {
                    do {
                        if (encounterDAO.getEncounterTypeUuid("ENCOUNTER_VISIT_NOTE").equalsIgnoreCase(encounterCursor.getString(encounterCursor.getColumnIndexOrThrow("encounter_type_uuid")))) {
                            visitnote = encounterCursor.getString(encounterCursor.getColumnIndexOrThrow("uuid"));
                        }
                    } while (encounterCursor.moveToNext());

                }
                if (encounterCursor != null) {
                    encounterCursor.close();
                }
                //ignore yhis...
                if (!diagnosisReturned.isEmpty()) {
                    diagnosisReturned = "";
                    diagnosisTextView.setText("");
                    diagnosisCard.setVisibility(View.GONE);
                }
                if (!rxReturned.isEmpty()) {
                    rxReturned = "";
                    prescriptionTextView.setText("");
                    prescriptionCard.setVisibility(View.GONE);

                }
                if (!adviceReturned.isEmpty()) {
                    adviceReturned = "";
                    medicalAdviceTextView.setText("");
                    medicalAdviceCard.setVisibility(View.GONE);
                }
                if (!testsReturned.isEmpty()) {
                    testsReturned = "";
                    requestedTestsTextView.setText("");
                    requestedTestsCard.setVisibility(View.GONE);
                }
//                if (!additionalReturned.isEmpty()) {
//                    additionalReturned = "";
//                    additionalCommentsTextView.setText("");
//                    additionalCommentsCard.setVisibility(View.GONE);
//
//                }
                if (!followUpDate.isEmpty()) {
                    followUpDate = "";
                    followUpDateTextView.setText("");
                    followUpDateCard.setVisibility(View.GONE);
                }
                String[] columns = {"value", " conceptuuid"};
                String visitSelection = "encounteruuid = ? and voided!='1'";
                String[] visitArgs = {visitnote};
                Cursor visitCursor = db.query("tbl_obs", columns, visitSelection, visitArgs, null, null, null);
                if (visitCursor.moveToFirst()) {
                    do {
                        String dbConceptID = visitCursor.getString(visitCursor.getColumnIndex("conceptuuid"));
                        String dbValue = visitCursor.getString(visitCursor.getColumnIndex("value"));
                        parseData(dbConceptID, dbValue);
                    } while (visitCursor.moveToNext());
                }
                visitCursor.close();

                if (uploaded) {
                    try {
                        downloaded = visitsDAO.isUpdatedDownloadColumn(visitUuid, true);
                    } catch (DAOException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                }
                downloadDoctorDetails();

            }

            additionalDocumentImagesDownload();
            physcialExaminationImagesDownload();

        } catch (DAOException e) {
            e.printStackTrace();
        }

    }

    public void downloadPrescriptionDefault() {
        String visitnote = "";
        EncounterDAO encounterDAO = new EncounterDAO();
        String encounterIDSelection = "visituuid = ?";
        String[] encounterIDArgs = {visitUuid};
        Cursor encounterCursor = db.query("tbl_encounter", null, encounterIDSelection, encounterIDArgs, null, null, null);
        if (encounterCursor != null && encounterCursor.moveToFirst()) {
            do {
                if (encounterDAO.getEncounterTypeUuid("ENCOUNTER_VISIT_NOTE").equalsIgnoreCase(encounterCursor.getString(encounterCursor.getColumnIndexOrThrow("encounter_type_uuid")))) {
                    visitnote = encounterCursor.getString(encounterCursor.getColumnIndexOrThrow("uuid"));
                }
            } while (encounterCursor.moveToNext());

        }
        encounterCursor.close();
        String[] columns = {"value", " conceptuuid"};
        String visitSelection = "encounteruuid = ? and voided!='1' ";
        String[] visitArgs = {visitnote};
        Cursor visitCursor = db.query("tbl_obs", columns, visitSelection, visitArgs, null, null, null);
        if (visitCursor.moveToFirst()) {
            do {
                String dbConceptID = visitCursor.getString(visitCursor.getColumnIndex("conceptuuid"));
                String dbValue = visitCursor.getString(visitCursor.getColumnIndex("value"));
                parseData(dbConceptID, dbValue);
            } while (visitCursor.moveToNext());
        }
        visitCursor.close();
        downloaded = true;
    }

    @Override
    protected void onStart() {
        registerDownloadPrescription();
        callBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver), new IntentFilter(FILTER));
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (downloadPrescriptionService != null) {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(downloadPrescriptionService);
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleMessage(intent);
        }
    };

    private void handleMessage(Intent msg) {
        Log.i(TAG, "handleMessage: Entered");
        Bundle data = msg.getExtras();
        int check = 0;
        if (data != null) {
            check = data.getInt("Restart");
        }
        if (check == 100) {
            Log.i(TAG, "handleMessage: 100");
            diagnosisReturned = "";
            rxReturned = "";
            testsReturned = "";
            adviceReturned = "";
            additionalReturned = "";
            followUpDate = "";
            String[] columns = {"value", " conceptuuid"};
            String visitSelection = "encounteruuid = ? ";
            String[] visitArgs = {encounterUuid};
            Cursor visitCursor = db.query("tbl_obs", columns, visitSelection, visitArgs, null, null, null);
            if (visitCursor.moveToFirst()) {
                do {
                    String dbConceptID = visitCursor.getString(visitCursor.getColumnIndex("conceptuuid"));
                    String dbValue = visitCursor.getString(visitCursor.getColumnIndex("value"));
                    parseData(dbConceptID, dbValue);
                } while (visitCursor.moveToNext());
            }
            visitCursor.close();
        } else if (check == 200) {
            Log.i(TAG, "handleMessage: 200");
            String[] columns = {"concept_id"};
            String orderBy = "visit_id";

            //obscursor checks in obs table
            Cursor obsCursor = db.query("tbl_obs", columns, null, null, null, null, orderBy);

            //dbconceptid will store data found in concept_id

            if (obsCursor.moveToFirst() && obsCursor.getCount() > 1) {
                String dbConceptID = obsCursor.getString(obsCursor.getColumnIndex("conceptuuid"));

//                    if obsCursor founds something move to next
                while (obsCursor.moveToNext()) ;

                switch (dbConceptID) {
                    //case values for each prescription
                    case UuidDictionary.TELEMEDICINE_DIAGNOSIS:
                        Log.i(TAG, "found diagnosis");
                        break;
                    case UuidDictionary.JSV_MEDICATIONS:
                        Log.i(TAG, "found medications");
                        break;
                    case UuidDictionary.MEDICAL_ADVICE:
                        Log.i(TAG, "found medical advice");
                        break;
                    case UuidDictionary.ADDITIONAL_COMMENTS:
                        Log.i(TAG, "found additional comments");
                        break;
                    case UuidDictionary.REQUESTED_TESTS:
                        Log.i(TAG, "found tests");
                        break;
                    default:
                }
                obsCursor.close();
                //  addDownloadButton();
                //if any obs  found then end the visit
                //endVisit();

            } else {
                Log.i(TAG, "found sothing for test");

            }

        }

    }


/*
    private void addDownloadButton() {
        if (!downloadButton.isEnabled()) {
            downloadButton.setEnabled(true);
            downloadButton.setVisibility(View.VISIBLE);
        }
    }
*/

    private void isNetworkAvailable(Context context) {
        int flag = 0;

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {

                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        if (!isConnected) {
                            if (internetCheck != null) {
                                internetCheck.setIcon(R.mipmap.ic_data_on);
                                flag = 1;
                            }
                        }
                    }
                }
            }
        }

        if (flag == 0) {
            if (internetCheck != null) {
                internetCheck.setIcon(R.mipmap.ic_data_off);
            }

        }

    }

    public class DownloadPrescriptionService extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logger.logD(TAG, "Download prescription happen" + new SimpleDateFormat("yyyy MM dd_HH mm ss").format(Calendar.getInstance().getTime()));
            downloadPrescriptionDefault();
            downloadDoctorDetails();
        }
    }

    /**
     * @param uuid the visit uuid of the patient visit records is passed to the function.
     * @return boolean value will be returned depending upon if the row exists in the tbl_visit_attribute tbl
     */
    public boolean speciality_row_exist_check(String uuid) {
        boolean isExists = false;
        SQLiteDatabase db = AppConstants.inteleHealthDatabaseHelper.getReadableDatabase();
        db.beginTransaction();
        Cursor cursor = db.rawQuery("SELECT * FROM tbl_visit_attribute WHERE visit_uuid=?",
                new String[]{uuid});

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                isExists = true;
            }
        }
        cursor.close();
        db.setTransactionSuccessful();
        db.endTransaction();
        return isExists;
    }

    /**
     * @param webView       Webview object.
     * @param contentHeight Height of the overall contents of the HTML string in int datatype.
     */
    private void createWebPrintJob_Button(WebView webView, int contentHeight) {
        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);


        // Get a print adapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();
        Log.d("webview content height", "webview content height: " + contentHeight);

        if (contentHeight > 2683 && contentHeight <= 3000) {
            //medium size prescription...
            PrintAttributes.Builder pBuilder = new PrintAttributes.Builder();
            pBuilder.setMediaSize(PrintAttributes.MediaSize.ISO_B4);
            pBuilder.setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600));
            pBuilder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);
            // Create a print job with name and adapter instance
            String jobName = getString(R.string.app_name) + " Visit Summary";

            //To display the preview window to user...
            PrintJob printJob = printManager.print(jobName, printAdapter,
                    pBuilder.build());

//            PrintJob printJob = printManager.print(jobName, printAdapter,
//                    pBuilder.build());
        } else if (contentHeight == 0) {
            //in case of webview bug of 0 contents...
            PrintAttributes.Builder pBuilder = new PrintAttributes.Builder();
            pBuilder.setMediaSize(PrintAttributes.MediaSize.JIS_B4);
            pBuilder.setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600));
            pBuilder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);
            // Create a print job with name and adapter instance
            String jobName = getString(R.string.app_name) + " Visit Summary";

            //To display the preview window to user...
            PrintJob printJob = printManager.print(jobName, printAdapter,
                    pBuilder.build());

//            PrintJob printJob = printManager.print(jobName, printAdapter,
//                    pBuilder.build());
        } else if (contentHeight > 3000) {
            //large size prescription...
            PrintAttributes.Builder pBuilder = new PrintAttributes.Builder();
            pBuilder.setMediaSize(PrintAttributes.MediaSize.JIS_B4);
            pBuilder.setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600));
            pBuilder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);
            // Create a print job with name and adapter instance
            String jobName = getString(R.string.app_name) + " Visit Summary";

            //To display the preview window to user...
            PrintJob printJob = printManager.print(jobName, printAdapter,
                    pBuilder.build());

//            PrintJob printJob = printManager.print(jobName, printAdapter,
//                    pBuilder.build());
        } else {
            //small size prescription...
            // Create a print job with name and adapter instance
            String jobName = getString(R.string.app_name) + " Visit Summary";

            Log.d("PrintPDF", "PrintPDF");
            PrintAttributes.Builder pBuilder = new PrintAttributes.Builder();
            pBuilder.setMediaSize(PrintAttributes.MediaSize.NA_LETTER);
            pBuilder.setResolution(new PrintAttributes.Resolution("pdf", "pdf", 600, 600));
            pBuilder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);

            //To display the preview window to user...
            PrintJob printJob = printManager.print(jobName, printAdapter,
                    pBuilder.build());

        }
    }

    private void downloadDoctorDetails() {
        String visitnote = "";
        EncounterDAO encounterDAO = new EncounterDAO();
        String encounterIDSelection = "visituuid = ? ";
        String[] encounterIDArgs = {visitUuid};
        String encounter_type_uuid_comp = "bd1fbfaa-f5fb-4ebd-b75c-564506fc309e";// make the encounter_type_uuid as constant later on.
        Cursor encounterCursor = db.query("tbl_encounter", null, encounterIDSelection, encounterIDArgs, null, null, null);
        if (encounterCursor != null && encounterCursor.moveToFirst()) {
            do {
                if (encounter_type_uuid_comp.equalsIgnoreCase(encounterCursor.getString
                        (encounterCursor.getColumnIndexOrThrow("encounter_type_uuid")))) {
                    visitnote = encounterCursor.getString(encounterCursor.getColumnIndexOrThrow("uuid"));
                }
            } while (encounterCursor.moveToNext());

        }
        encounterCursor.close();
        String[] columns = {"value", " conceptuuid"};
        String visitSelection = "encounteruuid = ? and voided!='1' ";
        String[] visitArgs = {visitnote};
        Cursor visitCursor = db.query("tbl_obs", columns, visitSelection, visitArgs, null, null, null);
        if (visitCursor.moveToFirst()) {
            do {
                String dbConceptID = visitCursor.getString(visitCursor.getColumnIndex("conceptuuid"));
                String dbValue = visitCursor.getString(visitCursor.getColumnIndex("value"));
                parseDoctorDetails(dbValue);
            } while (visitCursor.moveToNext());
        }
        visitCursor.close();
    }


    /**
     * @param input : pulled web presciption data of @type String
     * @return : formatted HTML string.
     */
    private String stringToWeb_sms(String input) {
        String formatted = "";
        if (input != null && !input.isEmpty()) {

            String para_open = "<b style=\"font-size:11pt; margin: 0px; padding: 0px;\">";
            String para_close = "</b><br>";
            formatted = para_open + "- " +
                    input.replaceAll("\n", para_close + para_open + "- ")
                    + para_close;
        }
        return formatted;
    }

    public String parseDateToddMMyyyy(String time) {
        String inputPattern = "dd-MM-yyyy";
        String outputPattern = "dd MMM yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern, Locale.ENGLISH);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


}