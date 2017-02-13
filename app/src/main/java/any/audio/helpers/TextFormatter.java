package any.audio.helpers;

import android.content.Context;

/**
 * Created by Ankit on 2/8/2017.
 */

public class TextFormatter {

    private static Context context;
    private static TextFormatter mInstance;

    public TextFormatter(Context context) {
        this.context = context;
    }

    public static TextFormatter getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new TextFormatter(context);
        }
        return mInstance;
    }

    public String reformat(String string){

        string.replace("&amp;", "&").replace("&quot;", "").replace("&quote;","");

        return string;
    }


}
