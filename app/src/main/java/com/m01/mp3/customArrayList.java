package com.m01.mp3;

/**
 * Created by Administrateur on 02/03/18.
 */

public class customArrayList {
    private String mSongName;
    private int mDuration;
    private String mArtist;
    private String mPath;


    public customArrayList(String songName, String artist, int duration, String path) {
        mSongName = songName;
        mDuration = duration;
        mArtist = artist;
        mPath = path;


    }

    public String getSongName() {
        return mSongName;
    }

    public String getArtist() {
        return mArtist;
    }

    public int getDuration() {
        return mDuration;
    }

    public String getPath() {
        return mPath;
    }


}
