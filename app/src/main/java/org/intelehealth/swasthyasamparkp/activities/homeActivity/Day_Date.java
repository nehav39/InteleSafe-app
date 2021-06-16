package org.intelehealth.swasthyasamparkp.activities.homeActivity;

public class Day_Date {

    private String Day, Date, note;
    public boolean hasPrescription;
    public String visitUid, physicalExamValue, currentComplaintValue, openmrsId;;

    public Day_Date(String day, String date) {
        Day = day;
        Date = date;
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        Day = day;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }


    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
