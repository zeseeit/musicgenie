package any.audio.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import any.audio.Managers.FontManager;
import any.audio.Network.ConnectivityUtils;
import any.audio.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecommendationThemed extends Activity {

    TextView fixedTextView;
    TextView recommendationText;
    TextView okBtn;
    Typeface typeface;
    SimpleDraweeView view;
    private TextView recommendationArtistTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_recommendation_themed_light);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));

        Bundle bundle = getIntent().getExtras();
        final String fixedTxt = bundle.getString("fixed");
        final String recom = bundle.getString("recom");
        final String recom_artist = bundle.getString("artist");
        final String urlThumb = bundle.getString("artUrl");
        final String title = bundle.getString("title");

        typeface = FontManager.getInstance(this).getTypeFace(FontManager.FONT_MATERIAL);
        fixedTextView = (TextView) findViewById(R.id.fixedText);
        okBtn = (TextView) findViewById(R.id.search_item_play_btn);
        recommendationText = (TextView) findViewById(R.id.recommendationTitle);
        recommendationArtistTv = (TextView) findViewById(R.id.recommendationArtist);
        view = (SimpleDraweeView) findViewById(R.id.recommndation_thumbnail);

        okBtn.setTypeface(typeface);
        fixedTextView.setText(fixedTxt);
        recommendationText.setText(title);
        recommendationArtistTv.setText(recom_artist);

        view.setImageURI(urlThumb);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search(recom);
            }
        });


    }

    private void search(String term) {

        Intent homeItent = new Intent(this, AnyAudioActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("type", "search");
        bundle.putString("term", term);
        homeItent.putExtras(bundle);
        homeItent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeItent);
        finish();

    }


}
