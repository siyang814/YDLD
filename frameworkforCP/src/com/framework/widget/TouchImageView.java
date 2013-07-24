package com.framework.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.widget.ImageView;

public class TouchImageView extends ImageView implements OnTouchListener {

	Matrix matrix = new Matrix();
	Matrix savedMatrix = new Matrix();
	Matrix prevMoveMatrix = new Matrix();
	DisplayMetrics dm;
	Bitmap bitmap;

	float minScaleR;// 最小缩放比例
	static final float MAX_SCALE = 3f;// 最大缩放比例

	static final int NONE = 0;// 初始状态
	static final int DRAG = 1;// 拖动
	static final int ZOOM = 2;// 缩放
	int mode = NONE;

	PointF prev = new PointF();
	PointF prevMov = new PointF();
	PointF mid = new PointF();
	float dist = 1f;
	float scale = 1f;
	int touchSlop, doubleTapSlop;
	int mMinimumFlingVelocity, mMaximumFlingVelocity;

	public TouchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (getDrawable() != null) {
			bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		}
		setScaleType(ScaleType.MATRIX);

		setOnTouchListener(this);// 设置触屏监听
		dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);// 获取分辨率

		if (bitmap != null) {
			Log.d("test", "TouchImageView(), call center()");
			minZoom();
			center();
		}
		setImageMatrix(matrix);

		final ViewConfiguration configuration = ViewConfiguration.get(context);
		touchSlop = configuration.getScaledTouchSlop();
		doubleTapSlop = configuration.getScaledDoubleTapSlop();
		mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
		mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();

		Log.d("test", "touchSlop: " + touchSlop + ", doubleTapSlop: " + doubleTapSlop + ", mMinimumFlingVelocity: "
				+ mMinimumFlingVelocity + ", mMaximumFlingVelocity: " + mMaximumFlingVelocity);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		if (bm == null) {
			Log.d("test", "setImageBitmap(), bmp == null");
			return;
		}

		bitmap = ((BitmapDrawable) getDrawable()).getBitmap();

		// matrix.postTranslate(0, getCenterDeltaY());
		// setImageMatrix(matrix);

		minZoom();
		moveToCenterY();
		// center();
		// CheckView();
		fixScale();
		Log.d("test", "setImageBitmap(), call fixScale()");
	}

	boolean isSet = false;

	@Override
	protected void onDraw(Canvas canvas) {
		float deltaY = getCenterDeltaY();
		Log.d("test", "onDraw, deltaY: " + deltaY + ", " + (bitmap != null) + ", " + (isSet));

		// if (!isCentering && bitmap != null && !isSet) {
		if (bitmap != null && !isSet) {
			moveToCenterY();
			isSet = true;
			Log.d("test", "onDraw, postTranslate");
		}

		super.onDraw(canvas);
	}

	private void moveToCenterY() {
		float deltaY = getCenterDeltaY();
		Log.d("test", "moveToCenterY, deltaY: " + deltaY + ", " + (bitmap != null));

		if (bitmap != null) {
			matrix.postTranslate(0, deltaY);
			setImageMatrix(matrix);
			Log.d("test", "moveToCenterY, postTranslate");
		}
	}

	/**
	 * 触屏监听
	 */
	public boolean onTouch(View v, MotionEvent event) {
		if (getDrawable() != null) {
			bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
			if (bitmap == null) {
				return true;
			}
		} else {
			return true;
		}

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			// 主点按下
			case MotionEvent.ACTION_DOWN:
				savedMatrix.set(matrix);
				prevMoveMatrix.set(matrix);
				prev.set(event.getX(), event.getY());
				prevMov.set(event.getX(), event.getY());
				mode = DRAG;
				break;
			// 副点按下
			case MotionEvent.ACTION_POINTER_DOWN:
				dist = spacing(event);
				// 如果连续两点距离大于10，则判定为多点模式
				if (spacing(event) > 10f) {
					savedMatrix.set(matrix);
					float[] vals = new float[9];
					savedMatrix.getValues(vals);
					scale = vals[Matrix.MSCALE_X];
					midPoint(mid, event);
					mode = ZOOM;
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				mode = NONE;
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == DRAG) {
					// Log.d("test", isDragLeft(event) + ", " +
					// isDragRight(event) + ", " + isDragUp(event) + ", "
					// + isDragDown(event));
					// matrix.set(savedMatrix);
					// matrix.postTranslate(event.getX() - prev.x, event.getY()
					// - prev.y);
					// ////////////////////////////////////////////////////////////

					if (isBitmapBoundOutOfImageViewBound()) {
						float xOffset = event.getX() - prevMov.x;
						float yOffset = event.getY() - prevMov.y;
						if ((isDragLeft(event) && getBitmapRight() + getLeft() < getRight())
								|| (isDragRight(event) && getBitmapLeft() + getLeft() > getLeft())) {
							xOffset = 0;
						}
						if ((isDragUp(event) && getBitmapBottom() + getTop() < getBottom())
								|| (isDragDown(event) && getBitmapTop() + getTop() > getTop())) {
							yOffset = 0;
						}
						matrix.set(prevMoveMatrix);
						matrix.postTranslate(xOffset, yOffset);
						prevMoveMatrix.set(matrix);
						prevMov.set(prevMov.x + xOffset, prevMov.y + yOffset);
					} else {
						matrix.set(savedMatrix);
						matrix.postTranslate(event.getX() - prev.x, event.getY() - prev.y);
					}
				} else if (mode == ZOOM) {
					float newDist = spacing(event);
					float tScale = newDist / dist;
					if (newDist > 10f) {
						if ((getScaleX() * tScale) < minScaleR) {
							break;
						}
						if ((getScaleX() * tScale) > MAX_SCALE) {
							break;
						}

						matrix.set(savedMatrix);
						matrix.postScale(tScale, tScale, mid.x, mid.y);
						float[] p = new float[9];
						getImageMatrix().getValues(p);
						Log.d("test", "minScale: " + minScaleR + ", getScale: " + getScaleX() + ", tscale: " + tScale
								+ ", scale: " + scale + ", *: " + (tScale * scale) + ", v: " + p[Matrix.MSCALE_Y]
								+ ",  " + ((getBitmapRight() - getBitmapLeft()) / bitmap.getWidth()));
					}
				}
				break;
		}
		setImageMatrix(matrix);
		if (event.getAction() == MotionEvent.ACTION_UP) {
			Log.d("test", "onTouch(), call center()");
			CheckView();
		}
		return true;
	}
	
	@Override
	public float getScaleX()
	{
		// TODO Auto-generated method stub
		float[] p = new float[9];
		getImageMatrix().getValues(p);
		return p[Matrix.MSCALE_X];
	}
	
//	@Override
//	private float getScaleX() {
//		float[] p = new float[9];
//		getImageMatrix().getValues(p);
//		return p[Matrix.MSCALE_X];
//	}

	private boolean isBitmapBoundOutOfImageViewBound() {
		boolean isOut = false;
		if ((getBitmapRight() - getBitmapLeft() > getWidth()) && (getBitmapBottom() - getBitmapTop() > getHeight())) {
			isOut = true;
		} else {
			isOut = false;
		}
		return isOut;
	}

	private boolean isDragLeft(MotionEvent event) {
		return event.getX() < prev.x;
	}

	private boolean isDragRight(MotionEvent event) {
		return event.getX() > prev.x;
	}

	private boolean isDragUp(MotionEvent event) {
		return event.getY() < prev.y;
	}

	private boolean isDragDown(MotionEvent event) {
		return event.getY() > prev.y;
	}

	/**
	 * 限制最大最小缩放比例，自动居中
	 */
	private void CheckView() {
		float p[] = new float[9];
		matrix.getValues(p);

		if (getScaleX() < minScaleR) {

			matrix.setScale(minScaleR, minScaleR);

			float deltay = getCenterDeltaY();
			Log.d("test", "CheckView, postTranslate, deltay: " + deltay);
			matrix.postTranslate(0, deltay);
		}
		if (p[Matrix.MSCALE_X] > MAX_SCALE) {
			matrix.set(savedMatrix);
		}
		setImageMatrix(matrix);
		center();
		checkBound();
	}

	private void fixScale() {
		float p[] = new float[9];
		matrix.getValues(p);

		if (getScaleX() < minScaleR) {

			matrix.setScale(minScaleR, minScaleR);

			float deltay = getCenterDeltaY();
			Log.d("test", "CheckView, postTranslate, deltay: " + deltay);
			matrix.postTranslate(0, deltay);
		}
		if (p[Matrix.MSCALE_X] > MAX_SCALE) {
			matrix.set(savedMatrix);
		}
		setImageMatrix(matrix);
		// center();
		// checkBound();
	}

	/**
	 * 最小缩放比例，最大为100%
	 */
	private void minZoom() {
		if (getDrawable() != null) {
			bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
			if (bitmap == null) {
				return;
			}
		} else {
			return;
		}
		minScaleR = Math.min((float) dm.widthPixels / (float) bitmap.getWidth(), (float) dm.heightPixels
				/ (float) bitmap.getHeight());
		if (minScaleR < 1.0) {
			matrix.postScale(minScaleR, minScaleR);
		}
		Log.d("test", "minZoom, minScaleR: " + minScaleR);
	}

	float[] vals = new float[9];

	private void checkBound() {
		float left = getBitmapLeft();
		float right = getBitmapRight();
		float top = getBitmapTop();
		float bottom = getBitmapBottom();
		Log.d("test", left + ", " + top + ",   " + right + ", " + bottom + "             " + getLeft() + ", "
				+ getTop() + ", " + getRight() + ", " + getBottom());
	}

	private float getBitmapLeft() {
		matrix.getValues(vals);
		return vals[Matrix.MTRANS_X];
	}

	private float getBitmapRight() {
		matrix.getValues(vals);
		float left = vals[Matrix.MTRANS_X];
		return left + bitmap.getWidth() * vals[Matrix.MSCALE_X];
	}

	private float getBitmapTop() {
		matrix.getValues(vals);
		return vals[Matrix.MTRANS_Y];
	}

	private float getBitmapBottom() {
		float top = vals[Matrix.MTRANS_Y];
		return top + bitmap.getHeight() * vals[Matrix.MSCALE_Y];
	}

	private void center() {
		center(true, true);
	}

	/**
	 * 横向、纵向居中
	 */
	// protected void center(final boolean horizontal, final boolean vertical) {
	// if (getDrawable() != null) {
	// bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
	// } else {
	// return;
	// }
	//
	// Matrix m = new Matrix();
	// m.set(matrix);
	// RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
	// m.mapRect(rect);
	//
	// float height = rect.height();
	// float width = rect.width();
	//
	// float deltaX = 0, deltaY = 0;
	//
	// // if (vertical) {
	// // // 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
	// // int screenHeight = dm.heightPixels;
	// // if (height < screenHeight) {
	// // deltaY = (screenHeight - height) / 2 - rect.top;
	// // } else if (rect.top > 0) {
	// // deltaY = -rect.top;
	// // } else if (rect.bottom < screenHeight) {
	// // deltaY = imgView.getHeight() - rect.bottom;
	// // }
	// // }
	// // if (horizontal) {
	// // int screenWidth = dm.widthPixels;
	// // if (width < screenWidth) {
	// // deltaX = (screenWidth - width) / 2 - rect.left;
	// // } else if (rect.left > 0) {
	// // deltaX = -rect.left;
	// // } else if (rect.right < screenWidth) {
	// // deltaX = screenWidth - rect.right;
	// // }
	// // }
	//
	// if (vertical) {
	// // 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
	// int imgHeight = getHeight();
	// if (imgHeight == 0) {
	// postDelayed(new Runnable() {
	//
	// @Override
	// public void run() {
	// center(horizontal, vertical);
	// }
	// }, 30);
	// return;
	// }
	// if (height < imgHeight) {
	// deltaY = (imgHeight - height) / 2 - rect.top;
	// } else if (rect.top > 0) {
	// deltaY = -rect.top;
	// } else if (rect.bottom < imgHeight) {
	// deltaY = getHeight() - rect.bottom;
	// }
	// }
	// if (horizontal) {
	// int screenWidth = getWidth();
	// if (width < screenWidth) {
	// deltaX = (screenWidth - width) / 2 - rect.left;
	// } else if (rect.left > 0) {
	// deltaX = -rect.left;
	// } else if (rect.right < screenWidth) {
	// deltaX = screenWidth - rect.right;
	// }
	// }
	//
	// // Log.d("test", "center, bmpRect: " + rect.toString() + "    dx: " +
	// deltaX + ", dy: " + deltaY + ", sw: "
	// // + dm.widthPixels + ", sh: " + dm.heightPixels + ", imgw: " +
	// getWidth() + ", imgh: " + getHeight());
	// // Log.d("test", "center, bmp: " + getBitmapLeft() + ", " +
	// getBitmapTop() + ", " + getBitmapRight() + ", "
	// // + getBitmapBottom());
	// // matrix.postTranslate(deltaX, deltaY);
	// postTranslateDur(deltaX, deltaY);
	// }
	protected void center(final boolean horizontal, final boolean vertical) {
		if (getDrawable() != null) {
			bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
			if (bitmap == null) {
				Log.d("test", "center(b, b), bitmap == null");
				return;
			}
		} else {
			Log.d("test", "center(b, b), getDrawable() == null");
			return;
		}

		Matrix m = new Matrix();
		m.set(matrix);
		RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
		m.mapRect(rect);

		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;

		if (vertical) {
			// 图片小于ImageView大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
			int imgHeight = getHeight();
			if (imgHeight == 0) {
				Log.d("test", "center(b, b), cannot get imageview height now");
				postDelayed(new Runnable() {
					@Override
					public void run() {
						center(horizontal, vertical);
					}
				}, 30);
				return;
			}

			if (height < imgHeight) {
				deltaY = (imgHeight - height) / 2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < imgHeight) {
				deltaY = getHeight() - rect.bottom;
			}
		}
		if (horizontal) {
			int screenWidth = getWidth();
			if (width < screenWidth) {
				deltaX = (screenWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < screenWidth) {
				deltaX = screenWidth - rect.right;
			}
		}
		postTranslateDur(deltaX, deltaY);
	}

	// protected void center(final boolean horizontal, final boolean vertical) {
	// if (getDrawable() != null) {
	// bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
	// } else {
	// return;
	// }
	//
	// Matrix m = new Matrix();
	// m.set(matrix);
	// RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
	// m.mapRect(rect);
	//
	// float height = rect.height();
	// float width = rect.width();
	//
	// float deltaX = 0, deltaY = 0;
	//
	// if (vertical) {
	// // 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
	// int screenHeight = dm.heightPixels;
	// if (height < screenHeight) {
	// deltaY = (screenHeight - height) / 2 - rect.top;
	// } else if (rect.top > 0) {
	// deltaY = -rect.top;
	// } else if (rect.bottom < screenHeight) {
	// deltaY = getHeight() - rect.bottom;
	// }
	// }
	// if (horizontal) {
	// int screenWidth = dm.widthPixels;
	// if (width < screenWidth) {
	// deltaX = (screenWidth - width) / 2 - rect.left;
	// } else if (rect.left > 0) {
	// deltaX = -rect.left;
	// } else if (rect.right < screenWidth) {
	// deltaX = screenWidth - rect.right;
	// }
	// }
	//
	//
	//
	// matrix.postTranslate(deltaX, deltaY);
	//
	// }

	// 计算当前位置到中间的偏移，y轴方向
	private float getCenterDeltaY() {
		if (bitmap == null) {
			return 0;
		}

		Matrix m = new Matrix();
		m.set(matrix);
		RectF rect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
		m.mapRect(rect);

		float height = rect.height();
		float width = rect.width();
		float deltaY = 0;
		int imgHeight = getHeight();
		if (imgHeight == 0) {
			return 0;
		}
		if (height < imgHeight) {
			deltaY = (imgHeight - height) / 2 - rect.top;
		} else if (rect.top > 0) {
			deltaY = -rect.top;
		} else if (rect.bottom < imgHeight) {
			deltaY = getHeight() - rect.bottom;
		}

		return deltaY;
	}

	int count = 10;
	int idx = 0;
	Handler h = new Handler();
	Runnable r;
	float _dx;
	float _dy;
	boolean isCentering = false;

	// 分10次，慢慢回到ImageView中间
	private void postTranslateDur(final float dx, final float dy) {
		isCentering = true;
		Log.d("test", "postTranslateDur, dx: " + dx + ", dy: " + dy);

		_dx = dx / count;
		_dy = dy / count;
		idx = 0;

		r = new Runnable() {
			@Override
			public void run() {
				if (idx < count) {
					matrix.postTranslate(_dx, _dy);
					setImageMatrix(matrix);
					idx++;
					h.postDelayed(r, 10);
				} else {
					isCentering = false;
				}
			}
		};
		h.postDelayed(r, 10);
	}

	/**
	 * 两点的距离
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	/**
	 * 两点的中点
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
}