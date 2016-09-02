package musicgenie.com.musicgenie.activity;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import musicgenie.com.musicgenie.R;
import musicgenie.com.musicgenie.adapters.TrendingItemsGridAdapter;
import musicgenie.com.musicgenie.adapters.TrendingRecyclerViewAdapter;
import musicgenie.com.musicgenie.models.Song;
import musicgenie.com.musicgenie.utilities.FontManager;

public class SectionedListViewTest extends AppCompatActivity{

    private static final String TAG = "SectionedListView";
    private TrendingItemsGridAdapter adapter;
    private GridView trendingGrid;
    private RecyclerView mRecyclerView;
    private TrendingRecyclerViewAdapter mRecyclerAdapter;
    private StaggeredGridLayoutManager layoutManager;
    private ArrayList<Song> list;
    private Display display;
    private int orientation;
    private boolean dataPuffed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sectioned_view);
        log("onCreate()");
        // test data hello ankit this is my push
        list = new ArrayList<>();
        list.add(new Song("Sanam Re1","03:15","ankit","","","","200,000"));
        list.add(new Song("Sanam Re2","03:15","ankit","","","","100,000"));
        list.add(new Song("Sanam Re3","03:15","ankit","","","","100,000"));
        list.add(new Song("Sanam Re4", "03:15", "ankit", "", "", "", "100,000"));
        list.add(new Song("Sanam Re5", "03:15", "ankit", "", "", "", "100,000"));

        if(isPortrait(getOrientation())){
            setUpRecycler(3);
        }else{
            setUpRecycler(4);
        }
        if(savedInstanceState!=null){
            if(!savedInstanceState.getBoolean("dataLoaded")){ // neglect repeated addition of data
                log("onCreate()p");
                puffRecyclerWithData();
            }
        }else{
            puffRecyclerWithData();
        }
        plugAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        log("onResume()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(dataPuffed)
        outState.putBoolean("dataLoaded",true);
    }

    private void setUpRecycler(int mxCols ) {
        mRecyclerView = (RecyclerView) findViewById(R.id.trendingRecylerView);
        mRecyclerAdapter = TrendingRecyclerViewAdapter.getInstance(this);
        layoutManager = new StaggeredGridLayoutManager(mxCols, 1);
        mRecyclerView.setLayoutManager(layoutManager);

    }

    private void puffRecyclerWithData(){
        mRecyclerAdapter.addSongs(list, "Pop");
        mRecyclerAdapter.addSongs(list,"Rock");
        dataPuffed = true;
    }

    private void plugAdapter(){
        mRecyclerAdapter.setOrientation(getOrientation());
        mRecyclerAdapter.setScreenMode(screenMode());
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    private int getOrientation(){
        return getWindowManager().getDefaultDisplay().getOrientation();
    }

    private int screenMode(){
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels/metrics.ydpi;
        float xInches = metrics.widthPixels/metrics.xdpi;

        double diagonal = Math.sqrt(yInches*yInches+xInches*xInches);
        if(diagonal>6.5){
            return 0;
        }else{
            return 1;
        }
    }

    private void log(String s) {
        Log.d(TAG, "log "+s);
    }

    private boolean isPortrait(int orientation) {
        return orientation%2==0;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        setContentView(R.layout.sectioned_view);
        mRecyclerAdapter.setOrientation(newConfig.orientation);
        Log.e(TAG, " nConfigurationChanged to" + newConfig.orientation);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        super.onConfigurationChanged(newConfig);
    }

    private void init() {

        Typeface fontawesome = FontManager.getInstance(this).getTypeFace(FontManager.FONT_AWESOME);
        Typeface ralewayTf = FontManager.getInstance(this).getTypeFace(FontManager.FONT_RALEWAY_REGULAR);
        // material
        TextView downloadBtn = (TextView) findViewById(R.id.download_btn_card);
        TextView uploader_icon = (TextView) findViewById(R.id.uploader_icon);
        TextView views_icon = (TextView) findViewById(R.id.views_icon);
        downloadBtn.setTypeface(fontawesome);
        uploader_icon.setTypeface(fontawesome);
        views_icon.setTypeface(fontawesome);

        // regular raleway
        TextView content_length = (TextView) findViewById(R.id.song_time_length);
        TextView uploader = (TextView) findViewById(R.id.uploader_name);
        TextView views = (TextView) findViewById(R.id.views_text);
        content_length.setTypeface(ralewayTf);
        uploader.setTypeface(ralewayTf);
        views.setTypeface(ralewayTf);
        // plain text
        TextView popMenuBtn = (TextView) findViewById(R.id.popUpMenuIcon);



    }

}
