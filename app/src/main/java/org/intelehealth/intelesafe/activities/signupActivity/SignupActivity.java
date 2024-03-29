package org.intelehealth.intelesafe.activities.signupActivity;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.parse.Parse;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import org.intelehealth.intelesafe.BuildConfig;
import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.activities.cameraActivity.CameraActivity;
import org.intelehealth.intelesafe.activities.homeActivity.HomeActivity;
import org.intelehealth.intelesafe.activities.identificationActivity.IdentificationActivity;
import org.intelehealth.intelesafe.activities.setupActivity.LocationArrayAdapter;
import org.intelehealth.intelesafe.app.AppConstants;
import org.intelehealth.intelesafe.database.dao.ImagesDAO;
import org.intelehealth.intelesafe.database.dao.PatientsDAO;
import org.intelehealth.intelesafe.models.GetDistrictRes;
import org.intelehealth.intelesafe.models.GetOpenMRS;
import org.intelehealth.intelesafe.models.GetUserCallRes.UserCallRes;
import org.intelehealth.intelesafe.models.IdentifierUUID;
import org.intelehealth.intelesafe.models.NewUserCreationCall.NameUser;
import org.intelehealth.intelesafe.models.NewUserCreationCall.PersonUser;
import org.intelehealth.intelesafe.models.NewUserCreationCall.UserCreationData;
import org.intelehealth.intelesafe.models.Patient;
import org.intelehealth.intelesafe.models.UUIDResData;
import org.intelehealth.intelesafe.models.UserAddressData;
import org.intelehealth.intelesafe.models.UserBirthAttribute;
import org.intelehealth.intelesafe.models.UserBirthData;
import org.intelehealth.intelesafe.models.dto.PatientAttributesDTO;
import org.intelehealth.intelesafe.models.dto.PatientDTO;
import org.intelehealth.intelesafe.models.loginModel.LoginModel;
import org.intelehealth.intelesafe.networkApiCalls.ApiClient;
import org.intelehealth.intelesafe.networkApiCalls.ApiInterface;
import org.intelehealth.intelesafe.utilities.Base64Utils;
import org.intelehealth.intelesafe.utilities.DateAndTimeUtils;
import org.intelehealth.intelesafe.utilities.Logger;
import org.intelehealth.intelesafe.utilities.NetworkConnection;
import org.intelehealth.intelesafe.utilities.SessionManager;
import org.intelehealth.intelesafe.utilities.StringUtils;
import org.intelehealth.intelesafe.utilities.UrlModifiers;
import org.intelehealth.intelesafe.utilities.UuidGenerator;
import org.intelehealth.intelesafe.utilities.exception.DAOException;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by Sagar Shimpi
 */

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = IdentificationActivity.class.getSimpleName();
    Context context;
    SessionManager sessionManager = null;
    UuidGenerator uuidGenerator = new UuidGenerator();
    Calendar today = Calendar.getInstance();
    Calendar dob = Calendar.getInstance();
    Patient patient1 = new Patient();
    private String patientUuid = "";
    private String mGender;
    String patientID_edit;
    private int mDOBYear;
    private int mDOBMonth;
    private int mDOBDay;
    private DatePickerDialog mDOBPicker;
    private int mAgeYears = 0;
    private int mAgeMonths = 0;
    private String country1;

    PatientsDAO patientsDAO = new PatientsDAO();
    EditText mFirstName;
    EditText mMiddleName;
    EditText mLastName;
    EditText mDOB;
    EditText mPhoneNum;
    EditText mAge;
    AlertDialog.Builder mAgePicker;
    EditText mAddress1;
    EditText mAddress2;
    AutoCompleteTextView mCity;
    EditText mPostal;
    RadioButton mGenderM;
    RadioButton mGenderF;
    EditText countryText;
    EditText stateText;
    EditText licenseID;
    EditText hospital_name;

    Spinner mCountry;
    Spinner mState;
    Spinner selectDistrict;
    Spinner selectLocation;

    LinearLayout countryStateLayout;

    ImageView mImageView;
    String uuid = "";
    PatientDTO patientdto = new PatientDTO();
    ImagesDAO imagesDAO = new ImagesDAO();
    private String mCurrentPhotoPath;
    private String BlockCharacterSet_Others = "0123456789\\@$!=><&^*+€¥£`~";
    private String BlockCharacterSet_Name = "\\@$!=><&^*+\"\'€¥£`~";

    Intent i_privacy;
    String privacy_value;

    String userName = "";
    String password = "";
    String cPassword = "";
    String country = "";
    String state = "";
    String district = "";
    private String personUUID = "";
    private String patientOpenMRSID = "";
    private String OpenMRSID = "";

    int age = 0;

    private TextInputEditText mEmailView;
    private EditText mPasswordView;
    private EditText mCPassword;

//    private UserSignupData userSignupData;
//    private List<UserSignupData.Person> personList = new ArrayList<>();

    private List<GetDistrictRes.Result> mLocations = new ArrayList<>();

    private String selectedLocationName = "";
    private String selectedLocationUUID = "";

    Base64Utils base64Utils = new Base64Utils();
    String encoded = null;

    ProgressDialog progress;

    private String PASSWORD_PATTERN = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]){8,20}";

    Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
    Pattern lowerCasePatten = Pattern.compile("[a-z ]");
    Pattern digitCasePatten = Pattern.compile("[0-9 ]");

    protected AccountManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        context = SignupActivity.this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progress = new ProgressDialog(SignupActivity.this);

        i_privacy = getIntent();
        privacy_value = i_privacy.getStringExtra("privacy"); //privacy_accept value retrieved from previous act.
        sessionManager = new SessionManager(this);
        manager = AccountManager.get(SignupActivity.this);

        mFirstName = findViewById(R.id.identification_first_name);
        mFirstName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25), inputFilter_Name}); //maxlength 25

        mMiddleName = findViewById(R.id.identification_middle_name);
        mMiddleName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25), inputFilter_Name}); //maxlength 25

        mLastName = findViewById(R.id.identification_last_name);
        mLastName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25), inputFilter_Name}); //maxlength 25

        mEmailView = findViewById(R.id.email);
        mEmailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPhoneNum.setText(mEmailView.getText().toString());
            }
        });


        mPasswordView = findViewById(R.id.password);
        mCPassword = findViewById(R.id.cpassword);

        licenseID = findViewById(R.id.identification_registration_no);
        hospital_name = findViewById(R.id.identification_hospital_name);

        mDOB = findViewById(R.id.identification_birth_date_text_view);
        mPhoneNum = findViewById(R.id.identification_phone_number);
        mAge = findViewById(R.id.identification_age);
        mAddress1 = findViewById(R.id.identification_address1);
        mAddress1.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50), inputFilter_Name}); //maxlength 50

        mAddress2 = findViewById(R.id.identification_address2);
        mAddress2.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50), inputFilter_Name}); //maxlength 50

        mCity = findViewById(R.id.identification_city);
        mCity.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25), inputFilter_Others}); //maxlength 25

        stateText = findViewById(R.id.identification_state);
        mState = findViewById(R.id.spinner_state);
        selectDistrict = findViewById(R.id.spinner_district);
        selectLocation = findViewById(R.id.spinner_location);
        mPostal = findViewById(R.id.identification_postal_code);
        countryText = findViewById(R.id.identification_country);
        mCountry = findViewById(R.id.spinner_country);
        mGenderM = findViewById(R.id.identification_gender_male);
        mGenderF = findViewById(R.id.identification_gender_female);
        mImageView = findViewById(R.id.imageview_id_picture);

        ArrayAdapter<CharSequence> countryAdapter = ArrayAdapter.createFromResource(this,
                R.array.countries, android.R.layout.simple_spinner_item);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCountry.setAdapter(countryAdapter);

        ArrayAdapter<CharSequence> distAdapter = ArrayAdapter.createFromResource(SignupActivity.this,
                R.array.selectDist, android.R.layout.simple_spinner_item);
        distAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectDistrict.setAdapter(distAdapter);

        ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(SignupActivity.this,
                R.array.selectLocation, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectLocation.setAdapter(locationAdapter);

        generateUuid();

        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(this, R.array.state_error, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mState.setAdapter(stateAdapter);

        mState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0)
                {
                state = parent.getItemAtPosition(position).toString();
                if (state.matches("Odisha")) {
                    //Creating the instance of ArrayAdapter containing list of fruit names
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(SignupActivity.this,
                            R.array.odisha_villages, android.R.layout.simple_spinner_item);
                    mCity.setThreshold(1);//will start working from first character
                    mCity.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
                } else {
                    mCity.setAdapter(null);
                }
                }
                else
                    {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i != 0) {
                    country = adapterView.getItemAtPosition(i).toString();

                    if (country.matches("India")) {
                        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(SignupActivity.this,
                                R.array.states_india, android.R.layout.simple_spinner_item);
                        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mState.setAdapter(stateAdapter);
                        // setting state according database when user clicks edit details

                        if (patientID_edit != null) {
                            mState.setSelection(stateAdapter.getPosition(String.valueOf(patient1.getState_province())));
                        } else {
                          //  mState.setSelection(stateAdapter.getPosition("Maharashtra"));
                        }

                    }
//                } else {
//                    ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(SignupActivity.this,
//                            R.array.state_error, android.R.layout.simple_spinner_item);
//                    stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    mState.setAdapter(stateAdapter);
//                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        selectDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    district = adapterView.getItemAtPosition(i).toString();

                    fetchDistrictData(district);

                } else {
                    ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(SignupActivity.this,
                            R.array.location_error, android.R.layout.simple_spinner_item);
                    stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    selectLocation.setAdapter(stateAdapter);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        selectLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String dist = adapterView.getItemAtPosition(i).toString();


                if (mLocations.size() > 0) {

                    int pos = 0;

                    if (i > 0) {
                        pos = (i - 1);
                    }

                    selectedLocationName = dist;
                    selectedLocationUUID = mLocations.get(pos).getUuid();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mGenderF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        mGenderM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String patientTemp = "";
                if (patientUuid.equalsIgnoreCase("")) {
                    patientTemp = patientID_edit;
                } else {
                    patientTemp = patientUuid;
                }
                File filePath = new File(AppConstants.IMAGE_PATH + patientTemp);
                if (!filePath.exists()) {
                    filePath.mkdir();
                }
                Intent cameraIntent = new Intent(SignupActivity.this, CameraActivity.class);

                // cameraIntent.putExtra(CameraActivity.SHOW_DIALOG_MESSAGE, getString(R.string.camera_dialog_default));
                cameraIntent.putExtra(CameraActivity.SET_IMAGE_NAME, patientTemp);
                cameraIntent.putExtra(CameraActivity.SET_IMAGE_PATH, filePath);
                startActivityForResult(cameraIntent, CameraActivity.TAKE_IMAGE);
            }
        });
        mDOBYear = today.get(Calendar.YEAR);
        mDOBMonth = today.get(Calendar.MONTH);
        mDOBDay = today.get(Calendar.DAY_OF_MONTH);
        //DOB is set using an AlertDialog
        Locale.setDefault(Locale.ENGLISH);

        mDOBPicker = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //Set the DOB calendar to the date selected by the user
                dob.set(year, monthOfYear, dayOfMonth);
                mDOB.setError(null);
                mAge.setError(null);
                //Set Maximum date to current date because even after bday is less than current date it goes to check date is set after today
                mDOBPicker.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                Locale.setDefault(Locale.ENGLISH);
                //Formatted so that it can be read the way the user sets
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                dob.set(year, monthOfYear, dayOfMonth);
                String dobString = simpleDateFormat.format(dob.getTime());
                mDOB.setText(dobString);

                mAgeYears = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
                mAgeMonths = today.get(Calendar.MONTH) - dob.get(Calendar.MONTH);


                if (mAgeMonths < 0) {
                    mAgeMonths = mAgeMonths + 12;
                    mAgeYears = mAgeYears - 1;
                }

                if (mAgeMonths < 0 || mAgeYears < 0 || dob.after(today)) {
                    mDOB.setError(getString(R.string.identification_screen_error_dob));
                    mAge.setError(getString(R.string.identification_screen_error_age));
                    return;
                }

                mDOBYear = year;
                mDOBMonth = monthOfYear;
                mDOBDay = dayOfMonth;

                String ageString = mAgeYears + getString(R.string.identification_screen_text_years)
                        + " - " + mAgeMonths + getString(R.string.identification_screen_text_months);
                mAge.setText(ageString);
            }
        }, mDOBYear, mDOBMonth, mDOBDay);

        //DOB Picker is shown when clicked
        mDOBPicker.getDatePicker().setMaxDate(System.currentTimeMillis());
        mDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDOBPicker.show();
            }
        });


        //if patient update then age will be set
        if (patientID_edit != null) {
            age = DateAndTimeUtils.getAge(patient1.getDate_of_birth(), context);
            mDOB.setText(DateAndTimeUtils.getFormatedDateOfBirthAsView(patient1.getDate_of_birth()));
            int month = DateAndTimeUtils.getMonth(patient1.getDate_of_birth());
            mAge.setText(age + getString(R.string.identification_screen_text_years) + month + getString(R.string.identification_screen_text_months));
        }
        mAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAgePicker = new AlertDialog.Builder(SignupActivity.this, R.style.AlertDialogStyle);
                mAgePicker.setTitle(R.string.identification_screen_prompt_age);
                final LayoutInflater inflater = getLayoutInflater();
                View convertView = inflater.inflate(R.layout.dialog_2_numbers_picker, null);
                mAgePicker.setView(convertView);
                final NumberPicker yearPicker = convertView.findViewById(R.id.dialog_2_numbers_quantity);
                final NumberPicker monthPicker = convertView.findViewById(R.id.dialog_2_numbers_unit);
                final TextView middleText = convertView.findViewById(R.id.dialog_2_numbers_text);
                final TextView endText = convertView.findViewById(R.id.dialog_2_numbers_text_2);
                middleText.setText(getString(R.string.identification_screen_picker_years));
                endText.setText(getString(R.string.identification_screen_picker_months));
                yearPicker.setMinValue(0);
                yearPicker.setMaxValue(100);
                monthPicker.setMinValue(0);
                monthPicker.setMaxValue(12);
                if (mAgeYears > 0) {
                    yearPicker.setValue(mAgeYears);
                }
                if (mAgeMonths > 0) {
                    monthPicker.setValue(mAgeMonths);
                }

                mAgePicker.setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        yearPicker.setValue(yearPicker.getValue());
                        monthPicker.setValue(monthPicker.getValue());
                        String ageString = yearPicker.getValue() + getString(R.string.identification_screen_text_years) + " - " + monthPicker.getValue() + getString(R.string.identification_screen_text_months);
                        mAge.setText(ageString);


                        Calendar calendar = Calendar.getInstance();
                        int curYear = calendar.get(Calendar.YEAR);
                        int birthYear = curYear - yearPicker.getValue();
                        int curMonth = calendar.get(Calendar.MONTH);
                        int birthMonth = curMonth - monthPicker.getValue();
                        mDOBYear = birthYear;
                        mDOBMonth = birthMonth;
                        mDOBDay = 1;

                        Locale.setDefault(Locale.ENGLISH);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
                        dob.set(mDOBYear, mDOBMonth, mDOBDay);
                        String dobString = simpleDateFormat.format(dob.getTime());
                        mDOB.setText(dobString);
                        mDOBPicker.updateDate(mDOBYear, mDOBMonth, mDOBDay);
                        dialog.dismiss();
                    }
                });
                mAgePicker.setNegativeButton(R.string.generic_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                mAgePicker.show();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                sessionManager.setFirstTimeLaunch(false);
//
//                startActivity(new Intent(context, HomeActivity.class)
//                        .putExtra("setup", true));
//                finish();

                // Reset errors.
                mEmailView.setError(null);
                mPasswordView.setError(null);
                mCPassword.setError(null);

                // Store values at the time of the login attempt.
                userName = mEmailView.getText().toString();
                password = mPasswordView.getText().toString();
                cPassword = mCPassword.getText().toString();

                boolean cancel = false;
                View focusView = null;

                if (mFirstName.getText().toString().equals("")) {
                    mFirstName.setError(getString(R.string.error_field_required));
                    mFirstName.requestFocus();
                    return;
                }

                if (mMiddleName.getText().toString().equals("")) {
                    mMiddleName.setError(getString(R.string.error_field_required));
                    mMiddleName.requestFocus();
                    return;
                }

                if (mLastName.getText().toString().equals("")) {
                    mLastName.setError(getString(R.string.error_field_required));
                    mLastName.requestFocus();
                    return;
                }

                if (licenseID.getText().toString().equals("")) {
                    licenseID.setError(getString(R.string.error_field_required));
                    licenseID.requestFocus();
                    return;
                }

                if (hospital_name.getText().toString().equals("")) {
                    hospital_name.setError(getString(R.string.error_field_required));
                    hospital_name.requestFocus();
                    return;
                }

                // Check for a valid email address.
                if (TextUtils.isEmpty(userName)) {
                    mEmailView.setError(getString(R.string.error_field_required));
                    mEmailView.requestFocus();
                    return;
                }

                if (userName.length() < 10) {
                    mEmailView.setError(getString(R.string.username_10digits));
                    mEmailView.requestFocus();
                    return;
                }

                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(password)) {
                    mPasswordView.setError(getString(R.string.error_field_required));
                    mPasswordView.requestFocus();
                    return;
                }

                if (password.length() < 8) {
                    mPasswordView.setError(getString(R.string.password_must_eight));
                    mPasswordView.requestFocus();
                    return;
                }

                if (!UpperCasePatten.matcher(password).find()) {
                    mPasswordView.setError(getString(R.string.upper_case_validation));
                    mPasswordView.requestFocus();
                    return;
                }
                if (!lowerCasePatten.matcher(password).find()) {
                    mPasswordView.setError(getString(R.string.password_validation_two));
                    mPasswordView.requestFocus();
                    return;
                }
                if (!digitCasePatten.matcher(password).find()) {
                    mPasswordView.setError(getString(R.string.password_validation));
                    mPasswordView.requestFocus();
                    return;
                }

//                if (!isPasswordValid(password)) {
//                    mPasswordView.setError(getString(R.string.invalid_password));
//                    mPasswordView.requestFocus();
//                    return;
//                }

                if (cPassword.equalsIgnoreCase("")) {
                    mCPassword.setError(getString(R.string.error_field_required));
                    mCPassword.requestFocus();
                    return;
                }

                if (!cPassword.equals(password)) {
                    mCPassword.setError(getString(R.string.confirm_password_is_mismatched));
                    mCPassword.requestFocus();
                    return;
                }

                if (!mGenderF.isChecked() && !mGenderM.isChecked()) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignupActivity.this);
                    alertDialogBuilder.setTitle(R.string.error);
                    alertDialogBuilder.setMessage(R.string.identification_screen_dialog_error_gender);
                    alertDialogBuilder.setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                    positiveButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                    return;
                }

                if (mDOB.getText().toString().equals("")) {
                    mDOB.setError(getString(R.string.error_field_required));
                    mDOB.requestFocus();
                    return;
                }

                if (dob.equals("") || dob.toString().equals("")) {
                    if (dob.after(today)) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignupActivity.this);
                        alertDialogBuilder.setTitle(R.string.error);
                        alertDialogBuilder.setMessage(R.string.identification_screen_dialog_error_dob);
                        //alertDialogBuilder.setMessage(getString(R.string.identification_dialog_date_error));
                        alertDialogBuilder.setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        mDOBPicker.show();
                        alertDialog.show();

                        Button postiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        postiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                        postiveButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);

                        return;
                    }
                }

                if (mAge.getText().toString().equals("")) {
                    mAge.setError(getString(R.string.error_field_required));
                    mAge.requestFocus();
                    return;
                }

                if (mPhoneNum.getText().toString().equals("")) {
                    mPhoneNum.setError(getString(R.string.error_field_required));
                    mPhoneNum.requestFocus();
                    return;
                }

                if (mPhoneNum.length() < 10) {
                    mPhoneNum.setError(getString(R.string.invalid_phone_number));
                    mPhoneNum.requestFocus();
                    return;
                }

                if (mAddress1.getText().toString().equals("")) {
                    mAddress1.setError(getString(R.string.error_field_required));
                    mAddress1.requestFocus();
                    return;
                }

                if (mCity.getText().toString().equals("")) {
                    mCity.setError(getString(R.string.error_field_required));
                    mCity.requestFocus();
                    return;
                }

                if (country.equalsIgnoreCase("")) {
                    Toast.makeText(context, getString(R.string.please_select_country), Toast.LENGTH_LONG).show();
                    return;
                }

                if (state.equalsIgnoreCase("")) {
                    Toast.makeText(context, getString(R.string.please_select_state), Toast.LENGTH_LONG).show();
                    return;
                }

                if (district.equalsIgnoreCase("")) {
                    Toast.makeText(context, getString(R.string.please_select_districts), Toast.LENGTH_LONG).show();
                    return;
                }

                if (selectedLocationName.equalsIgnoreCase("")) {
                    Toast.makeText(context, getString(R.string.please_select_location), Toast.LENGTH_LONG).show();
                    return;
                }

                if (selectedLocationName.equalsIgnoreCase("Select location")) {
                    Toast.makeText(context, getString(R.string.please_select_location), Toast.LENGTH_LONG).show();
                    return;
                }

                if (mPostal.getText().toString().equals("")) {
                    mPostal.setError(getString(R.string.error_field_required));
                    mPostal.requestFocus();
                    return;
                }

                if (mPostal.length() < 6) {
                    mPostal.setError(getString(R.string.postal_code_validation));
                    mPostal.requestFocus();
                    return;
                }


                ///////////Data Model for step 1
                UserCreationData userCreationData = new UserCreationData();
                userCreationData.setUsername(userName);
                userCreationData.setPassword(password);

                NameUser nameUser = new NameUser();
                nameUser.setGivenName("" + mFirstName.getText().toString());
                nameUser.setMiddleName("" + mMiddleName.getText().toString());
                nameUser.setFamilyName("" + mLastName.getText().toString());

                PersonUser personUser = new PersonUser();
                List<NameUser> nameUserList = new ArrayList<>();
                nameUserList.add(nameUser);
                personUser.setNames(nameUserList);

                personUser.setGender("" + mGender);

                List<String> roles = new ArrayList<>();
                roles.add("8d94f280-c2cc-11de-8d13-0010c6dffd0f");
                roles.add("f4c6152c-50cf-4055-b842-557baa0c5e30");
                userCreationData.setRoles(roles);

                userCreationData.setPerson(personUser);

                Gson gson = new Gson();
                Log.e("JSON-STEP1- ", "" + gson.toJson(userCreationData));

                ////////////Data Model for step 2
                UserBirthData userBirthData = new UserBirthData();
                userBirthData.setAge(mAgeYears);
                userBirthData.setBirthdate("" + DateAndTimeUtils.getFormatedDateOfBirth(StringUtils.getValue(mDOB.getText().toString())));
                userBirthData.setBirthdateEstimated(true);

                UserBirthAttribute userBirthAttribute = new UserBirthAttribute();
                userBirthAttribute.setAttributeType("14d4f066-15f5-102d-96e4-000c29c2a5d7");
                userBirthAttribute.setValue("" + mPhoneNum.getText().toString());

                userBirthAttribute.setAttributeType("ecdaadb6-14a0-4ed9-b5b7-cfed87b44b87"); // openmrsuuid occupation
                userBirthAttribute.setValue("" + licenseID.getText().toString()); //license text

                userBirthAttribute.setAttributeType("1c718819-345c-4368-aad6-d69b4c267db7"); //openmrsuuid education
                userBirthAttribute.setValue("" + hospital_name.getText().toString()); //hospital name text


                List<UserBirthAttribute> userAttributeList = new ArrayList<>();
                userAttributeList.add(userBirthAttribute);

                userBirthData.setAttributes(userAttributeList);

                Log.e("JSON-STEP2- ", "" + gson.toJson(userBirthData));

                ////////////Data Model for step 3
                UserAddressData userAddressData = new UserAddressData();
                userAddressData.setAddress1("" + mAddress1.getText().toString() + " " + mAddress2.getText().toString());
                userAddressData.setCityVillage("" + mCity.getText().toString());
                userAddressData.setCountry("" + country);
                userAddressData.setStateProvince("" + mState.getSelectedItem().toString());
                userAddressData.setPostalCode("" + mPostal.getText().toString());
                userAddressData.setCountyDistrict("" + selectedLocationName);

                Log.e("JSON-STEP3- ", "" + gson.toJson(userAddressData));

//                UserSignupData data = new UserSignupData();
//                data.setName("" + mFirstName.getText().toString() + " " + mLastName.getText().toString());
//                data.setUsername("" + mEmailView.getText().toString());
//                data.setPassword("" + mPasswordView.getText().toString());
//
//                //Person
//                Person person = new Person();
//                person.setGender("" + mGender);
//                person.setAge(mAgeYears);
//                person.setBirthdate("" + DateAndTimeUtils.getFormatedDateOfBirth(StringUtils.getValue(mDOB.getText().toString())));
//
//                //Name
//                Name name = new Name();
//                name.setGivenName("" + mFirstName.getText().toString());
//                name.setMiddleName("" + mMiddleName.getText().toString());
//                name.setFamilyName("" + mLastName.getText().toString());
//
//                List<Name> nameList = new ArrayList<>();
//                nameList.add(name);
//                person.setNames(nameList);
//
//                //Address
//                Address address = new Address();
//                address.setAddress1("" + mAddress1.getText().toString() + " " + mAddress2.getText().toString());
//                address.setCityVillage("" + mCity.getText().toString());
//                address.setCountry("" + country);
//                address.setStateProvince("" + mState.getSelectedItem().toString());
//                address.setPostalCode("" + mPostal.getText().toString());
//                address.setCountyDistrict("" + selectedLocationName);
//
//                List<Address> addressList = new ArrayList<>();
//                addressList.add(address);
//                person.setAddresses(addressList);
//
//                //Attribute
//                Attribute attribute = new Attribute();
//                attribute.setAttributeType("14d4f066-15f5-102d-96e4-000c29c2a5d7");
//                attribute.setValue("" + mPhoneNum.getText().toString());

//                Attribute attribute2 = new Attribute();
//                attribute2.setAttributeType("" + selectedLocationUUID);
//                attribute2.setValue("" + selectedLocationName);

//                List<Attribute> attributeList = new ArrayList<>();
//                attributeList.add(attribute);
////                attributeList.add(attribute2);
//                person.setAttributes(attributeList);

//                //Roles
//                Role role = new Role();
//                role.setName("Add Patients");
//                role.setDescription("Able to add patients");

//                //Provider
//                Privilege privilege = new Privilege();
//                privilege.setName("Provider");
//                privilege.setDescription("");
//                List<Privilege> privilegeList = new ArrayList<>();
//                privilegeList.add(privilege);
//                role.setPrivileges(privilegeList);

//                List<Role> roleList = new ArrayList<>();
//                roleList.add(role);
//                data.setRoles(roleList);

//                List<String> roles = new ArrayList<>();
//                roles.add("8d94f280-c2cc-11de-8d13-0010c6dffd0f");
//                data.setRoles(roles);


//                //Add all into UserSignup
//                data.setPerson(person);
//
//                Gson gson = new Gson();
//                Log.e("JSON", "" + gson.toJson(data));

//                registerUser(data, mEmailView.getText().toString(), mPasswordView.getText().toString());
                registerUser(userCreationData, userBirthData, userAddressData, userName, password);
//                onPatientCreateClicked();

            }
        });

    }

    private void fetchDistrictData(String district) {

        String BASE_URL = BuildConfig.BASE_URL;

        ApiClient.changeApiBaseUrl(BASE_URL);
        ApiInterface apiService = ApiClient.createService(ApiInterface.class);
        try {
            Observable<GetDistrictRes> resultsObservable = apiService.GET_DISTRICT_RES_OBSERVABLE(district);
            resultsObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<GetDistrictRes>() {
                        @Override
                        public void onNext(GetDistrictRes res) {

                            if (res.getResults() != null) {

                                if (res.getResults().size() > 0) {

                                    mLocations = res.getResults();
                                    List<String> items = getLocationStringList(res.getResults());
                                    LocationArrayAdapter adapter = new LocationArrayAdapter(SignupActivity.this, items);
                                    selectLocation.setAdapter(adapter);
                                }

                            } else {
                                Toast.makeText(context, getResources().getString(R.string.error_while_fetching_location), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e("LOCATIONS", "" + e);
                            Toast.makeText(context, getResources().getString(R.string.error_while_fetching_location), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "changeApiBaseUrl: " + e.getMessage());
            Log.e(TAG, "changeApiBaseUrl: " + e.getStackTrace());
        }
    }

    private List<String> getLocationStringList(List<GetDistrictRes.Result> locationList) {
        List<String> list = new ArrayList<String>();
        list.add(getString(R.string.login_location_select));
        for (int i = 0; i < locationList.size(); i++) {
            list.add(locationList.get(i).getDisplay());
        }
        return list;
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();
        switch (view.getId()) {
            case R.id.identification_gender_male:
                if (checked)
                    mGender = "M";
                Log.v(TAG, "gender:" + mGender);
                break;
            case R.id.identification_gender_female:
                if (checked)
                    mGender = "F";
                Log.v(TAG, "gender:" + mGender);
                break;
        }
    }

    public void onPatientCreateClicked(String personUUID) {
        PatientsDAO patientsDAO = new PatientsDAO();
        PatientAttributesDTO patientAttributesDTO = new PatientAttributesDTO();
        List<PatientAttributesDTO> patientAttributesDTOList = new ArrayList<>();
//        uuid = UUID.randomUUID().toString();

        //Ask Kundan
        patientdto.setUuid(personUUID);
        Gson gson = new Gson();

//        boolean cancel = false;
//        View focusView = null;


//        if (dob.equals("") || dob.toString().equals("")) {
//            if (dob.after(today)) {
//                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignupActivity.this);
//                alertDialogBuilder.setTitle(R.string.error);
//                alertDialogBuilder.setMessage(R.string.identification_screen_dialog_error_dob);
//                //alertDialogBuilder.setMessage(getString(R.string.identification_dialog_date_error));
//                alertDialogBuilder.setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//                AlertDialog alertDialog = alertDialogBuilder.create();
//
//                mDOBPicker.show();
//                alertDialog.show();
//
//                Button postiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                postiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
//                postiveButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
//
//                return;
//            }
//        }

//        ArrayList<EditText> values = new ArrayList<>();
//        values.add(mFirstName);
//        values.add(mMiddleName);
//        values.add(mLastName);
//        values.add(mDOB);
//        values.add(mPhoneNum);
//        values.add(mAddress1);
//        values.add(mAddress2);
//        values.add(mCity);
//        values.add(mPostal);

//        if (!mFirstName.getText().toString().equals("") && !mLastName.getText().toString().equals("")
//                && !mCity.getText().toString().equals("") && !countryText.getText().toString().equals("") &&
//                !stateText.getText().toString().equals("") && !mPostal.getText().toString().equals("") && !mDOB.getText().toString().equals("") && !mAge.getText().toString().equals("") && (mGenderF.isChecked() || mGenderM.isChecked())) {
//
//            Log.v(TAG, "Result");
//
//        } else {
////            if (mFirstName.getText().toString().equals("")) {
////                mFirstName.setError(getString(R.string.error_field_required));
////            }
////
////            if (mLastName.getText().toString().equals("")) {
////                mLastName.setError(getString(R.string.error_field_required));
////            }
////
////            if (mDOB.getText().toString().equals("")) {
////                mDOB.setError(getString(R.string.error_field_required));
////            }
////
////            if (mAge.getText().toString().equals("")) {
////                mAge.setError(getString(R.string.error_field_required));
////            }
////
////            if (mCity.getText().toString().equals("")) {
////                mCity.setError(getString(R.string.error_field_required));
////            }
////
////            if (mPostal.getText().toString().equals("")) {
////                mCity.setError(getString(R.string.error_field_required));
////            }
//
////            if (!mGenderF.isChecked() && !mGenderM.isChecked()) {
////                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignupActivity.this);
////                alertDialogBuilder.setTitle(R.string.error);
////                alertDialogBuilder.setMessage(R.string.identification_screen_dialog_error_gender);
////                alertDialogBuilder.setPositiveButton(R.string.generic_ok, new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////                        dialog.dismiss();
////                    }
////                });
////                AlertDialog alertDialog = alertDialogBuilder.create();
////                alertDialog.show();
////
////                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
////                positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
////                positiveButton.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
////
////            }
//
//
//            Toast.makeText(SignupActivity.this, R.string.identification_screen_required_fields, Toast.LENGTH_LONG).show();
//            return;
//        }

//        if (mCountry.getSelectedItemPosition() == 0) {
//            countryText.setError(getString(R.string.error_field_required));
//            focusView = countryText;
//            cancel = true;
//            return;
//        } else {
//            countryText.setError(null);
//        }
//        if (mState.getSelectedItemPosition() == 0) {
//            stateText.setError(getString(R.string.error_field_required));
//            focusView = stateText;
//            cancel = true;
//            return;
//        } else {
//            stateText.setError(null);
//        }
//        if (cancel) {
//            focusView.requestFocus();
//        } else {

        patientdto.setFirstname(StringUtils.getValue(mFirstName.getText().toString()));
        patientdto.setMiddlename(StringUtils.getValue(mMiddleName.getText().toString()));
        patientdto.setLastname(StringUtils.getValue(mLastName.getText().toString()));
        patientdto.setPhonenumber(StringUtils.getValue(mPhoneNum.getText().toString()));
        patientdto.setGender(StringUtils.getValue(mGender));
        patientdto.setDateofbirth(DateAndTimeUtils.getFormatedDateOfBirth(StringUtils.getValue(mDOB.getText().toString())));
        patientdto.setAddress1(StringUtils.getValue(mAddress1.getText().toString()));
        patientdto.setAddress2(StringUtils.getValue(mAddress2.getText().toString()));
        patientdto.setCityvillage(StringUtils.getValue(mCity.getText().toString()));
        patientdto.setPostalcode(StringUtils.getValue(mPostal.getText().toString()));
        patientdto.setCountry(StringUtils.getValue(mCountry.getSelectedItem().toString()));
        patientdto.setPatientPhoto(mCurrentPhotoPath);
//      patientdto.setEconomic(StringUtils.getValue(m));
        patientdto.setOpenmrsId("" + OpenMRSID);
        patientdto.setStateprovince(StringUtils.getValue(mState.getSelectedItem().toString()));

//            patientAttributesDTO = new PatientAttributesDTO();
//            patientAttributesDTO.setUuid(UUID.randomUUID().toString());
//            patientAttributesDTO.setPatientuuid(uuid);
//            patientAttributesDTO.setPersonAttributeTypeUuid(patientsDAO.getUuidForAttribute("caste"));
//            patientAttributesDTO.setValue(StringUtils.getProvided(mCaste));
//            patientAttributesDTOList.add(patientAttributesDTO);

        patientAttributesDTO = new PatientAttributesDTO();
        patientAttributesDTO.setUuid(UUID.randomUUID().toString());
        patientAttributesDTO.setPatientuuid(personUUID);
        patientAttributesDTO.setPersonAttributeTypeUuid(patientsDAO.getUuidForAttribute("Telephone Number"));
        patientAttributesDTO.setValue(StringUtils.getValue(mPhoneNum.getText().toString()));
        patientAttributesDTOList.add(patientAttributesDTO);

//            patientAttributesDTO = new PatientAttributesDTO();
//            patientAttributesDTO.setUuid(UUID.randomUUID().toString());
//            patientAttributesDTO.setPatientuuid(uuid);
//            patientAttributesDTO.setPersonAttributeTypeUuid(patientsDAO.getUuidForAttribute("Son/wife/daughter"));
//            patientAttributesDTO.setValue(StringUtils.getValue(mRelationship.getText().toString()));
//            patientAttributesDTOList.add(patientAttributesDTO);

            patientAttributesDTO = new PatientAttributesDTO();
            patientAttributesDTO.setUuid(UUID.randomUUID().toString());
            patientAttributesDTO.setPatientuuid(uuid);
            patientAttributesDTO.setPersonAttributeTypeUuid(patientsDAO.getUuidForAttribute("occupation"));
            patientAttributesDTO.setValue(StringUtils.getValue(licenseID.getText().toString()));
            patientAttributesDTOList.add(patientAttributesDTO);

//            patientAttributesDTO = new PatientAttributesDTO();
//            patientAttributesDTO.setUuid(UUID.randomUUID().toString());
//            patientAttributesDTO.setPatientuuid(uuid);
//            patientAttributesDTO.setPersonAttributeTypeUuid(patientsDAO.getUuidForAttribute("Economic Status"));
//            patientAttributesDTO.setValue(StringUtils.getProvided(mEconomicStatus));
//            patientAttributesDTOList.add(patientAttributesDTO);

            patientAttributesDTO = new PatientAttributesDTO();
            patientAttributesDTO.setUuid(UUID.randomUUID().toString());
            patientAttributesDTO.setPatientuuid(uuid);
            patientAttributesDTO.setPersonAttributeTypeUuid(patientsDAO.getUuidForAttribute("Education Level"));
            patientAttributesDTO.setValue(StringUtils.getValue(hospital_name.getText().toString()));
            patientAttributesDTOList.add(patientAttributesDTO);

        patientAttributesDTO = new PatientAttributesDTO();
        patientAttributesDTO.setUuid(UUID.randomUUID().toString());
        patientAttributesDTO.setPatientuuid(personUUID);
        patientAttributesDTO.setPersonAttributeTypeUuid(patientsDAO.getUuidForAttribute("ProfileImageTimestamp"));
        patientAttributesDTO.setValue(AppConstants.dateAndTimeUtils.currentDateTime());

        patientAttributesDTOList.add(patientAttributesDTO);
        Logger.logD(TAG, "PatientAttribute list size" + patientAttributesDTOList.size());
        patientdto.setPatientAttributesDTOList(patientAttributesDTOList);
        patientdto.setSyncd(false);
        Logger.logD("patient json : ", "Json : " + gson.toJson(patientdto, PatientDTO.class));

//        }

        try {
            Logger.logD(TAG, "insertpatinet ");
            boolean isPatientInserted = patientsDAO.insertPatientToDB(patientdto, personUUID);
            boolean isPatientImageInserted = imagesDAO.insertPatientProfileImages(mCurrentPhotoPath, personUUID);

            if (NetworkConnection.isOnline(getApplication())) {
//                patientApiCall();
//                frameJson();

//                AppConstants.notificationUtils.showNotifications(getString(R.string.patient_data_upload),
//                        getString(R.string.uploading) + patientdto.getFirstname() + "" + patientdto.getLastname() +
//                                "'s data", 2, getApplication());
//             Not Uploading data on Sync
//                SyncDAO syncDAO = new SyncDAO();
//                ImagesPushDAO imagesPushDAO = new ImagesPushDAO();
//                boolean push = syncDAO.pushDataApi();
//                boolean pushImage = imagesPushDAO.patientProfileImagesPush();

//                if (push)
//                    AppConstants.notificationUtils.DownloadDone(getString(R.string.patient_data_upload), "" + patientdto.getFirstname() + "" + patientdto.getLastname() + "'s data upload complete.", 2, getApplication());
//                else
//                    AppConstants.notificationUtils.DownloadDone(getString(R.string.patient_data_upload), "" + patientdto.getFirstname() + "" + patientdto.getLastname() + "'s data not uploaded.", 2, getApplication());

//                if (pushImage)
//                    AppConstants.notificationUtils.DownloadDone(getString(R.string.patient_data_upload), "" + patientdto.getFirstname() + "" + patientdto.getLastname() + "'s Image upload complete.", 4, getApplication());
//                else
//                    AppConstants.notificationUtils.DownloadDone(getString(R.string.patient_data_upload), "" + patientdto.getFirstname() + "" + patientdto.getLastname() + "'s Image not complete.", 4, getApplication());

            }
//            else {
//                AppConstants.notificationUtils.showNotifications(getString(R.string.patient_data_failed), getString(R.string.check_your_connectivity), 2, IdentificationActivity.this);
//            }
//            if (isPatientInserted && isPatientImageInserted) {
//                Logger.logD(TAG, "inserted");
//
//                Toast.makeText(SignupActivity.this, "User Registration is successful", Toast.LENGTH_SHORT).show();
//
////                Intent i = new Intent(getApplication(), PatientDetailActivity.class);
////                i.putExtra("patientUuid", uuid);
////                i.putExtra("patientName", patientdto.getFirstname() + " " + patientdto.getLastname());
////                i.putExtra("tag", "newPatient");
////                i.putExtra("privacy", privacy_value);
////                i.putExtra("hasPrescription", "false");
////                Log.d(TAG, "Privacy Value on (Identification): "+privacy_value); //privacy value transferred to PatientDetail activity.
////                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
////                getApplication().startActivity(i);
//            } else {
//                Toast.makeText(SignupActivity.this, "Error of adding the data", Toast.LENGTH_SHORT).show();
//            }
        } catch (DAOException e) {
            Crashlytics.getInstance().core.logException(e);
        }

    }

    public void generateUuid() {

        patientUuid = uuidGenerator.UuidGenerator();

    }


    private InputFilter inputFilter_Others = new InputFilter() { //filter input for all other fields
        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            if (charSequence != null && BlockCharacterSet_Others.contains(("" + charSequence))) {
                return "";
            }
            return null;
        }
    };

    private InputFilter inputFilter_Name = new InputFilter() { //filter input for name fields
        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            if (charSequence != null && BlockCharacterSet_Name.contains(("" + charSequence))) {
                return "";
            }
            return null;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "Result Received");
        if (requestCode == CameraActivity.TAKE_IMAGE) {
            Log.v(TAG, "Request Code " + CameraActivity.TAKE_IMAGE);
            if (resultCode == RESULT_OK) {
                Log.i(TAG, "Result OK");
                mCurrentPhotoPath = data.getStringExtra("RESULT");
                Log.v("IdentificationActivity", mCurrentPhotoPath);

                Glide.with(this)
                        .load(new File(mCurrentPhotoPath))
                        .thumbnail(0.25f)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(mImageView);
            }
        }
    }


    private void registerUser(UserCreationData userSignupData, UserBirthData userBirthData, UserAddressData userAddressData, String userName, String password) {

        UrlModifiers urlModifiers = new UrlModifiers();
        String urlString = urlModifiers.loginUrl(BuildConfig.CLEAN_URL);
        encoded = base64Utils.encoded("admin", "Admin123");
//      sessionManager.setEncoded(encoded);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        progress.setTitle(getString(R.string.please_wait_progress));
        progress.setMessage(getString(R.string.logging_in));
        progress.show();
        Observable<LoginModel> loginModelObservable = AppConstants.apiInterface.LOGIN_MODEL_OBSERVABLE(urlString, "Basic " + encoded);
        loginModelObservable.subscribe(new Observer<LoginModel>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(LoginModel loginModel) {
                int responsCode = loginModel.hashCode();
                Boolean authencated = loginModel.getAuthenticated();
                Gson gson = new Gson();
                Logger.logD(TAG, "success" + gson.toJson(loginModel));
                sessionManager.setChwname(loginModel.getUser().getDisplay());
                sessionManager.setCreatorID(loginModel.getUser().getUuid());
                sessionManager.setSessionID(loginModel.getSessionId());
                sessionManager.setProviderID(loginModel.getUser().getPerson().getUuid());

                UrlModifiers urlModifiers = new UrlModifiers();
                String url = urlModifiers.setRegistrationURL();
                if (authencated) {
                    Observable<UserCallRes> resultsObservable = AppConstants.apiInterface.REGISTER_USER(url, "Basic " + encoded, userSignupData);
                    resultsObservable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new DisposableObserver<UserCallRes>() {
                                @Override
                                public void onNext(UserCallRes res) {
                                    if (res != null) {
                                        Log.e("UUID", "" + res.getPerson().getUuid());

//                                        encoded = "";
//                                        encoded = base64Utils.encoded(userName, password);
                                        sessionManager.setEncoded(encoded);

                                        final Account account = new Account(userName, "io.intelehealth.openmrs");
                                        manager.addAccountExplicitly(account, password, null);

                                        sessionManager.setLocationName("" + userAddressData.getCountyDistrict());
                                        sessionManager.setLocationUuid("" + selectedLocationUUID);
                                        sessionManager.setLocationDescription("In Maharashtra State");
                                        sessionManager.setServerUrl(BuildConfig.CLEAN_URL);
                                        sessionManager.setServerUrlRest("http://" + BuildConfig.CLEAN_URL + "/openmrs/ws/rest/v1/");
                                        sessionManager.setServerUrlBase("http://" + BuildConfig.CLEAN_URL + "/openmrs");
                                        sessionManager.setBaseUrl("http://" + BuildConfig.CLEAN_URL + "/openmrs/ws/rest/v1/");
//                                        sessionManager.setSetupComplete(true);

                                        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                                                .applicationId(AppConstants.IMAGE_APP_ID)
                                                .server("http://" + BuildConfig.CLEAN_URL + "/parse/")
                                                .build()
                                        );

                                        personUUID = res.getPerson().getUuid();

                                        sendBirthDataCall(encoded, personUUID, userBirthData, userAddressData);

                                    } else {
                                        Toast.makeText(context, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    progress.dismiss();
                                    Log.e("LOCATIONS", "" + e);
                                    Toast.makeText(context, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                                }

                                @Override
                                public void onComplete() {
                                    Logger.logD(TAG, "completed");
                                }
                            });
                }
            }

            @Override
            public void onError(Throwable e) {
                progress.dismiss();
                Logger.logD(TAG, "Login Failure" + e.getMessage());
                Toast.makeText(context, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {
                Logger.logD(TAG, "completed");
            }
        });
    }

    private void sendBirthDataCall(String encoded, String personUUID, UserBirthData userBirthData, UserAddressData userAddressData) {

        UrlModifiers urlModifiers = new UrlModifiers();
        String url = urlModifiers.setUserBirthDataURL(personUUID);

        Observable<ResponseBody> resultsObservable = AppConstants.apiInterface.REGISTER_USER_BIRTHDATA(url, "Basic " + encoded, userBirthData);
        resultsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody res) {
                        if (res != null) {

                            sendAddressDataCall(encoded, personUUID, userAddressData);

                        } else {
                            Toast.makeText(context, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        progress.dismiss();
                        Log.e("LOCATIONS", "" + e);
                        Toast.makeText(context, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onComplete() {
                        Logger.logD(TAG, "completed");
                    }
                });
    }

    private void sendAddressDataCall(String encoded, String personUUID, UserAddressData userAddressData) {

        UrlModifiers urlModifiers = new UrlModifiers();
        String url = urlModifiers.setUserAddressURL(personUUID);

        Observable<ResponseBody> resultsObservable = AppConstants.apiInterface.REGISTER_USER_ADDRESS(url, "Basic " + encoded, userAddressData);
        resultsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody res) {
                        if (res != null) {

                            getOpenMRSID(personUUID);

                        } else {
                            Toast.makeText(context, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        progress.dismiss();
                        Log.e("LOCATIONS", "" + e);
                        Toast.makeText(context, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onComplete() {
                        Logger.logD(TAG, "completed");
                    }
                });
    }

    private void getOpenMRSID(String personUUID) {

        String BASE_URL = BuildConfig.BASE_URL;

        ApiClient.changeApiBaseUrl(BASE_URL);
        ApiInterface apiService = ApiClient.createService(ApiInterface.class);
        try {
            Observable<GetOpenMRS> resultsObservable = apiService.GET_OPENMRSID("1", userName, password);
            resultsObservable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new DisposableObserver<GetOpenMRS>() {
                        @Override
                        public void onNext(GetOpenMRS res) {

                            if (res.getIdentifiers().size() > 0) {

                                OpenMRSID = res.getIdentifiers().get(0);
                                Log.e("ID", "" + OpenMRSID);
                                if (!OpenMRSID.equals("")) {
                                    generateOpenMRSID(personUUID);
                                } else {
                                    Toast.makeText(context, getResources().getString(R.string.error_while_gen_mrs_id), Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(context, getResources().getString(R.string.error_while_gen_mrs_id), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            progress.dismiss();
                            Log.e("LOCATIONS", "" + e);
                            Toast.makeText(context, getResources().getString(R.string.error_while_gen_mrs_id), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (IllegalArgumentException e) {
            progress.dismiss();
            Log.e(TAG, "changeApiBaseUrl: " + e.getMessage());
            Log.e(TAG, "changeApiBaseUrl: " + e.getStackTrace());
        }
    }

    private void generateOpenMRSID(String personUUID) {

        UUIDResData uuidResData = new UUIDResData();
        uuidResData.setPerson("" + personUUID);

        IdentifierUUID identifierUUID = new IdentifierUUID();
        identifierUUID.setIdentifier(OpenMRSID);
        identifierUUID.setIdentifierType("05a29f94-c0ed-11e2-94be-8c13b969e334");
        identifierUUID.setLocation("" + selectedLocationUUID);
        identifierUUID.setPreferred(true);

        List<IdentifierUUID> identifierUUIDList = new ArrayList<>();
        identifierUUIDList.add(identifierUUID);
        uuidResData.setIdentifiers(identifierUUIDList);

        UrlModifiers urlModifiers = new UrlModifiers();
        String url = urlModifiers.setPatientOpenMRSURL();

        Observable<ResponseBody> resultsObservable = AppConstants.apiInterface.SET_PATIENT_OpenMRSID(url, "Basic " + encoded, uuidResData);
        resultsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody res) {
                        progress.dismiss();
                        if (res != null) {

                            Log.e("OpenMRS-ID =", "Generated Successfully");
                            onPatientCreateClicked(personUUID);
                            sessionManager.setFirstTimeLaunch(false);
                            Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                            intent.putExtra("setup", true);
                            intent.putExtra("from", "setup");
                            intent.putExtra("login", true);
                            intent.putExtra("username", userName);
                            intent.putExtra("password", password);
//                            intent.putExtra("name", "" + mFirstName.getText().toString() + " " + mLastName.getText().toString());
//                            intent.putExtra("patientUUID", "" + personUUID);

                            sessionManager.setPersionUUID(personUUID);
                            sessionManager.setUserName("" + mFirstName.getText().toString() + " " + mLastName.getText().toString());
                            sessionManager.setPrivacyValue(privacy_value);
                            sessionManager.setTriggerNoti("no");
                            startActivity(intent);

                            finish();

                        } else {
                            Toast.makeText(context, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        progress.dismiss();
                        Log.e("LOCATIONS", "" + e);
                        Toast.makeText(context, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        progress.dismiss();
                        Logger.logD(TAG, "completed");
                    }
                });
    }
}
