package org.intelehealth.intelesafe.activities.CardGuidelines;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import org.intelehealth.intelesafe.R;

public class Card_Guidelines extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerAdapter_CardGuidelines recyclerAdapter_cardGuidelines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card__guidelines);

        Bundle bundle = getIntent().getExtras();
        recyclerView = findViewById(R.id.card_guidelines_recycler);
        recyclerAdapter_cardGuidelines = new RecyclerAdapter_CardGuidelines();
        recyclerView.setLayoutManager(
                new LinearLayoutManager(Card_Guidelines.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(recyclerAdapter_cardGuidelines);


    }
}
