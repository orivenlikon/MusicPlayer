package com.musicplayer.fragment;

import java.util.ArrayList;

import com.musicplayer.R;
import com.musicplayer.receiver.ReceiverMain;
import com.musicplayer.util.Constant;
import com.musicplayer.util.DBUtil;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FgLocalMusic extends Fragment {

	private ListView localMusic;
	public static BaseAdapter ba;

	ArrayList<Integer> musiclist;
	ArrayList<String[]> playlist;
	View view;

	int selectTemp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fg_local_music, container, false);

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onStart();
		super.onResume();

		musiclist = DBUtil.getMusicList(Constant.LIST_ALLMUSIC);
		TextView tv_summusic = (TextView) view.findViewById(R.id.tv_summusic);
		tv_summusic.setText("共计:" + musiclist.size() + "首歌");

		

		setListView();
	}

	private void setListView() {

		ba = new BaseAdapter() {

			LayoutInflater inflater = LayoutInflater.from(getActivity());

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				// TODO Auto-generated method stub

				SharedPreferences sp = getActivity().getSharedPreferences("music", Context.MODE_MULTI_PROCESS);
				int musicid = sp.getInt(Constant.SHARED_ID, -1);
				LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.fragment_localmusic_listview_row, null)
						.findViewById(R.id.LinearLayout_row);
				TextView tv = (TextView) ll.getChildAt(1);
				String musicName = DBUtil.getMusicInfo(musiclist.get(position)).get(0);

				tv.setText(musicName);

				if (musiclist.get(position) == musicid) {
					tv.setTextColor(getResources().getColor(R.color.blue));
				}
				return ll;

			}

			@Override
			public long getItemId(int position) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public Object getItem(int position) {
				// TODO Auto-generated method stub
				return ba;
			}

			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return musiclist.size();
			}

			@Override
			public void notifyDataSetChanged() {
				// TODO Auto-generated method stub
				super.notifyDataSetChanged();
				TextView tv_summusic = (TextView) view.findViewById(R.id.tv_summusic);
				tv_summusic.setText("共计:" + musiclist.size() + "首歌");

			}
		};

		localMusic = (ListView) getActivity().findViewById(R.id.show_local_music);

		localMusic.setAdapter(ba);

		localMusic.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				SharedPreferences sp = getActivity().getSharedPreferences("music", Context.MODE_MULTI_PROCESS);
				SharedPreferences.Editor spEditor = sp.edit();
				spEditor.putInt(Constant.SHARED_LIST, Constant.LIST_ALLMUSIC);
				spEditor.commit();

				// 获取所点歌曲的ID;
				int musicid = musiclist.get(arg2);

				boolean flag;
				int oldmusicplay = sp.getInt(Constant.SHARED_ID, -1);

				// 判断是否点击的是正在播放的歌曲
				if (oldmusicplay == musicid) {
					flag = false;
				} else {
					flag = true;
					spEditor.putInt(Constant.SHARED_ID, musicid);
					spEditor.commit();
				}

				ArrayList<String> musicinfo = DBUtil.getMusicInfo(musicid);
				TextView tv_musicname = (TextView) getActivity().findViewById(R.id.textView_musicname);
				TextView tv_singer = (TextView) getActivity().findViewById(R.id.textView_singer);
				tv_musicname.setText(musicinfo.get(1));
				tv_singer.setText(musicinfo.get(2));

				if (flag) {
					sendintent(Constant.STATUS_STOP);
				} else {
					if (ReceiverMain.status == Constant.STATUS_PLAY) {
						sendintent(Constant.STATUS_PLAY);
					} else if (ReceiverMain.status == Constant.STATUS_STOP) {
						sendintent(Constant.STATUS_STOP);
					} else {
						sendintent(Constant.STATUS_PAUSE);
					}
				}
			}
		});
		localMusic.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				selectTemp = arg2;
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle("更多功能");
				builder.setItems(new String[] { "删除歌曲", "添加到歌单" }, new DialogInterface.OnClickListener() {
					@SuppressWarnings("deprecation")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							int musicTemp = musiclist.get(selectTemp);
							DBUtil.deleteMusic(musicTemp);
							SharedPreferences sp = getActivity().getSharedPreferences("music",
									Context.MODE_MULTI_PROCESS);
							int musicid = sp.getInt(Constant.SHARED_ID, -1);
							musiclist = DBUtil.getMusicList(Constant.LIST_ALLMUSIC);
							if (musicid == musicTemp) {
								if (musiclist.isEmpty()) {
									musicid = -1;
								} else {
									musicid = musiclist.get(0);
								}
								SharedPreferences.Editor spEditor = sp.edit();
								spEditor.putInt(Constant.SHARED_ID, musicid);
								spEditor.commit();
								Intent intent_start = new Intent();
								intent_start.putExtra("cmd", Constant.COMMAND_PLAY);
								intent_start.putExtra("path", DBUtil.getMusicPath(musicid));
								getActivity().sendBroadcast(intent_start);
								Intent intent_pause = new Intent();
								intent_pause.putExtra("cmd", Constant.COMMAND_PAUSE);
								getActivity().sendBroadcast(intent_pause);
							}
							ba.notifyDataSetChanged();
							break;
						case 1:
							playlist = DBUtil.getPlayList();
							LinearLayout ll_list = new LinearLayout(getActivity());
							ll_list.setLayoutParams(
									new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
							// ll_list.setLayoutDirection(0);
							ListView listview = new ListView(getActivity());
							listview.setLayoutParams(
									new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
							ll_list.addView(listview);
							final AlertDialog aDialog = new AlertDialog.Builder(getActivity()).create();
							aDialog.setTitle("歌单列表(" + playlist.size() + ")");
							aDialog.setView(ll_list);
							aDialog.show();

							// 设置listview适配器
							BaseAdapter ba = new BaseAdapter() {
								LayoutInflater inflater = LayoutInflater.from(getActivity());

								@Override
								public int getCount() {
									return playlist.size();
								}

								@Override
								public Object getItem(int arg0) {
									return null;
								}

								@Override
								public long getItemId(int arg0) {
									return 0;
								}

								@Override
								public View getView(int arg0, View arg1, ViewGroup arg2) {
									String musicName = playlist.get(arg0)[1];
									LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.list_w, null);
									TextView tvtemp = (TextView) ll.getChildAt(0);
									tvtemp.setText((arg0 + 1) + "");
									TextView tv = (TextView) ll.getChildAt(1);
									tv.setText(musicName);
									return ll;
								}
							};

							listview.setAdapter(ba);

							// 响应listview中的item的点击事件
							listview.setOnItemClickListener(new OnItemClickListener() {
								@Override
								public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
									aDialog.cancel();
									int listid = Integer.parseInt(playlist.get(arg2)[0]);
									DBUtil.setMusicInPlaylist(musiclist.get(selectTemp), listid);
									// FgMusicListInfo.ba.notifyDataSetChanged();
								}
							});
						}
						dialog.dismiss();
					}
				}).create().show();
				return true;
			}

		});
		localMusic.setOnCreateContextMenuListener(this);
	}

	private void sendintent(int code) {
		switch (code) {
		case Constant.STATUS_PLAY:
			Intent intentplay = new Intent(Constant.MUSIC_CONTROL);
			intentplay.putExtra("cmd", Constant.COMMAND_PAUSE);
			this.getActivity().sendBroadcast(intentplay);
			break;
		case Constant.STATUS_STOP:
			SharedPreferences sp = getActivity().getSharedPreferences("music", Context.MODE_MULTI_PROCESS);
			int musicid = sp.getInt(Constant.SHARED_ID, -1);
			String playpath = DBUtil.getMusicPath(musicid);
			Intent intentstop = new Intent(Constant.MUSIC_CONTROL);
			intentstop.putExtra("cmd", Constant.COMMAND_PLAY);
			intentstop.putExtra("path", playpath);
			this.getActivity().sendBroadcast(intentstop);
			break;
		case Constant.STATUS_PAUSE:
			Intent intentpause = new Intent(Constant.MUSIC_CONTROL);
			intentpause.putExtra("cmd", Constant.COMMAND_PLAY);
			this.getActivity().sendBroadcast(intentpause);
			break;
		}

	}

}
