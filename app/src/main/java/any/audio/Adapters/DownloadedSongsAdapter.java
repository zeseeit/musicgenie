package any.audio.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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

import any.audio.Managers.FontManager;
import any.audio.helpers.L;
import any.audio.R;


public class DownloadedSongsAdapter extends ArrayAdapter<String> {

    private static final String TAG = "DownloadedSongsAdapter";
    private static Context context;
    private static DownloadedSongsAdapter mInstance;
    private ArrayList<String> fileList;
    //Views
    private TextView mTitle;
    private ImageView mAlbumArt;
    private TextView overFlowIcon;
    private Typeface tf;

    public DownloadedSongsAdapter(Context context) {
        super(context, 0);
        DownloadedSongsAdapter.context = context;
        fileList = new ArrayList<>();
        tf = FontManager.getInstance(context).getTypeFace(FontManager.FONT_MATERIAL);
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
        overFlowIcon = (TextView) view.findViewById(R.id.secondaryActionDots);
        overFlowIcon.setTypeface(tf);

        mAlbumArt.setImageResource(R.drawable.downloaded);
        String fileName = fileList.get(position);
        mTitle.setText(fileName.substring(fileName.lastIndexOf("/")+1));

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
