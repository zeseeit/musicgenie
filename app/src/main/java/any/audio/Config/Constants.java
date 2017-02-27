package any.audio.Config;

import android.os.Environment;

public class Constants {

    public static final   String SERVER_URL = "http://anyaudio.in";
    public static final   String TERM_OF_USE_URL = "http://anyaudio.in/terms-of-use";
    public static final   String SDCARD = "sdcard";
    public static final  String PHONE = "phone";
    public static final  int SCREEN_MODE_TABLET = 0;
    public static final  int SCREEN_MODE_MOBILE = 1;
    public static final   String ACTION_DOWNLOAD_UPDATE = "action_progress_update";
    public static final  String EXTRA_TASK_ID = "task_id";
    public static final   String EXTRA_PROGRESS = "progress";
    public static final   String DOWNLOAD_FILE_DIR = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/AnyAudio";
    public static final   String ACTION_NETWORK_CONNECTED = "android.net.conn.CONNECTIVITY_CHANGE";
    public static final  int SCREEN_ORIENTATION_PORTRAIT = 0;
    public static final  int SCREEN_ORIENTATION_LANDSCAPE = 1;
    public static final  String EXTRA_CONTENT_SIZE = "contentSize";
    public static final  String ACTION_STREAM_URL_FETCHED = "action_uri_fetched";
    public static final  String EXTRAA_URI = "uri";
    public static final  String EXTRAA_STREAM_FILE = "stream_file_name";
    public static final  String EXTRAA_ACTIVITY_PRE_LOAD_FLAG = "actvity_preloaded";
    public static final  String FLAG_STREAMING_CONTINUED = "streamingWillContinue";
    public static final  int ACTION_TYPE_TRENDING = 0;
    public static final  int ACTION_TYPE_RESUME = 1;
    public static final  int ACTION_TYPE_REFRESS = 2;
    public static final  int ACTION_TYPE_SEARCH = 3;
    public static final  String KEY_SEARCH_TERM = "searchTerm";
    public static final  String FLAG_RESET_ADAPTER_DATA = "reset_data_0x879SADF8dsfkdfjd";
    public static final  String KEY_BUNDLE_FIRST_LOAD_DONE = "key_first_load_done";
    public static final  String KEY_FIRST_PAGE_LOADED = "first_page_loaded";
    public static final  int MESSAGE_STATUS_OK = 200;
    public static final  String FEATURE_STREAM = "stream";
    public static final  String FEATURE_DOWNLOAD = "download";
    public static final  int FLAG_STOP_MEDIA_PLAYER = 303;
    public static final  int FLAG_CANCEL_STREAM = 304;
    public static final  int FLAG_PASSING_HANDLER_REF = 101;
    public static final  long FLAG_STREAM_END = -1;
    int FLAG_NEW_VERSION = 1;
    public static final String KEY_CURRENT_VERSION = "currentVersion";
    public static final String KEY_STREAMING_THUMB_URL = "streamingThumbUrl";
    public static final String ACTION_STREAM_PROGRESS_UPDATE_BROADCAST = "streaming_update";
    public static final String STREAM_PROGRESS_TIME = "stream_progress_time";
    public static final String EXTRA_BUFFERED = "buffered_pos";
    public static final String EXTRAA_STREAM_CONTENT_LEN = "stream_content_len";
    public static final String EXTRAA_STREAM_BUFFERED_PROGRESS = "stream_buffered_extraa";
    public static final String EXTRAA_STREAM_PROGRESS = "stream_progress_extraa";
    public static final String STREAM_PREPARE_FAILED_URL_FLAG = "failed_url";
    public static final String EXTRAA_FLAG_STREAM_WILL_CONTINUE = "streamin_will_continue";
    public static final String EXTRAA_FLAG_DOWNLOAD_STATE = "healthy_download_status";
    public static final String KEY_NEW_APP_VERSION_AVAILABLE = "new_version_available";
    public static final String KEY_NEW_APP_VERSION_DESCRIPTION = "new_app_version_desc";
    public static final String KEY_DONOT_REMIND_ME_AGAIN = "donot_remind_me_again_for_update";
    public static final String EXTRAA_NEW_UPDATE_DESC = "extraa_new_update_des";
    public static final String KEY_APP_UPDATE_NOTIFIED = "app_update_notified";
    public static final String KEY_NEW_UPDATE_URL = "new_update_url";
    public static final String EXTRAA_STREAM_TITLE = "stream_title";
    public static final String EXTRAA_STREAM_THUMBNAIL_URL = "stream_thumbnail_url";
    public static final String KEY_LAST_LOADED_TYPE = "last_loaded_type";
    public static final String KEY_STREAM_CONTENT_LEN = "stream_content_len";
    int NOTIFICATION_ID_BIG_IMAGE = 104;
    int INTENT_TYPE_SEARCH = 1001;
    public static final  String MODE_REPEAT_NONE = "norepeat";
    public static final  String MODE_REPEAT_ALL = "repeatall";
    public static final  String MODE_SUFFLE = "suffle";
    public static final String EXTRAA_BROADCAST_TYPE = "BROADCAST_TYPE";
    public static final String BROADCAST_TYPE_STATE = "state_of_download_view";
    public static final String EXTRAA_DOWNLOAD_VIEW_STATE = "view_state";
    public static final String BROADCAST_TYPE_PROGRESS = "progress_update";
    public static final String KEY_NEW_ANYAUDIO_VERSION = "NEWvERSION";

    public static final  String ACTION_STREAM_TO_SERVICE_START = "streamServiceStartAction";
    public static final  String ACTION_STREAM_TO_SERVICE_RELEASE = "streamServiceReleaseAction";
    public static final  String ACTION_STREAM_TO_SERVICE_SEEK_TO = "streamServiceSeekToAction";
    public static final  String ACTION_STREAM_TO_SERVICE_PLAY_PAUSE = "streamServicePlayPauseAction";
    public static final  String ACTION_PREPARE_BOTTOM_PLAYER = "prepare_bottom_player";
    public static final  String ACTION_STREAM_TO_SERVICE_NEXT = "streamServicePlayNext";
    public static final  String SEARCH_MODE_SERVER = "searchModeServer";
    public static final  String SEARCH_MODE_DB = "searchModeDb";
    public static final String THUMBNAIL_VERSION_MEDIUM = "/mqdefault.jpg";

   public static interface DOWNLOAD {

        public static final String STATE_DOWNLOADING = "com.anyaudio.state.downloading";
        public static final String STATE_WAITING = "com.anyaudio.state.waiting";
        public static final String STATE_STOPPED = "com.anyaudio.state.stopped";
    }

   public static interface FIREBASE {
        public static final String TOPIC_UPDATE = "update";
        public static final String TOPIC_RECOMMEND = "recommend";
        public static final String TOPIC_EVE = "eve";
        public static final String KEY_UPDATE_SUBS = "updateSubs";
        public static final String KEY_DEFAULT_SUBS = "defaultSubs";
    }

   public static interface DIRECTION {
        int VERTICAL = 1;
        int HORIZONTAL = 2;
    }

   public static interface ACTIONS {

        public static final String SWIPE_TO_CANCEL = "com.anyaudio.in.action.swipe_to_cancel";
        public static final String PLAY_TO_PAUSE = "com.anyaudio.in.action.play_to_pause";
        public static final String PAUSE_TO_PLAY = "com.anyaudio.in.action.pause_to_play";
        public static final String MAIN_ACTION = "com.anyaudio.in.action.main";
        public static final String INIT_ACTION = "com.anyaudio.in.action.init";
        public static final String PLAY_ACTION = "com.anyaudio.in.action.play";
        public static final String START_FOREGROUND_ACTION = "com.anyaudio.in.action.startforeground";
        public static final String STOP_FOREGROUND_ACTION = "com.anyaudio.in.action.stopforeground";

        public static final String PLAYING = "notification_state_player";
        public static final String STOP_PLAYER = "notification_stop_player";
        public static final String STOP_FOREGROUND_ACTION_BY_STREAMSHEET = "com.anyaudio.in.action.stopforeground_from_user";

        public static final String AUDIO_OPTIONS = "com.anyaudio.in.action.songplayoncard";
        public static final String SONG_DOWNLOAD_ON_CARD = "com.anyaudio.in.action.downloadoncard";
        public static final String SONG_SHOWALL_ON_CARD = "com.anyaudio.in.action.showalloncard";

        public static final String NEXT_ACTION = "com.anyaudio.in.action.nextPlay";
        public static final String DN = "donothing";
    }

    public static interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 101;
    }

   public static interface PLAYER {

        boolean PLAYING = false;
        public static final String AUDIO_TITLE = "";
        public static final String THUMBNAIL_URL = "";
        public static final String AUDIO_SUBTITLE = "audio_subtitle";
        public static final String EXTRAA_PLAYER_STATE = "player_state_extraa";

        int PLAYER_STATE_PLAYING = 2;
        int PLAYER_STATE_PAUSED = 1;
        int PLAYER_STATE_STOPPED = -1;


    }

   public static interface PUSH {
        public static final String PUSH_TYPE_UPDATE = "update";
        public static final String PUSH_TYPE_EVE_WISHER = "eve";
        public static final String PUSH_TYPE_RECOMMENDATIONS = "recommendation";
        public static final String EXTRAA_PUSH_TYPE_RECOMMENDATION = "com.anyaudio.push.recommendation";
        int TYPE_RECOM = 1;
        int TYPE_UPDATE = 2;
        int TYPE_EVE = 3;
    }

}
