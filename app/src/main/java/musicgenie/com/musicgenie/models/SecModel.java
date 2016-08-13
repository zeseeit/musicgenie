package musicgenie.com.musicgenie.models;

/**
 * Created by Ankit on 8/14/2016.
 */
public class SecModel {
    String SSID;
    String MAC;
    String IP;
    String LinkSpeed;
    String NetID;

    public SecModel(String SSID, String MAC, String IP, String linkSpeed, String netID) {
        this.SSID = SSID;
        this.MAC = MAC;
        this.IP = IP;
        this.LinkSpeed = linkSpeed;
        this.NetID = netID;
    }
}
