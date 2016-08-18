package musicgenie.com.musicgenie.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by Ankit on 8/18/2016.
 */
public class TrendingItemsGridAdapter extends BaseAdapter {

    private static Context context;
    private static TrendingItemsGridAdapter mInstance;

    public TrendingItemsGridAdapter(Context context) {
        this.context = context;
    }

    public static TrendingItemsGridAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TrendingItemsGridAdapter(context);
        }
        return mInstance;
    }

    @Override
    public int getCount() {
        return 0;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
