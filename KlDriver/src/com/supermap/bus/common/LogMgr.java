package com.supermap.bus.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.TimeZone;

import android.text.format.Time;
import android.util.Log;

public class LogMgr {
	private static boolean flag=true;
	private static String  TAG = "LogMgr";
	public static int writeLog(String content){
		try {
			Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+08:00"));
			String month=null;
			String day = null;
			if((c.get(Calendar.MONTH) + 1)<10){
				month = "0"+(c.get(Calendar.MONTH) + 1);
			}else{
				month = ""+(c.get(Calendar.MONTH) + 1);
			}
			if(c.get(Calendar.DAY_OF_MONTH)<10){
				day = "0"+c.get(Calendar.DAY_OF_MONTH);
			}else{
				day = c.get(Calendar.DAY_OF_MONTH)+"";
			}
			String date = c.get(Calendar.YEAR) +"-"+ month+"-" + day+"";
			String hour = null;
			String minute = null;
			String second = null;
			if(c.get(Calendar.HOUR_OF_DAY)<10){
				hour = "0"+c.get(Calendar.HOUR_OF_DAY);
			}else{
				hour = c.get(Calendar.HOUR_OF_DAY)+"";
			}
			if(c.get(Calendar.MINUTE)<10){
				minute = "0"+c.get(Calendar.MINUTE);
			}else{
				minute = c.get(Calendar.MINUTE)+"";
			}
			if(c.get(Calendar.SECOND)<10){
				second = "0"+c.get(Calendar.SECOND);
			}else{
				second = c.get(Calendar.SECOND)+"";
			}
			String time = hour+":" + minute +":"+ second;
			String logPath = ConfigHelper.DATA_SUPPORT_ROOT 
					+ File.separator + "log";
					//+ File.separator + date;
			File dir = new File(logPath);
			// 如果目录中不存在，创建这个目录
			if (!dir.exists())
				dir.mkdirs();
			String fileName = logPath + File.separator + date + ".txt";
			FileOutputStream fout = null;
			OutputStreamWriter osw = null;
			File file = new File(fileName);
			try {
				fout = new FileOutputStream(file, true); // true表示追加到已存在的文件
				osw = new OutputStreamWriter(fout);
				osw.write("" + date + " " + time +":");
				osw.write(content);
				osw.write(System.getProperty("line.separator"));
				osw.flush();
				return 0;
			} catch (Exception e) {
				e.printStackTrace();
				return 1;
			} finally {
				try {
					osw.close();
					fout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing report file...", e);
			return 1;
		}
	}
	
	public static String getLogPath(){
		Time t = new Time("GMT+8");
		int date = t.year * 10000 + (t.month + 1) * 100 + t.monthDay;
		int time = t.hour * 10000 + t.minute * 100 + t.second;
		String logPath = ConfigHelper.DATA_SUPPORT_ROOT 
				+ File.separator + "log";
		return logPath;
	}

	public static String getLog(String logPath){
		return null;
	}
	public static void i(String message){
		if(flag){
			Log.i("info", message);
		}
	}
}
