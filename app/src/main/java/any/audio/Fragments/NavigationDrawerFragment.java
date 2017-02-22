package any.audio.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import any.audio.Activity.AnyAudioActivity;
import any.audio.Adapters.NavigationListAdapter;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.helpers.ToastMaker;

import static any.audio.Activity.AnyAudioActivity.anyAudioActivityInstance;

/**
 * Created by Ankit on 2/22/2017.
 */

public class NavigationDrawerFragment extends Fragment {

    private Context context;
    private ListView listView;
    private NavigationListAdapter navigationListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.navigation_drawer_fragment,null,false);
        listView = (ListView) v.findViewById(R.id.navigationListView);
        navigationListAdapter = new NavigationListAdapter(context);
        int selected = SharedPrefrenceUtils.getInstance(context).getSelectedNavIndex();
        navigationListAdapter.updateNavState(selected,true);
        listView.setAdapter(navigationListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPrefrenceUtils.getInstance(context).setSelectedNavIndex(i);
                ((NavigationListAdapter) adapterView.getAdapter()).updateNavState(i,true);
                int fragment = -1;
                String title = "";
                switch (i){
                    case 0:

                        fragment = AnyAudioActivity.FRAGMENT_EXPLORE;
                        title = "EXPLORE";
                        break;

                    case 1:

                        fragment = AnyAudioActivity.FRAGMENT_SEARCH;
                        title = "SEARCH";
                        break;

                    case 2:

                        fragment = AnyAudioActivity.FRAGMENT_DOWNLOADS;
                        title = "AnyAudio";
                        break;

                    case 3:

                        fragment = AnyAudioActivity.FRAGMENT_SETTINGS;
                        title = "SETTINGS";
                        break;

                    case 4:

                        fragment = AnyAudioActivity.FRAGMENT_ABOUT_US;
                        title = "About Us";
                        break;

                    case 5:

                        fragment = AnyAudioActivity.FRAGMENT_UPDATES;
                        title = "Updates";

                        break;
                }

                if(!SharedPrefrenceUtils.getInstance(context).isFirstSearchDone() && fragment==AnyAudioActivity.FRAGMENT_SEARCH){
                    Toast.makeText(context,"You Haven`t Searched Yet !",Toast.LENGTH_SHORT).show();
                }else {
                    anyAudioActivityInstance.onNavItemSelected(fragment, title);
                }

            }
        });

        return v;


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
}
