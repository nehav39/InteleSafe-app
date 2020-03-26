package app.intelehealth.covid.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar Shimpi
 * Github - TheSeasApps
 */
public class UUIDResData {

    public UUIDResData() {
    }

    public UUIDResData(String person, List<IdentifierUUID> identifiers) {
        this.person = person;
        this.identifiers = identifiers;
    }

    @SerializedName("person")
    @Expose
    public String person;
    @SerializedName("identifiers")
    @Expose
    public List<IdentifierUUID> identifiers = null;

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public List<IdentifierUUID> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<IdentifierUUID> identifiers) {
        this.identifiers = identifiers;
    }
}
