package any.audio.interfaces;

/**
 * Created by Ankit on 16-06-2017.
 */

public interface StreamUriCallback {
    void onStreamUriFetchFailed();
    void onStreamUriFetched(String uri);
}
