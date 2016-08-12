package musicgenie.com.musicgenie.models;

/**
 * Created by Ankit on 8/12/2016.
 */
public class DownloadTaskModel {
    public String Title;
    public int Progress;
    public String taskID;

    public DownloadTaskModel(String title, int progress,String taskID) {
        this.Title = title;
        this.taskID= taskID;
        this.Progress = progress;
    }
}
