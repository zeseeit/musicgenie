package any.audio.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import any.audio.Config.Constants;
import any.audio.Managers.FontManager;
import any.audio.R;

public class RecommendationThemed extends Activity {

    TextView fixedTextView;
    TextView recommendationText;
    TextView okBtn;
    Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_recommendation_themed);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));

        Bundle bundle = getIntent().getExtras();
        final String fixedTxt = bundle.getString("fixed");
        final String recom = bundle.getString("recom");


        typeface = FontManager.getInstance(this).getTypeFace(FontManager.FONT_RALEWAY_REGULAR);
        fixedTextView = (TextView) findViewById(R.id.fixedText);
        recommendationText = (TextView) findViewById(R.id.recommendationText);
        okBtn = (TextView) findViewById(R.id.recommendation_ok_btn);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //navigate to home and search for seach term
                search(recom);
            }
        });

        fixedTextView.setTypeface(typeface);
        recommendationText.setTypeface(typeface);
        okBtn.setTypeface(typeface);

        fixedTextView.setText(fixedTxt);
        recommendationText.setText(recom);

    }

    private void search(String term) {

        Intent homeItent = new Intent(this, Home.class);
        Bundle bundle = new Bundle();
        bundle.putString("push_type", Constants.PUSH.PUSH_TYPE_RECOMMENDATIONS);
        bundle.putString("search_term", term);
        homeItent.putExtras(bundle);
        homeItent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeItent);
        finish();

    }


}
