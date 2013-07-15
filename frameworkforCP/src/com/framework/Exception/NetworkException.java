package com.framework.Exception;

import com.framework.Util.PackageUtil;
import com.framework.R;


/**
 * �����쳣
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
	 * ���캯��
	 */
	public NetworkException() {
		super(MESSAGE);
	}

	/**
	 * ���캯��
	 * 
	 * @param message
	 *            �����쳣������
	 */
	public NetworkException(String message) {
		super(message);
	}
}
