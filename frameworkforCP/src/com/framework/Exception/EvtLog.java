package com.framework.Exception;

import org.apache.http.conn.ConnectTimeoutException;

import android.util.Log;

import com.framework.Util.PackageUtil;
import com.framework.app.BottomTab;


/**
 * 
 * @author 
 * 
 */
public class EvtLog {
	public static boolean isDebugLoggable = true;
	public static boolean isErrorLoggable = true;
	static {
		isDebugLoggable = PackageUtil.getConfigBoolean("debug_log_enable");
		isErrorLoggable = PackageUtil.getConfigBoolean("error_log_enable");
	}

	/**
	 * 输出debug信息
	 * 
	 * @param tag
	 *            标签
	 * @param msg
	 *            信息
	 * 
	 */
	public static void d(String tag, String msg) {
		if (isDebugLoggable) {
			Log.d(tag, msg);
		}
	}

	/**
	 * @param tag
	 *            标签
	 * @param msg
	 *            信息
	 * 
	 */
	public static void i(String tag, String msg) {
		if (isDebugLoggable) {
			Log.i(tag, msg);
		}
	}

	/**
	 * @param tag
	 *            标签
	 * @param msg
	 *            信息
	 * 
	 */
	public static void w(String tag, String msg) {
		if (isDebugLoggable) {
			Log.w(tag, msg);
		}
	}

	/**
	 * 输出错误信息
	 * 
	 * @param tag
	 *            标签
	 * @param exception
	 *            输出异常信息到控制台
	 */
	public static void w(String tag, Throwable exception) {
		String message = "";
		if (isDebugLoggable) {
			Log.w(tag, exception);
		}
	}

	/**
	 * 输出error信息并在程序中toast显示，该错误不会记录在日志文件中
	 * 
	 * @param tag
	 *            标签
	 * @param msg
	 *            信息
	 * 
	 */
	public static void e(String tag, String msg) {
		if (isErrorLoggable) {
			Log.e(tag, msg);
		}

		BottomTab.toast(msg);
	}

	public static void e(String tag, String msg, boolean isShowDialog) {
		if (isErrorLoggable) {
			Log.e(tag, msg);
		}
		if (isShowDialog) {
			BottomTab.toast(msg);
		}
	}

	/**
	 * 输出错误信息
	 * 
	 * @param tag
	 *            标签
	 * @param exception
	 *            异常对象：如果是MessageException，则会抛出提示信息；
	 *            如果是NetworkException，只会在控制台输出；
	 *            如果是其他的Exception，则会记录日志信息，并输出通用的提示信息
	 */
	public static void e(String tag, Throwable exception) {
		String message = "";
		if (isErrorLoggable) {
			if (exception instanceof MessageException) {
				message = exception.getMessage();
				Log.e(tag, message);
			} else if (exception instanceof NetworkException) {
				message = exception.getMessage();
				Log.e(tag, message);
			} else if (exception instanceof ConnectTimeoutException) {
				message = exception.getMessage();
				Log.e(tag, message);
			} else {
				CrashHandler.getInstance().saveInformation(exception);
				Log.e(tag, "", exception);
			}
		}
		if (exception instanceof MessageException) {
			BottomTab.toast(message);
		}
	}
}
