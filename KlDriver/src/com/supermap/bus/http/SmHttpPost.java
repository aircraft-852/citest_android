package com.supermap.bus.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.supermap.bus.common.LogMgr;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * HTTP请求封装
 * @author zhangxuan
 *
 */
public class SmHttpPost {
	
	private static String TAG = "SmHttpPost";
	
	/**
	 * 上报表单，如果是文件，键名为"file:"开头
	 * houUnitfig=file:{2=/sdcard/DCIM/Camera/1365305976395.jpg, 
	 * 1=/sdcard/DCIM/Camera/1365305926563.jpg, 
	 * 0=/sdcard/DCIM/Camera/1365301770018.jpg}
	 * 同时上报键名为Name-1,Name-2,Name-3...Name-n
	 * @param url
	 * @param nameValuePairs
	 * @return 响应结果
	 */
	public static String postForm(String url, List<NameValuePair> nameValuePairs) {
		LogMgr.i("发送http请求001");
		String resultStr="-1";
	    HttpClient httpClient = new DefaultHttpClient();
	    LogMgr.i("发送http请求002");
	    //请求超时 连接时间
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10*1000);
		LogMgr.i("发送http请求003");
		//读取超时 数据传输时间
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5*60*1000);
		LogMgr.i("发送http请求004");
	    HttpContext localContext = new BasicHttpContext(); 
	    LogMgr.i("发送http请求005");
	    HttpPost httpPost = new HttpPost(url);  
	    LogMgr.i("发送http请求006");
	    httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
	    try {  
	        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
	        StringBody stringBody; 
            FormBodyPart fbp; 
            FileBody fb;
            boolean upFileFlag = false;// 是否有文件上传 
            LogMgr.i("发送http请求007");
	        for(int index=0; index < nameValuePairs.size(); index++) {  
	        	if(nameValuePairs.get(index).getValue() != null){
	        		if(nameValuePairs.get(index).getValue().startsWith("file:")){
						String[] resArray = nameValuePairs
								.get(index).getValue().substring(5).split(",");
						String filePath;
						for(int i = 0;i<resArray.length;i++){
							// 2=/sdcard/DCIM/Camera/1365305976395.jpg
							filePath = resArray[i].substring(resArray[i].indexOf("/")+1);
							// /sdcard/DCIM/Camera/1365305976395.jpg
							filePath = Environment
									.getExternalStorageDirectory().getPath()+File.separator
									+ filePath.substring(filePath.indexOf("/")+1);
							// /DCIM/Camera/1365305976395.jpg
							fb=new FileBody(new File(filePath), "");//image/jpg
							upFileFlag = true;
							entity.addPart(nameValuePairs.get(index).getName()+"-"+i,fb);
						}
		        	}else{
		        		stringBody = new StringBody(nameValuePairs.get(index).getValue(),
		        				Charset.forName("UTF-8")); 
		        		fbp = new FormBodyPart(nameValuePairs.get(index).getName(), stringBody);
		        		entity.addPart(fbp);
		        	}
	        	}else{
	        		stringBody = new StringBody(nameValuePairs.get(index).getValue()+"",
	        				Charset.forName("UTF-8")); 
	        		fbp = new FormBodyPart(nameValuePairs.get(index).getName(), stringBody);
	        		entity.addPart(fbp);
	        	}
	        }
	        LogMgr.i("发送http请求008");
	        if (upFileFlag == true) {// 文件 上传 
	        	httpPost.setEntity(entity); 
	        } else { 
                /** 添加请求参数到请求对象 */ 
	        	httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, 
                        HTTP.UTF_8)); 
	        } 
	        
	        LogMgr.i("发送http请求");
	        HttpResponse response = httpClient.execute(httpPost, localContext); 
	        LogMgr.i("发送http请求返回");
	        if (response != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					resultStr = EntityUtils.toString(response.getEntity(),"UTF-8");
				}
			} else {
				resultStr ="-1";
			}
	    }catch(ConnectTimeoutException e){
			resultStr ="-2";
			e.printStackTrace();
		}catch(SocketTimeoutException e){
			resultStr ="-2";
			e.printStackTrace();
		}catch(IOException e) {  
			resultStr ="-3";
	        e.printStackTrace();  
	    }catch(OutOfMemoryError e){
	    	System.gc();
	    	resultStr = "-3";
	    	e.printStackTrace();
	    }catch(Exception e){
	    	resultStr ="-2";
	    	e.printStackTrace();
	    }
	    return resultStr;
	}
	
	/**
	 * 推送jpg图片
	 * @param url
	 * @param nameValuePairs
	 * @return
	 */
	public static String postMedia(String url, List<NameValuePair> nameValuePairs) {  
		String resultStr="-1";
	    HttpClient httpClient = new DefaultHttpClient();  
	    HttpContext localContext = new BasicHttpContext();  
	    HttpPost httpPost = new HttpPost(url);  
	    try {  
	        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
	        for(int index=0; index < nameValuePairs.size(); index++) {  
	        	FileBody fb=new FileBody(new File (nameValuePairs.get(index).getValue()), "");//image/jpg
                entity.addPart(nameValuePairs.get(index).getName(),fb);  
	        }  
	        httpPost.setEntity(entity);  
	        HttpResponse response = httpClient.execute(httpPost, localContext);  
	        if (response != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					resultStr = EntityUtils.toString(response.getEntity());
				}
			} else {
				Log.d(TAG, "服务器无响应");
			}
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }
	    return resultStr;
	}

	/**
	 * 请求服务器，下载文件到指定路径
	 * @param url
	 * @param params
	 * @param savePath
	 * @return
	 */
	@SuppressWarnings("finally")
	public static String post(String url, List<NameValuePair> params,String savePath) {
		String resultStr = null;
		DefaultHttpClient httpClient=null;
		HttpPost httpPost = new HttpPost(url);
		try {
			if (params != null) {
				httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));
			}
			httpClient = new DefaultHttpClient();
			//设置超时限制
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5*1000);
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 20*1000);
			HttpResponse response = httpClient.execute(httpPost);
			if (response != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					int BUFFER_SIZE = 4096;
					InputStream in = response.getEntity().getContent();
//					long l = response.getEntity().getContentLength();
//					Log.i("Entity",l+"");
					File file=new File(savePath);
					File parent = file.getParentFile(); 
					if(parent!=null&&!parent.exists()){
						parent.mkdirs();
					} 
					if(!file.exists()){
						file.createNewFile();
					}
					FileOutputStream outStream = new FileOutputStream(file);
					byte[] data = new byte[BUFFER_SIZE];  
					int count = -1;  
					while((count = in.read(data,0,BUFFER_SIZE)) != -1)  
						outStream.write(data, 0, count);  
					outStream.flush();
					data = null;  
					resultStr = "0"; 
				}
			} else {
				Log.d(TAG, "服务器无响应");
			}
		} catch(ConnectTimeoutException e){
			resultStr ="-1";
			e.printStackTrace();
		}catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
			return resultStr;
		}
	}

}
