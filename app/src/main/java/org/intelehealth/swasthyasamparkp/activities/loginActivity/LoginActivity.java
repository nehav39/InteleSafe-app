package org.intelehealth.swasthyasamparkp.activities.loginActivity;

//import android.accounts.AccountManager;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import org.intelehealth.swasthyasamparkp.BuildConfig;
import org.intelehealth.swasthyasamparkp.R;
import org.intelehealth.swasthyasamparkp.activities.homeActivity.HomeActivity;
import org.intelehealth.swasthyasamparkp.activities.resetPasswordActivity.ResetPasswordActivity;
import org.intelehealth.swasthyasamparkp.activities.signupActivity.SignupActivity;
import org.intelehealth.swasthyasamparkp.app.AppConstants;
import org.intelehealth.swasthyasamparkp.models.GetPassword;
import org.intelehealth.swasthyasamparkp.models.SendOtp;
import org.intelehealth.swasthyasamparkp.models.loginModel.LoginModel;
import org.intelehealth.swasthyasamparkp.models.loginProviderModel.LoginProviderModel;
import org.intelehealth.swasthyasamparkp.models.person.ClsPersonGetResponse;
import org.intelehealth.swasthyasamparkp.models.user.ClsUserGetResponse;
import org.intelehealth.swasthyasamparkp.models.user.ResultsItem;
import org.intelehealth.swasthyasamparkp.utilities.Base64Utils;
import org.intelehealth.swasthyasamparkp.utilities.Logger;
import org.intelehealth.swasthyasamparkp.utilities.NetworkConnection;
import org.intelehealth.swasthyasamparkp.utilities.OfflineLogin;
import org.intelehealth.swasthyasamparkp.utilities.SessionManager;
import org.intelehealth.swasthyasamparkp.utilities.StringEncryption;
import org.intelehealth.swasthyasamparkp.utilities.UrlModifiers;
import org.intelehealth.swasthyasamparkp.widget.materialprogressbar.CustomProgressDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {
    TextView txt_cant_login,txt_signup; // txt_signup added for signup navigation.
    /**
     * A dummy authentication store containing known user names and passwords.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "username:password", "admin:nimda"
    };
    private final String TAG = LoginActivity.class.getSimpleName();
    //protected AccountManager manager;
    //    ProgressDialog progress;
    Context context;
    CustomProgressDialog cpd;
    SessionManager sessionManager = null;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
//    private UserLoginTask mAuthTask = null;
    private OfflineLogin offlineLogin = null;

    UrlModifiers urlModifiers = new UrlModifiers();
    Base64Utils base64Utils = new Base64Utils();
    String encoded = null;
    // UI references.
    private EditText mUsernameView;
    //    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private ImageView icLogo;

    private long createdRecordsCount = 0;
    String provider_url_uuid;
    String privacy_value;

    Button mEmailSignInButton;
    View llOtp;
    TextInputLayout etPasswordLayout;
    String generatedOtp;
    private TextView tvResendOtp;
    String appLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(this);
        privacy_value = getIntent().getStringExtra("privacy"); //privacy_accept value retrieved from previous act.

        context = LoginActivity.this;
        sessionManager = new SessionManager(context);
        appLanguage = sessionManager.getAppLanguage();
        if (!appLanguage.equalsIgnoreCase("")) {
            setLocale(appLanguage);
        }
        cpd = new CustomProgressDialog(context);

        setTitle(R.string.title_activity_login);

        offlineLogin = OfflineLogin.getOfflineLogin();
        txt_cant_login = findViewById(R.id.cant_login_id);
        txt_cant_login.setText(getResources().getString(R.string.cant_login_user));
        txt_cant_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cant_log();
            }
        });

        // Commented by Venu for Account Manager Issue.
       /* manager = AccountManager.get(LoginActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Account[] accountList = manager.getAccountsByType("io.intelehealth.openmrs");
        if (accountList.length > 0) {
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("login", true);
            intent.putExtra("from", "login");
            intent.putExtra("username", "");
            intent.putExtra("password", "");
//            startJobDispatcherService(LoginActivity.this);
            startActivity(intent);
            finish();
        }*/

        //Enforces Offline Login Check only if network not present
        if (!NetworkConnection.isOnline(this)) {
            if (OfflineLogin.getOfflineLogin().getOfflineLoginStatus()) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("login", true);
                intent.putExtra("from", "login");
                intent.putExtra("username", "");
                intent.putExtra("password", "");
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }

        icLogo = findViewById(R.id.iv_logo);
        setLogo();

        // Set up the login form.
        mUsernameView = findViewById(R.id.et_email);
        mUsernameView.setHint(getResources().getString(R.string.prompt_phone_number));
        // populateAutoComplete(); TODO: create our own autocomplete code
        mPasswordView = findViewById(R.id.et_password);
        mPasswordView.setTransformationMethod(new PasswordTransformationMethod());
//      mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL) {
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });
        mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setText(getResources().getString(R.string.action_send_otp));
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.logD(TAG, "button pressed");
                if (!NetworkConnection.isOnline(context)) {
                    Toast.makeText(context, getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
                    return;
                }

                //commented as only otp is required now
                /*if (!sessionManager.isSetupComplete()) {
                    if (TextUtils.isEmpty(generatedOtp)) {
                        attemptLogin();
                    } else {
                        String mobile = mUsernameView.getText().toString();
                        String otp = mPasswordView.getText().toString();
                        if (generatedOtp.equals(otp) || (BuildConfig.DEBUG && otp.equals("0000"))) {
                            ResetPasswordActivity.start(context, mobile);
                        } else {
                            Toast.makeText(context, getResources().getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    attemptLogin();
                }*/
                if (TextUtils.isEmpty(generatedOtp)) {
                    attemptLogin();
                } else {
                    String mobile = mUsernameView.getText().toString();
                    String otp = mPasswordView.getText().toString();
                    if (generatedOtp.equals(otp) || (BuildConfig.DEBUG && otp.equals("0000"))) {
//                        ResetPasswordActivity.start(context, mobile);
                        getPassword(mobile);
                    } else {
                        Toast.makeText(context, getResources().getString(R.string.invalid_otp), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        llOtp = findViewById(R.id.llOtp);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        tvResendOtp.setText(getResources().getString(R.string.resend_otp));
        tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkConnection.isOnline(context)) {
                    Toast.makeText(context, R.string.no_network, Toast.LENGTH_SHORT).show();
                    return;
                }
                sendOtp(String.format("91%s",mUsernameView.getText().toString().trim()));
            }
        });
        etPasswordLayout = findViewById(R.id.etPasswordLayout);

        Button sign_up_button = findViewById(R.id.sign_up_button);
        sign_up_button.setText(getResources().getString(R.string.title_sign_up));
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        txt_signup = findViewById(R.id.txt_signup);
        String signupStr = getResources().getString(R.string.txt_sign_up_option);
        txt_signup.setText(signupStr);
       /* SpannableString spannableString = new SpannableString(signupStr);
        int startIndex = signupStr.lastIndexOf("?");
        Log.e("OnClick","startIndex" + startIndex);
        int endIndex = signupStr.length();
        Log.e("OnClick","endIndex" + endIndex);
        ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // do some thing
                Log.e("OnClick","txt_signup");
                Intent intent = new Intent(LoginActivity.this, PrivacyNotice_Activity.class);
                //intent.putExtra("privacy", privacy_value); //privacy value send to identificationActivity
                intent//addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                       . addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };
        spannableString.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), startIndex+1, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(span1, startIndex+1, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_signup.setText(spannableString);
        txt_signup.setMovementMethod(LinkMovementMethod.getInstance());*/

        checkSetup();
    }

    public void setLocale(String appLanguage)
    {
        Locale locale = new Locale(appLanguage);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    private void checkSetup() {
        //commented as only otp is required now
        /*if (sessionManager.isSetupComplete()) {
            etPasswordLayout.setHint(R.string.prompt_password);
            llOtp.setVisibility(View.VISIBLE);
            mEmailSignInButton.setText(R.string.action_sign_in);
        } else {
            etPasswordLayout.setHint(R.string.prompt_otp);
            llOtp.setVisibility(View.GONE);
            mEmailSignInButton.setText(R.string.action_send_otp);
        }*/
    }

    private void signUp() {
        /*Intent intent = new Intent(LoginActivity.this, PrivacyNotice_Activity.class);
        //intent.putExtra("privacy", privacy_value); //privacy value send to identificationActivity
        intent//addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                . addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);*/

        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        intent.putExtra("privacy", "Accept"); //privacy value send to identificationActivity
        intent
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void setLogo() {

        File f = new File("/data/data/" + context.getPackageName() + "/files/logo/ic_sams.png");
        if (f.isFile()) {
            Bitmap bitmap = BitmapFactory.decodeFile("/data/data/" + context.getPackageName() + "/files/logo/ic_sams.png");
            icLogo.setImageBitmap(bitmap);
        } else {
            Log.e("SetLogo", "No Logo Found in Mindmap Folder");
        }
    }

    /**
     * Returns void.
     * This method checks if valid username and password are given as input.
     *
     * @return void
     */
    private void attemptLogin() {

        // Store values at the time of the login attempt.
        String email = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mUsernameView.setError(getString(R.string.enter_username));
            mUsernameView.requestFocus();
            return;
        }

        if (NetworkConnection.isOnline(this)) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            //commented as only otp is required now
            /*if (sessionManager.isSetupComplete()) {
                // Check for a valid password, if the user entered one.
                if (TextUtils.isEmpty(password)) {
                    mPasswordView.setError(getString(R.string.enter_password));
                    mPasswordView.requestFocus();
                    return;
                }

                if (password.length() < 4) {
                    mPasswordView.setError(getString(R.string.error_invalid_password));
                    mPasswordView.requestFocus();
                    return;
                }

                UserLoginTask(email, password);
            } else {
                checkUserExistsOrNot(email);
            }*/
            checkUserExistsOrNot(email);
        }

    }

    /**
     * @param password Password
     * @return boolean
     */
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    public void cant_log() {

        ResetPasswordActivity.start(context, null);

        /*final SpannableString span_string = new SpannableString(getApplicationContext().getText(R.string.email_link)); //message is changed...
        Linkify.addLinks(span_string, Linkify.EMAIL_ADDRESSES);

        new AlertDialog.Builder(this)
                .setMessage(span_string)
                .setNegativeButton(getResources().getString(R.string.send_email_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //finish();
                        Intent intent = new Intent(Intent.ACTION_SENDTO); //to get only the list of e-mail clients
                        intent.setType("text/plain");
                        intent.setData(Uri.parse("mailto:support@intelehealth.io"));
                        // intent.putExtra(Intent.EXTRA_EMAIL, "support@intelehealth.io");
                        // intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                        //  intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");

                        startActivity(Intent.createChooser(intent, "Send Email"));
                        //add email function here !
                    }

                })
                .setPositiveButton(getResources().getString(R.string.close_button), null)
                .show();

        //prajwal_changes*/
    }

    /**
     * class UserLoginTask will authenticate user using email and password.
     * Depending on server's response, user may or may not have successful login.
     * This class also uses SharedPreferences to store session ID
     */
    public void UserLoginTask(String mEmail, String mPassword) {

        String urlString = urlModifiers.loginUrl(BuildConfig.CLEAN_URL);
        Logger.logD(TAG, "username and password" + mEmail + mPassword);
        encoded = base64Utils.encoded(mEmail, mPassword);
        sessionManager.setEncoded(encoded);
        cpd.show();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
                Log.d("SESSOO", "SESSOO_creator: " + loginModel.getUser().getUuid());
                sessionManager.setSessionID(loginModel.getSessionId());
                Log.d("SESSOO", "SESSOO: " + sessionManager.getSessionID());
                sessionManager.setProviderID(loginModel.getUser().getPerson().getUuid());
                Log.d("SESSOO", "SESSOO_PROVIDER: " + loginModel.getUser().getPerson().getUuid());
                Log.d("SESSOO", "SESSOO_PROVIDER_session: " + sessionManager.getProviderID());

                UrlModifiers urlModifiers = new UrlModifiers();
                String url = urlModifiers.loginUrlProvider(BuildConfig.CLEAN_URL, loginModel.getUser().getUuid());
                if (authencated) {
                    Observable<LoginProviderModel> loginProviderModelObservable = AppConstants.apiInterface.LOGIN_PROVIDER_MODEL_OBSERVABLE(url, "Basic " + encoded);
                    loginProviderModelObservable
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new DisposableObserver<LoginProviderModel>() {
                                @Override
                                public void onNext(LoginProviderModel loginProviderModel) {
                                    if (loginProviderModel.getResults().size() != 0) {
                                        for (int i = 0; i < loginProviderModel.getResults().size(); i++) {
                                            Log.i(TAG, "doInBackground: " + loginProviderModel.getResults().get(i).getUuid());
                                            sessionManager.setProviderID(loginProviderModel.getResults().get(i).getUuid());

                                            provider_url_uuid = loginProviderModel.getResults().get(i).getUuid();
//                                                success = true;
                                           /* final Account account = new Account(mEmail, "io.intelehealth.openmrs");
                                            manager.addAccountExplicitly(account, mPassword, null);
                                            Log.d("MANAGER", "MANAGER " + account);*/
                                            //offlineLogin.invalidateLoginCredentials();


                                        }
                                    }
                                    String url = urlModifiers.getUrlForPersonDetails(loginModel.getUser().getPerson().getUuid());
                                    getPersonDetails(url,mEmail,mPassword,loginModel.getUser().getUuid(),loginModel.getUser().getDisplay());

                                    //  showProgress(false);

                                    //sessionManager.setReturningUser(true);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Logger.logD(TAG, "handle provider error" + e.getMessage());
                                    cpd.dismiss();
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                }
            }

            @Override
            public void onError(Throwable e) {
                Logger.logD(TAG, "Login Failure" + e.getMessage());
                cpd.dismiss();
                Toast.makeText(LoginActivity.this, getString(R.string.error_incorrect_password), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onComplete() {
                Logger.logD(TAG, "completed");
            }
        });

    }

    private void getPersonDetails(String url, String mEmail,String mPassword,String userUUID,String chwname) {
        Observable<ClsPersonGetResponse> personGetResponseObservable = AppConstants.apiInterface.getPersonDetails(url, "Basic " + encoded,"full");

        personGetResponseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableObserver<ClsPersonGetResponse>() {
            @Override
            public void onNext(ClsPersonGetResponse clsPersonGetResponse) {
                if(clsPersonGetResponse != null){
                    Log.e("VENU PERSON Details: "," : "+clsPersonGetResponse);
                    SQLiteDatabase sqLiteDatabase = AppConstants.inteleHealthDatabaseHelper.getWriteDb();
                    //SQLiteDatabase read_db = AppConstants.inteleHealthDatabaseHelper.getReadableDatabase();

                    sqLiteDatabase.beginTransaction();
                    //read_db.beginTransaction();
                    ContentValues values = new ContentValues();

                    //StringEncryption stringEncryption = new StringEncryption();
                    String random_salt = getSalt_DATA();

                    //String random_salt = stringEncryption.getRandomSaltString();
                    Log.d("salt", "salt: " + random_salt);
                    //Salt_Getter_Setter salt_getter_setter = new Salt_Getter_Setter();
                    //salt_getter_setter.setSalt(random`_salt);


                    String hash_password = null;
                    try {
                        //hash_email = StringEncryption.convertToSHA256(random_salt + mEmail);
                        hash_password = StringEncryption.convertToSHA256(random_salt + mPassword);
                    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }

                    try {
                        values.put("username", mEmail);
                        values.put("password", hash_password);
                        values.put("creator_uuid_cred", userUUID);
                        values.put("chwname", chwname);
                        values.put("provider_uuid_cred", sessionManager.getProviderID());
                        createdRecordsCount = sqLiteDatabase.insertWithOnConflict("tbl_user_credentials", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                        sqLiteDatabase.setTransactionSuccessful();

                        Logger.logD("values", "values" + values);
                        Logger.logD("created user credentials", "create user records" + createdRecordsCount);
                    } catch (SQLException e) {
                        Log.d("SQL", "SQL user credentials: " + e);
                    } finally {
                        sqLiteDatabase.endTransaction();
                    }

                   // sessionManager.setState(clsPersonGetResponse.getPreferredAddress().getStateProvince()!= null?clsPersonGetResponse.getPreferredAddress().getStateProvince():"");
                    sessionManager.setPersionUUID(clsPersonGetResponse.getUuid());
                    sessionManager.setUserName(clsPersonGetResponse.getPreferredName().getDisplay());
                    sessionManager.setUseFirstName(clsPersonGetResponse.getPreferredName().getGivenName());
                    sessionManager.setSetupComplete(false);
                    sessionManager.setReturningUser(true);
                    sessionManager.setLocationUuid("eb374eaf-430e-465e-81df-fe94c2c515be");
                    sessionManager.setServerUrl(BuildConfig.CLEAN_URL);
                    sessionManager.setServerUrlRest("https://" + BuildConfig.CLEAN_URL + "/openmrs/ws/rest/v1/");
                    sessionManager.setServerUrlBase("https://" + BuildConfig.CLEAN_URL + "/openmrs");
                    sessionManager.setBaseUrl("https://" + BuildConfig.CLEAN_URL + "/openmrs/ws/rest/v1/");
                    sessionManager.setTriggerNoti("yes");
                    sessionManager.setPrivacyValue("Accept");
                    sessionManager.setFirstTimeLaunch(false);
                    sessionManager.setPatientCountry(clsPersonGetResponse.getPreferredAddress().getCountry());

                    sessionManager.setState_Name(clsPersonGetResponse.getAddresses().get(0).getStateProvince());
                    sessionManager.setDistrict_Name(clsPersonGetResponse.getAddresses().get(0).getCityVillage());
                    String s = sessionManager.getState_Name() + " " + sessionManager.getDistrict_Name();
                    Log.d(TAG, "state: " + s);

                    // offlineLogin.setUpOfflineLogin(mEmail, mPassword);
                    cpd.dismiss();
                    //address = cityvillage = stateprovince
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.putExtra("login", true);
                    intent.putExtra("from", "login");
                    intent.putExtra("username", "");
                    intent.putExtra("password", "");
//                startJobDispatcherService(LoginActivity.this);
                    startActivity(intent);
                    finish();
                }else{
                    cpd.dismiss();
                    Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                cpd.dismiss();
                Toast.makeText(LoginActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });

    }

    public String getSalt_DATA() {
        BufferedReader reader = null;
        String salt = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("salt.env")));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                salt = mLine;
                Log.d("SA", "SA " + salt);
            }
        } catch (Exception e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    //log the exception
                }
            }
        }
        return salt;

    }

    private void checkUserExistsOrNot(String enteredUserName) {
        cpd.show();
        UrlModifiers urlModifiers = new UrlModifiers();
        String urlString = urlModifiers.setRegistrationURL();
        String encoded = base64Utils.encoded("admin", BuildConfig.DEBUG ? "IHUser#1" : "IHUser#1");
        Observable<ClsUserGetResponse> userGetResponse = AppConstants.apiInterface.getUsersFromServer(urlString, "Basic " + encoded, enteredUserName);
        userGetResponse.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ClsUserGetResponse>() {
                    @Override
                    public void onNext(ClsUserGetResponse clsUserGetResponse) {
                        cpd.dismiss();
                        List<ResultsItem> resultList = clsUserGetResponse.getResults();
                        if (resultList == null || resultList.size() == 0) {
                            // new user - show sign up alert
                            /*isUSerExistsAlready = false;
                            image_username_valid.setVisibility(View.VISIBLE);*/
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);
                            alertDialogBuilder.setMessage(R.string.warning_sign_up);
                            alertDialogBuilder.setNeutralButton(R.string.title_sign_up, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    signUp();
                                }
                            });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        } else {
                            // existing uset - show otp
                            /*image_username_valid.setVisibility(View.GONE);
                            isUSerExistsAlready = true;
                            mEmailView.setError(getString(R.string.txt_user_exists));
                            mEmailView.requestFocus();*/

                            sendOtp(String.format("91%s",mUsernameView.getText().toString().trim()));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        cpd.dismiss();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void sendOtp(String enteredUserName) {
        cpd.show();
        UrlModifiers urlModifiers = new UrlModifiers();
        String urlString = urlModifiers.sendOtp("HXIN1701481071IN");
//        String encoded = base64Utils.encoded("admin", "IHUser#1");
        generatedOtp = new DecimalFormat("0000").format(new Random().nextInt(9999));
        Observable<SendOtp> userGetResponse = AppConstants.apiInterface.sendOtp(urlString,
                "A39e1e65900618ef9b6e16da473f8894d",
                enteredUserName,
                "OTP",
                "TIFDOC",
                String.format("Your Intelehealth Swasthsampark account verification code is :%s", generatedOtp),
                "1107162261167510445");
        userGetResponse.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<SendOtp>() {
                    @Override
                    public void onNext(SendOtp clsUserGetResponse) {
                        cpd.dismiss();
                        llOtp.setVisibility(View.VISIBLE);
                        tvResendOtp.setVisibility(View.VISIBLE);
                        mEmailSignInButton.setText(R.string.action_sign_in);
                        mPasswordView.setInputType(InputType.TYPE_CLASS_NUMBER);
                        mPasswordView.requestFocus();
                        Toast.makeText(context, R.string.otp_sent, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {
                        cpd.dismiss();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void getPassword(String enteredUserName) {
        cpd.show();
        UrlModifiers urlModifiers = new UrlModifiers();
        String urlString = urlModifiers.getPassword(BuildConfig.CLEAN_URL);
        String encoded = base64Utils.encoded("admin", BuildConfig.DEBUG ? "IHUser#1" : "IHUser#1");
        GetPassword getPassword = new GetPassword();
        getPassword.username = enteredUserName;
        Observable<GetPassword> userGetResponse = AppConstants.apiInterface.getPassword(urlString, "Basic " + encoded, getPassword);
        userGetResponse.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<GetPassword>() {
                    @Override
                    public void onNext(GetPassword response) {
                        cpd.dismiss();
//                        Toast.makeText(context, R.string.reset_password_success, Toast.LENGTH_SHORT).show();
                        UserLoginTask(enteredUserName, response.password);
                    }

                    @Override
                    public void onError(Throwable e) {
                        cpd.dismiss();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }
}