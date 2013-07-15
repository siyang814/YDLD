package com.framework.analytics;

import android.content.Context;

/**
 * 统计分析接口
 * 
 * @author 
 * 
 */
public interface AnalyticsInterface {
	/**
	 * onResume()方法，用于统计返回页面的时间的接口
	 * 
	 * @param context
	 *            Activity上下文
	 */
	void onResume(Context context);

	/**
	 * onPause()方法,用于统计离开页面的时间的接口
	 * 
	 * @param context
	 *            Activity上下文
	 */
	void onPause(Context context);

	/**
	 * onEvent(),自定义事件,可根据参数来统计不同事件
	 * 
	 * @param context
	 *            Activity上下文
	 * @param eventId
	 *            事件ID
	 */
	void onEvent(Context context, String eventId);

	/**
	 * onEvent(),自定义事件,可根据参数来统计不同事件
	 * 
	 * @param context
	 *            Activity上下文
	 * @param eventId
	 *            事件ID
	 * @param acc
	 *            触发次数
	 */
	void onEvent(Context context, String eventId, int acc);

	/**
	 * onEvent(),自定义事件,可根据参数来统计不同事件
	 * 
	 * @param context
	 *            Activity上下文
	 * @param eventId
	 *            事件ID
	 * @param label
	 *            标签
	 */
	void onEvent(Context context, String eventId, String label);

	/**
	 * onEvent(),自定义事件,可根据参数来统计不同事件
	 * 
	 * @param context
	 *            Activity上下文
	 * @param eventId
	 *            事件ID
	 * @param label
	 *            标签
	 * @param acc
	 *            触发次数
	 */

	void onEvent(Context context, String eventId, String label, int acc);

	/**
	 * 设置Debug模式
	 * 
	 * @param b
	 *            是否有Debug输出
	 */
	void setDeBugModel(boolean b);
}
