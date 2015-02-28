package com.supermap.bus;

import java.io.File;

import com.supermap.bus.biz.BaseApp;
import com.supermap.bus.biz.LocalData;
import com.supermap.bus.biz.LoginThread;
import com.supermap.bus.common.BaseActivity;
import com.supermap.bus.location.LocationUtil;
import com.supermap.bus.main_ui.LoginActivity;
import com.supermap.bus.main_ui.MyBusActivity;
import com.supermap.bus.service.LocationService;
import com.supermap.bus.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

public class WelcomeActivity extends BaseActivity {

	private Handler handler=new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Intent it=null;
			switch (msg.arg1) {
			case 0:		//登录成功
				it=new Intent(WelcomeActivity.this,MyBusActivity.class);
				WelcomeActivity.this.startActivity(it);
				break;
			case 1:		//登录失败，重新登陆或注册
				it=new Intent(WelcomeActivity.this,LoginActivity.class);
				WelcomeActivity.this.startActivity(it);
				break;
			default:
				break;
			}
			
			return false;
		}
	});
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.activity_welcome);
		
		
		if(!LocationUtil.isOpen(this)){
			//Toast.makeText(this, "请打开位置服务，允许本软件使用定位数据", Toast.LENGTH_LONG).show();
			LocationUtil.openGPSSetting(this);
			}
		
		int tmout=0;
		while(!LocationUtil.isOpen(this)){
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//10s之内如果不打开定位服务，则系统自动关闭
			if((++tmout)>10){
				BaseApp.exit(this);
			}
		}
		
		String user=LocalData.getLocalUserName();
		String pswd=LocalData.getLocalUserPswd();
		Thread th=new Thread((Runnable) new LoginThread(handler,"",""));
		th.start();	
		
		Intent it=new Intent(getApplicationContext(), LocationService.class);
		BaseApp.addService(it);
		startService(it);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.welcome, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
			
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
