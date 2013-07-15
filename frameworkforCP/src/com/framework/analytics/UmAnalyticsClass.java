package com.framework.analytics;

import android.content.Context;

import com.mobclick.android.MobclickAgent;

/**
 * 友盟统计分析调用
 * 
 * @author 
 * 
 */
public class UmAnalyticsClass implements AnalyticsInterface {

	public static final int REALTIME = 0;
	public static final int BATCH_AT_LAUNCH = 1;
	public static final int DAILY = 4;
	public static final int WIFIONLY = 5;

	private static UmAnalyticsClass mUmAnalytics;

	private UmAnalyticsClass() {

	}

	/**
	 * 得到 UmAnalyticsClass的单例对象
	 * 
	 * @return UmAnalyticsClass 单例
	 */
	protected static UmAnalyticsClass instance() {
		if (mUmAnalytics == null) {
			mUmAnalytics = new UmAnalyticsClass();
			mUmAnalytics.setDeBugModel(false);
		}
		return mUmAnalytics;
	}

	/**
	 * onResume(),用于统计返回页面的时间
	 * 
	 * @param context
	 *            Activity上下文
	 */
	public void onResume(Context context) {
		MobclickAgent.onResume(context);
	}

	/**
	 * onPause()方法,用于统计离开页面的时间
	 * 
	 * @param context
	 *            Activity上下文
	 */
	public void onPause(Context context) {
		MobclickAgent.onPause(context);
	}

	/**
	 * onEvent(),自定义事件,可根据参数来统计不同事件
	 * 
	 * @param context
	 *            Activity上下文
	 * @param eventId
	 *            事件ID
	 */
	public void onEvent(Context context, String eventId) {
		MobclickAgent.onEvent(context, eventId);
	}

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
	public void onEvent(Context context, String eventId, int acc) {
		MobclickAgent.onEvent(context, eventId, acc);
	}

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
	public void onEvent(Context context, String eventId, String label) {
		MobclickAgent.onEvent(context, eventId, label);
	}

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
	public void onEvent(Context context, String eventId, String label, int acc) {
		MobclickAgent.onEvent(context, eventId, label, acc);
	}

	/**
	 * 收集程序错误信息
	 * 
	 * @param context
	 *            Activity上下文
	 */
	public void onError(Context context) {
		MobclickAgent.onError(context);
	}

	/**
	 * 更新在线设置，上传策略
	 * 
	 * @param context
	 *            Activity上下文
	 */
	public void updateOnlineConfig(Context context) {
		MobclickAgent.updateOnlineConfig(context);
	}

	/**
	 * 设置默认上传策略
	 * 
	 * @param context
	 *            Activity上下文
	 * @param reportPolicy
	 *            策略编号 REALTIME 实时,BATCH_AT_LAUNCH 每次启动, DAILY 每天, WIFIONLY
	 *            仅wifi
	 */
	public void setDefaultReportPolicy(Context context, int reportPolicy) {
		MobclickAgent.setDefaultReportPolicy(context, reportPolicy);
	}

	/**
	 * 设置session统计时间间隔
	 * 
	 * @param millis
	 *            时间间隔,毫秒级 default：30000 ms
	 */
	public void setSessionContinueMillis(long millis) {
		MobclickAgent.setSessionContinueMillis(millis);
	}

	/**
	 * 设置DeBug模式
	 * 
	 * @param b
	 *            是否有DeBug输出
	 */
	public void setDeBugModel(boolean b) {
		MobclickAgent.setDebugMode(b);
	}

}
