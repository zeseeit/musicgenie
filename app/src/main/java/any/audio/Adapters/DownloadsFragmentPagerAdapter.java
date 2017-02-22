package any.audio.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import any.audio.Fragments.DownloadedFragment;
import any.audio.Fragments.DownloadingFragment;

/**
 * Created by Ankit on 2/22/2017.
 */

public class DownloadsFragmentPagerAdapter extends FragmentPagerAdapter {

    Context mContext;

    public DownloadsFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        // Create fragment object
        Fragment fragment = null;

        if(position==0){
            fragment = new DownloadingFragment();
        }else {
            fragment = new DownloadedFragment();
        }

        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String title = (position==0)?"DOWNLOADING":"DOWNLOADED";
        return title;
    }

}
