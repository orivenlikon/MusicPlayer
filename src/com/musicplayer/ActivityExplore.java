package com.musicplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.musicplayer.receiver.ReceiverMain;
import com.musicplayer.util.Constant;
import com.musicplayer.util.DBUtil;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class ActivityExplore extends ListActivity {
	
	private List<String> items = null;
	private TextView store_Card;
	BaseAdapter ba;
	ListView localFile;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.directory_list);
	
		fill(new File(Environment.getExternalStorageDirectory().toString()).listFiles());

	}
	
	private void fill(File[] files) {
		items = new ArrayList<String>();
		items.add(getString(R.string.to_top));
		for (File file : files) {
			if (file.isDirectory()) {
				if ((file.getPath().indexOf("/sdcard")) != -1 || (file.getPath().indexOf("/system")) != -1)
					items.add(file.getPath());
			}
			if ((file.getPath().indexOf(".mp3")) != -1) {
				items.add(file.getPath());
			}
		}
		setListView();

	}
	
	private void setListView() {

	
		
	}
}
