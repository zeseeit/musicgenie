package any.audio.Adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import any.audio.R;

/**
 * Created by Ankit on 1/12/2017.
 */
public class TourPagerAdapter extends PagerAdapter {

    private Context context;
    private int[] layouts;

    public TourPagerAdapter(Context context) {
        this.context = context;
        layouts = new int[]{
                R.layout.tour_first,
                R.layout.tour_second,
                R.layout.tour_third,
                R.layout.tour_fourth
        };
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(layouts[position], container, false);
        container.addView(view);

        return view;
    }

    @Override
    public int getCount() {
        return layouts.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }
}