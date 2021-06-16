package org.intelehealth.swasthyasamparkp.activities.pdflist;

public class PdfItem {
    public String title, url;
    public int placeholder;

    public PdfItem(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public PdfItem(String title, String url, int placeholder) {
        this(title, url);
        this.placeholder = placeholder;
    }
}
