package com.musicplayer.util;

public class Constant {
	public static final int COMMAND_PLAY = 10; // ��������
	public static final int COMMAND_PAUSE = 11; // ��ͣ����
	public static final int COMMAND_PROGRESS = 12; // ���ò���λ��
	public static final int COMMAND_STOP = 13; // ֹͣ����
	public static final int COMMAND_GO = 14;
	public static final int COMMAND_START = 15;
	public static final int COMMAND_PLAYMODE=16;
	//����״̬
	public static final int STATUS_PLAY = 4; // ����״̬
	public static final int STATUS_PAUSE = 5; // ��ͣ״̬
	public static final int STATUS_STOP = 6; // ֹͣ״̬
	//����ģʽ
	public static final int PLAYMODE_REPEATALL=21;
	public static final int PLAYMODE_REPEATSINGLE=22;
	public static final int PLAYMODE_SEQUENCE=23;
	public static final int PLAYMODE_RANDOM=24;
	
	//�����б���
	public static final int LIST_ALLMUSIC=-31;
	public static final int LIST_ILIKE=32;
	public static final int LIST_LASTPLAY=33;
	public static final int LIST_DOWNLOAD=34;	
	//sharedpreferences
	public static final String SHARED="music";
	public static final String SHARED_ID="musicid";
	public static final String SHARED_LIST="list";
	//fragment����
	public static final String FRAGMENT_MUSIC="ȫ������";
	public static final String FRAGMENT_SINGER="����";
	public static final String FRAGMENT_MYLIST="�ҵĸ赥";
	
	
	public static final String MUSIC_CONTROL="music_control";
	public static final String SERVICE_CLASS="com.musicplayer.service.MusicService";
	public static final String UPDATE_STATUS="update_statu";
	


}
