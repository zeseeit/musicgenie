package any.audio.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;

import any.audio.Config.Constants;
import any.audio.SharedPreferences.SharedPrefrenceUtils;

/**
 * Created by Ankit on 2/1/2017.
 */

public class ScreenDimension {

    private static Context context;
    private static ScreenDimension mInstance;

    public ScreenDimension(Context context) {
        this.context = context;
    }

    public static ScreenDimension getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ScreenDimension(context);
        }
        return mInstance;
    }

    private int screenMode() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((AppCompatActivity)context).getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float yInches = metrics.heightPixels / metrics.ydpi;
        float xInches = metrics.widthPixels / metrics.xdpi;

        double diagonal = Math.sqrt(yInches * yInches + xInches * xInches);
        if (diagonal > 6.5) {
            return Constants.SCREEN_MODE_TABLET;
        } else {
            return Constants.SCREEN_MODE_MOBILE;
        }
    }

    public void init(){

        recordScreenProp(getExploreColumnCount());

    }

    private void recordScreenProp(int cols) {

        Display display = ((AppCompatActivity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        float density = context.getResources().getDisplayMetrics().density;
        float screenWidthDP = outMetrics.widthPixels / density;
        Log.i("Home", "Screen Width " + screenWidthDP);

        Resources r = context.getResources();
        float card_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, screenWidthDP, r.getDisplayMetrics());
        float space_px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, r.getDisplayMetrics());
        float songCardWidth = 0;

        if (cols == 2) {
            songCardWidth = (card_px - space_px * 3) / 2; // 3*4dp for spaces
        }
        if (cols == 3) {
            songCardWidth = (card_px - space_px * 4) / 3; // 4 * 4dp for spaces
        }

        SharedPrefrenceUtils.getInstance(context).setSongCardWidthDp(songCardWidth);
        SharedPrefrenceUtils.getInstance(context).setCols(cols);
        SharedPrefrenceUtils.getInstance(context).setAdWidth(screenWidthDP);

    }


    private int getExploreColumnCount(){
        return screenMode()==Constants.SCREEN_MODE_MOBILE?2:3;
    }


}
