package org.intelehealth.swasthyasamparkp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sagar Shimpi
 * Github - TheSeasApps
 */
public class UserBirthAttribute {

    public UserBirthAttribute() {
    }

    public UserBirthAttribute(String attributeType, String value) {
        this.attributeType = attributeType;
        this.value = value;
    }

    @SerializedName("attributeType")
    @Expose
    public String attributeType;
    @SerializedName("value")
    @Expose
    public String value;

    public String getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(String attributeType) {
        this.attributeType = attributeType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
