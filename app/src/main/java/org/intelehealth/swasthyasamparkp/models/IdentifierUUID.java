package org.intelehealth.swasthyasamparkp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sagar Shimpi
 * Github - TheSeasApps
 */
public class IdentifierUUID {

    @SerializedName("identifier")
    @Expose
    public String identifier;
    @SerializedName("identifierType")
    @Expose
    public String identifierType;
    @SerializedName("location")
    @Expose
    public String location;
    @SerializedName("preferred")
    @Expose
    public Boolean preferred;

    public IdentifierUUID(String identifier, String identifierType, String location, Boolean preferred) {
        this.identifier = identifier;
        this.identifierType = identifierType;
        this.location = location;
        this.preferred = preferred;
    }

    public IdentifierUUID() {
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getPreferred() {
        return preferred;
    }

    public void setPreferred(Boolean preferred) {
        this.preferred = preferred;
    }
}
