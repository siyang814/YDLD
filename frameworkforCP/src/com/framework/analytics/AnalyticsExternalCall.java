package com.framework.analytics;

import java.util.LinkedList;

import com.framework.Exception.EvtLog;

import android.content.Context;



/**
 * 统计分析外部调用实体类
 * 
 * @author 
 * 
 */
public class AnalyticsExternalCall {

	private final String TAG = "AnalyticsExternalCall";
	private AnalyticsInterface mUmAnalytics;

	// 发送线程
	private ReportSender mReportSender = new ReportSender();
	// 待执行的任务队列
	private LinkedList<Runnable> queue = new LinkedList<Runnable>();

	class ReportSender extends Thread {
		@Override
		public void run() {
			while (true) {
				try {
					if (queue.size() == 0) {
						synchronized (queue) {
							queue.wait();
						}
					}

					if (queue.size() != 0) {
						Runnable r;
						synchronized (queue) {
							r = queue.removeFirst();
						}
						r.run();
					}
				} catch (Exception e) {
					EvtLog.e(TAG, e);
				}
			}
		}
	}

	private void addReport(Runnable r) {
		if (r == null) {
			return;
		}
		synchronized (queue) {
			queue.addLast(r);
			queue.notifyAll();
		}
	}

	/**
	 * 	
	 */
	public AnalyticsExternalCall() {
		mReportSender.setPriority(Thread.NORM_PRIORITY - 1);
		mReportSender.start();
		mUmAnalytics = UmAnalyticsClass.instance();
	}

	/**
	 * 收集程序错误信息 (友盟专有方法)
	 * 
	 * @param context
	 *            Activity 上下文
	 */
	public void umOnError(final Context context) {
		addReport(new Runnable() {
			@Override
			public void run() {
				if (mUmAnalytics != null && mUmAnalytics.getClass().equals(UmAnalyticsClass.class)) {
					((UmAnalyticsClass) mUmAnalytics).onError(context);
				}
			}
		});
	}

	/**
	 * 更新在线设置，上传策略 (友盟专有方法)
	 * 
	 * @param context
	 *            Activity 上下文
	 */
	public void umUpdateOnlineConfig(final Context context) {
		addReport(new Runnable() {
			@Override
			public void run() {
				if (mUmAnalytics != null && mUmAnalytics.getClass().equals(UmAnalyticsClass.class)) {
					((UmAnalyticsClass) mUmAnalytics).updateOnlineConfig(context);
				}
			}
		});
	}

	/**
	 * 设置默认上传策略 (友盟专有方法)
	 * 
	 * @param context
	 *            Activity上下文
	 * @param reportPolicy
	 *            策略编号 REALTIME 实时,BATCH_AT_LAUNCH 每次启动, DAILY 每天, WIFIONLY
	 *            仅wifi
	 */
	public void umSetDefaultReportPolicy(final Context context, final int reportPolicy) {
		addReport(new Runnable() {
			@Override
			public void run() {
				if (mUmAnalytics != null && mUmAnalytics.getClass().equals(UmAnalyticsClass.class)) {
					((UmAnalyticsClass) mUmAnalytics).setDefaultReportPolicy(context, reportPolicy);
				}
			}
		});
	}

	/**
	 * 设置session统计时间间隔(友盟专有方法)
	 * 
	 * @param millis
	 *            时间间隔,毫秒级 default：30000 ms
	 */
	public void umSetSessionContinueMillis(final long millis) {
		addReport(new Runnable() {
			@Override
			public void run() {
				if (mUmAnalytics != null && mUmAnalytics.getClass().equals(UmAnalyticsClass.class)) {
					((UmAnalyticsClass) mUmAnalytics).setSessionContinueMillis(millis);
				}
			}
		});
	}

	/**
	 * onResume(),用于统计返回页面的时间
	 * 
	 * @param context
	 *            Activity上下文
	 */
	public void analyticsOnResume(final Context context) {
		addReport(new Runnable() {
			@Override
			public void run() {
				mUmAnalytics.onResume(context);
			}
		});
	}

	/**
	 * onPause()方法,用于统计离开页面的时间
	 * 
	 * @param context
	 *            Activity上下文
	 */
	public void analyticsOnPause(final Context context) {
		addReport(new Runnable() {
			@Override
			public void run() {
				mUmAnalytics.onPause(context);
			}
		});
	}

	/**
	 * onEvent(),自定义事件,可根据参数来统计不同事件
	 * 
	 * @param context
	 *            Activity上下文
	 * @param eventId
	 *            事件ID
	 */
	public void analyticsOnEvent(final Context context, final String eventId) {
		addReport(new Runnable() {
			@Override
			public void run() {
				mUmAnalytics.onEvent(context, eventId);
			}
		});
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
	public void analyticsOnEvent(final Context context, final String eventId, final int acc) {
		addReport(new Runnable() {
			@Override
			public void run() {
				mUmAnalytics.onEvent(context, eventId, acc);
			}
		});
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
	public void analyticsOnEvent(final Context context, final String eventId, final String label) {
		addReport(new Runnable() {
			@Override
			public void run() {
				mUmAnalytics.onEvent(context, eventId, label);
			}
		});
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
	public void analyticsOnEvent(final Context context, final String eventId, final String label, final int acc) {
		addReport(new Runnable() {
			@Override
			public void run() {
				mUmAnalytics.onEvent(context, eventId, label, acc);
			}
		});
	}
}
