package org.intelehealth.intelesafe.activities.homeActivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.intelehealth.intelesafe.R;

import java.util.ArrayList;

public class DailyStatusActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DailyStatusAdapter dailyStatusAdapter;

    ArrayList<String> recyclerArraylist;
    ArrayList<String> array_original_date = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_status);

        recyclerView = findViewById(R.id.recycler_dates);

        recyclerArraylist = new ArrayList<String>();

        for (int j = 0; j < recyclerArraylist.size(); j++) {
            recyclerArraylist.add("Day "+ j);
        }

        dailyStatusAdapter = new DailyStatusAdapter(getApplicationContext(), recyclerArraylist);
        dailyStatusAdapter.notifyDataSetChanged();

        recyclerView.setLayoutManager(new LinearLayoutManager(DailyStatusActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(dailyStatusAdapter);
    }
}
