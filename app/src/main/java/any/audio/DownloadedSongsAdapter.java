package any.audio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


public class DownloadedSongsAdapter extends ArrayAdapter<String> {

    private static final String TAG = "DownloadedSongsAdapter";
    private static Context context;
    private static DownloadedSongsAdapter mInstance;
    private ArrayList<String> fileList;
    //Views
    private TextView mTitle;
    private TextView mExtraMeta;
    private TextView mOverflowBtn;
    private ImageView mAlbumArt;


    public DownloadedSongsAdapter(Context context) {
        super(context, 0);
        DownloadedSongsAdapter.context = context;
        fileList = new ArrayList<>();
    }

    public static DownloadedSongsAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DownloadedSongsAdapter(context);
        }
        return mInstance;
    }

    public void setItemList(ArrayList<String> list) {
        this.fileList = list;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View tempView = convertView;
        if (tempView == null) {
            tempView = LayoutInflater.from(context).inflate(R.layout.downloaded_item, parent, false);
        }
        bind(tempView, position);
        return tempView;
    }

    private void bind(View view,final int position) {

        // ins
        mTitle = (TextView) view.findViewById(R.id.downloaded_item_title);
        mAlbumArt = (ImageView) view.findViewById(R.id.downloaded_item_thumb);
        mExtraMeta = (TextView) view.findViewById(R.id.downloaded_item_extra_meta);
        mOverflowBtn = (TextView) view.findViewById(R.id.downloaded_item_overflow_icon);

        final android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(fileList.get(position));

        byte[] data = mmr.getEmbeddedPicture();

        if (data != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
            mAlbumArt.setImageBitmap(bmp);
        } else {
            mAlbumArt.setImageResource(R.drawable.head);
        }

        mExtraMeta.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
        mTitle.setText(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));

        L.m("Downloaded"," title "+mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // send Intent to Music Handle capable
                Intent audioIntent = new Intent();
                audioIntent.setAction(Intent.ACTION_VIEW);
                audioIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                File f = new File(fileList.get(position));
                Uri uri = Uri.fromFile(f);
                audioIntent.setDataAndType(uri,"audio/*");
                context.startActivity(audioIntent);

            }
        });

    }

    @Override
    public int getCount() {
        return fileList.size();
    }

}
