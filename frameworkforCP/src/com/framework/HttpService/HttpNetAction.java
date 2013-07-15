package com.framework.HttpService;

import java.io.IOException;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.methods.HttpGet;

public class HttpNetAction {

	private static HttpNetAction httpNetAction;
	private final String encode = "UTF-8";
	// 建立连接超时时间
	private final int ConnectionTimeout = 120000;
	// 连接超时时间,0 5s，10s; 1,20s
	private int SoTimeout = 120000;
	// 缓存大小
	private final int SocketBufferSize = 10240;
	
	HttpResponse response = null;
	
	public HttpNetAction() {
		// TODO Auto-generated constructor stub
		super();
	}
	
	public static HttpNetAction getHttpNetAction(){
		if(null == httpNetAction){
			httpNetAction = new HttpNetAction();  
		}
		return httpNetAction;
	}
	/**
	 * Get联接
	 * @param url
	 * @return
	 */
	
	public String HttpGet(String url){
		HttpParams httpParams = new BasicHttpParams();
		
		// HTTP 协议的版本,1.1/1.0/0.9
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		
		 // 字符集
		HttpProtocolParams.setContentCharset(httpParams, encode);
		HttpProtocolParams.setHttpElementCharset(httpParams, encode);
		HttpProtocolParams.setUseExpectContinue(httpParams, true);
		// 设置重定向，缺省为 true
		HttpClientParams.setRedirecting(httpParams, true);
		// 设置连接超时 5和 Socket 超时，以及 Socket 缓存大小
		HttpConnectionParams
						.setConnectionTimeout(httpParams, ConnectionTimeout);
				// socket超时时间，10秒
		HttpConnectionParams.setSoTimeout(httpParams, SoTimeout);
				// 缓存10k
		HttpConnectionParams.setSocketBufferSize(httpParams, SocketBufferSize);
		
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		
	//	String temp = url.replace("", "%20").replaceAll("\\<", "%3C").replaceAll("\\>", "%3E").replaceAll("\\|", "%7C");
		
		 HttpGet request = new HttpGet(url);
		 
		 try {
			 response = httpClient.execute(request);
			int status = response.getStatusLine().getStatusCode();
			if (status != 200 && status != 206){
				//throw (new RuntimeException());//应该返回服务器错误
				 return null;
			}
				
			// 取得数据记录
			HttpEntity entity = response.getEntity();
			if(entity != null){
				String content = EntityUtils.toString(entity);
				String newContent = content;
				newContent.replaceAll("\n","");
				newContent.replaceAll("\r", "");
				
				return newContent;
			}
			
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
		 return null;
	}
}
