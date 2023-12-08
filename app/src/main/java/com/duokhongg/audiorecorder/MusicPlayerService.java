package com.duokhongg.audiorecorder;

import static androidx.core.app.NotificationManagerCompat.IMPORTANCE_HIGH;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;

import androidx.annotation.OptIn;
import androidx.media3.common.AudioAttributes;

import android.graphics.Bitmap;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.media3.common.C;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerNotificationManager;

import java.io.IOException;

public class MusicPlayerService extends Service {
    ExoPlayer player;
    PlayerNotificationManager notificationManager;
    private IBinder serviceBinder = new ServiceBinder();

    public class ServiceBinder extends Binder {
        public MusicPlayerService getPlayerService() {
            return MusicPlayerService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    @OptIn(markerClass = UnstableApi.class) @Override
    public void onCreate() {
        player = new ExoPlayer.Builder(getApplicationContext()).build();
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .build();

        player.setAudioAttributes(audioAttributes, true);

        String channelId = "AudioRecorder channel";
        int notificationId = 11111111;
        notificationManager = new PlayerNotificationManager.Builder(this, notificationId, channelId)
                .setNotificationListener(notificationListener)
                .setMediaDescriptionAdapter(descriptionAdapter)
                .setChannelImportance(IMPORTANCE_HIGH)
                .setSmallIconResourceId(R.drawable.mic)
                .setNextActionIconResourceId(R.drawable.ic_next)
                .setPreviousActionIconResourceId(R.drawable.ic_prev)
                .setPauseActionIconResourceId(R.drawable.ic_pause)
                .setPlayActionIconResourceId(R.drawable.ic_play)
                .setChannelNameResourceId(R.string.app_name)
                .build();

        notificationManager.setPlayer(player);
        notificationManager.setPriority(NotificationCompat.PRIORITY_MAX);
        notificationManager.setUseRewindAction(false);
        notificationManager.setUseFastForwardAction(false);
    }

    @UnstableApi
    PlayerNotificationManager.NotificationListener notificationListener = new PlayerNotificationManager.NotificationListener() {
        @Override
        public void onNotificationCancelled(int notificationId, boolean dismissedByUser) {
            PlayerNotificationManager.NotificationListener.super.onNotificationCancelled(notificationId, dismissedByUser);
            stopForeground(true);
            if (player.isPlaying()) {
                player.pause();
            }
        }

        @Override
        public void onNotificationPosted(int notificationId, Notification notification, boolean ongoing) {
            PlayerNotificationManager.NotificationListener.super.onNotificationPosted(notificationId, notification, ongoing);
            startForeground(notificationId, notification);
        }
    };


    @UnstableApi
    PlayerNotificationManager.MediaDescriptionAdapter descriptionAdapter = new PlayerNotificationManager.MediaDescriptionAdapter() {
        @Override
        public CharSequence getCurrentContentTitle(Player player) {
            return player.getCurrentMediaItem().mediaMetadata.title;
        }

        @Nullable
        @Override
        public PendingIntent createCurrentContentIntent(Player player) {
            Intent openAppIntent = new Intent(getApplicationContext(), MainActivity.class);
            return PendingIntent.getActivity(getApplicationContext(), 0, openAppIntent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }

        @Nullable
        @Override
        public CharSequence getCurrentContentText(Player player) {
            return null;
        }

        @Nullable
        @Override
        public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
            return null;
        }
    };

    @OptIn(markerClass = UnstableApi.class) @Override
    public void onDestroy() {
        if (player.isPlaying()) {
            player.stop();
        }

        notificationManager.setPlayer(null);
        player.release();
        player = null;
        stopForeground(true);
        stopSelf();

        super.onDestroy();
    }
}
