package org.intelehealth.intelesafe.activities.videosActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.activities.pdflist.PdfListActivity;
import org.intelehealth.intelesafe.activities.videolist.VideoListActivity;
import org.intelehealth.intelesafe.utilities.SessionManager;

public class VideosActivity extends AppCompatActivity {

    RelativeLayout home_quarantine_guidelines, educational_videos;
    SessionManager sessionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        initViews();
        home_quarantine_guidelines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (sessionManager.getAppLanguage().equalsIgnoreCase("en")) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/playlist?list=PLY7f0i-HnvJ17kaGjtpPzQHO70GLqSW8z")));
//                } else {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/playlist?list=PLY7f0i-HnvJ1rONMTGFU03k-5RvNIsjvU")));
//                }
                VideoListActivity.start(VideosActivity.this);
            }
        });
        educational_videos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if (sessionManager.getAppLanguage().equalsIgnoreCase("en")) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/playlist?list=PLY7f0i-HnvJ2jx8sWpScxo95hxRkV66Qq")));
                } else {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/playlist?list=PLY7f0i-HnvJ2aWCpFe2C690qomg9p2zmu")));
                }*/
                PdfListActivity.start(VideosActivity.this);
            }
        });
    }

    private void initViews() {
        home_quarantine_guidelines = findViewById(R.id.button_home_quarantine);
        educational_videos = findViewById(R.id.button_educational_videos);
        sessionManager = new SessionManager(VideosActivity.this);
        Intent intent = getIntent();
        getSupportActionBar().setTitle(getString(R.string.post_covid_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
