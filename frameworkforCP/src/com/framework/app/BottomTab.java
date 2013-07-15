package com.framework.app;


import com.framework.Exception.EvtLog;
import com.framework.analytics.AnalyticsExternalCall;
import com.framework.analytics.UmAnalyticsClass;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.Toast;
import com.framework.R;

public class BottomTab extends TabActivity {
	
	private static final String TAG = "BottomTab";
	private static BottomTab INSTANCE;
	private String tag_debug = "BottomTab";
	private String tag_error = "BottomTab";
	private TabHost mTabHost = null;
	public static AbsoluteLayout mALayout;

	private AnalyticsExternalCall mAnalytics;
	private Context mContext;
	private long mSessionContinueMillis = 3000L;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		INSTANCE = this;
		
		setContentView(R.layout.tabhost_custom);
		mALayout = (AbsoluteLayout) findViewById(R.id.mLayout);
		

		mContext = this;
		EvtLog.d(TAG, "mContext1:" + mContext +"  mContext2："+getBaseContext());
		mTabHost = getTabHost();

		mAnalytics = new AnalyticsExternalCall();
		mAnalytics.umSetSessionContinueMillis(mSessionContinueMillis);
		mAnalytics.umSetDefaultReportPolicy(mContext, UmAnalyticsClass.REALTIME);
		mAnalytics.umUpdateOnlineConfig(mContext);
	}

	protected void onPause() {
		super.onPause();
		mAnalytics.analyticsOnPause(mContext);
	}

	protected void onResume() {
		super.onResume();
		mAnalytics.analyticsOnResume(mContext);
	}

	/*
	 * 添加一个子页面，tabViewResId为对应子页面的layout文件id，cls为管理子页面的Activity
	 */
	protected void setTabsBackground(int resId) {
		TabWidget tabs = mTabHost.getTabWidget();
		tabs.setBackgroundResource(resId);
	}

	/*
	 * 添加一个子页面，tabViewResId为对应子页面的layout文件id，cls为管理子页面的Activity
	 */
	protected void addOneTab(String tag, int tabViewResId, Class<?> cls) {
		mTabHost.addTab(mTabHost.newTabSpec(tag).setIndicator(getView(tabViewResId)).setContent(new Intent(this, cls)));
	}

	/*
	 * 添加一个子页面，tabView为对应子页面的view，cls为管理子页面的Activity
	 */
	protected void addOneTab(String tag, View tabView, Class<?> cls) {
		mTabHost.addTab(mTabHost.newTabSpec(tag).setIndicator(tabView).setContent(new Intent(this, cls)));
	}

	private View getView(int tabViewResId) {
		View layout = LayoutInflater.from(this).inflate(tabViewResId, null);

		return layout;
	}

	protected void setCurrentTab(int index) {
		mTabHost.setCurrentTab(index);
	}

	protected void setCurrentTabByTag(String tag) {
		mTabHost.setCurrentTabByTag(tag);
	}

	/**
	 * 弹出toast
	 * 
	 * @param msg
	 *            提示消息
	 */
	public static void toast(final String msg) {
		if (INSTANCE != null) {
			INSTANCE.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(INSTANCE, msg, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	/**
	 * 弹出toast
	 * 
	 * @param e
	 *            出错的exception
	 */
	public static void toast(final Throwable e) {
		if(INSTANCE == null){
			return ;
		}
		INSTANCE.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(INSTANCE, INSTANCE.getResources().getString(R.string.msg_operate_fail_try_again),
						Toast.LENGTH_SHORT).show();
			}
		});
	}
}