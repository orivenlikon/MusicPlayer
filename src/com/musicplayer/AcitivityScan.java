package com.musicplayer;

import java.io.File;
import java.util.ArrayList;

import com.musicplayer.util.Constant;
import com.musicplayer.util.DBUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

public class AcitivityScan extends Activity {
	boolean thread_flag;
	int degrees = 0;
	
	

	Handler handler;
//	SQLiteDatabase musicData;

	ArrayList<String> Music_filename = new ArrayList<String>();
	ArrayList<String> Music_name = new ArrayList<String>();
	ArrayList<String> Music_singer = new ArrayList<String>();
	ArrayList<String> Music_path = new ArrayList<String>();

	int progress = 0;
	int music_number = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_scanning);
		AlertDialog alert = new AlertDialog.Builder(AcitivityScan.this).create();
		alert.setTitle("提示");
		alert.setMessage("是否开始扫描");
		alert.setButton(DialogInterface.BUTTON_POSITIVE, "确认", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				 
				dialog.dismiss();
				
				DBUtil.deleteTable();
				File sdcardFile = Environment.getExternalStorageDirectory();
//				String url = sdcardFile.toString();
				String url="/sdcard/zapya";
				scanMusic(url);
				boolean flag = musicToDB();
				if (flag) {
					
					AlertDialog alert = new AlertDialog.Builder(AcitivityScan.this).create();
					alert.setTitle("消息");
					alert.setMessage("扫描完成");
					alert.setButton(DialogInterface.BUTTON_POSITIVE, "确认", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							finish();
						}
					});
					alert.show();
				}
			}
		});
		alert.show();

	}

	private void scanMusic(String url) {
		File files = new File(url); // 创建文件对象
		File[] file = files.listFiles();
		try {
			for (File f : file) { // 通过for循环遍历获取到的文件数组
				if (f.isDirectory()) { // 如果是目录，也就是文件夹
					scanMusic(f.getAbsolutePath()); // 递归调用
				} else {
					if (isAudioFile(f.getPath())) { // 如果是音频文件
						String filename = f.getName();
						String filepath = f.getAbsolutePath();
						File fileTemp = new File(filepath);
						if (fileTemp.length() < 10) {
							continue;
						}

						String[] fileinfo = filename.split(" - ");
						if (fileinfo.length != 1) {
							music_number++;

							Music_filename.add(filename.substring(0, filename.length() - 4));
							Music_name.add(fileinfo[1].substring(0, fileinfo[1].length() - 4).trim());
							Music_singer.add(fileinfo[0].trim());
							Music_path.add(filepath);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); // 输出异常信息
		}
	}

	// 判断是否为音频文件
	private static boolean isAudioFile(String path) {

		if (path.endsWith(".mp3")) { // 判断是否为有合法的音频文件
			return true;
		}

		return false;
	}

	private boolean musicToDB() {
		try {

			for (int i = 0; i < Music_filename.size(); i++) {

				String temp[] = { Music_filename.get(i), Music_name.get(i), Music_singer.get(i), Music_path.get(i) };
				DBUtil.setMusic(temp);
//				Log.i("filename",Music_filename.get(i)+"###"+Music_name.get(i)+"##"+Music_singer.get(i)+"##"+Music_path.get(i));
				
			}
			SharedPreferences sp = getSharedPreferences("music", Context.MODE_MULTI_PROCESS);
			SharedPreferences.Editor spEditor = sp.edit();
			spEditor.putInt(Constant.SHARED_ID, 1);
			spEditor.commit();

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
/*//			if (musicData != null) {
//				musicData.close();
//			}
*/		}
		return true;

	}
}
