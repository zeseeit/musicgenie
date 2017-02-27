package any.audio.Fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.services.UpdateCheckService;

/**
 * Created by Ankit on 2/24/2017.
 */

public class UpdatesFragment extends Fragment {

    TextView availableVersionTv,currentVersionTv,updateBtn;
    ProgressBar progressBar;
    SharedPrefrenceUtils utils;
    private Context context;
    private long WAIT_FOR_UPDATE_CHECK = 10000;
    private UpdateCheckedBroadcastReceiver receiver;
    private boolean receiverRegistered = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utils = SharedPrefrenceUtils.getInstance(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_updates,null,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        availableVersionTv = (TextView) view.findViewById(R.id.availableVersion);
        currentVersionTv = (TextView) view.findViewById(R.id.currentVersion);
        updateBtn = (TextView) view.findViewById(R.id.checkUpdateBtn);
        progressBar = (ProgressBar) view.findViewById(R.id.updateCheckProgress);
        progressBar.setVisibility(View.GONE);
        bindValues();

    }

    private void bindValues() {

        if(utils.getNewVersionAvailibility())
            availableVersionTv.setText("Available App Version : "+utils.getLatestVersionName());

        currentVersionTv.setText("Your App Version : "+getCurrentAppVersionName());
        updateBtn.setEnabled(true);

        if(utils.getNewVersionAvailibility()){
            updateBtn.setText("DOWNLOAD UPDATE");
        }else{
            updateBtn.setText("CHECK FOR NEWER UPDATE");
        }

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkForUpdate();
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if(!receiverRegistered)
            registerReceiver();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(receiverRegistered){
            unRegisterReceiver();
        }
    }

    public void checkForUpdate() {

        // start check service and change status
        if(!utils.getNewVersionAvailibility()) {

            updateBtn.setText("Checking For Updates...");
            updateBtn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            Intent updateCheckServiceIntent = new Intent(context, UpdateCheckService.class);
            updateCheckServiceIntent.setAction("ACTION_UPDATE");
            context.startService(updateCheckServiceIntent);

        }else {

            download();

        }

    }

    public void download() {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(utils.getNewUpdateUrl()));
        startActivity(intent);

    }

    private int getCurrentAppVersionCode() {

        try {

            PackageInfo _info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return _info.versionCode;

        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
            return -1;

        }
    }

    private String getCurrentAppVersionName() {

        try {

            PackageInfo _info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return _info.versionName;

        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();
            return "0.0";

        }
    }

    public void registerReceiver(){
        receiver = new UpdateCheckedBroadcastReceiver();
        context.registerReceiver(receiver,new IntentFilter("ACTION_UPDATE_CHECK"));
        receiverRegistered = true;
    }

    public void unRegisterReceiver(){
        context.unregisterReceiver(receiver);
        receiverRegistered = false;
    }

    public class UpdateCheckedBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getExtras().getString("msg");
            availableVersionTv.setText(msg);
            progressBar.setVisibility(View.GONE);
            updateBtn.setText("CHECK FOR UPDATE");
            updateBtn.setEnabled(true);
        }
    }

}
