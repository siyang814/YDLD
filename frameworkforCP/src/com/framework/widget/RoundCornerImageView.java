package com.framework.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Ô²½ÇµÄimageview
 * @author 
 *
 */
public class RoundCornerImageView extends ImageView {
	
	/**
	 * 
	 * @param context 
	 */
	public RoundCornerImageView(Context context) {
		super(context);
	}

	/**
	 * 
	 * @param context 
	 * @param attrs 
	 */
	public RoundCornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 
	 * @param context 
	 * @param attrs 
	 * @param defStyle 
	 */
	public RoundCornerImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// Path clipPath = new Path();
		// Paint paint = new Paint();
		// int w = this.getWidth();
		// int h = this.getHeight();
		// paint.setAntiAlias(true);
		// clipPath.addRoundRect(new RectF(0, 0, w, h), 10.5f, 10.5f,
		// Path.Direction.CW);
		// canvas.drawPath(clipPath, paint);
		super.onDraw(canvas);
	}
}
