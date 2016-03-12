package com.mathapp.saurabhjn76.musicgenie;

import javax.net.ssl.SSLContext;

/**
 * Created by saurabh on 12/3/16.
 */
public class Song {
    public String Title;
    public String url;
    public String artist;


    public Song(String title,String url,String artist){
        this.Title=title;
        this.url=url;
        this.artist=artist;
    }
}
