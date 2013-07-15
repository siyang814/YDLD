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
	 * 获取当前程序版本名
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
	 * 获取当前程序包名
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
	 * 获取当前程序版本code
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
	 * 应用程序退出时调用
	 */
	public static void destroy() {
	
	/*退出应用做一些东东
		*/
		application = null;
		
		//Logger.stop();
		AndroidUtil.forceQuit();
	}
}
