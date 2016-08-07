package musicgenie.com.musicgenie;

import android.content.Context;

/**
 * Created by Ankit on 8/7/2016.
 */
public class TaskHandler {

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

    // TODO: set of steps
    // what it does
    // on update call , it broadcast on intent with bundle(taskid, progress)
    // on task add call,  it updates shared pref with ordered task ids
    // on taskFinish call, it removes task id from shared pref and call interface method for task finish in order
    // to remove item from downloading fragment



}
