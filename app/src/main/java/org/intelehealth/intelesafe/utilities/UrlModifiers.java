package org.intelehealth.intelesafe.utilities;

import org.intelehealth.intelesafe.BuildConfig;
import org.intelehealth.intelesafe.app.IntelehealthApplication;

public class UrlModifiers {
    private SessionManager sessionManager = null;

    public String loginUrl(String CLEAN_URL) {

        String urlModifier = "session";

        String BASE_URL = "https://" + CLEAN_URL + "/openmrs/ws/rest/v1/";
        return BASE_URL + urlModifier;
    }

    public String loginUrlProvider(String CLEAN_URL, String USER_UUID) {

        String provider = "provider?user=" + USER_UUID;

        String BASE_URL = "https://" + CLEAN_URL + "/openmrs/ws/rest/v1/";
        return BASE_URL + provider;
    }

    public String patientProfileImageUrl(String patientUuid) {
        sessionManager = new SessionManager(IntelehealthApplication.getAppContext());
        String provider = "personimage/" + patientUuid;

        String BASE_URL = "https://" + sessionManager.getServerUrl() + "/openmrs/ws/rest/v1/";
        return BASE_URL + provider;
    }

    public String setPatientProfileImageUrl() {
        sessionManager = new SessionManager(IntelehealthApplication.getAppContext());
        String provider = "personimage";

        String BASE_URL = "https://" + sessionManager.getServerUrl() + "/openmrs/ws/rest/v1/";
        return BASE_URL + provider;
    }


    public String obsImageUrl(String obsUuid) {
        sessionManager = new SessionManager(IntelehealthApplication.getAppContext());
        String provider = "obs/" + obsUuid + "/value";

        String BASE_URL = "https://" + sessionManager.getServerUrl() + "/openmrs/ws/rest/v1/";
        return BASE_URL + provider;
    }

    public String obsImageDeleteUrl(String obsUuid) {
        sessionManager = new SessionManager(IntelehealthApplication.getAppContext());
        String provider = "obs/" + obsUuid;

        String BASE_URL = "https://" + sessionManager.getServerUrl() + "/openmrs/ws/rest/v1/";
        return BASE_URL + provider;
    }

    public String setObsImageUrl() {
        sessionManager = new SessionManager(IntelehealthApplication.getAppContext());
        String provider = "obs";

        String BASE_URL = "https://" + sessionManager.getServerUrl() + "/openmrs/ws/rest/v1/";
        return BASE_URL + provider;
    }

    public String setRegistrationURL() {
        String provider = "user";

        String BASE_URL = "https://"+ BuildConfig.CLEAN_URL+ "/openmrs/ws/rest/v1/";
        return BASE_URL + provider;
    }

    public String setPatientOpenMRSURL() {
        String provider = "patient";

        String BASE_URL = "https://"+ BuildConfig.CLEAN_URL+ "/openmrs/ws/rest/v1/";

        return BASE_URL + provider;
    }

    public String setUserBirthDataURL(String strUUID) {
        String provider = "person" + "/" + strUUID;

        String BASE_URL = "https://"+ BuildConfig.CLEAN_URL+ "/openmrs/ws/rest/v1/";
        return BASE_URL + provider;
    }

    public String setUserAddressURL(String strUUID) {
        String provider = "person" + "/" + strUUID + "/" + "address";

        String BASE_URL = "https://"+ BuildConfig.CLEAN_URL+"/openmrs/ws/rest/v1/";
        return BASE_URL + provider;
    }


    /**
     * for the patient DetailsGet
     * @param patientUUID
     * @return
     */
    public String getUrlForPersonDetails(String patientUUID){
        String provider = "person" + "/" + patientUUID;
        String BASE_URL = "https://"+ BuildConfig.CLEAN_URL+"/openmrs/ws/rest/v1/";
        return BASE_URL + provider;
    }

    public String resetPassword(String CLEAN_URL) {
        String urlModifier = "cpwd";
        String BASE_URL = "https://" + CLEAN_URL + "/";
        return BASE_URL + urlModifier;
    }

    public String sendOtp(String SID) {
        return String.format("https://api.kaleyra.io/v1/%s/messages", SID);
    }
}
