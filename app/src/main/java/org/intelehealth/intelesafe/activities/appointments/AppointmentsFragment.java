package org.intelehealth.intelesafe.activities.appointments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.activities.homeActivity.Day_Date;
import org.intelehealth.intelesafe.activities.homeActivity.Recycler_Home_Adapter;
import org.intelehealth.intelesafe.app.AppConstants;
import org.intelehealth.intelesafe.database.dao.EncounterDAO;
import org.intelehealth.intelesafe.database.dao.VisitsDAO;
import org.intelehealth.intelesafe.models.dto.EncounterDTO;
import org.intelehealth.intelesafe.models.dto.VisitDTO;
import org.intelehealth.intelesafe.utilities.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class AppointmentsFragment extends Fragment {

    private static final String ARG_SELF = "ARG_SELF";
    private RecyclerView recyclerView;
    private TextView tvNoVisit;
    private ArrayList<Day_Date> recycler_arraylist;
    private SessionManager sessionManager;
    private SQLiteDatabase db;
    private HashSet<String> hashSet;
    private Recycler_Home_Adapter recycler_home_adapter;
    boolean self;

    public static AppointmentsFragment newInstance(boolean self) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_SELF, self);
        AppointmentsFragment fragment = new AppointmentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_appointments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        db = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        recyclerView = view.findViewById(R.id.recyclerview_data);
        tvNoVisit = view.findViewById(R.id.tv_no_visit);
        if (getArguments() != null) {
            self = getArguments().getBoolean(ARG_SELF, false);
        }
        renderList();
    }

    private void renderList() {
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

                        EncounterDAO encounterDAO = new EncounterDAO();
                        String encounterIDSelection = "visituuid = ?";
                        String[] encounterIDArgs = {uuid};
                        Cursor encounterCursor = db.query("tbl_encounter", null, encounterIDSelection, encounterIDArgs, null, null, null);
                        if (encounterCursor != null && encounterCursor.moveToFirst()) {
                            if (encounterDAO.getEncounterTypeUuid("ENCOUNTER_ADULTINITIAL").equalsIgnoreCase(encounterCursor.getString(encounterCursor.getColumnIndexOrThrow("encounter_type_uuid")))) {
                                if (!self)
                                    continue;
                            } else {
                                if (self)
                                    continue;
                            }
                        }

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
                        /*recycler_arraylist.add(new Day_Date
                                ("Day " + a, dd));
                        a++;*/


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
            recycler_home_adapter = new Recycler_Home_Adapter(getContext(), recycler_arraylist, array_original_date);
            recycler_home_adapter.notifyDataSetChanged();

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(recycler_home_adapter);
        } else {
            sessionManager.setFirstCheckin("false");
            tvNoVisit.setVisibility(View.VISIBLE);
        }
    }

    private ArrayList<String> getVisitsWithPrescription() {
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
    }
}
