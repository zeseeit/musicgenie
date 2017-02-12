package any.audio.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import any.audio.Managers.FontManager;
import any.audio.R;

public class EveWisherThemedActivity extends AppCompatActivity {

    TextView eveTitle;
    TextView eveMessage;
    ImageView evePic;
    TextView cancelEveDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.eve_wishing_dialog);
        Bundle bundle = getIntent().getExtras();
        String eve_msg = bundle.getString("message");
        String title = bundle.getString("title");

        eveTitle = (TextView) findViewById(R.id.eveCaption);
        eveMessage = (TextView) findViewById(R.id.eveMessage);
        cancelEveDialog = (TextView) findViewById(R.id.cancelEveDialog);

        eveMessage.setText(eve_msg);
        eveTitle.setText(title);
        cancelEveDialog.setTypeface(FontManager.getInstance(this).getTypeFace(FontManager.FONT_MATERIAL));
        cancelEveDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}
