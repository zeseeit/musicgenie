package any.audio.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import any.audio.Config.Constants;
import any.audio.Managers.FontManager;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;

public class UpdateThemedActivity extends AppCompatActivity {

    TextView tvUpdateMessage;
    TextView tvUpdateDescription;
    TextView btnCancel;
    TextView btnDownload;
    Typeface tf;
    CheckBox mUpdateCheckBox;
    private String newInThisUpdateDescription;
    private String newAppDownloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_themed);

        tf = FontManager.getInstance(this).getTypeFace(FontManager.FONT_RALEWAY_REGULAR);
        tvUpdateMessage = (TextView) findViewById(R.id.updateMsg);
        tvUpdateDescription = (TextView) findViewById(R.id.updateDescription);
        btnCancel = (TextView) findViewById(R.id.cancel_update_msg_dialog);
        btnDownload = (TextView) findViewById(R.id.download_btn_update_msg);
        mUpdateCheckBox = (CheckBox) findViewById(R.id.checkedConfirmation);

        mUpdateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean byUser) {
                if (byUser) {
                    if (compoundButton.isChecked()) {
                        SharedPrefrenceUtils.getInstance(UpdateThemedActivity.this).setDoNotRemindMeAgainForAppUpdate(true);
                    } else {
                        SharedPrefrenceUtils.getInstance(UpdateThemedActivity.this).setDoNotRemindMeAgainForAppUpdate(false);
                    }
                }
            }
        });

        //Regular TypeFace
        btnDownload.setTypeface(tf);
        btnCancel.setTypeface(tf);
        tvUpdateMessage.setTypeface(tf);
        tvUpdateDescription.setTypeface(tf);

        // data setters

        newInThisUpdateDescription = getIntent().getExtras().getString(Constants.EXTRAA_NEW_UPDATE_DESC);
        newAppDownloadUrl = getIntent().getExtras().getString(Constants.KEY_NEW_UPDATE_URL);
        tvUpdateDescription.setText(newInThisUpdateDescription);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update_themed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void download(View view) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(newAppDownloadUrl));
        startActivity(intent);
        finish();

    }

    public void cancel(View view) {
        finish();
    }
}
