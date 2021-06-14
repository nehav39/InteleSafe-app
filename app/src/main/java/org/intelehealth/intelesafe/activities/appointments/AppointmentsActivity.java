package org.intelehealth.intelesafe.activities.appointments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.activities.homeActivity.Day_Date;
import org.intelehealth.intelesafe.activities.homeActivity.Recycler_Home_Adapter;
import org.intelehealth.intelesafe.activities.visitSummaryActivity.VisitSummaryActivity;
import org.intelehealth.intelesafe.app.AppConstants;
import org.intelehealth.intelesafe.database.dao.EncounterDAO;
import org.intelehealth.intelesafe.utilities.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class AppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvNoVisit;
    private ArrayList<Day_Date> recycler_arraylist;
    private SessionManager sessionManager;
    private SQLiteDatabase db;
    private HashSet<String> hashSet;
    private Recycler_Home_Adapter recycler_home_adapter;

    public static void start(Context context) {
        Intent starter = new Intent(context, AppointmentsActivity.class);
//        starter.putExtra();
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointments);

      /*  getSupportActionBar().setTitle(getString(R.string.my_vistis));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);*/

        sessionManager = new SessionManager(this);
        db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        /*
        recyclerView = findViewById(R.id.recyclerview_data);
        tvNoVisit = findViewById(R.id.tv_no_visit);
        renderList();*/
        setupTabs();
    }

    TabLayout tabLayout;
    ViewPager viewPager;
    @SuppressLint("ClickableViewAccessibility")
    private void setupTabs() {
        tabLayout= findViewById(R.id.tabLayout);
        viewPager= findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText(R.string.self_assessment_label));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.doctors_visits_label));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final TabAdapter adapter = new TabAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /*private void renderList() {
        ArrayList<String> visitsWithPrescription = getVisitsWithPrescription();
        recycler_arraylist = new ArrayList<Day_Date>();
        String endDate = "";
        String query = "SELECT v.uuid, v.startdate FROM tbl_visit v, tbl_patient p WHERE " +
                "p.uuid = v.patientuuid AND v.startdate IS NOT NULL AND (v.issubmitted == 1 OR v.enddate IS NOT NULL) AND " +
                "v.patientuuid = ?";
        String[] data = {sessionManager.getPersionUUID()};


        final Cursor cursor = db.rawQuery(query, data);
        int a = 1;
        int b = 0;
        String dd = "";
        int a1;
        StringBuilder stringBuilder;
        hashSet = new HashSet<>();
        ArrayList<String> array_original_date = new ArrayList<>();
        HashMap<String, Boolean> prescMap = new HashMap<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    try {
                        String uuid = cursor.getString(cursor.getColumnIndexOrThrow("uuid"));
                        endDate = cursor.getString(cursor.getColumnIndexOrThrow("startdate"));
                        stringBuilder = new StringBuilder(endDate);
                        a1 = stringBuilder.indexOf("T");
                        dd = stringBuilder.substring(0, a1);

                        //comment...
                        array_original_date.add(b, endDate);
                        b++;
//                        hashSet.add(new Day_Date("Day "+a, endDate));
                        //   boolean t = ;
                        hashSet.add(dd);


//                        for(int i=0; i<recycler_arraylist.size(); i++)
//                        {
//                            recycler_arraylist.get(i);
//                            String v = recycler_arraylist.get(i).getDate().toString();
//                            Log.d("MM","MM: "+v);
//
//                        }

                        if ((prescMap.get(dd) == null || !prescMap.get(dd)) && visitsWithPrescription.contains(uuid))
                            prescMap.put(dd, true);
                        *//*recycler_arraylist.add(new Day_Date
                                ("Day " + a, dd));
                        a++;*//*


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        ArrayList<String> new_arraylist = new ArrayList<>();
        new_arraylist.addAll(hashSet);

        Collections.sort(new_arraylist); // added by venu N to sort the

        for (int j = 0; j < new_arraylist.size(); j++) {
            Day_Date day_date = new Day_Date(getString(R.string.day_text) + " " + a, new_arraylist.get(j));
            day_date.hasPrescription = prescMap.containsKey(day_date.getDate());
            recycler_arraylist.add(day_date);
            a++;
        }

        if (!recycler_arraylist.isEmpty()) {
            Collections.reverse(recycler_arraylist);
            sessionManager.setFirstCheckin("true");
            tvNoVisit.setVisibility(View.GONE);
            recycler_home_adapter = new Recycler_Home_Adapter(this, recycler_arraylist, array_original_date);
            recycler_home_adapter.notifyDataSetChanged();

            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(recycler_home_adapter);
        } else {
            sessionManager.setFirstCheckin("false");
            tvNoVisit.setVisibility(View.VISIBLE);
        }
    }*/

    public void pastVisits(int position, String check_inDate, boolean self, String visitUuid) {

        String patientuuid = sessionManager.getPersionUUID();
        List<String> visitList = new ArrayList<>();
        List<String> encounterVitalList = new ArrayList<>();
        List<String> encounterAdultList = new ArrayList<>();

        String end_date = "";
        String date = "";
        String encounterlocalAdultintial = "";
        String encountervitalsLocal = null;
        String encounterIDSelection = "visituuid = ?";

        String visitSelection = "patientuuid = ?";

        String[] visitColumns = {"uuid, startdate", "enddate"};
        String visitOrderBy = "startdate";
        String query = "SELECT DISTINCT v.uuid, v.startdate, v.enddate FROM tbl_visit v WHERE " +
                "(v.issubmitted == 1 OR v.enddate IS NOT NULL) AND " +
                "v.patientuuid = ? AND v.uuid = ? ORDER BY v.startdate";
        String[] visitArgs = {patientuuid, visitUuid};

        Cursor visitCursor = db.rawQuery(query, visitArgs);
        //Cursor visitCursor = db.query("tbl_visit", visitColumns, visitSelection, visitArgs, null, null, visitOrderBy);

        if (visitCursor.getCount() < 1) {
//            neverSeen();
        } else {

            if (visitCursor.moveToLast() && visitCursor != null) {
                do {
                    EncounterDAO encounterDAO = new EncounterDAO();
                    date = visitCursor.getString(visitCursor.getColumnIndexOrThrow("startdate"));
                    end_date = visitCursor.getString(visitCursor.getColumnIndexOrThrow("enddate"));
                    String visit_id = visitCursor.getString(visitCursor.getColumnIndexOrThrow("uuid"));
                    StringBuilder stringBuilder = new StringBuilder(date);
                    int a1 = stringBuilder.indexOf("T");
                    String dateFromDB = stringBuilder.substring(0, a1);
                    //check for current check_in visits only.
                    if (dateFromDB.equals(check_inDate)) {
                        visitList.add(visit_id);

                        String[] encounterIDArgs = {visit_id};

                        Cursor encounterCursor = db.query("tbl_encounter", null, encounterIDSelection, encounterIDArgs, null, null, null);
                        if (encounterCursor != null && encounterCursor.moveToFirst()) {
                            do {
                                if (encounterDAO.getEncounterTypeUuid("ENCOUNTER_VITALS").equalsIgnoreCase(encounterCursor.getString(encounterCursor.getColumnIndexOrThrow("encounter_type_uuid")))) {
                                    encountervitalsLocal = encounterCursor.getString(encounterCursor.getColumnIndexOrThrow("uuid"));
                                    encounterVitalList.add(encountervitalsLocal);
                                }
                                if (encounterDAO.getEncounterTypeUuid("ENCOUNTER_ADULTINITIAL").equalsIgnoreCase(encounterCursor.getString(encounterCursor.getColumnIndexOrThrow("encounter_type_uuid")))) {
                                    encounterlocalAdultintial = encounterCursor.getString(encounterCursor.getColumnIndexOrThrow("uuid"));
                                    encounterAdultList.add(encounterlocalAdultintial);
                                }

                            } while (encounterCursor.moveToNext());
                        }
                        encounterCursor.close();
                    }
                    // Called when we close app on vitals screen and Didn't select any complaints

                } while (visitCursor.moveToPrevious());

                SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    Collections.reverse(visitList);
                    Collections.reverse(encounterVitalList);
                    Collections.reverse(encounterAdultList);

                    Date formatted = currentDate.parse(date);
                    String visitDate = currentDate.format(formatted);
                    OldVisit(visitDate, visitUuid, end_date, "", ""/*encounterVitalList.get(position)*/, encounterAdultList.get(0), self);
                } catch (ParseException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            }
        }
        visitCursor.close();
        finish();
    }

    private void OldVisit(final String datetime, String visit_id, String end_datetime, String visitValue, String encounterVitalslocal, String encounterAdultIntialslocal, boolean self) throws ParseException {

        final Boolean past_visit;

        past_visit = true;

        Intent visitSummary = new Intent(this, VisitSummaryActivity.class);

        visitSummary.putExtra("visitUuid", visit_id);
        visitSummary.putExtra("patientUuid", "" + sessionManager.getPersionUUID());
        visitSummary.putExtra("encounterUuidVitals", encounterVitalslocal);
        visitSummary.putExtra("encounterUuidAdultIntial", encounterAdultIntialslocal);
        visitSummary.putExtra("name", "" + sessionManager.getUserName());
        visitSummary.putExtra("tag", "prior");
        visitSummary.putExtra("pastVisit", past_visit);
        visitSummary.putExtra("hasPrescription", "false");
        visitSummary.putExtra("fromOldVisit", true);
        visitSummary.putExtra("self", self);
        startActivity(visitSummary);
    }

    public void closeActivity(View view) {
        finish();
    }

    /*private ArrayList<String> getVisitsWithPrescription() {
        ArrayList<String> encounterVisitUUID = new ArrayList<String>();
        HashSet<String> hsPatientUUID = new HashSet<String>();

        //Get all Visits
        VisitsDAO visitsDAO = new VisitsDAO();
        List<VisitDTO> visitsDTOList = visitsDAO.getAllVisits();

        //Get all Encounters
        EncounterDAO encounterDAO = new EncounterDAO();
        List<EncounterDTO> encounterDTOList = encounterDAO.getAllEncounters();

        //Get Visit Note Encounters only, visit note encounter id - d7151f82-c1f3-4152-a605-2f9ea7414a79
        if (encounterDTOList.size() > 0) {
            for (int i = 0; i < encounterDTOList.size(); i++) {
                if (encounterDTOList.get(i).getEncounterTypeUuid().equalsIgnoreCase("d7151f82-c1f3-4152-a605-2f9ea7414a79")) {
                    encounterVisitUUID.add(encounterDTOList.get(i).getVisituuid());
                }
            }
        }

        //Get patientUUID from visitList
        ArrayList<String> listPatientUUID = new ArrayList<>();
        for (int i = 0; i < encounterVisitUUID.size(); i++) {

            for (int j = 0; j < visitsDTOList.size(); j++) {

                if (encounterVisitUUID.get(i).equalsIgnoreCase(visitsDTOList.get(j).getUuid())) {
                    listPatientUUID.add(visitsDTOList.get(j).getUuid());
                }
            }
        }

        if (listPatientUUID.size() > 0) {
            hsPatientUUID.addAll(listPatientUUID);
            listPatientUUID.clear();
            listPatientUUID.addAll(hsPatientUUID);
        }
        return listPatientUUID;
    }*/
}