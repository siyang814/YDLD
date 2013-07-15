package com.framework.analytics;

import android.content.Context;

import com.mobclick.android.MobclickAgent;

/**
 * ����ͳ�Ʒ�������
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
	 * �õ� UmAnalyticsClass�ĵ�������
	 * 
	 * @return UmAnalyticsClass ����
	 */
	protected static UmAnalyticsClass instance() {
		if (mUmAnalytics == null) {
			mUmAnalytics = new UmAnalyticsClass();
			mUmAnalytics.setDeBugModel(false);
		}
		return mUmAnalytics;
	}

	/**
	 * onResume(),����ͳ�Ʒ���ҳ���ʱ��
	 * 
	 * @param context
	 *            Activity������
	 */
	public void onResume(Context context) {
		MobclickAgent.onResume(context);
	}

	/**
	 * onPause()����,����ͳ���뿪ҳ���ʱ��
	 * 
	 * @param context
	 *            Activity������
	 */
	public void onPause(Context context) {
		MobclickAgent.onPause(context);
	}

	/**
	 * onEvent(),�Զ����¼�,�ɸ��ݲ�����ͳ�Ʋ�ͬ�¼�
	 * 
	 * @param context
	 *            Activity������
	 * @param eventId
	 *            �¼�ID
	 */
	public void onEvent(Context context, String eventId) {
		MobclickAgent.onEvent(context, eventId);
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
	public void onEvent(Context context, String eventId, int acc) {
		MobclickAgent.onEvent(context, eventId, acc);
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
	public void onEvent(Context context, String eventId, String label) {
		MobclickAgent.onEvent(context, eventId, label);
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
	public void onEvent(Context context, String eventId, String label, int acc) {
		MobclickAgent.onEvent(context, eventId, label, acc);
	}

	/**
	 * �ռ����������Ϣ
	 * 
	 * @param context
	 *            Activity������
	 */
	public void onError(Context context) {
		MobclickAgent.onError(context);
	}

	/**
	 * �����������ã��ϴ�����
	 * 
	 * @param context
	 *            Activity������
	 */
	public void updateOnlineConfig(Context context) {
		MobclickAgent.updateOnlineConfig(context);
	}

	/**
	 * ����Ĭ���ϴ�����
	 * 
	 * @param context
	 *            Activity������
	 * @param reportPolicy
	 *            ���Ա�� REALTIME ʵʱ,BATCH_AT_LAUNCH ÿ������, DAILY ÿ��, WIFIONLY
	 *            ��wifi
	 */
	public void setDefaultReportPolicy(Context context, int reportPolicy) {
		MobclickAgent.setDefaultReportPolicy(context, reportPolicy);
	}

	/**
	 * ����sessionͳ��ʱ����
	 * 
	 * @param millis
	 *            ʱ����,���뼶 default��30000 ms
	 */
	public void setSessionContinueMillis(long millis) {
		MobclickAgent.setSessionContinueMillis(millis);
	}

	/**
	 * ����DeBugģʽ
	 * 
	 * @param b
	 *            �Ƿ���DeBug���
	 */
	public void setDeBugModel(boolean b) {
		MobclickAgent.setDebugMode(b);
	}

}
