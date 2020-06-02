package org.intelehealth.intelesafe.activities.CardGuidelines;

/**
 * Created by Prajwal Waingankar
 * on 28-May-20.
 * Github: prajwalmw
 */


public class Model_CardGuidelines {
    private String title,description, url;

    public Model_CardGuidelines(String title, String description, String url) {
        this.title = title;
        this.description = description;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
