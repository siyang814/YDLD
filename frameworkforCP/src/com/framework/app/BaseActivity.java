package com.framework.app;

import com.framework.widget.EvtPopupWindow;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;



/**
 * 所有应用Activity的基类
 * 
 * @author 
 * 
 */
public abstract class BaseActivity extends Activity {
	private final String TAG = "BaseActivity";
	public EvtPopupWindow PopupWindowInstance = new EvtPopupWindow(BaseActivity.this);
	public static final String ACTION_DEFAULT_BROAD = "TestCustomBroadCast";
	protected final IntentFilter mIntentFilter = new IntentFilter();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIntentFilter.addAction(ACTION_DEFAULT_BROAD);
		mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mGlobalReceiver, mIntentFilter);
	}

	protected BroadcastReceiver mGlobalReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action;
			String data;
			if (intent.getAction().equals(ACTION_DEFAULT_BROAD)) {
				action = intent.getStringExtra("action");
				data = intent.getStringExtra("data");
			} else {
				action = intent.getAction();
				data = intent.getStringExtra("data");
			}
			ProcessBroadReceiver(action, data);
		}
	};

	protected void ProcessBroadReceiver(String action, String data) {

	}

	/**
	 * 手动添加广播频道
	 * 
	 * @param str
	 */
	protected void addAction(String... str) {
		String action;
		for (int i = 0; i < str.length; i++) {
			action = str[i];
			mIntentFilter.addAction(action);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		//EvtLog.e(TAG, "onSaveInstanceState");
	}

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mGlobalReceiver);
	}

	/**
	 * 
	 * @param action
	 *            广播类型
	 * @param data
	 *            广播发送的数据
	 */
	public void sendBroadCastV(String action, String data) {
		Intent intent = new Intent(ACTION_DEFAULT_BROAD);
		intent.putExtra("action", action);
		intent.putExtra("data", data);
		BaseApplication.getInstance().sendBroadcast(intent);
	}
}
