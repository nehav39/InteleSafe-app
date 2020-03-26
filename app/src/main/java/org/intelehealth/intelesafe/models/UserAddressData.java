package org.intelehealth.intelesafe.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sagar Shimpi
 * Github - TheSeasApps
 */
public class UserAddressData {

    public UserAddressData() {
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

    public UserAddressData(Boolean preferred, String address1, String address2, String cityVillage, String stateProvince, String country, String postalCode, String countyDistrict) {
        this.preferred = preferred;
        this.address1 = address1;
        this.address2 = address2;
        this.cityVillage = cityVillage;
        this.stateProvince = stateProvince;
        this.country = country;
        this.postalCode = postalCode;
        this.countyDistrict = countyDistrict;
    }

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
}
