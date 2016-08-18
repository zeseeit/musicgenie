package musicgenie.com.musicgenie.activity;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import musicgenie.com.musicgenie.R;
import musicgenie.com.musicgenie.utilities.FontManager;

public class SectionedListViewTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responsive_drawer_test);
        init();
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
