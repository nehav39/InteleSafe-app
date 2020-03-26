package app.intelehealth.covid.models.UserRegistrationCall;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sagar Shimpi
 * Github - TheSeasApps
 */
public class Address {

    public Address() {
    }

    public Address(Boolean preferred, String address1, String address2, String cityVillage, String stateProvince, String country, String postalCode, String countyDistrict, String address3, String address4, String address5, String address6, String startDate, String endDate, String latitude, String longitude) {
        this.preferred = preferred;
        this.address1 = address1;
        this.address2 = address2;
        this.cityVillage = cityVillage;
        this.stateProvince = stateProvince;
        this.country = country;
        this.postalCode = postalCode;
        this.countyDistrict = countyDistrict;
        this.address3 = address3;
        this.address4 = address4;
        this.address5 = address5;
        this.address6 = address6;
        this.startDate = startDate;
        this.endDate = endDate;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @SerializedName("preferred")
    @Expose
    public Boolean preferred;
    @SerializedName("address1")
    @Expose
    public String address1;
    @SerializedName("address2")
    @Expose
    public String address2;
    @SerializedName("cityVillage")
    @Expose
    public String cityVillage;
    @SerializedName("stateProvince")
    @Expose
    public String stateProvince;
    @SerializedName("country")
    @Expose
    public String country;
    @SerializedName("postalCode")
    @Expose
    public String postalCode;
    @SerializedName("countyDistrict")
    @Expose
    public String countyDistrict;
    @SerializedName("address3")
    @Expose
    public String address3;
    @SerializedName("address4")
    @Expose
    public String address4;
    @SerializedName("address5")
    @Expose
    public String address5;
    @SerializedName("address6")
    @Expose
    public String address6;
    @SerializedName("startDate")
    @Expose
    public String startDate;
    @SerializedName("endDate")
    @Expose
    public String endDate;
    @SerializedName("latitude")
    @Expose
    public String latitude;
    @SerializedName("longitude")
    @Expose
    public String longitude;

    public Boolean getPreferred() {
        return preferred;
    }

    public void setPreferred(Boolean preferred) {
        this.preferred = preferred;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCityVillage() {
        return cityVillage;
    }

    public void setCityVillage(String cityVillage) {
        this.cityVillage = cityVillage;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountyDistrict() {
        return countyDistrict;
    }

    public void setCountyDistrict(String countyDistrict) {
        this.countyDistrict = countyDistrict;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getAddress4() {
        return address4;
    }

    public void setAddress4(String address4) {
        this.address4 = address4;
    }

    public String getAddress5() {
        return address5;
    }

    public void setAddress5(String address5) {
        this.address5 = address5;
    }

    public String getAddress6() {
        return address6;
    }

    public void setAddress6(String address6) {
        this.address6 = address6;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}
