package org.intelehealth.intelesafe.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendOtp {
    @SerializedName("totalCount")
    @Expose
    public String totalCount;
}
