package com.framework.Util;







import android.app.Application;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;


public class AppCfgManager {
	
	//private static DataStorage dataStorage;
	
	private static Application application;
	
	public static Application getApplication() {
		return application;
	}
	
	public static void setApplication(Application app) {
		application = app;
		
	}
	/**
	 * ��ȡ��ǰ����汾��
	 * @return
	 */
	public static String getPackageVersion() {
		String version = "";
        try {
        	PackageManager pm = application.getPackageManager();  
    	    PackageInfo pi = null;
			pi = pm.getPackageInfo(application.getPackageName(), 0);
			version = pi.versionName;
		} catch (Exception e) {
			version = ""; // failed, ignored  
		}
		return version;
	}
	
	
	/**
	 * ��ȡ��ǰ�������
	 * @return
	 */
	public static String getPackageName() {
		String version = "";
        try {
        	PackageManager pm = application.getPackageManager();  
    	    PackageInfo pi = null;
			pi = pm.getPackageInfo(application.getPackageName(), 0);
			version = pi.packageName;
		} catch (Exception e) {
			version = ""; // failed, ignored  
		}
		return version;
	}
	
	/**
	 * ��ȡ��ǰ����汾code
	 * @return
	 */
	public static String getPackageCode() {
		String code = "";
        try {
        	PackageManager pm = application.getPackageManager();  
    	    PackageInfo pi = null;
			pi = pm.getPackageInfo(application.getPackageName(), 0);
			code = pi.versionName;
		} catch (Exception e) {
			code = "1"; // failed, ignored  
		}
		return code;
	}
	/**
	 * Ӧ�ó����˳�ʱ����
	 */
	public static void destroy() {
	
	/*�˳�Ӧ����һЩ����
		*/
		application = null;
		
		//Logger.stop();
		AndroidUtil.forceQuit();
	}
}
