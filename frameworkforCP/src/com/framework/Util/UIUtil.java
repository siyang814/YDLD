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
 * UI的帮助类
 * 
 * @author
 * 
 */
public class UIUtil {
	/**
	 * 设置listview高度，以适应内容
	 * 
	 * @param listView
	 *            指定的listview
	 */
	public static void setListViewHeightMatchContent(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}

		int totalHeight = 0;
		int length = listAdapter.getCount();
		for (int i = 0; i < length; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	/**
	 * 设置view的高度
	 * 
	 * @param view
	 *            指定的view
	 * @param height
	 *            指定的高度，以像素为单位
	 */
	public static void setViewHeight(View view, int height) {
		ViewGroup.LayoutParams params = view.getLayoutParams();
		params.height = height;
		view.setLayoutParams(params);
	}

	/**
	 * 设置不同颜色的文字
	 * 
	 * @param startPos
	 *            需要文字颜色不同的开始位置
	 * @param endPos
	 *            需要文字颜色不同的结束位置
	 * @param text
	 *            文字内容
	 * @param color
	 *            需要转化成的颜色
	 * @param tv
	 *            需要操作的textview
	 */
	public static void setColorfulText(int startPos, int endPos, String text, int color, TextView tv) {
		SpannableStringBuilder builder = new SpannableStringBuilder(text);
		builder.setSpan(new ForegroundColorSpan(color), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tv.setText(builder);
	}

	/**
	 * dip转换为px
	 * 
	 * @param context
	 *            上下文对象
	 * @param dipValue
	 *            dip值
	 * @return px值
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * px转换为dip
	 * 
	 * @param context
	 *            上下文对象
	 * @param pxValue
	 *            px值
	 * @return dip值
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
