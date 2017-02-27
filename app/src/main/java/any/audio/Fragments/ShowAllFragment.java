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

import any.audio.Adapters.SearchResultsAdapter;
import any.audio.Centrals.CentralDataRepository;
import any.audio.Config.Constants;
import any.audio.Models.ExploreItemModel;
import any.audio.Models.ResultMessageObjectModel;
import any.audio.Network.ConnectivityUtils;
import any.audio.R;

/**
 * Created by Ankit on 2/21/2017.
 */


public class ShowAllFragment extends Fragment {

    Context context;
    private RecyclerView showAllResultRecycler;
    private ProgressBar progressBar;
    private TextView progressBarMsgPanel;
    private SearchResultsAdapter showAllResultAdapter;
    private SearchResultsAdapter.SearchResultActionListener showAllActionListener;
    private CentralDataRepository repository;
    private String extraa;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.search_fragment,null,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        Log.d("Search"," onViewCreated");

        showAllResultRecycler = (RecyclerView) view.findViewById(R.id.search_recycler_view);
        progressBar = (ProgressBar) view.findViewById(R.id.search_progressBar);
        progressBarMsgPanel = (TextView) view.findViewById(R.id.searchMessage);
        showAllResultRecycler.setLayoutManager(new LinearLayoutManager(context));
        showAllResultRecycler.setAdapter(showAllResultAdapter);
        invokeShowAllAction();

    }



    private void invokeShowAllAction() {


        repository = CentralDataRepository.getInstance(context);
        int _action_to_invoke = CentralDataRepository.FLAG_SHOW_ALL;
        repository.setSectionType(extraa);
        repository.submitAction(_action_to_invoke,getHandlerInstance());

        showAllResultRecycler.setVisibility(RecyclerView.GONE);
        progressBar.setVisibility(View.VISIBLE);
        progressBarMsgPanel.setVisibility(View.VISIBLE);

        if (!ConnectivityUtils.getInstance(context).isConnectedToNet()) {
            showAllResultRecycler.setVisibility(RecyclerView.GONE);
            progressBar.setVisibility(View.INVISIBLE);
            return;
        }

        progressBarMsgPanel.setText("Searching For You...");

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showAllResultAdapter = SearchResultsAdapter.getInstance(context);
        showAllResultAdapter.setActionListener(showAllActionListener);

    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        this.context = context;

    }


    private Handler getHandlerInstance(){

        Handler showAllHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                //WID: sets the main view adapter with data and disables progress bar

                Log.d("Search"," Receiving Message");

                progressBar.setVisibility(View.INVISIBLE);
                ResultMessageObjectModel object = (ResultMessageObjectModel) msg.obj;
                ExploreItemModel item = object.data;

                if (object.Status == Constants.MESSAGE_STATUS_OK) {

                    // check if item is empty
                    if (item.getList() != null) {
                        if (item.getList().size() == 0 && showAllResultAdapter.getItemCount() == 0) {
                            // hide the recycler view and Show Message
                            showAllResultRecycler.setVisibility(RecyclerView.GONE);
                            progressBar.setVisibility(View.GONE);
                            progressBarMsgPanel.setVisibility(View.VISIBLE);
                            progressBarMsgPanel.setText("Troubling Getting Data......");

                        } else {

                            showAllResultRecycler.setVisibility(RecyclerView.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            progressBarMsgPanel.setVisibility(View.GONE);

                        }
                    }

                    showAllResultAdapter.setItemList(item.getList());

                } else {
                    //TODO: Collect Unexpected Error From CDR(Central Data Repository)
                }

            }
        };

        return showAllHandler;
    }

    //set by Home Activity
    public void setActionListener(SearchResultsAdapter.SearchResultActionListener actionListener){
        this.showAllActionListener = actionListener;
    }

    public void setExtraa(String extraa) {
        this.extraa = extraa;
    }

}
