package any.audio.Fragments;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import any.audio.Activity.Home;
import any.audio.Interfaces.SeekBarChangeListener;
import any.audio.Interfaces.StreamCancelListener;
import any.audio.Interfaces.StreamPlayPauseListener;
import any.audio.Interfaces.StreamProgressListener;
import any.audio.Managers.FontManager;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.SharedPreferences.StreamSharedPref;
import any.audio.helpers.CircularImageTransformer;
import any.audio.helpers.L;

/**
 * Created by Ankit on 10/9/2016.
 */
public class StreamFragment extends Fragment {


    private Activity mActivity;
    private boolean ACTIVITY_ATTACHED_STATE = false;
    String playBtn;
    String pauseBtn;
    private boolean isStreaming = false;
    ImageView streamingThumbnail;
    TextView streamingSongTitle;
    SeekBar seekbar;
    ProgressBar indeterminateProgressBar;
    TextView cancelStreamBtn;
    TextView currentStreamPosition;
    TextView streamDuration;
    TextView playPauseStreamBtn;
    private View streamView;
    int mBuffered = -1;
    private boolean progressViewToggleDone = false;
    private SeekBarChangeListener seekBarChangeListener;
    private StreamCancelListener streamCancelListener;
    private StreamPlayPauseListener playPauseListener;
    private FontManager mFontManager;
    private Typeface mTypefaceMaterial;
    private Typeface mTypefaceRaleway;
    private Timer mTimer;
    private long STREAM_INFO_UPDATE_INTERVAL = 500;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playBtn = getActivity().getString(R.string.streaming_play_btn);
        pauseBtn = getActivity().getString(R.string.streaming_pause_btn);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        streamView = inflater.inflate(R.layout.stream_fragment_bottom_strip, container, false);
        initViews();
        return streamView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initValues();
        initialProgressItemVisibility();
        setTypeFaces();
        attachListeners();
        //subscribeForProgressUpdate((Home) mActivity);

        if (mTimer == null)
            mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new StreamInfoUpdateTask(), 0, STREAM_INFO_UPDATE_INTERVAL);

    }

    private void initViews() {

        streamingThumbnail = (ImageView) streamView.findViewById(R.id.streaming_item_thumb);
        streamDuration = (TextView) streamView.findViewById(R.id.streaming_item_totalTrackLengthText);
        seekbar = (SeekBar) streamView.findViewById(R.id.streaming_item_audio_seekbar);
        currentStreamPosition = (TextView) streamView.findViewById(R.id.streaming_item_currentTrackPositionText);
        playPauseStreamBtn = (TextView) streamView.findViewById(R.id.streaming_item_play_pauseBtn);
        streamingSongTitle = (TextView) streamView.findViewById(R.id.streaming_item_title);
        cancelStreamBtn = (TextView) streamView.findViewById(R.id.streaming_item_cancel_text_btn);
        indeterminateProgressBar = (ProgressBar) streamView.findViewById(R.id.stream_indeterminate_progress);

    }

    private void initValues() {

        String uri = StreamSharedPref.getInstance(getActivity()).getStreamThumbnailUrl();
        String streamFileName = StreamSharedPref.getInstance(getActivity()).getStreamTitle();
        Picasso.with(getActivity()).load(uri).transform(new CircularImageTransformer()).into(streamingThumbnail);
        streamingSongTitle.setText(streamFileName);

        streamDuration.setText(" | 00:00");
        currentStreamPosition.setText("00:00");
        seekbar.setProgress(0);
        seekbar.setSecondaryProgress(0);

    }

    private void setTypeFaces() {

        mFontManager = FontManager.getInstance(getActivity());
        mTypefaceMaterial = mFontManager.getTypeFace(FontManager.FONT_MATERIAL);
        mTypefaceRaleway = mFontManager.getTypeFace(FontManager.FONT_RALEWAY_REGULAR);
        // raleway
        streamDuration.setTypeface(mTypefaceRaleway);
        currentStreamPosition.setTypeface(mTypefaceRaleway);
        streamingSongTitle.setTypeface(mTypefaceRaleway);
        // material icons
        playPauseStreamBtn.setTypeface(mTypefaceMaterial);
        cancelStreamBtn.setTypeface(mTypefaceMaterial);

    }

    private void attachListeners() {
        // attach listeners
        cancelStreamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                L.m("StreamTest", " onCancel " + streamCancelListener);
                if (streamCancelListener != null) {
                    StreamSharedPref.getInstance(getActivity()).setStreamState(false);
                    streamCancelListener.onCancel();
                }

            }
        });

        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean fromUser) {

                if (fromUser) {
                    seekBarChangeListener.onSeekTo(position);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        playPauseStreamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isStreaming) {
                    // pause
                    isStreaming = false;
                    playPauseStreamBtn.setText(playBtn);
                    playPauseListener.onStateChange(isStreaming);
                } else {
                    //play
                    isStreaming = true;
                    playPauseStreamBtn.setText(pauseBtn);
                    playPauseListener.onStateChange(isStreaming);
                }

            }
        });

    }

    private void initialProgressItemVisibility() {

        seekbar.setVisibility(View.INVISIBLE);
        indeterminateProgressBar.setVisibility(View.VISIBLE);

    }

    private boolean isStreaming() {
        return (SharedPrefrenceUtils.getInstance(getActivity()).getCurrentStreamingItem().length() == 0);
    }

    private void subscribeForProgressUpdate(Home home) {

        home.setStreamProgressListener(new StreamProgressListener() {
            @Override
            public void onProgressChange(int progress, int buffered, int duration) {

                if (!progressViewToggleDone) {
                    if (buffered > 0) {
                        indeterminateProgressBar.setVisibility(View.INVISIBLE);
                        seekbar.setVisibility(View.VISIBLE);
                        progressViewToggleDone = true;
                    }
                }
                currentStreamPosition.setText(getTimeFromMillisecond(progress));
                seekbar.setProgress(progress);
                streamDuration.setText(" | " + getTimeFromMillisecond(duration));
                seekbar.setMax(duration);
                if (mBuffered < buffered) {
                    seekbar.setSecondaryProgress(buffered);
                    mBuffered = buffered;
                }
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = activity;

        //todo: start updatehandler which checks info from sharedpref. and update to UI.
        // regular update at interval

    }

    private class StreamInfoUpdateTask extends TimerTask {

        @Override
        public void run() {

            //read from sharedpref and log
            StreamSharedPref pref = StreamSharedPref.getInstance(getActivity());
            Log.d("StreamFragment", " progress " + pref.getStreamingProgress() + " content " + pref.getStreamContentLength());

            int buffered = pref.getStreamingBuffer();
            int progress = pref.getStreamCurrentPlayingPosition();
            int duration = pref.getStreamContentLength();

            try {

                if (!progressViewToggleDone) {
                    if (buffered > 0) {
                        indeterminateProgressBar.setVisibility(View.INVISIBLE);
                        seekbar.setVisibility(View.VISIBLE);
                        progressViewToggleDone = true;
                    }
                }

                currentStreamPosition.setText(getTimeFromMillisecond(progress));
                seekbar.setProgress(progress);
                streamDuration.setText(" | " + getTimeFromMillisecond(duration));
                seekbar.setMax(duration);
                if (mBuffered < buffered) {
                    seekbar.setSecondaryProgress(buffered);
                    mBuffered = buffered;
                }

            } catch (Exception e) {
                Log.d("StreamFragment", "something went wrong " + e);
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (mTimer != null) {
            mTimer.cancel();
        }

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void setPlayPauseListener(StreamPlayPauseListener streamPlayPauseListener) {
        this.playPauseListener = streamPlayPauseListener;
    }

    public void setStreamCancelListener(StreamCancelListener streamCancelListener) {
        this.streamCancelListener = streamCancelListener;
    }

    public void setSeekBarChangeListener(SeekBarChangeListener listener) {
        this.seekBarChangeListener = listener;
    }

    private String getTimeFromMillisecond(int millis) {
        String hr = "";
        String min = "";
        String sec = "";
        String time = "";
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

        return time;
    }

}
