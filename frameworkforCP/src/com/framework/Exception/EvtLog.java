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
	 * ���debug��Ϣ
	 * 
	 * @param tag
	 *            ��ǩ
	 * @param msg
	 *            ��Ϣ
	 * 
	 */
	public static void d(String tag, String msg) {
		if (isDebugLoggable) {
			Log.d(tag, msg);
		}
	}

	/**
	 * @param tag
	 *            ��ǩ
	 * @param msg
	 *            ��Ϣ
	 * 
	 */
	public static void i(String tag, String msg) {
		if (isDebugLoggable) {
			Log.i(tag, msg);
		}
	}

	/**
	 * @param tag
	 *            ��ǩ
	 * @param msg
	 *            ��Ϣ
	 * 
	 */
	public static void w(String tag, String msg) {
		if (isDebugLoggable) {
			Log.w(tag, msg);
		}
	}

	/**
	 * ���������Ϣ
	 * 
	 * @param tag
	 *            ��ǩ
	 * @param exception
	 *            ����쳣��Ϣ������̨
	 */
	public static void w(String tag, Throwable exception) {
		String message = "";
		if (isDebugLoggable) {
			Log.w(tag, exception);
		}
	}

	/**
	 * ���error��Ϣ���ڳ�����toast��ʾ���ô��󲻻��¼����־�ļ���
	 * 
	 * @param tag
	 *            ��ǩ
	 * @param msg
	 *            ��Ϣ
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
	 * ���������Ϣ
	 * 
	 * @param tag
	 *            ��ǩ
	 * @param exception
	 *            �쳣���������MessageException������׳���ʾ��Ϣ��
	 *            �����NetworkException��ֻ���ڿ���̨�����
	 *            �����������Exception������¼��־��Ϣ�������ͨ�õ���ʾ��Ϣ
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
