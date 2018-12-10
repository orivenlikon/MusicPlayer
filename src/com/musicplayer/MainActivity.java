package com.musicplayer;

import java.util.ArrayList;
import java.util.List;

import com.musicplayer.fragment.FgLocalMusic;
import com.musicplayer.fragment.FgMusicList;
import com.musicplayer.receiver.ReceiverMain;
import com.musicplayer.service.MusicService;
import com.musicplayer.util.Constant;
import com.musicplayer.util.DBUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends Activity implements RadioGroup.OnCheckedChangeListener {

	private RadioGroup rg_bar;
	private RadioButton rb_local_music;
	private FgLocalMusic fgLocalMusic;
	private FgMusicList fgMusicList;
	private FragmentManager fManager;
	private Button bt_main_menu;
	
	SeekBar sb_main;
	public boolean Seekbar_touch = true;
	int progress_seekbar;
	ReceiverMain recMain;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		DBUtil.createTable();
		
		recMain= new ReceiverMain(this);
		ActivityManager mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> mServiceList = mActivityManager.getRunningServices(100);
		if (!MusicServiceIsStart(mServiceList, com.musicplayer.util.Constant.SERVICE_CLASS)) {

			this.startService(new Intent(this, MusicService.class));
//			Log.i("test", "ser");

			// 获得 歌曲，歌手
			SharedPreferences sp = this.getSharedPreferences("music", Context.MODE_PRIVATE);
			int musicid = sp.getInt(Constant.SHARED_ID, -1);
			int seek = sp.getInt("current", 0);

			// 发送播放请求
			Intent intent_start = new Intent(Constant.MUSIC_CONTROL);
			intent_start.putExtra("cmd", Constant.COMMAND_START);
			intent_start.putExtra("path", DBUtil.getMusicPath(musicid));
			intent_start.putExtra("current", seek);
			this.sendBroadcast(intent_start);
		}

//		recMain = new ReceiverMain(this);

		fManager = getFragmentManager();

		rg_bar = (RadioGroup) findViewById(R.id.radioGroup_bar);
		rg_bar.setOnCheckedChangeListener(this);
		

		rb_local_music = (RadioButton) findViewById(R.id.radio_local_music);
		rb_local_music.setChecked(true);
		

		bt_main_menu = (Button) findViewById(R.id.bt_main_menu);
		bt_main_menu.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PopupMenu popup = new PopupMenu(MainActivity.this, bt_main_menu);
				popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());

				popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

					@Override
					public boolean onMenuItemClick(MenuItem item) {
						// TODO Auto-generated method stub
						switch (item.getItemId()) {

						case R.id.item_delete_music:
							DBUtil.deleteTable();
							fgLocalMusic.onResume();
							break;
						case R.id.item_scan:
							Intent it = new Intent(MainActivity.this, AcitivityScan.class);
							startActivity(it);

							break;
						}
						return true;
					}
				});
				popup.show();
			}
		});

//		LinearLayout ll_main_play_bar = (LinearLayout) findViewById(R.id.ll_main_play_bar);
//		

		ImageView iv_play = (ImageView) findViewById(R.id.imageButton_play);
		iv_play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int musicid = getShared(Constant.SHARED_ID);
				if (musicid == -1) {
					Intent intent = new Intent(Constant.MUSIC_CONTROL);
					intent.putExtra("cmd", Constant.COMMAND_STOP);
					MainActivity.this.sendBroadcast(intent);
					Toast.makeText(getApplicationContext(), "歌曲不存在", Toast.LENGTH_LONG).show();
					return;
				}
				if (musicid != -1) {
					if (ReceiverMain.status == Constant.STATUS_PLAY) {
						Intent intent = new Intent(Constant.MUSIC_CONTROL);
						intent.putExtra("cmd", Constant.COMMAND_PAUSE);
						MainActivity.this.sendBroadcast(intent);
					} else if (ReceiverMain.status == Constant.STATUS_PAUSE) {
						Intent intent = new Intent(Constant.MUSIC_CONTROL);
						intent.putExtra("cmd", Constant.COMMAND_PLAY);
						MainActivity.this.sendBroadcast(intent);
					} else {
						String path = DBUtil.getMusicPath(musicid);
						Intent intent = new Intent(Constant.MUSIC_CONTROL);
						intent.putExtra("cmd", Constant.COMMAND_PLAY);
						intent.putExtra("path", path);
						MainActivity.this.sendBroadcast(intent);
					}

				}
			}
		});

		ImageView iv_next = (ImageView) findViewById(R.id.imageButton_next);
		iv_next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int playMode = getShared("playmode");
				int musicid = getShared(Constant.SHARED_ID);

				ArrayList<Integer> musiclist = DBUtil.getMusicList(getShared(Constant.SHARED_LIST));
				if (playMode == Constant.PLAYMODE_RANDOM) {
					musicid = DBUtil.getRandomMusic(musiclist, musicid);
				} else {
					musicid = DBUtil.getNextMusic(musiclist, musicid);
				}
				setShared(Constant.SHARED_ID, musicid);

				if (musicid == -1) {
					Intent intent = new Intent(Constant.MUSIC_CONTROL);
					intent.putExtra("cmd", Constant.COMMAND_STOP);
					MainActivity.this.sendBroadcast(intent);
					Toast.makeText(getApplicationContext(), "歌曲不存在", Toast.LENGTH_LONG).show();
					return;
				}

				// 获取播放数据
				String path = DBUtil.getMusicPath(musicid);

				// 发送播放请求
				Intent intent = new Intent(Constant.MUSIC_CONTROL);
				intent.putExtra("cmd", Constant.COMMAND_PLAY);
				intent.putExtra("path", path);
				MainActivity.this.sendBroadcast(intent);
			}
		});

		ImageView iv_before = (ImageView) findViewById(R.id.imageButton_before);
		iv_before.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int playMode = getShared("playmode");
				int musicid = getShared(Constant.SHARED_ID);

				ArrayList<Integer> musiclist = DBUtil.getMusicList(getShared(Constant.SHARED_LIST));
				if (playMode == Constant.PLAYMODE_RANDOM) {
					musicid = DBUtil.getRandomMusic(musiclist, musicid);
				} else {
					musicid = DBUtil.getPreviousMusic(musiclist, musicid);
				}
				setShared(Constant.SHARED_ID, musicid);

				if (musicid == -1) {
					Intent intent = new Intent(Constant.MUSIC_CONTROL);
					intent.putExtra("cmd", Constant.COMMAND_STOP);
					MainActivity.this.sendBroadcast(intent);
					Toast.makeText(getApplicationContext(), "歌曲不存在", Toast.LENGTH_LONG).show();
					return;
				}

				// 获取播放数据
				String path = DBUtil.getMusicPath(musicid);

				// 发送播放请求
				Intent intent = new Intent(Constant.MUSIC_CONTROL);
				intent.putExtra("cmd", Constant.COMMAND_PLAY);
				intent.putExtra("path", path);
				MainActivity.this.sendBroadcast(intent);
			}
		});

		sb_main = (SeekBar) findViewById(R.id.seekBar_main);
		sb_main.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				Seekbar_touch = true;
				int musicid = getShared(Constant.SHARED_ID);
				if (musicid == -1) {
					sb_main.setProgress(0);
					Intent intent = new Intent(Constant.MUSIC_CONTROL);
					intent.putExtra("cmd", Constant.COMMAND_STOP);
					MainActivity.this.sendBroadcast(intent);
					Toast.makeText(getApplicationContext(), "歌曲不存在", Toast.LENGTH_LONG).show();
					return;
				}
				// progress_seekbar=seekBar.getProgress();
				Intent intent = new Intent(Constant.MUSIC_CONTROL);
				intent.putExtra("cmd", Constant.COMMAND_PROGRESS);
				intent.putExtra("current", progress_seekbar);
				MainActivity.this.sendBroadcast(intent);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// TODO Auto-generated method stub
				progress_seekbar = progress;
				if (fromUser) {
					Seekbar_touch = false;
				}
			}
		});

		
		
	
		
		initPlayMode();
		ImageView iv_playmode = (ImageView) findViewById(R.id.iv_playmode);
		iv_playmode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ImageView iv_playmode = (ImageView) findViewById(R.id.iv_playmode);
				SharedPreferences sp = getSharedPreferences("music", Context.MODE_MULTI_PROCESS);
				SharedPreferences.Editor spEditor = sp.edit();
				int playmode = sp.getInt("playmode", Constant.PLAYMODE_SEQUENCE);
				switch (playmode) {
				case Constant.PLAYMODE_REPEATSINGLE:
					spEditor.putInt("playmode", Constant.PLAYMODE_SEQUENCE);
					spEditor.commit();
					 iv_playmode.setImageResource(R.drawable.ic_sequence);
					Toast.makeText(MainActivity.this, "顺序播放", Toast.LENGTH_SHORT).show();
					break;
				case Constant.PLAYMODE_SEQUENCE:
					spEditor.putInt("playmode", Constant.PLAYMODE_REPEATALL);
					spEditor.commit();
					 iv_playmode.setImageResource(R.drawable.ic_circle);
					Toast.makeText(MainActivity.this, "列表循环", Toast.LENGTH_SHORT).show();
					break;
				case Constant.PLAYMODE_REPEATALL:
					spEditor.putInt("playmode", Constant.PLAYMODE_RANDOM);
					spEditor.commit();
						iv_playmode.setImageResource(R.drawable.ic_random);
					Toast.makeText(MainActivity.this, "随机播放", Toast.LENGTH_SHORT).show();
					break;
				case Constant.PLAYMODE_RANDOM:
					spEditor.putInt("playmode", Constant.PLAYMODE_REPEATSINGLE);
					spEditor.commit();
						iv_playmode.setImageResource(R.drawable.ic_circlesingle);
					Toast.makeText(MainActivity.this, "单曲循环", Toast.LENGTH_SHORT).show();
					break;

				}
			}
		});

		
	}

	public static boolean MusicServiceIsStart(List<RunningServiceInfo> mServiceList, String serviceClass) // 遍历服务项、检测本程序服务是否开启。
	{
		for (int i = 0; i < mServiceList.size(); i++) {
			if (serviceClass.equals(mServiceList.get(i).service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		FragmentTransaction fTransaction = fManager.beginTransaction();
		hideAllFragment(fTransaction);
		switch (checkedId) {
		case R.id.radio_local_music:

			if (fgLocalMusic == null) {
				fgLocalMusic = new FgLocalMusic();
				fTransaction.add(R.id.fg_container, fgLocalMusic);
				fTransaction.addToBackStack(null);
			} else {
				fTransaction.show(fgLocalMusic).hide(fgMusicList);
			}
			
			break;
		case R.id.radio_list:
			if (fgMusicList == null) {
				fgMusicList = new FgMusicList();
				fTransaction.add(R.id.fg_container, fgMusicList);
				fTransaction.addToBackStack(null);
			} else {
				fTransaction.show(fgMusicList).hide(fgLocalMusic);
			}
			break;
		}
		fTransaction.commit();

	}

	private void hideAllFragment(FragmentTransaction fTransaction) {
		// TODO Auto-generated method stub
		if (fgLocalMusic != null)
			fTransaction.hide(fgLocalMusic);
		if (fgMusicList != null)
			fTransaction.hide(fgMusicList);
	}

	public void onStart() {
		super.onStart();

		// 注册广播接收器
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constant.UPDATE_STATUS);
		this.registerReceiver(recMain, filter);

	}

	// 获取sharedpreferences
	public int getShared(String key) {
		SharedPreferences sp = this.getSharedPreferences("music", Context.MODE_MULTI_PROCESS);
		int value = sp.getInt(key, -1);
		return value;
	}

	// 设置sharedpreferences
	public void setShared(String key, int value) {
		SharedPreferences sp = this.getSharedPreferences("music", Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor spEditor = sp.edit();
		spEditor.putInt(key, value);
		spEditor.commit();
	}

	// 设置播放模式
	public void initPlayMode() {
		ImageView iv_playmode = (ImageView) findViewById(R.id.iv_playmode);
		SharedPreferences sp = getSharedPreferences("music", Context.MODE_MULTI_PROCESS);
		int bofang = sp.getInt("playmode", Constant.PLAYMODE_SEQUENCE);
		switch (bofang) {
		case Constant.PLAYMODE_SEQUENCE:
			 iv_playmode.setImageResource(R.drawable.ic_sequence);
			break;
		case Constant.PLAYMODE_REPEATALL:
			
			 iv_playmode.setImageResource(R.drawable.ic_circle);
			break;
		case Constant.PLAYMODE_RANDOM:
			 iv_playmode.setImageResource(R.drawable.ic_random);
			break;
		case Constant.PLAYMODE_REPEATSINGLE:
			iv_playmode.setImageResource(R.drawable.ic_circlesingle);
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		getFragmentManager().popBackStack();
	}
	@Override
	public void onStop() {
		super.onStop();
		// 注销接收播放、暂停、停止状态更新Intent的UIUpdateReceiver
		this.unregisterReceiver(recMain);
	}
}
