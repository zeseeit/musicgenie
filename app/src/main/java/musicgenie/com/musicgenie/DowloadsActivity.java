package musicgenie.com.musicgenie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class DowloadsActivity extends AppCompatActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    ProgressUpdateBroadcastReceiver receiver;
    TabLayout tabLayout;
    Toolbar toolbar;


    //TODO: make active download page and downloaded items page
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dowload);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setIcon(android.R.drawable.stat_sys_download));
        tabLayout.addTab(tabLayout.newTab().setIcon(android.R.drawable.stat_sys_download_done));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        int tabIconColor = ContextCompat.getColor(DowloadsActivity.this, R.color.AccentColor);
        tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ff4081"));
        tabLayout.setSelectedTabIndicatorHeight((int) (4 * getResources().getDisplayMetrics().density));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                int tabIconColor = ContextCompat.getColor(DowloadsActivity.this, R.color.AccentColor);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIconColor = ContextCompat.getColor(DowloadsActivity.this, R.color.TabUnselectionColor);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Downlaods");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dowload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new ActiveDownloadsFragment();
            } else return new DownloadedFragment();
        }

        @Override
        public int getCount() {
            return 2;
        }


        public class DownloadedFragment extends Fragment {

            public DownloadedFragment() {
            }

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                View rootView = inflater.inflate(R.layout.fragment_dowloaded, container, false);
                return rootView;
            }
        }


        public class ActiveDownloadsFragment extends Fragment {

            public ActiveDownloadsFragment() {
            }

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                View rootView = inflater.inflate(R.layout.fragment_active_downloads, container, false);
                //register
                registerForBroadcastListen();
                return rootView;
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterBroadcast();
    }



    public class ProgressUpdateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            log("update via br "+intent.getStringExtra(App_Config.EXTRA_PROGRESS));
            //TODO: get intent extras of progress and task id and update the list view accordingly
        }
    }

    private void registerForBroadcastListen() {
        receiver = new ProgressUpdateBroadcastReceiver();
        DowloadsActivity.this.registerReceiver(receiver, new IntentFilter(App_Config.ACTION_PROGRESS_UPDATE_BROADCAST));
    }

    private void unRegisterBroadcast() {
        DowloadsActivity.this.unregisterReceiver(receiver);
    }

    public void log(String msg){
        Log.d("DownloadsActivity",msg);
    }


}
