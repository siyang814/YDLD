package com.framework.Util;

import com.framework.Exception.EvtLog;
import com.framework.app.BaseApplication;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;


/**
 * 
 * @author 
 * 
 */
public class PackageUtil {

	private static final String TAG = "PackageUtil";
	private static final String DEVICE_ID = "Unknow";

	/**
	 * 获取应用程序的版本号
	 * 
	 * @param context
	 *            上下文
	 * @return 版本号
	 * @throws NameNotFoundException
	 *             找不到改版本号的异常信息
	 */
	public static int getVersionCode() throws NameNotFoundException {
		int verCode = BaseApplication.getInstance().getPackageManager().getPackageInfo(
				BaseApplication.getInstance().getPackageName(), 0).versionCode;

		return verCode;
	}

	/**
	 * 获取应用程序的外部版本号
	 * 
	 * @param context
	 *            上下文
	 * @return 外部版本号
	 * @throws NameNotFoundException
	 *             找不到信息的异常
	 */
	public static String getVersionName() throws NameNotFoundException {
		String versionName = BaseApplication.getInstance().getPackageManager().getPackageInfo(
				BaseApplication.getInstance().getPackageName(), 0).versionName;

		return versionName;
	}

	public static String getLocalMacAddress() {
		WifiManager wifi = (WifiManager) BaseApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();

		return info.getMacAddress();
	}

	/**
	 * 获取 string.xml 文件定义的字符串
	 * 
	 * @param resourceId
	 *            资源id
	 * @return 返回 string.xml 文件定义的字符串
	 */
	public static String getString(int resourceId) {
		Resources res = BaseApplication.getInstance().getResources();
		return res.getString(resourceId);
	}

	/**
	 * 
	 * @return 获得手机端终端标识
	 */
	public static String getTerminalSign() {
		String tvDevice = null;
		TelephonyManager tm = (TelephonyManager) BaseApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
		tvDevice = tm.getDeviceId();
		if (tvDevice == null) {
			tvDevice = getLocalMacAddress();
		}

		if (tvDevice == null) {
			tvDevice = DEVICE_ID;
		}

		EvtLog.d(TAG, "唯一终端标识号：" + tvDevice);
		return tvDevice;
	}

	/**
	 * 
	 * @return 获得手机型号
	 */
	public static String getDeviceType() {
		String deviceType = android.os.Build.MODEL;
		return deviceType;
	}

	/**
	 * 
	 * @return 获得操作系统版本号
	 */

	public static String getSysVersion() {
		String sysVersion = android.os.Build.VERSION.RELEASE;
		return sysVersion;
	}

	/**
	 * 读取manifest.xml中application标签下的配置项
	 * 
	 * @param key
	 * @return
	 */
	public static String getConfigString(String key) {
		String val = "";
		try {
			ApplicationInfo appInfo = BaseApplication.getInstance().getPackageManager().getApplicationInfo(
					BaseApplication.getInstance().getPackageName(), PackageManager.GET_META_DATA);
			val = appInfo.metaData.getString(key);
			if(val == null){
				EvtLog.e(TAG, "please set config value for "+key+" in manifest.xml first");				
			}
		} catch (NameNotFoundException e) {
			EvtLog.e(TAG, e);
		}
		return val;
	}

	/**
	 * 读取manifest.xml中application标签下的配置项
	 * 
	 * @param key
	 * @return
	 */
	public static int getConfigInt(String key) {
		int val = 0;
		try {
			ApplicationInfo appInfo = BaseApplication.getInstance().getPackageManager().getApplicationInfo(
					BaseApplication.getInstance().getPackageName(), PackageManager.GET_META_DATA);
			val = appInfo.metaData.getInt(key);
		} catch (NameNotFoundException e) {
			EvtLog.e(TAG, e);
		}
		return val;
	}

	/**
	 * 读取manifest.xml中application标签下的配置项
	 * 
	 * @param key
	 * @return
	 */
	public static boolean getConfigBoolean(String key) {
		boolean val = false;
		try {
			ApplicationInfo appInfo = BaseApplication.getInstance().getPackageManager().getApplicationInfo(
					BaseApplication.getInstance().getPackageName(), PackageManager.GET_META_DATA);
			val = appInfo.metaData.getBoolean(key);
		} catch (NameNotFoundException e) {
			EvtLog.e(TAG, e);
		}
		return val;
	}
}
