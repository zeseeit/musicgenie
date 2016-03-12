package com.mathapp.saurabhjn76.musicgenie;

import android.app.ProgressDialog;
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

public class HomeActivity extends AppCompatActivity implements ProgressUpdataListener {
    ProgressDialog progressBar;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);
      /*  progressBar= new ProgressDialog(this);
        progressBar.setIndeterminate(true);
        progressBar.setCancelable(false);*/
        listView= (ListView) findViewById(R.id.songListView);
        MusicListAdapter adapter= new MusicListAdapter(this);
        listView.setAdapter(adapter);
        String songPath="http://dl.enjoypur.vc/upload_file/5570/6757/PagalWorld%20-%20Bollywood%20Mp3%20Songs%202016/Sanam%20Re%20(2016)%20Mp3%20Songs/SANAM%20RE%20%28Official%20Remix%29%20DJ%20Chetas.mp3";

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
        String searchTerm=((EditText)findViewById(R.id.searchBox)).getText().toString();
     //  progressBar.show();
       // Search(searchTerm);

        new DownLoadFile().execute();

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


    @Override
    public void update(int value) {
        Log.e("prog",""+value);
//        new MusicListAdapter(this).setProgress(value);
    }
}
