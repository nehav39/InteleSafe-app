package org.intelehealth.intelesafe.activities.CardGuidelines;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.intelehealth.intelesafe.R;

import java.util.ArrayList;
import java.util.List;

public class Card_Guidelines extends AppCompatActivity {
    RecyclerView recyclerView;
    Context context = Card_Guidelines.this;
    List<Model_CardGuidelines> model_cardGuidelines, model_cardGuidelines_1, model_cardGuidelines_2,
            model_cardGuidelines_3, model_cardGuidelines_4;
    RecyclerAdapter_CardGuidelines recyclerAdapter_cardGuidelines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card__guidelines);
//        setTitle("Card Guidelines");

        /*
         * Toolbar which displays back arrow on action bar
         * Add the below lines for every activity*/
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });

        Bundle bundle = getIntent().getExtras();
        model_cardGuidelines = new ArrayList<>();
        model_cardGuidelines_1 = new ArrayList<>();
        model_cardGuidelines_2 = new ArrayList<>();
        model_cardGuidelines_3 = new ArrayList<>();
        model_cardGuidelines_4 = new ArrayList<>();
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerView = findViewById(R.id.card_guidelines_recycler);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(Card_Guidelines.this, LinearLayoutManager.VERTICAL, false));

        if(bundle != null) {
            if (bundle.containsKey("PPE_1")) {
                model_cardGuidelines.add(new Model_CardGuidelines("Introduction", "How does infection spread? What are types of PPE (Personal Protective Equipment)? How to properly wear (don) & remove (doff) PPE?",
                        "https://www.intelehealth.org/blog/2020/4/10/introduction-to-personal-protection-measures-for-health-workers"));
                model_cardGuidelines.add(new Model_CardGuidelines("Hand Hygiene", "How to wash hands with soap and water and How to hand rub with sanitizer using WHO technique. Video tutorials by Johns Hopkins Medicine",
                        "https://www.intelehealth.org/blog/2020/4/10/proper-hand-hygiene-for-health-workers"));
                model_cardGuidelines.add(new Model_CardGuidelines("What kind of PPE to wear in which settings?", "This section explains the kind of PPE that needs to be used in various healthcare settings by different cadres of health workers. The WHO recommends that PPE should be used based on the risk of exposure (e.g., type of activity) and the transmission dynamics of the pathogen (e.g., contact, droplet or aerosol).",
                        "https://www.intelehealth.org/ppe-guidelines"));
                recyclerAdapter_cardGuidelines =
                        new RecyclerAdapter_CardGuidelines
                                (context, model_cardGuidelines, bundle.getString("PPE_1"));
                recyclerView.setAdapter(recyclerAdapter_cardGuidelines);
            }
            else if (bundle.containsKey("PPE_2")) {
                model_cardGuidelines_1.add(new Model_CardGuidelines("What are the practice guidelines to extend the life of my N95 respirator and use it multiple times (extended use & limited reuse)? What is the best way to sterilize & reuse an N95 mask?", "Descrip B2", "https://intelehealth115235113.org/384-2/what-are-the-practice-guidelines-for-extended-use-and-limited-reuse-of-n95-respirators-reuse-without-sterilization/\n" +
                        "https://intelehealth115235113.org/384-2/what-is-the-best-way-to-sterilize-n95-masks-so-i-can-re-use-them/"));
                model_cardGuidelines_1.add(new Model_CardGuidelines("What is the best way to reuse a surgical mask?", "Descrip B2","https://intelehealth115235113.org/384-2/what-is-the-best-way-to-reuse-a-surgical-mask/"));
                model_cardGuidelines_1.add(new Model_CardGuidelines("What can I use as a gown to protect myself if there are no surgical gowns available?", "Descrip B3" , "https://intelehealth115235113.org/384-2/surgical-gown-alternatives/"));
                model_cardGuidelines_1.add(new Model_CardGuidelines("How can I make a face shield with commonly available resources?", "Descrip B3" , "https://intelehealth115235113.org/384-2/how-can-i-make-a-face-shield-or-similar-for-health-care-workers/"));
                recyclerAdapter_cardGuidelines =
                        new RecyclerAdapter_CardGuidelines
                                (context, model_cardGuidelines_1, bundle.getString("PPE_2"));
                recyclerView.setAdapter(recyclerAdapter_cardGuidelines);
            }
            else if (bundle.containsKey("PPE_3")) {
                model_cardGuidelines_2.add(new Model_CardGuidelines("Title C1", "Descrp C1", ""));
                model_cardGuidelines_2.add(new Model_CardGuidelines("Title C2", "Descrip C2", ""));
                model_cardGuidelines_2.add(new Model_CardGuidelines("Title C3", "Descrip C3", ""));
                recyclerAdapter_cardGuidelines =
                        new RecyclerAdapter_CardGuidelines
                                (context, model_cardGuidelines_2, bundle.getString("PPE_3"));
                recyclerView.setAdapter(recyclerAdapter_cardGuidelines);
            }
            else if (bundle.containsKey("PPE_4")) {
                model_cardGuidelines_3.add(new Model_CardGuidelines("Title D1", "Descrp D1", ""));
                model_cardGuidelines_3.add(new Model_CardGuidelines("Title D2", "Descrip D2", ""));
                model_cardGuidelines_3.add(new Model_CardGuidelines("Title D3", "Descrip D3", ""));
                recyclerAdapter_cardGuidelines =
                        new RecyclerAdapter_CardGuidelines
                                (context, model_cardGuidelines_3, bundle.getString("PPE_4"));
                recyclerView.setAdapter(recyclerAdapter_cardGuidelines);
            }
            else if (bundle.containsKey("PPE_5")) {
                model_cardGuidelines_4.add(new Model_CardGuidelines("Title E1", "Descrp E1", ""));
                model_cardGuidelines_4.add(new Model_CardGuidelines("Title E2", "Descrip E2", ""));
                model_cardGuidelines_4.add(new Model_CardGuidelines("Title E3", "Descrip E3", ""));
                recyclerAdapter_cardGuidelines =
                        new RecyclerAdapter_CardGuidelines
                                (context, model_cardGuidelines_4, bundle.getString("PPE_5"));
                recyclerView.setAdapter(recyclerAdapter_cardGuidelines);
            }
            else {
                Toast.makeText(context, "No Card Error", Toast.LENGTH_SHORT).show();
            }
        }



    }
}
