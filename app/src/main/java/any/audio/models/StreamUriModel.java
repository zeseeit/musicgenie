package any.audio.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ankit on 16-06-2017.
 */

public class StreamUriModel {

    @SerializedName("status")
    public int status;
    @SerializedName("url")
    public String url;

    public StreamUriModel(int status, String url) {
        this.status = status;
        this.url = url;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "StreamUriModel{" +
                "status=" + status +
                ", url='" + url + '\'' +
                '}';
    }
}
