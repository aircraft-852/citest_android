package com.supermap.bus.main_ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.zxing.client.android.CaptureActivity;
import com.supermap.bus.biz.BaseApp;
import com.supermap.bus.biz.LocalData;
import com.supermap.bus.common.BaseActivity;
import com.supermap.bus.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MyBusActivity_CR extends BaseActivity{

	private static final int WHAT_SHOWCOORD=852001;			//显示坐标 
	private static final int WHAT_LOADMYLINES=852002;		//加载我的线路
	//private static final int WHAT_SHOWCOORD=852001;			//显示坐标 
	
	private Date dtOut=new Date();	
	private static List<Map<String,String>> myLineList=null;
	
	Handler handler=new Handler(new Handler.Callback() {
		
		@Override
		public boolean handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
				case WHAT_SHOWCOORD:		//坐标更新通知
					TextView tv=(TextView)findViewById(R.id.txtLocInfo);
					switch(BaseApp.location.App_isLocated){
						case -1:
							tv.setText("定位功能未启用或无法定位");
							break;
						case 0:
							tv.setText("网络定位："+BaseApp.location.App_Street);
							break;
						case 1:
							tv.setText("GPS定位"+BaseApp.location.App_Street+","+BaseApp.location.App_Poi+"附近");
							break;
						default:
							break;
					}
					break;
				case WHAT_LOADMYLINES:
					ListView lvMyLines=(ListView)findViewById(R.id.lv_mylines);
					if(null==myLineList || myLineList.size()<1)
						break;
					((TextView)findViewById(R.id.txtNoLines)).setVisibility(View.GONE);
					SimpleAdapter simpAdapter=new SimpleAdapter(MyBusActivity_CR.this, 
							myLineList, R.layout.listview_mylines, 
							new String[]{"IS_CUR","LINE_ID","LINE_NAME"}, 
							new int[]{R.id.img_curline,R.id.lv_mylines,R.id.lv_txt_linename});
					lvMyLines.setAdapter(simpAdapter);
					
					lvMyLines.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							// TODO Auto-generated method stub
							Map<String,String> selectLine=(Map<String, String>) parent.getAdapter().getItem(position);
							
							if(selectLine.get("LINE_ID")!=null && selectLine.get("LINE_ID").trim().length()>0){
								//获取详情，并填充到activity中
								String sDetail=LocalData.getLineDetail(selectLine.get("LINE_ID").trim());
								if(sDetail!=null && sDetail.trim().length()>0){
									JSONObject jsonObj;
									try {
										jsonObj = new JSONObject(sDetail);						
										//填充线路名称
										((TextView)findViewById(R.id.txtLineName)).setText(jsonObj.getString("LINE_NAME"));
										
										//填充首末站点
										((TextView)findViewById(R.id.txtLineFirstLastStation)).setText(jsonObj.getString("FIRST_STATION")+"--"+jsonObj.getString("LAST_STATION"));
										
										//填充当前站
										((TextView)findViewById(R.id.txtCurStation)).setText(jsonObj.getString("FIRST_STATION"));
										
										//填充人数信息
										LocalData.updateSeatNumbers();
										((TextView)findViewById(R.id.txtBusSeatNumber)).setText(String.valueOf(LocalData.iBusSeatNumber));
										((TextView)findViewById(R.id.txtOrderSeatNumber)).setText(String.valueOf(LocalData.iOrderSeatNumber));
										((TextView)findViewById(R.id.txtUsedSeatNumber)).setText(String.valueOf(LocalData.iUsedSeatNumber));
										((TextView)findViewById(R.id.txtValidSeatNumber)).setText(String.valueOf(LocalData.iValidSeatNumber));
										
										//填充司机车辆信息
										JSONObject jsonBusDriver=jsonObj.getJSONArray("BUS_DRIVERS").getJSONObject(0);
										if(jsonBusDriver!=null){
											((TextView)findViewById(R.id.txtDriverName)).setText(jsonBusDriver.getString("DRIVER_NAME"));
											((TextView)findViewById(R.id.txtDriverPhone)).setText(jsonBusDriver.getString("DRIVER_PHONE"));
											((TextView)findViewById(R.id.txtBusPlate)).setText(jsonBusDriver.getString("PLATE_NUMBER"));
										}
										
										
										LocalData.setCurLineId(selectLine.get("LINE_ID").trim());
										
										((TextView)findViewById(R.id.txtNoLines)).setVisibility(View.GONE);
									} catch (JSONException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									
								}
							}
						}
					});
					break;
				case 852008:
					Toast.makeText(MyBusActivity_CR.this, "功能建设中...", Toast.LENGTH_SHORT).show();
					RadioButton rbMyBus=((RadioButton)findViewById(R.id.rdbtn_mybus));
					rbMyBus.setChecked(true);
					break;
				case 852009:
					Toast.makeText(MyBusActivity_CR.this, "功能建设中...", Toast.LENGTH_SHORT).show();
					RadioButton rbStart=((RadioButton)findViewById(R.id.rdbtn_Start));
					rbStart.setChecked(true);
					break;
				default:
					break;
			}
			return false;
		}
	});
	
	
	private Runnable locationMonitor=new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				Message msg=handler.obtainMessage(WHAT_SHOWCOORD);
				msg.sendToTarget();
				
				
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	};
	
	private class EventProcess implements Runnable{
		private String TAG="";
		public EventProcess(String tag){
			TAG=tag;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg=null;
			switch(TAG){
			case "MAIN_RADIO_BTN":
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				msg=handler.obtainMessage(852008);		//按钮通知
				break;
			case "START_STOP_BTN":
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				msg=handler.obtainMessage(852009);		//启停通知
				break;
			default:
				break;
			}
			
			msg.sendToTarget();
		}
		
		
	}
	
	
	PowerManager powerManager = null;
    WakeLock wakeLock = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		setContentView(R.layout.activity_my_bus);
		
		//保持常亮
		this.powerManager = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
		this.wakeLock = this.powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
		
		//启动显示位置信息线程
		Thread th=new Thread(locationMonitor);
		th.start();
		
		
		//我的线路列表弹出事件
		((TextView)findViewById(R.id.txtMyBusHead)).setOnClickListener(this);
		
		//底部主菜单切换事件
		((RadioGroup)findViewById(R.id.mainmenu_bottom)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId!=R.id.rdbtn_mybus){
					Thread th=new Thread(new EventProcess("MAIN_RADIO_BTN"));
					th.start();
				}
			}
		});
		
		//线路启动和停止事件
		((RadioGroup)findViewById(R.id.rgStartStop)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			@SuppressLint("ResourceAsColor")
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				RadioButton rdbtn_Start=(RadioButton)findViewById(R.id.rdbtn_Start);
				RadioButton rdbtn_Stop=(RadioButton)findViewById(R.id.rdbtn_Stop);
				if(checkedId==R.id.rdbtn_Start){
					int iStartResult=LocalData.startCurLine(0);
					if(1==iStartResult){
						rdbtn_Start.setTextColor(R.color.kl_white);
						rdbtn_Stop.setTextColor(R.color.kl_black);
					}else{
						Toast.makeText(MyBusActivity_CR.this, "服务端请求失败，请稍后重试", Toast.LENGTH_LONG).show();
					}
				}else{
					int iStartResult=LocalData.startCurLine(1);
					if(1==iStartResult){
						rdbtn_Start.setTextColor(R.color.kl_black);
						rdbtn_Stop.setTextColor(R.color.kl_white);
					}else{
						Toast.makeText(MyBusActivity_CR.this, "服务端请求失败，请稍后重试", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		
		
		//报站按钮滑动事件
		((TextView)findViewById(R.id.txtCurStation)).setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch(event.getAction() & MotionEvent.ACTION_MASK){
				case MotionEvent.ACTION_UP:
					Toast.makeText(MyBusActivity_CR.this, "滑动事件", Toast.LENGTH_SHORT).show();
					break;
				}
				
				return false;
			}
		});
		
		
		//二维码扫描界面
		ImageView imgCRCode=(ImageView)findViewById(R.id.imgCRCode);
		imgCRCode.setOnClickListener(this);
		
		
		//加载线路列表以及当前线路
		new Thread(new LoadMyLines(handler)).start();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_bus, menu);
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
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		RelativeLayout rl=(RelativeLayout)findViewById(R.id.myline_list);
		if(rl.getVisibility()==View.VISIBLE){
			showOrHideMyLines();
			return;
		}
		
		
		long timeout=(new Date()).getTime()-dtOut.getTime();
		if(timeout<1500){
			BaseApp.exit(MyBusActivity_CR.this);
		}else{
			dtOut=new Date();
			Toast.makeText(MyBusActivity_CR.this, "请再按一次退出系统", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(this.wakeLock!=null)
			this.wakeLock.acquire();
		
		//Toast.makeText(MyBusActivity.this, BaseApp.IMEI, Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
		if(this.wakeLock!=null)
			this.wakeLock.release();
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//super.onClick(v);
		
		switch(v.getId()){
		case R.id.txtMyBusHead:
			//Toast.makeText(MyBusActivity.this, "点击‘我的班车’设置线路班车", Toast.LENGTH_LONG).show();
			
			showOrHideMyLines();
			break;
		case R.id.imgCRCode:		//点击二维码，打开二维码刷卡功能
			Intent it=new Intent(MyBusActivity_CR.this,CaptureActivity.class);
			MyBusActivity_CR.this.startActivity(it);
			break;
		default:
			break;
		}

	}
	
	
	//我的线路显示切换
	private void showOrHideMyLines(){
		RelativeLayout rl=(RelativeLayout)findViewById(R.id.myline_list);
		if(rl.getVisibility()==View.GONE){
			//显示动画
			TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,   
			                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,   
			                    -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);   
			mShowAction.setDuration(200); 
			
			rl.startAnimation(mShowAction);
			rl.setVisibility(View.VISIBLE);

		}else{
		//隐藏动画
			TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,   
			                    0.0f, Animation.RELATIVE_TO_SELF, 0.0f,   
			                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,   
			                    -1.0f);  
			mHiddenAction.setDuration(200);   				
			
			rl.startAnimation(mHiddenAction);
			rl.setVisibility(View.GONE);
		}
	}

	
	//加载线路线程
	private class LoadMyLines implements Runnable{
		private Handler cbkHandler=null;
		public LoadMyLines(Handler handler){
			cbkHandler=handler;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message msg=cbkHandler.obtainMessage(WHAT_LOADMYLINES);
			if(LocalData.getMyLines()!=null){
				myLineList=new ArrayList<Map<String,String>>();
				Map<String,String> myLines=LocalData.getMyLines();
				Iterator<String> itLines=myLines.keySet().iterator();
				Map<String,String> singleLine=null;
				JSONObject jsonLine=null;
				while(itLines.hasNext()){
					try {
						jsonLine=new JSONObject(myLines.get(itLines.next()));
						singleLine=new HashMap<String,String>();
						
						if(jsonLine.getString("LINE_ID").trim().equalsIgnoreCase(LocalData.getCurLineId())){
							//获取当前线路
							singleLine.put("IS_CUR", "1");
						}else{
							singleLine.put("IS_CUR", "0");
						}
						singleLine.put("LINE_ID", jsonLine.getString("LINE_ID"));
						singleLine.put("LINE_NAME", jsonLine.getString("LINE_NAME"));		
						
						myLineList.add(singleLine);
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}//while end
				
				
			}else{		//LocalData.myLines is NULL
				
			}
			
			msg.sendToTarget();
		}
		
	}

}
