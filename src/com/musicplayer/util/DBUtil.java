package com.musicplayer.util;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBUtil {

	public static SQLiteDatabase getMusicData() {
		SQLiteDatabase musicData = null;
		musicData = SQLiteDatabase.openDatabase("/data/data/com.musicplayer/musicdata", null,
				SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY);
		return musicData;
	}

	// 创建表
	public static void createTable() {
		SQLiteDatabase musicData = getMusicData();
		String sqlcreate = "create table if not exists musicdata(";
		sqlcreate += "musicid integer PRIMARY KEY,filename varchar(100),music varchar(50),singer varchar(50),";
		sqlcreate += "path varchar(200));";

		musicData.execSQL(sqlcreate);

		String sqlplaylist = "create table if not exists playlist(listid integer PRIMARY KEY,listname varchar(20));";
		musicData.execSQL(sqlplaylist);
		String sqllistinfo = "create table if not exists listinfo(listid integer ,musicid integer,";
		sqllistinfo += "FOREIGN KEY(listid) REFERENCES playlist(listid), FOREIGN KEY(musicid) REFERENCES musicdata(musicid));";
		musicData.execSQL(sqllistinfo);
		musicData.close();

	}

	public static void createPlayList(String listname) {

		Cursor cur = null;
		SQLiteDatabase musicData = getMusicData();
		int listId = 0;
		try {
			String sql = "select max(listid) from playlist;";
			cur = musicData.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				listId = cur.getInt(0) + 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			String sqlinsert = "insert into playlist values(" + listId + ",'" + listname + "');";
			musicData.execSQL(sqlinsert);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cur != null) {
				cur.close();
			}
			musicData.close();
		}
	}

	public static void deleteTable() {
		SQLiteDatabase musicData = getMusicData();
		try {
			musicData.delete("listinfo", null, null);
			musicData.delete("playlist", null, null);

			musicData.delete("musicdata", null, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			musicData.close();
		}
	}

	public static void deletePlayList(int listid) {
		SQLiteDatabase musicData = getMusicData();
		String sql = "delete from playlist where listid=" + listid + ";";
		musicData.execSQL(sql);
		musicData.close();
	}

	public static void deleteMusic(int musicid)

	{
		SQLiteDatabase musicData = getMusicData();
		String sql = "delete from listinfo where musicid=" + musicid + ";";
		musicData.execSQL(sql);

		String sql4 = "delete from musicdata where musicid=" + musicid + ";";
		musicData.execSQL(sql4);
		musicData.close();
	}

	public static void deleteMusicInList(int musicid, int listid) {
		SQLiteDatabase musicData = getMusicData();
		String sql = "delete from listinfo where musicid=" + musicid + " and listid=" + listid + ";";
		musicData.execSQL(sql);
		musicData.close();
	}

	public static int setMusic(String[] musicInfo) {
		SQLiteDatabase musicData = getMusicData();
		Cursor cur = null;

		int musicId = -1;
		try {
			String sql = "select max(musicid) from musicdata;";
			cur = musicData.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				musicId = cur.getInt(0) + 1;
			} else {
				musicId = 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			String sql2 = "insert into musicdata values (";
			sql2 += musicId + ",'";
			sql2 += musicInfo[0] + "','";
			sql2 += musicInfo[1] + "','";
			sql2 += musicInfo[2] + "','";
			sql2 += musicInfo[3] + "');'";

			musicData.execSQL(sql2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cur != null) {
				cur.close();
			}
			musicData.close();
		}
		return musicId;
	}

	public static void setMusicInPlaylist(int musicid, int listid) {
		SQLiteDatabase musicData = getMusicData();
		try {
			String sql2 = "insert into listinfo values(" + listid + "," + musicid + ");";
			musicData.execSQL(sql2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			musicData.close();
		}
	}

	// 获取歌单列表
	public static ArrayList<String[]> getPlayList() {
		SQLiteDatabase musicData = null;
		Cursor cur = null;
		ArrayList<String[]> file = new ArrayList<String[]>();
		try {
			musicData = getMusicData();
			String sql = "select listid,listname from playlist;";
			cur = musicData.rawQuery(sql, null);
			while (cur.moveToNext()) {
				String[] tempStr = { cur.getInt(0) + "", cur.getString(1) };
				file.add(tempStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cur != null) {
				cur.close();
			}
			musicData.close();
		}
		return file;
	}

	public static String getMusicPath(int id) {
		if (id == -1) {
			return null;
		}

		SQLiteDatabase musicData = null;
		Cursor cur = null;
		String path = null;
		try {
			musicData = getMusicData();
			String sql = "select path from musicdata where musicid=" + id + ";";
			cur = musicData.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				path = cur.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cur != null) {
				cur.close();
			}
			musicData.close();
		}
		return path;
	}

	public static int getMusicCount() {
		SQLiteDatabase musicData = null;
		Cursor cur = null;
		int musicCount = 0;
		try {
			musicData = getMusicData();
			String sql = "select count(*) from musicdata;";
			cur = musicData.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				musicCount = cur.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cur != null) {
				cur.close();
			}
			musicData.close();
		}
		return musicCount;
	}

	public static ArrayList<Integer> getMusicList(int listNumber) {
		SQLiteDatabase musicData = null;
		Cursor cur = null;
		ArrayList<Integer> file = null;

		if (listNumber == Constant.LIST_ALLMUSIC) {
			file = new ArrayList<Integer>();
			int music = -1;
			try {
				musicData = getMusicData();
				String sql = "select musicid from musicdata order by singer DESC;";
				cur = musicData.rawQuery(sql, null);
				while (cur.moveToNext()) {
					music = cur.getInt(0);
					file.add(music);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cur != null) {
					cur.close();
				}
				musicData.close();
			}
		}

		else {
			file = new ArrayList<Integer>();
			int music = -1;
			try {
				musicData = getMusicData();
				String sql = "select musicid from listinfo where listid=" + listNumber + ";";
				cur = musicData.rawQuery(sql, null);
				while (cur.moveToNext()) {
					music = cur.getInt(0);
					file.add(music);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cur != null) {
					cur.close();
				}
				musicData.close();
			}
		}

		return file;
	}

	// 获取下一首歌曲
	public static int getNextMusic(ArrayList<Integer> musicList, int id) {
		if (id == -1) {
			return -1;
		}
		for (int i = 0; i < musicList.size(); i++) {
			if (id == musicList.get(i)) {
				if (i == musicList.size() - 1) {
					return musicList.get(0);
				} else {
					++i;
					return musicList.get(i);
				}
			}
		}
		return -1;
	}

	// 获取上一首歌曲
	public static int getPreviousMusic(ArrayList<Integer> musicList, int id) {
		if (id == -1) {
			return -1;
		}
		for (int i = 0; i < musicList.size(); i++) {
			if (id == musicList.get(i)) {
				if (i == 0) {
					return musicList.get(musicList.size() - 1);
				} else {
					--i;
					return musicList.get(i);
				}
			}
		}
		return -1;
	}

	public static int getRandomMusic(ArrayList<Integer> musicList, int id) {
		int musicid = -1;
		if (id == -1) {
			return -1;
		}
		if (musicList.isEmpty()) {
			return -1;
		}
		if (musicList.size() == 1) {
			return id;
		}
		do {
			int count = (int) (Math.random() * musicList.size());
			musicid = musicList.get(count);
		} while (musicid == id);
		return musicid;
	}

	// 获取歌曲信息
	public static ArrayList<String> getMusicInfo(int id) {
		SQLiteDatabase musicData = null;
		Cursor cur = null;
		ArrayList<String> musicInfo = new ArrayList<String>();
		try {
			musicData = getMusicData();
			String sql = "select filename, music,singer ";
			sql += "from musicdata where musicid=" + id + ";";
			cur = musicData.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				for (int i = 0; i < cur.getColumnCount(); i++) {
					musicInfo.add(cur.getString(i));
					// Log.v("musicinfo",cur.getString(i));
				}
			} else {
				

			}
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			if (cur != null) {
				cur.close();
			}
			musicData.close();
		}
		return musicInfo;
	}
	public static String getPlayListName(int listid) {
		SQLiteDatabase musicData = null;
		Cursor cur = null;
		String listname="";
		try {
			musicData = getMusicData();
			String sql = "select listname from playlist where listid="+listid+";";
			cur = musicData.rawQuery(sql, null);
			if (cur.moveToFirst()) {
				listname= cur.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cur != null) {
				cur.close();
			}
			musicData.close();
		}
		return listname;
	}
}
