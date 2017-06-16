package any.audio.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Ankit on 16-06-2017.
 */

public class SearchResultModel {


    @SerializedName("metadata")
    public Metadata metadata;
    @SerializedName("results")
    public List<AnyAudioMedia> results;

    public static class Metadata {
        @SerializedName("q")
        public String q;
        @SerializedName("count")
        public int count;

        public Metadata(String q, int count) {
            this.q = q;
            this.count = count;
        }

        public String getQ() {
            return q;
        }

        public void setQ(String q) {
            this.q = q;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        @Override
        public String toString() {
            return "Metadata{" +
                    "q='" + q + '\'' +
                    ", count=" + count +
                    '}';
        }
    }

    public SearchResultModel(Metadata metadata, List<AnyAudioMedia> results) {
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
        return "SearchResultModel{" +
                "metadata=" + metadata +
                ", results=" + results +
                '}';
    }
}
