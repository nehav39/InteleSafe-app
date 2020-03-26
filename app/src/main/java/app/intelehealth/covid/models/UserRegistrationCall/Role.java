package app.intelehealth.covid.models.UserRegistrationCall;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar Shimpi
 * Github - TheSeasApps
 */
public class Role {

    public Role() {
    }

    public Role(String name, String description, List<Privilege> privileges, List<Object> inheritedRoles) {
        this.name = name;
        this.description = description;
        this.privileges = privileges;
        this.inheritedRoles = inheritedRoles;
    }

    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("privileges")
    @Expose
    public List<Privilege> privileges = null;
    @SerializedName("inheritedRoles")
    @Expose
    public List<Object> inheritedRoles = null;

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

    public List<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
    }

    public List<Object> getInheritedRoles() {
        return inheritedRoles;
    }

    public void setInheritedRoles(List<Object> inheritedRoles) {
        this.inheritedRoles = inheritedRoles;
    }
}
