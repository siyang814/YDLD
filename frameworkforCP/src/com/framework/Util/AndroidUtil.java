package com.framework.Util;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.framework.app.BaseApplication;




import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


/**
 * android相关的辅助工具类
 *
 * @author 
 *
 */
public class AndroidUtil {
	
	/**
	 * 检查网络是否可用
	 * 
	 * @return
	 */
	public static boolean isNetworkAvailable() {
		ConnectivityManager conn = (ConnectivityManager) BaseApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conn.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
	
	/**
	 * 检查SD卡是否可用
	 * @return
	 */
	public static boolean isSDCardAvailable() {
		String status = Environment.getExternalStorageState(); 
		if(status.equals(Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}
	
	public static ProgressDialog createProgressDialog(Context context, String message) {
		ProgressDialog dialog = new ProgressDialog(context);
		Window window = dialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = 0.5f;// 透明度
		lp.dimAmount = 0.5f;// 黑暗度
		window.setAttributes(lp);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        return dialog;
	}
	
	/*
	 * 验证手机号码格式的方法
	 */
	public static boolean checkPhoneNum(String number){
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,2,5-9]))\\d{8}$");
		Matcher m = p.matcher(number);
		return m.matches();
	}
	
	/**
	 * 强制退出应用程序
	 */
	public static void forceQuit() {
		android.os.Process.killProcess(android.os.Process.myPid());
		Thread.currentThread().interrupt();//强制退出程序
		System.exit(0);//强制退出程序
	}
	
	/**
	 * 检查姓名合法性
	 * @param username
	 * @return
	 */
	public static boolean checkUserName(String username){         
		String expression = "[^a-z A-Z\u4E00-\u9FA5]";
		Matcher tagFind = Pattern.compile(
				expression,
				Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE
						| Pattern.DOTALL).matcher(username);
		if (tagFind.find()) {
			System.out.println(tagFind.group());
			return true;
			
		}else{
			return false;
		}

	}
	
	// 获取本机容量信息  
	public static String phoneCapacity() {  
	    // 获取本机信息  
	    File data = Environment.getDataDirectory();  
	    StatFs statFs = new StatFs(data.getPath());  
	    int availableBlocks = statFs.getAvailableBlocks();// 可用存储块的数量  
	    int blockCount = statFs.getBlockCount();// 总存储块的数量  
	  
	    int size = statFs.getBlockSize();// 每块存储块的大小  
	  
	    int totalSize = blockCount * size;// 总存储量  
	  
	    int availableSize = availableBlocks * size;// 可用容量  
	  
	    String phoneCapacity = Integer.toString(availableSize / 1024 / 1024)  
	            + "MB/" + Integer.toString(totalSize / 1024 / 1024) + "MB";  
	  
	    return phoneCapacity;  
	}  
	
	// 获取本机可用容量  MB  
	public static long phoneaVailableCapacity() {  
	    // 获取本机信息  
	    File data = Environment.getDataDirectory();  
	    StatFs statFs = new StatFs(data.getPath());  
	    int availableBlocks = statFs.getAvailableBlocks();// 可用存储块的数量  
	    int blockCount = statFs.getBlockCount();// 总存储块的数量  
	  
	    int size = statFs.getBlockSize();// 每块存储块的大小  
	  
//	    int totalSize = blockCount * size;// 总存储量  
	  
	    int availableSize = availableBlocks * size;// 可用容量  
	  
	    long availableCapacity = availableSize / 1024 / 1024;  
	  
	    return availableCapacity;  
	}
	  
	// 获取sdcard容量信息  
	public static String sdcardCapacity() {  
	    // 获取sdcard信息  
	    File sdData = Environment.getExternalStorageDirectory();  
	    StatFs sdStatFs = new StatFs(sdData.getPath());  
	  
	    int sdAvailableBlocks = sdStatFs.getAvailableBlocks();// 可用存储块的数量  
	    int sdBlockcount = sdStatFs.getBlockCount();// 总存储块的数量  
	    int sdSize = sdStatFs.getBlockSize();// 每块存储块的大小  
	    int sdTotalSize = sdBlockcount * sdSize;  
	    int sdAvailableSize = sdAvailableBlocks * sdSize;  
	  
	    String sdcardCapacity = Integer.toString(sdAvailableSize / 1024 / 1024)  
	            + "MB/" + Integer.toString(sdTotalSize / 1024 / 1024) + "MB";  
	    return sdcardCapacity;  
	} 
	
	// 获取sdcard可用容量  MB
	public static long sdAvailableSize() {  
	    // 获取sdcard信息  
	    File sdData = Environment.getExternalStorageDirectory();  
	    StatFs sdStatFs = new StatFs(sdData.getPath());  
	  
	    int sdAvailableBlocks = sdStatFs.getAvailableBlocks();// 可用存储块的数量  
//	    int sdBlockcount = sdStatFs.getBlockCount();// 总存储块的数量  
	    int sdSize = sdStatFs.getBlockSize();// 每块存储块的大小  
//	    int sdTotalSize = sdBlockcount * sdSize;  
	    int sdAvailableSize = sdAvailableBlocks * sdSize;  
	  
	    long sdAvailableCapacity = sdAvailableSize / 1024 / 1024;  //MB
	    return sdAvailableCapacity;  
	} 
	
	public static File downloadDirectory(String filename){
		String filePath;
		if(AndroidUtil.isSDCardAvailable())
			filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FileLoader/";
		else{
			filePath = "/data/data/"+AppCfgManager.getPackageName()+"/FileLoader/";//手机没SD卡时的文件下载目录
		}

		File f = new File(filePath);
		if(!f.exists()){ 
			f.mkdirs(); 
		}
		
		String local_file = f.getAbsolutePath() + "/" + filename;
		File file = new File(local_file);
		
		if(!AndroidUtil.isSDCardAvailable()){
			chmod("777", filePath);
			chmod("777", local_file);
		}
			
		return file;
	}
	
	//安装
	public static void LauchInstall(String FilePathName, Context context)
    {
        chmod("777", FilePathName);
        Intent intent = new Intent(Intent.ACTION_VIEW);  
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(FilePathName)), "application/vnd.android.package-archive");  
        context.startActivity(intent);                  
    }
   
    //修改apk权限
    public static void chmod(String permission, String path)
    {
        try
        {
            String command  = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
	// 查看输入日期与当前日期相差天数
	public static long howManyDays( int year,int month,int day) {
		Calendar calendar = Calendar.getInstance();// 可以对每个时间域单独修改
		int year1 = calendar.get(Calendar.YEAR);
		int month1 = calendar.get(Calendar.MONTH) + 1;
		int date1 = calendar.get(Calendar.DATE);
		Calendar ago = Calendar.getInstance();
		ago.set(year, month - 1, day);
		calendar.set(year1, month1 - 1, date1);
		long pass;
		pass = (ago.getTimeInMillis() - calendar.getTimeInMillis())/ (24 * 60 * 60000);
		Log.d("xuliangbo","pass||" + pass);
		return pass;
	}
}
