package musicgenie.com.musicgenie.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import musicgenie.com.musicgenie.utilities.App_Config;
import musicgenie.com.musicgenie.R;
import musicgenie.com.musicgenie.adapters.SectionsPagerAdapter;
import musicgenie.com.musicgenie.utilities.SharedPrefrenceUtils;

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
        int tabIconColor = ContextCompat.getColor(DowloadsActivity.this, R.color.White);
        tabLayout.getTabAt(0).getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#ffffff"));
        tabLayout.setSelectedTabIndicatorHeight((int) (3 * getResources().getDisplayMetrics().density));
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
        toolbar.setTitle("Downloads");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        log("tasks >" + SharedPrefrenceUtils.getInstance(this).getTaskSequence());
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }



    @Override
    protected void onPause() {
        super.onPause();
        //unRegisterBroadcast();
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
