package any.audio.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import any.audio.Adapters.ExploreTopDownAdapter;
import any.audio.Centrals.CentralDataRepository;
import any.audio.Config.Constants;
import any.audio.Models.ExploreItemModel;
import any.audio.Models.ResultMessageObjectModel;
import any.audio.Network.ConnectivityUtils;
import any.audio.R;

/**
 * Created by Ankit on 1/24/2017.
 */

public class ExploreFragment extends Fragment {

    Context context;
    RecyclerView exploreTopDownRecyler;
    ProgressBar progressBar;
    ExploreTopDownAdapter topDownAdapter;
    private TextView progressBarMsgPanel;


    public ExploreFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.explore_fragment,null,false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        exploreTopDownRecyler = (RecyclerView) view.findViewById(R.id.explore_recycler_view);
        exploreTopDownRecyler.setDrawingCacheEnabled(true);
        exploreTopDownRecyler.setHasFixedSize(true);
        exploreTopDownRecyler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        progressBar = (ProgressBar) view.findViewById(R.id.explore_progressBar);
        progressBarMsgPanel = (TextView) view.findViewById(R.id.exploreMessage);
        exploreTopDownRecyler.setLayoutManager(new LinearLayoutManager(context));
        exploreTopDownRecyler.setAdapter(ExploreTopDownAdapter.getInstance(context));

        invokeExploreAction();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //WID: invoke Action for get trending and a handler instance
        topDownAdapter = ExploreTopDownAdapter.getInstance(context);
    }


    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    private void invokeExploreAction(){


        exploreTopDownRecyler.setVisibility(RecyclerView.GONE);
        progressBar.setVisibility(View.VISIBLE);
        progressBarMsgPanel.setVisibility(View.VISIBLE);

        CentralDataRepository repository = CentralDataRepository.getInstance(context);

        if (!ConnectivityUtils.getInstance(context).isConnectedToNet()) {
            exploreTopDownRecyler.setVisibility(RecyclerView.GONE);
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        progressBarMsgPanel.setText("Loading Trending....");
        repository.submitAction(CentralDataRepository.FLAG_FIRST_LOAD, getHandlerInstance());

    }

    private Handler getHandlerInstance(){

        return new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //WID: sets the main view adapter with data and disables progress bar
                progressBar.setVisibility(View.GONE);
                progressBarMsgPanel.setVisibility(View.GONE);

                Log.d("Explore"," Receiving Message");
                progressBar.setVisibility(View.INVISIBLE);
                ResultMessageObjectModel object = (ResultMessageObjectModel) msg.obj;
                ExploreItemModel item = object.data;

                if (object.Status == Constants.MESSAGE_STATUS_OK) {

                    // check if item is empty
                    if (item.getList() != null) {
                        if (item.getList().size() == 0 && topDownAdapter.getItemCount() == 0) {
                            // hide the recycler view and Show Message
                            exploreTopDownRecyler.setVisibility(RecyclerView.GONE);
                            progressBar.setVisibility(View.GONE);
                            progressBarMsgPanel.setVisibility(View.VISIBLE);
                            progressBarMsgPanel.setText("Troubling Getting Data......");

                        } else {

                            exploreTopDownRecyler.setVisibility(RecyclerView.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            progressBarMsgPanel.setVisibility(View.GONE);

                        }
                    }

                    topDownAdapter.addExploreItem(item);

                } else {
                    //TODO: Collect Unexpected Error From CDR(Central Data Repository)
                }

            }
        };
    }

}
