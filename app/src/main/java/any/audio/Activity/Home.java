package any.audio.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import any.audio.Config.AppConfig;
import any.audio.Centrals.CentralDataRepository;
import any.audio.Network.ConnectivityUtils;
import any.audio.Config.Constants;
import any.audio.Interfaces.FeatureRequestListener;
import any.audio.helpers.L;
import any.audio.helpers.MusicStreamer;
import any.audio.R;
import any.audio.Adapters.ResulstsRecyclerAdapter;
import any.audio.Models.ResultMessageObjectModel;
import any.audio.Models.SearchSuggestion;
import any.audio.helpers.SearchSuggestionHelper;
import any.audio.Models.SectionModel;
import any.audio.Interfaces.SeekBarChangeListener;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.Interfaces.StreamCancelListener;
import any.audio.Fragments.StreamFragment;
import any.audio.Models.StreamMessageObjectModel;
import any.audio.Interfaces.StreamPlayPauseListener;
import any.audio.Interfaces.StreamProgressListener;
import any.audio.helpers.TaskHandler;
import any.audio.services.UpdateCheckService;

import static any.audio.R.id.stream_fragment_bottom_container;

public class Home extends AppCompatActivity {

    private static WeakReference<Home> wrActivity = null;

    private static final int MAX_DATABASE_RESPONSE_TIME = 5 * 1000; // 5 secs
    static SeekBar streamSeeker;
    static TextView currentTrackPosition;
    static TextView totalTrackPosition;
    static TextView cancelStreamingBtn;
    static TextView streamingItemTitle;

    private StreamFragment streamingFragment;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private ResulstsRecyclerAdapter mRecyclerAdapter;
    private ProgressDialog progressDialog;
    private CentralDataRepository repository;
    private FloatingSearchView searchView;
    private SwipeRefreshLayout swipeRefressLayout;
    // non-static handlers : We don`t have
    private Handler mStreamHandler;
    private Handler mCDRMessageHandler;
    private Handler mNewUpdateAvailableHandler;
    private Handler mRemoteThreadControlHandler;

    private SharedPrefrenceUtils utils;
    private ProgressBar progressBar;
    private TextView progressBarMsgPanel;
    //private StreamFragment streamDialog;
    private boolean isStreaming;
    private MediaPlayer mPlayer;
    private boolean prepared = false;
    private StreamUriBroadcastReceiver receiver;
    private boolean mReceiverRegistered = false;
    private int mDuration;
    private int mCurrentPosition;
    private int mCurrentBuffered;
    private MusicGenieMediaPlayer mPlayerThread;
    private ExoPlayer exoPlayer;
    private FrameLayout streamDialogContainer;
    private String mStreamFileName;

    private StreamProgressListener streamProgressListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        configureStorageDirectory(savedInstanceState);
        instantiateViews();
        handleMessages();   // message handler on UI thread
        subscribeToFeatureRequestListener();
        wrActivity = new WeakReference<Home>(this);
        utils = SharedPrefrenceUtils.getInstance(this);

        if (!utils.getFirstPageLoadedStatus()) {
            invokeAction(Constants.ACTION_TYPE_TRENDING);
            utils.setFirstPageLoadedStatus(true);
            // start only once
            startService(new Intent(this, UpdateCheckService.class));

        } else {
            invokeAction(Constants.ACTION_TYPE_RESUME);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!mReceiverRegistered) {
            registerForBroadcastListen(this);
            L.m("Home", "Register Receiver");
        }
    }

    @Override
    protected void onStop() {
        L.m("Home", "onStop()");
        super.onStop();

        if (mReceiverRegistered) {
            unRegisterBroadcast();
            L.m("Home", "UnRegister Receiver");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        invokeAction(Constants.ACTION_TYPE_RESUME);

        if (SharedPrefrenceUtils.getInstance(this).getCurrentStreamingItem().length() > 0) {
            L.m("StreamTest", "transacting");
            transactStreamFragment();
        }
    }

    // for stream fragment
    public void setStreamProgressListener(StreamProgressListener listener){
        this.streamProgressListener = listener;
    }

    private void handleMessages() {
        mCDRMessageHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // hide progress
                L.m("CDR", "handling Message[UI Thread]");

                if (swipeRefressLayout.isRefreshing()) {
                    L.m("Home", "Disabling Swipe Refresh Layout");
                    swipeRefressLayout.setRefreshing(false);
                    swipeRefressLayout.setEnabled(true);
                }

                hideProgress();
                ResultMessageObjectModel object = (ResultMessageObjectModel) msg.obj;
                SectionModel item = object.data;

                if (object.Status == Constants.MESSAGE_STATUS_OK) {

                    // check if item is empty
                    if (item.getList() != null) {
                        if (item.getList().size() == 0 && mRecyclerAdapter.getItemCount() == 0) {
                            // hide the recycler view and Show Message
                            mRecyclerView.setVisibility(RecyclerView.GONE);
                            progressBar.setVisibility(View.GONE);
                            progressBarMsgPanel.setVisibility(View.VISIBLE);
                            progressBarMsgPanel.setText("Troubling Getting Data......");

                        } else {

                            mRecyclerView.setVisibility(RecyclerView.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            progressBarMsgPanel.setVisibility(View.GONE);

                        }
                    }
                    // enque item regardless of cases
                    mRecyclerAdapter.enque(item);
                } else {
                    //TODO: Collect Unexpected Error From CDR(Central Data Repository)
                }
            }
        };
    }

    private Runnable resumeContentCheckTask = new Runnable() {
        @Override
        public void run() {
            if (mRecyclerView.getVisibility() != View.VISIBLE) {
                // mRecyclerView.setVisibility(RecyclerView.GONE);
                progressBar.setVisibility(View.INVISIBLE);
                progressBarMsgPanel.setVisibility(View.VISIBLE);
                progressBarMsgPanel.setText("No Cached Data . Plz Connect");
            }
        }
    };

    /**
     * @param actionType type of action to invoke
     */
    public void invokeAction(int actionType) {

        // regardless of any action Type
        // make Recycler View Invisible and progress bar visible
        mRecyclerView.setVisibility(RecyclerView.GONE);
        progressBar.setVisibility(View.VISIBLE);
        progressBarMsgPanel.setVisibility(View.VISIBLE);

        repository = CentralDataRepository.getInstance(this);

        switch (actionType) {


            case Constants.ACTION_TYPE_TRENDING:
                //  showProgress("Presenting Trending...");
                if (!ConnectivityUtils.getInstance(this).isConnectedToNet()) {
                    mRecyclerView.setVisibility(RecyclerView.GONE);
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBarMsgPanel.setVisibility(View.VISIBLE);
                    //  L.m("Home", "setting msg");
                    //Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
                    progressBarMsgPanel.setText("Troubling Getting DataCheck Your Working Data Connection");
                    return;
                }

                progressBarMsgPanel.setText("Loading Trending");
                repository.submitAction(CentralDataRepository.FLAG_FIRST_LOAD, mCDRMessageHandler);
                break;

            case Constants.ACTION_TYPE_RESUME:
                //showProgress("Presenting Your Items");
                progressBarMsgPanel.setText("Resuming Contents");
                repository.submitAction(CentralDataRepository.FLAG_RESTORE, mCDRMessageHandler);
                new Handler().postDelayed(resumeContentCheckTask, MAX_DATABASE_RESPONSE_TIME);
                break;

            case Constants.ACTION_TYPE_REFRESS:

                if (!ConnectivityUtils.getInstance(this).isConnectedToNet()) {
                    mRecyclerView.setVisibility(RecyclerView.GONE);
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBarMsgPanel.setVisibility(View.VISIBLE);
                    progressBarMsgPanel.setText("Troubling Getting Data......\nCheck Your Working Data Connection");
                    return;
                }// or continue the same

                progressBarMsgPanel.setText("Refressing Content");

                repository.submitAction(CentralDataRepository.FLAG_REFRESS, mCDRMessageHandler);
                break;

            case Constants.ACTION_TYPE_SEARCH:
                //showProgress("Searching Item");

                if (!ConnectivityUtils.getInstance(this).isConnectedToNet()) {
                    mRecyclerView.setVisibility(RecyclerView.GONE);
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBarMsgPanel.setVisibility(View.VISIBLE);
                    progressBarMsgPanel.setText("Can`t Search Your Item . No Connectivity !");
                    return;
                }// or continue the same

                String searchQuery = SharedPrefrenceUtils.getInstance(this).getLastSearchTerm();
                progressBarMsgPanel.setText("Searching For.. " + searchQuery);
                repository.submitAction(CentralDataRepository.FLAG_SEARCH, mCDRMessageHandler);
                break;
        }

    }

    private void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void subscribeToFeatureRequestListener() {
        ResulstsRecyclerAdapter.getInstance(this).setOnFeatureRequestListener(new FeatureRequestListener() {
            @Override
            public void onTaskTapped(String type, String video_id, String file_name) {

                switch (type) {

                    case Constants.FEATURE_DOWNLOAD:
                        showAlertForAssuringFeatureRequest(Constants.FEATURE_DOWNLOAD, file_name, video_id);
                        break;

                    case Constants.FEATURE_STREAM:
                        showAlertForAssuringFeatureRequest(Constants.FEATURE_STREAM, file_name, video_id);
                        break;

                }
            }
        });
    }

    MusicStreamer.OnStreamUriFetchedListener streamUriFetchedListener = new MusicStreamer.OnStreamUriFetchedListener() {
        @Override
        public void onUriAvailable(String uri) {
        }
    };

    private void showAlertForAssuringFeatureRequest(String forType, final String stuff, final String v_id) {

        switch (forType) {
            case Constants.FEATURE_STREAM:
                DialogInterface.OnClickListener streamDialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                stream(v_id, stuff);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //Dismiss dialog
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(Html.fromHtml("Do You Want To " + "<b> " + " Stream " + "</b>" + " ?") + "\n" + stuff).setPositiveButton("Yes", streamDialogClickListener)
                        .setNegativeButton("No", streamDialogClickListener).show();

                break;
            case Constants.FEATURE_DOWNLOAD:
                DialogInterface.OnClickListener downloaDialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                TaskHandler
                                        .getInstance(Home.this)
                                        .addTask(stuff, v_id);

                                Toast.makeText(Home.this, stuff + " Added To Download", Toast.LENGTH_LONG).show();

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //dismiss dialog
                                dialog.dismiss();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builderDownloadAlert = new AlertDialog.Builder(this);
                builderDownloadAlert.setMessage(Html.fromHtml("Do You Want To " + "<b>" + "Download " + "</b>" + "?") + "\n" + stuff).setPositiveButton("Yes", downloaDialogClickListener)
                        .setNegativeButton("No", downloaDialogClickListener).show();
        }

    }

    private void stream(String v_id, String stuff) {

        resetPlayer();
        transactStreamFragment();
        MusicStreamer
                .getInstance(Home.this)
                .setData(v_id, stuff)
                .setOnStreamUriFetchedListener(streamUriFetchedListener)
                .initProcess();

    }

    private int screenMode() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;

        double diagonal = Math.sqrt(yInches * yInches + xInches * xInches);
        if (diagonal > 6.5) {
            return Constants.SCREEN_MODE_TABLET;
        } else {
            return Constants.SCREEN_MODE_MOBILE;
        }
    }

    private void plugAdapter() {
        mRecyclerAdapter.setOrientation(getOrientation());
        mRecyclerAdapter.setScreenMode(screenMode());
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    private void instantiateViews() {

        int maxCols = (isPortrait(getOrientation())) ? ((screenMode() == Constants.SCREEN_MODE_MOBILE) ? 2 : 3) : 4;
        swipeRefressLayout = (SwipeRefreshLayout) findViewById(R.id.content_refresser);
        mRecyclerView = (RecyclerView) findViewById(R.id.trendingRecylerView);
        layoutManager = new StaggeredGridLayoutManager(maxCols, 1);
        mRecyclerAdapter = ResulstsRecyclerAdapter.getInstance(this);
        mRecyclerView.setLayoutManager(layoutManager);
        progressBar = (ProgressBar) findViewById(R.id.homeProgressBar);
        progressBarMsgPanel = (TextView) findViewById(R.id.ProgressMsgPanel);
        swipeRefressLayout.setColorSchemeColors(getResources().getColor(R.color.PrimaryColor), Color.WHITE);
        swipeRefressLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refressContent();
            }
        });

        plugAdapter();
        setSearchView();
    }

    private boolean isPortrait(int orientation) {
        return orientation % 2 == 0;
    }

    private void refressContent() {

        if (ConnectivityUtils.getInstance(this).isConnectedToNet()) {
            invokeAction(Constants.ACTION_TYPE_REFRESS);
            swipeRefressLayout.setRefreshing(true);
            swipeRefressLayout.setEnabled(false);

        } else {
            Snackbar.make(swipeRefressLayout, "No Connectivity !!", Snackbar.LENGTH_SHORT).show();
            swipeRefressLayout.setRefreshing(false);
            swipeRefressLayout.setVisibility(View.VISIBLE);
        }

    }

    public void setSearchView() {
        searchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        searchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {


            @Override
            public void onSearchTextChanged(String oldText, String newText) {


                if (!ConnectivityUtils.getInstance(Home.this).isConnectedToNet()) {
                    mRecyclerView.setVisibility(RecyclerView.GONE);
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBarMsgPanel.setVisibility(View.VISIBLE);
                    progressBarMsgPanel.setText("Can`t Suggest. No Connectivity");
                    return;
                } else {
                    progressBarMsgPanel.setText("");
                }

                if (!oldText.equals("") && newText.equals("")) {
                    searchView.clearSuggestions();
                } else {

                    searchView.showProgress();
                    SearchSuggestionHelper.getInstance(Home.this).findSuggestion(newText,
                            new SearchSuggestionHelper.OnFindSuggestionListener() {
                                @Override
                                public void onResult(ArrayList<SearchSuggestion> list) {
                                    searchView.swapSuggestions(list);
                                    searchView.hideProgress();
                                }
                            });
                }


            }


        });


        searchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(com.arlib.floatingsearchview.suggestions.model.SearchSuggestion searchSuggestion) {
                //stop futhur suggestion requests

                SearchSuggestionHelper.getInstance(Home.this).cancelFuthurRequestUntilQueryChange();

                fireSearch(searchSuggestion.getBody());
            }

            @Override
            public void onSearchAction(String query) {
                fireSearch(query);
            }
        });

        searchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {
            @Override
            public void onActionMenuItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (id == R.id.action_settings) {
                    Intent i = new Intent(Home.this, UserPreferenceSetting.class);
                    startActivity(i);
                }
                if (id == R.id.action_downloads) {
                    Intent i = new Intent(Home.this, DowloadsActivity.class);
                    startActivity(i);
                }

                if (id == R.id.action_request_trending) {
                    invokeAction(Constants.ACTION_TYPE_TRENDING);
                }

            }
        });


    }

    private void fireSearch(String query) {

        SharedPrefrenceUtils.getInstance(this).setLastSearchTerm(query);
        L.m("Home", " invoking action search");
        invokeAction(Constants.ACTION_TYPE_SEARCH);

    }

    private void configureStorageDirectory(Bundle savedInstance) {

        if (savedInstance == null) {
            L.m("Home configureStorageDirectory()", "making dirs");
            AppConfig.getInstance(this).configureDevice();
        }
    }

    private int getOrientation() {
        return getWindowManager().getDefaultDisplay().getOrientation();
    }

    private void prepareStreaming(String uri, final String file_name) {

        mStreamHandler = new Handler() {

            //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void handleMessage(Message msg) {

                StreamMessageObjectModel object = (StreamMessageObjectModel) msg.obj;
                mCurrentPosition = (int) object.progress;
                mDuration = (int) object.maxLength;
                mCurrentBuffered = (int) object.currentBuffered;
                if (mCurrentPosition >= 0) {
                    if(streamProgressListener!=null)
                        streamProgressListener.onProgressChange(mCurrentPosition, mCurrentBuffered, mDuration);
                }
            }
        };

        mPlayerThread = new MusicGenieMediaPlayer(this, uri, mStreamHandler);
        mPlayerThread.start();

    }

    private void unRegisterBroadcast() {
        if (mReceiverRegistered) {
            this.unregisterReceiver(receiver);
            mReceiverRegistered = false;
        }
    }

    private void registerForBroadcastListen(Context context) {
        receiver = new StreamUriBroadcastReceiver();
        if (!mReceiverRegistered) {
            context.registerReceiver(receiver, new IntentFilter(Constants.ACTION_STREAM_URL_FETCHED));
            mReceiverRegistered = true;
        }
    }

    public class StreamUriBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.ACTION_STREAM_URL_FETCHED)) {

                L.m("Home", "update via broadcast: streaming uri " + intent.getStringExtra(Constants.EXTRAA_URI));

                if (!isStreaming)
                    prepareStreaming(intent.getStringExtra(Constants.EXTRAA_URI), intent.getStringExtra(Constants.EXTRAA_STREAM_FILE));
            }
        }

    }

    public class MusicGenieMediaPlayer extends Thread {

        private static final String TAG = "MusicGenieMediaPlayer";
        private Context context;
        private MusicGenieMediaPlayer mInstance;
        private MediaPlayer player;
        private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
        private static final int BUFFER_SEGMENT_COUNT = 256;
        private MediaCodecAudioTrackRenderer audioRenderer;
        private Uri mUri;
        private Handler mUIHandler;
        private boolean PLAYER_STATE_ENDED = false;
        private boolean PLAYER_STATE_PLAYING = false;
        private int playerCurrentPositon = -1;
        private int playerContentDuration = -1;

        public MusicGenieMediaPlayer(Context context, String uri, Handler handler) {
            this.context = context;
            mUri = Uri.parse(uri);
            mUIHandler = handler;

        }


        @Override
        public void run() {
            Looper.prepare();
            useExoplayer();
            Looper.loop();
        }

        private void useNativeMediaPlayer() {

            Uri mUri = Uri.parse("https://redirector.googlevideo.com/videoplayback?ip=2405%3A204%3Aa108%3Ad941%3Ac1a6%3Aee19%3Ab91e%3A2212&requiressl=yes&lmt=1475255327003268&itag=43&id=o-AEwHFbPb9W4VvmStnHurqdnMuVo-XQif-0oAXbXuVoed&dur=0.000&pcm2cms=yes&source=youtube&upn=gA6OYhdD4fs&mime=video%2Fwebm&ratebypass=yes&ipbits=0&initcwndbps=320000&expire=1475706999&gcr=in&sparams=dur%2Cei%2Cgcr%2Cid%2Cinitcwndbps%2Cip%2Cipbits%2Citag%2Clmt%2Cmime%2Cmm%2Cmn%2Cms%2Cmv%2Cpcm2cms%2Cpl%2Cratebypass%2Crequiressl%2Csource%2Cupn%2Cexpire&key=yt6&mn=sn-gwpa-qxae&mm=31&ms=au&ei=Fyz1V4KYDImEoAPRwLjwCQ&pl=36&mv=m&mt=1475684495&signature=A8B6FD2BC32B05B17E0C62DA1E36967B72E84E3A.3515AE79C436E6A1B1A42BC9E3E14892C5C2C95A&title=%E0%A4%B2%E0%A4%B2%E0%A4%95%E0%A5%80+%E0%A4%9A%E0%A5%81%E0%A4%A8%E0%A4%B0%E0%A4%BF%E0%A4%AF%E0%A4%BE+%E0%A4%93%E0%A5%9D+%E0%A4%95%E0%A5%87+-+Pawan+Singh+%26+Akshara+Singh+-+Dular+Devi+Maiya+Ke+-+Bhojpuri+Devi+Geet+2016");
            MediaPlayer mediaPlayer = MediaPlayer.create(context, mUri);
            mediaPlayer.start();

            if (mediaPlayer != null) {
                while (mediaPlayer.isPlaying()) {

                    StreamMessageObjectModel objectModel = new StreamMessageObjectModel(
                            mediaPlayer.getCurrentPosition(),
                            mediaPlayer.getDuration(),
                            0);

                    Message msg = Message.obtain();
                    msg.obj = objectModel;
                    mUIHandler.sendMessage(msg);

                }
            }

        }

        private void resetExoPlayer() {

            // check for already streaming
            if (exoPlayer != null)
                if (exoPlayer.getPlayWhenReady()) {
                    exoPlayer.stop();
                    exoPlayer.release();
                }

        }

        private void useExoplayer() {

            // resetExoPlayer();

            exoPlayer = ExoPlayer.Factory.newInstance(1);
            // Settings for exoPlayer
            Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
            String userAgent = Util.getUserAgent(context, "AnyAudio");
            DataSource dataSource = new DefaultUriDataSource(context, null, userAgent);

            ExtractorSampleSource sampleSource = new ExtractorSampleSource(
                    mUri,
                    dataSource,
                    allocator,
                    BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT);

            audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource);
            // Prepare ExoPlayer
            exoPlayer.prepare(audioRenderer);
            exoPlayer.setPlayWhenReady(true);

            while (exoPlayer.getPlayWhenReady()) {

                playerCurrentPositon = (int) exoPlayer.getCurrentPosition();
                playerContentDuration = (int) exoPlayer.getDuration();

                StreamMessageObjectModel objectModel = new StreamMessageObjectModel(
                        playerCurrentPositon,
                        playerContentDuration,
                        exoPlayer.getBufferedPosition()
                );

                Message msg = Message.obtain();
                msg.obj = objectModel;
                L.m("ExoPlayer", "sending..");
                mUIHandler.sendMessage(msg);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


                if (playerContentDuration != -1) {
                    if (playerCurrentPositon >= playerContentDuration) break;
                }
            }
        }
    }

    private void resetPlayer() {

        if (exoPlayer != null) {
            L.m("StreamingFragment", "reset Player");
            exoPlayer.stop();
            exoPlayer.release();
        }

    }

    private void transactStreamFragment() {

        streamDialogContainer = (FrameLayout) findViewById(stream_fragment_bottom_container);
        streamDialogContainer.setVisibility(View.VISIBLE);
        final FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = manager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.leave_from_bottom);

        StreamFragment oldFragment = (StreamFragment) manager.findFragmentById(R.id.stream_fragment_bottom_container);

        if(oldFragment==null) {

            streamingFragment = new StreamFragment();
            streamingFragment.setRetainInstance(true);
            streamingFragment.setSeekBarChangeListener(new SeekBarChangeListener() {
                @Override
                public void onSeekTo(int seekToPosition) {

                    if (mPlayerThread != null) {
                        L.m("Home", "sending seek msg");
                        if (exoPlayer.getBufferedPosition() > seekToPosition)
                            exoPlayer.seekTo(seekToPosition);
                    }
                }
            });


            streamingFragment.setStreamCancelListener(new StreamCancelListener() {
                @Override
                public void onCancel() {

                    if (mPlayerThread != null) {
                        exoPlayer.stop();
                        exoPlayer.release();

                        //reset shared pref
                        final Activity activity = wrActivity.get();
                        if (activity != null && !activity.isFinishing()) {
                            SharedPrefrenceUtils.getInstance(getApplicationContext()).setCurrentStreamingItem("");
                            FragmentTransaction newFragmentTransaction = getSupportFragmentManager().beginTransaction();
                            newFragmentTransaction.setCustomAnimations(R.anim.enter_from_bottom, R.anim.leave_from_bottom);
                            newFragmentTransaction.remove(streamingFragment);
                            newFragmentTransaction.commitAllowingStateLoss();
                        }

                    }

                }
            });

            streamingFragment.setPlayPauseListener(new StreamPlayPauseListener() {
                @Override
                public void onStateChange(boolean Stream) {
                    if (Stream) {
                        exoPlayer.setPlayWhenReady(true);
                    } else {
                        exoPlayer.setPlayWhenReady(false);
                    }
                }
            });

            L.m("StreamTest", " new trans");
            fragmentTransaction.replace(stream_fragment_bottom_container, streamingFragment).commit();
        }else{
            L.m("StreamTest"," old trans");
            fragmentTransaction.replace(stream_fragment_bottom_container, oldFragment).commit();
        }
    }
}
