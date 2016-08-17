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
import android.speech.RecognizerIntent;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import musicgenie.com.musicgenie.fragments.NavigationFragment;
import musicgenie.com.musicgenie.handlers.TaskHandler;
import musicgenie.com.musicgenie.interfaces.TaskAddListener;
import musicgenie.com.musicgenie.notification.AlertDialogManager;
import musicgenie.com.musicgenie.utilities.App_Config;
import musicgenie.com.musicgenie.utilities.ConnectivityUtils;
import musicgenie.com.musicgenie.R;
import musicgenie.com.musicgenie.adapters.SearchResultListAdapter;
import musicgenie.com.musicgenie.utilities.Segmentor;
import musicgenie.com.musicgenie.utilities.SharedPrefrenceUtils;
import musicgenie.com.musicgenie.utilities.SoftInputManager;
import musicgenie.com.musicgenie.models.Song;
import musicgenie.com.musicgenie.utilities.VolleyUtils;

//TODO: add activity transition on swipe
public class MainActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    ListView resultListView;
    SearchResultListAdapter adapter;
    FloatingSearchView searchView = null;
    DrawerLayout mDrawerLayout;
    private static final String TAG = "MainActivity";
    private boolean mFloatingSearchViewSet;
    private FloatingActionButton fab;
    // private ConnectivityBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            //TODO: getParcelable arrayList and populate the result list
        }
        setContentView(R.layout.activity_home);
        resultListView = (ListView) findViewById(R.id.listView);
        new App_Config(this).configureDevice();
        //TODO:load trending items and during load , show progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Trending Songs...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (SharedPrefrenceUtils.getInstance(this).getOptionForTrendingAudio()) {
            loadTrendingSongs();
        }
        setSearchView();
        pinFAB();
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
    protected void onResume() {
        super.onResume();
        if(!mFloatingSearchViewSet)setSearchView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unsubscribeToTaskAddListener();
    }

    public void checkPendings(){
        SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(this);
        if(utils.getCurrentDownloadsCount()==0){ // check for on-going download process
            int pendingCount = new Segmentor().getParts(utils.getTaskSequence(),'#').size();
            if(pendingCount>0){
                AlertDialogManager.getInstance(this).popAlertForPendings(pendingCount);
            }
        }
    }

    public void setSearchView() {
        mFloatingSearchViewSet = true;
        searchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String s, String s1) {
                log("query changed from " + s + " to " + s1);
            }
        });


        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                log("suggestion clicked");
            }

            @Override
            public void onSearchAction(String s) {
                log("search action clicked");
                fireSearch(s);
            }
        });

        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                log("onFocusChange()");
            }
        });
        searchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id==R.id.action_settings){
                    Intent i = new Intent(MainActivity.this,UserPreferenceSetting.class);
                    startActivity(i);
                }
                if(id==R.id.action_downloads){
                    Intent i = new Intent(MainActivity.this,UserPreferenceSetting.class);
                    startActivity(i);
                }

            }
        });



    }

    public void AddSuggestionToSharedPreferences(String suggestion) {

        SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(this);
        String currStack = utils.getSuggestionList();
        currStack += suggestion + "#";
        currStack = currStack.substring(0, currStack.length());
        SharedPrefrenceUtils.getInstance(this).setSuggestionsList(currStack);

    }

    public ArrayList<String> getSuggestionList(){
        ArrayList<String> suggs;
        String _s = SharedPrefrenceUtils.getInstance(this).getSuggestionList();
        suggs = new Segmentor().getParts(_s, '#');
        return suggs;
    }

    private void fireSearch(String term) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.progress_dialog_msg));
        progressDialog.show();

        if (!ConnectivityUtils.getInstance(this).isConnectedToNet()) {

            getCurrentFocus().clearFocus();
            SoftInputManager.getInstance(this).hideKeyboard(searchView);
            progressDialog.dismiss();
            makeSnake("No Internet Connection !! ");
            return;
        }
        AddSuggestionToSharedPreferences(term);
        String url = App_Config.SERVER_URL + "/search?q=" + URLEncoder.encode(term);
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ;
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
                songs.add(new Song(results.getJSONObject(i).getString("title"),
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

        adapter = SearchResultListAdapter.getInstance(this);
        subscribeToTaskAddListener();
        adapter.setSongs(songs);
        resultListView.setAdapter(adapter);
    }

    private void loadTrendingSongs() {
        progressDialog.dismiss();


    }

    private void subscribeToTaskAddListener() {
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

    private void unsubscribeToTaskAddListener() {
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
