package com.m01.mp3;

import android.os.Bundle;
import android.widget.ImageView;
import android.view.View;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ImageView;
import android.media.MediaPlayer;
import android.os.Handler;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.database.Cursor;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.provider.MediaStore;
import java.util.ArrayList;
import android.widget.AdapterView;
import android.net.Uri;
import android.media.AudioManager;
import java.io.IOException;
import android.media.MediaPlayer.OnCompletionListener;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
import android.view.WindowManager;
import android.view.Window;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    public static final String pos="position";
    public static final String MyPref = "MyPrefs" ;
    public static final String seekPos="seekPosition";
    public static final String saveRepeat="Repeat";
    public static TextView duration,progress,name,numberOfSong;
    public static SeekBar seek;
    public static ImageView playPause,repeat;
    private ImageView next;
    private ImageView previous;
    public static MediaPlayer mMediaPlayer;
    public static AudioManager mAudioManager;
    public static Handler mHandler;
    public static Runnable mRunnable;
    private int playerDuration;
    private ContentResolver resolver;
    private ListView list;
    public static ArrayList<customArrayList> audioFile;
    public static int position;
    static int seekPosition;
    static int repeatState=1;
    private Toast toast;
    Intent intent;
    public static SharedPreferences mSharedPreferences;
    public static Context xt;
    public static SharedPreferences.Editor editor;
    public static Uri path;
    public static int focusState=1;




    private static OnCompletionListener mOnCompletionListener=new OnCompletionListener(){
        public void onCompletion(MediaPlayer mMediaplayer){
            if(repeatState==1){
                position+=1;
                if(position>audioFile.size()-1){
                    position=0;
                }
                seekPosition=0;
                audioInfo(position);
                forgroundService.notificationView.setTextViewText(R.id.songName,MainActivity.audioFile.get(MainActivity.position)
                        .getSongName());
                forgroundService.notificationUpdate(forgroundService.ground);
                playPauseSong();
            }else if(repeatState==2){
                seekPosition=0;
                audioInfo(position);
                forgroundService.notificationView.setTextViewText(R.id.songName,MainActivity.audioFile.get(MainActivity.position)
                        .getSongName());
                forgroundService.notificationUpdate(forgroundService.ground);
                playPauseSong();
            }else if(repeatState==3){
                mMediaPlayer.seekTo(0);
                forgroundService.notificationView.setImageViewResource(R.id.playPause_notification, android.R.drawable.ic_media_play);
                playPause.setImageResource(android.R.drawable.ic_media_play);
                forgroundService.notificationUpdate(forgroundService.ground);
            }
        }
    };

    public static AudioManager.OnAudioFocusChangeListener mOnAudioFocusChange=
            new AudioManager.OnAudioFocusChangeListener(){
                public void onAudioFocusChange(int focusChange){
                    if(focusChange==AudioManager.AUDIOFOCUS_GAIN){
                        playPause.setImageResource(android.R.drawable.ic_media_pause);
                        forgroundService.notificationView.setImageViewResource(R.id.playPause_notification, android.R.drawable.ic_media_pause);
                        forgroundService.notificationUpdate(forgroundService.ground);
                        mMediaPlayer.start();
                    }else if(focusChange==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK||focusChange==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
                        playPause.setImageResource(android.R.drawable.ic_media_play);
                        forgroundService.notificationView.setImageViewResource(R.id.playPause_notification, android.R.drawable.ic_media_play);
                        forgroundService.notificationUpdate(forgroundService.ground);
                        mMediaPlayer.pause();
                        focusState=2;
                    }else if(focusChange==AudioManager.AUDIOFOCUS_LOSS){
                        playPause.setImageResource(android.R.drawable.ic_media_play);
                        mMediaPlayer.pause();
                        focusState=2;
                    }
                }
            };


    @Override
    protected void onStop() {
        super.onStop();

        save();


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }
        xt=this;



        // Get the widget reference from xml layout
        seek =(SeekBar) findViewById(R.id.seek);
        duration=(TextView)findViewById(R.id.duration);
        progress=(TextView)findViewById(R.id.progress);
        name=(TextView)findViewById(R.id.name);
        numberOfSong=(TextView)findViewById(R.id.number_of_songs);
        playPause=(ImageView)findViewById(R.id.play_pause);
        next=(ImageView)findViewById(R.id.next);
        previous=(ImageView)findViewById(R.id.previous);
        list=(ListView)findViewById(R.id.list);
        repeat=(ImageView)findViewById(R.id.repeat);
        intent = new Intent(MainActivity.this, forgroundService.class);

        intent.setAction(constants.ACTION.STOPFOREGROUND_ACTION);

        //create toast
        toast=Toast.makeText(getApplicationContext(),"",Toast.LENGTH_SHORT);

        mAudioManager=(AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mSharedPreferences=getSharedPreferences(MyPref,Context.MODE_PRIVATE);
        editor=mSharedPreferences.edit();

        // Initialize the handler
        mHandler=new Handler();
        //get the stored  paremeter
        position=mSharedPreferences.getInt(pos,0);
        seekPosition=mSharedPreferences.getInt(seekPos,0);

        if(mSharedPreferences.contains(saveRepeat)) {
            repeatState = mSharedPreferences.getInt(saveRepeat, 0);
        }

        //initialize of the repeatState
        if(repeatState==1){
            repeat.setImageDrawable(null);
            repeat.setImageResource(R.drawable.ic_repeat);
        }else if(repeatState==2){
            repeat.setImageDrawable(null);
            repeat.setImageResource(R.drawable.ic_repeat_once);
        }else if(repeatState==3){
            repeat.setImageDrawable(null);
            repeat.setImageResource(R.drawable.ic_repeat_off);
        }
        //remove seek bar thumb
        seek.getThumb().mutate().setAlpha(0);


        audioFile=new ArrayList<customArrayList>();
        resolver=getContentResolver();
        Cursor audioCursor=resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                ,null,null,null,null);
        //get all the song info from storage
        if(audioCursor.moveToFirst()){
            do{
                //get path of the song
                int path=audioCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                //get song title
                int songName=audioCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                //get artist name
                int artist=audioCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                //get song duration
                int duration=audioCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
                //convert all song info to string
                String thisName=audioCursor.getString(songName);
                String thisArtist=audioCursor.getString(artist);
                String thisPath=audioCursor.getString(path);
                int thisDuration=audioCursor.getInt(duration);
                //store song info to arraylist
                audioFile.add(new customArrayList(thisName,thisArtist,thisDuration,thisPath));

            }while(audioCursor.moveToNext());
        }


        if(audioFile.size()>0) {


            customAdapter audioAdapter = new customAdapter(this, audioFile);
            list.setAdapter(audioAdapter);
            numberOfSong.setText(audioFile.size()+" Songs");
            int audioDuration = audioFile.get(position).getDuration();
            //display duration
            duration.setText(timeFormat(audioDuration));
            //set seek bar max
            seek.setMax(audioDuration / 1000);
            //display name of the song
            name.setText(audioFile.get(position).getSongName());
            //set seek bar progress
            seek.setProgress(seekPosition);
            //display the progress
            progress.setText(timeFormat(seekPosition * 1000));
            if (mMediaPlayer==null) {
                releaseMediaPlayer();
                mAudioManager.abandonAudioFocus(mOnAudioFocusChange);
                path = Uri.parse(audioFile.get(position).getPath());
                mMediaPlayer = MediaPlayer.create(MainActivity.xt, path);
                mMediaPlayer.seekTo(seekPosition * 1000);
            }else{
                if(mMediaPlayer!=null&&mMediaPlayer.isPlaying()) {
                    playPause.setImageResource(android.R.drawable.ic_media_pause);

                }else{
                    playPause.setImageResource(android.R.drawable.ic_media_play);
                }
            }




            // Click listener for play mediaPlayer
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //get the current position of the clicked item
                    intent.setAction(constants.ACTION.STARTFOREGROUND_ACTION);
                    startService(intent);

                    position = i;
                    audioFocusGain(i);
                }

            });

            // Click listener for play and pause mediaPlayer
            playPause.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {

                    intent.setAction(constants.ACTION.STARTFOREGROUND_ACTION);
                    startService(intent);
                        playPauseSong();

                }
            });

            // Click listener for playing next music
            next.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {

                    intent.setAction(constants.ACTION.STARTFOREGROUND_ACTION);
                    startService(intent);

                    position += 1;
                    if (position > audioFile.size() - 1) {
                        position = 0;
                    }
                    audioFocusGain(position);
                }
            });

            // Click listener for playing previous music
            previous.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    intent.setAction(constants.ACTION.STARTFOREGROUND_ACTION);
                    startService(intent);

                    position -= 1;
                    if (position < 0) {
                        position = audioFile.size() - 1;
                    }
                    audioFocusGain(position);
                }
            });

            // Click listener to set repeat state(repeat all songs, repeat current song, repeat off)
            repeat.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (repeatState == 1) {
                        repeat.setImageDrawable(null);
                        repeat.setImageResource(R.drawable.ic_repeat_once);
                        toast.setText("Repeating current song.");
                        toast.show();
                        repeatState = 2;
                    } else if (repeatState == 2) {
                        repeat.setImageDrawable(null);
                        repeat.setImageResource(R.drawable.ic_repeat_off);
                        toast.setText("Repeat is off.");
                        toast.show();
                        repeatState = 3;
                    } else if (repeatState == 3) {
                        repeat.setImageDrawable(null);
                        repeat.setImageResource(R.drawable.ic_repeat);
                        toast.setText("Repeatint all songs.");
                        toast.show();
                        repeatState = 1;
                    }
                }
            });


            //Set a change listener for seek bar
            seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                /*
                    void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser)
                        Notification that the progress level has changed. Clients can use the fromUser
                        parameter to distinguish user-initiated changes from those that occurred programmatically.

                    Parameters
                        seekBar SeekBar : The SeekBar whose progress has changed
                        progress int : The current progress level. This will be in the range min..max
                                       where min and max were set by setMin(int) and setMax(int),
                                       respectively. (The default values for min is 0 and max is 100.)
                        fromUser boolean : True if the progress change was initiated by the user.
                */
                public void onProgressChanged(SeekBar seekbar, int i, boolean fromUser) {
                    if (mMediaPlayer != null && fromUser) {
                        seekPosition = i;
                        //set position of the mediaPlayer
                        mMediaPlayer.seekTo(i * 1000);
                        progress.setText(timeFormat(i * 1000));
                    }
                }

                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }else{
            progress.setText("--:--");
            duration.setText("--:--");
            numberOfSong.setText("No Song");

        }
    }

    public static void playPauseSong(){
        if(mMediaPlayer.isPlaying()){
            //set play icon
            playPause.setImageResource(android.R.drawable.ic_media_play);


            //Pause the media player
            mMediaPlayer.pause();
            mAudioManager.abandonAudioFocus(mOnAudioFocusChange);
        }else{


            int result = mAudioManager.requestAudioFocus(mOnAudioFocusChange, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                //set pause icon
                playPause.setImageResource(android.R.drawable.ic_media_pause);
                if(focusState==2){
                    mAudioManager.abandonAudioFocus(mOnAudioFocusChange);
                    focusState=1;
                }

                // Start the media player
                mMediaPlayer.start();
                // Initialize the seek bar
                seekSetProgress();
                //set a completion listener for media player
                mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
            }
        }
    }


    public static void seekSetProgress(){
        mRunnable=new Runnable() {
            public void run(){
                if(mMediaPlayer!=null){
                    seekPosition=mMediaPlayer.getCurrentPosition()/1000;
                    seek.setProgress(seekPosition);
                    progress.setText(timeFormat(seekPosition*1000));
                    save();
                }
                mHandler.postDelayed(this,100);
            }
        };
        mHandler.postDelayed(mRunnable,100);
    }
    /**
     *  Convert from a secconds to a formatted String mm:ss
     *  @param duration en seconds
     */
    public static String timeFormat(int duration){
        SimpleDateFormat songFormat=new SimpleDateFormat("mm:ss");
        String time=songFormat.format(new Date(duration));
        return time;
    }

    public static void releaseMediaPlayer(){
        if(mMediaPlayer!=null){
            mMediaPlayer.release();
            mMediaPlayer=null;

        }
    }

    public static void audioInfo(int position){

        int audioDuration=audioFile.get(position).getDuration();
        //display duration
        duration.setText(timeFormat(audioDuration));
        //set seek bar max
        seek.setMax(audioDuration/1000);
        //display name of the song
        name.setText(audioFile.get(position).getSongName());



        //get the uri of the song
        path=Uri.parse(audioFile.get(position).getPath());
        //release media player
        releaseMediaPlayer();
        mMediaPlayer=MediaPlayer.create(MainActivity.xt,path);


    }

    public static void save(){

        //storing the number of the current song
        editor.putInt(pos,position);
        //storing the progress of the song
        editor.putInt(seekPos,seekPosition);
        //storing the repeat state(repeat all song, repeat current song, repeat off)
        editor.putInt(saveRepeat,repeatState);
        //commit changes
        editor.commit();
    }

    public static void audioFocusGain(int position){

            audioInfo(position);
        playPauseSong();

        }

}