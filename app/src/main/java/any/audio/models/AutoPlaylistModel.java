package any.audio.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Ankit on 16-06-2017.
 */

public class AutoPlaylistModel {

    @SerializedName("metadata")
    public Metadata metadata;
    @SerializedName("results")
    public List<AnyAudioMedia> results;

    public static class Metadata {
        @SerializedName("count")
        public int count;
    }

    public AutoPlaylistModel(Metadata metadata, List<AnyAudioMedia> results) {
        this.metadata = metadata;
        this.results = results;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<AnyAudioMedia> getResults() {
        return results;
    }

    public void setResults(List<AnyAudioMedia> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "AutoPlaylistModel{" +
                "metadata=" + metadata +
                ", results=" + results +
                '}';
    }
}
