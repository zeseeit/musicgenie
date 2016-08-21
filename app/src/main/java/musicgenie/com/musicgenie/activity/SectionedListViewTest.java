package musicgenie.com.musicgenie.activity;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Display;
import android.view.OrientationEventListener;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

import musicgenie.com.musicgenie.R;
import musicgenie.com.musicgenie.adapters.TrendingItemsGridAdapter;
import musicgenie.com.musicgenie.adapters.TrendingRecyclerViewAdapter;
import musicgenie.com.musicgenie.models.Song;
import musicgenie.com.musicgenie.models.TrendingSongModel;
import musicgenie.com.musicgenie.utilities.FontManager;

public class SectionedListViewTest extends AppCompatActivity{

    private static final String TAG = "SectionedListView";
    private TrendingItemsGridAdapter adapter;
    private GridView trendingGrid;
    private RecyclerView mRecyclerView;
    private TrendingRecyclerViewAdapter mRecyclerAdapter;
    private StaggeredGridLayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trending_layout);

        // test data
        ArrayList<Song> list = new ArrayList<>();
        list.add(new Song("Sanam Re1","03:15","ankit","","","","200,000"));
        list.add(new Song("Sanam Re2","03:15","ankit","","","","100,000"));
        list.add(new Song("Sanam Re3","03:15","ankit","","","","100,000"));
        list.add(new Song("Sanam Re4","03:15","ankit","","","","100,000"));
        list.add(new Song("Sanam Re5","03:15","ankit","","","","100,000"));

        mRecyclerView = (RecyclerView) findViewById(R.id.trendingRecylerView);
        mRecyclerAdapter = TrendingRecyclerViewAdapter.getInstance(this);
        layoutManager = new StaggeredGridLayoutManager(2,1);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerAdapter.addSongs(list, "Pop");
        mRecyclerAdapter.addSongs(list,"Rock");
        Display display =  getWindowManager().getDefaultDisplay();
        int orientation = display.getOrientation();
        mRecyclerAdapter.setOrientation(orientation);
        mRecyclerView.setAdapter(mRecyclerAdapter);

//
//        adapter = TrendingItemsGridAdapter.getInstance(this);
//        Display display =  getWindowManager().getDefaultDisplay();
//        int orientation = display.getOrientation();
//        // addition of data
//        adapter.submitGrid(trendingGrid);
//        adapter.addSongs(list,"Pop");
//        adapter.addSongs(list,"Rock");
//        adapter.addSongs(list,"Remix");
//
//        adapter.setOrientation(orientation);
//        trendingGrid.setAdapter(adapter);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        setContentView(R.layout.trending_layout);
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
