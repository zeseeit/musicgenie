package musicgenie.com.musicgenie.interfaces;

/**
 * Created by Ankit on 8/9/2016.
 */
public interface DownloadListener {
    public void onError(String error);
    public void onDownloadTaskProcessStart();
    public void onDownloadFinish();
}
