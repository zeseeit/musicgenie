package any.audio.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import any.audio.Activity.UpdateThemedActivity;
import any.audio.Activity.UserPreferenceSetting;
import any.audio.Config.Constants;
import any.audio.Managers.FontManager;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.services.UpdateCheckService;

/**
 * Created by Ankit on 2/12/2017.
 */

public class SettingsFragment extends Fragment {

    private Context context;
    TextView thumbnailTxtView;
    TextView pushNotificationTxtView;
    TextView pushNotificationSoundTxtView;
    Switch pushNotificationSwitch;
    Switch pushNotificationSoundSwitch;
    TextView issueTxtView;
    TextView issueBtnTxt;
    Switch thumbnailSwitch;
    Toolbar toolbar;
    TextView updateTextView;
    TextView suggestTextView;
    TextView suggestBtn;
    FrameLayout updateBtn;
    private TextView termsOfUse;
    private SharedPrefrenceUtils utils;
    private TextView updateStatus;
    private ProgressBar updateProgress;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utils = SharedPrefrenceUtils.getInstance(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_user_preference_setting,null,false);
        init(view);
        loadSettings();
        attachListeners();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void init(View view) {

        thumbnailTxtView = (TextView) view.findViewById(R.id.loadThumbnailTextMessage);
        pushNotificationTxtView = (TextView) view.findViewById(R.id.pushNotificationTextMessage);
        pushNotificationSoundTxtView = (TextView) view.findViewById(R.id.pushNotificationSoundTextMessage);
        pushNotificationSwitch = (Switch) view.findViewById(R.id.pushNotificationSwitch);
        pushNotificationSoundSwitch = (Switch) view.findViewById(R.id.pushNotificationSoundSwitch);
        updateBtn = (FrameLayout) view.findViewById(R.id.UpdateTextFrame);
        issueTxtView = (TextView) view.findViewById(R.id.issuesTextMessage);
        issueBtnTxt = (TextView) view.findViewById(R.id.issueBtn);
        thumbnailSwitch = (Switch) view.findViewById(R.id.loadThumbnailSwitch);
        updateTextView = (TextView) view.findViewById(R.id.UpdateTextMessage);
        suggestTextView = (TextView) view.findViewById(R.id.SuggestTextMessage);
        updateProgress = (ProgressBar) view.findViewById(R.id.updateProgress);
        suggestBtn = (TextView) view.findViewById(R.id.suggestBtn);
        updateStatus = (TextView) view.findViewById(R.id.updateStatus);
        termsOfUse = (TextView) view.findViewById(R.id.termsOfUse);


        Typeface tf = FontManager.getInstance(context).getTypeFace(FontManager.FONT_RALEWAY_REGULAR);
        Typeface materialIconFont = FontManager.getInstance(context).getTypeFace(FontManager.FONT_MATERIAL);
        thumbnailTxtView.setTypeface(tf);
        pushNotificationSoundTxtView.setTypeface(tf);
        pushNotificationTxtView.setTypeface(tf);
        issueTxtView.setTypeface(tf);
        termsOfUse.setTypeface(tf);
        updateTextView.setTypeface(tf);
        suggestTextView.setTypeface(tf);
        issueBtnTxt.setTypeface(materialIconFont);
        suggestBtn.setTypeface(materialIconFont);

    }

    private void loadSettings() {

        // get thumbnail choice
        thumbnailSwitch.setChecked(utils.getOptionsForThumbnailLoad());
        pushNotificationSwitch.setChecked(utils.getOptionsForPushNotification());
        pushNotificationSoundSwitch.setChecked(utils.getOptionsForPushNotificationSound());

        if(utils.getNewVersionAvailibility()){
            updateStatus.setText("Available Version : "+utils.getLatestVersionName());
        }else{
            updateStatus.setText("");
        }


    }

    private void suggestOther() {

        String dndUrl = utils.getNewUpdateUrl();
        String textToShare = "AnyAudio a tool to download/stream Any Audio from Internet. You Can Download it from "+dndUrl;

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, textToShare);
        startActivity(share);

    }

    private void attachListeners() {


        thumbnailSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean state) {
                if (state) {
                    utils.setOptionsForThumbnailLoad(true);
                } else {
                    utils.setOptionsForThumbnailLoad(false);
                }
            }
        });

        pushNotificationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean state) {
                if (state) {
                    utils.setOptionsForPushNotification(true);
                } else {
                    utils.setOptionsForPushNotification(false);
                }
            }
        });

        pushNotificationSoundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean state) {

                if (state) {
                    utils.setOptionsForPushNotificationSound(true);
                } else {
                    utils.setOptionsForPushNotificationSound(false);
                }
            }
        });

        issueBtnTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_SEND);
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
                utils.setDoNotRemindMeAgainForAppUpdate(false);
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

        // start check service and change status
        updateStatus.setText("Checkig.......");
        updateProgress.setVisibility(View.VISIBLE);
        context.startService(new Intent(context, UpdateCheckService.class));


    }

}
