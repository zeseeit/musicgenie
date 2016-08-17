package musicgenie.com.musicgenie.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import musicgenie.com.musicgenie.R;
import musicgenie.com.musicgenie.utilities.ConnectivityUtils;
import musicgenie.com.musicgenie.utilities.FontManager;

public class ErrorSplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ConnectivityUtils.getInstance(this).isConnectedToNet()){
            Intent i = new Intent(this,MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }

        setContentView(R.layout.conn_error_layout);
        TextView tv = (TextView) findViewById(R.id.con_text);
        tv.setTypeface(FontManager.getInstance(this).getTypeFace(FontManager.FONT_RALEWAY_REGULAR));
        TextView conError = (TextView) findViewById(R.id.no_connection_wifi);
        conError.setTypeface(FontManager.getInstance(this).getTypeFace(FontManager.FLATICON));
        TextView contBtn = (TextView) findViewById(R.id.continueBtn);
        contBtn.setTypeface(FontManager.getInstance(this).getTypeFace(FontManager.FONT_RALEWAY_REGULAR));

        contBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ErrorSplashActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
        });


    }


}
