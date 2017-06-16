package any.audio.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import any.audio.models.AnyAudioMedia;


/**
 * Created by Ankit on 13-06-2017.
 */

public class AnyAudioPreferenceManager {

    private static final int PREF_MODE = Context.MODE_PRIVATE;
    private static final String PREF_NAME = "anaud_pref";
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private static AnyAudioPreferenceManager mInstance;
    Context context;
    Gson gson;

    public static AnyAudioPreferenceManager getInstance() {
        if (mInstance == null) {
            mInstance = new AnyAudioPreferenceManager();
        }
        return mInstance;
    }

    public AnyAudioPreferenceManager() {
        preferences = context.getSharedPreferences(PREF_NAME, PREF_MODE);
        editor = preferences.edit();
        gson = new Gson();
    }

    public void savePlaylist(List<AnyAudioMedia> medias) {

        String _mediasAsString = gson.toJson(medias);
        editor.putString("playlist", _mediasAsString);
        editor.commit();

    }

    public ArrayList<AnyAudioMedia> getPlaylist() {

        String json = preferences.getString("playlist", null);
        Type type = new TypeToken<ArrayList<AnyAudioMedia>>() {
        }.getType();
        return gson.fromJson(json, type);

    }

}
