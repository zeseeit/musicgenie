package musicgenie.com.musicgenie;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.support.v7.widget.SearchView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    ProgressDialog progressDialog;
    ListView resultListView;
    SearchResultListAdapter adapter;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("MusicGenie");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.progress_dialog_msg));
        setSupportActionBar(toolbar);
        resultListView = (ListView) findViewById(R.id.listView);
        adapter = new SearchResultListAdapter(this);

        new App_Config(this).configureDevice();


    }

    private void fireSearch(String term) {
        progressDialog.show();

        String url = App_Config.SERVER_URL+"/search?q="+ URLEncoder.encode(term);
        //log("url "+url);
        StringRequest request = new StringRequest(Request.Method.GET,url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //log(" reponse rec "+response);
                //makeToast(response);
                parseSearchResults(response);
                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                log("Error While searching :" + volleyError);
            }
        });

        VolleyUtils.getInstance().addToRequestQueue(request, TAG, this);

    }

    private void makeSnake(String msg) {
        Snackbar.make(resultListView, msg, Snackbar.LENGTH_LONG).show();
    }

    private void makeToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void log(String _lg) {
        Log.e(TAG, _lg);
    }

    private void parseSearchResults(String response) {
        ArrayList<Song> songs = new ArrayList<>();
        // parse youtube results
        try {
            JSONArray results = new JSONArray(response);
            for (int i = 0; i < results.length(); i++) {

                songs.add(new Song( results.getJSONObject(i).getString("title"),
                                    results.getJSONObject(i).getString("length"),
                                    results.getJSONObject(i).getString("uploader"),
                                    results.getJSONObject(i).getString("thumb"),
                                    results.getJSONObject(i).getString("id"),
                                    results.getJSONObject(i).getString("time"),
                                    results.getJSONObject(i).getString("views")
                                    ));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
       // makeToast("loading "+songs.size()+" items");
        adapter.setSongs(songs);
        resultListView.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.


        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(SEARCH_SERVICE);

        SearchView searchView = null;

        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            Log.e("Main", "" + searchView);
            //searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //TODO: uncomment code here
                fireSearch(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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

}
