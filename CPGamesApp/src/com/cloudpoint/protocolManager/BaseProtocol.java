package com.cloudpoint.protocolManager;

import java.net.URLEncoder;
import java.util.Map;

import com.framework.HttpService.HttpNetAction;




public class BaseProtocol {

	private static final String HTTP_REQUEST_URL = "http://www.crhealthclub.com/api/interface.php";
	
	public static Object startInvoke(Map<String,String> params){

		
		String strUrl = strtoUrl(HTTP_REQUEST_URL,params);
		
		HttpNetAction httpNetAction = HttpNetAction.getHttpNetAction();
		
		Object object = httpNetAction.HttpGet(strUrl);
		
		return object;
	}
	
	
	//ƴдURL
	public static String strtoUrl(String path ,Map<String,String> params){
			
			if(params == null){
				return path;
			}
			
			StringBuilder sb = new StringBuilder(path);
			sb.append('?');
			for(Map.Entry<String, String> entry : params.entrySet()){
				sb.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue())).append('&');
			}
			sb.deleteCharAt(sb.length()-1);
			
			return sb.toString();
    }
	
	
	
	private DataProtocolInterface procotolInterface;
	
	public static final String TAG = BaseProtocol.class.getSimpleName();
	
	public DataProtocolInterface getProcotolInterface() {
		return procotolInterface;
	}

	public void setProcotolInterface(DataProtocolInterface procotolInterface) {
		this.procotolInterface = procotolInterface;
	}
}
