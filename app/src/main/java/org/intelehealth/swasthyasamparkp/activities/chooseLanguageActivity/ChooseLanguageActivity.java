package org.intelehealth.swasthyasamparkp.activities.chooseLanguageActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.intelehealth.swasthyasamparkp.R;
import org.intelehealth.swasthyasamparkp.activities.homeActivity.HomeActivity;
import org.intelehealth.swasthyasamparkp.activities.loginActivity.LoginActivity;
import org.intelehealth.swasthyasamparkp.utilities.Logger;
import org.intelehealth.swasthyasamparkp.utilities.SessionManager;

import java.util.ArrayList;
import java.util.Locale;

public class ChooseLanguageActivity extends AppCompatActivity {

    String langaugeSelected;
    ListView LanguageListView;
    Button SaveButton;
    ImageView BackImage;
    ArrayList<String> languages_list;
    SessionManager sessionManager = null;
    CheckedTextView v;
    String LOG_TAG = "ChooseLanguageActivity";
    String systemLanguage = Resources.getSystem().getConfiguration().locale.getLanguage();
    int getSystemLanguagePosition;
    String appLanguage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);
        initViews();
        PopulatingLanguages();
        appLanguage = sessionManager.getAppLanguage();
        if (!appLanguage.equalsIgnoreCase("")) {
            setLocale(appLanguage);
        }
        if(!sessionManager.isFirstTimeLaunch())
        {
            BackImage.setVisibility(View.VISIBLE);
            BackImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
            if(sessionManager.getAppLanguage()=="hi")
            {
                LanguageListView.setItemChecked(0,true);
                LanguageListView.setSelection(0);
            }
            else
            {
                LanguageListView.setItemChecked(1,true);
                LanguageListView.setSelection(1);
            }
        }
        if(sessionManager.isFirstTimeLaunch())
        {
            LanguageListView.setItemChecked(getSystemLanguagePosition,true);
            LanguageListView.setSelection(getSystemLanguagePosition);
        }

        LanguageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                v = (CheckedTextView) view;
                boolean currentCheck = v.isChecked();
                if(currentCheck == true) {
                    langaugeSelected = v.getText().toString();
                    sessionManager.setAppLanguage(langaugeSelected);
                }
            }
        });
        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.isFirstTimeLaunch()) {
                    Logger.logD(LOG_TAG, "Starting setup");
//                    Intent intent = new Intent(ChooseLanguageActivity.this, IntroActivity.class);
                    Intent intent = new Intent(ChooseLanguageActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(ChooseLanguageActivity.this, HomeActivity.class);
                    intent.putExtra("from", "splash");
                    intent.putExtra("username", "");
                    intent.putExtra("password", "");
                    startActivity(intent);
                }
            }
        });    }
    public void initViews()
    {
        sessionManager = new SessionManager(ChooseLanguageActivity.this);
        LanguageListView = findViewById(R.id.language_listview);
        SaveButton = findViewById(R.id.save_button);
        BackImage = findViewById(R.id.backButton);
        getSystemLanguagePosition = sessionManager.getSystemLanguage(systemLanguage);
    }
    public void PopulatingLanguages()
    {
        LanguageListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayList<String> languages_list = new ArrayList<String>();
        languages_list.add("हिंदी");
        languages_list.add("English");
       // languages_list.add("ગુજરાતી");
       // languages_list.add("मराठी");
       /* languages_list.add("മലയാളം");
        languages_list.add("ਪੰਜਾਬੀ");
        languages_list.add("বাংলা");
        languages_list.add("తెలుగు");
        languages_list.add("தமிழ்");*/
        ArrayAdapter<String> language_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked,languages_list);
        LanguageListView.setAdapter(language_adapter);
    }
    public void setLocale(String appLanguage)
    {
        Locale locale = new Locale(appLanguage);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

}
