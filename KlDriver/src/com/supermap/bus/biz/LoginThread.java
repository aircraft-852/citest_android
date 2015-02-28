package com.supermap.bus.biz;

import android.os.Handler;
import android.os.Message;

public class LoginThread implements Runnable {

	private Handler myHandler=null;
	private String user="";
	private String password="";
	
	public LoginThread(Handler handler,String userName,String userPassword) {
		// TODO Auto-generated constructor stub
		myHandler=handler;
		user=userName;
		password=userPassword;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Message msg=myHandler.obtainMessage(0);
		//验证账户密码
		
		if(null==user || user.trim().length()!=11){
			msg.arg1=1;		//非手机号
			msg.obj="不是合法手机号";
		}else{
			msg.arg1=0;		//验证成功
			
			msg.obj="登录成功";
			
			LocalData.setLocalUser(user, password);
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		msg.sendToTarget();
	}

}
