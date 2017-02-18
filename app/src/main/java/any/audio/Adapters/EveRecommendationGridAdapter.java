package any.audio.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import any.audio.Models.EveSuggestionsItemModel;
import any.audio.Models.PlaylistItem;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;

/**
 * Created by Ankit on 2/16/2017.
 */

public class EveRecommendationGridAdapter extends BaseAdapter {

    private static Context context;

    private static EveRecommendationGridAdapter mInstance;
    private ArrayList<EveSuggestionsItemModel> suggestionsItemModels;
    public EveRecommendationGridAdapter(Context context) {
        this.context = context;
        suggestionsItemModels = new ArrayList<>();
    }

    public static EveRecommendationGridAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new EveRecommendationGridAdapter(context);

        }
        return mInstance;
    }

    @Override
    public int getCount() {
        return suggestionsItemModels.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {

        EveSuggestionsViewHolder viewHolder=null;

        if(convertView==null){

            viewHolder = new EveSuggestionsViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.eve_recommendation_item_layout,container,false);
            viewHolder.thumbnail = (SimpleDraweeView) convertView.findViewById(R.id.eveItemThumbnail);
            viewHolder.title = (TextView) convertView.findViewById(R.id.eve_suggestion_item_title);
            viewHolder.artist = (TextView) convertView.findViewById(R.id.eve_suggestion_item_artist);
            viewHolder.wrapper = (RelativeLayout) convertView.findViewById(R.id.eve_card_top_wrapper);
            // size fixations

            int widthPx = (int) SharedPrefrenceUtils.getInstance(context).getScreenWidthPx();
            int thumbnailHeight = (int) (0.56*widthPx);
            LinearLayout.LayoutParams thumbnailParams = new LinearLayout.LayoutParams(widthPx,thumbnailHeight);
            viewHolder.wrapper.setLayoutParams(thumbnailParams);

            convertView.setTag(viewHolder);

        }else{
            viewHolder = (EveSuggestionsViewHolder) convertView.getTag();
        }

        bindViewData(viewHolder,suggestionsItemModels.get(position));

        return convertView;
    }

    private void bindViewData(EveSuggestionsViewHolder viewHolder,EveSuggestionsItemModel data) {

        viewHolder.title.setText(data.title);
        viewHolder.artist.setText(data.artist);
        viewHolder.thumbnail.setImageURI(data.thumbnailUrl);

    }

    public void setSuggestionsList(ArrayList<PlaylistItem> items) {

        for(PlaylistItem item:items){
            suggestionsItemModels.add(new EveSuggestionsItemModel(item.getTitle(),item.getUploader(),getImageUrl(item.getYoutubeId())));
        }

        notifyDataSetChanged();

    }


    private String getImageUrl(String vid) {
        //return "https://i.ytimg.com/vi/kVgKfScL5yk/hqdefault.jpg";
        return "https://i.ytimg.com/vi/" + vid + "/hqdefault.jpg";  // additional query params => ?custom=true&w=240&h=256
    }


    public static class EveSuggestionsViewHolder{
        public TextView title;
        public TextView artist;
        public SimpleDraweeView thumbnail;
        public RelativeLayout wrapper;

    }

}
