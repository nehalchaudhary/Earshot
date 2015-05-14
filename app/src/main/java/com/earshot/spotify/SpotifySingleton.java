package com.earshot.spotify;

import android.app.Activity;

/**
 * Created by nehal.chaudhary on 5/13/15.
 */
public class SpotifySingleton {

    private static Spotify instance = null;

    protected SpotifySingleton() {
        // Exists only to defeat instantiation.
    }

    public static Spotify getSpotifyInstance(Activity context) {
        if(instance == null) {
            instance = new Spotify(context);
        }
        return instance;
    }
}