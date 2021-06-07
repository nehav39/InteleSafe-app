package org.intelehealth.intelesafe.activities.resetPasswordActivity;

//import android.accounts.AccountManager;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;

import org.intelehealth.intelesafe.BuildConfig;
import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.activities.homeActivity.HomeActivity;
import org.intelehealth.intelesafe.app.AppConstants;
import org.intelehealth.intelesafe.models.ResetPassoword;
import org.intelehealth.intelesafe.models.SendOtp;
import org.intelehealth.intelesafe.models.loginModel.LoginModel;
import org.intelehealth.intelesafe.models.loginProviderModel.LoginProviderModel;
import org.intelehealth.intelesafe.models.person.ClsPersonGetResponse;
import org.intelehealth.intelesafe.models.user.ClsUserGetResponse;
import org.intelehealth.intelesafe.utilities.Base64Utils;
import org.intelehealth.intelesafe.utilities.Logger;
import org.intelehealth.intelesafe.utilities.NetworkConnection;
import org.intelehealth.intelesafe.utilities.OfflineLogin;
import org.intelehealth.intelesafe.utilities.SessionManager;
import org.intelehealth.intelesafe.utilities.StringEncryption;
import org.intelehealth.intelesafe.utilities.UrlModifiers;
import org.intelehealth.intelesafe.widget.materialprogressbar.CustomProgressDialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class ResetPasswordActivity extends AppCompatActivity {
    private static final String EXTRA_MOBILE = "EXTRA_MOBILE";
    public static void start(Context context, String mobile) {
        Intent starter = new Intent(context, ResetPasswordActivity.class);
        starter.putExtra(EXTRA_MOBILE, mobile);
        context.startActivity(starter);
    }

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "username:password", "admin:nimda"
    };
    private final String TAG = ResetPasswordActivity.class.getSimpleName();
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
    private EditText mPasswordView, mConfirmPasswordView;
    private ImageView icLogo;

    private long createdRecordsCount = 0;
    String provider_url_uuid;
    String privacy_value;

    Button mEmailSignInButton;
    private View llPassoword, llOtp;
    private EditText mOTP;
    private TextView tvResendOtp;
    String generatedOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        sessionManager = new SessionManager(this);
        privacy_value = getIntent().getStringExtra("privacy"); //privacy_accept value retrieved from previous act.

        context = ResetPasswordActivity.this;
        sessionManager = new SessionManager(context);
        cpd = new CustomProgressDialog(context);

        setTitle(R.string.title_activity_login);

        offlineLogin = OfflineLogin.getOfflineLogin();

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
        // populateAutoComplete(); TODO: create our own autocomplete code
        mPasswordView = findViewById(R.id.et_password);
        mPasswordView.setTransformationMethod(new PasswordTransformationMethod());

        mConfirmPasswordView = findViewById(R.id.et_confirm_password);
        mConfirmPasswordView.setTransformationMethod(new PasswordTransformationMethod());
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
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.logD(TAG, "button pressed");
                attemptLogin();
            }
        });

        if (getIntent() != null) {
            String mobile = getIntent().getStringExtra(EXTRA_MOBILE);
            if (!TextUtils.isEmpty(mobile)) {
                mUsernameView.setText(mobile);
                mUsernameView.setEnabled(false);
                mPasswordView.requestFocus();
            }
        }

        llPassoword = findViewById(R.id.llPassword);
        llOtp = findViewById(R.id.llOtp);
        mOTP = findViewById(R.id.et_otp);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = String.format("91%s", mUsernameView.getText().toString());
                if (!TextUtils.isEmpty(mobile)) {
                    sendOtp(mobile);
                }
            }
        });
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
    Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
    Pattern lowerCasePatten = Pattern.compile("[a-z ]");
    Pattern digitCasePatten = Pattern.compile("[0-9 ]");

    private void attemptLogin() {
        // Store values at the time of the login attempt.
        String email = mUsernameView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        String confirmPassword = mConfirmPasswordView.getText().toString().trim();

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mUsernameView.setError(getString(R.string.enter_username));
            mUsernameView.requestFocus();
            return;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.enter_password));
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

        if (TextUtils.isEmpty(confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.enter_password));
            mConfirmPasswordView.requestFocus();
            return;
        }

        if (!confirmPassword.equals(password)) {
            mConfirmPasswordView.setError(getString(R.string.confirm_password_is_mismatched));
            mConfirmPasswordView.requestFocus();
            return;
        }

        if (NetworkConnection.isOnline(this)) {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
//            UserLoginTask(email, password);
            if (llOtp.getVisibility() == View.VISIBLE) {
                if (TextUtils.isEmpty(mOTP.getText().toString())) {
                    mOTP.setError(getString(R.string.error_field_required));
                    mOTP.requestFocus();
                    return;
                }

                String otp = mOTP.getText().toString().trim();
                if (!otp.equals(generatedOtp)) {
                    if (BuildConfig.DEBUG && otp.equals("0000")) {
                        System.out.println("testing with fake otp");
                    } else {
                        mOTP.setError(getString(R.string.invalid_otp));
                        mOTP.requestFocus();
                        return;
                    }
                }
                resetPassword(email, password);
            } else {
                String mobile = String.format("91%s", mUsernameView.getText().toString());
                if (!TextUtils.isEmpty(mobile)) {
                    sendOtp(mobile);
                }
            }
        } else {
            //offlineLogin.login(email, password);
//            offlineLogin.offline_login(email, password);
            Toast.makeText(context, R.string.no_network, Toast.LENGTH_SHORT).show();
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
                                    getPersonDetails(url, mEmail, mPassword, loginModel.getUser().getUuid(), loginModel.getUser().getDisplay());

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
                Toast.makeText(ResetPasswordActivity.this, getString(R.string.error_incorrect_password), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onComplete() {
                Logger.logD(TAG, "completed");
            }
        });

    }

    private void getPersonDetails(String url, String mEmail, String mPassword, String userUUID, String chwname) {
        Observable<ClsPersonGetResponse> personGetResponseObservable = AppConstants.apiInterface.getPersonDetails(url, "Basic " + encoded, "full");

        personGetResponseObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableObserver<ClsPersonGetResponse>() {
            @Override
            public void onNext(ClsPersonGetResponse clsPersonGetResponse) {
                if (clsPersonGetResponse != null) {
                    Log.e("VENU PERSON Details: ", " : " + clsPersonGetResponse);
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
                    sessionManager.setLocationUuid("b56d5d16-bf89-4ac0-918d-e830fbfba290");
                    sessionManager.setServerUrl(BuildConfig.CLEAN_URL);
                    sessionManager.setServerUrlRest("https://" + BuildConfig.CLEAN_URL + "/openmrs/ws/rest/v1/");
                    sessionManager.setServerUrlBase("https://" + BuildConfig.CLEAN_URL + "/openmrs");
                    sessionManager.setBaseUrl("https://" + BuildConfig.CLEAN_URL + "/openmrs/ws/rest/v1/");
                    sessionManager.setTriggerNoti("yes");
                    sessionManager.setPrivacyValue("Accept");
                    sessionManager.setFirstTimeLaunch(false);
                    sessionManager.setPatientCountry(clsPersonGetResponse.getPreferredAddress().getCountry());
                    // offlineLogin.setUpOfflineLogin(mEmail, mPassword);
                    cpd.dismiss();
                    Intent intent = new Intent(ResetPasswordActivity.this, HomeActivity.class);
                    intent.putExtra("login", true);
                    intent.putExtra("from", "login");
                    intent.putExtra("username", "");
                    intent.putExtra("password", "");
//                startJobDispatcherService(LoginActivity.this);
                    startActivity(intent);
                    finish();
                } else {
                    cpd.dismiss();
                    Toast.makeText(ResetPasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                cpd.dismiss();
                Toast.makeText(ResetPasswordActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
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

    private void checkUserExistsOrNot(String enteredUserName, String password) {
        cpd.show();
        UrlModifiers urlModifiers = new UrlModifiers();
        String urlString = urlModifiers.setRegistrationURL();
        String encoded = base64Utils.encoded("admin", "Admin123");
        Observable<ClsUserGetResponse> userGetResponse = AppConstants.apiInterface.getUsersFromServer(urlString, "Basic " + encoded, enteredUserName);
        userGetResponse.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ClsUserGetResponse>() {
                    @Override
                    public void onNext(ClsUserGetResponse clsUserGetResponse) {
                        cpd.dismiss();
//                        resetPassword(enteredUserName, password);
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

    private void resetPassword(String enteredUserName, String password) {
        cpd.show();
        UrlModifiers urlModifiers = new UrlModifiers();
        String urlString = urlModifiers.resetPassword(BuildConfig.CLEAN_URL);
        String encoded = base64Utils.encoded("admin", "Admin123");
        ResetPassoword resetPassoword = new ResetPassoword();
        resetPassoword.username = enteredUserName;
        resetPassoword.password = password;
        Observable<ResetPassoword> userGetResponse = AppConstants.apiInterface.resetPassword(urlString, "Basic " + encoded, resetPassoword);
        userGetResponse.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<ResetPassoword>() {
                    @Override
                    public void onNext(ResetPassoword clsUserGetResponse) {
                        cpd.dismiss();
                        Toast.makeText(context, R.string.reset_password_success, Toast.LENGTH_SHORT).show();
                        UserLoginTask(enteredUserName, password);
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
        if (!NetworkConnection.isOnline(context)) {
            Toast.makeText(context, R.string.no_network, Toast.LENGTH_SHORT).show();
            return;
        }

        cpd.show();
        UrlModifiers urlModifiers = new UrlModifiers();
        String urlString = urlModifiers.sendOtp("HXIN1701481071IN");
//        String encoded = base64Utils.encoded("admin", "Admin123");
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
                        Toast.makeText(context, R.string.otp_sent, Toast.LENGTH_SHORT).show();
                        llOtp.setVisibility(View.VISIBLE);
                        llPassoword.setVisibility(View.GONE);
                        mOTP.requestFocus();
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
