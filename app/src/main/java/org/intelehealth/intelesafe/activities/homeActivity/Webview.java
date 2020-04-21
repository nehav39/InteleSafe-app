package org.intelehealth.intelesafe.activities.homeActivity;

/*
Created By: Prajwal Waingankar
Github: prajwalmw
*/

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.widget.materialprogressbar.CustomProgressDialog;

public class Webview extends AppCompatActivity {
WebView webView;
CustomProgressDialog customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_new);

        webView = findViewById(R.id.webview);
        customProgressDialog = new CustomProgressDialog(Webview.this);
        //since we are showing dialog, we have to pass the Activity instance and not the Application instance.
        customProgressDialog.show();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            if(bundle.containsKey("FAQ"))
            {
                setTitle("FAQ's");
                webView.loadUrl("https://www.intelehealth.org/ppe-faqs"); // FAQs url
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

            if(bundle.containsKey("PPE"))
            {
                setTitle("PPE Guidelines");
                webView.loadUrl("https://www.intelehealth.org/ppe-guidelines"); // PPE url
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
}
