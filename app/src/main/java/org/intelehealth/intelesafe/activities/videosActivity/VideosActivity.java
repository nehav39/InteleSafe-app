package org.intelehealth.intelesafe.activities.videosActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.activities.pdflist.PdfAdapter;
import org.intelehealth.intelesafe.activities.pdflist.PdfItem;
import org.intelehealth.intelesafe.activities.pdflist.PdfListActivity;
import org.intelehealth.intelesafe.activities.pdflist.PdfViewerActivity;
import org.intelehealth.intelesafe.activities.videolist.VideoAdapter;
import org.intelehealth.intelesafe.activities.videolist.VideoItem;
import org.intelehealth.intelesafe.activities.videolist.VideoListActivity;
import org.intelehealth.intelesafe.activities.videolist.VideoPlayerActivity;
import org.intelehealth.intelesafe.utilities.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class VideosActivity extends AppCompatActivity {

    RelativeLayout home_quarantine_guidelines, educational_videos;
    SessionManager sessionManager;
    private RecyclerView recyclerViewVideos;
    private RecyclerView recyclerViewPDF;
    private static final String EXTRA_URI = "EXTRA_URI";
    private static final String EXTRA_TITLE = "EXTRA_TITLE";

    public static void start(Context context) {
        Intent starter = new Intent(context, VideoListActivity.class);
        context.startActivity(starter);
    }

    public static void startPDF(Context context, String uri, String title) {
        Intent starter = new Intent(context, PdfViewerActivity.class);
        starter.putExtra(EXTRA_URI, uri);
        starter.putExtra(EXTRA_TITLE, title);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);
        initViews();
//        home_quarantine_guidelines.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if (sessionManager.getAppLanguage().equalsIgnoreCase("en")) {
////                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/playlist?list=PLY7f0i-HnvJ17kaGjtpPzQHO70GLqSW8z")));
////                } else {
////                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/playlist?list=PLY7f0i-HnvJ1rONMTGFU03k-5RvNIsjvU")));
////                }
//                VideoListActivity.start(VideosActivity.this);
//            }
//        });
//        educational_videos.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                /*if (sessionManager.getAppLanguage().equalsIgnoreCase("en")) {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/playlist?list=PLY7f0i-HnvJ2jx8sWpScxo95hxRkV66Qq")));
//                } else {
//                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtube.com/playlist?list=PLY7f0i-HnvJ2aWCpFe2C690qomg9p2zmu")));
//                }*/
//                PdfListActivity.start(VideosActivity.this);
//            }
//        });


    }

    private void initViews() {
        home_quarantine_guidelines = findViewById(R.id.button_home_quarantine);
        educational_videos = findViewById(R.id.button_educational_videos);
        sessionManager = new SessionManager(VideosActivity.this);
        Intent intent = getIntent();
        getSupportActionBar().setTitle(getString(R.string.post_covid_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initVideos();
        initPDF();

    }

    private void initPDF() {

        recyclerViewPDF = findViewById(R.id.recyclerViewPDF);
        recyclerViewPDF.setNestedScrollingEnabled(false);
        recyclerViewPDF.setHasFixedSize(true);
        recyclerViewPDF.setLayoutManager(new GridLayoutManager(this, 2));
        List<PdfItem> documents = getDocuments();
        PdfAdapter adapter = new PdfAdapter(this, documents);
        adapter.setClickListener(new PdfAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == RecyclerView.NO_POSITION)
                    return;
                PdfItem item = documents.get(position);
                VideosActivity.startPDF(VideosActivity.this, item.url, item.title);
            }
        });
        recyclerViewPDF.setAdapter(adapter);

    }

    private void initVideos() {

        recyclerViewVideos = findViewById(R.id.recyclerViewVideos);
        recyclerViewVideos.setNestedScrollingEnabled(false);
        recyclerViewVideos.setLayoutManager(new GridLayoutManager(this, 2));
        List<VideoItem> videos = getVideos();
        VideoAdapter adapter = new VideoAdapter(this, videos);
        adapter.setClickListener(new VideoAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == RecyclerView.NO_POSITION)
                    return;
                VideoItem videoItem = videos.get(position);
                VideoPlayerActivity.start(VideosActivity.this, videoItem.url);
            }
        });
        recyclerViewVideos.setAdapter(adapter);
    }

    private List<VideoItem> getVideos() {
        ArrayList<VideoItem> videoItems = new ArrayList<>();
        videoItems.add(new VideoItem(getResources().getString(R.string.how_to_use_spo2), "https://swasthyasampark.intelehealth.org/IEC/pulse_oximeter.mp4"));
        videoItems.add(new VideoItem(getResources().getString(R.string.how_to_use_thermo), "https://swasthyasampark.intelehealth.org/IEC/digital_thermometer.mp4"));
        return videoItems;
    }

    private List<PdfItem> getDocuments() {
        ArrayList<PdfItem> items = new ArrayList<>();
        if (sessionManager.getAppLanguage().equalsIgnoreCase("hi")) {
            items.add(new PdfItem("डबल मास्किंग", "https://swasthyasampark.intelehealth.org/IEC/Double_masking-Hindi.pdf"));
            items.add(new PdfItem("COVID19 के बाद देखभाल", "https://swasthyasampark.intelehealth.org/IEC/Post_COVID19_care-Hindi.pdf"));
            items.add(new PdfItem("स्वास्थ्यकर्मियों के लिए मानसिक स्वास्थ्य", "https://swasthyasampark.intelehealth.org/IEC/Mental_Health_for_Healthworkers_Hindi .pdf"));
            items.add(new PdfItem("स्तनपान और COVID19", "https://swasthyasampark.intelehealth.org/IEC/Breastfeeding_and_COVID19-Hindi.pdf"));
        } else {
            items.add(new PdfItem("Double up Protection by Double masking - Dos and Don't", "https://swasthyasampark.intelehealth.org/IEC/Double_masking_Jharkhand_2.pdf"));
            items.add(new PdfItem("Double up Protection by Double masking - Dos and Don't", "https://swasthyasampark.intelehealth.org/IEC/Double_masking_MP.pdf"));
            items.add(new PdfItem("How to dispose off a mask", "https://swasthyasampark.intelehealth.org/IEC/How_to_dispose_off_a_mask_MP.pdf"));
            items.add(new PdfItem("Post COVID", "https://swasthyasampark.intelehealth.org/IEC/Post_COVID_MP.pdf"));
            items.add(new PdfItem("Tips to Maintain Mental Health", "https://swasthyasampark.intelehealth.org/IEC/Tips_to_Maintain_Mental_Health-Jharkhand.pdf"));
            items.add(new PdfItem("Tips to Maintain Mental Health", "https://swasthyasampark.intelehealth.org/IEC/Tips_to_Maintain_Mental_Health_MP.pdf"));
            items.add(new PdfItem("FAQ about breastfeeding and covid", "https://swasthyasampark.intelehealth.org/IEC/faqs-breastfeeding-and-covid-19.pdf"));
        }
        return items;
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
