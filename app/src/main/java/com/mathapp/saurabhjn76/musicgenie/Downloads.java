package com.mathapp.saurabhjn76.musicgenie;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class Downloads extends AppCompatActivity{

    ProgressBar progressBar;
    TextView percentage;
    ImageButton pause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_downloads);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        percentage= (TextView) findViewById(R.id.percentageProgress);
        toolbar.setTitle("Downloads");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listView= (ListView) findViewById(R.id.downLoadedList);
        ArrayList<String> fileList= new ArrayList<>();

        File dir= new File(Environment.getExternalStorageDirectory()+"/musicgenie");
        File[] files= dir.listFiles();

        for(File path:files){
            Log.e("path",""+path);
            fileList.add(0,path.toString());
        }


        DownloadedListAdapter adapter= new DownloadedListAdapter(this,fileList);


        listView.setAdapter(adapter);

    }

}
