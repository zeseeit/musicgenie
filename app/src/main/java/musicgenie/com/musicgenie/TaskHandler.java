package musicgenie.com.musicgenie;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Ankit on 8/7/2016.
 */
public class TaskHandler {


    private static final String TAG = "TaskHandler";
    private static Context context;
    private static TaskHandler mInstance;


    public TaskHandler(Context context) {
        this.context = context;
    }

    public static TaskHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TaskHandler(context);
        }
        return mInstance;
    }

    public TaskHandler createNewTask(){
        SearchResultListAdapter.getInstance(context).setOnProgressUpdateListener(listener);
        return mInstance;
    }

    private SearchResultListAdapter.ProgressUpdateListener listener = new SearchResultListAdapter.ProgressUpdateListener() {
        @Override
        public void onProgressUpdate(String taskID, String progress) {
            log("taskID "+taskID +" progress "+ progress + " %");
            postUpdate(taskID,progress);
        }
    };
    
    // adds task to shared preferences task queue
    public String addTask(){
        Date d = new Date();
        String timeStamp = DateFormat.format("yyyyMMddhhmmss", d.getTime()).toString();
        log("adding task :[audTsk" + timeStamp + "]");

        SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(context);
        String tasks = utils.getTaskSequence();
        utils.setTasksSequence("audTsk"+timeStamp+"#"+tasks);
        log("reloaded task : "+ utils.getTaskSequence());

        return "audTsk"+timeStamp;
    }

    // broadcast progress throught intents
    public void postUpdate(String taskID , String progressPercentage){

        Intent intent = new Intent(App_Config.ACTION_PROGRESS_UPDATE_BROADCAST);
        intent.putExtra(App_Config.EXTRA_TASK_ID,taskID);
        intent.putExtra(App_Config.EXTRA_PROGRESS, progressPercentage);
        context.sendBroadcast(intent);

        if(Integer.valueOf(progressPercentage)==100){
            log("clearing task");
            clearTask(taskID);
        }
        // if progress %  = 100 remove the task from queue
    }

    // removes taskID from sharedPreferences string queue
    public void clearTask(String taskID){

        ArrayList<String> tids =new Segmentor().getParts(SharedPrefrenceUtils.getInstance(context).getTaskSequence(),'#');
        for (String tid :tids) {
                if(tid.equals(taskID)){
                    log("removing "+ taskID );
                    tids.remove(tid);
                }
        }
        // write back to spref
        writeToSharedPreferences(tids);
    }

    public void writeToSharedPreferences(ArrayList<String> taskIDs){
        String currStack="";
        for (String id : taskIDs) {
            currStack +="#"+id;
        }
        currStack = currStack.substring(0,currStack.length());
        log("writing back the tasks :"+currStack);
        SharedPrefrenceUtils.getInstance(context).setTasksSequence(currStack);
    }

    public void log(String msg){
        Log.d(TAG,msg);
    }

}
