package com.supermap.bus.location;

import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.supermap.bus.common.LogMgr;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class BDLocal {
	private static BDLocal myLocal=null;
	private Context m_context = null;
	public LocationClient mLocationClient = null;
	private Handler m_CallbackHandler=null;
	

	public enum COORD_TYPE{
		gcj02,bd09ll
	};
	
	private BDLocal(Context con,Handler hand){
		m_context=con;
		m_CallbackHandler=hand;
	}
	public static void startLocating(Context context,Handler handler, long minTime, float minDistance){
		if(null==context || null==handler)
			return;
		if(null==myLocal)
		{
			synchronized(BDLocal.class)
			{
				if(null==myLocal)
				{
//					m_locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
					myLocal=new BDLocal(context,handler);
					myLocal.startLocatingByBaiduLocSDK(context,minTime);
				}
			}
		}
		
	}
	public static void stopLocating(){
		if(myLocal!=null){
			if(myLocal.mLocationClient!=null)
			{
				myLocal.mLocationClient.stop();
				myLocal.mLocationClient=null;
			}
			
			myLocal=null;
		}
	}
	
	/**
	 * 开始通过百度定位sdk定位
	 * @param context
	 * @param minTime 定位时间间隔
	 */
	private void startLocatingByBaiduLocSDK(Context context, long minTime){
		BDLocationListener myListener = new MyLocationListener(m_CallbackHandler);
		mLocationClient = new LocationClient(context);     //声明LocationClient类
		mLocationClient.registerLocationListener( myListener); 
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); 
		option.setAddrType("all");
		option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02,bd09ll
		option.setScanSpan((int)minTime);//设置发起定位请求的间隔时间为minTime
		mLocationClient.setLocOption(option);
		if(!mLocationClient.isStarted()){
			mLocationClient.start();
		}else{
			LogMgr.writeLog("百度定位服务已经启动，无需重新启动");
		}
		
	}
	
	
	
	/**
	 * 百度定位监听方法
	 * @author mw
	 *
	 */
	private class MyLocationListener implements BDLocationListener {
		
		private Message msg=null;
		private Bundle data=null;
		private Handler hdcallback=null;
		MyLocationListener(Handler handler){
			hdcallback = handler;
		}
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null){
				return ;
			}
		            
			data=new Bundle();
			data.putDouble("lng", location.getLongitude());
			data.putDouble("lat",location.getLatitude());
			data.putDouble("alt", location.getAltitude());
			data.putFloat("speed", location.getSpeed());
			data.putFloat("direct", location.getDerect());
			data.putString("time",location.getTime());
			data.putFloat("radius", location.getRadius());
			data.putInt("satelliteNumber", location.getSatelliteNumber());
			
			data.putString("province", location.getProvince());
			data.putString("city", location.getCity());
			data.putString("citycode", location.getCityCode());
			data.putString("district", location.getDistrict());
			data.putString("street", location.getStreet());
			data.putString("streetnum", location.getStreetNumber());
			data.putString("poi", location.getPoi());

			if(location.getLongitude() == 4.9E-324 || location.getLatitude() == 4.9E-324){
				LogMgr.writeLog("百度定位返回无效坐标");

			}else{

				msg=hdcallback.obtainMessage();
				msg.setData(data);
				msg.sendToTarget();
			}
		}
		@Override
		public void onReceivePoi(BDLocation arg0) {
		}

	}
	
}
