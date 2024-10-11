package com.example.player;

import android.media.MediaPlayer;

public class MyMediaPlayer {
    static MediaPlayer instance;

    public static MediaPlayer getinstance(){
        if (instance ==null){
            instance = new MediaPlayer();
        }
        return instance;
    }

    public static int currentIndex = -1;
}
