package com.framework.analytics;

import java.util.LinkedList;

import com.framework.Exception.EvtLog;

import android.content.Context;



/**
 * ͳ�Ʒ����ⲿ����ʵ����
 * 
 * @author 
 * 
 */
public class AnalyticsExternalCall {

	private final String TAG = "AnalyticsExternalCall";
	private AnalyticsInterface mUmAnalytics;

	// �����߳�
	private ReportSender mReportSender = new ReportSender();
	// ��ִ�е��������
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
	 * �ռ����������Ϣ (����ר�з���)
	 * 
	 * @param context
	 *            Activity ������
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
	 * �����������ã��ϴ����� (����ר�з���)
	 * 
	 * @param context
	 *            Activity ������
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
	 * ����Ĭ���ϴ����� (����ר�з���)
	 * 
	 * @param context
	 *            Activity������
	 * @param reportPolicy
	 *            ���Ա�� REALTIME ʵʱ,BATCH_AT_LAUNCH ÿ������, DAILY ÿ��, WIFIONLY
	 *            ��wifi
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
	 * ����sessionͳ��ʱ����(����ר�з���)
	 * 
	 * @param millis
	 *            ʱ����,���뼶 default��30000 ms
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
	 * onResume(),����ͳ�Ʒ���ҳ���ʱ��
	 * 
	 * @param context
	 *            Activity������
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
	 * onPause()����,����ͳ���뿪ҳ���ʱ��
	 * 
	 * @param context
	 *            Activity������
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
	 * onEvent(),�Զ����¼�,�ɸ��ݲ�����ͳ�Ʋ�ͬ�¼�
	 * 
	 * @param context
	 *            Activity������
	 * @param eventId
	 *            �¼�ID
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
	 * onEvent(),�Զ����¼�,�ɸ��ݲ�����ͳ�Ʋ�ͬ�¼�
	 * 
	 * @param context
	 *            Activity������
	 * @param eventId
	 *            �¼�ID
	 * @param acc
	 *            ��������
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
	 * onEvent(),�Զ����¼�,�ɸ��ݲ�����ͳ�Ʋ�ͬ�¼�
	 * 
	 * @param context
	 *            Activity������
	 * @param eventId
	 *            �¼�ID
	 * @param label
	 *            ��ǩ
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
	 * onEvent(),�Զ����¼�,�ɸ��ݲ�����ͳ�Ʋ�ͬ�¼�
	 * 
	 * @param context
	 *            Activity������
	 * @param eventId
	 *            �¼�ID
	 * @param label
	 *            ��ǩ
	 * @param acc
	 *            ��������
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
