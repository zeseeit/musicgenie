package musicgenie.com.musicgenie.interfaces;

/**
 * Created by Ankit on 8/9/2016.
 */
public interface DownloadListener {
     void onError(String error);
     void onDownloadTaskProcessStart();
     void onDownloadFinish();
}
