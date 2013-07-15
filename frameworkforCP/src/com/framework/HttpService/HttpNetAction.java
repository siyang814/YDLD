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
	// �������ӳ�ʱʱ��
	private final int ConnectionTimeout = 120000;
	// ���ӳ�ʱʱ��,0 5s��10s; 1,20s
	private int SoTimeout = 120000;
	// �����С
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
	 * Get����
	 * @param url
	 * @return
	 */
	
	public String HttpGet(String url){
		HttpParams httpParams = new BasicHttpParams();
		
		// HTTP Э��İ汾,1.1/1.0/0.9
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		
		 // �ַ���
		HttpProtocolParams.setContentCharset(httpParams, encode);
		HttpProtocolParams.setHttpElementCharset(httpParams, encode);
		HttpProtocolParams.setUseExpectContinue(httpParams, true);
		// �����ض���ȱʡΪ true
		HttpClientParams.setRedirecting(httpParams, true);
		// �������ӳ�ʱ 5�� Socket ��ʱ���Լ� Socket �����С
		HttpConnectionParams
						.setConnectionTimeout(httpParams, ConnectionTimeout);
				// socket��ʱʱ�䣬10��
		HttpConnectionParams.setSoTimeout(httpParams, SoTimeout);
				// ����10k
		HttpConnectionParams.setSocketBufferSize(httpParams, SocketBufferSize);
		
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		
	//	String temp = url.replace("", "%20").replaceAll("\\<", "%3C").replaceAll("\\>", "%3E").replaceAll("\\|", "%7C");
		
		 HttpGet request = new HttpGet(url);
		 
		 try {
			 response = httpClient.execute(request);
			int status = response.getStatusLine().getStatusCode();
			if (status != 200 && status != 206){
				//throw (new RuntimeException());//Ӧ�÷��ط���������
				 return null;
			}
				
			// ȡ�����ݼ�¼
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
