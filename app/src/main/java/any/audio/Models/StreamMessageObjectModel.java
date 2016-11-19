package any.audio.Models;

/**
 * Created by Ankit on 10/5/2016.
 */
public class StreamMessageObjectModel {

    public long progress;
    public long maxLength;
    public long currentBuffered;

    public StreamMessageObjectModel(long progress, long maxLength, long currentBuffered) {
        this.progress = progress;
        this.maxLength = maxLength;
        this.currentBuffered = currentBuffered;
    }
}
