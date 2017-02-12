package any.audio.helpers;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Ankit on 2/8/2017.
 */

public class ToastMaker {
    private static Context context;
    private static ToastMaker mInstance;

    public ToastMaker(Context context) {
        this.context = context;
    }

    public static ToastMaker getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ToastMaker(context);
        }
        return mInstance;
    }

    public void toast(final String message){

        //Thread Independent Toasting
        new View(context).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
            }
        });
    }

}
