package any.audio.data;

import any.audio.preference.AnyAudioPreferenceManager;
import any.audio.api.AnyAudioAPI;
import any.audio.api.AnyAudioApiClient;
import any.audio.helpers.L;
import any.audio.interfaces.PlaylistCallback;
import any.audio.interfaces.StreamUriCallback;
import any.audio.models.AutoPlaylistModel;
import any.audio.models.StreamUriModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
/**
 * Created by Ankit on 16-06-2017.
 */

public class DataServer {

    /*
    * Note: All the helper methods will be static in order to follow singleton pattern
    * */

    private static final String TAG = DataServer.class.getSimpleName();
    private static AnyAudioAPI anyaudioAPI = null;
    private static AnyAudioPreferenceManager anyaudioPreferenceManager;
    private static int STATUS_OK = 200;

    private static AnyAudioAPI getService() {

        anyaudioAPI = AnyAudioApiClient
                .getClient()
                .create(AnyAudioAPI.class);

        anyaudioPreferenceManager = AnyAudioPreferenceManager.getInstance();

        return anyaudioAPI;

    }


    public static void requestPlaylist(String currentUrl, final PlaylistCallback callback) {

        L.D.m(TAG, "requesting playlist");

        getService()
                .getPlaylist(currentUrl)
                .enqueue(new Callback<AutoPlaylistModel>() {
                    @Override
                    public void onResponse(Call<AutoPlaylistModel> call, Response<AutoPlaylistModel> response) {
                        try {
                            if (response.body().getMetadata().count > 0) {
                                callback.onPlaylistFetched(response.body());
                            } else {
                                callback.onPlaylistFetchError();
                            }
                        } catch (Exception e) {
                            L.E.m(TAG, e.toString());
                            callback.onPlaylistFetchError();
                        }
                    }

                    @Override
                    public void onFailure(Call<AutoPlaylistModel> call, Throwable t) {
                        L.E.m(TAG, t.toString());
                        callback.onPlaylistFetchError();
                    }
                });

    }

    public static void requestStreamUri(String hashUrl, final StreamUriCallback callback) {

        L.D.m(TAG, "requesting stream uri");

        getService()
                .getStreamUri(hashUrl)
                .enqueue(new Callback<StreamUriModel>() {
                    @Override
                    public void onResponse(Call<StreamUriModel> call, Response<StreamUriModel> response) {
                        try {
                            if (response.body().getStatus() == STATUS_OK) {
                                callback.onStreamUriFetched(response.body().getUrl());
                            } else {
                                callback.onStreamUriFetchFailed();
                            }
                        } catch (Exception e) {
                            callback.onStreamUriFetchFailed();
                            L.E.m(TAG, e.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<StreamUriModel> call, Throwable t) {
                        L.E.m(TAG, t.toString());
                        callback.onStreamUriFetchFailed();
                    }
                });

    }
}
