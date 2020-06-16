package org.intelehealth.intelesafe.activities.homeActivity;

/*
Created By: Prajwal Waingankar
Github: prajwalmw
*/

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.widget.materialprogressbar.CustomProgressDialog;

/**
 * Created By: Prajwal Waingankar
 * Github: prajwalmw
 */
public class Webview extends AppCompatActivity {

    private CustomProgressDialog customProgressDialog;
    private String webUrl;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_new);

        WebView webView = findViewById(R.id.webview);
        customProgressDialog = new CustomProgressDialog(Webview.this);
        //since we are showing dialog, we have to pass the Activity instance and not the Application instance.
        customProgressDialog.show();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            if(bundle.containsKey("FAQ")) {
                setTitle("FAQ's");
                webUrl = "https://www.intelehealth.org/ppe-faqs"; // FAQs url
            } else if(bundle.containsKey("PPE")) {
                setTitle("PPE Guidelines");
                webUrl = "https://www.intelehealth.org/ppe-guidelines"; // PPE url
            }
            else if(bundle.containsKey("FAQ2")) {
                setTitle("Request for PPE kits");
                webUrl = "https://www.intelesafe.org/ppe-help"; // Request for PPE kit url.
            }
            else if(bundle.containsKey("PPE2")) {
                setTitle("Use of Telemedicine");
                webUrl = "https://www.intelesafe.org/telemedicine"; // Use of Telemedicine url
            }
            webView.loadUrl(webUrl);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    customProgressDialog.show();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    customProgressDialog.dismiss();
                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    customProgressDialog.dismiss();
                }
            });
        }
    }
}
