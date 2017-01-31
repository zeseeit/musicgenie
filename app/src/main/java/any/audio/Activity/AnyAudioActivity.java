package any.audio.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.DefaultUriDataSource;
import com.google.android.exoplayer.util.Util;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import any.audio.Adapters.ExploreLeftToRightAdapter;
import any.audio.Adapters.PlaylistAdapter;
import any.audio.Adapters.SearchResultsAdapter;
import any.audio.Config.Constants;
import any.audio.Fragments.ExploreFragment;
import any.audio.Fragments.SearchFragment;
import any.audio.Models.PlaylistItem;
import any.audio.Network.ConnectivityUtils;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.SharedPreferences.StreamSharedPref;
import any.audio.helpers.CircularImageTransformer;
import any.audio.helpers.L;
import any.audio.helpers.MusicStreamer;
import any.audio.helpers.PlaylistGenerator;
import any.audio.services.NotificationPlayerService;
import de.hdodenhof.circleimageview.CircleImageView;

public class AnyAudioActivity extends AppCompatActivity implements PlaylistGenerator.PlaylistGenerateListener, ExploreLeftToRightAdapter.ExploreActionListener, SearchResultsAdapter.SearchResultActionListener, PlaylistAdapter.PlaylistItemListener {

    private static final int FRAGMENT_EXPLORE = 1;
    private static final int FRAGMENT_SEARCH = 2;

    final String playBtnString = "\uE039";
    final String pauseBtnString = "\uE036";

    private CircleImageView thumbnail;
    private TextView nextBtn;
    private TextView pauseBtn;
    private TextView playlistBtn;
    private TextView pushDown;
    private TextView title;
    private TextView artist;
    private TextView title_second;
    private TextView artist_second;
    FrameLayout playerBg;
    SeekBar seekBar;

    private Typeface typeface;

    private SlidingUpPanelLayout mLayout;
    private TextView streamDuration;
    private TextView homePanelTitle;
    private static ExoPlayer exoPlayer;
    private SharedPrefrenceUtils utils;
    private int UP_NEXT_PREPARE_TIME_OFFSET = 50000;
    private int mBuffered = -1;
    private AnyAudioPlayer mPlayerThread;
    private StreamProgressUpdateBroadcastReceiver streamProgressUpdpateReceiver;
    private NotificationPlayerStateBroadcastReceiver notificationPlayerStateReceiver;
    private StreamUriBroadcastReceiver streamUriReceiver;
    private SongActionBroadcastListener songActionReceiver;
    private boolean receiverRegistered = false;
    private ProgressBar progressBarStream;
    private ProgressBar playlistPreparingProgressbar;
    private RecyclerView playlistRecyclerView;
    private PlaylistAdapter playlistAdapter;
    private PlaylistGenerator playlistGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_any_audio);
        setSupportActionBar((Toolbar) findViewById(R.id.home_toolbar));
        getSupportActionBar().setTitle("");
        initView();
        handleIntent();
        loadInitials();
        recordScreenProp(2);
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

                transformThumbnail(slideOffset);
                transformControl(slideOffset);
                transformInfo(slideOffset);
                Log.d("SildePanel", "offset " + slideOffset);

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                Log.d("SlidePanel", " onPanelStateChanged " + newState);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!receiverRegistered){
            registerReceivers();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_anyaudio, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_search:

                Intent search = new Intent(this, SearchActivity.class);
                startActivity(search);

                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || mLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(receiverRegistered){
            unRegisterReceivers();
        }

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPlayAction(String video_id, String title) {

        Log.d("StreamTestNew", " action play " + video_id);
        initStream(video_id, title);

    }


    @Override
    public void onDownloadAction(String video_id, String title) {

    }

    @Override
    public void onAddToQueue(String video_id, String title) {

    }

    @Override
    public void onShowAll(String type) {

    }


    @Override
    public void onPlaylistPreparing() {

        // hide the current playlist items  and make the current progresh bar visible
        playlistRecyclerView.setVisibility(View.GONE);
        playlistPreparingProgressbar.setVisibility(View.VISIBLE);

    }

    @Override
    public void onPlaylistPrepared(ArrayList<PlaylistItem> items) {

        // dismiss the progress bar and reset the adapter data
        playlistPreparingProgressbar.setVisibility(View.GONE);
        playlistRecyclerView.setVisibility(View.VISIBLE);
        playlistAdapter.setPlaylistItem(items);
        playlistRecyclerView.setAdapter(playlistAdapter);

    }


    @Override
    public void onPlaylistItemTapped(PlaylistItem item) {

        // stream current item
        initStream(item.videoId,item.getTitle());

    }

    private void handleIntent() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            String type = bundle.getString("type");
            String term = bundle.getString("term");

            if (type != null) {
                if (type != null && type.equals("search")) {

                    SharedPrefrenceUtils.getInstance(this).setLastSearchTerm(term);
                    L.m("AnyAudioHome", " invoking action search");
                    homePanelTitle.setText(reformatHomeTitle(term));
                    transactFragment(FRAGMENT_SEARCH, term);

                }
            } else {
                transactFragment(FRAGMENT_EXPLORE, "Explore");
            }
        } else {
            transactFragment(FRAGMENT_EXPLORE, "Explore");
        }
    }

    private String reformatHomeTitle(String term) {
        String t = (term.length()>21)?term.substring(0,18)+"...":term;
        return t;
    }

    private void loadInitials() {

        //ads
        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        utils = SharedPrefrenceUtils.getInstance(this);

        if (!utils.getFirstPageLoadedStatus()) {
//            invokeAction(Constants.ACTION_TYPE_TRENDING);
            utils.setFirstPageLoadedStatus(true);
            Log.d("HomeTest", " loadInit() Trending ");
            // start only once

        } else {
            Log.d("HomeTest", " loadInit() Resuming ");
            //       invokeAction(Constants.ACTION_TYPE_RESUME);
        }


    }

    private void initView() {
        playlistGenerator = PlaylistGenerator.getInstance(AnyAudioActivity.this);
        playlistGenerator.setPlaylistGenerationListener(this);

        typeface = Typeface.createFromAsset(getAssets(), "MaterialFont.ttf");
        progressBarStream = (ProgressBar) findViewById(R.id.progressBarStreamProgress);
        homePanelTitle = (TextView) findViewById(R.id.homePanelTitle);
        playerBg = (FrameLayout) findViewById(R.id.playerBgFrame);
        thumbnail = (CircleImageView) findViewById(R.id.thumbnail);
        nextBtn = (TextView) findViewById(R.id.nextBtn);
        pauseBtn = (TextView) findViewById(R.id.pauseBtn);
        playlistBtn = (TextView) findViewById(R.id.playlistBtn);
        pushDown = (TextView) findViewById(R.id.pushDown);
        streamDuration = (TextView) findViewById(R.id.stream_duration);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        //playlist view
        playlistPreparingProgressbar = (ProgressBar) findViewById(R.id.auto_play_or_queue_progress_bar);
        playlistRecyclerView = (RecyclerView) findViewById(R.id.autoplay_or_queue_recycler_view);
        playlistRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        playlistRecyclerView.setHasFixedSize(true);

        //font face
        nextBtn.setTypeface(typeface);
        pauseBtn.setTypeface(typeface);
        playlistBtn.setTypeface(typeface);
        pushDown.setTypeface(typeface);

        title = (TextView) findViewById(R.id.title);
        artist = (TextView) findViewById(R.id.artist);
        title_second = (TextView) findViewById(R.id.title_second);
        artist_second = (TextView) findViewById(R.id.artist_second);

        FrameLayout.LayoutParams controlParams = new FrameLayout.LayoutParams((int) inPx(36), (int) inPx(36));
        controlParams.setMargins(0, (int) inPx(6), 0, 0);

        pauseBtn.setLayoutParams(controlParams);
        nextBtn.setLayoutParams(controlParams);
        playlistBtn.setLayoutParams(controlParams);

        RelativeLayout.LayoutParams infoParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        infoParams.setMargins(0, (int) inPx(12), 0, 0);
        title.setLayoutParams(infoParams);
    }

    private void recordScreenProp(int cols) {

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = getResources().getDisplayMetrics().density;
        float screenWidthDP = outMetrics.widthPixels / density;
        Log.i("Home", "Screen Width " + screenWidthDP);

        Resources r = getResources();
        float card_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, screenWidthDP, r.getDisplayMetrics());
        float space_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, r.getDisplayMetrics());
        float songCardWidth = 0;

        if (cols == 2) {
            songCardWidth = (card_px - space_px * 3) / 2; // 3*4dp for spaces
        }
        if (cols == 3) {
            songCardWidth = (card_px - space_px * 4) / 3; // 4 * 4dp for spaces
        }

        SharedPrefrenceUtils.getInstance(this).setSongCardWidthDp(songCardWidth);
        SharedPrefrenceUtils.getInstance(this).setCols(cols);
        SharedPrefrenceUtils.getInstance(this).setAdWidth(screenWidthDP);
    }

    private void transformThumbnail(float slideOffset) {

        float _dimen_DIFFpx = inPx(36);
        float _margin_DIFFpx = inPx(52);
        float _margin_top_DIFFpx = inPx(30);

        float px = inPx(64);

        int newDimen = (int) Math.ceil((slideOffset) * _dimen_DIFFpx + px);
        int newMargin = (int) Math.ceil((slideOffset) * _margin_DIFFpx);

        int newMarginTop = (int) Math.ceil((slideOffset) * _margin_top_DIFFpx);

        FrameLayout.LayoutParams thumbnailParams = new FrameLayout.LayoutParams(newDimen, newDimen);
        thumbnailParams.setMargins(newMargin, newMarginTop, 0, 0);
        thumbnail.setLayoutParams(thumbnailParams);

        playerBg.setAlpha((float) (1.0 - slideOffset));
        progressBarStream.setAlpha((float) (1.0 - slideOffset));
    }

    private void transformControl(float offset) {

        playlistBtn.setAlpha((float) (1.0 - offset * 5));
        pushDown.setAlpha((float) (offset));

        float _dimen_DIFFpx = inPx(14);
        float _margin_top_DIFFpx = inPx(36);
        float px = inPx(36);

        int newDimen = (int) Math.ceil((offset) * _dimen_DIFFpx + px);
        int newMarginTop = (int) Math.ceil((offset) * _margin_top_DIFFpx + inPx(6));

        FrameLayout.LayoutParams controlParams = new FrameLayout.LayoutParams(newDimen, newDimen);

        controlParams.setMargins(0, newMarginTop, 0, 0);
        nextBtn.setLayoutParams(controlParams);
        pauseBtn.setLayoutParams(controlParams);

    }

    private void transformInfo(float offset) {

        int startTextSize = 28;
        int _textSize = (int) Math.ceil((offset) * 28 + startTextSize);
        pauseBtn.setTextSize(_textSize);
        nextBtn.setTextSize(_textSize);


        float alp = (float) (1.0 - offset * 8);
        float sec_alp = offset * 10;

        title.setAlpha(alp);
        artist.setAlpha(alp);
        title_second.setAlpha(sec_alp);
        artist_second.setAlpha(sec_alp);

    }

    private void transactFragment(int fragmentType, String extraa) {

        FragmentManager manager = getSupportFragmentManager();

        switch (fragmentType) {

            case FRAGMENT_EXPLORE:

                ExploreFragment exploreFragment = new ExploreFragment();
                exploreFragment.setActionListener(this);

                manager
                        .beginTransaction()
                        .replace(R.id.fragmentPlaceHolder, exploreFragment)
                        .commitAllowingStateLoss();


                break;

            case FRAGMENT_SEARCH:

                SearchFragment searchFragment = new SearchFragment();
                searchFragment.setExtraa(extraa);
                searchFragment.setActionListener(this);

                manager
                        .beginTransaction()
                        .replace(R.id.fragmentPlaceHolder, searchFragment)
                        .commit();


                break;

            default:
                break;
        }
    }

    private float inPx(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private String getTimeFromMillisecond(int millis) {

        String hr;
        String min;
        String sec;
        String time;
        int i_hr = (millis / 1000) / 3600;
        int i_min = (millis / 1000) / 60;
        int i_sec = (millis / 1000) % 60;

        if (i_hr == 0) {
            min = (String.valueOf(i_min).length() < 2) ? "0" + i_min : String.valueOf(i_min);
            sec = (String.valueOf(i_sec).length() < 2) ? "0" + i_sec : String.valueOf(i_sec);
            time = min + " : " + sec;
        } else {
            hr = (String.valueOf(i_hr).length() < 2) ? "0" + i_hr : String.valueOf(i_hr);
            min = (String.valueOf(i_min).length() < 2) ? "0" + i_min : String.valueOf(i_min);
            sec = (String.valueOf(i_sec).length() < 2) ? "0" + i_sec : String.valueOf(i_sec);
            time = hr + " : " + min + " : " + sec;
        }

        // Log.d("StreamingHome"," time returned "+time);

        return time;


    }

//=============================================================================================================================================================//

    public class StreamProgressUpdateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.ACTION_STREAM_PROGRESS_UPDATE_BROADCAST)) {

                int contentLen = Integer.parseInt(intent.getStringExtra(Constants.EXTRAA_STREAM_CONTENT_LEN));
                int buffered = Integer.parseInt(intent.getStringExtra(Constants.EXTRAA_STREAM_BUFFERED_PROGRESS));
                int progress = Integer.parseInt(intent.getStringExtra(Constants.EXTRAA_STREAM_PROGRESS));

                if (contentLen > 0) {

                    try {

                        String trackLen = getTimeFromMillisecond(contentLen);

                        if (contentLen > 0 && buffered > 0) {

                            pauseBtn.setVisibility(View.VISIBLE);
                            nextBtn.setVisibility(View.VISIBLE);

                            StreamSharedPref.getInstance(AnyAudioActivity.this).setStreamContentLength(trackLen);
                            startNotificationService();

                            if (utils.getNextStreamUrl().length() == 0 && !utils.isStreamUrlFetcherInProgress() && (contentLen - progress) < UP_NEXT_PREPARE_TIME_OFFSET) {

                                Log.d("PlaylistTest", "pre-ready:>starting fetching next Stream Url");
                                utils.setStreamUrlFetcherInProgress(true);

                                // get next play details
                                PlaylistItem nxtItem = playlistGenerator.getUpNext();

                                String nextVid = nxtItem.videoId;
                                String nextVidTitle = nxtItem.title;
                                String upNextThumbnailUrl = getImageUrl(nxtItem.youtubeId);
                                String upNextArtist = nxtItem.uploader;


                                utils.setNextVId(nextVid);
                                utils.setNextStreamTitle(nextVidTitle);

                                // set data ready for notification and bottom sheets
                                StreamSharedPref.getInstance(AnyAudioActivity.this).setStreamThumbnailUrl(upNextThumbnailUrl);
                                StreamSharedPref.getInstance(AnyAudioActivity.this).setStreamTitle(nextVidTitle);
                                StreamSharedPref.getInstance(AnyAudioActivity.this).setStreamSubTitle(upNextArtist);

                                MusicStreamer
                                        .getInstance(AnyAudioActivity.this)
                                        .setData(nextVid, nextVidTitle)
                                        .setBroadcastMode(false)
                                        .setOnStreamUriFetchedListener(new MusicStreamer.OnStreamUriFetchedListener() {
                                            @Override
                                            public void onUriAvailable(String uri) {
                                                Log.d("PlaylistTest", "pre-ready:>next uri available " + uri);
                                                //this is first time stream url fetch
                                                utils.setStreamUrlFetcherInProgress(false);
                                                utils.setNextStreamUrl(uri);
                                            }
                                        })
                                        .initProcess();
                            }

                        }


                        seekBar.setMax(contentLen);
                        progressBarStream.setMax(contentLen);

                        seekBar.setProgress(progress);
                        progressBarStream.setProgress(progress);
                        streamDuration.setText(getTimeFromMillisecond(progress) + "/" + trackLen);

                        if (mBuffered < buffered) {
                            seekBar.setSecondaryProgress(buffered);
                            mBuffered = buffered;
                        }

                    } catch (Exception e) {
                        Log.d("StreamTest", "something went wrong " + e);
                    }
                }
            }

        }
    }

    public class NotificationPlayerStateBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("HomeStream", " onReceive() Notification ");
            if (exoPlayer != null) {

                if (intent.getAction().equals(Constants.ACTIONS.PLAY_TO_PAUSE)) {

                    // pause
                    pauseBtn.setText(playBtnString);
                    StreamSharedPref.getInstance(AnyAudioActivity.this).setStreamerPlayState(false);
                    exoPlayer.setPlayWhenReady(StreamSharedPref.getInstance(AnyAudioActivity.this).getStreamerPlayState());
                }
                if (intent.getAction().equals(Constants.ACTIONS.PAUSE_TO_PLAY)) {

                    // play
                    pauseBtn.setText(pauseBtnString);
                    StreamSharedPref.getInstance(AnyAudioActivity.this).setStreamerPlayState(true);
                    exoPlayer.setPlayWhenReady(StreamSharedPref.getInstance(AnyAudioActivity.this).getStreamerPlayState());
                }

                if (intent.getAction().equals(Constants.ACTIONS.STOP_PLAYER)) {

                    resetPlayer();
                    //todo: hide the bottom player view (optional)

                }
            }

        }
    }

    public class StreamUriBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.ACTION_STREAM_URL_FETCHED)) {

                L.m("PlaylistTest", "update via broadcast: streaming uri " + intent.getStringExtra(Constants.EXTRAA_URI));
                StreamSharedPref.getInstance(AnyAudioActivity.this).setStreamUrlFetchedStatus(true);
                String uri = intent.getStringExtra(Constants.EXTRAA_URI);

                if (uri.equals(Constants.STREAM_PREPARE_FAILED_URL_FLAG)) {
                    // WID: close the bottomSheets with a toast error
                    promptError("Something is Wrong !! Please Try Again.");
                    return;
                }

                mPlayerThread = new AnyAudioPlayer(AnyAudioActivity.this, uri);
                mPlayerThread.start();

            }

        }
    }

    private class SongActionBroadcastListener extends BroadcastReceiver {

        private final int ACTION_STREAM = 101;
        private final int ACTION_DOWNLOAD = 102;
        private final int ACTION_SHOW_ALL = 103;
        private final int ACTION_ADD_TO_QUEUE = 104;

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Constants.ACTIONS.AUDIO_OPTIONS)) {

                int actionType = intent.getIntExtra("actionType", 0);
                String v_id = intent.getStringExtra("vid");
                String title = intent.getStringExtra("title");

                switch (actionType) {

                    case ACTION_STREAM:
                        initStream(v_id, title);
                        break;
                    case ACTION_DOWNLOAD:

                        break;
                    case ACTION_SHOW_ALL:

                        break;
                    case ACTION_ADD_TO_QUEUE:

                        break;
                    default:
                        break;

                }
            }
        }
    }

    private void registerReceivers() {

        streamProgressUpdpateReceiver = new StreamProgressUpdateBroadcastReceiver();
        notificationPlayerStateReceiver = new NotificationPlayerStateBroadcastReceiver();
        streamUriReceiver = new StreamUriBroadcastReceiver();
        songActionReceiver = new SongActionBroadcastListener();

        registerReceiver(streamProgressUpdpateReceiver, new IntentFilter(Constants.ACTION_STREAM_PROGRESS_UPDATE_BROADCAST));
        registerReceiver(notificationPlayerStateReceiver, new IntentFilter(Constants.ACTIONS.PAUSE_TO_PLAY));
        registerReceiver(notificationPlayerStateReceiver, new IntentFilter(Constants.ACTIONS.PLAY_TO_PAUSE));
        registerReceiver(streamUriReceiver, new IntentFilter(Constants.ACTION_STREAM_URL_FETCHED));
        registerReceiver(songActionReceiver, new IntentFilter(Constants.ACTIONS.AUDIO_OPTIONS));
        receiverRegistered = true;

    }

    private void unRegisterReceivers() {

        unregisterReceiver(streamProgressUpdpateReceiver);
        unregisterReceiver(notificationPlayerStateReceiver);
        unregisterReceiver(streamUriReceiver);
        unregisterReceiver(songActionReceiver);
        receiverRegistered = false;

    }

//=============================================================================================================================================================//


    private void promptError(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void startNotificationService() {

        Intent notificationIntent = new Intent(this, NotificationPlayerService.class);
        notificationIntent.setAction(Constants.ACTIONS.START_FOREGROUND_ACTION);
        startService(notificationIntent);

    }

    private void initStream(String video_id, String title) {

        if (ConnectivityUtils.getInstance(this).isConnectedToNet()) {
            resetPlayer();
            StreamSharedPref.getInstance(this).setStreamState(true);
            prepareBottomPlayer();
        } else {
            // re-init player
            prepareBottomPlayer();
        }

        MusicStreamer
                .getInstance(this)
                .setBroadcastMode(true)
                .setData(video_id, title)
                .initProcess();

    }

    private void prepareBottomPlayer() {

        String streamUri = StreamSharedPref.getInstance(this).getStreamThumbnailUrl();
        String streamTitle = StreamSharedPref.getInstance(this).getStreamTitle();

        //Player-View Common
        pauseBtn.setVisibility(View.GONE);
        nextBtn.setVisibility(View.GONE);
        Picasso.with(this).load(streamUri).transform(new CircularImageTransformer()).into(thumbnail);

        //Player-View Visible
        title.setText(streamTitle);
        progressBarStream.setProgress(0);
        progressBarStream.setSecondaryProgress(0);

        //Player-View Hidden
        title_second.setText(streamTitle);
        streamDuration.setText("00:00/00:00");
        seekBar.setProgress(0);
        seekBar.setSecondaryProgress(0);

        //plug adapter

        playlistAdapter = PlaylistAdapter.getInstance(this);
        playlistAdapter.setPlaylistItemListener(this);
        playlistRecyclerView.setAdapter(playlistAdapter);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                streamNext();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {

                if (fromUser) {
                    if (mPlayerThread != null) {
                        L.m("Home", "sending seek msg");
                        if (exoPlayer.getBufferedPosition() > position)
                            exoPlayer.seekTo(position);
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("StreamingHome", " clicked Play Pause Btn");

                if (exoPlayer != null) {

                    if (StreamSharedPref.getInstance(AnyAudioActivity.this).getStreamerPlayState()) {
                        // pause
                        StreamSharedPref.getInstance(AnyAudioActivity.this).setStreamerPlayState(false);
                        pauseBtn.setText(playBtnString);

                        //handle: On Long time pause: stop streaming
//                        pauseWaitHandler.postDelayed(
//                                pauseWaitRunnable,
//                                STREAMING_PAUSED_WAIT_TIMEOUT
//                        );


                    } else {
                        //play
                        StreamSharedPref.getInstance(AnyAudioActivity.this).setStreamerPlayState(true);
                        pauseBtn.setText(pauseBtnString);

                        //remove callbacks for pause handler(if any)
//                        pauseWaitHandler.removeCallbacks(pauseWaitRunnable);

                    }

                    sendPlayerStateToNotificationService(StreamSharedPref.getInstance(AnyAudioActivity.this).getStreamerPlayState());

                    exoPlayer.setPlayWhenReady(StreamSharedPref.getInstance(AnyAudioActivity.this).getStreamerPlayState());
                } else {
                    Log.d("StreamingHome", " exoPlayer object null");
                }
            }
        });

    }

    private void sendPlayerStateToNotificationService(boolean streamerPlayState) {

        Intent notificationIntent = new Intent(this, NotificationPlayerService.class);
        notificationIntent.setAction(Constants.ACTIONS.PLAY_ACTION);
        notificationIntent.putExtra(Constants.PLAYER.EXTRAA_PLAYER_STATE, streamerPlayState);
        startService(notificationIntent);

    }

    private void resetPlayer() {

        collapsePlayerNotificationControl();
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.stop();
            exoPlayer.release();
            L.m("StreamingHome", "Player Reset Done");

        }
        StreamSharedPref.getInstance(this).setStreamState(false);

    }

    private void collapsePlayerNotificationControl() {

        Intent notificationIntent = new Intent(this, NotificationPlayerService.class);
        notificationIntent.setAction(Constants.ACTIONS.STOP_FOREGROUND_ACTION_BY_STREAMSHEET);
        startService(notificationIntent);
    }

//======================================================================================================================================================================================//

    public class AnyAudioPlayer extends Thread {

        private static final String TAG = "AnyAudioPlayer";
        private static final int BUFFER_SEGMENT_SIZE = 64 * 1024;
        private static final int BUFFER_SEGMENT_COUNT = 256;
        private Context context;
        private AnyAudioActivity.AnyAudioPlayer mInstance;
        private MediaCodecAudioTrackRenderer audioRenderer;
        private Uri mUri;
        private Handler mUIHandler;
        private boolean PLAYER_STATE_ENDED = false;
        private boolean PLAYER_STATE_PLAYING = false;
        private int playerCurrentPositon = -1;
        private int playerContentDuration = -1;

        public AnyAudioPlayer(Context context, String uri) {
            this.context = context;
            mUri = Uri.parse(uri);
        }

        @Override
        public void run() {
            Looper.prepare();
            useExoplayer();
            Looper.loop();
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
            StreamSharedPref.getInstance(context).setStreamerPlayState(true);
            exoPlayer.addListener(new ExoPlayer.Listener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                    if (playbackState == 5) // 5 - > integer code for player end state
                        StreamSharedPref.getInstance(context).setStreamState(false);

                }

                @Override
                public void onPlayWhenReadyCommitted() {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    Log.d("ExoPlayer", "exo error setting stream state false");
                    StreamSharedPref.getInstance(context).setStreamState(false);
                }
            });

            while (exoPlayer != null) {

                playerCurrentPositon = (int) exoPlayer.getCurrentPosition();
                playerContentDuration = (int) exoPlayer.getDuration();


                if (playerContentDuration != -1) {
                    if (playerCurrentPositon >= playerContentDuration) {
                        Log.d("PlaylistText", " releasing and stoping exoplayer");
                        exoPlayer.setPlayWhenReady(false);
                        exoPlayer.release();
                        exoPlayer.stop();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                streamNext();
                            }
                        });

                        StreamSharedPref.getInstance(context).setStreamState(false);
                        break;
                    }
                }

                if (exoPlayer.getPlayWhenReady()) {
                    Log.d("ExoPlayer", " broadcasting progress");
                    broadcastStreamProgresUpdate(
                            String.valueOf(playerCurrentPositon),
                            String.valueOf(playerContentDuration),
                            String.valueOf(exoPlayer.getBufferedPosition())
                    );
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }


        }

    }

    public void broadcastStreamProgresUpdate(String playingAt, String contentLen, String bufferedProgress) {

        Intent intent = new Intent(Constants.ACTION_STREAM_PROGRESS_UPDATE_BROADCAST);
        intent.putExtra(Constants.EXTRAA_STREAM_PROGRESS, playingAt);
        intent.putExtra(Constants.EXTRAA_STREAM_CONTENT_LEN, contentLen);
        intent.putExtra(Constants.EXTRAA_STREAM_BUFFERED_PROGRESS, bufferedProgress);
        sendBroadcast(intent);

    }

    private void streamNext() {

        long diff;
        if (exoPlayer != null) {

            exoPlayer.setPlayWhenReady(false);
            exoPlayer.stop();
            exoPlayer.release();
            L.m("PlaylistTest", "Player Released");

            StreamSharedPref.getInstance(this).setStreamState(false);

            PlaylistItem nxtItem = PlaylistGenerator.getInstance(this).getUpNext();

            String upNextVid = nxtItem.videoId;
            String upNextTitle = nxtItem.title;
            String upNextThumbnailUrl = getImageUrl(nxtItem.youtubeId);
            String upNextArtist = nxtItem.uploader;

            Log.d("PlaylistTest", "nextVid:=" + upNextVid + " nextTitle:=" + upNextTitle);

            utils.setNextVId(upNextVid);
            utils.setNextStreamTitle(upNextTitle);

            diff = exoPlayer.getDuration() - exoPlayer.getCurrentPosition();

            if (diff > UP_NEXT_PREPARE_TIME_OFFSET) {
                L.m("PlaylistTest", "diff : " + diff);
                // means stream fetcher not in progress
                StreamSharedPref.getInstance(this).setStreamThumbnailUrl(upNextThumbnailUrl);
                StreamSharedPref.getInstance(this).setStreamTitle(upNextTitle);
                StreamSharedPref.getInstance(this).setStreamSubTitle(upNextArtist);

                Log.d("PlaylistTest", "starting normal stream..");
                initStream(upNextVid, upNextTitle);

            } else {

                // means stream fetcher is in progress or has finished
                boolean isFetcherInProgress = utils.isStreamUrlFetcherInProgress();
                String nextStreamUrl = utils.getNextStreamUrl();

                if (nextStreamUrl.length() > 0) {

                    streamNext();

                } else {

                    if (!isFetcherInProgress) {
                        // some network issue caused the url fetcher to stop its fetching task
                        initStream(upNextVid, upNextTitle);

                    } else {
                        // no cases possible
                    }
                }
            }
        }
    }

    private String getImageUrl(String vid) {
        //return "https://i.ytimg.com/vi/kVgKfScL5yk/hqdefault.jpg";
        return "https://i.ytimg.com/vi/" + vid + "/hqdefault.jpg";  // additional query params => ?custom=true&w=240&h=256
    }

}