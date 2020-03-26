package org.intelehealth.intelesafe.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar Shimpi
 * Github - TheSeasApps
 */
public class UserBirthData {

    public UserBirthData() {
    }

    public UserBirthData(Integer age, String gender, String birthdate, Boolean birthdateEstimated, List<UserBirthAttribute> attributes) {
        this.age = age;
        this.gender = gender;
        this.birthdate = birthdate;
        this.birthdateEstimated = birthdateEstimated;
        this.attributes = attributes;
    }

    @SerializedName("age")
    @Expose
    public Integer age;
    @SerializedName("gender")
    @Expose
    public String gender;
    @SerializedName("birthdate")
    @Expose
    public String birthdate;
    @SerializedName("birthdateEstimated")
    @Expose
    public Boolean birthdateEstimated;
    @SerializedName("attributes")
    @Expose
    public List<UserBirthAttribute> attributes = null;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public Boolean getBirthdateEstimated() {
        return birthdateEstimated;
    }

    public void setBirthdateEstimated(Boolean birthdateEstimated) {
        this.birthdateEstimated = birthdateEstimated;
    }

    public List<UserBirthAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<UserBirthAttribute> attributes) {
        this.attributes = attributes;
    }
}
