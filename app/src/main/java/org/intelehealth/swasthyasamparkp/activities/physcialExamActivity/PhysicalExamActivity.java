package org.intelehealth.swasthyasamparkp.activities.physcialExamActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import org.intelehealth.swasthyasamparkp.R;
import org.intelehealth.swasthyasamparkp.activities.visitSummaryActivity.VisitSummaryActivity;
import org.intelehealth.swasthyasamparkp.alert.AlertEngine;
import org.intelehealth.swasthyasamparkp.app.AppConstants;
import org.intelehealth.swasthyasamparkp.database.dao.EncounterDAO;
import org.intelehealth.swasthyasamparkp.database.dao.ImagesDAO;
import org.intelehealth.swasthyasamparkp.database.dao.ObsDAO;
import org.intelehealth.swasthyasamparkp.database.dao.VisitsDAO;
import org.intelehealth.swasthyasamparkp.knowledgeEngine.Node;
import org.intelehealth.swasthyasamparkp.knowledgeEngine.PhysicalExam;
import org.intelehealth.swasthyasamparkp.models.dto.EncounterDTO;
import org.intelehealth.swasthyasamparkp.models.dto.ObsDTO;
import org.intelehealth.swasthyasamparkp.utilities.FileUtils;
import org.intelehealth.swasthyasamparkp.utilities.SessionManager;
import org.intelehealth.swasthyasamparkp.utilities.StringUtils;
import org.intelehealth.swasthyasamparkp.utilities.UuidDictionary;
import org.intelehealth.swasthyasamparkp.utilities.exception.DAOException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class PhysicalExamActivity extends AppCompatActivity {
    final static String TAG = PhysicalExamActivity.class.getSimpleName();
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    static String patientUuid;
    static String visitUuid;
    String state;
    String patientName;
    String intentTag;

    ArrayList<String> selectedExamsList;

    SQLiteDatabase localdb;


    static String imageName;
    static String baseDir;
    static File filePath;


    String mFileName = "physExam_4.json";
    String mFileName_2 = "physExam_4.json";


    PhysicalExam physicalExamMap;

    String physicalString;
    Boolean complaintConfirmed = false;
    String encounterVitals;
    String encounterAdultIntials;
    SessionManager sessionManager;

    public AlertEngine mAlertEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        baseDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();

        localdb = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
        sessionManager = new SessionManager(this);
        mAlertEngine = new AlertEngine(this, new AlertEngine.MessageListener() {
            @Override
            public void onMessageGenerated(String message, int type) {
                //TODO : UI updates
            }
        });
       /* AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.wash_hands);
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.hand_wash, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button pb = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        pb.setTextColor(getResources().getColor((R.color.colorPrimary)));
        pb.setTypeface(Typeface.DEFAULT, Typeface.BOLD);*/

        selectedExamsList = new ArrayList<>();
        Intent intent = this.getIntent(); // The intent was passed to the activity
        if (intent != null) {
            patientUuid = intent.getStringExtra("patientUuid");
            // Visit is pre created before coming to this screen so need to clear if user will not submit and going back from the screen
            visitUuid = intent.getStringExtra("visitUuid");
            encounterVitals = intent.getStringExtra("encounterUuidVitals");
            encounterAdultIntials = intent.getStringExtra("encounterUuidAdultIntial");

            state = intent.getStringExtra("state");
            patientName = intent.getStringExtra("name");
            intentTag = intent.getStringExtra("tag");
            Set<String> selectedExams = sessionManager.getVisitSummary(patientUuid);
            selectedExamsList.clear();
            if (selectedExams != null) selectedExamsList.addAll(selectedExams);
            filePath = new File(AppConstants.IMAGE_PATH);
        }

        if (encounterAdultIntials.equalsIgnoreCase("") || encounterAdultIntials == null) {
            encounterAdultIntials = UUID.randomUUID().toString();
        }

        EncounterDTO encounterDTO = new EncounterDTO();
        EncounterDAO encounterDAO = new EncounterDAO();
        encounterDTO = new EncounterDTO();
        encounterDTO.setUuid(encounterAdultIntials);
        encounterDTO.setEncounterTypeUuid(encounterDAO.getEncounterTypeUuid("ENCOUNTER_ADULTINITIAL"));
        encounterDTO.setEncounterTime(AppConstants.dateAndTimeUtils.currentDateTime());
        encounterDTO.setVisituuid(visitUuid);
        encounterDTO.setSyncd(false);
        encounterDTO.setProvideruuid(sessionManager.getProviderID());
        Log.d("DTO", "DTOcomp: " + encounterDTO.getProvideruuid());
        encounterDTO.setVoided(0);
        try {
            encounterDAO.createEncountersToDB(encounterDTO);
        } catch (DAOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        encounterVitals = encounterDTO.getUuid();

        if ((selectedExamsList == null) || selectedExamsList.isEmpty()) {
            Log.d(TAG, "No additional exams were triggered");

            if (sessionManager.getFirstCheckin().equals("false")) {
                physicalExamMap = new PhysicalExam(FileUtils.encodeJSON(this, mFileName), selectedExamsList);
            } else {
                physicalExamMap = new PhysicalExam(FileUtils.encodeJSON(this, mFileName_2), selectedExamsList);
            }

        } else {
            Set<String> selectedExamsWithoutDuplicates = new LinkedHashSet<>(selectedExamsList);
            Log.d(TAG, selectedExamsList.toString());
            selectedExamsList.clear();
            selectedExamsList.addAll(selectedExamsWithoutDuplicates);
            Log.d(TAG, selectedExamsList.toString());
            for (String string : selectedExamsList) Log.d(TAG, string);

            boolean hasLicense = false;
//            if (sessionManager.getLicenseKey() != null && !sessionManager.getLicenseKey().isEmpty())
            if (!sessionManager.getLicenseKey().isEmpty())
                hasLicense = true;

            if (hasLicense) {
                try {
                    JSONObject currentFile = null;
                    currentFile = new JSONObject(FileUtils.readFileRoot(mFileName, this));
                    physicalExamMap = new PhysicalExam(currentFile, selectedExamsList);
                } catch (JSONException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            } else {

                if (sessionManager.getFirstCheckin().equals("false")) {
                    physicalExamMap = new PhysicalExam(FileUtils.encodeJSON(this, mFileName), selectedExamsList);
                } else {
                    physicalExamMap = new PhysicalExam(FileUtils.encodeJSON(this, mFileName_2), selectedExamsList);
                }
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_exam);
        setTitle(getString(R.string.title_activity_physical_exam));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        final FloatingActionButton fab = findViewById(R.id.fab);

        setTitle(patientName + ": " + getString(R.string.check_in));
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), physicalExamMap);

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorHeight(15);

        // Modified by venu N on 02/04/2020.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // tabLayout.setSelectedTabIndicatorColor(getColor(R.color.amber));
            tabLayout.setTabTextColors(getColor(R.color.txt_sub_header_color), getColor(R.color.white));
        } else {
            // tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.amber));
            tabLayout.setTabTextColors(getResources().getColor(R.color.txt_sub_header_color), getResources().getColor(R.color.white));
        }
        if (tabLayout != null) {
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            tabLayout.setupWithViewPager(mViewPager);
        }


        // added by Venu N on 02/04/2020.
        if (mViewPager.getCurrentItem() == mViewPager.getAdapter().getCount() - 1) {
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_done_24dp));
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.svg_right_arrow));
        }
        assert fab != null;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //returns true if all Mandatory questions have been answered...
                //complaintConfirmed = physicalExamMap.areRequiredAnswered();
                complaintConfirmed = physicalExamMap.areRequiredAnsweredForGivenNodes(new int[]{1, 2});

                if (complaintConfirmed) {

                    /*
                     * Here, checks if the currentview is not the last view of the viewpager
                     * than it sets the current view to the next view and so user will be sent to
                     * the question that is not mandatory as well...If the user than reaches
                     * to the last question and complaintconfirmed is True that means all
                     * required questions are answered then in that case the
                     * currentItem (2) is < getcount()-1 (2) this becomes FALSE
                     * and so the else loop is executed...*/

                    if (mViewPager.getCurrentItem() < mViewPager.getAdapter().getCount() - 1) {
                        fab.setImageDrawable(ContextCompat.getDrawable
                                (PhysicalExamActivity.this, R.drawable.svg_right_arrow));
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                    } else {
                        physicalString = physicalExamMap.generateFindings();

                        List<String> imagePathList = physicalExamMap.getImagePathList();

                        if (imagePathList != null) {
                            for (String imagePath : imagePathList) {
                                updateImageDatabase();
                            }
                        }

                        // add alert info
                        String colorCode = "";
                        if (mAlertEngine.getAlertType() >= 3) {
                            if (mAlertEngine.getAlertType() == 3) {
                                colorCode = "orange";
                            } else {
                                colorCode = "red";
                            }
                            String alertMessage = "<div hidden><b><br>Alert Message</b><br><font color='" + colorCode + "'>" + mAlertEngine.getAlertMessageToTeleCaller() + "</font></div>";
                            physicalString += alertMessage;
                            EncounterDAO encounterDAO = new EncounterDAO();
                            try {
                                encounterDAO.setEmergency(visitUuid, true);
                            } catch (DAOException e) {
                                e.printStackTrace();
                            }
                        }
                         if (intentTag != null && intentTag.equals("edit")) {

                            updateDatabase(physicalString);
                            Intent intent = new Intent(PhysicalExamActivity.this, VisitSummaryActivity.class);
                            intent.putExtra("patientUuid", patientUuid);
                            intent.putExtra("visitUuid", visitUuid);
                            intent.putExtra("encounterUuidVitals", encounterVitals);
                            intent.putExtra("encounterUuidAdultIntial", encounterAdultIntials);
                            intent.putExtra("state", state);
                            intent.putExtra("name", patientName);
                            intent.putExtra("tag", intentTag);
                            intent.putExtra("hasPrescription", "false");
                            intent.putExtra("self", true);
                            intent.putExtra("AlertType", mAlertEngine.getAlertType());
                            intent.putExtra("MessageToPatient", mAlertEngine.getAlertMessageToPatient());
                            intent.putExtra("MessageToTeleCaller", mAlertEngine.getAlertMessageToTeleCaller());

                            for (String exams : selectedExamsList) {
                                Log.i(TAG, "onClick:++ " + exams);
                            }
                            // intent.putStringArrayListExtra("exams", selectedExamsList);
                            startActivity(intent);
                        } else {
                            boolean obsId = insertDb(physicalString);
                            Intent intent1 = new Intent(PhysicalExamActivity.this, VisitSummaryActivity.class); // earlier visitsummary
                            intent1.putExtra("patientUuid", patientUuid);
                            intent1.putExtra("visitUuid", visitUuid);
                            intent1.putExtra("encounterUuidVitals", encounterVitals);
                            intent1.putExtra("encounterUuidAdultIntial", encounterAdultIntials);
                            intent1.putExtra("state", state);
                            intent1.putExtra("name", patientName);
                            intent1.putExtra("tag", intentTag);
                            intent1.putExtra("hasPrescription", "false");
                            intent1.putExtra("self", true);
                            intent1.putExtra("AlertType", mAlertEngine.getAlertType());
                            intent1.putExtra("MessageToPatient", mAlertEngine.getAlertMessageToPatient());
                            intent1.putExtra("MessageToTeleCaller", mAlertEngine.getAlertMessageToTeleCaller());

                            // intent1.putStringArrayListExtra("exams", selectedExamsList);
                            startActivity(intent1);
                        }
                    }

                } else {
                    // added by venu N 0n 02/04/2020.
                    if (mViewPager.getCurrentItem() == mViewPager.getAdapter().getCount() - 1) {
                        questionsMissing();
                    } else if (mViewPager.getCurrentItem() < mViewPager.getAdapter().getCount() - 1) {
                        fab.setImageDrawable(ContextCompat.getDrawable(PhysicalExamActivity.this, R.drawable.svg_right_arrow));
                        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                    } else {
                        fab.setImageDrawable(ContextCompat.getDrawable(PhysicalExamActivity.this, R.drawable.ic_done_24dp));
                    }

                }
            }
        });


        // Added by venu N as per the figma file on 02/04/2020.
        RelativeLayout btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // remove the visit
                    VisitsDAO visitsDAO = new VisitsDAO();
                    visitsDAO.deleteByVisitUUID(visitUuid);
                    // remove the visit obs i.e. observation
                    // Obs is creating during the final FAB button submit but i am removing here for
                    // anything went wrong during submit and data inserted to that obs table
                    // so it will also clear on back from this screen
                    ObsDAO obsDAO = new ObsDAO();
                    obsDAO.deleteByEncounterUud(encounterAdultIntials);
                    // remove the Encounter
                    EncounterDAO encounterDAO = new EncounterDAO();
                    encounterDAO.deleteByVisitUUID(visitUuid);

                    finish();
                } catch (DAOException e) {
                    e.printStackTrace();
                    Toast.makeText(PhysicalExamActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (i == mViewPager.getAdapter().getCount() - 1) {
                    fab.setImageDrawable(ContextCompat.getDrawable(PhysicalExamActivity.this, R.drawable.ic_done_24dp));
                } else {
                    fab.setImageDrawable(ContextCompat.getDrawable(PhysicalExamActivity.this, R.drawable.svg_right_arrow));
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    private boolean insertDb(String value) {
        Log.i(TAG, "insertDb: ");

        ObsDAO obsDAO = new ObsDAO();
        ObsDTO obsDTO = new ObsDTO();
        obsDTO.setConceptuuid(UuidDictionary.PHYSICAL_EXAMINATION);
        obsDTO.setEncounteruuid(encounterAdultIntials);
        obsDTO.setCreator(sessionManager.getCreatorID());
        obsDTO.setValue(StringUtils.getValue(value));
        boolean isInserted = false;
        try {
            isInserted = obsDAO.insertObs(obsDTO);
        } catch (DAOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        return isInserted;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private PhysicalExam exams;

        public SectionsPagerAdapter(FragmentManager fm, PhysicalExam inputNode) {
            super(fm);
            this.exams = inputNode;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, exams, patientUuid, visitUuid);
        }

        @Override
        public int getCount() {
            return exams.getTotalNumberOfExams();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            //return exams.getTitle(position);
            return String.valueOf(position + 1);
        }
    }

    private void updateDatabase(String string) {
        ObsDTO obsDTO = new ObsDTO();
        ObsDAO obsDAO = new ObsDAO();
        try {
            obsDTO.setConceptuuid(UuidDictionary.PHYSICAL_EXAMINATION);
            obsDTO.setEncounteruuid(encounterAdultIntials);
            obsDTO.setCreator(sessionManager.getCreatorID());
            obsDTO.setValue(string);
            obsDTO.setUuid(obsDAO.getObsuuid(encounterAdultIntials, UuidDictionary.PHYSICAL_EXAMINATION));

            obsDAO.updateObs(obsDTO);

        } catch (DAOException dao) {
            FirebaseCrashlytics.getInstance().recordException(dao);
        }

        EncounterDAO encounterDAO = new EncounterDAO();
        try {
            encounterDAO.updateEncounterSync("false", encounterAdultIntials);
            encounterDAO.updateEncounterModifiedDate(encounterAdultIntials);
        } catch (DAOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public void questionsMissing() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.please_give_your_answer);
        alertDialogBuilder.setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void updateImageDatabase() {
        ImagesDAO imagesDAO = new ImagesDAO();

        try {
            imagesDAO.insertObsImageDatabase(imageName, encounterAdultIntials, UuidDictionary.COMPLEX_IMAGE_PE);
        } catch (DAOException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Node.TAKE_IMAGE_FOR_NODE) {
            if (resultCode == RESULT_OK) {
                String mCurrentPhotoPath = data.getStringExtra("RESULT");
                physicalExamMap.setImagePath(mCurrentPhotoPath);
                Log.i(TAG, mCurrentPhotoPath);
                physicalExamMap.displayImage(this, filePath.getAbsolutePath(), imageName);
                updateImageDatabase();

            }

        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        public static PhysicalExam exam_list;

        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        CustomExpandableListAdapter adapter;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, PhysicalExam exams, String patientUuid, String visitUuid) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString("patientUuid", patientUuid);
            args.putString("visitUuid", visitUuid);
            exam_list = exams;
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_physical_exam, container, false);

            final ImageView imageView = rootView.findViewById(R.id.physical_exam_image_view);
            TextView textView = rootView.findViewById(R.id.physical_exam_text_view);
            ExpandableListView expandableListView = rootView.findViewById(R.id.physical_exam_expandable_list_view);

            int viewNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            final String patientUuid1 = getArguments().getString("patientUuid");
            final String visitUuid1 = getArguments().getString("visitUuid");
            final Node viewNode = exam_list.getExamNode(viewNumber - 1);
            final String parent_name = exam_list.getExamParentNodeName(viewNumber - 1);
            String nodeText = parent_name + " : " + viewNode.findDisplay();

            textView.setText(nodeText);

            Node displayNode = viewNode.getOption(0);

            if (displayNode.isAidAvailable()) {
                String type = displayNode.getJobAidType();
                if (type.equals("video")) {
                    imageView.setVisibility(View.GONE);
                } else if (type.equals("image")) {
                    String drawableName = "physicalExamAssets/" + displayNode.getJobAidFile() + ".jpg";
                    try {
                        // get input stream
                        InputStream ims = getContext().getAssets().open(drawableName);
                        // load image as Drawable
                        Drawable d = Drawable.createFromStream(ims, null);
                        // set image to ImageView
                        imageView.setImageDrawable(d);
                        imageView.setMinimumHeight(500);
                        imageView.setMinimumWidth(500);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        imageView.setVisibility(View.GONE);
                    }
                } else {
                    imageView.setVisibility(View.GONE);
                }
            } else {
                imageView.setVisibility(View.GONE);
            }


            adapter = new CustomExpandableListAdapter(getContext(), viewNode, this.getClass().getSimpleName());
            expandableListView.setAdapter(adapter);

            expandableListView.setGroupIndicator(null);
            expandableListView.setChildIndicator(null);
            expandableListView.setChildDivider(getResources().getDrawable(R.color.white));
            expandableListView.setDivider(getResources().getDrawable(R.color.white));
            expandableListView.setDividerHeight(0);
            expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                    Node question = viewNode.getOption(groupPosition).getOption(childPosition);

                    question.toggleSelected();
                    if (viewNode.getOption(groupPosition).anySubSelected()) {
                        viewNode.getOption(groupPosition).setSelected();
                    } else {
                        viewNode.getOption(groupPosition).setUnselected();
                    }
                    Node rootNode = viewNode.getOption(groupPosition);
                    if (rootNode.isMultiChoice() && !question.isExcludedFromMultiChoice()) {
                        for (int i = 0; i < rootNode.getOptionsList().size(); i++) {
                            Node childNode = rootNode.getOptionsList().get(i);
                            if (childNode.isSelected() && childNode.isExcludedFromMultiChoice()) {
                                viewNode.getOption(groupPosition).getOptionsList().get(i).setUnselected();

                            }
                        }
                    }
                    Log.v(TAG, "rootNode - "+new Gson().toJson(rootNode));
                    if (!rootNode.isMultiChoice() || (rootNode.isMultiChoice() && question.isExcludedFromMultiChoice() && question.isSelected())) {
                        for (int i = 0; i < rootNode.getOptionsList().size(); i++) {
                            Node childNode = rootNode.getOptionsList().get(i);
                            if (!childNode.getId().equals(question.getId())) {
                                viewNode.getOption(groupPosition).getOptionsList().get(i).setUnselected();
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();

                    boolean flagForHandleQuestion = false;

                    if (question.getInputType() != null && !question.getInputType().isEmpty() && question.isSelected()) {
                        if (question.getInputType().equals("camera")) {
                            if (!filePath.exists()) {
                                boolean res = filePath.mkdirs();
                                Log.i("RES>", "" + filePath + " -> " + res);
                            }
                            imageName = UUID.randomUUID().toString();
                            Node.handleQuestion(question, getActivity(), adapter, filePath.toString(), imageName);
                        } else {
                            Node.handleQuestion(question, (Activity) getContext(), adapter, null, null);
                        }

                        flagForHandleQuestion = true;
                    }

                    if (!question.isTerminal() && question.isSelected()) {
                        Node.subLevelQuestion(viewNode.getOption(groupPosition), question, (Activity) getContext(), adapter, filePath.toString(), imageName);
                        ///flagForHandleQuestion = true;
                    }

                    if (!flagForHandleQuestion && question.isSelected()) {
                        ((PhysicalExamActivity) Objects.requireNonNull(getContext())).mAlertEngine.scanForAlert(question.getId(), question.getText());
                    }

                    if (!question.isSelected()) {
                        ((PhysicalExamActivity) Objects.requireNonNull(getContext())).mAlertEngine.handleItemUnselected(question.getId());
                    }
                    return false;
                }
            });

            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    return true;
                }
            });

            expandableListView.expandGroup(0);


            return rootView;
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {

    }

}

