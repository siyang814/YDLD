package com.framework.Exception;

import com.framework.Util.JavaUtil;







/**
 * δ�����쳣������
 * @author 
 *
 */
public class UnhandledExceptionHandler implements Thread.UncaughtExceptionHandler {
	
	private static final String TAG = UnhandledExceptionHandler.class.getSimpleName();
	
	private Thread.UncaughtExceptionHandler _oldHandler;

	public UnhandledExceptionHandler() {
		this._oldHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.currentThread().setUncaughtExceptionHandler(this);
	}

	/**
	 * ָ���߳��׳�δ������쳣ʱ���ص��˷�����¼��־
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		String msg = "";
		if (thread != null) {
			msg = thread.getName() + "\n";
		}
		msg += JavaUtil.getDetailFromThrowable(throwable);
		//Logger.e(TAG, msg);
		
		if (null != _oldHandler)
		{
			_oldHandler.uncaughtException(thread, throwable);
		}
	}
}