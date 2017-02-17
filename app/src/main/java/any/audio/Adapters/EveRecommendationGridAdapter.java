package any.audio.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Ankit on 2/16/2017.
 */

public class EveRecommendationGridAdapter extends BaseAdapter {

    private static Context context;

    private static EveRecommendationGridAdapter mInstance;

    public EveRecommendationGridAdapter(Context context) {
        this.context = context;
    }

    public static EveRecommendationGridAdapter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new EveRecommendationGridAdapter(context);
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
