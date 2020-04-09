package org.intelehealth.intelesafe.activities.languageActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Locale;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.activities.homeActivity.HomeActivity;
import org.intelehealth.intelesafe.activities.introActivity.IntroActivity;
import org.intelehealth.intelesafe.app.IntelehealthApplication;
import org.intelehealth.intelesafe.utilities.SessionManager;

/**
 * Created by Sagar Shimpi
 * modified by Deepak Sachdeva
 */
public class LanguageActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;

    //    private Toolbar toolbar;
    private LinearLayout rlEnglish;
    private LinearLayout rlHindi;
    private LinearLayout rlMarathi;

    private LinearLayout rlBangali;
    private LinearLayout rlGujarati;
    private LinearLayout rlMalayalam;
    private LinearLayout rlTamil;

    //    LinearLayout rlKannada;
    private Button btnSave;
    private String selectedLang = "";
    private boolean isLangSelected;

    private String previousView = "";

    IntelehealthApplication intelehealthApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        context = LanguageActivity.this;
        intelehealthApplication = (IntelehealthApplication) getApplicationContext();
        initUI();
        setClickListenerEvents();

        selectedLang = "en";
        Intent intent = this.getIntent(); // The intent was passed to the activity
        if (intent != null) {
            previousView = intent.getStringExtra("previous_view");
        }
    }

    /**
     * to set UI ids
     */
    private void initUI() {
//        toolbar = findViewById(R.id.toolbar);
        rlEnglish = findViewById(R.id.rl_english);
        rlHindi = findViewById(R.id.rl_hindi);
        rlMarathi = findViewById(R.id.rl_marathi);

        rlBangali = findViewById(R.id.rl_bangali);
        rlGujarati = findViewById(R.id.rl_gujarati);
        rlMalayalam = findViewById(R.id.rl_malayalam);
        rlTamil = findViewById(R.id.rl_tamil);
//        rlKannada = findViewById(R.id.rl_kannada);
        btnSave = findViewById(R.id.btnSave);
    }

    /**
     * click events
     */
    private void setClickListenerEvents() {
        rlHindi.setOnClickListener(this);
        rlEnglish.setOnClickListener(this);
        rlMarathi.setOnClickListener(this);
        rlBangali.setOnClickListener(this);
        rlGujarati.setOnClickListener(this);
        rlMalayalam.setOnClickListener(this);
        rlTamil.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }

    public void setLocale(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        final Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        saveLocale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }


    public void loadLocale() {
        String langPref = "Language";
        SharedPreferences prefs = context.getSharedPreferences("Intelehealth", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        if (language != null) {
            setLocale(language);
        }
    }

    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = context.getSharedPreferences("Intelehealth", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        prefs.getAll();
        editor.apply();

        SessionManager sessionManager = new SessionManager(IntelehealthApplication.getAppContext());
        sessionManager.setCurrentLang(lang);

    }

    @Override
    public void onClick(View view) {
        int blue = getResources().getColor(R.color.blue_transparent);
        int white = getResources().getColor(R.color.white);
        switch (view.getId()) {
            case R.id.rl_hindi:
                changeColorOfSelectedLang(blue, white, white, white, white, white, white, "hi");
                break;
            case R.id.rl_english:
                changeColorOfSelectedLang(white, blue, white, white, white, white, white, "en");
                break;
            case R.id.rl_marathi:
                changeColorOfSelectedLang(white, white, blue, white, white, white, white, "mr");
                break;
            case R.id.rl_bangali:
                changeColorOfSelectedLang(white, white, white, blue, white, white, white, "en");
                break;
            case R.id.rl_gujarati:
                changeColorOfSelectedLang(white, white, white, white, blue, white, white, "en");
                break;
            case R.id.rl_malayalam:
                changeColorOfSelectedLang(white, white, white, white, white, blue, white, "en");
                break;
            case R.id.rl_tamil:
                changeColorOfSelectedLang(white, white, white, white, white, white, blue, "en");
                break;
            case R.id.btnSave:
                moveToHomeOrIntro();
                break;
        }
    }

    /**
     * condition to check next Activity
     */
    private void moveToHomeOrIntro() {
        if (isLangSelected) {
            setLocale(selectedLang);
            loadLocale();
            if (previousView != null && !previousView.isEmpty() && previousView.equals("home")) {
                Intent intent = new Intent(context, HomeActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, IntroActivity.class);
                startActivity(intent);
            }
        } else {
            Toast.makeText(context, "Please select language", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * To change the color of selected language button
     *
     * @param hindiColor    hindi option
     * @param englishColor  eng option
     * @param marathiColor  marathi option
     * @param bangaliColor  bangali option
     * @param gujaratiColor gujarati option
     * @param malayalmColor malayalm option
     * @param tamilColor    tamil option
     * @param selectedLang  selected language
     */
    private void changeColorOfSelectedLang(int hindiColor, int englishColor, int marathiColor, int bangaliColor,
                                           int gujaratiColor, int malayalmColor, int tamilColor, String selectedLang) {
        isLangSelected = true;

        rlHindi.setBackgroundColor(hindiColor);
        rlEnglish.setBackgroundColor(englishColor);
        rlMarathi.setBackgroundColor(marathiColor);
        rlBangali.setBackgroundColor(bangaliColor);
        rlGujarati.setBackgroundColor(gujaratiColor);
        rlMalayalam.setBackgroundColor(malayalmColor);
        rlTamil.setBackgroundColor(tamilColor);

        this.selectedLang = selectedLang;
    }
}
