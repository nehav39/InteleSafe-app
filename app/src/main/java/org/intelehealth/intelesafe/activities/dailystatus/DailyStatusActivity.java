package org.intelehealth.intelesafe.activities.dailystatus;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.activities.homeActivity.Day_Date;
import org.intelehealth.intelesafe.activities.physcialExamActivity.PhysicalExamActivity;
import org.intelehealth.intelesafe.activities.visitSummaryActivity.VisitSummaryActivity;
import org.intelehealth.intelesafe.app.AppConstants;
import org.intelehealth.intelesafe.database.InteleHealthDatabaseHelper;
import org.intelehealth.intelesafe.database.dao.EncounterDAO;
import org.intelehealth.intelesafe.database.dao.VisitsDAO;
import org.intelehealth.intelesafe.models.dto.EncounterDTO;
import org.intelehealth.intelesafe.models.dto.VisitDTO;
import org.intelehealth.intelesafe.utilities.SessionManager;
import org.intelehealth.intelesafe.utilities.UuidDictionary;
import org.intelehealth.intelesafe.utilities.exception.DAOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Created By: Deepak Sachdeva
 */

public class DailyStatusActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnStartAssessment;
    private RecyclerView recyclerView;
    private TextView tvNoVisit;
    private ArrayList<Day_Date> recyclerDayDatelist;
    private SQLiteDatabase sqLiteDatabase;
    private SessionManager sessionManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_status);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnStartAssessment = findViewById(R.id.btn_start_assessment);
        btnStartAssessment.setOnClickListener(this);

        recyclerView = findViewById(R.id.recycler_dates);
        tvNoVisit = findViewById(R.id.tv_no_visit);

        recyclerDayDatelist = new ArrayList<>();
        sessionManager = new SessionManager(this);

        getDailyCheckInDataFromDB();
    }

    private void getDailyCheckInDataFromDB() {
        sqLiteDatabase = AppConstants.inteleHealthDatabaseHelper.getWriteDb();

        String endDate = "";
        String query = "SELECT v.startdate FROM tbl_visit v, tbl_patient p WHERE " +
                "p.uuid = v.patientuuid AND v.startdate IS NOT NULL AND " +
                "v.patientuuid = ?";
        String[] data = {sessionManager.getPersionUUID()};

        final Cursor cursor = sqLiteDatabase.rawQuery(query, data);
        int a = 1;
        int b = 0;
        String dd="";
        int a1;
        StringBuilder stringBuilder;
        HashSet hashSet = new HashSet<>();
        ArrayList<String> array_original_date = new ArrayList<>();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    try {

                        endDate = cursor.getString(cursor.getColumnIndexOrThrow("startdate"));
                        stringBuilder = new StringBuilder(endDate);
                        a1 = stringBuilder.indexOf("T");
                        dd = stringBuilder.substring(0, a1);

                        //comment...
                        array_original_date.add(b,endDate);
                        b++;
//                        hashSet.add(new Day_Date("Day "+a, endDate));
                        //   boolean t = ;
                        hashSet.add(dd);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }while (cursor.moveToNext());
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        ArrayList<String> new_arraylist = new ArrayList<>();
        new_arraylist.addAll(hashSet);

        Collections.sort(new_arraylist); // added by venu N to sort the

        for (int j = 0; j < new_arraylist.size(); j++) {
            recyclerDayDatelist.add(new Day_Date("Day "+a, new_arraylist.get(j)));
            a++;
        }
//        String endDate;
//        String query = "SELECT v.startdate FROM tbl_visit v, tbl_patient p WHERE " +
//                "p.uuid = v.patientuuid AND v.startdate IS NOT NULL AND " +
//                "v.patientuuid = ?";
//        String[] data = {sessionManager.getPersionUUID()};
//
//        final Cursor cursor = sqLiteDatabase.rawQuery(query, data);
//        String dd;
//        int a1;
//        StringBuilder stringBuilder;
//        HashSet<String> hashSet = new HashSet<>();
//
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                try {
//                    endDate = cursor.getString(cursor.getColumnIndexOrThrow("startdate"));
//                    stringBuilder = new StringBuilder(endDate);
//                    a1 = stringBuilder.indexOf("T");
//                    dd = stringBuilder.substring(0, a1);
//                    hashSet.add(dd);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } while (cursor.moveToNext());
//        }
//
//        if (cursor != null) {
//            cursor.close();
//        }
//
//        ArrayList<String> arrayList = new ArrayList<>(hashSet);
//
//        Collections.sort(arrayList); // added by venu N to sort the
//
//        for (int i = 0; i < arrayList.size(); i++) {
//            recyclerDayDatelist.add(new Day_Date("Day " + i + 1, arrayList.get(i)));
//        }

        if (!recyclerDayDatelist.isEmpty()) {
            tvNoVisit.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            DailyStatusAdapter recycler_home_adapter = new DailyStatusAdapter(
                    DailyStatusActivity.this, recyclerDayDatelist);
            recycler_home_adapter.notifyDataSetChanged();

            recyclerView.setLayoutManager(new LinearLayoutManager(DailyStatusActivity.this,
                    LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(recycler_home_adapter);
        } else {
            tvNoVisit.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }


    public void pastVisits(int position) {

        String patientUuid = sessionManager.getPersionUUID();
        List<String> visitList = new ArrayList<>();
        List<String> encounterVitalList = new ArrayList<>();
        List<String> encounterAdultList = new ArrayList<>();

        String end_date;
        String date;
        String encounterLocalAdultInitial;
        String encounterVitalsLocal;
        String encounterIDSelection = "visituuid = ?";

        String visitSelection = "patientuuid = ?";
        String[] visitArgs = {patientUuid};
        String[] visitColumns = {"uuid, startdate", "enddate"};
        String visitOrderBy = "startdate";
        Cursor visitCursor = sqLiteDatabase.query("tbl_visit", visitColumns,
                visitSelection, visitArgs, null, null, visitOrderBy);

        if (visitCursor.getCount() < 1) {
//            neverSeen();
        } else {
            if (visitCursor.moveToLast() && visitCursor != null) {
                do {
                    EncounterDAO encounterDAO = new EncounterDAO();
                    date = visitCursor.getString(visitCursor.getColumnIndexOrThrow("startdate"));
                    end_date = visitCursor.getString(visitCursor.getColumnIndexOrThrow("enddate"));
                    String visit_id = visitCursor.getString(visitCursor.getColumnIndexOrThrow("uuid"));

                    visitList.add(visit_id);

                    String[] encounterIDArgs = {visit_id};

                    Cursor encounterCursor = sqLiteDatabase.query("tbl_encounter", null,
                            encounterIDSelection, encounterIDArgs, null, null, null);
                    if (encounterCursor != null && encounterCursor.moveToFirst()) {
                        do {
                            if (encounterDAO.getEncounterTypeUuid("ENCOUNTER_VITALS")
                                    .equalsIgnoreCase(encounterCursor.getString(encounterCursor
                                            .getColumnIndexOrThrow("encounter_type_uuid")))) {
                                encounterVitalsLocal = encounterCursor.getString(encounterCursor.getColumnIndexOrThrow("uuid"));
                                encounterVitalList.add(encounterVitalsLocal);
                            }
                            if (encounterDAO.getEncounterTypeUuid("ENCOUNTER_ADULTINITIAL")
                                    .equalsIgnoreCase(encounterCursor.getString(encounterCursor
                                            .getColumnIndexOrThrow("encounter_type_uuid")))) {
                                encounterLocalAdultInitial = encounterCursor.getString(encounterCursor.getColumnIndexOrThrow("uuid"));
                                encounterAdultList.add(encounterLocalAdultInitial);
                            }

                        } while (encounterCursor.moveToNext());
                    }

                    if (encounterCursor != null) {
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
                    OldVisit(visitDate, visitList.get(position), end_date, "",
                            encounterVitalList.get(position), encounterAdultList.get(position));
                } catch (ParseException e) {
                    Crashlytics.getInstance().core.logException(e);
                }
            }
        }
        visitCursor.close();
    }

    private void OldVisit(final String datetime, String visit_id, String end_datetime,
                          String visitValue, String encounterVitalsLocal, String encounterAdultInitalLocal) throws ParseException {

        final Boolean past_visit;

        past_visit = true;

        Intent visitSummary = new Intent(DailyStatusActivity.this, VisitSummaryActivity.class);

        visitSummary.putExtra("visitUuid", visit_id);
        visitSummary.putExtra("patientUuid", "" + sessionManager.getPersionUUID());
        visitSummary.putExtra("encounterUuidVitals", encounterVitalsLocal);
        visitSummary.putExtra("encounterUuidAdultIntial", encounterAdultInitalLocal);
        visitSummary.putExtra("name", "" + sessionManager.getUserName());
        visitSummary.putExtra("tag", "prior");
        visitSummary.putExtra("pastVisit", past_visit);
        visitSummary.putExtra("hasPrescription", "false");
        visitSummary.putExtra("fromOldVisit", true);
        startActivity(visitSummary);
    }




    private boolean returning;
    String fhistory = "";
    String phistory = "";
    EncounterDTO encounterDTO = new EncounterDTO();




    private void createNewVisit() {

        sessionManager.setProviderID("28cea4ab-3188-434a-82f0-055133090a38");

        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        Date todayDate = new Date();
        String thisDate = currentDate.format(todayDate);

        String uuid = UUID.randomUUID().toString();
        EncounterDAO encounterDAO = new EncounterDAO();
        encounterDTO = new EncounterDTO();
        encounterDTO.setUuid(UUID.randomUUID().toString());
        encounterDTO.setEncounterTypeUuid(encounterDAO.getEncounterTypeUuid("ENCOUNTER_VITALS"));
        encounterDTO.setEncounterTime(thisDate);
        encounterDTO.setVisituuid(uuid);
        encounterDTO.setSyncd(false);
        encounterDTO.setProvideruuid(sessionManager.getProviderID());
        Log.d("DTO", "DTO:detail " + encounterDTO.getProvideruuid());
        encounterDTO.setVoided(0);
        encounterDTO.setPrivacynotice_value(sessionManager.getPrivacyValue());//privacy value added.

        try {
            encounterDAO.createEncountersToDB(encounterDTO);
        } catch (DAOException e) {
            Crashlytics.getInstance().core.logException(e);
        }

        InteleHealthDatabaseHelper mDatabaseHelper = new InteleHealthDatabaseHelper(DailyStatusActivity.this);
        SQLiteDatabase sqLiteDatabase = mDatabaseHelper.getReadableDatabase();

        String CREATOR_ID = sessionManager.getCreatorID();
        returning = false;
        sessionManager.setReturning(returning);

        String[] cols = {"value"};
        Cursor cursor = sqLiteDatabase.query("tbl_obs", cols, "encounteruuid=? and conceptuuid=?",// querying for PMH
                new String[]{encounterDTO.getUuid(), UuidDictionary.RHK_MEDICAL_HISTORY_BLURB},
                null, null, null);

        if (cursor.moveToFirst()) {
            // rows present
            do {
                // so that null data is not appended
                phistory = phistory + cursor.getString(0);

            }
            while (cursor.moveToNext());
            returning = true;
            sessionManager.setReturning(returning);
        }
        cursor.close();

        Cursor cursor1 = sqLiteDatabase.query("tbl_obs", cols, "encounteruuid=? and conceptuuid=?",// querying for FH
                new String[]{encounterDTO.getUuid(), UuidDictionary.RHK_MEDICAL_HISTORY_BLURB},
                null, null, null);
        if (cursor1.moveToFirst()) {
            // rows present
            do {
                fhistory = fhistory + cursor1.getString(0);
            }
            while (cursor1.moveToNext());
            returning = true;
            sessionManager.setReturning(returning);
        }
        cursor1.close();

        // Will display data for patient as it is present in database
        // Toast.makeText(PatientDetailActivity.this,"PMH: "+phistory,Toast.LENGTH_SHORT).sƒhow();
        // Toast.makeText(PatientDetailActivity.this,"FH: "+fhistory,Toast.LENGTH_SHORT).show();

        Intent intent2 = new Intent(DailyStatusActivity.this, PhysicalExamActivity.class);
//      String fullName = patient_new.getFirst_name() + " " + patient_new.getLast_name();
        intent2.putExtra("patientUuid", sessionManager.getPersionUUID());

        VisitDTO visitDTO = new VisitDTO();

        visitDTO.setUuid(uuid);
        visitDTO.setPatientuuid(sessionManager.getPersionUUID());
        visitDTO.setStartdate(thisDate);
        visitDTO.setVisitTypeUuid(UuidDictionary.VISIT_TELEMEDICINE);
        visitDTO.setLocationuuid(sessionManager.getLocationUuid());
        visitDTO.setSyncd(false);
        visitDTO.setCreatoruuid(sessionManager.getCreatorID());//static

        VisitsDAO visitsDAO = new VisitsDAO();

        try {
            visitsDAO.insertPatientToDB(visitDTO);
        } catch (DAOException e) {
            Crashlytics.getInstance().core.logException(e);
        }

        // visitUuid = String.valueOf(visitLong);
//                localdb.close();
        intent2.putExtra("patientUuid", sessionManager.getPersionUUID());
        intent2.putExtra("visitUuid", uuid);
        intent2.putExtra("encounterUuidVitals", "");
        intent2.putExtra("encounterUuidAdultIntial", "");
        intent2.putExtra("name", sessionManager.getUserName());
        intent2.putExtra("state", "");
        intent2.putExtra("tag", "new");
        startActivity(intent2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start_assessment:
                createNewVisit();
                break;
        }
    }
}
