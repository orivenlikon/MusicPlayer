package com.musicplayer.receiver;

import java.util.ArrayList;

import com.musicplayer.MainActivity;
import com.musicplayer.R;
import com.musicplayer.fragment.FgLocalMusic;
import com.musicplayer.util.Constant;
import com.musicplayer.util.DBUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class ReceiverMain extends BroadcastReceiver {

	public static int status = Constant.STATUS_STOP;
	MainActivity mainAc;
	ArrayList<String> musicinfo;
	int updateTime = 0;
	int duration = 0;
	int current = 0;

	public ReceiverMain(MainActivity ma) {

		this.mainAc = ma;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		int statustemp = intent.getIntExtra("status", -1);
			ImageView iv_play = (ImageView) mainAc.findViewById(R.id.imageButton_play);

		try {
			SharedPreferences sp = mainAc.getSharedPreferences("music", Context.MODE_MULTI_PROCESS);
			int musicid = sp.getInt(Constant.SHARED_ID, -1);
			TextView tv_musicname= (TextView) mainAc.findViewById(R.id.textView_musicname);
			TextView tv_singer = (TextView) mainAc.findViewById(R.id.textView_singer);
			tv_musicname.setText(DBUtil.getMusicInfo(musicid).get(1));
			tv_singer.setText(DBUtil.getMusicInfo(musicid).get(2));
			FgLocalMusic.ba.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}

		switch (statustemp) {
		case Constant.STATUS_PLAY:
			status = statustemp;
			//MusicUpdatePlay.status = statustemp;
			iv_play.setImageResource(R.drawable.ic_pause);
			break;
		
		case Constant.STATUS_PAUSE:
			status = statustemp;
			//MusicUpdatePlay.status = statustemp;
			iv_play.setImageResource(R.drawable.ic_play);
			break;
		case Constant.COMMAND_GO:
			if (status != intent.getIntExtra("status2", Constant.STATUS_STOP)) {
				status = intent.getIntExtra("status2", Constant.STATUS_STOP);
				//MusicUpdatePlay.status = status;
				if (status == Constant.STATUS_PLAY) {
					iv_play.setImageResource(R.drawable.ic_pause);
				}
			}
			if (mainAc.Seekbar_touch) {
				SeekBar sb = (SeekBar) mainAc.findViewById(R.id.seekBar_main);
				duration = intent.getIntExtra("duration", 0);
				current = intent.getIntExtra("current", 0);
				sb.setMax(duration);
				sb.setProgress(current);
				updateTime++;
				if (updateTime > 10) {
					updateTime = 0;
					try {
						FgLocalMusic.ba.notifyDataSetChanged();
					} catch (Exception e) {

					}
					try {
						//MusicFragmentFour.ba.notifyDataSetChanged();
					} catch (Exception e) {

					}
				}
			}
			
		}
	}

	public String fromMsToMinuteStr(int ms) 
	{
		ms = ms / 1000;
		int minute = ms / 60;
		int second = ms % 60;
		return minute + ":" + ((second > 9) ? second : "0" + second);
	}
	
}
