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

        Typeface ralewayFace = Typeface.createFromAsset(this.getAssets(), "rl.ttf");
        Typeface bold = Typeface.createFromAsset(this.getAssets(),"Raleway-Bold.ttf");
        Typeface ebold = Typeface.createFromAsset(this.getAssets(),"Raleway-ExtraBold.ttf");
        Typeface heavy = Typeface.createFromAsset(this.getAssets(),"Raleway-Heavy.ttf");
        Typeface regular = Typeface.createFromAsset(this.getAssets(),"Raleway-Regular.ttf");
        Typeface sbold = Typeface.createFromAsset(this.getAssets(),"Raleway-SemiBold.ttf");

        TextView tv = (TextView) findViewById(R.id.con_text);
        tv.setTypeface(regular);

        TextView contBtn = (TextView) findViewById(R.id.continueBtn);
        contBtn.setTypeface(regular);

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
