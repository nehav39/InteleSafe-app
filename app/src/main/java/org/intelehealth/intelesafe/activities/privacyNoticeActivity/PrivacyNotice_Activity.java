package org.intelehealth.intelesafe.activities.privacyNoticeActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.activities.signupActivity.SignupActivity;
import org.intelehealth.intelesafe.app.AppConstants;
import org.intelehealth.intelesafe.utilities.FileUtils;
import org.intelehealth.intelesafe.utilities.SessionManager;

/*
* Created By: Prajwal Waingankar
* 
* */
public class PrivacyNotice_Activity extends AppCompatActivity {
    TextView privacy_textview;
    SessionManager sessionManager = null;
    private boolean hasLicense = false;
//    RadioGroup radiogrp;
//    RadioButton radiobtn;
//    RadioButton radio_acc;
//    RadioButton radio_rej;
    CheckBox chbAgreePrivacy;
    TextView txt_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_notice_2);
        setTitle(getString(R.string.privacy_notice_title));

        /*
         * Toolbar which displays back arrow on action bar
         * Add the below lines for every activity*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sessionManager = new SessionManager(this);
        privacy_textview = findViewById(R.id.privacy_text);

//        radiogrp = findViewById(R.id.radio_privacy_grp);
//        radio_acc = findViewById(R.id.radio_accept);
//        radio_rej = findViewById(R.id.radio_reject);

        chbAgreePrivacy = findViewById(R.id.chb_agree_privacy);

        txt_next = findViewById(R.id.txt_privacy);

        if (!sessionManager.getLicenseKey().isEmpty())
            hasLicense = true;

        //Check for license key and load the correct config file
        try {
            JSONObject obj = null;
            if (hasLicense) {
                obj = new JSONObject(FileUtils.readFileRoot(AppConstants.CONFIG_FILE_NAME, this)); //Load the config file

            } else {
                obj = new JSONObject(String.valueOf(FileUtils.encodeJSON(this, AppConstants.CONFIG_FILE_NAME)));
            }

//            SharedPreferences sharedPreferences = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
//            if(sharedPreferences.getAll().values().contains("cb"))
            Locale current = getResources().getConfiguration().locale;
            if (current.toString().equals("cb")) {
                String privacy_string = obj.getString("privacyNoticeText_Cebuano");
                if (privacy_string.isEmpty()) {
                    privacy_string = obj.getString("privacyNoticeText");
                    privacy_textview.setText(privacy_string);
                } else {
                    privacy_textview.setText(privacy_string);
                }

            } else if (current.toString().equals("hi")) {
                String privacy_string = obj.getString("privacyNoticeText_Hindi");
                if (privacy_string.isEmpty()) {
                    privacy_string = obj.getString("privacyNoticeText");
                    privacy_textview.setText(privacy_string);
                } else {
                    privacy_textview.setText(privacy_string);
                }

            } else if (current.toString().equals("mr")) {
                String privacy_string = obj.getString("privacyNoticeText_Marathi");
                if (privacy_string.isEmpty()) {
                    privacy_string = obj.getString("privacyNoticeText");
                    privacy_textview.setText(privacy_string);
                } else {
                    privacy_textview.setText(privacy_string);
                }

            } else if (current.toString().equals("or")) {
                String privacy_string = obj.getString("privacyNoticeText_Odiya");
                if (privacy_string.isEmpty()) {
                    privacy_string = obj.getString("privacyNoticeText");
                    privacy_textview.setText(privacy_string);
                } else {
                    privacy_textview.setText(privacy_string);
                }

            } else {
                String privacy_string = obj.getString("privacyNoticeText");
                changeColorOfText(privacy_string);
            }

            txt_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (chbAgreePrivacy.isChecked()) {
//                            sessionManager.setOfllineOpenMRSID("");
                        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                        intent.putExtra("privacy", "Accept"); //privacy value send to identificationActivity
                        intent
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.privacy_toast), Toast.LENGTH_LONG).show();
                    }
                }
            });


        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Toast.makeText(getApplicationContext(), "JsonException" + e, Toast.LENGTH_LONG).show();
        }
    }

    private void changeColorOfText(String privacy_string) {
        //Highlighting Text
        String second = "Privacy notice and consent form";
        Spannable spannable = colorized(privacy_string, second, Color.RED);
        spannable = textSize(spannable, privacy_string, "How we protect information");
        spannable = boldSize(spannable, privacy_string, "How we protect information");

        spannable = textSize(spannable, privacy_string, "Amendments");
        spannable = boldSize(spannable, privacy_string, "Amendments");

        spannable = boldSize(spannable, privacy_string, "Telehealth Innovations Foundation Privacy Notice");
        spannable = boldSize(spannable, privacy_string, "INFORMATION WE COLLECT");
        spannable = boldSize(spannable, privacy_string, "Use");
        spannable = boldSize(spannable, privacy_string, "Your contact information may be used to");
        spannable = boldSize(spannable, privacy_string, "Intelehealth generally provides Personal Information to third parties where:");
        spannable = boldSize(spannable, privacy_string, "We may also disclose your personal information:");
        spannable = boldSize(spannable, privacy_string, "Protection Measures");
        spannable = boldSize(spannable, privacy_string, "Access and Correction");
        spannable = boldSize(spannable, privacy_string, "Privacy notice and consent form");



        privacy_textview.setText(spannable, TextView.BufferType.SPANNABLE);
    }

    public static Spannable colorized(final String text, final String word, final int argb) {
        final Spannable spannable = new SpannableString(text);
        int substringStart = 0;
        int start;
        while ((start = text.indexOf(word, substringStart)) >= 0) {
            spannable.setSpan(
                    new ForegroundColorSpan(argb), start, start + word.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            substringStart = start + word.length();
        }
        return spannable;
    }

    private static Spannable textSize(Spannable spannable, String text, String word){
        int substringStart = 0;
        int start;
        while ((start = text.indexOf(word, substringStart)) >= 0) {
            spannable.setSpan(new RelativeSizeSpan(1.5f), start, start + word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            substringStart = start + word.length();
        }
        return spannable;
    }

    private static Spannable boldSize(Spannable spannable, String text, String word){
        int substringStart = 0;
        int start;
        while ((start = text.indexOf(word, substringStart)) >= 0) {
            spannable.setSpan(new StyleSpan(Typeface.BOLD), start, start + word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            substringStart = start + word.length();
        }
        return spannable;
    }
}
