package any.audio.Models;

/**
 * Created by Ankit on 2/16/2017.
 */

public class EveSuggestionsItemModel {

    public String title;
    public String artist;
    public String thumbnailUrl;

    public EveSuggestionsItemModel(String title, String artist, String thumbnailUrl) {
        this.title = title;
        this.artist = artist;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }
}
