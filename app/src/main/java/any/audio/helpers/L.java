package any.audio.helpers;

import android.util.Log;

/**
 * Created by Ankit on 16-06-2017.
 */


public class L {

    private static boolean DEBUG = true;
    public static class E {

        public static void m(String tag, String msg) {
            if (DEBUG)
                Log.e(tag, msg);
        }

    }

    public static class D {

        public static void m(String tag, String msg) {
            if (DEBUG)
                Log.d(tag, msg);
        }

    }

}
