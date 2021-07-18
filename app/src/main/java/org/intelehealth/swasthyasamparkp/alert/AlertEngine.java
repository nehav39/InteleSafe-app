package org.intelehealth.swasthyasamparkp.alert;

import android.content.Context;
import android.util.Log;

import org.intelehealth.swasthyasamparkp.utilities.FileUtils;
import org.intelehealth.swasthyasamparkp.utilities.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class AlertEngine {
    private static final String FILE_NAME = "alert_engine.json";
    private static final int HEALTHY = 1;
    private static final int MILD = 2;
    private static final int MODERATE = 3;
    private static final int SEVERE = 4;
    private Context mContext;
    private int mAlertType = HEALTHY;
    private String mAlertMessageToPatient = "";
    private String mAlertMessageToTeleCaller = "";
    private JSONObject mConfigData = null;
    private HashMap<String, Integer> mScanHistory = new HashMap<String, Integer>();
    private MessageListener mMessageListener;
    private SessionManager mSessionManager;

    public interface MessageListener {
        void onMessageGenerated(String message, int type);
    }

    public AlertEngine(Context context, MessageListener messageListener) {
        mContext = context;
        mMessageListener = messageListener;
        mSessionManager = new SessionManager(mContext);
    }

    public void handleItemUnselected(String itemID) {
        mScanHistory.remove(itemID);
        generateResult();
    }


    /**
     * get the config json data from assets
     *
     * @return
     */
    private JSONObject getAlertConfigInfo() {
        if (mConfigData == null) {
            mConfigData = FileUtils.encodeJSON(mContext, FILE_NAME);
        }
        return mConfigData;
    }

    public int getAlertType() {
        return mAlertType;
    }

    public void setAlertType(int alertType) {
        this.mAlertType = alertType;
    }

    public String getAlertMessageToPatient() {
        return mAlertMessageToPatient;
    }

    public void setAlertMessageToPatient(String alertMessageToPatient) {
        this.mAlertMessageToPatient = alertMessageToPatient;
    }

    public String getAlertMessageToTeleCaller() {
        return mAlertMessageToTeleCaller;
    }

    public void setAlertMessageToTeleCaller(String alertMessageToTeleCaller) {
        this.mAlertMessageToTeleCaller = alertMessageToTeleCaller;
    }

    /**
     * Find out the alert type & messages as per the inputs
     *
     * @param itemID
     * @param value
     */
    public void scanForAlert(String itemID, String value) {
        if (itemID == null || value == null || itemID.isEmpty() || value.isEmpty()) {
            return;
        }
        JSONObject engineConfigJsonObject = getAlertConfigInfo();
        int alertType = HEALTHY;
        try {
            if (engineConfigJsonObject.has("data") && engineConfigJsonObject.getJSONObject("data").has(itemID)) {
                JSONObject itemJsonObject = engineConfigJsonObject.getJSONObject("data").getJSONObject(itemID);
                // vitals info
                if (itemJsonObject.has("input-type") && itemJsonObject.getString("input-type").equalsIgnoreCase("number")) {
                    double enteredValue = Double.parseDouble(value);
                    double severeMin = itemJsonObject.getDouble("severe-min");
                    double severeMax = itemJsonObject.getDouble("severe-max");
                    if (severeMin == -1) {
                        if (enteredValue <= severeMax) {
                            alertType = SEVERE;
                        }
                    } else if (severeMax == -1) {
                        if (enteredValue >= severeMin) {
                            alertType = SEVERE;
                        }
                    } else {
                        if (enteredValue >= severeMin && enteredValue <= severeMax) {
                            alertType = SEVERE;
                        }
                    }
                    if (alertType == HEALTHY) {
                        double moderateMin = itemJsonObject.getDouble("moderate-min");
                        double moderateMax = itemJsonObject.getDouble("moderate-max");
                        if (moderateMin == -1) {
                            if (enteredValue <= moderateMax) {
                                alertType = MODERATE;

                            }
                        } else if (moderateMax == -1) {
                            if (enteredValue >= moderateMin) {
                                alertType = MODERATE;
                            }
                        } else {
                            if (enteredValue >= moderateMin && enteredValue <= moderateMax) {
                                alertType = MODERATE;
                            }
                        }
                    }
                    if (alertType == HEALTHY) {
                        double mildMin = itemJsonObject.getDouble("mild-min");
                        double mildMax = itemJsonObject.getDouble("mild-max");
                        if (mildMin == -1) {
                            if (enteredValue <= mildMax) {
                                alertType = MILD;
                            }
                        } else if (mildMax == -1) {
                            if (enteredValue >= mildMin) {
                                alertType = MILD;
                            }
                        } else {
                            if (enteredValue >= mildMin && enteredValue <= mildMax) {
                                alertType = MILD;
                            }
                        }
                    }

                } else { // symptoms
                    if (itemJsonObject.getString("alert-category").equalsIgnoreCase("severe")) {
                        alertType = SEVERE;
                    } else if (itemJsonObject.getString("alert-category").equalsIgnoreCase("moderate")) {
                        alertType = MODERATE;
                    } else if (itemJsonObject.getString("alert-category").equalsIgnoreCase("mild")) {
                        alertType = MILD;
                    }
                }
            }

            mScanHistory.put(itemID, alertType);

            generateResult();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // we can do check at last end or prepare the result in each userinput for real time
    // now i am implementing for real time result calculation
    public void generateResult() {
        try {
            Log.v("AlertEngine", mScanHistory + "");

            if (mScanHistory.isEmpty()) {
                setAlertMessageToPatient("");
                setAlertMessageToTeleCaller("");
                setAlertType(0);
            } else {
                Map.Entry<String, Integer> maxEntry = Collections.max(mScanHistory.entrySet(), new Comparator<Map.Entry<String, Integer>>() {
                    public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                        return e1.getValue()
                                .compareTo(e2.getValue());
                    }
                });

                setAlertType(mScanHistory.get(maxEntry.getKey()));
                JSONObject engineConfigJsonObject = getAlertConfigInfo();
                JSONObject messageJsonObject = engineConfigJsonObject.getJSONObject("alert_messages");
                String patientMessage = "";
                String teleCallerMessage = "";
                String patientKey = "patient";
                String teleCallerKey = "tele_caller";
                if (mSessionManager.getAppLanguage().equalsIgnoreCase("hi")) {
                    patientKey = "patient_hi";
                    teleCallerKey = "tele_caller_hi";
                }
                if (getAlertType() == HEALTHY) {
                    patientMessage = messageJsonObject.getJSONObject("healthy").getString(patientKey);
                    teleCallerMessage = messageJsonObject.getJSONObject("healthy").getString(teleCallerKey);

                } else if (getAlertType() == MILD) {
                    patientMessage = messageJsonObject.getJSONObject("mild").getString(patientKey);
                    teleCallerMessage = messageJsonObject.getJSONObject("mild").getString(teleCallerKey);
                } else if (getAlertType() == MODERATE) {
                    patientMessage = messageJsonObject.getJSONObject("moderate").getString(patientKey);
                    teleCallerMessage = messageJsonObject.getJSONObject("moderate").getString(teleCallerKey);
                } else if (getAlertType() == SEVERE) {
                    patientMessage = messageJsonObject.getJSONObject("severe").getString(patientKey);
                    teleCallerMessage = messageJsonObject.getJSONObject("severe").getString(teleCallerKey);
                }
                setAlertMessageToPatient(patientMessage);
                setAlertMessageToTeleCaller(teleCallerMessage);
            }
            Log.v("AlertEngine", "Type - " + getAlertType());
            mMessageListener.onMessageGenerated(getAlertMessageToPatient(), getAlertType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
