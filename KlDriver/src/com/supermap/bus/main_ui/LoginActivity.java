package com.supermap.bus.main_ui;

import com.supermap.bus.R;
import com.supermap.bus.WelcomeActivity;
import com.supermap.bus.biz.LocalData;
import com.supermap.bus.biz.LoginThread;
import com.supermap.bus.common.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity {

	private Handler loginHandler=new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			Intent it=null;
			switch (msg.arg1) {
			case 0:		//登录成功
				it=new Intent(LoginActivity.this,MyBusActivity.class);
				LoginActivity.this.startActivity(it);
				break;
			case 1:		//登录失败，重新登陆或注册
				Toast.makeText(LoginActivity.this, "账号不存在", Toast.LENGTH_LONG).show();
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
		setContentView(R.layout.activity_login);
		
		
		//登录按钮事件
		Button btnLogin = (Button)findViewById(R.id.btn_login_button);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView tvUser,tvPwd;
				tvUser=(TextView)findViewById(R.id.txt_login_name);
				tvPwd=(TextView)findViewById(R.id.txt_login_pswd);
				String sUser=(String) tvUser.getText().toString();
				String sPwd=(String) tvPwd.getText().toString();
				
				//司机版，暂时没有验证密码
				if(sUser !=null && sUser.trim().length()>0){
					
					Thread th=new Thread((Runnable) new LoginThread(loginHandler,sUser,""));
					th.start();
					
					return;
				}
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		TextView tvUser=(TextView)findViewById(R.id.txt_login_name);
		if(LocalData.getLocalUserName()!=null)
			tvUser.setText(LocalData.getLocalUserName().trim());
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
	
	
}
