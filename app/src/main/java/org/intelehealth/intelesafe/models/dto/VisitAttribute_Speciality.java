package org.intelehealth.intelesafe.models.dto;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created By: Prajwal Waingankar on 01-Jun-21
 * Github: prajwalmw
 * Email: prajwalwaingankar@gmail.com
 */

public class VisitAttribute_Speciality {
    //the keys used for SeriaizedName in this class will be shared by dev handling Openmrs.

    @SerializedName("attributeType")
    @Expose
    private String attributeType;

    @SerializedName("uuid")
    @Expose
    private String uuid;

    @SerializedName("value")
    @Expose
    private String value;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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
