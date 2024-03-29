package org.intelehealth.intelesafe.activities.pastMedicalHistoryActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.app.AppConstants;
import org.intelehealth.intelesafe.database.dao.EncounterDAO;
import org.intelehealth.intelesafe.database.dao.ImagesDAO;
import org.intelehealth.intelesafe.database.dao.ObsDAO;
import org.intelehealth.intelesafe.knowledgeEngine.Node;
import org.intelehealth.intelesafe.models.dto.ObsDTO;
import org.intelehealth.intelesafe.utilities.FileUtils;
import org.intelehealth.intelesafe.utilities.SessionManager;
import org.intelehealth.intelesafe.utilities.StringUtils;
import org.intelehealth.intelesafe.utilities.UuidDictionary;

import org.intelehealth.intelesafe.activities.familyHistoryActivity.FamilyHistoryActivity;
import org.intelehealth.intelesafe.activities.physcialExamActivity.CustomExpandableListAdapter;
import org.intelehealth.intelesafe.activities.visitSummaryActivity.VisitSummaryActivity;
import org.intelehealth.intelesafe.utilities.exception.DAOException;

public class PastMedicalHistoryActivity extends AppCompatActivity {

    String patient = "patient";
    String patientUuid;
    String visitUuid;
    String state;
    String patientName;
    String intentTag;

    ArrayList<String> physicalExams;
    int lastExpandedPosition = -1;

    String mFileName = "patHist.json";
    String image_Prefix = "MH";
    String imageDir = "Medical History";
    String imageName;
    File filePath;

    SQLiteDatabase localdb;

    boolean hasLicense = false;

//  String mFileName = "DemoHistory.json";

    private static final String TAG = PastMedicalHistoryActivity.class.getSimpleName();

    Node patientHistoryMap;
    CustomExpandableListAdapter adapter;
    ExpandableListView historyListView;

    String patientHistory;
    String phistory = "";

    boolean flag = false;

    SessionManager sessionManager = null;
    private String encounterVitals;
    private String encounterAdultIntials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sessionManager = new SessionManager(this);
        localdb = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        filePath = new File(AppConstants.IMAGE_PATH);
//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        e = sharedPreferences.edit();

        boolean past = sessionManager.isReturning();
        if (past) {

            AlertDialog.Builder alertdialog = new AlertDialog.Builder(PastMedicalHistoryActivity.this);
            alertdialog.setTitle(getString(R.string.title_activity_patient_history));
            alertdialog.setMessage(getString(R.string.question_update_details));
            alertdialog.setPositiveButton(getString(R.string.generic_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // allow to edit
                    flag = true;
                }
            });
            alertdialog.setNegativeButton(getString(R.string.generic_no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    String[] columns = {"value", " conceptuuid"};
                    try {
                        String medHistSelection = "encounteruuid = ? AND conceptuuid = ? AND voided!='1'";
                        String[] medHistArgs = {encounterAdultIntials, UuidDictionary.RHK_MEDICAL_HISTORY_BLURB};
                        Cursor medHistCursor = localdb.query("tbl_obs", columns, medHistSelection, medHistArgs, null, null, null);
                        medHistCursor.moveToLast();
                        phistory = medHistCursor.getString(medHistCursor.getColumnIndexOrThrow("value"));
                        medHistCursor.close();
                    } catch (CursorIndexOutOfBoundsException e) {
                        phistory = ""; // if medical history does not exist
                    }

                    // skip
                    flag = false;
                    if (phistory != null && !phistory.isEmpty() && !phistory.equals("null")) {
                        insertDb(phistory);
                    }

                    Intent intent = new Intent(PastMedicalHistoryActivity.this, FamilyHistoryActivity.class);
                    intent.putExtra("patientUuid", patientUuid);
                    intent.putExtra("visitUuid", visitUuid);
                    intent.putExtra("encounterUuidVitals", encounterVitals);
                    intent.putExtra("encounterUuidAdultIntial", encounterAdultIntials);
                    intent.putExtra("state", state);
                    intent.putExtra("name", patientName);
                    intent.putExtra("tag", intentTag);
                    //    intent.putStringArrayListExtra("exams", physicalExams);
                    startActivity(intent);

                }
            });
            alertdialog.show();

        }

        Intent intent = this.getIntent(); // The intent was passed to the activity
        if (intent != null) {
            patientUuid = intent.getStringExtra("patientUuid");
            visitUuid = intent.getStringExtra("visitUuid");
            encounterVitals = intent.getStringExtra("encounterUuidVitals");
            encounterAdultIntials = intent.getStringExtra("encounterUuidAdultIntial");
            state = intent.getStringExtra("state");
            patientName = intent.getStringExtra("name");
            intentTag = intent.getStringExtra("tag");
        }


        setTitle(getString(R.string.title_activity_patient_history));
        setTitle(getTitle() + ": " + patientName);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_medical_history);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        FloatingActionButton fab = findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If nothing is selected, there is nothing to put into the database.

                List<String> imagePathList = patientHistoryMap.getImagePathList();

                if (imagePathList != null) {
                    for (String imagePath : imagePathList) {
                        updateImageDatabase(imagePath);
                    }
                }


                if (intentTag != null && intentTag.equals("edit")) {
                    if (patientHistoryMap.anySubSelected()) {
                        patientHistory = patientHistoryMap.generateLanguage();
                        updateDatabase(patientHistory); // update details of patient's visit, when edit button on VisitSummary is pressed
                    }

                    // displaying all values in another activity
                    Intent intent = new Intent(PastMedicalHistoryActivity.this, VisitSummaryActivity.class);
                    intent.putExtra("patientUuid", patientUuid);
                    intent.putExtra("visitUuid", visitUuid);
                    intent.putExtra("encounterUuidVitals", encounterVitals);
                    intent.putExtra("encounterUuidAdultIntial", encounterAdultIntials);
                    intent.putExtra("state", state);
                    intent.putExtra("name", patientName);
                    intent.putExtra("tag", intentTag);
                    intent.putExtra("hasPrescription", "false");
                    startActivity(intent);
                } else {

                    //  if(patientHistoryMap.anySubSelected()){
                    patientHistory = patientHistoryMap.generateLanguage();

                    if (flag == true) { // only if OK clicked, collect this new info (old patient)
                        phistory = phistory + patientHistory; // only PMH updated
                        sessionManager.setReturning(true);


                        insertDb(phistory);

                        // however, we concat it here to patientHistory and pass it along to FH, not inserting into db
                    } else  // new patient, directly insert into database
                    {
                        insertDb(patientHistory);
                    }

                    Intent intent = new Intent(PastMedicalHistoryActivity.this, FamilyHistoryActivity.class);
                    intent.putExtra("patientUuid", patientUuid);
                    intent.putExtra("visitUuid", visitUuid);
                    intent.putExtra("encounterUuidVitals", encounterVitals);
                    intent.putExtra("encounterUuidAdultIntial", encounterAdultIntials);
                    intent.putExtra("state", state);
                    intent.putExtra("name", patientName);
                    intent.putExtra("tag", intentTag);
                    //       intent.putStringArrayListExtra("exams", physicalExams);
                    startActivity(intent);

                }
            }

        });


//        if (sessionManager.getLicenseKey() != null && !sessionManager.getLicenseKey().isEmpty())
        if (!sessionManager.getLicenseKey().isEmpty())
            hasLicense = true;

        if (hasLicense) {
            try {
                JSONObject currentFile = null;
                currentFile = new JSONObject(FileUtils.readFileRoot(mFileName, this));
                patientHistoryMap = new Node(currentFile); //Load the patient history mind map
            } catch (JSONException e) {
                Crashlytics.getInstance().core.logException(e);
            }
        } else {
            patientHistoryMap = new Node(FileUtils.encodeJSON(this, mFileName)); //Load the patient history mind map
        }

        historyListView = findViewById(R.id.patient_history_expandable_list_view);
        adapter = new CustomExpandableListAdapter(this, patientHistoryMap, this.getClass().getSimpleName()); //The adapter might change depending on the activity.
        historyListView.setAdapter(adapter);

        historyListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Node clickedNode = patientHistoryMap.getOption(groupPosition).getOption(childPosition);
                clickedNode.toggleSelected();

                //Nodes and the expandable list act funny, so if anything is clicked, a lot of stuff needs to be updated.
                if (patientHistoryMap.getOption(groupPosition).anySubSelected()) {
                    patientHistoryMap.getOption(groupPosition).setSelected();
                } else {
                    patientHistoryMap.getOption(groupPosition).setUnselected();
                }
                adapter.notifyDataSetChanged();

                if (clickedNode.getInputType() != null) {
                    if (!clickedNode.getInputType().equals("camera")) {
                        imageName = UUID.randomUUID().toString();
                        Node.handleQuestion(clickedNode, PastMedicalHistoryActivity.this, adapter, null, null);
                    }
                }

                Log.i(TAG, String.valueOf(clickedNode.isTerminal()));
                if (!clickedNode.isTerminal() && clickedNode.isSelected()) {
                    imageName = UUID.randomUUID().toString();

                    Node.subLevelQuestion(clickedNode, PastMedicalHistoryActivity.this, adapter, filePath.toString(), imageName);
                }

                return false;
            }
        });

        //Same fix as before, close all other groups when something is clicked.
        historyListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    historyListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
    }


    /**
     * This method inserts medical history of patient in database.
     *
     * @param value variable of type String
     * @return long
     */
    public boolean insertDb(String value) {
        ObsDAO obsDAO = new ObsDAO();
        ObsDTO obsDTO = new ObsDTO();
        obsDTO.setConceptuuid(UuidDictionary.RHK_MEDICAL_HISTORY_BLURB);
        obsDTO.setEncounteruuid(encounterAdultIntials);
        obsDTO.setCreator(sessionManager.getCreatorID());
        obsDTO.setValue(StringUtils.getValue(value));
        boolean isInserted = false;
        try {
            isInserted = obsDAO.insertObs(obsDTO);
        } catch (DAOException e) {
            Crashlytics.getInstance().core.logException(e);
        }

        return isInserted;
    }


    private void updateImageDatabase(String imagePath) {

        ImagesDAO imagesDAO = new ImagesDAO();

        try {
            imagesDAO.insertObsImageDatabase(imageName, encounterAdultIntials, "");
        } catch (DAOException e) {
            Crashlytics.getInstance().core.logException(e);
        }
    }


    /**
     * This method updates medical history of patient in database.
     *
     * @param string variable of type String
     * @return void
     */
    private void updateDatabase(String string) {

        ObsDTO obsDTO = new ObsDTO();
        ObsDAO obsDAO = new ObsDAO();
        try {
            obsDTO.setConceptuuid(UuidDictionary.RHK_MEDICAL_HISTORY_BLURB);
            obsDTO.setEncounteruuid(encounterAdultIntials);
            obsDTO.setCreator(sessionManager.getCreatorID());
            obsDTO.setValue(string);
            obsDTO.setUuid(obsDAO.getObsuuid(encounterAdultIntials, UuidDictionary.RHK_MEDICAL_HISTORY_BLURB));

            obsDAO.updateObs(obsDTO);

        } catch (DAOException dao) {
            Crashlytics.getInstance().core.logException(dao);
        }

        EncounterDAO encounterDAO = new EncounterDAO();
        try {
            encounterDAO.updateEncounterSync("false", encounterAdultIntials);
            encounterDAO.updateEncounterModifiedDate(encounterAdultIntials);
        } catch (DAOException e) {
            Crashlytics.getInstance().core.logException(e);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Node.TAKE_IMAGE_FOR_NODE) {
            if (resultCode == RESULT_OK) {
                String mCurrentPhotoPath = data.getStringExtra("RESULT");
                patientHistoryMap.setImagePath(mCurrentPhotoPath);
                Log.i(TAG, mCurrentPhotoPath);
                patientHistoryMap.displayImage(this, filePath.getAbsolutePath(), imageName);
            }
        }
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

