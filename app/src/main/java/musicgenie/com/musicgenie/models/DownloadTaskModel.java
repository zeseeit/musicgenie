package musicgenie.com.musicgenie.models;

/**
 * Created by Ankit on 8/12/2016.
 */
public class DownloadTaskModel {
    public String Title;
    public int Progress;

    public DownloadTaskModel(String title, int progress) {
        this.Title = title;
        this.Progress = progress;
    }
}
