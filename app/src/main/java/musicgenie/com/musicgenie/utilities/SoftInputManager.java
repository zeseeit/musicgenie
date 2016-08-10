package musicgenie.com.musicgenie.utilities;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Ankit on 8/8/2016.
 */
public class SoftInputManager {
    private static Context context;
    private static SoftInputManager mInstance;

    public SoftInputManager(Context context) {
        this.context = context;
    }

    public static SoftInputManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SoftInputManager(context);
        }
        return mInstance;
    }

    public void hideKeyboard(View v){
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(v.getWindowToken(),0);

    }
}
