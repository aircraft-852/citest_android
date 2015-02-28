package com.supermap.bus.biz;

import java.util.Date;
import java.util.HashSet;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

public class BaseApp extends Application {

	public static String IMEI="";
	private static String PHONENUMBER="";
	
	//应用程序上下文
	public static BaseApp baseContext=null;
	
	public BaseApp() {
		// TODO Auto-generated constructor stub

	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		baseContext=this;
		//获取本机相关信息
		getPhoneState();
	}

	//获取本机信息
	private void getPhoneState(){
		TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);     
	       
		  /*   
		   * 电话状态：   
		   * 1.tm.CALL_STATE_IDLE=0          无活动   
		   * 2.tm.CALL_STATE_RINGING=1  响铃   
		   * 3.tm.CALL_STATE_OFFHOOK=2  摘机   
		   */    
		  tm.getCallState();//int     
		       
		  /*   
		   * 电话方位：   
		   *    
		   */    
		  tm.getCellLocation();//CellLocation     
		       
		  /*   
		   * 唯一的设备ID：   
		   * GSM手机的 IMEI 和 CDMA手机的 MEID.    
		   * Return null if device ID is not available.   
		   */    
		  IMEI=tm.getDeviceId();//String     
		       
		  /*   
		   * 设备的软件版本号：   
		   * 例如：the IMEI/SV(software version) for GSM phones.   
		   * Return null if the software version is not available.    
		   */    
		  tm.getDeviceSoftwareVersion();//String     
		       
		  /*   
		   * 手机号：   
		   * GSM手机的 MSISDN.   
		   * Return null if it is unavailable.    
		   */    
		  tm.getLine1Number();//String     
		       
		  /*   
		   * 附近的电话的信息:   
		   * 类型：List<NeighboringCellInfo>    
		   * 需要权限：android.Manifest.permission#ACCESS_COARSE_UPDATES   
		   */    
		  tm.getNeighboringCellInfo();//List<NeighboringCellInfo>     
		       
		  /*   
		   * 获取ISO标准的国家码，即国际长途区号。   
		   * 注意：仅当用户已在网络注册后有效。   
		   *       在CDMA网络中结果也许不可靠。   
		   */    
		  tm.getNetworkCountryIso();//String     
		       
		  /*   
		   * MCC+MNC(mobile country code + mobile network code)   
		   * 注意：仅当用户已在网络注册时有效。   
		   *    在CDMA网络中结果也许不可靠。   
		   */    
		  tm.getNetworkOperator();//String     
		       
		  /*   
		   * 按照字母次序的current registered operator(当前已注册的用户)的名字   
		   * 注意：仅当用户已在网络注册时有效。   
		   *    在CDMA网络中结果也许不可靠。   
		   */    
		  tm.getNetworkOperatorName();//String     
		       
		  /*   
		   * 当前使用的网络类型：   
		   * 例如： NETWORK_TYPE_UNKNOWN  网络类型未知  0   
		     NETWORK_TYPE_GPRS     GPRS网络  1   
		     NETWORK_TYPE_EDGE     EDGE网络  2   
		     NETWORK_TYPE_UMTS     UMTS网络  3   
		     NETWORK_TYPE_HSDPA    HSDPA网络  8    
		     NETWORK_TYPE_HSUPA    HSUPA网络  9   
		     NETWORK_TYPE_HSPA     HSPA网络  10   
		     NETWORK_TYPE_CDMA     CDMA网络,IS95A 或 IS95B.  4   
		     NETWORK_TYPE_EVDO_0   EVDO网络, revision 0.  5   
		     NETWORK_TYPE_EVDO_A   EVDO网络, revision A.  6   
		     NETWORK_TYPE_1xRTT    1xRTT网络  7   
		   */    
		  tm.getNetworkType();//int     
		       
		  /*   
		   * 手机类型：   
		   * 例如： PHONE_TYPE_NONE  无信号   
		     PHONE_TYPE_GSM   GSM信号   
		     PHONE_TYPE_CDMA  CDMA信号   
		   */    
		  tm.getPhoneType();//int     
		       
		  /*   
		   * Returns the ISO country code equivalent for the SIM provider's country code.   
		   * 获取ISO国家码，相当于提供SIM卡的国家码。   
		   *    
		   */    
		  tm.getSimCountryIso();//String     
		       
		  /*   
		   * Returns the MCC+MNC (mobile country code + mobile network code) of the provider of the SIM. 5 or 6 decimal digits.   
		   * 获取SIM卡提供的移动国家码和移动网络码.5或6位的十进制数字.   
		   * SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断).   
		   */    
		  tm.getSimOperator();//String     
		       
		  /*   
		   * 服务商名称：   
		   * 例如：中国移动、联通   
		   * SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断).   
		   */    
		  tm.getSimOperatorName();//String     
		       
		  /*   
		   * SIM卡的序列号：   
		   * 需要权限：READ_PHONE_STATE   
		   */    
		  tm.getSimSerialNumber();//String     
		       
		  /*   
		   * SIM的状态信息：   
		   *  SIM_STATE_UNKNOWN          未知状态 0   
		   SIM_STATE_ABSENT           没插卡 1   
		   SIM_STATE_PIN_REQUIRED     锁定状态，需要用户的PIN码解锁 2   
		   SIM_STATE_PUK_REQUIRED     锁定状态，需要用户的PUK码解锁 3   
		   SIM_STATE_NETWORK_LOCKED   锁定状态，需要网络的PIN码解锁 4   
		   SIM_STATE_READY            就绪状态 5   
		   */    
		  tm.getSimState();//int     
		       
		  /*   
		   * 唯一的用户ID：   
		   * 例如：IMSI(国际移动用户识别码) for a GSM phone.   
		   * 需要权限：READ_PHONE_STATE   
		   */    
		  tm.getSubscriberId();//String     
		       
		  /*   
		   * 取得和语音邮件相关的标签，即为识别符   
		   * 需要权限：READ_PHONE_STATE   
		   */    
		  tm.getVoiceMailAlphaTag();//String     
		       
		  /*   
		   * 获取语音邮件号码：   
		   * 需要权限：READ_PHONE_STATE   
		   */    
		  tm.getVoiceMailNumber();//String     
		       
		  /*   
		   * ICC卡是否存在   
		   */    
		  tm.hasIccCard();//boolean     
		       
		  /*   
		   * 是否漫游:   
		   * (在GSM用途下)   
		   */    
		  tm.isNetworkRoaming();//     		
		  }

	private static final String SERVER_IP="123.56.87.126";//"123.56.87.126";
	private static final int SERVER_PORT=8080;
	private static final String SERVER_ROOT="/klbusserver";
	private static final String ACTION_PATH="/actionmobile";
	private static final String COORDS_PATH="/actionGPSMessage";
	
	public static String getServerRoot(){
		return "http://"+SERVER_IP+":"+SERVER_PORT+SERVER_ROOT;
	}
	public static String getCoordUploadPath(){
		return "http://"+SERVER_IP+":"+SERVER_PORT+SERVER_ROOT+COORDS_PATH;
	}
	public static String getActionPath(){
		return "http://"+SERVER_IP+":"+SERVER_PORT+SERVER_ROOT+ACTION_PATH;
	}
	

	//页面列表
	private static HashSet<Activity> mActivityList = new HashSet<Activity>();
	//服务列表
	private static HashSet<Intent> mServiceList=new HashSet<Intent>();

	public static void addActivity(Activity activity) {
		mActivityList.add(activity);
	}
	public static void addService(Intent it){
		mServiceList.add(it);
	}
	
	public static void exit(Activity curActivity) {
		//关闭service
		for(Intent it:mServiceList){
			if(it!=null){
				curActivity.stopService(it);
			}
		}
		//关闭activity
		for (Activity activity : mActivityList) {
			if (null != activity) {
				activity.finish();
			}
		}
		System.exit(0);
	}
	
	//手机定位信息
	public static class location{
		public static int locationType=1;	//0:GPS定位wgs84，1：百度定位bd09ll
		
		public static int App_isLocated=-1;	//-1未定位，0非精确定位，1精确定位
		public static double App_Lon;
		public static double App_Lat;
		public static double App_Alt;
		public static double App_X;
		public static double App_Y;
		public static double App_Z;
		public static double App_Radius;
		public static float App_Speed;
		public static float App_Direction;
		public static String App_LocTime;
		
		//地址信息，百度定位才有
		public static String App_Province;
		public static String App_City;
		public static String App_Citycode;
		public static String App_District;
		public static String App_Street;
		public static String App_Streetnum;
		public static String App_Poi;
		
		public static long App_Loc_Timeout=1000;			//默认定位间隔1s
		public static float App_Loc_Distance=10;				//默认定位间距50m
		public static long UPLOAD_Timeout=5000;				//默认上传坐标间隔
	}
	
}
