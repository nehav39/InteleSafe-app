package org.intelehealth.intelesafe.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar Shimpi
 */
public class GetOpenMRS {

    @SerializedName("identifiers")
    @Expose
    public List<String> identifiers = null;

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }
}
