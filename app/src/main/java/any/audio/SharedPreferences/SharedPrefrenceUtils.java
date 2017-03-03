package any.audio.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import any.audio.Config.Constants;

import static any.audio.Centrals.CentralDataRepository.TYPE_TRENDING;

/**
 * Created by Ankit on 9/13/2016.
 */
public class SharedPrefrenceUtils {

    private static final String PREF_NAME = "anyaudio_tasks";
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
        return preferences.getBoolean(Constants.KEY_FIRST_PAGE_LOADED, false);
    }

    public void setFirstPageLoadedStatus(boolean firstPageLoadedStatus) {
        editor.putBoolean(Constants.KEY_FIRST_PAGE_LOADED, firstPageLoadedStatus);
        editor.commit();
    }

    public int getCurrentVersionCode() {
        return preferences.getInt(Constants.KEY_CURRENT_VERSION, 1);
    }

    public boolean getNewVersionAvailibility() {

        return preferences.getBoolean(Constants.KEY_NEW_APP_VERSION_AVAILABLE, false);

    }

    public void setNewVersionAvailibility(boolean status) {
        editor.putBoolean(Constants.KEY_NEW_APP_VERSION_AVAILABLE, status);
        editor.commit();
    }

    public String getNewVersionDescription() {

        return preferences.getString(Constants.KEY_NEW_APP_VERSION_DESCRIPTION, "");

    }

    public void setNewVersionDescription(String description) {
        editor.putString(Constants.KEY_NEW_APP_VERSION_DESCRIPTION, description);
        editor.commit();
    }

    public boolean donotRemindForAppUpdate() {

        return preferences.getBoolean(Constants.KEY_DONOT_REMIND_ME_AGAIN, false);

    }

    public void setDoNotRemindMeAgainForAppUpdate(boolean status) {
        editor.putBoolean(Constants.KEY_DONOT_REMIND_ME_AGAIN, status);
        editor.commit();
    }

    public void setNotifiedForUpdate(boolean state) {
        editor.putBoolean(Constants.KEY_APP_UPDATE_NOTIFIED, state);
        editor.commit();
    }

    public boolean getNotifiedForUpdate() {
        return preferences.getBoolean(Constants.KEY_APP_UPDATE_NOTIFIED, true);
    }

    public void setNewUpdateUrl(String url) {
        editor.putString(Constants.KEY_NEW_UPDATE_URL, url);
        editor.commit();
    }

    public String getNewUpdateUrl() {
        return preferences.getString(Constants.KEY_NEW_UPDATE_URL, "");
    }

    public int getLastLoadedType() {
        return preferences.getInt(Constants.KEY_LAST_LOADED_TYPE, TYPE_TRENDING);
    }

    public void setLastLoadedType(int type) {
        editor.putInt(Constants.KEY_LAST_LOADED_TYPE, type);
        editor.commit();
    }

    public boolean subscribedForUpdate() {
        return preferences.getBoolean(Constants.FIREBASE.KEY_UPDATE_SUBS, false);
    }

    public void setSubscribedForUpdate(boolean isSubscribed) {
        editor.putBoolean(Constants.FIREBASE.KEY_UPDATE_SUBS, isSubscribed);
        editor.commit();
    }

    public boolean subscribeForDefaults_get() {
        return preferences.getBoolean(Constants.FIREBASE.KEY_DEFAULT_SUBS, true);
    }

    public void subscribeForDefaults_set(boolean isSubscribed) {
        editor.putBoolean(Constants.FIREBASE.KEY_DEFAULT_SUBS, isSubscribed);
        editor.commit();
    }

    public void setOptionsForPushNotification(boolean shouldAllow) {
        editor.putBoolean("pushNotificationAllowance", shouldAllow);
        editor.commit();
    }

    public void setFirstSearchDone(boolean firstSearchDone) {
        editor.putBoolean("firstSearchDone", firstSearchDone);
        editor.commit();
    }

    public boolean isFirstSearchDone() {
        return preferences.getBoolean("firstSearchDone", false);
    }

    public boolean getOptionsForPushNotification() {
        return preferences.getBoolean("pushNotificationAllowance", true);
    }

    public void setOptionsForPushNotificationSound(boolean shouldAllow) {
        editor.putBoolean("pushSoundAllowance", shouldAllow);
        editor.commit();
    }

    public boolean getOptionsForPushNotificationSound() {
        return preferences.getBoolean("pushSoundAllowance", true);
    }

    public void setTermsAccepted(boolean accepted) {
        editor.putBoolean("terms_acceptance", accepted);
        editor.commit();
    }

    public boolean getTermsAccepted() {
        return preferences.getBoolean("terms_acceptance", false);
    }

    public void setFcmToken(String token) {
        editor.putString("fcmToken", token);
        editor.commit();
    }

    public String getFcmToken() {
        return preferences.getString("fcmToken", "");
    }

    public void setSongCardWidthDp(float screenWidthDP) {
        editor.putFloat("screenWidth", screenWidthDP);
        editor.commit();
    }

    public float getScreenWidthPx() {
        return preferences.getFloat("screenWidth", 0);
    }

    public void setCols(int cols) {
        editor.putInt("cols", cols);
        editor.commit();
    }

    public int getCols() {
        return preferences.getInt("cols", 2);
    }

    public void setAdWidth(float screenWidthDP) {
        editor.putFloat("adWidth", screenWidthDP);
        editor.commit();
    }

    public float getAdWidth() {
        return preferences.getFloat("adWidth", 0);
    }

    public String getPlaylistVideoId() {
        return preferences.getString("playlist", "");
    }

    public void setPlaylistVideoId(String playlistVideoIds) {
        editor.putString("playlist", playlistVideoIds);
        editor.commit();
    }

    public boolean isStreamUrlFetcherInProgress() {
        return preferences.getBoolean("fetcherAtWork", false);
    }

    public void setStreamUrlFetcherInProgress(boolean state) {

        editor.putBoolean("fetcherAtWork", state);
        editor.commit();

    }

    public String getNextStreamUrl() {
        return preferences.getString("upNextStreamUrl", "");
    }

    public void setNextStreamUrl(String url) {
        editor.putString("upNextStreamUrl", url);
        editor.commit();
    }

    public String getNextVId() {
        return preferences.getString("nvid", "");
    }

    public void setNextVId(String vId) {
        editor.putString("nvid", vId);
        editor.commit();
    }


    public String getNextStreamTitle() {
        return preferences.getString("nvt", "");
    }

    public void setNextStreamTitle(String title) {
        editor.putString("nvt", title);
        editor.commit();
    }

    public void resetPlaylistData() {
        setNextStreamUrl("");
        setNextStreamTitle("");
        setNextVId("");
    }

    public String getPlaylistVideoTitles() {
        return preferences.getString("vtitles", "");
    }

    public void setPlaylistVideoTitles(String titles) {
        editor.putString("vtitles", titles);
        editor.commit();
    }

    public String getPlaylistYoutubeId() {
        return preferences.getString("yids", "");
    }

    public void setPlaylistYoutubeId(String newIds) {
        editor.putString("yids", newIds);
        editor.commit();
    }

    public String getPlaylistUploaders() {
        return preferences.getString("uploaders", "");
    }

    public void setPlaylistUploaders(String uploaders) {
        editor.putString("uploaders", uploaders);
        editor.commit();
    }


    // Playlist Queue

    public void setQueueYoutubId(String queue) {

        editor.putString("queueyid", queue);
        editor.commit();

    }

    public String getQueueYoutubeIds() {
        return preferences.getString("queueyid", "");
    }

    public void setQueueVideoId(String queue) {

        editor.putString("queuevid", queue);
        editor.commit();

    }

    public String getQueueVideoId() {
        return preferences.getString("queuevid", "");
    }

    public void setQueueTitle(String queue) {

        editor.putString("queuetitle", queue);
        editor.commit();

    }

    public String getQueueTitles() {
        return preferences.getString("queuetitle", "");
    }

    public void setQueueUploaders(String queue) {

        editor.putString("queueuploaders", queue);
        editor.commit();

    }

    public String getQueueUploaders() {
        return preferences.getString("queueuploaders", "");
    }

    public void clearQueue() {

        editor.putString("queueuploaders", "");
        editor.putString("queuetitle", "");
        editor.putString("queuevid", "");
        editor.putString("queueyid", "");

        editor.commit();

    }

    public void setAutoPlayMode(boolean isAutoPlay) {

        Log.d("SwitchTest", " turning " + isAutoPlay);
        editor.putBoolean("autoPlay", isAutoPlay);
        editor.commit();

    }

    public boolean getAutoPlayMode() {

        Log.d("SwitchTest", " returning " + preferences.getBoolean("autoPlay", true));
        return preferences.getBoolean("autoPlay", true);

    }

    public String getRepeatMode() {
        return preferences.getString("repeatmode", Constants.MODE_REPEAT_NONE);
    }

    public void setRepeatMode(String mode) {
        editor.putString("repeatmode", mode);
        editor.commit();
    }

    public int getCurrentQueueIndex() {
        return preferences.getInt("currentQueueIndex", -1);
    }

    public void setCurrentQueueIndex(int currentQueueIndex) {
        editor.putInt("currentQueueIndex", currentQueueIndex);
        editor.commit();
    }

    // Last Item

    public void setLastItemStreamUrl(String url) {
        editor.putString("lastItemStream", url);
        editor.commit();

        Log.d("PrefCheck", "setting Last Url : " + url);
    }

    public String getLastItemStreamUrl() {
        Log.d("PrefCheck", " returning Last Url : " + preferences.getString("lastItemStream", ""));
        return preferences.getString("lastItemStream", "");
    }

    public void setLastItemThumbnailUrl(String url) {
        editor.putString("lastItemThumbnail", url);
        editor.commit();
    }

    public String getLastItemThumbnail() {
        return preferences.getString("lastItemThumbnail", "");
    }

    public void setLastItemTitle(String title) {
        editor.putString("lastItemTitle", title);
        editor.commit();
    }

    public String getLastItemTitle() {
        return preferences.getString("lastItemTitle", "");
    }

    public void setLastItemArtist(String artist) {
        editor.putString("lastItemArtist", artist);
        editor.commit();
    }

    public String getLastItemArtist() {
        return preferences.getString("lastItemArtist", "");
    }

    // Current Item

    public void setCurrentItemStreamUrl(String url) {
        editor.putString("currentItemStream", url);
        editor.commit();

        if (url.length() > 0) {
            setLastItemStreamUrl(url);
        }

    }

    public String getCurrentItemStreamUrl() {
        return preferences.getString("currentItemStream", "");
    }

    public void setCurrentItemThumbnailUrl(String url) {
        editor.putString("currentItemThumbnail", url);
        editor.commit();

        //only if there is new item
        if (url.length() > 0)
            setLastItemThumbnailUrl(url);

    }

    public String getCurrentItemThumbnail() {
        return preferences.getString("currentItemThumbnail", "");
    }

    public void setCurrentItemTitle(String title) {
        editor.putString("currentItemTitle", title);
        editor.commit();

        if (title.length() > 0)
            setLastItemTitle(title);

    }

    public String getCurrentItemTitle() {
        return preferences.getString("currentItemTitle", "");
    }

    public void setCurrentItemArtist(String artist) {
        editor.putString("currentItemArtist", artist);
        editor.commit();

        if (artist.length() > 0)
            setLastItemArtist(artist);

    }

    public String getCurrentItemArtist() {
        return preferences.getString("currentItemArtist", "");
    }

    // Player State

    public void setPlayerState(int state) {

        editor.putInt("AnyAudioPlayerState", state);
        editor.commit();

        if (state == Constants.PLAYER.PLAYER_STATE_STOPPED) {

            setCurrentItemTitle("");
            setCurrentItemThumbnailUrl("");
            setCurrentItemArtist("");

        }

    }

    public int getPlayerState() {
        return preferences.getInt("AnyAudioPlayerState", Constants.PLAYER.PLAYER_STATE_STOPPED);
    }


    //Notification Tray Player
    public void setStreamContentLength(String trackLen) {
        editor.putString("trackLen", trackLen);
        editor.commit();
    }

    public String getStreamContentLength() {
        return preferences.getString("trackLen", "00:00");
    }

    public void setStreamUrlFetchedStatus(boolean fetchedStatus) {

        editor.putBoolean("fetchedStatus", fetchedStatus);
        editor.commit();

    }

    public boolean getStreamUrlFetchedStatus() {
        return preferences.getBoolean("fetchedStatus", false);
    }

    public void setTaskThumbnail(String taskID, String thumbanil) {
        editor.putString("taskThumbnail" + taskID, thumbanil);
        editor.commit();
    }


    public void setTaskArtist(String taskID, String artist) {
        editor.putString("taskArtist" + taskID, artist);
        editor.commit();
    }

    public String getTaskThumbnail(String taskId) {
        return preferences.getString("taskThumbnail" + taskId, "");
    }

    public String getTaskArtist(String taskId) {
        return preferences.getString("taskArtist" + taskId, "");
    }

    public void setTaskStatus(String taskID, String stateWaiting) {
        editor.putString("taskStatus" + taskID, stateWaiting);
        editor.commit();
    }

    public String getTaskStatus(String taskId) {
        return preferences.getString("taskStatus" + taskId, Constants.DOWNLOAD.STATE_WAITING);
    }

    public void setCurrentOngoingTask(String taskID) {
        editor.putString("currentOngoingTask", taskID);
        editor.commit();
    }

    public String getCurrentOngoingTask() {
        return preferences.getString("currentOngoingTask", "");
    }

    public void setMetadataDuration(String fileName, String duration) {
        editor.putString("mtd" + fileName, duration);
        editor.commit();
    }

    public String getMetadataDuration(String fileName) {
        return preferences.getString("mtd" + fileName, "<Unknown>");
    }

    public void setMetadataArtist(String filename, String artist) {
        editor.putString("mtar" + filename, artist);
        editor.commit();
    }

    public String getMetadataArtist(String fileName) {
        return preferences.getString("mtar" + fileName, "<Unknown>");
    }

    public boolean isNotificationPlayerControlReceiverRegistered() {
        return preferences.getBoolean("npcbr", false);
    }

    public void setNotificationPlayerControlReceiverRegistered(boolean registered) {
        editor.putBoolean("npcbr", registered);
        editor.commit();
    }

    public void setNewVersionName(String newVersionName) {
        editor.putString("versionNm", newVersionName);
        editor.commit();
    }

    public String getLatestVersionName(){
        return preferences.getString("versionNm","");
    }

    public void setNewVersionCode(int newVersion) {

        editor.putInt("versionCd", newVersion);
        editor.commit();
    }

    public int getLatestVersionCode(){
        return preferences.getInt("versionCd",0);
    }

    public void newSearch(boolean equals) {
        Log.d("SearchTest"," wrting sp "+equals);
        editor.putBoolean("ns",equals);
        editor.commit();
    }

    public boolean isNewSearch(){
        return preferences.getBoolean("ns",false);
    }

    public int getSelectedNavIndex() {
        return preferences.getInt("selectedNav",0);
    }

    public void setSelectedNavIndex(int index){
        editor.putInt("selectedNav",index);
        editor.commit();
    }
}
