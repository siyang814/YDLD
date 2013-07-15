package com.framework.widget;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.framework.R;
import com.framework.Exception.EvtLog;
import com.framework.Util.PackageUtil;


/**
 * 一个activity中只需要一个popupwindow show一次之后dismiss一次
 * show一次之后没有dismiss，再次dismiss会直接返回
 * 
 * @author 
 * 
 */
public class EvtPopupWindow {
	private static final String TAG = "EvtPopupWindow";
	private static final int TIMER_DELAY_FIRST_IN_MS = 200;
	private PopupWindow mPopupWindow;
	private Activity mCurrentActivity;
	private Boolean mIsShowing = false;

	/**
	 * 
	 * @param act
	 *            Activity
	 */
	public EvtPopupWindow(Activity act) {
		this.mCurrentActivity = act;
	}

	/**
	 * 显示“加载中...”
	 * 
	 */
	public void showPopup() {
		showPopup(PackageUtil.getString(R.string.downloading_data));
	}

	/**
	 * showpopupWindow
	 * 
	 * @param msg
	 *            内容
	 */
	public void showPopup(final String msg) {
		synchronized (mIsShowing) {
			if (mIsShowing) {
				return;
			}
			changeShowStatus(true);
		}

		LayoutInflater inflater = (LayoutInflater) mCurrentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.popup, null);
		if (msg != null && !msg.equals("")) {
			TextView tv = (TextView) layout.findViewById(R.id.tv_popup);
			tv.setText(msg);
		} else {
			TextView tv = (TextView) layout.findViewById(R.id.tv_popup);
			tv.setVisibility(View.GONE);
		}
		ImageView img = (ImageView) layout.findViewById(R.id.img_popup);
		Animation anim = AnimationUtils.loadAnimation(mCurrentActivity, R.anim.popup_loading);
		img.startAnimation(anim);
		mPopupWindow = new PopupWindow(layout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		showPopupTask(msg);
	}

	private void showPopupTask(final String msg) {
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				mCurrentActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						while (mPopupWindow != null && !mPopupWindow.isShowing()) {
							try {
								EvtLog.d(TAG, "try to show PopuWindow()");
								synchronized (mIsShowing) {
									if (mIsShowing) {
										EvtLog.d(TAG, "show PopupWindow");
										mPopupWindow.showAtLocation(
												mCurrentActivity.getWindow().findViewById(Window.ID_ANDROID_CONTENT),
												Gravity.CENTER, 0, 0);
										EvtLog.d(TAG, "show PopupWindow ok");
									}
								}

							} catch (Exception e) {
								EvtLog.d(TAG, "show PopuWindow failed, try again");
								showPopupTask(msg);
							}
						}
					}
				});
			}
		};
		timer.schedule(task, TIMER_DELAY_FIRST_IN_MS);
	}

	/**
	 * dismiss popupwindow
	 */
	public void dismiss() {
		EvtLog.d(TAG, "dismiss");
		synchronized (mIsShowing) {
			if (mIsShowing) {
				changeShowStatus(false);
				try {
					if (mPopupWindow != null && mPopupWindow.isShowing()) {
						mPopupWindow.dismiss();
					}
					EvtLog.d(TAG, "dismiss ok");
				} catch (Exception e) {
				} finally {
					mPopupWindow = null;
				}
			}
		}
	}

	/**
	 * 返回是否在显示popupwindow
	 * 
	 * @return
	 */
	public boolean isShowing() {
		return mIsShowing;
	}

	private void changeShowStatus(boolean bShowing) {
		this.mIsShowing = bShowing;
	}
}
