package com.musicplayer.util;

public class Constant {
	public static final int COMMAND_PLAY = 10; // 播放命令
	public static final int COMMAND_PAUSE = 11; // 暂停命令
	public static final int COMMAND_PROGRESS = 12; // 设置播放位置
	public static final int COMMAND_STOP = 13; // 停止命令
	public static final int COMMAND_GO = 14;
	public static final int COMMAND_START = 15;
	public static final int COMMAND_PLAYMODE=16;
	//播放状态
	public static final int STATUS_PLAY = 4; // 播放状态
	public static final int STATUS_PAUSE = 5; // 暂停状态
	public static final int STATUS_STOP = 6; // 停止状态
	//播放模式
	public static final int PLAYMODE_REPEATALL=21;
	public static final int PLAYMODE_REPEATSINGLE=22;
	public static final int PLAYMODE_SEQUENCE=23;
	public static final int PLAYMODE_RANDOM=24;
	
	//歌曲列表常量
	public static final int LIST_ALLMUSIC=-31;
	public static final int LIST_ILIKE=32;
	public static final int LIST_LASTPLAY=33;
	public static final int LIST_DOWNLOAD=34;	
	//sharedpreferences
	public static final String SHARED="music";
	public static final String SHARED_ID="musicid";
	public static final String SHARED_LIST="list";
	//fragment播放
	public static final String FRAGMENT_MUSIC="全部歌曲";
	public static final String FRAGMENT_SINGER="歌手";
	public static final String FRAGMENT_MYLIST="我的歌单";
	
	
	public static final String MUSIC_CONTROL="music_control";
	public static final String SERVICE_CLASS="com.musicplayer.service.MusicService";
	public static final String UPDATE_STATUS="update_statu";
	


}
