package com.cloudpoint.protocolManager;

import android.content.Context;
import android.os.Handler;

public class DataManager {

	public static final int NETWORK_ERROR = 0;
	public static final int NOTIFICATION = 1;
	public static final int NOTIFICATION_SUBMIT_SUCCESS = 2;
	public static final int NOTIFICATION_WEIBOKEY = 3;
	public static final int NOTIFICATION_CHECKINFO = 4;
	public static final int NOTIFICATION_RESPONSE_ERRORINFO = 5;
	public static final int TIMEOUT_ERROR = 6;
	
	public static final int NOTIFICATION_SUBMIT_GETCODE = 7;
	public static final int NOTIFICATION_NOTREGISTERLOGIN = 8;
	
	private Context mContext;
	private Handler mHandler;
	
	
	public DataManager(Context mContext, Handler mHandler) {
		super();
		this.mContext = mContext;
		this.mHandler = mHandler;
	}
}
