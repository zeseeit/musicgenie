package com.mathapp.saurabhjn76.musicgenie;

import android.app.Application;
import android.content.Context;

/**
 * Created by saurabh on 12/3/16.
 */
public class Contexter extends Application {


    public Context getContext(){
        return getBaseContext();
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }
}
