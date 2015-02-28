package com.supermap.bus.location;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;

public class LocationUtil {

	/** 
     * 判断GPS是否开启，GPS或者AGPS开启一个就认为是开启的 
     * @param context 
     * @return true 表示开启 
     */ 
    public static final boolean isOpen(final Context context) {  
        LocationManager locationManager   
                                 = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);  
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）  
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);  
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）  
        boolean network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);  
        if (gps || network) {  
            return true;  
        }  
   
        return false;  
    }

    /** 
     * 强制帮用户打开GPS 
     * @param context 
     */ 
    public static final void openGPS(Context context) {  
        Intent GPSIntent = new Intent();  
        GPSIntent.setClassName("com.android.settings",  
                "com.android.settings.widget.SettingsAppWidgetProvider");  
        GPSIntent.addCategory("android.intent.category.ALTERNATIVE");  
        GPSIntent.setData(Uri.parse("custom:3"));  
        try {  
            PendingIntent.getBroadcast(context, 0, GPSIntent, 0).send();  
        } catch (CanceledException e) {  
            e.printStackTrace();  
        }  
    }

    /*
     * 打开GPS设置按钮
     */
    public static final void openGPSSetting(Context context){
		if(android.os.Build.VERSION.SDK_INT > 10 ){
			context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}else {
			context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
		}
    	
    	/*
    	 * com.android.settings.AccessibilitySettings 辅助功能设置
 　　com.android.settings.ActivityPicker 选择活动
 　　com.android.settings.ApnSettings APN设置
 　　com.android.settings.ApplicationSettings 应用程序设置
 　　com.android.settings.BandMode 设置GSM/UMTS波段
 　　com.android.settings.BatteryInfo 电池信息
 　　com.android.settings.DateTimeSettings 日期和坝上旅游网时间设置
 　　com.android.settings.DateTimeSettingsSetupWizard 日期和时间设置
 　　com.android.settings.DevelopmentSettings 应用程序设置=》开发设置
 　　com.android.settings.DeviceAdminSettings 设备管理器
 　　com.android.settings.DeviceInfoSettings 关于手机
 　　com.android.settings.Display 显示——设置显示字体大小及预览
 　　com.android.settings.DisplaySettings 显示设置
 　　com.android.settings.DockSettings 底座设置
 　　com.android.settings.IccLockSettings SIM卡锁定设置
 　　com.android.settings.InstalledAppDetails 语言和键盘设置
 　　com.android.settings.LanguageSettings 语言和键盘设置
 　　com.android.settings.LocalePicker 选择手机语言
 　　com.android.settings.LocalePickerInSetupWizard 选择手机语言
 　　com.android.settings.ManageApplications 已下载（安装）软件列表
 　　com.android.settings.MasterClear 恢复出厂设置
 　　com.android.settings.MediaFormat 格式化手机闪存
 　　com.android.settings.PhysicalKeyboardSettings 设置键盘
 　　com.android.settings.PrivacySettings 隐私设置
 　　com.android.settings.ProxySelector 代理设置
 　　com.android.settings.RadioInfo 手机信息
 　　com.android.settings.RunningServices 正在运行的程序（服务）
 　　com.android.settings.SecuritySettings 位置和安全设置
 　　com.android.settings.Settings 系统设置
 　　com.android.settings.SettingsSafetyLegalActivity 安全信息
 　　com.android.settings.SoundSettings 声音设置
 　　com.android.settings.TestingSettings 测试——显示手机信息、电池信息、使用情况统计、Wifi information、服务信息
 　　com.android.settings.TetherSettings 绑定与便携式热点
 　　com.android.settings.TextToSpeechSettings 文字转语音设置
 　　com.android.settings.UsageStats 使用情况统计
 　　com.android.settings.UserDictionarySettings 用户词典
 　　com.android.settings.VoiceInputOutputSettings 语音输入与输出设置
 　　com.android.settings.WirelessSettings 无线和网络设置
    	 */
    }

}
