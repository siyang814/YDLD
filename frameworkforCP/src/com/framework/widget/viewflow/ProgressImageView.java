package com.framework.widget.viewflow;


import com.framework.R;
import com.framework.Exception.EvtLog;
import com.framework.Util.UIUtil;

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
 * ��ϸ�������ͼImageView
 * 
 * @author 
 * 
 */
public class ProgressImageView extends FrameLayout {
	private static final String TAG = "ProgressImageView";

	// ͼƬ�ֱ���
	private static final int IMAGE_WIDTH_IN_PX = 600;
	private static final int IMAGE_HEIGHT_IN_PX = 680;
	private static final int IMAGE_PADDING_IN_DIP = 16; // ����Ļ���ҿ������dip
	private static final int IMAGE_MAGIN_IN_DIP = 10; // ͼƬ�߿���dp
	private Context mContext;
	private int mWidthPixels;
	private int mHeightPixels;
	private ImageView mImageView;
	private ImageView mProgressBar;
	private ImageView mZoomInTag;
	private Animation mAnim;

	/**
	 * ���췽��
	 * 
	 * @param context
	 *            ������
	 * @param attrs
	 *            ����
	 */
	public ProgressImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		mImageView = new ImageView(context);
		mImageView.setVisibility(View.INVISIBLE);
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		mWidthPixels = metrics.widthPixels - UIUtil.dip2px(context, IMAGE_PADDING_IN_DIP);
		float nums = IMAGE_HEIGHT_IN_PX / IMAGE_WIDTH_IN_PX * mWidthPixels;
		mHeightPixels = Math.round(nums) + UIUtil.dip2px(context, IMAGE_PADDING_IN_DIP + 7);
		mProgressBar = new ImageView(context);
		mProgressBar.setImageResource(R.drawable.popup_loading);
		startAnim();
		this.addView(mProgressBar, new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		// ���ñ߿���
		setImageBolderWidth(IMAGE_MAGIN_IN_DIP, mImageView);
		EvtLog.d(TAG, "widthPixels:heightPixels = " + mWidthPixels + ":" + mHeightPixels);
	}

	/**
	 * ����ͼƬBitmap
	 * 
	 * @param bitmap
	 *            ����
	 */
	public void setImageBitmap(Bitmap bitmap) {
		mImageView.setImageBitmap(bitmap);
	}

	private void startAnim() {
		mAnim = AnimationUtils.loadAnimation(mContext, R.anim.popup_loading);
		mProgressBar.startAnimation(mAnim);
	}

	/**
	 * �������ʱ�����ط����,��ʾͼƬ(�������Ŵ���)
	 */
	public void displayImageView() {
		displayImageView(false);
	}

	/**
	 * �������ʱ�����ط���֣���ʾͼƬ�ͷŴ���
	 * 
	 * @param setZoomInTag
	 *            �Ƿ���Ҫ�Ŵ���
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
	 * ����Ϊ������
	 */
	public void prepareImage() {
		if (mImageView.getVisibility() == View.VISIBLE) {
			mProgressBar.setVisibility(View.VISIBLE);
			startAnim();
			mImageView.setVisibility(View.INVISIBLE);
			mZoomInTag.setVisibility(View.INVISIBLE);
			this.setBackgroundResource(0);
		}
	}

	/**
	 * 
	 * @param resId
	 *            ͼƬ������ԴID
	 */
	public void setImageResource(int resId) {
		mImageView.setImageResource(resId);
	}

	/**
	 * ����ͼƬdrawable
	 * 
	 * @param drawable
	 *            drawable����
	 */
	public void setImageDrawable(Drawable drawable) {
		mImageView.setImageDrawable(drawable);
	}

	/**
	 * ��ͼƬ���ü���
	 * 
	 * @param onClickListener
	 *            ����¼�����
	 */
	public void setOnclickedListener(OnClickListener onClickListener) {
		mImageView.setOnClickListener(onClickListener);
	}

	/**
	 * ��õ�ǰͼƬ����ʾ״̬
	 * 
	 * @return trueΪ��ʾ��falseΪ����
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


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(mWidthPixels, mHeightPixels);
	}

	/**
	 * ����ͼƬ�߿�,���Զ���dpת����px,�������addView����
	 * 
	 * @param bolderWidth
	 *            �߿���(dp)
	 * 
	 * @param mImageView
	 *            �����õ�ͼƬ
	 */
	public void setImageBolderWidth(int bolderWidth, ImageView mImageView) {
		int margin = UIUtil.dip2px(mContext, bolderWidth);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, Gravity.CENTER);
		// params.setMargins(margin, margin, margin, margin);
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
		marginLayoutParams.bottomMargin = margin - UIUtil.dip2px(mContext, 2);
		marginLayoutParams.rightMargin = margin;
		mZoomInTag.setLayoutParams(marginLayoutParams);
	}
}
