package org.intelehealth.intelesafe.activities.chooseLanguageActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.activities.homeActivity.HomeActivity;
import org.intelehealth.intelesafe.activities.introActivity.IntroActivity;
import org.intelehealth.intelesafe.utilities.Logger;
import org.intelehealth.intelesafe.utilities.SessionManager;

import java.util.ArrayList;

public class ChooseLanguageActivity extends AppCompatActivity {

    String langaugeSelected;
    ListView LanguageListView;
    Button SaveButton;
    SessionManager sessionManager = null;
    CheckedTextView v;
    String LOG_TAG = "ChooseLanguageActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);
        initViews();
        PopulatingLanguages();
        LanguageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                v = (CheckedTextView) view;
                boolean currentCheck = v.isChecked();
                if(currentCheck == true) {
                    langaugeSelected = v.getText().toString();
                    sessionManager.setAppLanguage(langaugeSelected);
                    SaveButton.setEnabled(true);
                }
            }
        });
        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sessionManager.isFirstTimeLaunch()) {
                    Logger.logD(LOG_TAG, "Starting setup");
                    Intent intent = new Intent(ChooseLanguageActivity.this, IntroActivity.class);
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
    }
    public void PopulatingLanguages()
    {
        LanguageListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayList<String> languages_list = new ArrayList<String>();
        languages_list.add("हिंदी");
        languages_list.add("English");
        languages_list.add("ગુજરાતી");
        languages_list.add("मराठी");
        languages_list.add("മലയാളം");
        languages_list.add("ਪੰਜਾਬੀ");
        languages_list.add("বাংলা");
        languages_list.add("తెలుగు");
        languages_list.add("தமிழ்");
        ArrayAdapter<String> language_adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked,languages_list);
        LanguageListView.setAdapter(language_adapter);
    }
}
