package org.intelehealth.intelesafe.activities.pdflist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.utilities.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class PdfListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TextView tvNoData;
    private SessionManager sessionManager;

    public static void start(Context context) {
        Intent starter = new Intent(context, PdfListActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        getSupportActionBar().setTitle(R.string.info_about_covid);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);

        sessionManager = new SessionManager(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        List<PdfItem> documents = getDocuments();
        PdfAdapter adapter = new PdfAdapter(this, documents);
        adapter.setClickListener(new PdfAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == RecyclerView.NO_POSITION)
                    return;
                PdfItem item = documents.get(position);
                PdfViewerActivity.start(PdfListActivity.this, item.url, item.title);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private List<PdfItem> getDocuments() {
        ArrayList<PdfItem> items = new ArrayList<>();
        if (sessionManager.getAppLanguage().equalsIgnoreCase("hi")) {
            items.add(new PdfItem("डबल मास्किंग", "https://swasthyasampark.intelehealth.org/IEC/Double_masking-Hindi.pdf"));
            items.add(new PdfItem("COVID19 के बाद देखभाल", "https://swasthyasampark.intelehealth.org/IEC/Post_COVID19_care-Hindi.pdf"));
            items.add(new PdfItem("स्वास्थ्यकर्मियों के लिए मानसिक स्वास्थ्य", "https://swasthyasampark.intelehealth.org/IEC/Mental_Health_for_Healthworkers_Hindi .pdf"));
            items.add(new PdfItem("स्तनपान और COVID19", "https://swasthyasampark.intelehealth.org/IEC/Breastfeeding_and_COVID19-Hindi.pdf"));
        } else {
            items.add(new PdfItem("Double up Protection by Double masking_Dos and Don't_Jharkhand", "https://swasthyasampark.intelehealth.org/IEC/Double_masking_Jharkhand_2.pdf"));
            items.add(new PdfItem("Double up Protection by Double masking_Dos and Don't_ MP", "https://swasthyasampark.intelehealth.org/IEC/Double_masking_MP.pdf"));
            items.add(new PdfItem("How to dispose off a mask_MP", "https://swasthyasampark.intelehealth.org/IEC/How_to_dispose_off_a_mask_MP.pdf"));
            items.add(new PdfItem("Post COVID_MP", "https://swasthyasampark.intelehealth.org/IEC/Post_COVID_MP.pdf"));
            items.add(new PdfItem("Tips to Maintain Mental Health Jharkhand", "https://swasthyasampark.intelehealth.org/IEC/Tips_to_Maintain_Mental_Health-Jharkhand.pdf"));
            items.add(new PdfItem("Tips to Maintain Mental Health MP", "https://swasthyasampark.intelehealth.org/IEC/Tips_to_Maintain_Mental_Health_MP.pdf"));
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