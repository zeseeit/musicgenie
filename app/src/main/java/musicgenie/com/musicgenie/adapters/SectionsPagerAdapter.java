package musicgenie.com.musicgenie.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import musicgenie.com.musicgenie.fragments.ActiveTaskFragment;
import musicgenie.com.musicgenie.fragments.DownloadFragment;

/**
 * Created by Ankit on 8/9/2016.
 */
public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new ActiveTaskFragment();
        } else return new DownloadFragment();
    }

    @Override
    public int getCount() {
        return 2;
    }
}
