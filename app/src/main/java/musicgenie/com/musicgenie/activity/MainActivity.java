package musicgenie.com.musicgenie.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lapism.searchview.SearchView;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;

import musicgenie.com.musicgenie.fragments.NavigationFragment;
import musicgenie.com.musicgenie.handlers.TaskHandler;
import musicgenie.com.musicgenie.interfaces.TaskAddListener;
import musicgenie.com.musicgenie.utilities.App_Config;
import musicgenie.com.musicgenie.utilities.ConnectivityUtils;
import musicgenie.com.musicgenie.R;
import musicgenie.com.musicgenie.adapters.SearchResultListAdapter;
import musicgenie.com.musicgenie.utilities.SoftInputManager;
import musicgenie.com.musicgenie.models.Song;
import musicgenie.com.musicgenie.utilities.VolleyUtils;
//TODO: add activity transition on swipe
public class MainActivity extends Activity {

    ProgressDialog progressDialog;
    ListView resultListView;
    SearchResultListAdapter adapter;
    SearchView searchView = null;
    DrawerLayout mDrawerLayout;
    private static final String TAG = "MainActivity";
    private ActionBarDrawerToggle toggle;
    private Toolbar mToolbar;
    private FloatingActionButton fab;
    private ConnectivityBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceiver();

        if(!ConnectivityUtils.getInstance(this).isConnectedToNet()){
            setContentView(R.layout.conn_error_layout);
            return;
        }
        else {
                setContentView(R.layout.activity_home);
                resultListView = (ListView) findViewById(R.id.listView);
                new App_Config(this).configureDevice();
                //load trending items and during load , show progress dialog
                setUpDrawer();
                setSearchView();
                pinFAB();
                subscribeToTaskAddListener();
        }
    }

    public class ConnectivityBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(App_Config.ACTION_NETWORK_CONNECTED)) {
                if (ConnectivityUtils.getInstance(context).isConnectedToNet()) {
                    onCreate(null);
                }
            }
        }
    }

    private void registerReceiver() {
        receiver = new ConnectivityBroadcastReceiver();
        this.registerReceiver(receiver, new IntentFilter(App_Config.ACTION_NETWORK_CONNECTED));
    }

    private void pinFAB() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View view) {
                                       Intent intent = new Intent(MainActivity.this, DowloadsActivity.class);
                                       overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                       startActivity(intent);
                                   }
                               }
        );

    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else //noinspection StatementWithEmptyBody
            if (searchView != null && searchView.isSearchOpen()) { // TODO
                searchView.close(true);
            } else {
                super.onBackPressed();
            }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unsubscribeToTaskAddListener();
    }

    public void setUpDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        if (mDrawerLayout != null) {
            mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {

                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    invalidateOptionsMenu();
                    if (searchView != null && searchView.isSearchOpen()) {
                        searchView.close(true);
                    }

                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    invalidateOptionsMenu();
                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
        }

        toggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close);
        toggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(toggle);

//        mDrawerLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                toggle.syncState();
//            }
//        });

    }

    private void getToolbar() {

        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            if (mToolbar != null) {
                mToolbar.setNavigationContentDescription(getResources().getString(R.string.app_name));
               // setSupportActionBar(mToolbar);
            }
        }
    }

    public void setSearchView() {

        searchView = (SearchView) findViewById(R.id.searchView);
        if (searchView != null) {
            searchView.setVersion(SearchView.VERSION_TOOLBAR);
            searchView.setVersionMargins(SearchView.VERSION_MARGINS_TOOLBAR_BIG);
            searchView.setTextSize(16);
            searchView.setHint("Search");
            searchView.setDivider(false);
            searchView.setVoice(true);
            searchView.setAnimationDuration(SearchView.ANIMATION_DURATION);
            //searchView.setShadowColor(ContextCompat.getColor(this, R.color.search_shadow_layout));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.clearFocus();
                    searchView.close(true);
                    fireSearch(query);
                    // mSearchView.close(false);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
            searchView.setOnOpenCloseListener(new SearchView.OnOpenCloseListener() {
                @Override
                public void onOpen() {

                }

                @Override
                public void onClose() {

                }
            });

        }
    }

    private void fireSearch(String term) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.progress_dialog_msg));
        progressDialog.show();

        if(!ConnectivityUtils.getInstance(this).isConnectedToNet()){

            getCurrentFocus().clearFocus();
            SoftInputManager.getInstance(this).hideKeyboard(searchView);
            progressDialog.dismiss();
            makeSnake("No Internet Connection !! ");

            return;
        }

        String url = App_Config.SERVER_URL+"/search?q="+ URLEncoder.encode(term);
        StringRequest request = new StringRequest(Request.Method.GET,url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {;
                parseSearchResults(response);
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                log("[fireSearch()] Error While searching :" + volleyError);
            }
        });

        VolleyUtils.getInstance().addToRequestQueue(request, TAG, this);

    }

    private void parseSearchResults(String response) {
        ArrayList<Song> songs = new ArrayList<>();
        // parse youtube results
        try {
            JSONArray results = new JSONArray(response);
            for (int i = 0; i < results.length(); i++) {
                String enc_v_id = results.getJSONObject(i).getString("get_url").substring(3);
                songs.add(new Song( results.getJSONObject(i).getString("title"),
                        results.getJSONObject(i).getString("length"),
                        results.getJSONObject(i).getString("uploader"),
                        results.getJSONObject(i).getString("thumb"),
                        enc_v_id,
                        results.getJSONObject(i).getString("time"),
                        results.getJSONObject(i).getString("views")
                ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new SearchResultListAdapter(this);
        adapter.setSongs(songs);
        resultListView.setAdapter(adapter);
    }

    private void loadTrending(){

    }

    private void subscribeToTaskAddListener(){
        SearchResultListAdapter.getInstance(this).setOnTaskAddListener(new TaskAddListener() {
            @Override
            public void onTaskTapped() {
                log("callback: task tapped");
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Requesting Your Stuff..");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }

            @Override
            public void onTaskAddedToQueue(String task_info) {
                log("callback: task added to download queue");
                progressDialog.dismiss();
                makeToast(task_info + " Added To Download");
                //TODO: navigate to DownloadsActivity
            }
        });
    }

    private void unsubscribeToTaskAddListener(){
        SearchResultListAdapter.getInstance(this).setOnTaskAddListener(null);
    }

    private void makeSnake(String msg) {
        Snackbar.make(resultListView, msg, Snackbar.LENGTH_LONG).show();
    }

    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void log(String _lg) {
        Log.d(TAG, _lg);
    }

}
