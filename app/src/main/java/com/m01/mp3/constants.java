package com.m01.mp3;

/**
 * Created by Administrateur on 07/03/18.
 */

public class constants {
    public interface ACTION {
        public static String MAIN_ACTION = "com.example.android.musicplayer.action.main";
        public static String INIT_ACTION = "com.example.android.musicplayer.action.init";
        public static String PREV_ACTION = "com.example.android.musicplayer.action.prev";
        public static String PLAY_ACTION = "com.example.android.musicplayer.action.play";
        public static String NEXT_ACTION = "com.example.android.musicplayer.action.next";
        public static String STARTFOREGROUND_ACTION = "com.example.android.musicplayer.action.startforeground";
        public static String STOPFOREGROUND_ACTION = "com.example.android.musicplayer.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
