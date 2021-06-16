package org.intelehealth.swasthyasamparkp.activities.appointments;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {
    int totalTabs;

    public TabAdapter(FragmentManager fm, int totalTabs) {
        super(fm);
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Fragment fragment = AppointmentsFragment.newInstance(true);
                return fragment;
            case 1:
                Fragment fragment1 = AppointmentsFragment.newInstance(false);
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