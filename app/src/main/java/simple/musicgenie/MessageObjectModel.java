package simple.musicgenie;

/**
 * Created by Ankit on 10/3/2016.
 */
public class MessageObjectModel {

    int Status;
    String Message;
    SectionModel data;

    public MessageObjectModel(int status, String message, SectionModel data) {
        Status = status;
        Message = message;
        this.data = data;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public SectionModel getData() {
        return data;
    }

    public void setData(SectionModel data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MessageObjectModel{" +
                "Status=" + Status +
                ", Message='" + Message + '\'' +
                ", data=" + data +
                '}';
    }
}
