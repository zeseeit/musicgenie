package com.mathapp.saurabhjn76.musicgenie;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity{
    ProgressDialog progressBar;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("MUSIC GENIE");
        getSupportActionBar().setIcon(android.R.drawable.ic_menu_search);



        listView= (ListView) findViewById(R.id.songListView);
        MusicListAdapter adapter= new MusicListAdapter(this);
        listView.setAdapter(adapter);
        String songPath="http://dl.enjoypur.vc/upload_file/5570/6757/PagalWorld%20-%20Bollywood%20Mp3%20Songs%202016/Sanam%20Re%20(2016)%20Mp3%20Songs/SANAM%20RE%20%28Official%20Remix%29%20DJ%20Chetas.mp3";

        adapter.addSongToList(new Song("Sanam Re", songPath, "ankit"));
        adapter.addSongToList(new Song("Sanam Re", songPath, "ankit"));
        adapter.addSongToList(new Song("Sanam Re", songPath, "ankit"));
        adapter.addSongToList(new Song("Sanam Re", songPath, "ankit"));
        adapter.addSongToList(new Song("Sanam Re",songPath,"ankit"));
        adapter.addSongToList(new Song("Sanam Re", songPath, "ankit"));
        adapter.addSongToList(new Song("Sanam Re",songPath,"ankit"));
        adapter.addSongToList(new Song("Sanam Re",songPath,"ankit"));
        adapter.addSongToList(new Song("Sanam Re",songPath,"ankit"));
        adapter.addSongToList(new Song("Sanam Re",songPath,"ankit"));
        adapter.addSongToList(new Song("Sanam Re",songPath,"ankit"));
        adapter.addSongToList(new Song("Sanam Re",songPath,"ankit"));

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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

    public void search(View view) {
        if(isConnectedToNet())
        refress();
        else
            Snackbar.make(listView,"No Connectivity !!! ",Snackbar.LENGTH_LONG).show();
    }

    public void refress(){
        String searchTerm=((EditText)findViewById(R.id.searchBox)).getText().toString();
        //TODO: take the search term and format it
        //show progressDialoge till it loads list
        // after it gets list add all to adapter


    }

    private void Search(final String term){

        //String testPath="http://www.listentoyoutube.com/process.php?url=https://www.youtube.com/watch?v=DS-raAyMxl4";

        StringRequest request= new StringRequest(Request.Method.GET,AppConfig.URL_YOUTUBE_LINK_FETCH, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Log.e("Length >>",""+s.length());
                Log.e("Home",">>"+s);
                Toast.makeText(HomeActivity.this,""+s,Toast.LENGTH_LONG).show();
                progressBar.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                    Log.e("Home",""+volleyError);
                    progressBar.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("search_query",term);
                return params;
            }

        };

        RequestManager.getInstance().addToRequestQueue(request,"ytd_req",this);

    }


    public boolean isConnectedToNet() {

        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo mobileData = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        final android.net.NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mobileData.isConnected()) {
            return true;
        } else if (wifi.isConnected()) {
            return true;
        }
        return false;
    }



}
