package org.intelehealth.swasthyasamparkp.activities.homeActivity;

/*
Created By: Prajwal Waingankar
Github: prajwalmw
*/

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.intelehealth.swasthyasamparkp.R;
import org.intelehealth.swasthyasamparkp.widget.materialprogressbar.CustomProgressDialog;

/**
 * Created By: Prajwal Waingankar
 * Github: prajwalmw
 */
public class Webview extends AppCompatActivity {

    private CustomProgressDialog customProgressDialog;
    private String webUrl, visitUuid, openMRS_id;

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
            if(bundle.containsKey("Base_Url")) {
                setTitle("Download Presscription");
                visitUuid = bundle.getString("visitUuid");
                openMRS_id = bundle.getString("openMRS_id");

                webUrl = bundle.getString("Base_Url") +
                        "preApi/i.jsp?v=" +
                        visitUuid + "&pid=" + openMRS_id;

                Log.d("vdownload", "URL: " + webUrl);
            //https://trainingss.intelehealth.org/
                // preApi/i.jsp?v=2ffcd907-a08f-469d-8273-595b99cc366c&pid=10FKT-4

                webView.loadUrl(webUrl);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url)
                    {
                        if(URLUtil.isNetworkUrl(url))
                        { return false; }
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        finish();
                        return true;
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        customProgressDialog.show();
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        customProgressDialog.dismiss();
                        webView.loadUrl("javascript:document.getElementById('skip_button').click()");
                      //  Log.d("webview", "webview: "+webView.)
                    }

                    @Override
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        super.onReceivedError(view, request, error);
                        customProgressDialog.dismiss();
                    }
                });
            }
        }
/*
        if(bundle != null) {
            if(bundle.containsKey("FAQ")) {
                setTitle("PPE reuse & Alternative");
                webUrl = "https://www.intelesafe.org/ppe-reuse-alternatives"; // FAQs url
            } else if(bundle.containsKey("PPE")) {
                setTitle("PPE Guidelines");
                webUrl = "https://www.intelesafe.org/ppe-infection-control"; // PPE url
            }
            else if(bundle.containsKey("FAQ2")) {
                setTitle("Request for PPE kits");
                webUrl = "https://www.intelesafe.org/ppe-help"; // Request for PPE kit url.
            }
            else if(bundle.containsKey("PPE2")) {
                setTitle("Use of Telemedicine");
                webUrl = "https://www.intelesafe.org/telemedicine"; // Use of Telemedicine url
            }
            else if(bundle.containsKey("HomeMental")) {
                String title = bundle.getString("HomeCardTitle");
                setTitle(title);
                webUrl = "https://www.intelesafe.org/mental-health";
            }
            else if(bundle.containsKey("VisitMental")) {
                String title = bundle.getString("VisitTitle");
                setTitle(title);
                webUrl = "https://www.intelesafe.org/mental-health";
            }
            webView.loadUrl(webUrl);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url)
                {
                    if(URLUtil.isNetworkUrl(url))
                    { return false; }
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(intent);
                        finish();
                    return true;
                }

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
*/
    }

}
