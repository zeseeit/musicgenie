package any.audio.helpers;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import any.audio.Config.Constants;
import any.audio.Models.PlaylistItem;
import any.audio.SharedPreferences.SharedPrefrenceUtils;

/**
 * Created by Ankit on 1/31/2017.
 */

public class QueueManager {

    private static Context context;
    private static QueueManager mInstance;
    private SharedPrefrenceUtils utils;
    private ArrayList<String> videoTitles;
    private ArrayList<String> videoIds;
    private ArrayList<String> youtubeIds;
    private ArrayList<String> uploadersList;
    private QueueEventListener queueEventListener;

    public QueueManager(Context context) {

        this.context = context;
        utils = SharedPrefrenceUtils.getInstance(context);
        videoTitles = new ArrayList<>();
        videoIds = new ArrayList<>();
        youtubeIds = new ArrayList<>();
        uploadersList = new ArrayList<>();

    }

    public static QueueManager getInstance(Context context) {

        if (mInstance == null) {
            mInstance = new QueueManager(context);
        }
        return mInstance;

    }

    public void popQueueItem() {

        ArrayList<String> general = new ArrayList<>();
        //remove top videoTitle
        general = new Segmentor().getParts(utils.getQueueTitles(), '#');
        if (general.size() > 0) {
            general.remove(0);
            utils.setQueueTitle(listToString(general));
        }

        //remove top videoId
        general = new Segmentor().getParts(utils.getQueueVideoId(), '#');
        if (general.size() > 0) {
            general.remove(0);
            utils.setQueueVideoId(listToString(general));
        }

        // remove top youtubeId
        general = new Segmentor().getParts(utils.getQueueYoutubeIds(), '#');
        if (general.size() > 0) {
            general.remove(0);
            utils.setQueueYoutubId(listToString(general));
        }

        // remove top uploader
        general = new Segmentor().getParts(utils.getQueueUploaders(), '#');
        if (general.size() > 0) {
            general.remove(0);
            utils.setQueueUploaders(listToString(general));
        }

        if (queueEventListener != null) {
            queueEventListener.onQueueItemPop();
        }

    }

    public void pushQueueItem(PlaylistItem item, boolean neededCallback) {

        // enque the new item and notify
        // title
        utils.setQueueTitle(utils.getQueueTitles() + "#" + item.getTitle());
        //youtubeId
        utils.setQueueYoutubId(utils.getQueueYoutubeIds() + "#" + item.getYoutubeId());
        //videoIds
        utils.setQueueVideoId(utils.getQueueVideoId() + "#" + item.getVideoId());
        // uploaders
        utils.setQueueUploaders(utils.getQueueUploaders() + "#" + item.getUploader());

        if (neededCallback) {
            if (queueEventListener != null) {
                queueEventListener.onQueueItemPush(item);
            }
        }

        Log.d("QueueManager","Items "+utils.getQueueTitles());

    }

    public void removeQueueItem(String vid) {
        // no-callback require since action is from User
        // remove item from the adapter-done--

        ArrayList<PlaylistItem> oldList = getQueue();
        utils.clearQueue();
        utils.setCurrentQueueIndex(-1);
        for (PlaylistItem item : oldList) {
            if (!item.videoId.equals(vid)) {
                pushQueueItem(item, false);
            }
        }

    }

    private String listToString(ArrayList<String> general) {
        String _string = "";

        for (int i = 0; i < general.size(); i++) {
            _string += "#" + general.get(i);
        }
        return _string;
    }

    public ArrayList<PlaylistItem> getQueue() {

        ArrayList<PlaylistItem> playlistItems = new ArrayList<>();


        PlaylistItem upNextItem = null;
        videoTitles = new Segmentor().getParts(utils.getQueueTitles(), '#');
        videoIds = new Segmentor().getParts(utils.getQueueVideoId(), '#');
        youtubeIds = new Segmentor().getParts(utils.getQueueYoutubeIds(), '#');
        uploadersList = new Segmentor().getParts(utils.getQueueUploaders(), '#');

        // Add the last played item as the first item of the queue

        for (int i = 0; i < videoIds.size(); i++) {
            upNextItem = new PlaylistItem(videoIds.get(i), youtubeIds.get(i), videoTitles.get(i), uploadersList.get(i));
            playlistItems.add(upNextItem);
        }

        return playlistItems;

    }

    public void setQueueEventListener(QueueEventListener eventListener) {

        this.queueEventListener = eventListener;

    }

    public PlaylistItem getUpNext() {

        ArrayList<PlaylistItem> queue = getQueue();
        if(queue.size()==0){
            return null;
        }else {
            int ind = getNextIndex();

            if(ind==-1){
                return null;
            }else{
                return queue.get(ind);
            }

        }

    }

    private int getNextIndex() {

        int totalQueueLength = getQueue().size();
        String repeatMode = utils.getRepeatMode();
        int currentIndex = utils.getCurrentQueueIndex();
        int nextIndex = -1;

        switch (repeatMode) {

            case Constants.MODE_REPEAT_NONE:

                Log.d("RepeatMode","[None] current Index "+currentIndex+" Queue Len "+ totalQueueLength);

                if (currentIndex == (totalQueueLength - 1)) {
                    // Reset the queue index
                    utils.setCurrentQueueIndex(nextIndex);
                    Log.d("RepeatMode","[None] last item reached ");
                    return nextIndex;

                } else {

                    nextIndex = currentIndex + 1;
                    utils.setCurrentQueueIndex(nextIndex);
                    Log.d("RepeatMode","[None] next to play index "+nextIndex);
                    return nextIndex;

                }

            case Constants.MODE_REPEAT_ALL:

                Log.d("RepeatMode","[All] current Index "+currentIndex+" Queue Len "+ totalQueueLength);
                // keep repeating between 0-len
                nextIndex = (currentIndex+1)%totalQueueLength;
                utils.setCurrentQueueIndex(nextIndex);
                Log.d("RepeatMode","[All] next to play index "+nextIndex);
                return nextIndex;

            case Constants.MODE_SUFFLE:

                Log.d("RepeatMode","[Suffle] current Index "+currentIndex+" Queue Len " + totalQueueLength);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    nextIndex = ThreadLocalRandom.current().nextInt(0, totalQueueLength);
                } else {
                    nextIndex = new Random().nextInt(totalQueueLength);
                }

                Log.d("RepeatMode","[Suffle] next to play index "+nextIndex);
                return nextIndex;

        }

        return nextIndex;

    }

    public interface QueueEventListener {

        void onQueueItemPop();

        void onQueueItemPush(PlaylistItem item);

    }

}
