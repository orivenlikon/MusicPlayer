package com.musicplayer.receiver;

import java.util.ArrayList;

import com.musicplayer.service.MusicService;
import com.musicplayer.thread.MusicPlayerThread;
import com.musicplayer.util.Constant;
import com.musicplayer.util.DBUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;
import android.widget.Toast;

public class ReceiverMedia extends BroadcastReceiver {

	public MediaPlayer mp;
	public int status = Constant.STATUS_STOP;
	public int playMode;
	public int threadNumber;
	Context context;

	MusicService ms;

	public ReceiverMedia(MusicService ms) {

		this.ms = ms;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		this.context = context;
		switch (intent.getIntExtra("cmd", -1)) {
	
		case Constant.COMMAND_START:// Media��ʼ��
			NumberRandom();
			String musicpath = intent.getStringExtra("path");
			int musiccurrent = intent.getIntExtra("current", 0);
			if (musiccurrent == 0)// �������Ϊ0����Ҫ���д˲�����
			{
				return;
			}
			if (mp != null) {
				mp.release();
			}
			
			mp = new MediaPlayer();
			mp.setOnCompletionListener(new OnCompletionListener() {
				// ���ò�����ɺ�Ĳ���.
				@Override
				public void onCompletion(MediaPlayer mp) {
					
					NumberRandom();
					onComplete(mp);
					UpdateUI();
				}
			});
			if (musicpath == null)
			{
				return;
			}
			try {
				boolean flag = intent.getBooleanExtra("flag", true);
				mp.setDataSource(musicpath);// ����MediaPlayer����Դ
				mp.prepare();// MediaPlayer��׼��
			
				mp.start();// ��ʼ����
				mp.seekTo(musiccurrent);// ��ת����
				status = Constant.STATUS_PLAY;// ���ò���״̬Ϊ����
				if (flag)// �����־λΪ�棬�������ͣ���ڽ���Activityʱ�����á�
				{
					mp.pause();
				
					status = Constant.STATUS_PAUSE;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				
			}
			break;
		case Constant.COMMAND_PLAY:
			String path = intent.getStringExtra("path");
			if (path != null) // ���·��Ϊ�����ʾ��������ͣ״̬������״̬
			{
				commandPlay(path);
			} else 
			{
				mp.start();
				
				try {
					
				} catch (Exception e) {
					e.printStackTrace();
					status = Constant.STATUS_STOP;
					break;
				}
			}
			status = Constant.STATUS_PLAY;// ���Ĳ���״̬
			break;
		case Constant.COMMAND_STOP:// ֹͣ
			
			status = Constant.STATUS_STOP;
			if (mp != null) {
				mp.release();
			}
		
			break;
		case Constant.COMMAND_PAUSE:// ��ͣ
			status = Constant.STATUS_PAUSE;
			mp.pause();
			
			break;
		case Constant.COMMAND_PROGRESS:// �϶�����
			int current = intent.getIntExtra("current", 0);
			Log.i("p", Integer.toString(current));
			mp.seekTo(current);
			break;
		}
		UpdateUI();

	}

	private void commandPlay(String path) // �������ֵķ���
	{
		NumberRandom();
		if (mp != null) {
			mp.release();// �ͷŲ�����
		}

		mp = new MediaPlayer();
		mp.setOnCompletionListener(new OnCompletionListener()// ������ɼ���
		{
			@Override
			public void onCompletion(MediaPlayer mp) {
				
				NumberRandom();
				onComplete(mp);
				UpdateUI();
			}
		});
		try {
			mp.setDataSource(path);// ��MediaPlayer��������Դ
			mp.prepare();// ׼��MediaPlayer
			mp.start();// ��ʼ����
			new MusicPlayerThread(this, context, threadNumber).start();
		} catch (Exception e) {
			e.printStackTrace();

		}
		status = Constant.STATUS_PLAY;
	}

	public void NumberRandom() 
	{
		int numberTemp;
		do 
		{
			numberTemp = (int) (Math.random() * 30);
		}
		while (numberTemp == threadNumber);
		threadNumber = numberTemp;
	}
	public void UpdateUI() {

		Intent intent = new Intent(Constant.UPDATE_STATUS);// ���͸���Activity�Ĺ㲥
		intent.putExtra("status", status);
		context.sendBroadcast(intent);
	}

	public void UpdateUI(Context context)//����һ��������һ��
	{
		
		
		Intent intent = new Intent(Constant.UPDATE_STATUS);
		intent.putExtra("status", status);
		context.sendBroadcast(intent);
	}
	public void onComplete(MediaPlayer mp) {
		SharedPreferences sp = ms.getSharedPreferences("music", Context.MODE_MULTI_PROCESS);// ��ȡSharedPreferences������
		int musicid = sp.getInt(Constant.SHARED_ID, -1);// ������ڲ��ŵ�����id
		int playMode = sp.getInt("playmode", Constant.PLAYMODE_SEQUENCE);// ��õ�ǰ�Ĳ���ģʽ
		int list = sp.getInt(Constant.SHARED_LIST, Constant.LIST_ALLMUSIC);// ��õ�ǰ�Ĳ����б�
		ArrayList<Integer> musicList = DBUtil.getMusicList(list);// ��ø��������б�
		if (musicid == -1)// �����ǰ���Ÿ����������򷵻�
		{
			return;
		}
		if (musicList.size() == 0)// ��������б�Ϊ���򷵻�
		{
			return;
		}
		String playpath;
		switch (playMode) {
		case Constant.PLAYMODE_REPEATSINGLE:// ����ѭ��ģʽ
			playpath = DBUtil.getMusicPath(musicid);// ��ø�����ַ
			commandPlay(playpath);
			break;
		case Constant.PLAYMODE_REPEATALL:// �б�ѭ��ģʽ
			musicid = DBUtil.getNextMusic(musicList, musicid);// �����һ�׸���
			playpath = DBUtil.getMusicPath(musicid);
			commandPlay(playpath);
			break;
		case Constant.PLAYMODE_SEQUENCE:// �б���ģʽ
			if (musicList.get(musicList.size() - 1) == musicid) // �ж��Ƿ�Ϊ�����б�����һ��
			{

				mp.release();
				status = Constant.STATUS_STOP;
				Toast.makeText(context, "�ѵ��ﲥ���б�����������ѡ��", Toast.LENGTH_LONG).show();
			} else {
				musicid = DBUtil.getNextMusic(musicList, musicid);
				playpath = DBUtil.getMusicPath(musicid);
				commandPlay(playpath);
			}
			break;
		case Constant.PLAYMODE_RANDOM:// �������ģʽ
			musicid = DBUtil.getRandomMusic(musicList, musicid);// ����������id
			playpath = DBUtil.getMusicPath(musicid);
			commandPlay(playpath);
			break;
		}
		SharedPreferences.Editor spEditor = sp.edit();// ��ñ༭SharedPreferences������
		spEditor.putInt(Constant.SHARED_ID, musicid);// ��������id
		spEditor.commit();
		UpdateUI();
	}
}
