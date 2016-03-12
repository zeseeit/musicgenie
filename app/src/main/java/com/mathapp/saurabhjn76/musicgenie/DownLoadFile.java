package com.mathapp.saurabhjn76.musicgenie;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by saurabh on 12/3/16.
 */
public class DownLoadFile extends AsyncTask<String,Integer ,String > {

    @Override
    protected String doInBackground(String... params) {

        Log.e("download","in doinBack");
        int count;

        String songPath="http://dl.enjoypur.vc/upload_file/5570/6757/PagalWorld%20-%20Bollywood%20Mp3%20Songs%202016/Sanam%20Re%20(2016)%20Mp3%20Songs/SANAM%20RE%20%28Official%20Remix%29%20DJ%20Chetas.mp3";
        try {
            URL url=new URL(songPath);
            URLConnection connection= url.openConnection();

            connection.setReadTimeout(10000);
            connection.setConnectTimeout(20000);
            connection.connect();

            int fileLength=connection.getContentLength();

            File root= Environment.getExternalStorageDirectory();
            File dir=new File(root.getAbsolutePath()+"/musicgenie");
            if(dir.exists()==false){
                dir.mkdirs();
            }
         //   Log.e("dir:::",""+dir);
            File file=new File(dir,"song.mp3");

            // download file
            InputStream inputStream= new BufferedInputStream(url.openStream());
            OutputStream outputStream= new FileOutputStream(file);


            byte data[] = new byte[1024];
            long total=0;
            //Log.e("dd",""+inputStream.read());
            while((count=inputStream.read(data))!=-1){
                total+=count;
           //     Log.e(">>",""+data);
                publishProgress((int) total * 100 / fileLength);
                outputStream.write(data,0,count);
            }
            Log.e("Downloaded",": size:"+fileLength);
            outputStream.flush();
            outputStream.close();
            inputStream.close();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "done";
    }

    @Override
    protected void onPostExecute(String result){
        Log.e("downloaad:","done !!!");
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.e("downloaded",""+values.toString()+"%");
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPreExecute(){
        Log.e("download:","downloading.................");
    }

}
