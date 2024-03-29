package org.intelehealth.intelesafe.activities.questionNodeActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.app.AppConstants;
import org.intelehealth.intelesafe.database.dao.EncounterDAO;
import org.intelehealth.intelesafe.database.dao.ImagesDAO;
import org.intelehealth.intelesafe.database.dao.ObsDAO;
import org.intelehealth.intelesafe.models.dto.ObsDTO;
import org.intelehealth.intelesafe.utilities.FileUtils;
import org.intelehealth.intelesafe.utilities.SessionManager;
import org.intelehealth.intelesafe.utilities.UuidDictionary;

import org.intelehealth.intelesafe.activities.pastMedicalHistoryActivity.PastMedicalHistoryActivity;
import org.intelehealth.intelesafe.activities.physcialExamActivity.CustomExpandableListAdapter;
import org.intelehealth.intelesafe.activities.physcialExamActivity.PhysicalExamActivity;
import org.intelehealth.intelesafe.knowledgeEngine.Node;
import org.intelehealth.intelesafe.utilities.StringUtils;
import org.intelehealth.intelesafe.utilities.exception.DAOException;

public class QuestionNodeActivity extends AppCompatActivity {
    final String TAG = "Question Node Activity";
    String patientUuid;
    String visitUuid;
    String state;
    String patientName;
    String intentTag;

    String imageName;
    File filePath;
    Boolean complaintConfirmed = false;
    SessionManager sessionManager = null;

    //    Knowledge mKnowledge; //Knowledge engine
    ExpandableListView questionListView;
    String mFileName = "knowledge.json"; //knowledge engine file
    //    String mFileName = "DemoBrain.json";
    int complaintNumber = 0; //assuming there is at least one complaint, starting complaint number
    HashMap<String, String> complaintDetails; //temporary storage of complaint findings
    ArrayList<String> complaints; //list of complaints going to be used
    List<Node> complaintsNodes; //actual nodes to be used
    ArrayList<String> physicalExams;
    Node currentNode;
    CustomExpandableListAdapter adapter;
    boolean nodeComplete = false;

    int lastExpandedPosition = -1;
    String insertion = "";
    private SharedPreferences prefs;
    private String encounterVitals;
    private String encounterAdultIntials;

    private List<Node> optionsList = new ArrayList<>();
    Node assoSympNode;
    private JSONObject assoSympObj = new JSONObject();
    private JSONArray assoSympArr = new JSONArray();
    private JSONObject finalAssoSympObj = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sessionManager = new SessionManager(this);
        filePath = new File(AppConstants.IMAGE_PATH);
        Intent intent = this.getIntent(); // The intent was passed to the activity
        if (intent != null) {
            patientUuid = intent.getStringExtra("patientUuid");
            visitUuid = intent.getStringExtra("visitUuid");
            state = intent.getStringExtra("state");
            encounterVitals = intent.getStringExtra("encounterUuidVitals");
            encounterAdultIntials = intent.getStringExtra("encounterUuidAdultIntial");
            patientName = intent.getStringExtra("name");
            intentTag = intent.getStringExtra("tag");
            complaints = intent.getStringArrayListExtra("complaints");
        }
        complaintDetails = new HashMap<>();
        physicalExams = new ArrayList<>();
        complaintsNodes = new ArrayList<>();

        boolean hasLicense = false;
//        if (sessionManager.getLicenseKey() != null && !sessionManager.getLicenseKey().isEmpty())
        if (!sessionManager.getLicenseKey().isEmpty())
            hasLicense = true;

        JSONObject currentFile = null;
        for (int i = 0; i < complaints.size(); i++) {
            if (hasLicense) {
                try {
                    currentFile = new JSONObject(FileUtils.readFile(complaints.get(i) + ".json", this));
                } catch (JSONException e) {
                    Crashlytics.getInstance().core.logException(e);
                }
            } else {
                String fileLocation = "engines/" + complaints.get(i) + ".json";
                currentFile = FileUtils.encodeJSON(this, fileLocation);
            }
            Node currentNode = new Node(currentFile);
            complaintsNodes.add(currentNode);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_node);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        questionListView = findViewById(R.id.complaint_question_expandable_list_view);

        FloatingActionButton fab = findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabClick();
            }
        });
        setupQuestions(complaintNumber);
        //In the event there is more than one complaint, they will be prompted one at a time.

        questionListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                onListClicked(v, groupPosition, childPosition);

                return false;

            }
        });

        //Not a perfect method, but closes all other questions when a new one is clicked.
        //Expandable Lists in Android are broken, so this is a band-aid fix.
        questionListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    questionListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

    }

    public void onListClicked(View v, int groupPosition, int childPosition) {

        if ((currentNode.getOption(groupPosition).getChoiceType().equals("single")) && !currentNode.getOption(groupPosition).anySubSelected()) {
            Node question = currentNode.getOption(groupPosition).getOption(childPosition);
            question.toggleSelected();
            if (currentNode.getOption(groupPosition).anySubSelected()) {
                currentNode.getOption(groupPosition).setSelected();
            } else {
                currentNode.getOption(groupPosition).setUnselected();
            }


            if (!question.getInputType().isEmpty() && question.isSelected()) {
                if (question.getInputType().equals("camera")) {
                    if (!filePath.exists()) {
                        filePath.mkdirs();
                    }
                    imageName = UUID.randomUUID().toString();
                    Node.handleQuestion(question, QuestionNodeActivity.this, adapter, filePath.toString(), imageName);
                } else {
                    Node.handleQuestion(question, QuestionNodeActivity.this, adapter, null, null);
                }
            }


            if (!question.isTerminal() && question.isSelected()) {
                Node.subLevelQuestion(question, QuestionNodeActivity.this, adapter, filePath.toString(), imageName);
                //If the knowledgeEngine is not terminal, that means there are more questions to be asked for this branch.
            }
        } else if ((currentNode.getOption(groupPosition).getChoiceType().equals("single")) && currentNode.getOption(groupPosition).anySubSelected()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(QuestionNodeActivity.this);
            alertDialogBuilder.setMessage(R.string.this_question_only_one_answer);
            alertDialogBuilder.setNeutralButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {

            Node question = currentNode.getOption(groupPosition).getOption(childPosition);
            question.toggleSelected();
            if (currentNode.getOption(groupPosition).anySubSelected()) {
                currentNode.getOption(groupPosition).setSelected();
            } else {
                currentNode.getOption(groupPosition).setUnselected();
            }

            if (!question.getInputType().isEmpty() && question.isSelected()) {
                if (question.getInputType().equals("camera")) {
                    if (!filePath.exists()) {
                        filePath.mkdirs();
                    }
                    Node.handleQuestion(question, QuestionNodeActivity.this, adapter, filePath.toString(), imageName);
                } else {
                    Node.handleQuestion(question, QuestionNodeActivity.this, adapter, null, null);
                }
                //If there is an input type, then the question has a special method of data entry.
            }

            if (!question.isTerminal() && question.isSelected()) {
                Node.subLevelQuestion(question, QuestionNodeActivity.this, adapter, filePath.toString(), imageName);
                //If the knowledgeEngine is not terminal, that means there are more questions to be asked for this branch.
            }
        }
        adapter.notifyDataSetChanged();

    }

    /**
     * Summarizes the information of the current complaint knowledgeEngine.
     * Then has that put into the database, and then checks to see if there are more complaint nodes.
     * If there are more, presents the user with the next set of questions.
     * All exams are also stored into a string, which will be passed through the activities to the Physical Exam Activity.
     */
    private void fabClick() {
        nodeComplete = true;

        if (!complaintConfirmed) {
            questionsMissing();
        } else {
            List<String> imagePathList = currentNode.getImagePathList();

            if (imagePathList != null) {
                for (String imagePath : imagePathList) {
                    updateImageDatabase(imagePath);
                }
            }

            String complaintString = currentNode.generateLanguage();

            if (complaintString != null && !complaintString.isEmpty()) {
                //     String complaintFormatted = complaintString.replace("?,", "?:");

                String complaint = currentNode.getText();
                //    complaintDetails.put(complaint, complaintFormatted);

                insertion = insertion.concat(Node.bullet_arrow + "<b>" + complaint + "</b>" + ": " + Node.next_line + complaintString + " ");
            } else {
                String complaint = currentNode.getText();
                if (!complaint.equalsIgnoreCase(getResources().getString(R.string.associated_symptoms))) {
                    insertion = insertion.concat(Node.bullet_arrow + "<b>" + complaint + "</b>" + ": " + Node.next_line + " ");
                }
            }
            ArrayList<String> selectedAssociatedComplaintsList = currentNode.getSelectedAssociations();
            if (selectedAssociatedComplaintsList != null && !selectedAssociatedComplaintsList.isEmpty()) {
                for (String associatedComplaint : selectedAssociatedComplaintsList) {
                    if (!complaints.contains(associatedComplaint)) {
                        complaints.add(associatedComplaint);
                        String fileLocation = "engines/" + associatedComplaint + ".json";
                        JSONObject currentFile = FileUtils.encodeJSON(this, fileLocation);
                        Node currentNode = new Node(currentFile);
                        complaintsNodes.add(currentNode);
                    }
                }
            }

            ArrayList<String> childNodeSelectedPhysicalExams = currentNode.getPhysicalExamList();
            if (!childNodeSelectedPhysicalExams.isEmpty())
                physicalExams.addAll(childNodeSelectedPhysicalExams); //For Selected child nodes

            ArrayList<String> rootNodePhysicalExams = parseExams(currentNode);
            if (rootNodePhysicalExams != null && !rootNodePhysicalExams.isEmpty())
                physicalExams.addAll(rootNodePhysicalExams); //For Root Node

            if (complaintNumber < complaints.size() - 1) {
                complaintNumber++;
                setupQuestions(complaintNumber);
                complaintConfirmed = false;
            } else if (complaints.size() >= 1 && complaintNumber == complaints.size() - 1 && !optionsList.isEmpty()) {
                complaintNumber++;
                removeDuplicateSymptoms();
                complaintConfirmed = false;
            } else {
                if (intentTag != null && intentTag.equals("edit")) {
                    Log.i(TAG, "fabClick: update" + insertion);
                    updateDatabase(insertion);
                    Intent intent = new Intent(QuestionNodeActivity.this, PhysicalExamActivity.class);
                    intent.putExtra("patientUuid", patientUuid);
                    intent.putExtra("visitUuid", visitUuid);
                    intent.putExtra("encounterUuidVitals", encounterVitals);
                    intent.putExtra("encounterUuidAdultIntial", encounterAdultIntials);
                    intent.putExtra("state", state);
                    intent.putExtra("name", patientName);
                    intent.putExtra("tag", intentTag);

                    Set<String> selectedExams = new LinkedHashSet<>(physicalExams);
                    sessionManager.setVisitSummary(patientUuid, selectedExams);

                    startActivity(intent);
                } else {
                    Log.i(TAG, "fabClick: " + insertion);
                    insertDb(insertion);
                    Intent intent = new Intent(QuestionNodeActivity.this, PastMedicalHistoryActivity.class);
                    intent.putExtra("patientUuid", patientUuid);
                    intent.putExtra("visitUuid", visitUuid);
                    intent.putExtra("encounterUuidVitals", encounterVitals);
                    intent.putExtra("encounterUuidAdultIntial", encounterAdultIntials);
                    intent.putExtra("state", state);
                    intent.putExtra("name", patientName);
                    intent.putExtra("tag", intentTag);
                    Set<String> selectedExams = new LinkedHashSet<>(physicalExams);
                    sessionManager.setVisitSummary(patientUuid, selectedExams);

                    startActivity(intent);
                }
            }
        }
    }

    /**
     * Insert into DB could be made into a Helper Method, but isn't because there are specific concept IDs used each time.
     * Although this could also be made into a function, for now it has now been.
     *
     * @param value String to put into DB
     * @return DB Row number, never used
     */
    private boolean insertDb(String value) {

        Log.i(TAG, "insertDb: " + patientUuid + " " + visitUuid + " " + UuidDictionary.CURRENT_COMPLAINT);
        ObsDAO obsDAO = new ObsDAO();
        ObsDTO obsDTO = new ObsDTO();
        obsDTO.setConceptuuid(UuidDictionary.CURRENT_COMPLAINT);
        obsDTO.setEncounteruuid(encounterAdultIntials);
        obsDTO.setCreator(sessionManager.getCreatorID());
        obsDTO.setValue(StringUtils.getValue1(value));
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

    private void updateDatabase(String string) {
        Log.i(TAG, "updateDatabase: " + patientUuid + " " + visitUuid + " " + UuidDictionary.CURRENT_COMPLAINT);
//        }
        ObsDTO obsDTO = new ObsDTO();
        ObsDAO obsDAO = new ObsDAO();
        try {
            obsDTO.setConceptuuid(UuidDictionary.CURRENT_COMPLAINT);
            obsDTO.setEncounteruuid(encounterAdultIntials);
            obsDTO.setCreator(sessionManager.getCreatorID());
            obsDTO.setValue(string);
            obsDTO.setUuid(obsDAO.getObsuuid(encounterAdultIntials, UuidDictionary.CURRENT_COMPLAINT));

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

    /**
     * Sets up the complaint knowledgeEngine's questions.
     *
     * @param complaintIndex Index of complaint being displayed to user.
     */
    private void setupQuestions(int complaintIndex) {
        nodeComplete = false;

        if (complaints.size() >= 1) {
            getAssociatedSymptoms(complaintIndex);
        } else {
            currentNode = complaintsNodes.get(complaintIndex);
        }

        adapter = new CustomExpandableListAdapter(this, currentNode, this.getClass().getSimpleName());
        questionListView.setAdapter(adapter);
        questionListView.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);
        questionListView.expandGroup(0);
        setTitle(patientName + ": " + currentNode.findDisplay());

    }

    private void getAssociatedSymptoms(int complaintIndex) {

        List<Node> assoComplaintsNodes = new ArrayList<>();
        assoComplaintsNodes.addAll(complaintsNodes);

        for (int i = 0; i < complaintsNodes.get(complaintIndex).size(); i++) {

            if ((complaintsNodes.get(complaintIndex).getOptionsList().get(i).getText()
                    .equalsIgnoreCase("Associated symptoms"))
                    || (complaintsNodes.get(complaintIndex).getOptionsList().get(i).getText()
                    .equalsIgnoreCase("ସମ୍ପର୍କିତ ଲକ୍ଷଣଗୁଡ଼ିକ"))) {

                optionsList.addAll(complaintsNodes.get(complaintIndex).getOptionsList().get(i).getOptionsList());

                assoComplaintsNodes.get(complaintIndex).getOptionsList().remove(i);
                currentNode = assoComplaintsNodes.get(complaintIndex);
                Log.e("CurrentNode", "" + currentNode);

            } else {
                currentNode = complaintsNodes.get(complaintIndex);
            }
        }
    }

    private void removeDuplicateSymptoms() {

        nodeComplete = false;

        HashSet<String> hashSet = new HashSet<>();

        List<Node> finalOptionsList = new ArrayList<>(optionsList);

        if (optionsList.size() != 0) {

            for (int i = 0; i < optionsList.size(); i++) {

                if (hashSet.contains(optionsList.get(i).getText())) {

                    finalOptionsList.remove(optionsList.get(i));

                } else {
                    hashSet.add(optionsList.get(i).getText());
                }
            }

            try {
                assoSympObj.put("id", "ID_294177528");
                assoSympObj.put("text", "Associated symptoms");
                assoSympObj.put("display", "Do you have the following symptom(s)?");
                assoSympObj.put("display-or", "ତମର ଏହି ଲକ୍ଷଣ ସବୁ ଅଛି କି?");
                assoSympObj.put("pos-condition", "c.");
                assoSympObj.put("neg-condition", "s.");
                assoSympArr.put(0, assoSympObj);
                finalAssoSympObj.put("id", "ID_844006222");
                finalAssoSympObj.put("text", "Associated symptoms");
                finalAssoSympObj.put("display-or", "ପେଟଯନ୍ତ୍ରଣା");
                finalAssoSympObj.put("perform-physical-exam", "");
                finalAssoSympObj.put("options", assoSympArr);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            assoSympNode = new Node(finalAssoSympObj);
            assoSympNode.getOptionsList().get(0).setOptionsList(finalOptionsList);
            assoSympNode.getOptionsList().get(0).setTerminal(false);

            currentNode = assoSympNode;
            adapter = new CustomExpandableListAdapter(this, currentNode, this.getClass().getSimpleName());
            questionListView.setAdapter(adapter);
            questionListView.setChoiceMode(ExpandableListView.CHOICE_MODE_MULTIPLE);
            questionListView.expandGroup(0);
            setTitle(patientName + ": " + currentNode.getText());

        }
    }

    //Dialog Alert forcing user to answer all questions.
    //Can be removed if necessary
    //TODO: Add setting to allow for all questions unrequired..addAll(Arrays.asList(splitExams))
    public void questionsMissing() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(Html.fromHtml(currentNode.formQuestionAnswer(0)));
        alertDialogBuilder.setPositiveButton(R.string.generic_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                complaintConfirmed = true;
                dialog.dismiss();
                fabClick();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.generic_back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    private ArrayList<String> parseExams(Node node) {
        ArrayList<String> examList = new ArrayList<>();
        String rawExams = node.getPhysicalExams();
        if (rawExams != null) {
            String[] splitExams = rawExams.split(";");
            examList.addAll(Arrays.asList(splitExams));
            return examList;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Node.TAKE_IMAGE_FOR_NODE) {
            if (resultCode == RESULT_OK) {
                String mCurrentPhotoPath = data.getStringExtra("RESULT");
                currentNode.setImagePath(mCurrentPhotoPath);
                currentNode.displayImage(this, filePath.getAbsolutePath(), imageName);
            }
        }
    }

    @Override
    public void onBackPressed() {
    }


}
