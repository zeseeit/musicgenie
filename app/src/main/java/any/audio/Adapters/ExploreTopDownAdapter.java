package any.audio.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arlib.floatingsearchview.util.Util;

import java.util.ArrayList;

import any.audio.Config.Constants;
import any.audio.Models.ExploreItemModel;
import any.audio.R;

import static any.audio.Activity.AnyAudioActivity.anyAudioActivityInstance;

/**
 * Created by Ankit on 1/25/2017.
 */

public class ExploreTopDownAdapter extends RecyclerView.Adapter<ExploreTopDownAdapter.ExploreItemRowViewHolder> {

    String TAG = ExploreTopDownAdapter.class.getSimpleName();
    ArrayList<ExploreItemModel> exploreItemModels;
    private int mLastAnimatedItemPosition = -1;
    private static Context context;
    private static ExploreTopDownAdapter mInstance;


    public ExploreTopDownAdapter(Context context) {

        this.context = context;
        exploreItemModels = new ArrayList<>();
    }

    public static ExploreTopDownAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ExploreTopDownAdapter(context);
        }
        return mInstance;
    }

    public void addExploreItem(ExploreItemModel item) {

        if (item.sectionTitle.equals(Constants.FLAG_RESET_ADAPTER_DATA)) {
            exploreItemModels = new ArrayList<>();
            return;
        }

//        Log.d(TAG,"adding item : "+item.getSectionTitle());
        exploreItemModels.add(item);
        notifyDataSetChanged();

    }

    private void animateItem(View view) {
        view.setTranslationY(Util.getScreenHeight((Activity) view.getContext()));
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }

    @Override
    public ExploreItemRowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(context).inflate(R.layout.explore_item_layout, null, false);
        return new ExploreItemRowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExploreItemRowViewHolder holder, int position) {

        ExploreItemModel exploreItem = exploreItemModels.get(holder.getAdapterPosition());

        //bind values : [header] [recyclerView <--> ]

        String formattedHeader = exploreItem.getSectionTitle().substring(0, 1).toUpperCase() + exploreItem.getSectionTitle().substring(1);
        holder.sectionTitle.setText(formattedHeader);
//         create and attach adapters
        ExploreLeftToRightAdapter adapter = new ExploreLeftToRightAdapter(context);
        adapter.setItemList(exploreItem.getList());
        holder.recyclerViewLeftToRight.setHasFixedSize(true);
        holder.recyclerViewLeftToRight.setDrawingCacheEnabled(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.recyclerViewLeftToRight.setLayoutManager(linearLayoutManager);
        holder.recyclerViewLeftToRight.setAdapter(adapter);

//        if (mLastAnimatedItemPosition < position) {
//            animateItem(holder.itemView);
//            mLastAnimatedItemPosition = position;
//        }

    }

    @Override
    public int getItemCount() {

        return exploreItemModels.size();

    }


    public static class ExploreItemRowViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout wrapper;
        TextView sectionTitle;
        TextView showAll;
        RecyclerView recyclerViewLeftToRight;

        public ExploreItemRowViewHolder(View itemView) {
            super(itemView);

            ///wrapper = (RelativeLayout) itemView.findViewById(R.id.sectionContainer);
            sectionTitle = (TextView) itemView.findViewById(R.id.explore_item_header);
            showAll = (TextView) itemView.findViewById(R.id.explore_item_show_all);
            recyclerViewLeftToRight = (RecyclerView) itemView.findViewById(R.id.explore_left_to_right_recyclerView);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL);
            dividerItemDecoration.setDrawable(context.getResources().getDrawable(R.drawable.vertical_divider));
            recyclerViewLeftToRight.addItemDecoration(dividerItemDecoration);


            showAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ExploreTopDownAdapter adapter = ExploreTopDownAdapter.getInstance(context);
                    int pos = getAdapterPosition();
                    String type = adapter.exploreItemModels.get(pos).sectionTitle;
                    adapter.onShowAll(type);
                }
            });

        }

    }

    private void onShowAll(String type) {
        anyAudioActivityInstance.onShowAll(type);
    }


}
