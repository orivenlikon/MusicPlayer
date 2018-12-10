package com.musicplayer.service;

import com.musicplayer.receiver.ReceiverMedia;
import com.musicplayer.util.Constant;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {

	ReceiverMedia receMedia;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		receMedia = new ReceiverMedia(this);
		receMedia.mp = new MediaPlayer();
		receMedia.status = Constant.STATUS_STOP;

		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.MUSIC_CONTROL);
		this.registerReceiver(receMedia, filter);
		
		
		Notification notification = new Notification();

//		Intent notificationIntent = new Intent(this, MainActivity.class);
//		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//		notification.setLatestEventInfo(this, "֪ͨ", "MusicPlayer", pendingIntent);
		startForeground(1, notification);

	}

	public void onDestroy() {
		super.onDestroy();
		receMedia.NumberRandom();
		if (receMedia.mp != null) {
			receMedia.mp.release();
		}
		this.unregisterReceiver(receMedia);
	}

	@Override
	public void onStart(Intent intent, int id) {
		receMedia.UpdateUI(getApplicationContext());
	}

}
