package org.intelehealth.swasthyasamparkp.models.UserRegistrationCall;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar Shimpi
 * Github - TheSeasApps
 */
public class Person {

    public Person() {
    }

    public Person(List<Name> names, String gender, Integer age, String birthdate, Boolean birthdateEstimated, Boolean dead, String deathDate, String causeOfDeath, List<Address> addresses, List<Attribute> attributes, String deathdateEstimated, String birthtime) {
        this.names = names;
        this.gender = gender;
        this.age = age;
        this.birthdate = birthdate;
        this.birthdateEstimated = birthdateEstimated;
        this.dead = dead;
        this.deathDate = deathDate;
        this.causeOfDeath = causeOfDeath;
        this.addresses = addresses;
        this.attributes = attributes;
        this.deathdateEstimated = deathdateEstimated;
        this.birthtime = birthtime;
    }

    @SerializedName("names")
    @Expose
    public List<Name> names = null;
    @SerializedName("gender")
    @Expose
    public String gender;
    @SerializedName("age")
    @Expose
    public Integer age;
    @SerializedName("birthdate")
    @Expose
    public String birthdate;
    @SerializedName("birthdateEstimated")
    @Expose
    public Boolean birthdateEstimated;
    @SerializedName("dead")
    @Expose
    public Boolean dead;
    @SerializedName("deathDate")
    @Expose
    public String deathDate;
    @SerializedName("causeOfDeath")
    @Expose
    public String causeOfDeath;
    @SerializedName("addresses")
    @Expose
    public List<Address> addresses = null;
    @SerializedName("attributes")
    @Expose
    public List<Attribute> attributes = null;
    @SerializedName("deathdateEstimated")
    @Expose
    public String deathdateEstimated;
    @SerializedName("birthtime")
    @Expose
    public String birthtime;

    public List<Name> getNames() {
        return names;
    }

    public void setNames(List<Name> names) {
        this.names = names;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
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

    public Boolean getDead() {
        return dead;
    }

    public void setDead(Boolean dead) {
        this.dead = dead;
    }

    public String getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }

    public String getCauseOfDeath() {
        return causeOfDeath;
    }

    public void setCauseOfDeath(String causeOfDeath) {
        this.causeOfDeath = causeOfDeath;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public String getDeathdateEstimated() {
        return deathdateEstimated;
    }

    public void setDeathdateEstimated(String deathdateEstimated) {
        this.deathdateEstimated = deathdateEstimated;
    }

    public String getBirthtime() {
        return birthtime;
    }

    public void setBirthtime(String birthtime) {
        this.birthtime = birthtime;
    }

}
