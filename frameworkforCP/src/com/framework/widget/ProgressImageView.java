package com.framework.widget;



import com.framework.R;
import com.framework.Exception.EvtLog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * 详细界面加载图ImageView
 * 
 * @author 
 * 
 */
public class ProgressImageView extends FrameLayout {
	private static final String TAG = "ProgressImageView";

	// 图片分辨率
	private static final int IMAGE_WIDTH_IN_PX = 444;
	private static final int IMAGE_HEIGHT_IN_PX = 482;
	private static final int IMAGE_PADDING_IN_DIP = 25; // 离屏幕左右空余距离dip
	private static final int IMAGE_MAGIN_IN_DIP = 0; // 图片边框宽度dp
	private Context mContext;
	private int mWidthPixels;
	private int mHeightPixels;
	private ImageView mImageView;
	private ImageView mProgressBar;
	private ImageView mZoomInTag;
	private Animation mAnim;

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            上下文
	 * @param attrs
	 *            属性
	 */

	public ProgressImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		mImageView = new ImageView(context);
		mImageView.setVisibility(View.INVISIBLE);
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		mWidthPixels = metrics.widthPixels - dip2px(context, IMAGE_PADDING_IN_DIP);
		float nums = IMAGE_HEIGHT_IN_PX / IMAGE_WIDTH_IN_PX * mWidthPixels;
		mHeightPixels = Math.round(nums) + dip2px(context, IMAGE_PADDING_IN_DIP);
		mProgressBar = new ImageView(mContext);
		startAnim();
		this.addView(mProgressBar, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		// 设置边框宽度
		setImageBolderWidth(IMAGE_MAGIN_IN_DIP, mImageView);
		EvtLog.d(TAG, "widthPixels:heightPixels = " + mWidthPixels + ":" + mHeightPixels);
	}

	/**
	 * 播放动画popupwindow.
	 * 
	 */
	public void startAnim() {
		mImageView.setVisibility(View.INVISIBLE);
		mProgressBar.setImageResource(R.drawable.popup_loading);
		mAnim = AnimationUtils.loadAnimation(mContext, R.anim.popup_loading);
		mProgressBar.startAnimation(mAnim);
	}

	/**
	 * 设置图片Bitmap
	 * 
	 * @param bitmap
	 *            对象
	 */
	public void setImageBitmap(Bitmap bitmap) {
		mImageView.setImageBitmap(bitmap);
	}

	/**
	 * 加载完成时，隐藏风火轮,显示图片(不包含放大标记)
	 */
	public void displayImageView() {
		displayImageView(false);
	}

	/**
	 * 加载完成时，隐藏风火轮，显示图片和放大标记
	 * 
	 * @param setZoomInTag
	 *            是否需要放大标记
	 */
	public void displayImageView(boolean setZoomInTag) {
		if (mImageView.getVisibility() == View.INVISIBLE) {
			mProgressBar.clearAnimation();
			mProgressBar.setVisibility(View.INVISIBLE);
			mImageView.setVisibility(View.VISIBLE);
			if (setZoomInTag) {
				mZoomInTag.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 重置为加载中
	 */
	public void prepareImage() {
		if (mImageView.getVisibility() == View.VISIBLE) {
			mProgressBar.setVisibility(View.VISIBLE);
			mImageView.setVisibility(View.INVISIBLE);
			mZoomInTag.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 
	 * @param resId
	 *            图片引用资源ID
	 */
	public void setImageResource(int resId) {
		mImageView.setImageResource(resId);
	}

	/**
	 * 设置图片drawable
	 * 
	 * @param drawable
	 *            drawable对象
	 */
	public void setImageDrawable(Drawable drawable) {
		mImageView.setImageDrawable(drawable);
	}

	/**
	 * 给图片设置监听
	 * 
	 * @param onClickListener
	 *            点击事件监听
	 */
	public void setOnclickedListener(OnClickListener onClickListener) {
		mImageView.setOnClickListener(onClickListener);
	}

	/**
	 * 获得当前图片的显示状态
	 * 
	 * @return true为显示，false为隐藏
	 */
	public boolean isVisible() {
		return mImageView.getVisibility() == VISIBLE ? true : false;
	}

	/**
	 * 
	 * @param onClickListener
	 */
	public void removeOnClickedListener() {
		mImageView.setOnClickListener(null);
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

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(mWidthPixels, mHeightPixels);
	}

	/**
	 * 设置图片边框,会自动将dp转换成px,并会调用addView方法
	 * 
	 * @param bolderWidth
	 *            边框宽度(dp)
	 * 
	 * @param mImageView
	 *            需设置的图片
	 */
	public void setImageBolderWidth(int bolderWidth, ImageView mImageView) {
		int margin = dip2px(mContext, bolderWidth);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, Gravity.CENTER);
		params.setMargins(margin, margin, margin, margin);
		this.addView(mImageView, params);
		mZoomInTag = new ImageView(mContext);
		mZoomInTag.setVisibility(View.INVISIBLE);
		mZoomInTag.setFocusable(false);
		mZoomInTag.setFocusableInTouchMode(false);
		mZoomInTag.setClickable(false);
		mZoomInTag.setImageResource(R.drawable.zoom_in);
		this.addView(mZoomInTag, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.RIGHT
				+ Gravity.BOTTOM));
		MarginLayoutParams marginLayoutParams = (MarginLayoutParams) mZoomInTag.getLayoutParams();
		marginLayoutParams.bottomMargin = margin - dip2px(mContext, 2);
		marginLayoutParams.rightMargin = margin;
		mZoomInTag.setLayoutParams(marginLayoutParams);
	}
}
