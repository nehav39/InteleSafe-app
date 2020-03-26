package app.intelehealth.covid.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sagar Shimpi
 * Github - TheSeasApps
 */
public class GetDistrictRes {

    @SerializedName("results")
    @Expose
    public List<Result> results = null;

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public class Result {

        @SerializedName("uuid")
        @Expose
        public String uuid;
        @SerializedName("display")
        @Expose
        public String display;
        @SerializedName("links")
        @Expose
        public List<Link> links = null;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        public List<Link> getLinks() {
            return links;
        }

        public void setLinks(List<Link> links) {
            this.links = links;
        }
    }


    public class Link {

        @SerializedName("rel")
        @Expose
        public String rel;
        @SerializedName("uri")
        @Expose
        public String uri;

        public String getRel() {
            return rel;
        }

        public void setRel(String rel) {
            this.rel = rel;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }
    }
}
