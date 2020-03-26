package app.intelehealth.covid.networkApiCalls;


import app.intelehealth.covid.models.DownloadMindMapRes;
import app.intelehealth.covid.models.GetDistrictRes;
import app.intelehealth.covid.models.GetOpenMRS;
import app.intelehealth.covid.models.GetUserCallRes.UserCallRes;
import app.intelehealth.covid.models.Location;
import app.intelehealth.covid.models.NewUserCreationCall.UserCreationData;
import app.intelehealth.covid.models.ObsImageModel.ObsJsonResponse;
import app.intelehealth.covid.models.ObsImageModel.ObsPushDTO;
import app.intelehealth.covid.models.Results;
import app.intelehealth.covid.models.UUIDResData;
import app.intelehealth.covid.models.UserAddressData;
import app.intelehealth.covid.models.UserBirthData;
import app.intelehealth.covid.models.UserRegistrationCall.UserSignupData;
import app.intelehealth.covid.models.dto.ResponseDTO;
import app.intelehealth.covid.models.loginModel.LoginModel;
import app.intelehealth.covid.models.loginProviderModel.LoginProviderModel;
import app.intelehealth.covid.models.patientImageModelRequest.PatientProfile;
import app.intelehealth.covid.models.pushRequestApiCall.PushRequestApiCall;
import app.intelehealth.covid.models.pushResponseApiCall.PushResponseApiCall;
import io.reactivex.Observable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiInterface {


    @GET("location?tag=Login%20Location")
    Observable<Results<Location>> LOCATION_OBSERVABLE(@Query("v") String representation);


    @DELETE
    Call<Void> DELETE_ENCOUNTER(@Url String url,
                                @Header("Authorization") String authHeader);

    //EMR-Middleware/webapi/pull/pulldata/
    @GET
    Call<ResponseDTO> RESPONSE_DTO_CALL(@Url String url,
                                        @Header("Authorization") String authHeader);

    @GET
    Observable<LoginModel> LOGIN_MODEL_OBSERVABLE(@Url String url,
                                                  @Header("Authorization") String authHeader);

    @GET
    Observable<LoginProviderModel> LOGIN_PROVIDER_MODEL_OBSERVABLE(@Url String url,
                                                                   @Header("Authorization") String authHeader);

    @Headers({"Accept: application/json"})
    @POST
    Single<PushResponseApiCall> PUSH_RESPONSE_API_CALL_OBSERVABLE(@Url String url,
                                                                  @Header("Authorization") String authHeader,
                                                                  @Body PushRequestApiCall pushRequestApiCall);

    @GET
    Observable<ResponseBody> PERSON_PROFILE_PIC_DOWNLOAD(@Url String url,
                                                         @Header("Authorization") String authHeader);

    @POST
    Single<ResponseBody> PERSON_PROFILE_PIC_UPLOAD(@Url String url,
                                                   @Header("Authorization") String authHeader,
                                                   @Body PatientProfile patientProfile);

    @GET
    Observable<ResponseBody> OBS_IMAGE_DOWNLOAD(@Url String url,
                                                @Header("Authorization") String authHeader);

    @Headers({"Accept: application/json"})
    @POST
    @Multipart
    Observable<ObsJsonResponse> OBS_JSON_RESPONSE_OBSERVABLE(@Url String url,
                                                             @Header("Authorization") String authHeader,
                                                             @Part MultipartBody.Part image,
                                                             @Part("json") ObsPushDTO obsJsonRequest);

    @DELETE
    Observable<Void> DELETE_OBS_IMAGE(@Url String url, @Header("Authorization") String authHeader);


    @GET("/api/mindmap/download")
    Observable<DownloadMindMapRes> DOWNLOAD_MIND_MAP_RES_OBSERVABLE(@Query("key") String licenseKey);

    @GET("/openmrs/ws/rest/v1/location")
    Observable<GetDistrictRes> GET_DISTRICT_RES_OBSERVABLE(@Query("tag") String tag);

    @POST
    Observable<UserCallRes> REGISTER_USER(@Url String url,
                                          @Header("Authorization") String authHeader,
                                          @Body UserCreationData userCreationData);

    @POST
    Observable<ResponseBody> REGISTER_USER_BIRTHDATA(@Url String url,
                                                     @Header("Authorization") String authHeader,
                                                     @Body UserBirthData userBirthData);


    @POST
    Observable<ResponseBody> REGISTER_USER_ADDRESS(@Url String url,
                                                   @Header("Authorization") String authHeader,
                                                   @Body UserAddressData userAddressData);


    @POST
    Observable<ResponseBody> SET_PATIENT_OpenMRSID(@Url String url,
                                                   @Header("Authorization") String authHeader,
                                                   @Body UUIDResData uuidResData);

    @GET("/openmrs/module/idgen/generateIdentifier.form")
    Observable<GetOpenMRS> GET_OPENMRSID(@Query("source") String source,
                                         @Query("username") String userName,
                                         @Query("password") String password);

//    @GET("/api/hfn_app_update.json")
//    Single<CheckAppUpdateRes> checkAppUpdate();


}
