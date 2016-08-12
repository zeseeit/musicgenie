package musicgenie.com.musicgenie.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import musicgenie.com.musicgenie.R;
import musicgenie.com.musicgenie.adapters.LiveDownloadListAdapter;
import musicgenie.com.musicgenie.handlers.TaskHandler;
import musicgenie.com.musicgenie.models.DownloadTaskModel;
import musicgenie.com.musicgenie.utilities.SharedPrefrenceUtils;


public class ActiveTaskFragment extends Fragment {


    private static final String TAG = "ActiveTaskFragment";
    private ListView liveDownloadListView;
    private LiveDownloadListAdapter adapter;

    public ActiveTaskFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView =inflater.inflate(R.layout.fragment_active_task, container, false);
        liveDownloadListView = (ListView) fragmentView.findViewById(R.id.liveDownloadListView);

        adapter = new LiveDownloadListAdapter(getActivity());
        adapter.setDownloadingList(getTasksList());
        liveDownloadListView.setAdapter(adapter);

        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private ArrayList<DownloadTaskModel> getTasksList(){
        ArrayList<DownloadTaskModel> list = new ArrayList<>();
        //get tasks list from taskhandler
        //get title from sf
        ArrayList<String> taskIDs = TaskHandler.getInstance(getActivity()).getTaskSequence();
        for(String t_id : taskIDs) {
            String title = SharedPrefrenceUtils.getInstance(getActivity()).getTaskTitle(t_id);
            list.add(new DownloadTaskModel(title,0,t_id));
        }
        return list;
    }

    private void resetDownloadingList(){
        adapter.setDownloadingList(getTasksList());
        liveDownloadListView.setAdapter(adapter);
    }

    private void updateItem(int position){
        int start = liveDownloadListView.getFirstVisiblePosition();
        int end = liveDownloadListView.getLastVisiblePosition();


        if(start<=position && end>=position){
            log("updating "+position);

        }

    }

    public void log(String msg){
        Log.d(TAG,msg);
    }
}
