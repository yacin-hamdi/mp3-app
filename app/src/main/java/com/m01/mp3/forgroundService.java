    package com.m01.mp3;

    /**
     * Created by Administrateur on 07/03/18.
     */

    import android.app.Notification;
    import android.app.PendingIntent;
    import android.app.Service;
    import android.content.BroadcastReceiver;
    import android.content.Context;
    import android.content.Intent;
    import android.graphics.Bitmap;
    import android.graphics.BitmapFactory;
    import android.os.IBinder;

    import android.util.Log;
    import android.widget.RemoteViews;
    import android.widget.Toast;
    import android.widget.ImageView;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.LinearLayout;
    import android.view.LayoutInflater;
    import android.app.NotificationManager;
    import android.media.AudioManager;

    import androidx.core.app.NotificationCompat;

    public class forgroundService extends Service {
        private static final String LOG_TAG = "ForegroundService";
        public static NotificationCompat.Builder builder;
        public static RemoteViews notificationView;
        public static Context ground;
        public static Notification notification;
        @Override
        public void onCreate() {
            super.onCreate();


        }



        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            ground=this;
            builder=new NotificationCompat.Builder(ground);
            notificationView = new RemoteViews(this.getPackageName(), R.layout.notifications);

            if (intent.getAction().equals(constants.ACTION.STARTFOREGROUND_ACTION)) {



                Intent notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.setAction(constants.ACTION.MAIN_ACTION);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, 0);




                Intent previousIntent = new Intent(this, forgroundService.class);
                previousIntent.setAction(constants.ACTION.PREV_ACTION);
                PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                        previousIntent, 0);
                // And now, building and attaching the Play button.
                Intent playIntent = new Intent(this, forgroundService.class);
                playIntent.setAction(constants.ACTION.PLAY_ACTION);
                PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                        playIntent, 0);



                Intent nextIntent = new Intent(this, forgroundService.class);
                nextIntent.setAction(constants.ACTION.NEXT_ACTION);
                PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                        nextIntent, 0);

                Intent closeIntent = new Intent(this, forgroundService.class);
                closeIntent.setAction(constants.ACTION.STOPFOREGROUND_ACTION);
                PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                        closeIntent, 0);

                notificationView.setOnClickPendingIntent(R.id.playPause_notification, pplayIntent);


                notificationView.setOnClickPendingIntent(R.id.next_notification, pnextIntent);


                notificationView.setOnClickPendingIntent(R.id.previous_notificaion, ppreviousIntent);


                notificationView.setOnClickPendingIntent(R.id.close_notification, pcloseIntent);

                if(MainActivity.mMediaPlayer.isPlaying()){
                    notificationView.setImageViewResource(R.id.playPause_notification, android.R.drawable.ic_media_pause);
                }else{
                    notificationView.setImageViewResource(R.id.playPause_notification, android.R.drawable.ic_media_play);
                }
                notificationView.setTextViewText(R.id.songName,MainActivity.audioFile.get(MainActivity.position)
                        .getSongName());

                Bitmap icon = BitmapFactory.decodeResource(forgroundService.ground.getResources(),
                        R.drawable.ic_music);

                notification=builder
                        .setContentTitle("Music Player")
                        .setTicker("Music Player")
                        .setContentText("Music Player")
                        .setSmallIcon(R.drawable.ic_music)
                        .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                        .setContent(notificationView)
                        .setOngoing(true).build();


                startForeground(constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                        notification);
            }else if (intent.getAction().equals(constants.ACTION.PREV_ACTION)) {
                MainActivity.position-=1;
                if(MainActivity.position<0){
                    MainActivity.position=MainActivity.audioFile.size()-1;
                }
                MainActivity.audioFocusGain(MainActivity.position);
                notificationView.setTextViewText(R.id.songName,MainActivity.audioFile.get(MainActivity.position)
                        .getSongName());
                notificationView.setImageViewResource(R.id.playPause_notification, android.R.drawable.ic_media_pause);
                notificationUpdate(ground);

            } else if (intent.getAction().equals(constants.ACTION.PLAY_ACTION)) {
                    MainActivity.playPauseSong();

                if(MainActivity.mMediaPlayer.isPlaying()){
                    notificationView.setImageViewResource(R.id.playPause_notification, android.R.drawable.ic_media_pause);
                }else{

                    notificationView.setImageViewResource(R.id.playPause_notification, android.R.drawable.ic_media_play);
                }
                notificationUpdate(ground);

            } else if (intent.getAction().equals(constants.ACTION.NEXT_ACTION)) {
                MainActivity.position+=1;
                if(MainActivity.position>MainActivity.audioFile.size()-1){
                    MainActivity.position=0;
                }
                MainActivity.audioFocusGain(MainActivity.position);
                notificationView.setTextViewText(R.id.songName,MainActivity.audioFile.get(MainActivity.position)
                        .getSongName());
                notificationView.setImageViewResource(R.id.playPause_notification, android.R.drawable.ic_media_pause);
                notificationUpdate(ground);


            }  else if (intent.getAction().equals(constants.ACTION.STOPFOREGROUND_ACTION)) {

                stopForeground(true);
                stopSelf();
            }
            return START_STICKY;
        }

       public static void notificationUpdate(Context c){

            Bitmap icon = BitmapFactory.decodeResource(forgroundService.ground.getResources(),
                    R.drawable.ic_music);

           notification=builder
                   .setContentTitle("Music Player")
                   .setTicker("Music Player")
                   .setContentText("Music Player")
                   .setSmallIcon(R.drawable.ic_music)
                   .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                   .setContent(notificationView)
                   .setOngoing(true).build();


            NotificationManager mNotificationManager = (NotificationManager) c.getSystemService(ground.NOTIFICATION_SERVICE);
            mNotificationManager.notify(constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                    notification);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            Log.i(LOG_TAG, "In onDestroy");
        }

        @Override
        public IBinder onBind(Intent intent) {
            // Used only in case of bound services.
            return null;
        }

    }


