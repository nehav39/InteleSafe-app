package org.intelehealth.intelesafe.activities.appointments;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {
    private Context myContext;
    int totalTabs;

    public TabAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Fragment fragment = new Fragment();
                return fragment;
            case 1:
                Fragment fragment1 = new Fragment();
                return fragment1;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}