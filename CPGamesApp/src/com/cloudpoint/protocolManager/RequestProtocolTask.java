package com.cloudpoint.protocolManager;

import java.util.Map;



public class RequestProtocolTask implements Runnable {

	String http_request_method ;
	Map<String,String> params;
	
	DataProtocolInterface dataProcotolInterface;
	
	BaseProtocol baseProtocol;
	/**	
	public RequestProcotolTask(String http_request_method,
			Map<String, String> params,DataProcotolInterface dataProcotolInterface) {
		super();
		this.http_request_method = http_request_method;
		this.params = params;
		this.dataProcotolInterface = dataProcotolInterface;
	
	    baseProtocol = new BaseProtocol();
		baseProtocol.setProcotolInterface(dataProcotolInterface);
	}
*/
	public RequestProtocolTask(
			Map<String, String> params,DataProtocolInterface dataProcotolInterface) {
		super();
		//this.http_request_method = http_request_method;
		this.params = params;
		this.dataProcotolInterface = dataProcotolInterface;
	
	    baseProtocol = new BaseProtocol();
		baseProtocol.setProcotolInterface(dataProcotolInterface);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Object object = BaseProtocol.startInvoke(params);
		if(object != null){
			baseProtocol.getProcotolInterface().onResposeProcotolData(object);
		}else{
			baseProtocol.getProcotolInterface().onResposeProcotolData(null);
		}
	}

}
