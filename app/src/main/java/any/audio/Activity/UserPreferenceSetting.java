package any.audio.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import any.audio.Config.Constants;
import any.audio.Managers.FontManager;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;

public class UserPreferenceSetting extends AppCompatActivity {

    TextView thumbnailTxtView;
    TextView issueTxtView;
    TextView issueBtnTxt;
    Switch thumbnailSwitch;
    Toolbar toolbar;

    TextView updateTextView;
    TextView suggestTextView;
    TextView suggestBtn;
    TextView updateBtn;

    private TextView termsOfUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_preference_setting);
        // initializes all view components
        init();
        // setsToolbar
        setUpToolbar();
        // loads settings
        loadSettings();
        // attach listeners to setting widgets
        attachListeners();
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void attachListeners() {


        thumbnailSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean state) {
                SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(UserPreferenceSetting.this);
                if (state) {
                    utils.setOptionsForThumbnailLoad(true);
                } else {
                    utils.setOptionsForThumbnailLoad(false);
                }
            }
        });

        issueBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, "anyaudio.in@gmail.com");
                intent.putExtra(Intent.EXTRA_SUBJECT, "Issue/Report");
                intent.putExtra(Intent.EXTRA_TEXT, "");

                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForUpdate();
                SharedPrefrenceUtils.getInstance(UserPreferenceSetting.this).setDoNotRemindMeAgainForAppUpdate(false);
            }
        });

        suggestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                suggestOther();
            }
        });


        termsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = Constants.TERM_OF_USE_URL;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

    }


    public void checkForUpdate() {

        SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(this);

        if (utils.getNewVersionAvailibility() && !utils.getDoNotRemindMeAgainForAppUpdate()) {

            Intent updateIntent = new Intent(getApplicationContext(), UpdateThemedActivity.class);
            updateIntent.putExtra(Constants.EXTRAA_NEW_UPDATE_DESC, utils.getNewVersionDescription());
            updateIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(updateIntent);

        }
    }

    private void suggestOther() {
        String dndUrl = SharedPrefrenceUtils.getInstance(this).getNewUpdateUrl();
        String textToShare = "AnyAudio a tool to download/stream Any Audio from Internet. You Can Download it from "+dndUrl;

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, textToShare);
        startActivity(share);

    }

    private void loadSettings() {

        SharedPrefrenceUtils utils = SharedPrefrenceUtils.getInstance(this);
        // get thumbnail choice
        thumbnailSwitch.setChecked(utils.getOptionsForThumbnailLoad());
    }

    private void init() {

        thumbnailTxtView = (TextView) findViewById(R.id.loadThumbnailTextMessage);
        issueTxtView = (TextView) findViewById(R.id.issuesTextMessage);
        issueBtnTxt = (TextView) findViewById(R.id.issueBtn);
        thumbnailSwitch = (Switch) findViewById(R.id.loadThumbnailSwitch);
        updateTextView = (TextView) findViewById(R.id.UpdateTextMessage);
        suggestTextView = (TextView) findViewById(R.id.SuggestTextMessage);
        updateBtn = (TextView) findViewById(R.id.updateBtn);
        suggestBtn = (TextView) findViewById(R.id.suggestBtn);

        termsOfUse = (TextView) findViewById(R.id.termsOfUse);


        Typeface tf = FontManager.getInstance(this).getTypeFace(FontManager.FONT_RALEWAY_REGULAR);
        Typeface materialIconFont = FontManager.getInstance(this).getTypeFace(FontManager.FONT_MATERIAL);
        thumbnailTxtView.setTypeface(tf);
        issueTxtView.setTypeface(tf);
        termsOfUse.setTypeface(tf);
        updateTextView.setTypeface(tf);
        suggestTextView.setTypeface(tf);
        issueBtnTxt.setTypeface(materialIconFont);
        suggestBtn.setTypeface(materialIconFont);
        updateBtn.setTypeface(materialIconFont);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_user_preference_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
