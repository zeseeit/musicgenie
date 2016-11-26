package any.audio.Config;

import android.os.Environment;

public class Constants {

    public static final String SERVER_URL = "http://ymp3.aavi.me";
    public static final String SDCARD = "sdcard";
    public static final String PHONE = "phone";
    public static final int SCREEN_MODE_TABLET = 0 ;
    public static final int SCREEN_MODE_MOBILE = 1;
    public static final String ACTION_DOWNLOAD_PROGRESS_UPDATE_BROADCAST = "action_progress_update";
    public static final String EXTRA_TASK_ID = "task_id";
    public static final String EXTRA_PROGRESS = "progress";
    public static final String FILES_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Musicgenie/Audio";
    public static final String ACTION_NETWORK_CONNECTED = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final int SCREEN_ORIENTATION_PORTRAIT = 0;
    public static final int SCREEN_ORIENTATION_LANDSCAPE = 1;
    public static final String EXTRA_CONTENT_SIZE = "contentSize";
    public static final String ACTION_STREAM_URL_FETCHED = "action_uri_fetched";
    public static final String EXTRAA_URI = "uri";
    public static final String EXTRAA_STREAM_FILE = "stream_file_name";
    public static final String EXTRAA_ACTIVITY_PRE_LOAD_FLAG = "actvity_preloaded";
    public static final String FLAG_STREAMING_CONTINUED = "streamingWillContinue";
    public static final int ACTION_TYPE_TRENDING = 0;
    public static final int ACTION_TYPE_RESUME = 1;
    public static final int ACTION_TYPE_REFRESS = 2;
    public static final int ACTION_TYPE_SEARCH = 3;
    public static final String KEY_SEARCH_TERM = "searchTerm";
    public static final String FLAG_RESET_ADAPTER_DATA = "reset_data_0x879SADF8dsfkdfjd";
    public static final String KEY_BUNDLE_FIRST_LOAD_DONE = "key_first_load_done";
    public static final String KEY_FIRST_PAGE_LOADED = "first_page_loaded";
    public static final int MESSAGE_STATUS_OK = 200;
    private static final String TAG = "AppConfig";
    public static final String FEATURE_STREAM = "stream";
    public static final String FEATURE_DOWNLOAD = "download";
    public static final int FLAG_STOP_MEDIA_PLAYER = 303;
    public static final int FLAG_CANCEL_STREAM = 304;
    public static final int FLAG_PASSING_HANDLER_REF = 101;
    public static final long FLAG_STREAM_END = -1;
    public static int FLAG_NEW_VERSION = 1;
    public static String KEY_CURRENT_VERSION = "currentVersion";
    public static String KEY_STREAMING_THUMB_URL = "streamingThumbUrl";
    public static String ACTION_STREAM_PROGRESS_UPDATE_BROADCAST = "streaming_update";
    public static String STREAM_PROGRESS_TIME = "stream_progress_time";
    public static String EXTRA_BUFFERED = "buffered_pos";
    public static String EXTRAA_STREAM_CONTENT_LEN = "stream_content_len";
    public static String EXTRAA_STREAM_BUFFERED_PROGRESS = "stream_buffered_extraa";
    public static String EXTRAA_STREAM_PROGRESS = "stream_progress_extraa";
    public static String STREAM_PREPARE_FAILED_URL_FLAG = "failed_url";
    public static String EXTRAA_FLAG_STREAM_WILL_CONTINUE = "streamin_will_continue";
}
