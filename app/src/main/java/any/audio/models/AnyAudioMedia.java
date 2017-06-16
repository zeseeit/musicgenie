package any.audio.models;

/**
 * Created by Ankit on 13-06-2017.
 */

public class AnyAudioMedia {

    @com.google.gson.annotations.SerializedName("get_url")
    public String getUrl;
    @com.google.gson.annotations.SerializedName("id")
    public String id;
    @com.google.gson.annotations.SerializedName("length")
    public String length;
    @com.google.gson.annotations.SerializedName("thumb")
    public String thumb;
    @com.google.gson.annotations.SerializedName("title")
    public String title;
    @com.google.gson.annotations.SerializedName("uploader")
    public String uploader;
    @com.google.gson.annotations.SerializedName("views")
    public String views;
    @com.google.gson.annotations.SerializedName("description")
    public String description;
    @com.google.gson.annotations.SerializedName("suggest_url")
    public String suggestUrl;

    public AnyAudioMedia(String getUrl, String id, String length, String thumb, String title, String uploader, String views, String description, String suggestUrl) {
        this.getUrl = getUrl;
        this.id = id;
        this.length = length;
        this.thumb = thumb;
        this.title = title;
        this.uploader = uploader;
        this.views = views;
        this.description = description;
        this.suggestUrl = suggestUrl;
    }

    public String getGetUrl() {
        return getUrl;
    }

    public void setGetUrl(String getUrl) {
        this.getUrl = getUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUploader() {
        return uploader;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSuggestUrl() {
        return suggestUrl;
    }

    public void setSuggestUrl(String suggestUrl) {
        this.suggestUrl = suggestUrl;
    }

    @Override
    public String toString() {
        return "AnyAudioMedia{" +
                "getUrl='" + getUrl + '\'' +
                ", id='" + id + '\'' +
                ", length='" + length + '\'' +
                ", thumb='" + thumb + '\'' +
                ", title='" + title + '\'' +
                ", uploader='" + uploader + '\'' +
                ", views='" + views + '\'' +
                ", description='" + description + '\'' +
                ", suggestUrl='" + suggestUrl + '\'' +
                '}';
    }
}
