package any.audio.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import any.audio.Config.Constants;
import any.audio.Managers.FontManager;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;

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
    Switch thumbnailSwitch;
    private TextView termsOfUse;
    private SharedPrefrenceUtils utils;


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
        thumbnailSwitch = (Switch) view.findViewById(R.id.loadThumbnailSwitch);
        termsOfUse = (TextView) view.findViewById(R.id.termsOfUse);

        Typeface tf = FontManager.getInstance(context).getTypeFace(FontManager.FONT_RALEWAY_REGULAR);
        thumbnailTxtView.setTypeface(tf);
        pushNotificationSoundTxtView.setTypeface(tf);
        pushNotificationTxtView.setTypeface(tf);
        termsOfUse.setTypeface(tf);

    }

    private void loadSettings() {

        // get thumbnail choice
        thumbnailSwitch.setChecked(utils.getOptionsForThumbnailLoad());
        pushNotificationSwitch.setChecked(utils.getOptionsForPushNotification());
        pushNotificationSoundSwitch.setChecked(utils.getOptionsForPushNotificationSound());


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


}
