package any.audio.control;

/**
 * Created by Ankit on 17-06-2017.
 */

public class PlayerControl {

    public static void stopPlayer(){

    }

    public static void setSource(String source) {

    }

    public static void onPlayerStartedPlay(){
       UIControl.setPlayingState();
    }

    public static void onPlayerPaused(){
        UIControl.setPausedState();
    }

    public static void onStreamProgress(int progress){
        UIControl.updateProgress(progress);
    }

    public static void onBufferProgress(int buffered){
        UIControl.updateBufferedProgress(buffered);
    }

}
