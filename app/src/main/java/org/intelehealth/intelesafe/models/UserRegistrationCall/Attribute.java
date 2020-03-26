package org.intelehealth.intelesafe.models.UserRegistrationCall;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sagar Shimpi
 * Github - TheSeasApps
 */
public class Attribute {

    public Attribute() {
    }

    public Attribute(String attributeType, String value, String hydratedObject) {
        this.attributeType = attributeType;
        this.value = value;
        this.hydratedObject = hydratedObject;
    }

    @SerializedName("attributeType")
    @Expose
    public String attributeType;
    @SerializedName("value")
    @Expose
    public String value;
    @SerializedName("hydratedObject")
    @Expose
    public String hydratedObject;

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

    public String getHydratedObject() {
        return hydratedObject;
    }

    public void setHydratedObject(String hydratedObject) {
        this.hydratedObject = hydratedObject;
    }
}
