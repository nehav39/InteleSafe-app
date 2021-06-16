package org.intelehealth.swasthyasamparkp.models.UserRegistrationCall;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sagar Shimpi
 * Github - TheSeasApps
 */
public class Privilege {

    public Privilege() {
    }

    public Privilege(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("description")
    @Expose
    public String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
