package any.audio.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import any.audio.Config.Constants;
import any.audio.Managers.FontManager;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;

public class TermsConditionsAcceptance extends AppCompatActivity {

    TextView termsStarttv;
    TextView linkTv;
    TextView acceptBtbTv;

    Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions_acceptance);

        termsStarttv = (TextView) findViewById(R.id.termsStartMsg);
        linkTv = (TextView) findViewById(R.id.terms_link);
        acceptBtbTv = (TextView) findViewById(R.id.acceptTermsBtn);
        typeface = FontManager.getInstance(this).getTypeFace(FontManager.FONT_RALEWAY_REGULAR);

        termsStarttv.setTypeface(typeface);
        linkTv.setTypeface(typeface);
        acceptBtbTv.setTypeface(typeface);
        attachListeners();

    }

    private void attachListeners() {

        linkTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String url = Constants.TERM_OF_USE_URL;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);

            }
        });

        acceptBtbTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPrefrenceUtils.getInstance(TermsConditionsAcceptance.this).setTermsAccepted(true);
                startActivity(new Intent(TermsConditionsAcceptance.this, Home.class));
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
