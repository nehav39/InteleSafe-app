package org.intelehealth.swasthyasamparkp.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.util.Set;
import java.util.UUID;

public class SessionManager {
    // Shared preferences file name
    private static final String PREF_NAME = "Intelehealth";
    private static final String VISIT_ID = "visitID";
    private static final String BASE_URL = "base_url";
    private static final String ENCODED = "encoded";
    private static final String PULL_EXECUTED_TIME = "pullexecutedtime";
    private static final String KEY_PREF_SETUP_COMPLETE = "setup";
    private static final String APP_LANGUAGE = "Language";
    private static final String SESSION_ID = "sessionid";
    private static final String CREATOR_ID = "creatorid";
    private static final String PROVIDER_ID = "providerid";
    private static final String CHWNAME = "chwname";
    private static final String KEY_PREF_SERVER_URL_REST = "serverurl";
    private static final String KEY_PREF_SERVER_URL = "url";
    private static final String KEY_PREF_SERVER_URL_BASE = "serverurlbase";
    private static final String KEY_PREF_LOCATION_UUID = "locationuuid";
    private static final String KEY_PREF_LOCATION_NAME = "locationname";
    private static final String KEY_PREF_LOCATION_DESCRIPTION = "locationdesc";
    private static final String LICENSE_KEY = "licensekey";
    private static final String persionUUID = "persionUUID";
    private static final String personName = "personName";
    private static final String privacyValue = "privacyValue";
    private static final String DATE = "date";
    private static final String TIME = "time";
    private static final String FIRST_TIME_SYNC_EXECUTE = "FIRST_TIME_SYNC_EXECUTE";
    private static final String LICENSE = "license";
    private static final String RETURNING = "returning";
    private static final String PULLED = "pulled";
    private static final String NEW_DATABASE = "newDatabase";
    private static final String FIRST_TIME_LAUNCHED = "firsttimelaunched";
    private static final String SYNC_FINISHED = "syncfinished";
    private static final String LAST_PULLED_EXECUTED_DATE_TIME = "lastpulledexecutedtimeanddate";
    private static final String LAST_SYNC_SUCCESS_DATE_TIME = "lastsyncsuccessdatetime";
    private static final String LAST_SYNC_TIME_AGO = "LAST_SYNC_TIME_AGO";
    private static final String PULL_SYNC_FINISHED = "pullsyncfinished";
    private static final String PUSH_SYNC_FINISHED = "pushsyncfinished";
    private static final String MIND_MAP_SERVER_URL = "mindmapurl";
    private static final String RETURNING_USER = "returninguser";
    private static final String VISIT_SUMMARY = "visit_summary";
    private static final String EXAM = "exam_";
    private static final String MIGRATION_KEY = "migrationkey";
    private static final String TRIGGER_NOTI = "TRIGGER_NOTI";
    private static final String OFFLINE_OPENMRSID = "OFFLINE_OPENMRSID";
    private static final String CURRENT_LANG = "CURRENT_LANG";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String FIRST_CHECKIN = "FIRST_CHECKIN";
    private static final String DB_CLEAR = "DB_CLEAR";
    private static final String STATE = "STATE";
    private static final String DISTRICT = "DISTRICT";
    private static final String PROFILE_DUMMY_ENCOUNTER_UID = "PROFILE_DUMMY_ENCOUNTER_UID";
    private static final String PROFILE_OBS_UID = "PROFILE_OBS_UID";
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();
    // Shared Preferences
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;
    // Shared pref mode
    private int PRIVATE_MODE = 0;

    private static final String personFirstName = "personFirstName"; // Added by venu N  to store user First Name on 07/04/2020.
    private static final String personSelectedCountry = "personSelectedCountry";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public int getSystemLanguage(String systemLanguage) {
        if (systemLanguage == "hi")
            return 0;
        else
            return 1;
    }


    public String getVisitId() {
        return pref.getString(VISIT_ID, "");
    }

    public void setVisitId(String token) {
        editor.putString(VISIT_ID, token);
        editor.commit();
    }

    public String getState_Name() {
        return pref.getString(STATE, "");
    }

    public void setState_Name(String name) {
        editor.putString(STATE, name);
        editor.commit();
    }

    public String getDistrict_Name() {
        return pref.getString(DISTRICT, "");
    }

    public void setDistrict_Name(String name) {
        editor.putString(DISTRICT, name);
        editor.commit();
    }

    public String getBaseUrl() {
        return pref.getString(BASE_URL, "https://openmrs.intelehealth.io");
    }

    public void setBaseUrl(String baseUrl) {
        editor.putString(BASE_URL, baseUrl);
        editor.commit();
    }

    public String getEncoded() {
        return pref.getString(ENCODED, "");
    }

    public void setEncoded(String encoded) {
        editor.putString(ENCODED, encoded);
        editor.commit();
    }

    public String getPullExcutedTime() {
        return pref.getString(PULL_EXECUTED_TIME, "2006-08-22 22:21:48 ");
    }

    public void setPullExcutedTime(String pullExcutedTime) {
        editor.putString(PULL_EXECUTED_TIME, pullExcutedTime);
        editor.commit();
    }

    public String getAppLanguage() {
        /*if(pref.getString(APP_LANGUAGE, "").equals("हिंदी"))
            return "hi";

        if(pref.getString(APP_LANGUAGE, "").equals("मराठी"))
        return "mr";

        else
            return "en";*/
        return pref.getString(APP_LANGUAGE, "en");
    }

    public void setAppLanguage(String pullExcutedTime) {
        editor.putString(APP_LANGUAGE, pullExcutedTime);
        editor.commit();
    }

    public boolean isSetupComplete() {
        return pref.getBoolean(KEY_PREF_SETUP_COMPLETE, false);
    }

    public void setSetupComplete(Boolean setupComplete) {
        editor.putBoolean(KEY_PREF_SETUP_COMPLETE, setupComplete);
        editor.commit();
    }

    public String getSessionID() {
        return pref.getString(SESSION_ID, "");
    }

    public void setSessionID(String sessionID) {
        editor.putString(SESSION_ID, sessionID);
        editor.commit();
    }

    public String getProviderID() {
        return pref.getString(PROVIDER_ID, "");
    }

    public void setProviderID(String providerID) {
        editor.putString(PROVIDER_ID, providerID);
        editor.commit();
    }

    public String getCreatorID() {
        return pref.getString(CREATOR_ID, "");
    }

    public void setCreatorID(String creatorID) {
        editor.putString(CREATOR_ID, creatorID);
        editor.commit();
    }

    public String getChwname() {
        return pref.getString(CHWNAME, "");
    }

    public void setChwname(String chwname) {
        editor.putString(CHWNAME, chwname);
        editor.commit();
    }

    public String getLocationName() {
        return pref.getString(KEY_PREF_LOCATION_NAME, "");
    }

    public void setLocationName(String locationName) {
        editor.putString(KEY_PREF_LOCATION_NAME, locationName);
        editor.commit();
    }

    public String getLocationUuid() {
        return pref.getString(KEY_PREF_LOCATION_UUID, "");
    }

    public void setLocationUuid(String locationUuid) {
        editor.putString(KEY_PREF_LOCATION_UUID, locationUuid);
        editor.commit();
    }

    public String getLocationDescription() {
        return pref.getString(KEY_PREF_LOCATION_DESCRIPTION, "");
    }

    public void setLocationDescription(String locationDescription) {
        editor.putString(KEY_PREF_LOCATION_DESCRIPTION, locationDescription);
        editor.commit();
    }

    public String getServerUrl() {
        return pref.getString(KEY_PREF_SERVER_URL, "");
    }

    public void setServerUrl(String serverUrl) {
        editor.putString(KEY_PREF_SERVER_URL, serverUrl);
        editor.commit();
    }

    public String getTriggerNoti() {
        return pref.getString(TRIGGER_NOTI, "");
    }

    public void setTriggerNoti(String flag) {
        editor.putString(TRIGGER_NOTI, flag);
        editor.commit();
    }

    public String getOfllineOpenMRSID() {
        return pref.getString(OFFLINE_OPENMRSID, "");
    }

    public void setOfllineOpenMRSID(String id) {
        editor.putString(OFFLINE_OPENMRSID, id);
        editor.commit();
    }

    public String getFirstCheckin() {
        return pref.getString(FIRST_CHECKIN, "false");
    }

    public void setFirstCheckin(String flag) {
        editor.putString(FIRST_CHECKIN, flag);
        editor.commit();
    }

    public String getCurrentLang() {
        return pref.getString(CURRENT_LANG, "");
    }

    public void setCurrentLang(String lang) {
        editor.putString(CURRENT_LANG, lang);
        editor.commit();
    }

    public String getDBCLEAR() {
        return pref.getString(DB_CLEAR, "");
    }

    public void setDBCLEAR(String flag) {
        editor.putString(DB_CLEAR, flag);
        editor.commit();
    }

    public String getServerUrlRest() {
        return pref.getString(KEY_PREF_SERVER_URL_REST, "");
    }

    public void setServerUrlRest(String serverUrlRest) {
        editor.putString(KEY_PREF_SERVER_URL_REST, serverUrlRest);
        editor.commit();
    }

    public String getServerUrlBase() {
        return pref.getString(KEY_PREF_SERVER_URL_BASE, "");
    }

    public void setServerUrlBase(String serverUrlBase) {
        editor.putString(KEY_PREF_SERVER_URL_BASE, serverUrlBase);
        editor.commit();
    }

    public String getLicenseKey() {
        return pref.getString(LICENSE_KEY, "");
    }

    public void setLicenseKey(String licenseKey) {
        editor.putString(LICENSE_KEY, licenseKey);
        editor.commit();
    }

    public String getPersionUUID() {
        return pref.getString(persionUUID, "");
    }

    public void setPersionUUID(String uuid) {
        editor.putString(persionUUID, uuid);
        editor.commit();
    }

    public String getPrivacyValue() {
        return pref.getString(privacyValue, "");
    }

    public void setPrivacyValue(String value) {
        editor.putString(privacyValue, value);
        editor.commit();
    }

    public String getUserName() {
        return pref.getString(personName, "");
    }

    public void setUserName(String value) {
        editor.putString(personName, value);
        editor.commit();
    }


    public void setState(String state) {
        editor.putString("STATE_DATA", state);
        editor.commit();
    }

    public String getState() {
        return pref.getString("STATE_DATA", "");
    }


    public void deleteLicensekey() {
        editor.remove(LICENSE_KEY);
        editor.commit();
    }

    public String getDate() {
        return pref.getString(DATE, "");
    }

    public void setDate(String date) {
        editor.putString(DATE, date);
        editor.commit();
    }

    public String getTime() {
        return pref.getString(TIME, "");
    }

    public void setTime(String time) {
        editor.putString(TIME, time);
        editor.commit();
    }

    public boolean valueContains(String value) {
        boolean hasvalue = false;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        hasvalue = sharedPreferences.contains(value);
        return hasvalue;
    }

    public boolean isFirstTimeSyncExcuted() {
        return pref.getBoolean(FIRST_TIME_SYNC_EXECUTE, true);
    }

    public void setFirstTimeSyncExecute(Boolean firstTimeSyncExecute1) {
        editor.putBoolean(FIRST_TIME_SYNC_EXECUTE, firstTimeSyncExecute1);
        editor.commit();
    }

    public boolean isReturning() {
        return pref.getBoolean(RETURNING, false);
    }

    public void setReturning(Boolean returning) {
        editor.putBoolean(RETURNING, returning);
        editor.commit();
    }

    public String isPulled() {
        return pref.getString(PULLED, "2006-08-22 22:21:48");
    }  //getting the sync value  and time and saving in the sharedpref

    public void setPulled(String pulled) {
        editor.putString(PULLED, pulled);
        editor.commit();
    }

    public String getNewDatabase() {
        return pref.getString(NEW_DATABASE, "");
    }  //getting the sync value  and time and saving in the sharedpref

    public void setNewDatabase(String newDatabase) {
        editor.putString(NEW_DATABASE, newDatabase);
        editor.commit();
    }

    public boolean newDatabaseContains(String value) {
        boolean hasvalue = false;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(_context);
        hasvalue = sharedPreferences.contains(value);
        return hasvalue;
    }

    public boolean isFirstTimeLaunched() {
        return pref.getBoolean(FIRST_TIME_LAUNCHED, true);
    }

    public void setFirstTimeLaunched(Boolean firstTimeLaunched) {
        editor.putBoolean(FIRST_TIME_LAUNCHED, firstTimeLaunched);
        editor.commit();
    }


    public boolean isSyncFinished() {
        return pref.getBoolean(SYNC_FINISHED, false);
    }

    public void setSyncFinished(Boolean syncFinished) {
        editor.putBoolean(SYNC_FINISHED, syncFinished);
        editor.commit();
    }

    public String getLastPulledDateTime() {
        return pref.getString(LAST_PULLED_EXECUTED_DATE_TIME, "01 Jan 2019 12:15:26");
    }  //getting the sync value  and time and saving in the sharedpref

    public void setLastPulledDateTime(String lastPulledDateTime) {
        editor.putString(LAST_PULLED_EXECUTED_DATE_TIME, lastPulledDateTime);
        editor.commit();
    }

    public String getLastSyncDateTime() {
        return pref.getString(LAST_SYNC_SUCCESS_DATE_TIME, "- - - -");
    }  //getting the sync value  and time and saving in the sharedpref

    public void setLastSyncDateTime(String lastPulledDateTime) {
        editor.putString(LAST_SYNC_SUCCESS_DATE_TIME, lastPulledDateTime);
        editor.commit();
    }

    public String getLastTimeAgo() {
        return pref.getString(LAST_SYNC_TIME_AGO, "");
    }  //getting the sync value  and time and saving in the sharedpref

    public void setLastTimeAgo(String lastPulledDateTime) {
        editor.putString(LAST_SYNC_TIME_AGO, lastPulledDateTime);
        editor.commit();
    }

    public boolean isPullSyncFinished() {
        return pref.getBoolean(PULL_SYNC_FINISHED, false);
    }

    public void setPullSyncFinished(Boolean syncFinished) {
        editor.putBoolean(PULL_SYNC_FINISHED, syncFinished);
        editor.commit();
    }

    public boolean isPushSyncFinished() {
        return pref.getBoolean(PUSH_SYNC_FINISHED, false);
    }

    public void setPushSyncFinished(Boolean syncFinished) {
        editor.putBoolean(PUSH_SYNC_FINISHED, syncFinished);
        editor.commit();
    }

    public String getMindMapServerUrl() {
        return pref.getString(MIND_MAP_SERVER_URL, "https://mindmaps2.intelehealth.io");
    }  //getting the sync value  and time and saving in the sharedpref

    public void setMindMapServerUrl(String mindMapServerUrl) {
        editor.putString(MIND_MAP_SERVER_URL, mindMapServerUrl);
        editor.commit();
    }

    public boolean isReturningUser() {
        return pref.getBoolean(RETURNING_USER, false);
    }

    public void setReturningUser(Boolean returningUser) {
        editor.putBoolean(RETURNING_USER, returningUser);
        editor.commit();
    }

    public Set<String> getVisitSummary(String patientUUid) {

        return pref.getStringSet(EXAM + patientUUid, null);
    }

    public void setVisitSummary(String patientUuid, Set<String> selectedExams) {
        editor.putStringSet(EXAM + patientUuid, selectedExams);
        editor.commit();

    }

    public void removeVisitSummary(String patientUuid, String visitUuid) {
        editor.remove(EXAM + patientUuid + "_" + visitUuid);
        editor.commit();

    }

    public boolean isMigration() {
        return pref.getBoolean(MIGRATION_KEY, false);
    }

    public void setMigration(Boolean migration) {
        editor.putBoolean(MIGRATION_KEY, migration);
        editor.commit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }


    /**
     * for getting User First Name only by Venu N
     *
     * @return
     */
    public String getUserFirstName() {
        return pref.getString(personFirstName, "");
    }

    /**
     * to set User First Name only by Venu N
     *
     * @param value
     */
    public void setUseFirstName(String value) {
        editor.putString(personFirstName, value);
        editor.commit();
    }

    /**
     * for setting the patient Selected Country
     *
     * @param countryStr
     */
    public void setPatientCountry(String countryStr) {
        editor.putString(personSelectedCountry, countryStr);
        editor.commit();
    }


    /**
     * get patient Country.
     *
     * @return
     */
    public String getPatientCountry() {
        return pref.getString(personSelectedCountry, "");
    }

    public void cachePdfPath(String url, String downloadPath) {
        editor.putString(url, downloadPath);
        editor.apply();
    }

    public String getCachedPdfPath(String url) {
        return pref.getString(url, null);
    }

    public String getDummyEncounterUidForProfile() {
        String string = pref.getString(PROFILE_DUMMY_ENCOUNTER_UID, null);
        if (TextUtils.isEmpty(string)) {
            string = UUID.randomUUID().toString();
            pref.edit().putString(PROFILE_DUMMY_ENCOUNTER_UID, string).apply();
        }
        return string;
    }

    public String getObsUidForProfile() {
        String string = pref.getString(PROFILE_OBS_UID, null);
        if (TextUtils.isEmpty(string)) {
            string = UUID.randomUUID().toString();
            pref.edit().putString(PROFILE_OBS_UID, string).apply();
        }
        return string;
    }
}