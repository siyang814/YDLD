package com.framework.Util;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * UI�İ�����
 * 
 * @author
 * 
 */
public class UIUtil {
	/**
	 * ����listview�߶ȣ�����Ӧ����
	 * 
	 * @param listView
	 *            ָ����listview
	 */
	public static void setListViewHeightMatchContent(ListView listView) {
		// ��ȡListView��Ӧ��Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		int length = listAdapter.getCount();
		for (int i = 0; i < length; i++) { // listAdapter.getCount()�������������Ŀ
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // ��������View �Ŀ��
			totalHeight += listItem.getMeasuredHeight(); // ͳ������������ܸ߶�
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()��ȡ�����ָ���ռ�õĸ߶�
		// params.height���õ�����ListView������ʾ��Ҫ�ĸ߶�
		listView.setLayoutParams(params);
	}

	/**
	 * ����view�ĸ߶�
	 * 
	 * @param view
	 *            ָ����view
	 * @param height
	 *            ָ���ĸ߶ȣ�������Ϊ��λ
	 */
	public static void setViewHeight(View view, int height) {
		ViewGroup.LayoutParams params = view.getLayoutParams();
		params.height = height;
		view.setLayoutParams(params);
	}

	/**
	 * ���ò�ͬ��ɫ������
	 * 
	 * @param startPos
	 *            ��Ҫ������ɫ��ͬ�Ŀ�ʼλ��
	 * @param endPos
	 *            ��Ҫ������ɫ��ͬ�Ľ���λ��
	 * @param text
	 *            ��������
	 * @param color
	 *            ��Ҫת���ɵ���ɫ
	 * @param tv
	 *            ��Ҫ������textview
	 */
	public static void setColorfulText(int startPos, int endPos, String text, int color, TextView tv) {
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		builder.setSpan(new ForegroundColorSpan(color), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(builder);
	}

	/**
	 * dipת��Ϊpx
	 * 
	 * @param context
	 *            �����Ķ���
	 * @param dipValue
	 *            dipֵ
	 * @return pxֵ
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * pxת��Ϊdip
	 * 
	 * @param context
	 *            �����Ķ���
	 * @param pxValue
	 *            pxֵ
	 * @return dipֵ
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
