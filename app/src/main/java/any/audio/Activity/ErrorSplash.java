package any.audio.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import any.audio.Fragments.ActiveTaskFragment;
import any.audio.Network.ConnectivityUtils;
import any.audio.Managers.FontManager;
import any.audio.R;

public class ErrorSplash extends AppCompatActivity {

    private TextView tv;
    private TextView conError;
    private TextView contBtn;
    private TextView poweredBy;
    private static Context mContext;
    private NetworkChangeReceiver receiver;
    private boolean mReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error_splash);
        mContext = this;
        Log.d("AnyAudioApp","[ErrorSplash] onCreate()");

        // check connectivity and redirect
        redirectIfConnected();
        // set up Warning Page
        setUpWarningPage();


    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mReceiverRegistered)
            unRegisterNetworkStateBroadcastListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!mReceiverRegistered)
            registerNetworkStateBroadcastListener();
    }

    private void setUpWarningPage() {

        // xml -> java objects
        tv = (TextView) findViewById(R.id.no_con_text);
        conError = (TextView) findViewById(R.id.no_connection_wifi_icon);
        contBtn = (TextView) findViewById(R.id.continueBtn);
        poweredBy = (TextView) findViewById(R.id.poweredBy);

        // set Type faces
        tv.setTypeface(FontManager.getInstance(this).getTypeFace(FontManager.FONT_RALEWAY_REGULAR));
        conError.setTypeface(FontManager.getInstance(this).getTypeFace(FontManager.FONT_MATERIAL));
        contBtn.setTypeface(FontManager.getInstance(this).getTypeFace(FontManager.FONT_RALEWAY_REGULAR));
        poweredBy.setTypeface(FontManager.getInstance(this).getTypeFace(FontManager.FONT_RALEWAY_REGULAR));

        // set Text Colors
        conError.setTextColor(getResources().getColor(R.color.NoWifiColor));

        // attach Click Listener
        contBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToHome();
            }
        });


    }

    private static void redirectIfConnected() {

        if (ConnectivityUtils.getInstance(mContext).isConnectedToNet())
            navigateToHome();
    }

    private static void navigateToHome(){
        mContext.startActivity(new Intent(mContext, Home.class));
        ((Activity) mContext).finish();
    }

    public void unRegisterNetworkStateBroadcastListener(){

        unregisterReceiver(receiver);
        mReceiverRegistered = false;

    }

    public void registerNetworkStateBroadcastListener(){

        receiver = new NetworkChangeReceiver();
        registerReceiver(receiver,new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        mReceiverRegistered = true;

    }

    public static class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {

            if(intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {

                final ConnectivityManager connMgr = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);

                final android.net.NetworkInfo wifi = connMgr
                        .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

                final android.net.NetworkInfo mobile = connMgr
                        .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

                if (wifi.isAvailable() || mobile.isAvailable()) {
                    redirectIfConnected();
                }

            }
        }
    }


}
