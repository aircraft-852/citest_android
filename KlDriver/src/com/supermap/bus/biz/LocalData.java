package com.supermap.bus.biz;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.supermap.bus.R;
import com.supermap.bus.common.Cookie;
import com.supermap.bus.common.LogMgr;
import com.supermap.bus.http.SmHttpPost;


//本地数据管理类
public class LocalData {

	
	//----------------------用户信息
	private static final String USER_NAME="localUserName";
	private static final String USER_PASSWORD="localUserPswd";
	private static final String USER_HISTORY_LIST="localUserHistory";
	
	private static Cookie cookie=new Cookie(BaseApp.baseContext, BaseApp.baseContext.getString(R.string.app_name));
	
	private static Map<String,String> localUserHistory=new HashMap<String,String>();
	//获取本地账户信息，如果未登录过，则返回“”
	public static String getLocalUserName(){
		return cookie.getVal(USER_NAME, "");
	}
	
	public static String getLocalUserPswd(){
		return cookie.getVal(USER_PASSWORD, "");
	}
	
	public static void setLocalUser(String userName,String userPswd){
		if(userName!=null && userName.trim().length()>0){
			cookie.putVal(USER_NAME, userName);
			
			if(userPswd!=null && userPswd.trim().length()>0)
				cookie.putVal(USER_PASSWORD, userPswd);
			else
				cookie.putVal(USER_PASSWORD, "");
		}		
	}
	
	public static Map<String,String> getLocalUserList(){
		//用户列表字符串格式
		//{USERLIST:[{USERNAME:"zhangsan",USERPSWD:"1231",LOGINTIME:"2015-1-24 12:34:23"},{USERNAME:"lisi",USERPSWD:"4324",LOGINTIME:"2015-1-24 12:34:23"}]}
		String strUserList = cookie.getVal("USER_HISTORY_LIST","");
		if(strUserList.trim().length()<1)
			return null;
		
		Map<String,String> list=new HashMap<String,String>();
		JSONObject json=null;
		Map<String,String> userList=new HashMap<String,String>();
		try{
			json=new JSONObject(strUserList);
			
			JSONArray jsonArr=json.getJSONArray("USERLIST");
			
			for(int i=0;i<jsonArr.length();i++){
				userList.put(jsonArr.getJSONObject(i).getString("USERNAME"), jsonArr.getJSONObject(i).toString());
			}
		}catch(Exception e){
			LogMgr.writeLog("本地用户历史记录获取失败，json格式解析有误："+strUserList);
			return null;
		}
		return list;
		
		
		
	}

	//----------------------线路信息
	//本地缓存线路对象，key是线路ID
	private static Map<String,String> myLines=null;
	//当前选择的线路ID
	private static String curLineId="";
	//当前选择的线路启动状态
	private static boolean curLineStatus=false;
	
	//清空本地缓存线路
	private static void resetLines(){
		myLines=null;
		curLineId="";
		curLineStatus=false;
	}
	
	//获取本司机的线路信息
	public static Map<String,String> getMyLines(){
		if(myLines!=null)
			return myLines;
		
		getMyLinesRemote();
		
		return myLines;
	}
	//获取当前线路ID
	public static String getCurLineId(){
		if(myLines!=null && myLines.size()>0){
			if(null==curLineId || curLineId.trim().length()<1){
				Iterator<String> iter=myLines.keySet().iterator();
				curLineId=iter.next();
			}

			return curLineId;
		}else{
			curLineId="";
			return curLineId;
		}
	}
	//获取当前线路启动状态
	public static boolean getCurLineStatus(){
		return curLineStatus;
	}
	
	//选择当前线路
	public static void setCurLineId(String lineId){
		curLineId=lineId;
		curLineStatus=false;
	}
	
	//获取线路详情
	public static String getLineDetail(String lineId){
		Map<String,String> lines=getMyLines();
		if(lines.containsKey(lineId))
			return lines.get(lineId);
		else
			return null;
	}
	
	//从服务端获取本人线路
	private static void getMyLinesRemote(){
		String sPostParam="{T:\"204\",PHONENUMBER:\""+getLocalUserName()
				+"\",DEVICEID:\""+BaseApp.IMEI+"\"}";
		
		NameValuePair nvp=new BasicNameValuePair("CONTENT",sPostParam);
		List<NameValuePair> nvps=new ArrayList<NameValuePair>();
		nvps.add(nvp);
		
		//向服务的请求线路列表
		String sRes=SmHttpPost.postForm(BaseApp.getActionPath(), nvps);
		
		
		//解析服务端返回结果
		try {
			JSONObject jsonResult=new JSONObject(sRes);
			String sR=jsonResult.getString("R");
			if(null==sR || !sR.trim().equals("0")){
				//查询失败
				return;
			}
			
			JSONArray jsonLines=jsonResult.getJSONArray("LINES");
			myLines=new HashMap<String,String>();
			for(int i=0;i<jsonLines.length();i++){
				JSONObject jsonLine=jsonLines.getJSONObject(i);
				String str=jsonLines.getJSONObject(i).toString();
				myLines.put(jsonLines.getJSONObject(i).getString("LINE_ID"),jsonLines.getJSONObject(i).toString());
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
		
	/*
	 * 线路启动或停止，返回1表示成功，返回0表示失败
	 * iStart为0，启动，为1停止
	 */
	public static int startCurLine(int iStart){
		String postParam="{T:\"205\",PHONENUMBER:\""
				+getLocalUserName()+"\",LINE_ID:\""
				+getCurLineId()+"\",BUS_PLATE:\"\",START_STOP:\""+
				iStart+"\",TIME:\""+
				(new Date()).toString()+"\"}";
		
		NameValuePair nvp=new BasicNameValuePair("CONTENT",postParam);
		List<NameValuePair> nvps=new ArrayList<NameValuePair>();
		nvps.add(nvp);
		
		//向服务的请求线路启动或停止
		String sRes=SmHttpPost.postForm(BaseApp.getActionPath(), nvps);
		
		if(sRes!=null && sRes.trim().length()>0){
			try {
				JSONObject jsonRes=new JSONObject(sRes);
				
				String sR=jsonRes.getString("R");
				if(sR!=null && sR.trim().equals("0"))
					return 1;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return 0;
	}
	
	
	//-------------------人数信息
	public static int iBusSeatNumber=0;
	public static int iOrderSeatNumber=0;
	public static int iUsedSeatNumber=0;
	public static int iValidSeatNumber=0;
	
	//获取当前线路的座位信息
	public static void updateSeatNumbers(){
		//seatNumber[0]=
		String lineId=getCurLineId();
		if(lineId!=null && lineId.trim().length()>0){
			//从服务端请求
			String postParam="{T:\"206\",LINE_ID:\""
					+lineId+"\",BUS_PLATE:\"\"}";
			NameValuePair nvp=new BasicNameValuePair("CONTENT",postParam);
			List<NameValuePair> nvps=new ArrayList<NameValuePair>();
			nvps.add(nvp);
			
			//向服务的请求线路启动或停止
			String sRes=SmHttpPost.postForm(BaseApp.getActionPath(), nvps);
			
			JSONObject jsonResult=null;
			try {
				jsonResult=new JSONObject(sRes);
				
				//车座数
				iBusSeatNumber=Integer.getInteger(jsonResult.getString("BUS_SEATS"), 44);
				iOrderSeatNumber=Integer.getInteger(jsonResult.getString("ORDER_SEATS"), 0);
				iUsedSeatNumber=Integer.getInteger(jsonResult.getString("USED_SEATS"), 0);
				
				if(iBusSeatNumber>=iUsedSeatNumber)
					iValidSeatNumber=iBusSeatNumber-iUsedSeatNumber;
				else
					iValidSeatNumber=0;
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
