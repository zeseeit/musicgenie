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

    private LayoutInflater layoutInflater;
    private Context context;
    private int[] layouts;

    public TourPagerAdapter(Context context) {
        this.context = context;
        layouts = new int[]{
                R.layout.first_tour_page,
                R.layout.second_tour_page,
                R.layout.third_tour_page,
                R.layout.fourth_tour_page
        };
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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