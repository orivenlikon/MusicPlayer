package com.musicplayer.thread;


import com.musicplayer.receiver.ReceiverMedia;
import com.musicplayer.util.Constant;

import android.content.Context;
import android.content.Intent;

public class MusicPlayerThread extends Thread
{
	ReceiverMedia recMedia;
	Context context;
	int threadNumber;
	
	public MusicPlayerThread(ReceiverMedia recMedia , Context context , int threadNumber)
	{
		this.recMedia=recMedia;
		this.context=context;
		this.threadNumber=threadNumber;
	}
	
	public void run() 
	{
		looper: while (recMedia.threadNumber==threadNumber) 
		{
			if (recMedia.status == Constant.STATUS_STOP||recMedia.mp==null)
			{
				break looper;
			}
			int duration = 0;
			int current = 0;
			try 
			{
				if (recMedia.mp != null && recMedia.threadNumber==threadNumber
						&& (recMedia.status == Constant.STATUS_PLAY || recMedia.status == Constant.STATUS_PAUSE)) {
					duration = recMedia.mp.getDuration();
					current = recMedia.mp.getCurrentPosition();
				}

				Intent intent = new Intent(Constant.UPDATE_STATUS);
				intent.putExtra("status",Constant.COMMAND_GO);
				intent.putExtra("status2",recMedia.status);
				intent.putExtra("duration", duration);
				intent.putExtra("current", current);
				context.sendBroadcast(intent);
				
				
			}
			catch (Exception e) 
			{
				e.printStackTrace();
			}

			try 
			{
				Thread.sleep(100);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
