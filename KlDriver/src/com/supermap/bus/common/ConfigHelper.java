package com.supermap.bus.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.telephony.TelephonyManager;

public class ConfigHelper {

	public ConfigHelper() {
		// TODO Auto-generated constructor stub
	}

	/** 支持文件目录 */
	public static boolean isSendingMessage = false;
	/** SD卡路径 */
	public static final String SDCARD_PATH = getRootDataPath();
	public static final String SYS_DCIM = getSysDCIMPath(); // 系统相册路径
	public static final String DATA_SUPPORT_ROOT = getRootDataPath()+ "/klbus";

	/**
	 * 
	 * <p>
	 * 获取Android系统相册路径
	 * </p>
	 * 
	 * @creation 2013-4-24
	 * 
	 * @author 烜
	 * 
	 * @return
	 */
	private static String getSysDCIMPath() {
		String dcimFolder = "/sdcard/DCIM/Camera";
		String[] mayOption = new String[] { "/sdcard/DCIM/Camera",
				"/sdcard0/DCIM/Camera", "/sdcard1/DCIM/Camera",
				SDCARD_PATH + "/DCIM/Camera" };
		File folder = null;
		for (int i = 0; i < mayOption.length; i++) {
			folder = new File(mayOption[i]);
			if (folder.exists() && folder.isDirectory())
				dcimFolder = mayOption[i];
		}
		return dcimFolder;
	}

	
	/**
	 * 
	 * <p>
	 * 获取城管通主文件夹cgt_data_support
	 * </p>
	 * 
	 * @creation 2013-4-24
	 * 
	 * @author 烜
	 * 
	 * @return
	 */
	private static String getRootDataPath() {
		try {
			Runtime runtime = Runtime.getRuntime();
			java.lang.Process proc = runtime.exec("mount");
			InputStream is = proc.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			String line;
			String mount = new String();
			File f = null;
			BufferedReader br = new BufferedReader(isr);
			while ((line = br.readLine()) != null) {
				if (line.contains("secure"))
					continue;
				if (line.contains("asec"))
					continue;

				if (line.contains("fat")) {
					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1) {
						// mount = mount.concat("*" + columns[1] + "\n");
						for (int i = 1; i < columns.length; i++) {
							if (columns[i].contains("/")) {
								mount = columns[i];
								break;
							}
						}
					}
				} else if (line.contains("fuse")) {
					String columns[] = line.split(" ");
					if (columns != null && columns.length > 1) {
						// mount = mount.concat(columns[1] + "\n");
						for (int i = 1; i < columns.length; i++) {
							if (columns[i].contains("/")) {
								mount = columns[i];
								break;
							}
						}
					}
				}
				f = new File(mount + "/DCIM/Camera");
				boolean flag=f.exists();
				if (flag){
					f=new File(mount+"/klbus");
					if(!f.exists())
						f.mkdirs();
					return mount;
				}

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
