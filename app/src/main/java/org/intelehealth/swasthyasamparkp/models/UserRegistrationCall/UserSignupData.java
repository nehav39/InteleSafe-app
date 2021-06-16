package org.intelehealth.swasthyasamparkp.models.UserRegistrationCall;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar Shimpi
 *
 */
public class UserSignupData {

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("password")
    @Expose
    public String password;
    @SerializedName("person")
    @Expose
    public Person person;
    @SerializedName("systemId")
    @Expose
    public String systemId;

    @SerializedName("roles")
    @Expose
    public List<String> roles = null;

    @SerializedName("secretQuestion")
    @Expose
    public String secretQuestion;

    public UserSignupData() {
    }

    public UserSignupData(String name, String description, String username, String password, Person person, String systemId, List<String> roles, String secretQuestion) {
        this.name = name;
        this.description = description;
        this.username = username;
        this.password = password;
        this.person = person;
        this.systemId = systemId;
        this.roles = roles;
        this.secretQuestion = secretQuestion;
    }

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getSecretQuestion() {
        return secretQuestion;
    }

    public void setSecretQuestion(String secretQuestion) {
        this.secretQuestion = secretQuestion;
    }


}
