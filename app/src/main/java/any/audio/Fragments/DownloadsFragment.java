package any.audio.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import any.audio.Adapters.DownloadsFragmentPagerAdapter;
import any.audio.R;

/**
 * Created by Ankit on 2/22/2017.
 */

public class DownloadsFragment extends Fragment {

    Context context;
    ViewPager pager;
    DownloadsFragmentPagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_downloads, container, false);
        pager = (ViewPager) fragmentView.findViewById(R.id.pager);
        tabLayout = (TabLayout) fragmentView.findViewById(R.id.tabs);
        pagerAdapter = new DownloadsFragmentPagerAdapter(getFragmentManager(),context);
        pager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(pager);
        return fragmentView;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        pagerAdapter = null;
        System.gc();
    }

}
