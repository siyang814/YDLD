package com.framework.analytics;

import android.content.Context;

/**
 * ͳ�Ʒ����ӿ�
 * 
 * @author 
 * 
 */
public interface AnalyticsInterface {
	/**
	 * onResume()����������ͳ�Ʒ���ҳ���ʱ��Ľӿ�
	 * 
	 * @param context
	 *            Activity������
	 */
	void onResume(Context context);

	/**
	 * onPause()����,����ͳ���뿪ҳ���ʱ��Ľӿ�
	 * 
	 * @param context
	 *            Activity������
	 */
	void onPause(Context context);

	/**
	 * onEvent(),�Զ����¼�,�ɸ��ݲ�����ͳ�Ʋ�ͬ�¼�
	 * 
	 * @param context
	 *            Activity������
	 * @param eventId
	 *            �¼�ID
	 */
	void onEvent(Context context, String eventId);

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
	void onEvent(Context context, String eventId, int acc);

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
	void onEvent(Context context, String eventId, String label);

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

	void onEvent(Context context, String eventId, String label, int acc);

	/**
	 * ����Debugģʽ
	 * 
	 * @param b
	 *            �Ƿ���Debug���
	 */
	void setDeBugModel(boolean b);
}
