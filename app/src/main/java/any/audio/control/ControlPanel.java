package any.audio.control;

import android.os.Bundle;
import android.provider.Contacts;
import android.support.annotation.IntDef;

import any.audio.data.DataServer;
import any.audio.helpers.L;
import any.audio.interfaces.StreamUriCallback;

/**
 * Created by IITGN on 13-06-2017.
 */

public class ControlPanel {

    public static final int ACTION_STREAM =80;
    public static final int ACTION_NEXT = 81;
    public static final int ACTION_PAUSE = 82;
    public static final int ACTION_PLAY = 83;
    public static final int ACTION_STOP = 84;

    @IntDef({ACTION_STREAM,ACTION_NEXT,ACTION_PAUSE,ACTION_PLAY,ACTION_STOP})
    @interface PlayerActions {}

    class ActionCenter implements StreamUriCallback {

        private String TAG = ActionCenter.class.getSimpleName();

        public void act(@PlayerActions int action, Bundle extraa){

            switch (action){
                case ACTION_STREAM:
                    L.D.m(TAG,"action-stream");
                    // there is some hashedUrl to stream,
                    /*
                    *  > get the url
                    *  > stop the playing item
                    *  > update the UI
                    *  > request stream uri
                    *
                    * */
                    String url = extraa.getString("url","");
                    PlayerControl.stopPlayer();
                    UIControl.setPreparingState();
                    requestStreamUri(url);

                    break;
                case ACTION_PAUSE:


                    break;
                case ACTION_NEXT:


                    break;
                case ACTION_PLAY:

                    break;

                case ACTION_STOP:
                    break;

                default:
                    break;

            }
        }

        private void requestStreamUri(String url) {
            DataServer.requestStreamUri(url,this);
        }

        @Override
        public void onStreamUriFetchFailed() {
            /*
            * User tapped for stream and it failed.
            * Hence,
            * > notify the UI
            *
            *
            * */

            UIControl.setStoppedState();

        }

        @Override
        public void onStreamUriFetched(String uri) {
            /*
            *  User has tapped for stream and uri is fetched
            *  Hence,
            *  > Set uri to player
            * */
            PlayerControl.setSource(uri);
        }
    }


}
