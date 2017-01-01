package any.audio.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import any.audio.R;

public class EveWisherThemedActivity extends AppCompatActivity {

    TextView eveMessage;
    ImageView evePic;
    TextView okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eve_wisher_themed);
        Bundle bundle = getIntent().getExtras();
        String eve_msg = bundle.getString("message");
        String dpUrl = bundle.getString("dp");

        eveMessage = (TextView) findViewById(R.id.eveMessage);
        evePic = (ImageView) findViewById(R.id.evePic);
        okBtn = (TextView) findViewById(R.id.sayThanks);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        eveMessage.setText(eve_msg);
        if (dpUrl.length() > 0)
            Picasso.with(this).load(dpUrl).into(evePic);

    }
}
