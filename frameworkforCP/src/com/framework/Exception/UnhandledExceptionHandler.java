package com.framework.Exception;

import com.framework.Util.JavaUtil;







/**
 * 未捕获异常处理类
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
	 * 指定线程抛出未捕获的异常时，回调此方法记录日志
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