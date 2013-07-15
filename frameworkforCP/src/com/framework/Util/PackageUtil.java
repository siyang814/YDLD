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
	 * ��ȡӦ�ó���İ汾��
	 * 
	 * @param context
	 *            ������
	 * @return �汾��
	 * @throws NameNotFoundException
	 *             �Ҳ����İ汾�ŵ��쳣��Ϣ
	 */
	public static int getVersionCode() throws NameNotFoundException {
		int verCode = BaseApplication.getInstance().getPackageManager().getPackageInfo(
				BaseApplication.getInstance().getPackageName(), 0).versionCode;

		return verCode;
	}

	/**
	 * ��ȡӦ�ó�����ⲿ�汾��
	 * 
	 * @param context
	 *            ������
	 * @return �ⲿ�汾��
	 * @throws NameNotFoundException
	 *             �Ҳ�����Ϣ���쳣
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
	 * ��ȡ string.xml �ļ�������ַ���
	 * 
	 * @param resourceId
	 *            ��Դid
	 * @return ���� string.xml �ļ�������ַ���
	 */
	public static String getString(int resourceId) {
		Resources res = BaseApplication.getInstance().getResources();
		return res.getString(resourceId);
	}

	/**
	 * 
	 * @return ����ֻ����ն˱�ʶ
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

		EvtLog.d(TAG, "Ψһ�ն˱�ʶ�ţ�" + tvDevice);
		return tvDevice;
	}

	/**
	 * 
	 * @return ����ֻ��ͺ�
	 */
	public static String getDeviceType() {
		String deviceType = android.os.Build.MODEL;
		return deviceType;
	}

	/**
	 * 
	 * @return ��ò���ϵͳ�汾��
	 */

	public static String getSysVersion() {
		String sysVersion = android.os.Build.VERSION.RELEASE;
		return sysVersion;
	}

	/**
	 * ��ȡmanifest.xml��application��ǩ�µ�������
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
	 * ��ȡmanifest.xml��application��ǩ�µ�������
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
	 * ��ȡmanifest.xml��application��ǩ�µ�������
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
