package any.audio.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import any.audio.Models.DownloadedItemModel;
import any.audio.Models.DownloadingItemModel;
import any.audio.R;
import any.audio.helpers.MetaDataHelper;


public class DownloadedItemsAdapter extends ArrayAdapter<String> {

    private static final String TAG = "DownloadedSongsAdapter";
    private static Context context;
    private MetaDataHelper metaDataHelper;
    private static DownloadedItemsAdapter mInstance;
    //Views
    private TextView mTitle;
    private ImageView mAlbumArt;
    private DownloadedItemDeleteListener deleteListener;
    private ArrayList<DownloadedItemModel> downloadedListItems;

    public DownloadedItemsAdapter(Context context) {
        super(context, 0);
        DownloadedItemsAdapter.context = context;
        downloadedListItems = new ArrayList<>();
    }

    public static DownloadedItemsAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DownloadedItemsAdapter(context);
        }
        return mInstance;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DownloadedItemViewHolder viewHolder = null;

        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.downloaded_items_layout,parent,false);
            viewHolder.thumbnail = (ImageView) convertView.findViewById(R.id.downloaded_item_thumb);
            viewHolder.contentLength = (TextView) convertView.findViewById(R.id.downloaded_item_duration);
            viewHolder.title = (TextView) convertView.findViewById(R.id.downloaded_item_title);
            viewHolder.artist = (TextView) convertView.findViewById(R.id.downloaded_item_artist);
            viewHolder.deleteBtn = (TextView) convertView.findViewById(R.id.deleteDownloadedItem);
            viewHolder.infoWrapper = (RelativeLayout) convertView.findViewById(R.id.downloaded_item_info_wrapper);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (DownloadedItemViewHolder) convertView.getTag();
        }

        bind(viewHolder, position);
        return convertView;
    }

    private class DownloadedItemViewHolder{

        public ImageView thumbnail;
        public TextView title;
        public TextView deleteBtn;
        public TextView contentLength;
        public TextView artist;
        public RelativeLayout infoWrapper;

    }

    private void bind(DownloadedItemViewHolder viewHolder,final int position) {

        //bind values
        viewHolder.thumbnail.setImageBitmap(metaDataHelper.getBitmap(downloadedListItems.get(position).title));
        viewHolder.title.setText(downloadedListItems.get(position).title);
        viewHolder.artist.setText(metaDataHelper.getArtist(downloadedListItems.get(position).title));
        viewHolder.contentLength.setText(metaDataHelper.getArtist(downloadedListItems.get(position).title));


        //attach listeners

        viewHolder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(deleteListener!=null){
                    deleteListener.onDelete(position);
                }
            }
        });

        viewHolder.infoWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // send Intent to Music Handle capable
                Intent audioIntent = new Intent();
                audioIntent.setAction(Intent.ACTION_VIEW);
                audioIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                File f = new File(downloadedListItems.get(position).title);
                Uri uri = Uri.fromFile(f);
                audioIntent.setDataAndType(uri,"audio/*");
                context.startActivity(audioIntent);

            }
        });

    }

    @Override
    public int getCount() {
        return downloadedListItems.size();
    }

    public void setDownloadingList(ArrayList<DownloadedItemModel> downloadedItemModelArrayList){
        this.downloadedListItems = downloadedItemModelArrayList;
        notifyDataSetChanged();
    }

    public void setOnDownloadCancelListener(DownloadedItemDeleteListener listener){
        this.deleteListener = listener;
    }

    public interface DownloadedItemDeleteListener{
        void onDelete(int index);
    }

}
