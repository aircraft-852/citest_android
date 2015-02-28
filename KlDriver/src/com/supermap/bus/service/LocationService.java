package com.supermap.bus.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.supermap.bus.biz.BaseApp;
import com.supermap.bus.biz.LocalData;
import com.supermap.bus.common.LogMgr;
import com.supermap.bus.http.SmHttpPost;
import com.supermap.bus.location.BDLocal;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class LocationService extends Service {

	private boolean RUNNING=true;
	public LocationService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		BDLocal.startLocating(getApplicationContext(), callbackhandler, BaseApp.location.App_Loc_Timeout, BaseApp.location.App_Loc_Distance);
		
		RUNNING=true;
		Thread thUpload=new Thread(UploadCoords);
		thUpload.start();
		//return super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		BDLocal.stopLocating();
		RUNNING=false;
		super.onDestroy();
	}

	/**
	 * 监听位置改为时坐标
	 * 
	 * @param x
	 * @param y
	 */
	public Handler callbackhandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle data=msg.getData();
			if(data!=null){
				BaseApp.location.App_Lon=data.getDouble("lng");
				BaseApp.location.App_Lat=data.getDouble("lat");
				BaseApp.location.App_Alt=data.getDouble("alt");
				BaseApp.location.App_Radius=data.getFloat("radius");
				BaseApp.location.App_Direction=data.getFloat("direct");
				BaseApp.location.App_Speed=data.getFloat("speed");	
				BaseApp.location.App_LocTime=data.getString("time");
				
				BaseApp.location.App_Province=data.getString("province");
				BaseApp.location.App_City=data.getString("city");
				BaseApp.location.App_Citycode=data.getString("citycode");
				BaseApp.location.App_District=data.getString("district");
				BaseApp.location.App_Street=data.getString("street");
				BaseApp.location.App_Streetnum=data.getString("streetnum");
				BaseApp.location.App_Poi=data.getString("poi");
				
				if(BaseApp.location.App_Radius<20){
					BaseApp.location.App_isLocated=1;
				}else if(BaseApp.location.App_Radius>=20){
					BaseApp.location.App_isLocated=0;
				}else
					BaseApp.location.App_isLocated=-1;
				
			}
		}
		
	};

	
	//坐标上报线程
	public Runnable UploadCoords = new Runnable() {
		
		String postParam="{}";
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			while(RUNNING){
				try {
					Thread.sleep(BaseApp.location.UPLOAD_Timeout);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//上传坐标
				if(BaseApp.location.App_isLocated<0 || null==BaseApp.IMEI || BaseApp.IMEI.trim().length()<1)
					continue;
				if(LocalData.getCurLineId().trim().length()<1)
					continue;
				
				postParam="{T:\"201\",lineId:\""+LocalData.getCurLineId().trim()+"\",busPlate:\"\",deviceId:\""+""
						+"\",locTime:\""+BaseApp.location.App_LocTime
						+"\",locType:\""+BaseApp.location.locationType
						+"\",filterType:\""+"0"
						+"\",locX:\""+BaseApp.location.App_Lon+"\",locY:\""+BaseApp.location.App_Lat
						+"\",locAlt:\""+BaseApp.location.App_Alt
						+"\",locRatio:\""+BaseApp.location.App_Radius
						+"\",locDirection:\""+BaseApp.location.App_Direction
						+"\",locSpeed:\""+BaseApp.location.App_Speed
						+"\",attitudeId:\""+"0"+"\"}";
				List<NameValuePair> params=new ArrayList<NameValuePair>();
				BasicNameValuePair param=new BasicNameValuePair("CONTENT", postParam);
				params.add(param);
				String sRes=SmHttpPost.postForm(BaseApp.getActionPath(), params);
				if(sRes!=null && sRes.trim().length()>0){
					
				}
				LogMgr.writeLog(BaseApp.location.App_Lon+"--"+BaseApp.location.App_Lat);
			
				
			}
		}
	};
}
