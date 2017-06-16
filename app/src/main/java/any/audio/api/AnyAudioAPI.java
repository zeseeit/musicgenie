package any.audio.api;


import any.audio.models.AutoPlaylistModel;
import any.audio.models.StreamUriModel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Ankit on 5/13/2017.
 */

public interface AnyAudioAPI {

    @GET("api/v1/suggest")
    Call<AutoPlaylistModel> getPlaylist(@Path("url") String hashedRootUrl);

    @GET("api/v1/stream")
    Call<StreamUriModel> getStreamUri(@Path("url") String hashedUrl);


}
