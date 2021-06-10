package org.intelehealth.intelesafe.activities.pdflist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.intelehealth.intelesafe.R;
import org.intelehealth.intelesafe.utilities.SessionManager;
import org.intelehealth.intelesafe.widget.materialprogressbar.CustomProgressDialog;

import es.voghdev.pdfviewpager.library.PDFViewPager;
import es.voghdev.pdfviewpager.library.RemotePDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;
import es.voghdev.pdfviewpager.library.remote.DownloadFile;

public class PdfViewerActivity extends AppCompatActivity implements DownloadFile.Listener {
    private static final String EXTRA_URI = "EXTRA_URI";
    private static final String EXTRA_TITLE = "EXTRA_TITLE";

    private String uri, title;
    private PDFPagerAdapter adapter;
    private RemotePDFViewPager remotePDFViewPager;
    CustomProgressDialog customProgressDialog;
    private SessionManager sessionManager;

    public static void start(Context context, String uri, String title) {
        Intent starter = new Intent(context, PdfViewerActivity.class);
        starter.putExtra(EXTRA_URI, uri);
        starter.putExtra(EXTRA_TITLE, title);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        uri = getIntent().getStringExtra(EXTRA_URI);
        title = getIntent().getStringExtra(EXTRA_TITLE);
        if (TextUtils.isEmpty(uri))
            throw new IllegalArgumentException("uri is required");

        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewGroup container = findViewById(R.id.container);
        sessionManager = new SessionManager(this);
        String cachedPdfPath = sessionManager.getCachedPdfPath(uri);
        if (TextUtils.isEmpty(cachedPdfPath)) {
            remotePDFViewPager = new RemotePDFViewPager(this, uri, this);
            container.addView(remotePDFViewPager);
            customProgressDialog = new CustomProgressDialog(this);
            customProgressDialog.show();
        } else {
            PDFViewPager pdfViewPager = new PDFViewPager(this, cachedPdfPath);
            container.addView(pdfViewPager);
        }
    }

    @Override
    public void onSuccess(String url, String destinationPath) {
        sessionManager.cachePdfPath(url, destinationPath);
        customProgressDialog.dismiss();
        adapter = new PDFPagerAdapter(this, destinationPath);
        remotePDFViewPager.setAdapter(adapter);
    }

    @Override
    public void onFailure(Exception e) {
        customProgressDialog.dismiss();
        Toast.makeText(this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProgressUpdate(int progress, int total) {

    }
}