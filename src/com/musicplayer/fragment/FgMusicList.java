package com.musicplayer.fragment;

import java.util.ArrayList;

import com.musicplayer.R;
import com.musicplayer.util.Constant;
import com.musicplayer.util.DBUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FgMusicList extends Fragment {
	public static BaseAdapter ba;
	ArrayList<String[]> playlist;
	View view;

	public FgMusicList() {

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.fg_music_list, container, false);
		
		
		TextView tv_addlist=(TextView)view.findViewById(R.id.tv_addlist);
		tv_addlist.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final View view=v;
				final EditText etTemp = new EditText(getActivity());
				etTemp.setSingleLine();
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle("创建歌单");
				builder.setView(etTemp);
				builder.setPositiveButton("确定",new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog,int which) 
					{
						dialog.dismiss();
						HideKeyboard(view);
						DBUtil.createPlayList(etTemp.getText().toString());
						playlist = DBUtil.getPlayList();
						ba.notifyDataSetChanged();
					}
				});
				builder.setNegativeButton("取消",new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog,int which)
					{
						dialog.dismiss();
					}
				});
				builder.create().show();
			}
		});
		return view;

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		playlist = DBUtil.getPlayList();
		TextView tv_summusic = (TextView) view.findViewById(R.id.tv_list_summusic);
		tv_summusic.setText("歌单总计:" + playlist.size());
		setListView();
	}

	

	private void setListView() {
		ba = new BaseAdapter() {
			LayoutInflater inflater = LayoutInflater.from(getActivity());

			public void notifyDataSetChanged() {
				// TODO Auto-generated method stub
				super.notifyDataSetChanged();
				TextView tv_summusic = (TextView) view.findViewById(R.id.tv_list_summusic);
				tv_summusic.setText("歌单总计:" + playlist.size());

			}

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

				LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.fragment_localmusic_listview_row, null).findViewById(R.id.LinearLayout_row);
				TextView tv = (TextView) ll.getChildAt(1);
				String listname = playlist.get(arg0)[1];
				ImageView iv_list = (ImageView)ll.getChildAt(0);
				iv_list.setImageResource(R.drawable.ic_list);
				tv.setText(listname);
				return ll;
			}
		};

		ListView lv = (ListView) getActivity().findViewById(R.id.show_music_list);

		lv.setAdapter(ba);

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				
				FgMusicListInfo fragment=new FgMusicListInfo(Constant.FRAGMENT_MYLIST,playlist.get(arg2)[0]);
				FgMusicList.changeFragment(fragmentTransaction, fragment);
			
			}
		});

		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				final int selectTemp = arg2;
				AlertDialog.Builder builder = new Builder(getActivity());
				builder.setTitle("更多功能");
				builder.setItems(new String[] { "删除歌单" }, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							int temp = Integer.parseInt(playlist.get(selectTemp)[0]);
							DBUtil.deletePlayList(temp);
							playlist = DBUtil.getPlayList();
							ba.notifyDataSetChanged();
							break;
						}
						dialog.dismiss();
					}
				}).create().show();
				return true;
			}
		});
	}

	protected static void changeFragment(FragmentTransaction fragmentTransaction, FgMusicListInfo fragment) {
		// TODO Auto-generated method stub
		fragmentTransaction.replace(R.id.show_music_list,fragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}
	
	public static void HideKeyboard(View v)
    {
      InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );     
    if ( imm.isActive( ) ) {     
        imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );   
        
    }    
  }


}
