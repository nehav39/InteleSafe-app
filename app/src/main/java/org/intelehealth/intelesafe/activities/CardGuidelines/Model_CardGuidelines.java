package org.intelehealth.intelesafe.activities.CardGuidelines;

/**
 * Created by Prajwal Waingankar
 * on 28-May-20.
 * Github: prajwalmw
 */


public class Model_CardGuidelines {
    private String title;
    private String description;

    public Model_CardGuidelines(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
