package musicgenie.com.musicgenie;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.format.DateFormat;
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
import java.util.ArrayList;
import java.util.Date;

import br.com.bemobi.medescope.Medescope;
import br.com.bemobi.medescope.callback.DownloadStatusCallback;

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
    private void initiate(){


        log("handler running "+isHandlerRunning);
        if(!isHandlerRunning){
            //isHandlerRunning = true;

            while (task_count > 0 && isConnected()){
                log("=========================cur loop . task_count "+task_count);
                    final  ArrayList<String> taskIDs = getDispatchTaskSequence();
//                    Thread t = new Thread(new Runnable() {
//                        @Override
//                        public void run() {
                            for (String taskID : taskIDs) {
                                dispatch(taskID);
                                removeDispatchTask(taskID);

                            }
                        }
//                    });
//                    t.start();
              //   }
            log("turning handler off");
            isHandlerRunning = false;
        }
    }

    /*
    * WID: get task details from s.pref. and start AsyncTask for download
    * */
    private void dispatch(String taskID){

        SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(context);
        String file_name = utils.getTaskTitle(taskID);
        String url = utils.getTaskUrl(taskID);
        log("dispatched : " + taskID);
        log("file ="+file_name);
        downloadViaLib(taskID,file_name,url);
       // new DownLoadFile(context,taskID,url,file_name).execute();
        task_count--;

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
        log(" dispatch pendings :: "+_tasks);
        task = new Segmentor().getParts(_tasks, '#');
        return task;
    }



    private int getTaskCount(){
        return getTaskSequence().size();
    }

    // adds task to shared preferences task queue
    public void addTask(String file_name, String _url){
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
        utils.setTaskUrl(taskID, _url);
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

        Medescope
                .getInstance(context)
                .enqueue(taskID,
                        url,
                        file.toString(),
                        file_name,
                        "{some:'samplejson'}"
                );

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
