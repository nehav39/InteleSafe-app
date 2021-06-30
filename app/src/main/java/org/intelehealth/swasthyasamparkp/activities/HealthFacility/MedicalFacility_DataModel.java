package org.intelehealth.swasthyasamparkp.activities.HealthFacility;

/**
 * Created By: Prajwal Waingankar on 30-Jun-21
 * Github: prajwalmw
 * Email: prajwalwaingankar@gmail.com
 */

public class MedicalFacility_DataModel {
    private String title;
    private String address;
    private String phoneno;
    private String map_url;

    public MedicalFacility_DataModel(String title, String address, String phoneno, String map_url) {
        this.title = title;
        this.address = address;
        this.phoneno = phoneno;
        this.map_url = map_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public String getMap_url() {
        return map_url;
    }

    public void setMap_url(String map_url) {
        this.map_url = map_url;
    }
}
