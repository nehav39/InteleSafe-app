package org.intelehealth.swasthyasamparkp.activities.introActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.intelehealth.swasthyasamparkp.R;
import org.intelehealth.swasthyasamparkp.activities.loginActivity.LoginActivity;
import org.intelehealth.swasthyasamparkp.utilities.SessionManager;

import java.util.Locale;

public class IntroActivity extends AppCompatActivity {

    Context context;

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    SessionManager sessionManager = null;
    String appLanguage;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        sessionManager = new SessionManager(IntroActivity.this);
        appLanguage = sessionManager.getAppLanguage();
        if (!appLanguage.equalsIgnoreCase("")) {
            setLocale(appLanguage);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        context = IntroActivity.this;

//        // Making notification bar transparent
//        if (Build.VERSION.SDK_INT >= 21) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);


        // layouts of all welcome sliders
        // add few more layouts if you want
//        layouts = new int[]{
//                R.layout.welcome_slide1,
//                R.layout.welcome_slide2,
//                R.layout.welcome_slide3,
//        };

        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.intro_slide2
        };

        // adding bottom dots
        addBottomDots(1);

        // making notification bar transparent
//        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {

                    launchHomeScreen();

                }
            }
        });

        btnSkip.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setLocale(String appLanguage) {
        Locale locale = new Locale(appLanguage);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.font_black_3));
            dotsLayout.addView(dots[i]);
        }

        //Changed color of Dot
        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.white));
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {

        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        finish();

    }

    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.transparent_tab));
        }
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private TextView tvIntroOne;
        private TextView tvIntroTwo;
        private TextView tvIntroThree;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            if (position == 0) {
                tvIntroOne = view.findViewById(R.id.tv_intro_one);
                tvIntroTwo = view.findViewById(R.id.tv_intro_two);
                tvIntroThree = view.findViewById(R.id.tv_intro_three);

//            //Highlighting Text
                String introOne = getString(R.string.intro1_text1);
//            String textToHighlightOne = getString(R.string.authentic_information);
//            String newString = introOne.replaceAll(textToHighlightOne, "<font color='blue'>" + textToHighlightOne + "</font>");
                tvIntroOne.setText(Html.fromHtml(introOne));

                String introTwo = getString(R.string.intro1_text2);
                String textToHighlightTwo = getString(R.string.swasth_sampark);
                String newStringTwo = introTwo.replaceAll(textToHighlightTwo, "<font color='blue'>" + textToHighlightTwo + "</font>");
                tvIntroTwo.setText(Html.fromHtml(newStringTwo));

                String introThree = getString(R.string.intro1_text3);
//            String textToHighlightOne = getString(R.string.authentic_information);
//            String newString = introOne.replaceAll(textToHighlightOne, "<font color='blue'>" + textToHighlightOne + "</font>");
                tvIntroThree.setText(Html.fromHtml(introThree));

            } else if (position == 1) {
                tvIntroOne = view.findViewById(R.id.tv_intro_one);
                tvIntroTwo = view.findViewById(R.id.tv_intro_two);

                String introTwo = getString(R.string.intro2_text1);
                String textToHighlightTwo = getString(R.string.swasth_sampark);
                String newStringTwo = introTwo.replaceAll(textToHighlightTwo, "<font color='blue'>" + textToHighlightTwo + "</font>");
                tvIntroOne.setText(Html.fromHtml(newStringTwo));

                String introThree = getString(R.string.intro2_text2);
//            String textToHighlightOne = getString(R.string.authentic_information);
//            String newString = introOne.replaceAll(textToHighlightOne, "<font color='blue'>" + textToHighlightOne + "</font>");
                tvIntroTwo.setText(Html.fromHtml(introThree));

            }

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
