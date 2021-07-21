package org.intelehealth.swasthyasamparkp.activities;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class WebViewLocaleFixActivity extends AppCompatActivity {
    @Override
    protected void onStop() {
        super.onStop();
        fixLocale();
    }

    @Override
    public void onBackPressed() {
        fixLocale();
        super.onBackPressed();
    }

    /**
     * The locale configuration of the activity context and the global application context gets overridden with the first language the app supports.
     */
    public void fixLocale() {
        loadLocale();
    }

    public void setLocale(String lang) {
        if (lang.equalsIgnoreCase(""))
            return;
        final Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }


    public void loadLocale() {
        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("Intelehealth", Activity.MODE_PRIVATE);
        String language = prefs.getString(langPref, "");
        if (language != null) {
            setLocale(language);
        }
    }
}