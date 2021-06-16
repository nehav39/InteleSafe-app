package org.intelehealth.swasthyasamparkp.utilities;

import android.content.Context;
import android.widget.Toast;


import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import org.intelehealth.swasthyasamparkp.app.IntelehealthApplication;

import static org.intelehealth.swasthyasamparkp.app.AppConstants.CONFIG_FILE_NAME;

public class ConfigUtils {
    public static String TAG = ConfigUtils.class.getSimpleName();
    SessionManager sessionManager = null;
    Context context;

    public ConfigUtils(Context context) {
        this.context = context;
    }

    private JSONObject jsonreader() {
        sessionManager = new SessionManager(IntelehealthApplication.getAppContext());
        JSONObject obj = null;
        try {
//            if (sessionManager.valueContains("licensekey")) {
            if (!sessionManager.getLicenseKey().isEmpty()) {
                obj = new JSONObject(FileUtils.readFileRoot(CONFIG_FILE_NAME, context)); //Load the config file

            } else {
                obj = new JSONObject(String.valueOf(FileUtils.encodeJSON(context, CONFIG_FILE_NAME)));

            }
        } catch (JSONException e) {
            Logger.logE(TAG, "Exception", e);
            Toast.makeText(context, "JsonException" + e, Toast.LENGTH_LONG).show();
        }
        return obj;
    }


    public boolean height() {
        boolean view = false;

        JSONObject object = jsonreader();
        try {
            view = object.getBoolean("mHeight");
        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return view;
    }

    public boolean weight() {
        boolean view = false;

        JSONObject object = jsonreader();
        try {
            view = object.getBoolean("mWeight");
        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return view;
    }


    public boolean temperature() {
        boolean view = false;

        JSONObject object = jsonreader();
        try {
            view = object.getBoolean("mTemperature");
        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return view;
    }

    public boolean celsius() {
        boolean view = false;

        JSONObject object = jsonreader();
        try {
            view = object.getBoolean("mCelsius");
        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return view;
    }

    public boolean fahrenheit() {
        boolean view = false;

        JSONObject object = jsonreader();
        try {
            view = object.getBoolean("mFahrenheit");
            Logger.logD(TAG, String.valueOf(view));
        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return view;
    }

    public boolean privacy_notice() {
        boolean view = false;

        JSONObject object = jsonreader();
        try {
            view = object.getBoolean("privacyNotice");
        } catch (JSONException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return view;
    }

}
