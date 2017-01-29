package any.audio.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import any.audio.Adapters.ExploreLeftToRightAdapter;
import any.audio.Adapters.SearchResultsAdapter;
import any.audio.Centrals.CentralDataRepository;
import any.audio.Config.Constants;
import any.audio.Fragments.ExploreFragment;
import any.audio.Fragments.SearchFragment;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.helpers.L;
import any.audio.helpers.SearchSuggestionHelper;

public class AnyAudioActivity extends AppCompatActivity implements ExploreLeftToRightAdapter.ExploreActionListener, SearchResultsAdapter.SearchResultActionListener {

    private static final int FRAGMENT_EXPLORE = 1;
    private static final int FRAGMENT_SEARCH = 2;

    private ImageView thumbnail;
    private TextView nextBtn;
    private TextView pauseBtn;
    private TextView playlistBtn;
    private TextView pushDown;
    private TextView title;
    private TextView artist;
    private TextView title_second;
    private TextView artist_second;
    FrameLayout playerBg;
    ProgressBar progressBar;

    private Typeface typeface;

    private SlidingUpPanelLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_any_audio);
        setSupportActionBar((Toolbar) findViewById(R.id.home_toolbar));
        getSupportActionBar().setTitle("");
        initView();
        handleIntent();
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

    private void handleIntent() {

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            String type = bundle.getString("type");
            String term = bundle.getString("term");

            if (type != null) {
                if (type != null && type.equals("search")) {

                    SharedPrefrenceUtils.getInstance(this).setLastSearchTerm(term);
                    L.m("AnyAudioHome", " invoking action search");
                    transactFragment(FRAGMENT_SEARCH, term);

                }
            } else {
                transactFragment(FRAGMENT_EXPLORE,"Explore");
            }
        }else{
            transactFragment(FRAGMENT_EXPLORE,"Explore");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private void initView() {

        typeface = Typeface.createFromAsset(getAssets(), "MaterialFont.ttf");
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        playerBg = (FrameLayout) findViewById(R.id.playerBgFrame);
        thumbnail = (ImageView) findViewById(R.id.thumbnail);
        nextBtn = (TextView) findViewById(R.id.nextBtn);
        pauseBtn = (TextView) findViewById(R.id.pauseBtn);
        playlistBtn = (TextView) findViewById(R.id.playlistBtn);
        pushDown = (TextView) findViewById(R.id.pushDown);
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
        progressBar.setAlpha((float) (1.0 - slideOffset));
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
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPlayAction(String video_id, String title) {

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

    private void setUpPlaylist() {

    }

}
