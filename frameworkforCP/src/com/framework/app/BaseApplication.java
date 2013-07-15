package com.framework.app;

import com.framework.Exception.CrashHandler;
import com.framework.Exception.UnhandledExceptionHandler;
import com.framework.ImageFileLoader.FileLoader;
import com.framework.Util.AppCfgManager;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;



/**
 * 全局应用程序
 * 
 * @author 
 * 
 */
public class BaseApplication extends Application {

//	public static Context CONTEXT;

	public static final String TAG = BaseApplication.class.getSimpleName();
	
	public static BaseApplication baseApplication;
	
	public static BaseApplication getInstance(){
		
		if(baseApplication == null){
			baseApplication = new BaseApplication();
		}
		return baseApplication;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		//CONTEXT = getApplicationContext();
		
		baseApplication = this;
		AppCfgManager.setApplication(baseApplication);
		
		new UnhandledExceptionHandler();
		FileLoader.getInstance().start();
		
		String crashPath = "/sdcard/juxian/" +  baseApplication.getApplicationInfo().packageName + "/crash/";
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(baseApplication, crashPath);
		
		
	}
	
	@Override
	public void onTerminate() {
		FileLoader.getInstance().exitRun();
		AppCfgManager.destroy();
		super.onTerminate();
	}

}
