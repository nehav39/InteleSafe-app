package org.intelehealth.swasthyasamparkp.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserProfileData {
    public UserProfileData() {
    }

    public UserProfileData(String person_id, String creator_id, String concept_id, String obs_value) {
        this.person_id = person_id;
        this.creator_id = creator_id;
        this.concept_id = concept_id;
        this.obs_value = obs_value;
    }

    @SerializedName("person_id")
    @Expose
    public String person_id;
    @SerializedName("creator_id")
    @Expose
    public String creator_id;
    @SerializedName("concept_id")
    @Expose
    public String concept_id;
    @SerializedName("obs_value")
    @Expose
    public String obs_value;
}
