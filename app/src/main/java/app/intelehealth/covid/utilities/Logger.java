package app.intelehealth.covid.utilities;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

/**
 * Updated by mahiti on 24/02/16.
 */
public class Logger {
    /**
     * Default constructors
     */
    private Logger() {
        // This Constructor is not Used
    }

    /**
     * function to use in catch block....
     *
     * @param tag
     * @param desc
     * @param e
     */
    public static void logE(String tag, String desc, Exception e) {
        Log.e(tag, desc, e);
        Crashlytics.getInstance().core.logException(e);
    }

    /**
     * function to use for debug and showing in console..
     *
     * @param tag
     * @param desc
     */
    public static void logD(String tag, String desc) {
        Log.d(tag, "" + desc);
    }

    /**
     * function to use for debug and showing in console....
     *
     * @param tag
     * @param desc
     */
    public static void logV(String tag, String desc) {
        Log.v(tag, "" + desc);
    }

}
