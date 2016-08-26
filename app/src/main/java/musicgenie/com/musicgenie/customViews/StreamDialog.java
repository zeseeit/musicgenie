package musicgenie.com.musicgenie.customViews;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import musicgenie.com.musicgenie.R;

/**
 * Created by Ankit on 8/26/2016.
 */
public class StreamDialog extends Dialog {

    private static Context context;
    private static StreamDialog mInstance;

    public StreamDialog(Context context) {
        super(context);
        this.context = context;
    }

    public static StreamDialog getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new StreamDialog(context);
        }
        return mInstance;
    }

    @Override
    public void setContentView(int layoutResID) {
        View myLayout = LayoutInflater.from(context).inflate(R.layout.stream_layout, null);
        super.setContentView(myLayout);
    }


}
