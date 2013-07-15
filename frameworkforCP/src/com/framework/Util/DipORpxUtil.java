package com.framework.Util;



import android.content.Context;

public class DipORpxUtil {

	
	/**
	 * �����ֻ��ķֱ��ʴ� dp �ĵ�λ ת��Ϊ px(����)
	 * 
	 * ����llp.height=Dip2pxUtil.dip2px(c,81*num);num������
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	/**
	* �����ֻ��ķֱ��ʴ� px(����) �ĵ�λ ת��Ϊ dp
	*/
	public static int px2dip(Context context, float pxValue) {
	  final float scale = context.getResources().getDisplayMetrics().density;
	  return (int) (pxValue / scale + 0.5f);
	} 

	
}
