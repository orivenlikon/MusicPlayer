package com.musicplayer.fragment;

import java.util.ArrayList;

import com.musicplayer.MainActivity;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FgMusicListInfo extends Fragment {

	String title;
	public static BaseAdapter ba;
	ArrayList<Integer> musiclist;
	int playlistNumber;
	View view;
	String listname;

	public FgMusicListInfo(String title, String playlistNumber) {
		this.title = title;
		this.playlistNumber = Integer.parseInt(playlistNumber);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		musiclist = DBUtil.getMusicList(playlistNumber);
		setListView();

		TextView tv_addlist = (TextView) view.findViewById(R.id.tv_list_title);
		tv_addlist.setText("返回");

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	private void setListView() {
		ba = new BaseAdapter() {
			LayoutInflater inflater = LayoutInflater.from(getActivity());

			@Override
			public int getCount() {
				return musiclist.size() + 1;
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
				LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.fragment_localmusic_listview_row, null)
						.findViewById(R.id.LinearLayout_row);
				TextView tv = (TextView) ll.getChildAt(1);
				if (arg0 == 0) {

					listname = DBUtil.getPlayListName(playlistNumber);
					tv.setText("歌单：" + listname + "（共" + musiclist.size() + "首)");

					ImageView iv = (ImageView) ll.getChildAt(0);
					iv.setImageResource(R.drawable.ic_back);
					iv.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							Intent it = new Intent(getActivity(), MainActivity.class);
							startActivity(it);
							// getFragmentManager().popBackStack();
						}
					});
					return ll;
				}
				String musicName = DBUtil.getMusicInfo(musiclist.get(arg0 - 1)).get(0);

				tv.setText(musicName);
				return ll;
			}

		};

		ListView lv = (ListView) getActivity().findViewById(R.id.show_music_list);

		lv.setAdapter(ba);

		if (title.equals(Constant.FRAGMENT_MYLIST)) {
			lv.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					if (arg2 != 0) {
						final int selectTemp = arg2;
						AlertDialog.Builder builder = new Builder(getActivity());
						builder.setTitle("更多功能");
						builder.setItems(new String[] { "从歌单中删除" }, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
								DBUtil.deleteMusicInList(musiclist.get(selectTemp - 1), playlistNumber);
								musiclist = DBUtil.getMusicList(playlistNumber);
								ba.notifyDataSetChanged();
							}
						}).create().show();
					}
					return true;
				}
			});
		}

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				if (arg2 == 0) {
					return;
				}
				SharedPreferences sp = getActivity().getSharedPreferences("music", Context.MODE_MULTI_PROCESS);
				SharedPreferences.Editor spEditor = sp.edit();

				spEditor.putInt(Constant.SHARED_LIST, playlistNumber);

				spEditor.commit();

				// 获取所点歌曲的ID;
				int musicid = musiclist.get(arg2 - 1);

				// 播放所选歌曲
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
				TextView tv_gequ = (TextView) getActivity().findViewById(R.id.textView_musicname);
				TextView tv_geshou = (TextView) getActivity().findViewById(R.id.textView_singer);
				tv_gequ.setText(musicinfo.get(1));
				tv_geshou.setText(musicinfo.get(2));

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
		lv.setOnCreateContextMenuListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fg_music_list, container, false);
		return super.onCreateView(inflater, container, savedInstanceState);
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
