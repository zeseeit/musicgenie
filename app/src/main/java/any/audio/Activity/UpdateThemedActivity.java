package any.audio.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;;
import android.widget.TextView;
import any.audio.Config.Constants;
import any.audio.Managers.FontManager;
import any.audio.R;
public class UpdateThemedActivity extends AppCompatActivity {

    TextView tvUpdateDescription;
    TextView btnCancel;
    TextView btnDownload;
    Typeface tfMaterial;
    TextView newVersion;
    TextView previousVersion;
    private String newAppDownloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_dialog_v2);

        tfMaterial = FontManager.getInstance(this).getTypeFace(FontManager.FONT_MATERIAL);
        tvUpdateDescription = (TextView) findViewById(R.id.newFeatures);
        btnCancel = (TextView) findViewById(R.id.cancelUpdateDialog);
        btnDownload = (TextView) findViewById(R.id.updateBtn);
        newVersion = (TextView) findViewById(R.id.newVersionText);
        previousVersion = (TextView) findViewById(R.id.oldVersionText);


        //Regular TypeFace
        btnCancel.setTypeface(tfMaterial);
        // data setters

        String newInThisUpdateDescription = getIntent().getExtras().getString(Constants.EXTRAA_NEW_UPDATE_DESC);
        newAppDownloadUrl = getIntent().getExtras().getString(Constants.KEY_NEW_UPDATE_URL);
        String ov = getCurrentVersion();
        String nv = getIntent().getExtras().getString(Constants.KEY_NEW_ANYAUDIO_VERSION);

        tvUpdateDescription.setText(newInThisUpdateDescription);
        previousVersion.setText("Your Version "+ ov);
        newVersion.setText("New Version "+ nv);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel();
            }
        });

    }

    private String getCurrentVersion(){
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionName;
    }

    public void download(View view) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(newAppDownloadUrl));
        startActivity(intent);
        finish();

    }

    public void cancel() {
        finish();
    }
}
