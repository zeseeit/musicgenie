package any.audio;

/**
 * Created by Ankit on 10/7/2016.
 */
public interface StreamProgressListener {
    void onProgressChange(int progress,int buffered, int duration);
}
