package org.intelehealth.intelesafe.models.UserRegistrationCall;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sagar Shimpi
 * Github - TheSeasApps
 */
public class Name {

    public Name() {
    }

    public Name(String givenName, String middleName, String familyName, Boolean preferred, String prefix, String familyNamePrefix, String familyNameSuffix, String degree, String location) {
        this.givenName = givenName;
        this.middleName = middleName;
        this.familyName = familyName;
        this.preferred = preferred;
        this.prefix = prefix;
        this.familyNamePrefix = familyNamePrefix;
        this.familyNameSuffix = familyNameSuffix;
        this.degree = degree;
        this.location = location;
    }

    @SerializedName("givenName")
    @Expose
    public String givenName;
    @SerializedName("middleName")
    @Expose
    public String middleName;
    @SerializedName("familyName")
    @Expose
    public String familyName;
    @SerializedName("preferred")
    @Expose
    public Boolean preferred;
    @SerializedName("prefix")
    @Expose
    public String prefix;
    @SerializedName("familyNamePrefix")
    @Expose
    public String familyNamePrefix;
    @SerializedName("familyNameSuffix")
    @Expose
    public String familyNameSuffix;
    @SerializedName("degree")
    @Expose
    public String degree;
    @SerializedName("location")
    @Expose
    public String location;

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public Boolean getPreferred() {
        return preferred;
    }

    public void setPreferred(Boolean preferred) {
        this.preferred = preferred;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getFamilyNamePrefix() {
        return familyNamePrefix;
    }

    public void setFamilyNamePrefix(String familyNamePrefix) {
        this.familyNamePrefix = familyNamePrefix;
    }

    public String getFamilyNameSuffix() {
        return familyNameSuffix;
    }

    public void setFamilyNameSuffix(String familyNameSuffix) {
        this.familyNameSuffix = familyNameSuffix;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


}
