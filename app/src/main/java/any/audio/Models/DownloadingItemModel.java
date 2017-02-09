package any.audio.Models;

/**
 * Created by Ankit on 2/9/2017.
 */

public class DownloadingItemModel {

    public String taskId;
    public String thumbnailUrl;
    public String title;
    public String artist;
    public String progress;
    public String contentSize;
    public String downloadingState;

    public DownloadingItemModel(String taskId,String thumbnailUrl,String title, String artist, String progress, String downloadingState,String contentSize) {
        this.taskId = taskId;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.contentSize=contentSize;
        this.artist = artist;
        this.progress = progress;
        this.downloadingState = downloadingState;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getContentSize() {
        return contentSize;
    }

    public void setContentSize(String contentSize) {
        this.contentSize = contentSize;
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

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getDownloadingState() {
        return downloadingState;
    }

    public void setDownloadingState(String downloadingState) {
        this.downloadingState = downloadingState;
    }
}
