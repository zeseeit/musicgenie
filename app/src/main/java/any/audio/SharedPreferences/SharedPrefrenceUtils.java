package any.audio.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import any.audio.Config.Constants;

/**
 * Created by Ankit on 9/13/2016.
 */
public class SharedPrefrenceUtils {
    private static final String PREF_NAME = "musicgenie_tasks";
    private static SharedPrefrenceUtils mInstance;
    private static int MODE = 0;
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private boolean firstPageLoadedStatus;


    public SharedPrefrenceUtils(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences(PREF_NAME, MODE);
        editor = preferences.edit();
    }

    public static SharedPrefrenceUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefrenceUtils(context);
        }
        return mInstance;
    }

    public void setTasksSequence(String sequence) {
        editor.putString("task_seq", sequence);
        editor.commit();
    }

    public String getTaskSequence() {
        //Log.d("SF",preferences.getString("task_seq",""));
        return preferences.getString("task_seq", "");
    }

    public void setDispatchTasksSequence(String sequence) {
        editor.putString("dis_task_seq", sequence);
        editor.commit();
    }

    public String getDispatchTaskSequence() {
        //Log.d("SF-d", preferences.getString("dis_task_seq", ""));
        return preferences.getString("dis_task_seq", "");
    }

    public void setTaskVideoID(String taskID, String v_id) {
        // taskID : key and download_url : value
        editor.putString(taskID + "_u", v_id);
        editor.commit();
    }

    public String getTaskVideoID(String taskID) {
        return preferences.getString(taskID + "_u", "");
    }

    public void setTaskTitle(String taskId, String file_name) {
        editor.putString(taskId + "_t", file_name);
        editor.commit();
    }

    public String getTaskTitle(String taskID) {
        return preferences.getString(taskID + "_t", "");
    }

    public int getCurrentDownloadsCount() {
        return preferences.getInt("cur_dnd", 0);
    }

    public void setCurrentDownloadCount(int count) {
        editor.putInt("cur_dnd", count);
        editor.commit();
    }


    public void setActiveFragmentAttachedState(boolean yesOrNo) {
        editor.putBoolean("isActive", yesOrNo);
        editor.commit();
    }

    public boolean getOptionsForThumbnailLoad() {
        return preferences.getBoolean("needThumb", true);
    }

    public void setOptionsForThumbnailLoad(boolean needLoading) {
        editor.putBoolean("needThumb", needLoading);
        editor.commit();
    }

    public String getCurrentStreamingItem() {
        return preferences.getString("streaming", "");
    }

    public String getLastSearchTerm() {
        return preferences.getString(Constants.KEY_SEARCH_TERM, "");
    }

    public void setLastSearchTerm(String term) {
        editor.putString(Constants.KEY_SEARCH_TERM, term);
        editor.commit();
    }

    public boolean getFirstPageLoadedStatus() {
        return preferences.getBoolean(Constants.KEY_FIRST_PAGE_LOADED,false);
    }

    public void setFirstPageLoadedStatus(boolean firstPageLoadedStatus) {
        editor.putBoolean(Constants.KEY_FIRST_PAGE_LOADED,firstPageLoadedStatus);
        editor.commit();
    }

    public int getCurrentVersionCode(){
        return preferences.getInt(Constants.KEY_CURRENT_VERSION,1);
    }

    public boolean getNewVersionAvailibility(){

        return preferences.getBoolean(Constants.KEY_NEW_APP_VERSION_AVAILABLE,false);

    }

    public void setNewVersionAvailibility(boolean status) {
        editor.putBoolean(Constants.KEY_NEW_APP_VERSION_AVAILABLE, status);
        editor.commit();
    }

    public String getNewVersionDescription(){

        return preferences.getString(Constants.KEY_NEW_APP_VERSION_DESCRIPTION,"");

    }

    public void setNewVersionDescription(String description) {
        editor.putString(Constants.KEY_NEW_APP_VERSION_DESCRIPTION, description);
        editor.commit();
    }

    public boolean getDoNotRemindMeAgainForAppUpdate(){

        return preferences.getBoolean(Constants.KEY_DONOT_REMIND_ME_AGAIN,false);

    }

    public void setDoNotRemindMeAgainForAppUpdate(boolean status) {
        editor.putBoolean(Constants.KEY_DONOT_REMIND_ME_AGAIN, status);
        editor.commit();
    }

    public void setNotifiedForUpdate(boolean state){
        editor.putBoolean(Constants.KEY_APP_UPDATE_NOTIFIED,state);
        editor.commit();
    }

    public boolean getNotifiedForUpdate(){
        return preferences.getBoolean(Constants.KEY_APP_UPDATE_NOTIFIED,true);
    }

}
