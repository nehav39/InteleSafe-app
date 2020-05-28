package org.intelehealth.intelesafe.activities.CardGuidelines;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
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

        Bundle bundle = getIntent().getExtras();
        model_cardGuidelines = new ArrayList<>();
        model_cardGuidelines_1 = new ArrayList<>();
        model_cardGuidelines_2 = new ArrayList<>();
        model_cardGuidelines_3 = new ArrayList<>();
        model_cardGuidelines_4 = new ArrayList<>();

        recyclerView = findViewById(R.id.card_guidelines_recycler);
        recyclerView.setLayoutManager(
                new LinearLayoutManager(Card_Guidelines.this, LinearLayoutManager.VERTICAL, false));

        if(bundle != null) {
            if (bundle.containsKey("PPE_1")) {
                model_cardGuidelines.add(new Model_CardGuidelines("Title A1", "Descrp A1"));
                model_cardGuidelines.add(new Model_CardGuidelines("Title A2", "Descrip A2"));
                model_cardGuidelines.add(new Model_CardGuidelines("Title A3", "Descrip A3"));
                recyclerAdapter_cardGuidelines =
                        new RecyclerAdapter_CardGuidelines(context, model_cardGuidelines);
            }
            else if (bundle.containsKey("PPE_2")) {
                model_cardGuidelines_1.add(new Model_CardGuidelines("Title B1", "Descrp B1"));
                model_cardGuidelines_1.add(new Model_CardGuidelines("Title B2", "Descrip B2"));
                model_cardGuidelines_1.add(new Model_CardGuidelines("Title B3", "Descrip B3"));
                recyclerAdapter_cardGuidelines =
                        new RecyclerAdapter_CardGuidelines(context, model_cardGuidelines);
            }
            else if (bundle.containsKey("PPE_3")) {
                model_cardGuidelines_2.add(new Model_CardGuidelines("Title C1", "Descrp C1"));
                model_cardGuidelines_2.add(new Model_CardGuidelines("Title C2", "Descrip C2"));
                model_cardGuidelines_2.add(new Model_CardGuidelines("Title C3", "Descrip C3"));
                recyclerAdapter_cardGuidelines =
                        new RecyclerAdapter_CardGuidelines(context, model_cardGuidelines);
            }
            else if (bundle.containsKey("PPE_4")) {
                model_cardGuidelines_3.add(new Model_CardGuidelines("Title D1", "Descrp D1"));
                model_cardGuidelines_3.add(new Model_CardGuidelines("Title D2", "Descrip D2"));
                model_cardGuidelines_3.add(new Model_CardGuidelines("Title D3", "Descrip D3"));
                recyclerAdapter_cardGuidelines =
                        new RecyclerAdapter_CardGuidelines(context, model_cardGuidelines);
            }
            else if (bundle.containsKey("PPE_1")) {
                model_cardGuidelines_4.add(new Model_CardGuidelines("Title E1", "Descrp E1"));
                model_cardGuidelines_4.add(new Model_CardGuidelines("Title E2", "Descrip E2"));
                model_cardGuidelines_4.add(new Model_CardGuidelines("Title E3", "Descrip E3"));
                recyclerAdapter_cardGuidelines =
                        new RecyclerAdapter_CardGuidelines(context, model_cardGuidelines);
            }
            else {
                Toast.makeText(context, "No Card Error", Toast.LENGTH_SHORT).show();
            }
        }

        recyclerView.setAdapter(recyclerAdapter_cardGuidelines);

    }
}
