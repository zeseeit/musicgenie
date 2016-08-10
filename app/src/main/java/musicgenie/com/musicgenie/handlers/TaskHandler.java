package musicgenie.com.musicgenie.handlers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Date;

import musicgenie.com.musicgenie.R;
import musicgenie.com.musicgenie.utilities.App_Config;
import musicgenie.com.musicgenie.interfaces.ConnectivityUtils;
import musicgenie.com.musicgenie.interfaces.DownloadCancelListener;
import musicgenie.com.musicgenie.interfaces.DownloadListener;
import musicgenie.com.musicgenie.utilities.Segmentor;
import musicgenie.com.musicgenie.utilities.SharedPrefrenceUtils;
import musicgenie.com.musicgenie.utilities.VolleyUtils;

/**
 * Created by Ankit on 8/7/2016.
 */
public class TaskHandler {

    private static final int TYPE_TASK_DOWNLOAD = 0;
    private static final int TYPE_TASK_DISPATCH = 1;
    private static final String TAG = "TaskHandler";
    private static Context context;
    private static TaskHandler mInstance;
    private boolean isHandlerRunning = false;
    private int task_count = 0;
    private ProgressDialog progressDialog;
    private String dwnd_url;

    public TaskHandler(Context context) {
        this.context = context;
    }

    public static TaskHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TaskHandler(context);
        }
        return mInstance;
    }

    /*
    * WID: loops over pending tasks and dipatches in one by one fashion
    * */
    public void initiate(){

        if(!isHandlerRunning){

            while (getDispatchTaskCount() >0 && isConnected()){

                isHandlerRunning = true;
                    final  ArrayList<String> taskIDs = getDispatchTaskSequence();

                            for (final String taskID : taskIDs) {

                                if(SharedPrefrenceUtils.getInstance(context).getCurrentDownloadsCount()<1) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dispatch(taskID);
                                        }
                                    }).start();
                                    removeDispatchTask(taskID);
                                }
                            }
                        }
        }else{
            log("Handler Already running , Task is enqued ");
        }
    }

    public void pauseHandler(){
        //TODO: check how to cancel activities on thread
    }
    /*
    * WID: get task details from s.pref. and start AsyncTask for download
    * */


    private void dispatch(final String taskID) {

        String v_id = SharedPrefrenceUtils.getInstance(context).getTaskVideoID(taskID);

        String url = App_Config.SERVER_URL + "/g/" + v_id;
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                log("got url resp " + response);
                handleResponse(response,taskID);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                log("Error While searching :" + volleyError);
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyUtils.getInstance().addToRequestQueue(request, TAG, context);

    }

    private void handleResponse(String response,String taskID) {

        String download_url = App_Config.SERVER_URL;
        int status = -1;
        try {
            JSONObject obj = new JSONObject(response);
            if (obj.getInt("status") == 0) {
                download_url += obj.getString("url");
                log("download url:" + download_url);


                SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(context);
                String file_name = utils.getTaskTitle(taskID);
                log("dispatched : " + taskID+"["+file_name+"]");

                DownloadListener listener = new DownloadListener() {
                    @Override
                    public void onError(String error) {
                        SharedPrefrenceUtils.getInstance(context).setCurrentDownloadCount(0);
                        //TODO: handle error during download
                    }

                    @Override
                    public void onDownloadStart() {
                        SharedPrefrenceUtils.getInstance(context).setCurrentDownloadCount(1);
                        log("download started");
//                    progressDialog.dismiss();
                    }

                    @Override
                    public void onDownloadFinish() {
                        SharedPrefrenceUtils.getInstance(context).setCurrentDownloadCount(0);
                    }
                };


                DownloadThread thread = new DownloadThread(taskID,download_url,file_name,listener);
                thread.start();
                try {
                    log("waiting thread to join");
                    isHandlerRunning = true;
                    thread.join();
                    isHandlerRunning = false;
                    //  last-round check up for any residue task taken-in in beetween
                    initiate();
                    log("thread joined !");

                } catch (InterruptedException e) {
                    log("thread join Interrupted");
                }

            } else {

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    *                  Task Helpers
    * */

    private ArrayList<String> getTaskSequence() {
        ArrayList<String> task;
        String _tasks = SharedPrefrenceUtils.getInstance(context).getTaskSequence();
        log("downloads pendings :: "+_tasks);
        task = new Segmentor().getParts(_tasks, '#');
        return task;
    }

    private ArrayList<String> getDispatchTaskSequence() {

        ArrayList<String> task;
        String _tasks = SharedPrefrenceUtils.getInstance(context).getDispatchTaskSequence();
        //log(" dispatch pendings :: "+_tasks);
        task = new Segmentor().getParts(_tasks, '#');
        return task;

    }

    public int getTaskCount(){
        return getTaskSequence().size();
    }

    public int getDispatchTaskCount(){
        return getDispatchTaskSequence().size();
    }

    // adds task to shared preferences task queue
    public void addTask(String file_name, String v_id){
        task_count++;
        SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(context);
        // create taskID
        Date d = new Date();
        String timeStamp = DateFormat.format("yyyyMMddhhmmss", d.getTime()).toString();
       // log("adding task :[audTsk" + timeStamp + "]");
        String taskID = "audTsk"+timeStamp;
        String tasks = utils.getTaskSequence();
        utils.setTasksSequence(tasks + taskID + "#");
        // task ready for dispatch
        utils.setDispatchTasksSequence(tasks + taskID + "#");
        //log("after adding " + utils.getTaskSequence());
        // save taskTitle:file_name
        utils.setTaskTitle(taskID, file_name);
        // save taskUrl  : _url
        utils.setTaskVideoID(taskID, v_id);
        //log("initiating proc...");
        // notifies handler for new task arrival
                initiate();
    }
    // removes taskID from sharedPreferences string queue
    public void removeTask(String taskID){

        ArrayList<String> tids =new Segmentor().getParts(SharedPrefrenceUtils.getInstance(context).getTaskSequence(), '#');
        for (int i =0;i<tids.size();i++) {
            String tid = tids.get(i);
                if(tid.equals(taskID)){
                    log("removing download task "+ taskID );
                    tids.remove(i);
                }
        }
        // write back to spref
        writeToSharedPreferences(tids,TYPE_TASK_DOWNLOAD);
        task_count--;
    }

    //remove dispatch task
    public void removeDispatchTask(String taskID){

        ArrayList<String> tids =new Segmentor().getParts(SharedPrefrenceUtils.getInstance(context).getDispatchTaskSequence(), '#');
        for (int i =0;i<tids.size();i++) {
            String tid = tids.get(i);
            if(tid.equals(taskID)){
                log("removing dispatch task "+ taskID );
                tids.remove(i);
            }
        }
        // write back to spref
        writeToSharedPreferences(tids,TYPE_TASK_DISPATCH);
        task_count--;
    }

    public void writeToSharedPreferences(ArrayList<String> taskIDs,int type){
        String currStack="";
        for (String id : taskIDs) {
            currStack +=id+"#";
        }
        currStack = currStack.substring(0,currStack.length());
        log("writing back the tasks :" + currStack);

        if(type==TYPE_TASK_DOWNLOAD)
        SharedPrefrenceUtils.getInstance(context).setTasksSequence(currStack);
        else
            SharedPrefrenceUtils.getInstance(context).setDispatchTasksSequence(currStack);
    }

    private boolean isConnected(){
        return ConnectivityUtils.getInstance(context).isConnectedToNet();
    }

    public void log(String msg) {
        Log.d(TAG, msg);
    }

    /*
    *   lib--downloader
    * */

    private void downloadViaLib(final String taskID,String file_name,String url){

        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/Musicgenie/Audio");
        File file = new File(dir, file_name.trim() + ".mp3");

    }

    /*
    *   Download Thread
    * */

    //WID: takes taskID , file_name , url and  download it , removes task after 100% , publishes progress

    private class DownloadThread extends Thread implements DownloadCancelListener {

        private String taskID;
        private String url;
        private String file_name;
        private DownloadListener downloadListener;
        private boolean isCanceled = false;

        //private Context context;

        public DownloadThread(String taskID , String url , String file_name,DownloadListener listener) {
            this.taskID = taskID;
            this.url = url;
            this.file_name = file_name;
            this.downloadListener = listener;
            //this.context = context;
        }

        @Override
        public void run() {
            int count;
            int fileLength;
            final String t_url = this.url;
            final String t_file_name = this.file_name;

            try {

                URL url = new URL(t_url);
                URLConnection connection = url.openConnection();
                connection.setReadTimeout(20000);
                connection.setConnectTimeout(20000);
                connection.connect();
                fileLength = connection.getContentLength();
                log("content len "+fileLength);
                File dir = new File(App_Config.FILES_DIR);
                File file = new File(dir, t_file_name.trim() + ".mp3");
                log("writing to "+file.getAbsolutePath().toString());

                InputStream inputStream = new BufferedInputStream(url.openStream());
                OutputStream outputStream = new FileOutputStream(file);
                byte data[] = new byte[1024];
                long total = 0;
                while (!isCanceled && (count = inputStream.read(data)) != -1) {

                    if(total==0)downloadListener.onDownloadStart();

                    total += count;
                    publishProgress((int) total * 100 / fileLength);
                    outputStream.write(data, 0, count);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();

            } catch (MalformedURLException e) {
                downloadListener.onError(e.toString());
                log("URL exception " + e);
            } catch (IOException e) {
                downloadListener.onError(e.toString());
                log("IO exception " + e);
            }

        }


        private void publishProgress(int progress){
            //  to reduce log lines
            if(progress%10==0)log(taskID+" done.."+ progress + " %");

            if(progress == 100){
                removeTask(taskID);
                downloadListener.onDownloadFinish();
                log("downloaded task " + taskID);
            }
            broadcastUpdate(String.valueOf(progress));
        }

        public void broadcastUpdate(String progressPercentage){
            Intent intent = new Intent(App_Config.ACTION_PROGRESS_UPDATE_BROADCAST);
            intent.putExtra(App_Config.EXTRA_TASK_ID,taskID);
            intent.putExtra(App_Config.EXTRA_PROGRESS, progressPercentage);
            context.sendBroadcast(intent);
        }

        @Override
        public void onDownloadCancel(String taskID) {
                this.isCanceled = true;
                log("task "+taskID +" got canceled !!");
        }
    }

    /*
    *       Downloade Async
    * */

    class DownLoadFile extends AsyncTask<String, Integer, String> {

        //// TODO: it needs some error broadcaster
        private Context context;
        private String songURL;
        private String filename;
        private String taskID;

        public DownLoadFile() {
        }

        public DownLoadFile(Context context,String taskID , String uri, String filename) {
            log("dnd ctxt "+context);
            this.context = context;
            this.songURL = uri;
            this.filename = filename;
            this.taskID = taskID;
        }

        @Override
        protected String doInBackground(String... params) {
            log("in doinBack");
            int count;
            int fileLength = 24;        // for debug purpo.
            //songPath="http://dl.enjoypur.vc/upload_file/5570/6757/PagalWorld%20-%20Bollywood%20Mp3%20Songs%202016/Sanam%20Re%20(2016)%20Mp3%20Songs/SANAM%20RE%20%28Official%20Remix%29%20DJ%20Chetas.mp3";
            try {
                URL url = new URL(songURL);
                    URLConnection connection = url.openConnection();
//                    connection.setReadTimeout(1000);
//                    connection.setConnectTimeout(1000);
                    connection.connect();
                    fileLength = connection.getContentLength();

                log("content len "+fileLength);
                File root = Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath() + "/Musicgenie/Audio");

                File file = new File(dir, filename.trim() + ".mp3");
                log("writing to "+file.getAbsolutePath().toString());
                // download file
                InputStream inputStream = new BufferedInputStream(url.openStream());
                OutputStream outputStream = new FileOutputStream(file);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = inputStream.read(data)) != -1) {
                    total += count;
                    publishProgress((int) total * 100 / fileLength);
                    outputStream.write(data, 0, count);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } catch (MalformedURLException e) {
                log("URL exception " + e);
            } catch (IOException e) {
                log("IO exception "+e);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            log("downloaded task "+taskID);
        }

        @Override
        protected void onProgressUpdate(final Integer... values) {
            if(values[0]%10==0)log(taskID+" done.."+ values[0] + " %");
            if(values[0] == 100){
                log("pre tasks seq "+SharedPrefrenceUtils.getInstance(context).getTaskSequence());
                removeTask(taskID);
                log("post tasks seq " + SharedPrefrenceUtils.getInstance(context).getTaskSequence());
            }
            broadcastUpdate(String.valueOf(values[0]));
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPreExecute() {
            log("starting download");
        }


        public void broadcastUpdate(String progressPercentage){
            Intent intent = new Intent(App_Config.ACTION_PROGRESS_UPDATE_BROADCAST);
            intent.putExtra(App_Config.EXTRA_TASK_ID,taskID);
            intent.putExtra(App_Config.EXTRA_PROGRESS, progressPercentage);
            context.sendBroadcast(intent);
        }

    }

}
