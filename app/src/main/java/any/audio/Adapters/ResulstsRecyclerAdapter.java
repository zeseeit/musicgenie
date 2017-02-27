package any.audio.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.arlib.floatingsearchview.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import any.audio.Config.Constants;
import any.audio.Interfaces.FeatureRequestListener;
import any.audio.Managers.FontManager;
import any.audio.Models.BaseSong;
import any.audio.Models.ItemModel;
import any.audio.Models.ExploreItemModel;
import any.audio.Models.ViewTypeModel;
import any.audio.Network.ConnectivityUtils;
import any.audio.R;
import any.audio.SharedPreferences.SharedPrefrenceUtils;
import any.audio.SharedPreferences.StreamSharedPref;
import any.audio.helpers.FileNameReformatter;
import any.audio.helpers.PlaylistGenerator;

public class ResulstsRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SONG = 0;
    private static final int TYPE_SECTION_TITLE = 1;
    private static final String TAG = "TrendingRecylerAdapter";
    private static Context context;
    private static ResulstsRecyclerAdapter mInstance;
    private ArrayList<ViewTypeModel> typeViewList;
    private ArrayList<BaseSong> songs;
    private int orientation;
    private int screenMode;
    private int viewToInflate;
    private FeatureRequestListener featureRequestListener;
    private int mLastAnimatedItemPosition = -1;
    private static final int TYPE_ADS = 2;
    private static final int TYPE_BLANK = 3;

    public ResulstsRecyclerAdapter(Context context) {
        ResulstsRecyclerAdapter.context = context;
        typeViewList = new ArrayList<>();
        songs = new ArrayList<>();
    }

    public static ResulstsRecyclerAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ResulstsRecyclerAdapter(context);
        }
        return mInstance;
    }

    /**
     * @param inList : list of items to be appended to existing list
     *               new items are appended and notifies
     * @param type   : section to which list belongs to
     */
    private void appendSongs(String type, ArrayList<ItemModel> inList) {

        //   L.m("RADP","adding type "+type);
        addItem(null, type);
        for (int i = 0; i < inList.size(); i++) {
            //     L.m("RADP","adding song "+inList.get(i).Title);
            addItem(inList.get(i), "");
        }
        notifyDataSetChanged();
    }


    public void enque(ExploreItemModel exploreItemModel) {

        if (exploreItemModel.sectionTitle.equals(Constants.FLAG_RESET_ADAPTER_DATA)) {
            //reset previous data
            //   L.m("Result Adapter[enque]","Data wiping out!");
            resetData();
        } else {
            //append
            // L.m("Result Adapter[enque] ","Section Title : "+sectionModel.sectionTitle);
            appendSongs(exploreItemModel.sectionTitle, exploreItemModel.getList());
        }

    }


    private void addItem(BaseSong song, String section) {   //     create view list

        int index = typeViewList.size();
        int adsPosition = (this.screenMode==Constants.SCREEN_MODE_MOBILE)?4:5;

        if(index==0) {
            typeViewList.add(new ViewTypeModel(TYPE_BLANK, "", -1));
            songs.add(null);
        }

        if (index == adsPosition) {
            typeViewList.add(new ViewTypeModel(TYPE_ADS, "", -1));
            songs.add(null);
        }

        if (section.equals("")) {
            int s = songs.size();
            Log.d("ResultListAdapter","adding songs at index "+s);
            songs.add(song);
            typeViewList.add(new ViewTypeModel(TYPE_SONG, " ", s));
        } else {
            //L.m("Result Adapter"," TypeViewList Addition ->"+section);
            songs.add(null);
            typeViewList.add(new ViewTypeModel(TYPE_SECTION_TITLE, section, -1));
        }

        //logListValues(typeViewList);

    }

    private void logListValues(ArrayList<ViewTypeModel> list) {
        //L.m("Result Adapter","Logging Values");
        for (int i = 0; i < list.size(); i++) {
            //  L.m("Result Adapter","Title = "+list.get(i).sectionTitle);
        }
    }

    private void resetData() {
        typeViewList = new ArrayList<>();
        songs = new ArrayList<>();
        //       notifyDataSetChanged();
    }

    private void log(String s) {
        Log.d(TAG, "log>>" + s);
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    private boolean isPortrait(int orientation) {
        return orientation % 2 == 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        switch (viewType) {

            case TYPE_SECTION_TITLE:
                int hvti = getHeaderViewToInflate();
                view = LayoutInflater.from(context).inflate(hvti, parent, false);
                //          L.m("Result Adapter"," sectionTitle");
                return new SectionTitleViewHolder(view);
            case TYPE_SONG:
                int vti = getViewToInflate();   // getView depending on screen screen sizes
                view = LayoutInflater.from(context).inflate(vti, parent, false);
//            log("returning song item");
                return new SongViewHolder(view);

            case TYPE_ADS:

                int adi = getAdLayoutToInflate();   // getView depending on screen screen sizes
                view = LayoutInflater.from(context).inflate(adi, parent, false);
                return new AdViewHolder(view);

            case TYPE_BLANK:

                int bla = getBlankHeaderViewToInflate();   // getView depending on screen screen sizes
                view = LayoutInflater.from(context).inflate(bla, parent, false);
                return new BlankHeaderViewHolder(view);

            default:
                return null;
        }
//
//        if (viewType == TYPE_SECTION_TITLE) {
//            int hvti = getHeaderViewToInflate();
//            view = LayoutInflater.from(context).inflate(hvti, parent, false);
//            //          L.m("Result Adapter"," sectionTitle");
//            return new SectionTitleViewHolder(view);
//        } else {
//            int vti = getViewToInflate();   // getView depending on screen screen sizes
//            view = LayoutInflater.from(context).inflate(vti, parent, false);
////            log("returning song item");
//            return new SongViewHolder(view);
//        }
    }

    private int getViewToInflate() {

        return R.layout.song_card_new;

    }

    private int getHeaderViewToInflate() {

        return R.layout.section_header_layout;

    }

    private int getBlankHeaderViewToInflate() {

        int _temp_header_viewID = R.layout.results_blank_header;
        return _temp_header_viewID;

    }

    private int getAdLayoutToInflate() {
        return R.layout.adview_item;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Typeface fontawesome = FontManager.getInstance(context).getTypeFace(FontManager.FONT_AWESOME);
        Typeface ralewayTfRegular = FontManager.getInstance(context).getTypeFace(FontManager.FONT_RALEWAY_REGULAR);

        if (holder instanceof AdViewHolder) {

            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);

            MobileAds.initialize(context, "ca-app-pub-6848758347511349/3530419313");
            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            ((AdViewHolder) holder).adView.loadAd(adRequest);

        }

        if (holder instanceof SongViewHolder) {

            // bind section data
            //  log("binding song " + position);

            Log.d("ResultListAdapter","bind songs index "+position);

            BaseSong song = songs.get(typeViewList.get(position).index);
            ((SongViewHolder) holder).title.setText(song.Title);
            ((SongViewHolder) holder).uploader.setText(song.UploadedBy);
            ((SongViewHolder) holder).views.setText(song.UserViews);
            //            ((SongViewHolder) holder).popMenuBtn.setText("\uF142");
            ((SongViewHolder) holder).content_length.setText(song.TrackDuration);
            // loads thumbnail in async fashion
            if (connected() && SharedPrefrenceUtils.getInstance(context).getOptionsForThumbnailLoad())
                Picasso.with(context)
                        .load(song.Thumbnail_url)
                        .into(((SongViewHolder) holder).thumbnail);

            // setting typeface to fonta
            ((SongViewHolder) holder).downloadBtn.setTypeface(fontawesome);
            ((SongViewHolder) holder).uploader_icon.setTypeface(fontawesome);
            ((SongViewHolder) holder).views_icon.setTypeface(fontawesome);
            //            ((SongViewHolder) holder).popMenuBtn.setTypeface(fontawesome);
            //setting typeface to raleway
            ((SongViewHolder) holder).title.setTypeface(ralewayTfRegular);
            ((SongViewHolder) holder).content_length.setTypeface(ralewayTfRegular);
            ((SongViewHolder) holder).uploader.setTypeface(ralewayTfRegular);
            ((SongViewHolder) holder).views.setTypeface(ralewayTfRegular);

        }

        if(holder instanceof SectionTitleViewHolder){
            // binnd song data
            //log("binding header " + position);
            StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
            layoutParams.setFullSpan(true);
            String section_format = typeViewList.get(position).sectionTitle.substring(0, 1).toUpperCase() + typeViewList.get(position).sectionTitle.substring(1);
            ((SectionTitleViewHolder) holder).sectionTitle.setText(section_format);
            ((SectionTitleViewHolder) holder).sectionTitle.setTypeface(ralewayTfRegular);
        }

        if (mLastAnimatedItemPosition < position) {
            animateItem(holder.itemView);
            mLastAnimatedItemPosition = position;
        }

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
    public int getItemCount() {
        return typeViewList.size();
    }

    @Override
    public int getItemViewType(int position) {

        return typeViewList.get(position).viewType;

    }

    public void setOnFeatureRequestListener(FeatureRequestListener listener) {
        this.featureRequestListener = listener;
    }

    public void requestDownload(final String video_id, final String file_name) {

        if (this.featureRequestListener != null)
            this.featureRequestListener.onTaskTapped(Constants.FEATURE_DOWNLOAD, video_id, file_name);

    }

    public void requestStream(final String video_id, final String file_name) {

        if (this.featureRequestListener != null)
            this.featureRequestListener.onTaskTapped(Constants.FEATURE_STREAM, video_id, file_name);

    }

    public void setScreenMode(int mode) {
        this.screenMode = mode;
    }

    private boolean connected() {
        return ConnectivityUtils.getInstance(context).isConnectedToNet();
    }

    public static class SectionTitleViewHolder extends RecyclerView.ViewHolder {

        TextView sectionTitle;

        public SectionTitleViewHolder(View itemView) {
            super(itemView);
            sectionTitle = (TextView) itemView.findViewById(R.id.section_title);
        }
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        ProgressDialog progressDialoge;
        TextView downloadBtn;
        TextView uploader_icon;
        TextView views_icon;
        TextView popMenuBtn;
        TextView content_length;
        TextView uploader;
        TextView streamBtn;
        TextView title;
        TextView views;
        ImageView thumbnail;
        CardView songCard;


        public SongViewHolder(View itemView) {
            super(itemView);

            songCard = (CardView) itemView.findViewById(R.id.song_card);
            int widthPx = (int) SharedPrefrenceUtils.getInstance(context).getScreenWidthPx();
            int thumbnailHeight = (int) (0.75*widthPx);
            FrameLayout.LayoutParams thumbnailParams = new FrameLayout.LayoutParams(widthPx,thumbnailHeight);

            songCard.setLayoutParams(new RelativeLayout.LayoutParams(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT));

            Typeface fontawesome = FontManager.getInstance(context).getTypeFace(FontManager.FONT_AWESOME);
            Typeface ralewayTfRegular = FontManager.getInstance(context).getTypeFace(FontManager.FONT_RALEWAY_REGULAR);
            Typeface ralewayTfBold = FontManager.getInstance(context).getTypeFace(FontManager.FONT_RALEWAY_BOLD);
            Typeface material = FontManager.getInstance(context).getTypeFace(FontManager.FONT_MATERIAL);

            // material
            downloadBtn = (TextView) itemView.findViewById(R.id.download_btn_card);
            uploader_icon = (TextView) itemView.findViewById(R.id.uploader_icon);
            views_icon = (TextView) itemView.findViewById(R.id.views_icon);
            // popMenuBtn = (TextView) itemView.findViewById(R.id.popUpMenuIcon);
            thumbnail = (ImageView) itemView.findViewById(R.id.Videothumbnail);
            thumbnail.setLayoutParams(thumbnailParams);
            streamBtn = (TextView) itemView.findViewById(R.id.stream_btn_card);

            streamBtn.setTypeface(material);
            downloadBtn.setTypeface(fontawesome);
            uploader_icon.setTypeface(fontawesome);
            views_icon.setTypeface(fontawesome);
            //popMenuBtn.setTypeface(fontawesome);
            // regular raleway
            content_length = (TextView) itemView.findViewById(R.id.song_time_length);
            uploader = (TextView) itemView.findViewById(R.id.uploader_name);
            views = (TextView) itemView.findViewById(R.id.views_text);
            title = (TextView) itemView.findViewById(R.id.song_title);
            title.setTypeface(ralewayTfBold);
            content_length.setTypeface(ralewayTfRegular);
            uploader.setTypeface(ralewayTfRegular);
            views.setTypeface(ralewayTfRegular);


            // attach listener
            downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ResulstsRecyclerAdapter adapter = ResulstsRecyclerAdapter.getInstance(context);
                    int pos = getAdapterPosition() - 1;
                    String v_id = adapter.songs.get(pos).Video_id;
                    String file_name = FileNameReformatter.getInstance(context).getFormattedName(adapter.songs.get(pos).Title);
                    adapter.requestDownload(v_id, file_name);

                }
            });

            streamBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ResulstsRecyclerAdapter adapter = ResulstsRecyclerAdapter.getInstance(context);
                    int pos = getAdapterPosition();
                    Log.d("ResultListAdapter","stream req for index "+pos);
                    String v_id = adapter.songs.get(pos).Video_id;

                    PlaylistGenerator.getInstance(context).preparePlaylist(v_id);

                    String file_name = adapter.songs.get(pos).Title;
                    String thumb_uri = adapter.songs.get(pos).Thumbnail_url;
                    String subTitle = adapter.songs.get(pos).UploadedBy;
                    StreamSharedPref.getInstance(context).setStreamTitle(file_name);
                    Log.d("StreamHome","v_id "+v_id);
                    Log.d("StreamingHome", " setting thumb uri " + thumb_uri);
                    StreamSharedPref.getInstance(context).setStreamThumbnailUrl(thumb_uri);
                    StreamSharedPref.getInstance(context).setStreamSubTitle(subTitle);
                    adapter.requestStream(v_id, file_name);

                }
            });


        }


    }

    public static class AdViewHolder extends RecyclerView.ViewHolder {

        AdView adView;

        public AdViewHolder(View itemView) {
            super(itemView);
            adView = (AdView) itemView.findViewById(R.id.adView);

        }
    }

    public static class BlankHeaderViewHolder extends RecyclerView.ViewHolder {

        public BlankHeaderViewHolder(View itemView) {
            super(itemView);

        }
    }

}
