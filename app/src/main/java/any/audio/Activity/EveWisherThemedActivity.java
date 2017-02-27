package any.audio.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import any.audio.Adapters.EveRecommendationGridAdapter;
import any.audio.Managers.FontManager;
import any.audio.Models.PlaylistItem;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.helpers.QueueManager;
import any.audio.helpers.Segmentor;

public class EveWisherThemedActivity extends AppCompatActivity {

    String mTitleBuffer;
    String mArtistBuffer;
    String mVideoIdBuffer;
    String mYoutubeIdBuffer;

    TextView eveTitle;
    TextView eveMessage;
    TextView eveSuggestionAddToQueueBtn;
    GridView gridLayout;
    TextView cancelEveDialog;
    private ArrayList<PlaylistItem> items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.eve_wishing_dialog);
        Bundle bundle = getIntent().getExtras();
        String eve_msg = bundle.getString("message");
        String title = bundle.getString("title");
        collectSuggestions(bundle);
        eveTitle = (TextView) findViewById(R.id.eveCaption);
        eveMessage = (TextView) findViewById(R.id.eveMessage);
        eveSuggestionAddToQueueBtn = (TextView) findViewById(R.id.eveAddToQueue);
        cancelEveDialog = (TextView) findViewById(R.id.cancelEveDialog);
        gridLayout = (GridView) findViewById(R.id.eveSuggestionsGrid);
        gridLayout.setNumColumns(SharedPrefrenceUtils.getInstance(this).getCols());
        eveMessage.setText(eve_msg);
        eveTitle.setText(title);
        cancelEveDialog.setTypeface(FontManager.getInstance(this).getTypeFace(FontManager.FONT_MATERIAL));

        cancelEveDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        eveSuggestionAddToQueueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appendQueueItems();
                Toast.makeText(EveWisherThemedActivity.this,"Added Items To Your Queue",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EveWisherThemedActivity.this,AnyAudioActivity.class));

                finish();
            }
        });

        EveRecommendationGridAdapter gridLayoutAdapter = EveRecommendationGridAdapter.getInstance(this);
        gridLayoutAdapter.setSuggestionsList(items);
        gridLayout.setAdapter(gridLayoutAdapter);

    }

    private void appendQueueItems() {

        for(PlaylistItem i:items){
            QueueManager.getInstance(this).pushQueueItem(i,false);
        }

    }

    private void collectSuggestions(Bundle bundle) {

        items = new ArrayList<>();

        try {

            ArrayList<String> videoTitlesBuffer = new Segmentor().getParts(mTitleBuffer = bundle.getString("vtlts"), '#');
            ArrayList<String> videoIds = new Segmentor().getParts(mVideoIdBuffer = bundle.getString("vids"), '#');
            ArrayList<String> yids = new Segmentor().getParts(mYoutubeIdBuffer = bundle.getString("yids"), '#');
            ArrayList<String> uploaders = new Segmentor().getParts(mArtistBuffer = bundle.getString("uploaders"), '#');

            for (int i = 0; i < videoIds.size(); i++) {
                items.add(new PlaylistItem(videoIds.get(i), yids.get(i), videoTitlesBuffer.get(i), uploaders.get(i)));
            }

        }catch (Exception e){
            Log.d(EveWisherThemedActivity.class.getSimpleName(),"Data Error");
        }

    }
}
