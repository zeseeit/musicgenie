package any.audio.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

import any.audio.Config.Constants;
import any.audio.Config.URLS;
import any.audio.Interfaces.DownloadItemInvalidatedListener;
import any.audio.Interfaces.DownloadListener;
import any.audio.Notification.LocalNotificationManager;
import any.audio.Network.ConnectivityUtils;
import any.audio.Managers.PermissionManager;
import any.audio.SharedPreferences.SharedPrefrenceUtils;

/**
 * Created by Ankit on 9/13/2016.
 */
public class TaskHandler {
    private static final int TYPE_TASK_DOWNLOAD = 0;
    private static final int TYPE_TASK_DISPATCH = 1;
    private static final String TAG = "TaskHandler";
    private static Context context;
    private static TaskHandler mInstance;
    private static Handler mHandler;
    private boolean isHandlerRunning = false;
    private int task_count = 0;
    private ProgressDialog progressDialog;
    public static boolean isCanceled = false;
    private String dwnd_url;
    private static final int SOCKET_CONNECT_TIMEOUT = 60 * 1000; // 1 min
    private DownloadItemInvalidatedListener itemInvalidatedListener;
    private SharedPrefrenceUtils utils;

    public TaskHandler(Context context) {
        TaskHandler.context = context;
        utils = SharedPrefrenceUtils.getInstance(context);
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
    public void initiate() {

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                L.m("TaskHandler", "Handling Msg On Main Thread");
                initiate();
            }
        };

        if (!isHandlerRunning) {

            if (getDispatchTaskCount() > 0 && isConnected()) {
                isHandlerRunning = true;
                final ArrayList<String> taskIDs = getDispatchTaskSequence();

                for (final String taskID : taskIDs) {
                    Log.d("TaskHandler", " Looping dispatch tasks.. length:[" + taskIDs.size() + "]");
                    if (utils.getCurrentDownloadsCount() < 1) {
                        Log.d("TaskHandler", " Current No Downloading..So Starting download thread");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
//                                log("succ: dispatched " + taskID);
                                L.m("TaskHandler", "dispatched Task [" + taskID + "]");
                                dispatch(taskID);

                                L.m("TaskHandler", "Sending Message For Last Round Check up");
                                // last round check up
                                Message message = mHandler.obtainMessage();
                                message.sendToTarget();

                            }
                        }).start();

                        removeDispatchTask(taskID);
                    } else {
                        Log.d("TaskHandler", " Download going on.........");
                    }
                }
            }
        } else {
            L.m("TaskHandler", "Task Handler Running.... Your Task is enqued");
        }
    }

    public void setItemInvalidatedListener(DownloadItemInvalidatedListener itemInvalidatedListener) {
        this.itemInvalidatedListener = itemInvalidatedListener;
    }

    public void pauseHandler() {
        //TODO: check how to cancel activities on thread
    }
    /*
    * WID: get task details from s.pref. and start AsyncTask for download
    * */

    private void dispatch(final String taskID) {

        String v_id = utils.getTaskVideoID(taskID);
        String file_name = utils.getTaskTitle(taskID);
        DownloadListener listener = new DownloadListener() {

            @Override
            public void onInterruptted(String taskID) {
                // retrieve the file and delete it
                deleteFile(taskID);
                String flnm = utils.getTaskTitle(taskID);

                utils.setTaskStatus(taskID, Constants.DOWNLOAD.STATE_STOPPED);
                broadcastStateUpdate(taskID, Constants.DOWNLOAD.STATE_STOPPED);
                utils.setCurrentOngoingTask("");
                utils.setCurrentDownloadCount(0);
                //LocalNotificationManager.getInstance(context).launchNotification("Failed To Download - " + flnm);
            }

            @Override
            public void onError(String taskID) {
                deleteFile(taskID);
                String flnm = utils.getTaskTitle(taskID);

                utils.setTaskStatus(taskID, Constants.DOWNLOAD.STATE_STOPPED);
                broadcastStateUpdate(taskID, Constants.DOWNLOAD.STATE_STOPPED);
                utils.setCurrentOngoingTask("");
                utils.setCurrentDownloadCount(0);
                // LocalNotificationManager.getInstance(context).launchNotification("Failed To Download - " + flnm);
            }

            @Override
            public void onDownloadTaskProcessStart() {
                utils.setCurrentDownloadCount(1);
                log("callback: download started");

            }

            @Override
            public void onDownloadFinish() {
                utils.setCurrentOngoingTask("");
                utils.setCurrentDownloadCount(0);
            }
        };
        setCancelled(false);
        DownloadThread thread = new DownloadThread(taskID, v_id, file_name, listener);
        thread.start();

        try {
            isHandlerRunning = true;
            thread.join();
            isHandlerRunning = false;
            L.m("TaskHandler", "Thread Joined");

        } catch (InterruptedException e) {
            log("thread join Interrupted");
        }
    }

    public void setCancelled(Boolean status) {
        this.isCanceled = status;
        L.m("TaskHandler", "isCancelled set to " + status);
    }

    private void deleteFile(String taskID) {

        utils.setCurrentDownloadCount(0);
        L.m("TaskHandler", "callback: download error");

        String file_to_delete = FileNameReformatter
                .getInstance(context)
                .getFormattedName(
                        SharedPrefrenceUtils
                                .getInstance(context)
                                .getTaskTitle(taskID)
                );

        File dest_file = new File(Constants.DOWNLOAD_FILE_DIR + "/" + file_to_delete + ".m4a");
        if (dest_file.exists()) {
            if (dest_file.delete()) {
                if (itemInvalidatedListener != null)
                    itemInvalidatedListener.onItemsInvalidated();
                L.m("TaskHandler", "Successfully Deleted File" + dest_file.getName());
            } else {
                L.m("TaskHandler", "Failed To Delete File " + dest_file.getName());
            }
        }
    }

    /*
    *                  Task Helpers
    * */

    public ArrayList<String> getTaskSequence() {
        ArrayList<String> task;
        String _tasks = utils.getTaskSequence();
        task = new Segmentor().getParts(_tasks, '#');
        return task;
    }

    private ArrayList<String> getDispatchTaskSequence() {

        ArrayList<String> task;
        String _tasks = utils.getDispatchTaskSequence();
        //log(" dispatch pendings :: "+_tasks);
        task = new Segmentor().getParts(_tasks, '#');
        return task;

    }

    public int getTaskCount() {
        return getTaskSequence().size();
    }

    public int getDispatchTaskCount() {
        return getDispatchTaskSequence().size();
    }

    // adds task to shared preferences task queue
    public void addTask(String file_name, String v_id, String thumbanil, String artist) {

        // create taskID
        Date d = new Date();
        String timeStamp = DateFormat.format("yyyyMMddhhmmss", d.getTime()).toString();
        String taskID = "audTsk" + timeStamp;

        // appending tasks
        String tasks = utils.getTaskSequence();
        utils.setTasksSequence(tasks + taskID + "#");
        tasks = utils.getDispatchTaskSequence();
        utils.setDispatchTasksSequence(tasks + taskID + "#");

        // adding new Title to SPref.
        utils.setTaskTitle(taskID, file_name);
        // adding new V_id to SPref.
        utils.setTaskVideoID(taskID, v_id);
        // adding new ThumbnailUrl to SPref.
        utils.setTaskThumbnail(taskID, thumbanil);
        //adding new Artist to SPref.
        utils.setTaskArtist(taskID, artist);
        // adding task Status as "Waiting"
        utils.setTaskStatus(taskID, Constants.DOWNLOAD.STATE_WAITING);
        // get-off and start task
        // notifies handler for new task arrival
        initiate();
    }

    // removes taskID from sharedPreferences string queue
    public void removeTask(String taskID) {

        ArrayList<String> tids = new Segmentor().getParts(utils.getTaskSequence(), '#');
        for (int i = 0; i < tids.size(); i++) {
            String tid = tids.get(i);
            if (tid.equals(taskID)) {
                log("removing download task " + taskID);
                tids.remove(i);
            }
        }
        // write back to spref
        writeToSharedPreferences(tids, TYPE_TASK_DOWNLOAD);

    }

    // remove all tasks
    public void removeAllTasks() {
        log("removing all tasks");
        utils.setTasksSequence("");
    }

    //remove dispatch task
    public void removeDispatchTask(String taskID) {

        ArrayList<String> tids = new Segmentor().getParts(utils.getDispatchTaskSequence(), '#');
        for (int i = 0; i < tids.size(); i++) {
            String tid = tids.get(i);
            if (tid.equals(taskID)) {
                log("removing dispatch task " + taskID);
                tids.remove(i);
            }
        }
        // write back to spref
        writeToSharedPreferences(tids, TYPE_TASK_DISPATCH);

    }

    // write string task sequence to SF

    public void writeToSharedPreferences(ArrayList<String> taskIDs, int type) {
        String currStack = "";
        for (String id : taskIDs) {
            currStack += id + "#";
        }
        currStack = currStack.substring(0, currStack.length());


        if (type == TYPE_TASK_DOWNLOAD) {
            log("writing back tasks :" + currStack);
            utils.setTasksSequence(currStack);
        } else {
            log("writing back the dispatch tasks :" + currStack);
            utils.setDispatchTasksSequence(currStack);
        }

    }

    // getConnectivity of Device
    private boolean isConnected() {
        return ConnectivityUtils.getInstance(context).isConnectedToNet();
    }

    // logs
    public void log(String msg) {
        Log.d(TAG, msg);
    }

    public void cancelCurrentDownload() {
        this.isCanceled = true;
        utils.setCurrentDownloadCount(0);
    }

    public void restartTask(String taskId) {

        // Failed Or Stopped Downloads
        // Set the tasks status to be waiting
        utils.setTaskStatus(taskId, Constants.DOWNLOAD.STATE_WAITING);
        broadcastStateUpdate(taskId, Constants.DOWNLOAD.STATE_WAITING);
        // Add task to Dispatch Queue and poke the handler
        Log.d("TaskHandler", " restarting Task " + taskId);
        String _tasks = utils.getDispatchTaskSequence();
        utils.setDispatchTasksSequence(_tasks + taskId + "#");
        initiate();

    }

    public void stopTask(String taskId) {
        // check if current task is ongoing
        // if yes then ->
        if (!utils.getTaskStatus(taskId).equals(Constants.DOWNLOAD.STATE_STOPPED)) {
            // Ongoing/Pending
            if (utils.getTaskStatus(taskId).equals(Constants.DOWNLOAD.STATE_DOWNLOADING)) {
                //Ongoing
                cancelCurrentDownload();
            } else {
                // Pending
                removeDispatchTask(taskId);
            }

            utils.setTaskStatus(taskId, Constants.DOWNLOAD.STATE_STOPPED);
            broadcastStateUpdate(taskId, Constants.DOWNLOAD.STATE_STOPPED);
        }


    }

    public void cancelTask(String taskId) {

        if (utils.getCurrentDownloadsCount() > 0) {
            //some download is going on---
            String currentOngoingTask = utils.getCurrentOngoingTask();

            //check if "task to remove" is the current Ongoing task
            if (taskId.equals(currentOngoingTask)) {
                // stop download and remove from task queue
                removeTask(taskId);
                cancelCurrentDownload();

            } else {
                // items are pending/failed
                // remove from queue
                removeTask(taskId);
            }
        } else {
            // all downloads are stopped/inturrupted
            // so - simply remove from queue
            removeTask(taskId);
        }


    }


    /*
    *   Download Thread
    * */

    //WID: takes taskID , file_name , url and  download it , removes task after 100% , publishes progress

    private class DownloadThread extends Thread {

        private String taskID;
        private String v_id;
        private String file_name;
        private DownloadListener downloadListener;

        private int currentProgress;
        private int fileLength;
        //private Context context;

        public DownloadThread(String taskID, String v_id, String file_name, DownloadListener listener) {
            this.taskID = taskID;
            this.v_id = v_id;
            this.file_name = file_name;
            this.downloadListener = listener;
            this.currentProgress = 0;
            utils.setCurrentOngoingTask(taskID);
            //  this.context = context;
        }

        @Override
        public void run() {
            int count;
            final String t_v_id = this.v_id;
            final String t_file_name = this.file_name;
            String t_url = URLS.URL_SERVER_ROOT;
            File dest_file = null;
            File dest_dir = null;
            subscribeDownloadCancelListener();

            if (!isCanceled) {

                L.m("TaskHandler", " isCancelled " + isCanceled);
                downloadListener.onDownloadTaskProcessStart();
                String _url = URLS.URL_SERVER_ROOT + "api/v1/g?url=" + t_v_id;
                L.m("TaskHandler", " Request For Download Url - on - " + _url);

                URL u = null;
                try {
                    u = new URL(_url);
                    URLConnection dconnection = null;
                    dconnection = u.openConnection();
                    dconnection.setReadTimeout(SOCKET_CONNECT_TIMEOUT);
                    dconnection.setConnectTimeout(SOCKET_CONNECT_TIMEOUT);
                    dconnection.connect();
                    StringBuilder result = new StringBuilder();
                    InputStream in = new BufferedInputStream(dconnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    JSONObject obj = new JSONObject(result.toString());
                    if (obj.getInt("status") == 200) {
                        t_url += obj.getString("url");
                        L.m("TaskHandler", "Download Url " + t_url);
                    } else {
                        downloadListener.onError(taskID);
                        return;
                    }

                } catch (JSONException | IOException e) {
                    downloadListener.onError(taskID);
                    e.printStackTrace();
                    return;
                }

                try {
                    URL url = new URL(t_url);
                    URLConnection connection = url.openConnection();
                    connection.setReadTimeout(SOCKET_CONNECT_TIMEOUT);
                    connection.setConnectTimeout(SOCKET_CONNECT_TIMEOUT);
                    connection.connect();
                    fileLength = connection.getContentLength();
                    L.m("TaskHandler", " Content Length " + fileLength);
                    if (fileLength == -1 || fileLength == 24 || fileLength==0) {
                        downloadListener.onError(taskID);
                        return;
                    }

                    utils.setTaskStatus(taskID, Constants.DOWNLOAD.STATE_DOWNLOADING);
                    broadcastStateUpdate(taskID, Constants.DOWNLOAD.STATE_DOWNLOADING);

                    // file creation
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        PermissionManager.getInstance(context).seek();
                    }

                    dest_dir = new File(Constants.DOWNLOAD_FILE_DIR);
                    String fileName = FileNameReformatter.getInstance(context).getFormattedName(t_file_name.trim());
                    dest_file = new File(dest_dir, fileName + ".m4a");
                    L.m("TaskHandler", "Writing to " + dest_file.toString());
                    InputStream inputStream = new BufferedInputStream(url.openStream());
                    OutputStream outputStream = new FileOutputStream(dest_file);
                    byte data[] = new byte[1024];
                    long total = 0;
                    int progressPercentage = 0;
                    int progressSent = 0;
                    while (!isCanceled && (count = inputStream.read(data)) != -1) {
                        total += count;
                        progressPercentage = ((int) total * 100 / fileLength);//, fileLength;

                        if (progressSent < progressPercentage && progressPercentage % 4 == 0) {
                            publishProgress(progressPercentage, String.valueOf(fileLength));
                            progressSent = progressPercentage;
                        }

                        outputStream.write(data, 0, count);
                    }

                    //check inturruption
                    if (total < fileLength) {
                        if (downloadListener != null) {
                            downloadListener.onInterruptted(taskID);
                            L.m("TaskHandler", "Download Interrupted");
                        }
                    }

                    outputStream.flush();
                    outputStream.close();
                    inputStream.close();

                } catch (MalformedURLException e) {
                    downloadListener.onError(taskID);
                    L.m("TaskHandler", " URL exception");
                } catch (IOException e) {
                    downloadListener.onError(taskID);
                    L.m("TaskHandler", " IO exception " + e.getMessage());
                }

            }
        }

        private void publishProgress(int progress, String cl) {

            if (progress == 100) {
                removeTask(taskID);
                downloadListener.onDownloadFinish();
//                log("downloaded task " + taskID);
                L.m("TaskHandler", "downloaded task " + taskID);
            }
            L.m("TaskHandler", "prog " + progress);

            broadcastProgressUpdate(taskID, String.valueOf(progress), cl);
            if (currentProgress < progress) {
                LocalNotificationManager.getInstance(context).publishProgressOnNotification(progress, file_name);
            }
            this.currentProgress = progress;
        }

        // broadcast is for progress update to download activity


        private void subscribeDownloadCancelListener() {
        }

    }

    public void broadcastProgressUpdate(String taskID, String progressPercentage, String contentLen) {

        Intent intent = new Intent(Constants.ACTION_DOWNLOAD_UPDATE);
        intent.putExtra(Constants.EXTRA_TASK_ID, taskID);
        intent.putExtra(Constants.EXTRAA_BROADCAST_TYPE, Constants.BROADCAST_TYPE_PROGRESS);
        intent.putExtra(Constants.EXTRA_PROGRESS, progressPercentage);
        intent.putExtra(Constants.EXTRA_CONTENT_SIZE, contentLen);
        context.sendBroadcast(intent);

    }

    public void broadcastStateUpdate(String taskID, String state) {

        Intent intent = new Intent(Constants.ACTION_DOWNLOAD_UPDATE);
        intent.putExtra(Constants.EXTRA_TASK_ID, taskID);
        intent.putExtra(Constants.EXTRAA_BROADCAST_TYPE, Constants.BROADCAST_TYPE_STATE);
        intent.putExtra(Constants.EXTRAA_DOWNLOAD_VIEW_STATE, state);
        context.sendBroadcast(intent);

    }
}
