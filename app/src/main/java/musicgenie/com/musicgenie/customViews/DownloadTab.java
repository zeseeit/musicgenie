package musicgenie.com.musicgenie.customViews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import musicgenie.com.musicgenie.R;
import musicgenie.com.musicgenie.utilities.FontManager;

/**
 * Created by Ankit on 8/17/2016.
 */
public class DownloadTab extends TabLayout{

    private Typeface mTypeface;
    private Context context;
    public DownloadTab(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public DownloadTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DownloadTab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mTypeface = FontManager.getInstance(context).getTypeFace(FontManager.FLATICON);
    }

    @Override
    public void addTab(Tab tab) {
        super.addTab(tab);
        ViewGroup mainView = (ViewGroup) getChildAt(0);
        ViewGroup tabView = (ViewGroup) mainView.getChildAt(tab.getPosition());

        int tabChildCount = tabView.getChildCount();
        for (int i = 0; i < tabChildCount; i++) {
            View tabViewChild = tabView.getChildAt(i);
            if (tabViewChild instanceof TextView) {
                ((TextView) tabViewChild).setTypeface(mTypeface,Typeface.BOLD);
                ((TextView) tabViewChild).setHeight(60);
                ((TextView) tabViewChild).setTextSize(TypedValue.COMPLEX_UNIT_SP,60);
                ((TextView) tabViewChild).setTextColor(getResources().getColor(R.color.White));
            }
        }
    }

    public void setUpTab(DownloadTab tab){
        String downloadingTab = " \uF253 ";
        String downloadedTab =" \uF245 ";
        addTab(tab.newTab().setText(downloadingTab));
        addTab(tab.newTab().setText(downloadedTab));

    }
}
