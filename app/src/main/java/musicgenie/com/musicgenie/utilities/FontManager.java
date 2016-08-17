package musicgenie.com.musicgenie.utilities;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Ankit on 8/16/2016.
 */
public class FontManager {
    public static final String FONT_RALEWAY_BOLD = "Raleway-Bold.ttf";
    public static final String FONT_RALEWAY_REGULAR = "Raleway-Regular.ttf";
    public static final String FONT_RALEWAY_SEMIBOLD = "Raleway-Semibold.ttf";
    public static final String FLATICON = "Flaticon.ttf";

    private static Context context;
    private static FontManager mInstance;

    public FontManager(Context context) {
        this.context = context;
    }

    public static FontManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FontManager(context);
        }
        return mInstance;
    }

    public Typeface getTypeFace(String type){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),type);
        return typeface;
    }

}
