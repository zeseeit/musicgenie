package any.audio.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import any.audio.Managers.FontManager;
import any.audio.Models.NavItem;
import any.audio.R;

/**
 * Created by Ankit on 2/22/2017.
 */

public class NavigationListAdapter extends ArrayAdapter<NavItem> {

    Context context;
    private int selectedIndex = 0;
    private Typeface tf;

    public NavigationListAdapter(Context context) {
        super(context, 0);
        this.context = context;
        tf = FontManager.getInstance(context).getTypeFace(FontManager.FONT_MATERIAL);
    }

    public void updateNavState(int index, boolean isSelected) {

        if (selectedIndex!=index) {
            selectedIndex = index;
        }
        notifyDataSetInvalidated();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NavItemViewHolder viewHolder = null;

        if(convertView==null){
            viewHolder = new NavItemViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.navigation_item_layout,parent,false);
            viewHolder.icon = (TextView) convertView.findViewById(R.id.item_icon);
            viewHolder.title = (TextView) convertView.findViewById(R.id.item_title);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (NavItemViewHolder) convertView.getTag();
        }

        viewHolder.icon.setTypeface(tf);
        viewHolder.icon.setText(NavItem.icons[position]);
        viewHolder.title.setText(NavItem.titles[position]);

        // setting selection
        if(position==selectedIndex){

            viewHolder.icon.setTextColor(context.getResources().getColor(R.color.AnyAudioPrimaryColor));
            viewHolder.title.setTextColor(context.getResources().getColor(R.color.AnyAudioPrimaryColor));

        }else{
            viewHolder.icon.setTextColor(context.getResources().getColor(R.color.AnyAudioGrey));
            viewHolder.title.setTextColor(context.getResources().getColor(R.color.AnyAudioGrey));
        }

        convertView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                view.getBackground().setHotspot(motionEvent.getX(), motionEvent.getY());
                return false;

            }
        });


        return convertView;
    }

    public static class NavItemViewHolder{

        public TextView icon;
        public TextView title;

    }

    @Override
    public int getCount() {
        return NavItem.icons.length;
    }
}
