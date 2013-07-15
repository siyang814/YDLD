package com.framework.Exception;

import com.framework.Util.PackageUtil;
import com.framework.R;


/**
 * 网络异常
 * 
 * @author 
 * 
 */
public class NetworkException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4521612743569217432L;
	private static String MESSAGE = PackageUtil.getString(R.string.network_is_not_available);

	/**
	 * 构造函数
	 */
	public NetworkException() {
		super(MESSAGE);
	}

	/**
	 * 构造函数
	 * 
	 * @param message
	 *            网络异常的内容
	 */
	public NetworkException(String message) {
		super(message);
	}
}
