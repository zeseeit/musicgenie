package any.audio.interfaces;

import any.audio.models.AutoPlaylistModel;

/**
 * Created by Ankit on 16-06-2017.
 */

public interface PlaylistCallback {
    void onPlaylistFetchError();
    void onPlaylistFetched(AutoPlaylistModel model);
}
