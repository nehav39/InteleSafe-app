package org.intelehealth.intelesafe.activities.languageActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.Locale;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.activities.introActivity.IntroActivity;
import org.intelehealth.intelesafe.app.IntelehealthApplication;
import org.intelehealth.intelesafe.utilities.SessionManager;

/**
 * Created by Sagar Shimpi
 */
public class LanguageActivity extends AppCompatActivity {

    Context context;

    Toolbar toolbar;
    LinearLayout rlEnglish;
    LinearLayout rlHindi;
    LinearLayout rlMarathi;
//    LinearLayout rlKannada;
//    Button btnSelect;
    FloatingActionButton floatingActionButton;
    private String selectedLang = "";
    private String isLangSelected = "false";

    IntelehealthApplication intelehealthApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        context = LanguageActivity.this;
        intelehealthApplication = (IntelehealthApplication) getApplicationContext();

        toolbar = findViewById(R.id.toolbar);
        rlEnglish = findViewById(R.id.rl_english);
        rlHindi = findViewById(R.id.rl_hindi);
        rlMarathi = findViewById(R.id.rl_marathi);
//        rlKannada = findViewById(R.id.rl_kannada);
//        btnSelect = findViewById(R.id.button_select);
        floatingActionButton = findViewById(R.id.fab);

        selectedLang = "en";

        rlEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isLangSelected = "true";
                rlEnglish.setBackgroundColor(getResources().getColor(R.color.blue_transparent));
                rlHindi.setBackgroundColor(getResources().getColor(R.color.white));
                rlMarathi.setBackgroundColor(getResources().getColor(R.color.white));

                selectedLang = "en";

            }
        });

        rlHindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isLangSelected = "true";
                rlEnglish.setBackgroundColor(getResources().getColor(R.color.white));
                rlHindi.setBackgroundColor(getResources().getColor(R.color.blue_transparent));
                rlMarathi.setBackgroundColor(getResources().getColor(R.color.white));

                selectedLang = "hi";
            }
        });

        rlMarathi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isLangSelected = "true";
                rlEnglish.setBackgroundColor(getResources().getColor(R.color.white));
                rlHindi.setBackgroundColor(getResources().getColor(R.color.white));
                rlMarathi.setBackgroundColor(getResources().getColor(R.color.blue_transparent));

                selectedLang = "mr";
            }
        });


        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isLangSelected.equals("true")) {
                    setLocale(selectedLang);
                    loadLocale();
                    Intent intent = new Intent(context, IntroActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, R.string.please_select_language_, Toast.LENGTH_LONG).show();
                }

            }
        });

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

        SessionManager sessionManager = null;
        sessionManager = new SessionManager(IntelehealthApplication.getAppContext());
        sessionManager.setCurrentLang(lang);

    }

}
