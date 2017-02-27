package any.audio.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import any.audio.Managers.FontManager;
import any.audio.Network.ConnectivityUtils;
import any.audio.R;

public class ErrorSplash extends AppCompatActivity {

    private static Context mContext;
    private NetworkChangeReceiver receiver;
    private boolean mReceiverRegistered = false;

    private static void redirectIfConnected() {

        if (ConnectivityUtils.getInstance(mContext).isConnectedToNet())
            navigateToHome();
    }

    private static void navigateToHome() {
        mContext.startActivity(new Intent(mContext, AnyAudioActivity.class));
        ((Activity) mContext).finish();
    }

    public void fullScreencall() {
        if(Build.VERSION.SDK_INT < 19){
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else {
            //for higher api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fullScreencall();
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
        TextView contBtn = (TextView) findViewById(R.id.continueBtn);

         contBtn.setTypeface(FontManager.getInstance(this).getTypeFace(FontManager.FONT_RALEWAY_REGULAR));

        // attach Click Listener
        contBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToHome();
            }
        });


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
