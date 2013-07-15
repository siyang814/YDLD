package com.framework.Util;
/**
 * 网络辅助类
 */


import com.framework.Exception.EvtLog;
import com.framework.app.BaseApplication;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



/**
 * @author 
 * 
 */
public class NetUtil {

	private static final String TAG = "NetUtil";

	/**
	 * 检查网络是否可用
	 * 
	 * @return 如果可用，返回true，不可用则返回false
	 */
	public static boolean isNetworkAvailable() {
		Context con = BaseApplication.getInstance();
		ConnectivityManager cm = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return false;
		}

		NetworkInfo[] info = cm.getAllNetworkInfo();
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}

		// return true;

		return false;
	}

	/**
	 * 检查网络是否可用
	 * 
	 * @return 如果可用，返回true，不可用则返回false
	 */
	public static boolean isGPSAvailable() {
		boolean result;
		LocationManager locationManager = (LocationManager) BaseApplication.getInstance()
				.getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			result = true;
		} else {
			result = false;
		}
		EvtLog.d(TAG, "result:" + result);

		return result;
	}

	/**
	 * 
	 * @return 检查wifi网络是否可用
	 */
	public static boolean isWifiAvailable() {
		return false;
	}

}
