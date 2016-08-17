package musicgenie.com.musicgenie.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import musicgenie.com.musicgenie.R;

public class SectionedListViewTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responsive_drawer_test);
        init();
    }

    private void init() {
        TextView downloadBtn = (TextView) findViewById(R.id.download_btn_card);

    }


}
