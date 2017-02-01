package any.audio.Config;

import android.os.Environment;

public class Constants {

    public static final String SERVER_URL = "http://anyaudio.in";
    public static final String TERM_OF_USE_URL = "http://anyaudio.in/terms-of-use";
    public static final String SDCARD = "sdcard";
    public static final String PHONE = "phone";
    public static final int SCREEN_MODE_TABLET = 0;
    public static final int SCREEN_MODE_MOBILE = 1;
    public static final String ACTION_DOWNLOAD_PROGRESS_UPDATE_BROADCAST = "action_progress_update";
    public static final String EXTRA_TASK_ID = "task_id";
    public static final String EXTRA_PROGRESS = "progress";
    //    public static final String FILES_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()+"/AnyAudio/YourSongs";
    public static final String DOWNLOAD_FILE_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/AnyAudio";
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
    public static final String FEATURE_STREAM = "stream";
    public static final String FEATURE_DOWNLOAD = "download";
    public static final int FLAG_STOP_MEDIA_PLAYER = 303;
    public static final int FLAG_CANCEL_STREAM = 304;
    public static final int FLAG_PASSING_HANDLER_REF = 101;
    public static final long FLAG_STREAM_END = -1;
    private static final String TAG = "AppConfig";
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
    public static String EXTRAA_FLAG_DOWNLOAD_STATE = "healthy_download_status";
    public static String KEY_NEW_APP_VERSION_AVAILABLE = "new_version_available";
    public static String KEY_NEW_APP_VERSION_DESCRIPTION = "new_app_version_desc";
    public static String KEY_DONOT_REMIND_ME_AGAIN = "donot_remind_me_again_for_update";
    public static String EXTRAA_NEW_UPDATE_DESC = "extraa_new_update_des";
    public static String KEY_APP_UPDATE_NOTIFIED = "app_update_notified";
    public static String KEY_NEW_UPDATE_URL = "new_update_url";
    public static java.lang.String EXTRAA_STREAM_TITLE = "stream_title";
    public static java.lang.String EXTRAA_STREAM_THUMBNAIL_URL = "stream_thumbnail_url";
    public static String KEY_LAST_LOADED_TYPE = "last_loaded_type";
    public static String KEY_STREAM_CONTENT_LEN = "stream_content_len";
    public static int NOTIFICATION_ID_BIG_IMAGE = 104;
    public static int INTENT_TYPE_SEARCH = 1001;
    public static final String MODE_REPEAT_NONE = "norepeat";
    public static final String MODE_REPEAT_ALL = "repeatall";
    public static final String MODE_SUFFLE = "suffle";

    public interface FIREBASE {
        String TOPIC_UPDATE = "update";
        String TOPIC_RECOMMEND = "recommend";
        String TOPIC_EVE = "eve";
        String KEY_UPDATE_SUBS = "updateSubs";
        String KEY_DEFAULT_SUBS = "defaultSubs";
    }

    public interface ACTIONS {

        public static String SWIPE_TO_CANCEL = "com.anyaudio.in.action.swipe_to_cancel";
        public static String PLAY_TO_PAUSE = "com.anyaudio.in.action.play_to_pause";
        public static String PAUSE_TO_PLAY = "com.anyaudio.in.action.pause_to_play";
        public static String MAIN_ACTION = "com.anyaudio.in.action.main";
        public static String INIT_ACTION = "com.anyaudio.in.action.init";
        public static String PLAY_ACTION = "com.anyaudio.in.action.play";
        public static String START_FOREGROUND_ACTION = "com.anyaudio.in.action.startforeground";
        public static String STOP_FOREGROUND_ACTION = "com.anyaudio.in.action.stopforeground";

        public static String PLAYING = "notification_state_player";
        public static String STOP_PLAYER = "notification_stop_player";
        String STOP_FOREGROUND_ACTION_BY_STREAMSHEET = "com.anyaudio.in.action.stopforeground_from_user";

        public static String AUDIO_OPTIONS = "com.anyaudio.in.action.songplayoncard";
        public static String SONG_DOWNLOAD_ON_CARD = "com.anyaudio.in.action.downloadoncard";
        public static String SONG_SHOWALL_ON_CARD = "com.anyaudio.in.action.showalloncard";

    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }

    public interface PLAYER{
        public static boolean PLAYING = false;
        public static String AUDIO_TITLE = "";
        public static String THUMBNAIL_URL = "";
        String AUDIO_SUBTITLE = "audio_subtitle";
        public String EXTRAA_PLAYER_STATE = "player_state_extraa";

    }

    public interface PUSH {
        public String PUSH_TYPE_UPDATE = "update";
        public String PUSH_TYPE_EVE_WISHER = "eve";
        public String PUSH_TYPE_RECOMMENDATIONS = "recommendation";
        String EXTRAA_PUSH_TYPE_RECOMMENDATION = "com.anyaudio.push.recommendation";
        int TYPE_RECOM = 1;
        int TYPE_UPDATE = 2;
        int TYPE_EVE = 3;
    }

}
