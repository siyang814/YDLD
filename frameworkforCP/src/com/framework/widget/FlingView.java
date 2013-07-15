package com.framework.widget;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Scroller;

import com.framework.R;
import com.framework.Exception.EvtLog;
import com.framework.Util.PackageUtil;


/**
 * 类似于Gallery的左右滑动控件，支持当单个屏幕超出手机高度后，可以上下滑动；基本的使用方式类似于Gallery
 * 
 * @author 
 * @version 2012-03-22,首次完成基本的效果
 * 
 */
public class FlingView extends AdapterView<Adapter> {

	private static final String TAG = "FlingView";

	private static final int SNAP_VELOCITY = 1000;
	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;

	private static final int DIRECTION_NONE = 0;
	private static final int DIRECTION_UP_DOWN = 1;
	private static final int DIRECTION_LEFT_RIGHT = 2;

	private static final int SIDE_BUFFER = 1;
	private static final float mRate = 2;
	private static float DEFAULT_X_DISTANCE = 50;
	private static float DEFAULT_Y_DISTANCE = 100;
	private static final int INVALID_SCREEN = -1;

	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mCurrentScreen;
	private int mScrollWhichScreen;

	private float mLastMotionX = -1;
	private float mLastMotionY = -1;

	private int mTouchState = TOUCH_STATE_REST;
	private int mDirection = DIRECTION_NONE;

	private LinkedList<View> mLoadedViews;
	private int mCurrentBufferIndex;
	private int mCurrentAdapterIndex;
	private int mSideBuffer = 2;

	private int mTouchSlop;
	private int mMaximumVelocity;
	private int mNextScreen = INVALID_SCREEN;
	private boolean mFirstLayout = true;
	private ViewSwitchListener mViewSwitchListener;
	private Adapter mAdapter;
	private int mLastScrollDirection;
	private AdapterDataSetObserver mDataSetObserver;
	private OnItemSelectedListener mOnItemSelectedListener;
	private OnItemSelectedListener mOnItemPreSelectedListener;

	/**
	 * Receives call backs when a new {@link View} has been scrolled to.
	 */
	public static interface ViewSwitchListener {

		/**
		 * This method is called when a new View has been scrolled to.
		 * 
		 * @param view
		 *            the {@link View} currently in focus.
		 * @param position
		 *            The position in the adapter of the {@link View} currently
		 *            in focus.
		 */
		void onSwitched(View view, int position);
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            当前控件的上下文
	 */
	public FlingView(Context context) {
		super(context);
		mSideBuffer = SIDE_BUFFER;
		init();

	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            当前控件的上下文
	 * @param sideBuffer
	 *            默认的缓存页面数目
	 */
	public FlingView(Context context, int sideBuffer) {
		super(context);
		mSideBuffer = sideBuffer;
		init();
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 *            当前控件的上下文
	 * @param attrs
	 *            使用布局页面时的属性
	 */
	public FlingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.fling_view);
		mSideBuffer = styledAttrs.getInt(R.styleable.fling_view_sidebuffer, SIDE_BUFFER);
		init();
	}

	private void init() {
		mLoadedViews = new LinkedList<View>();
		mScroller = new Scroller(getContext());
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
		DEFAULT_X_DISTANCE = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		DEFAULT_Y_DISTANCE = DEFAULT_X_DISTANCE * mRate;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(PackageUtil.getString(R.string.must_be_exactly));
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(PackageUtil.getString(R.string.must_be_exactly));
		}

		// The children are given the same width and height as the workspace
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}

		if (mFirstLayout) {
			scrollTo(mCurrentScreen * width, 0);
			mFirstLayout = false;
		}

		Log.d(TAG, "onMeasure");
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childLeft = 0;

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (getChildCount() == 0) {
			return false;
		}

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}

		boolean result = false;
		final float x = ev.getX();
		final float y = ev.getY();
		switch (action) {
			case MotionEvent.ACTION_MOVE:
				final int xDiff = (int) Math.abs(x - mLastMotionX);
				final int yDiff = (int) Math.abs(y - mLastMotionY);

				int direction = getDirection(xDiff, yDiff);
				if (direction != DIRECTION_NONE && mDirection == DIRECTION_NONE) {
					mDirection = direction;
					Log.d(TAG, "onInterceptTouchEvent ACTION_MOVE set mDirection " + mDirection);
				}

				result = mDirection == DIRECTION_LEFT_RIGHT;
				if (result) {
					mTouchState = TOUCH_STATE_SCROLLING;
				}
				break;
			case MotionEvent.ACTION_DOWN:
				mLastMotionX = mLastMotionX == -1 ? x : mLastMotionX;
				mLastMotionY = mLastMotionY == -1 ? y : mLastMotionY;
				mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			default:
				reset();
				break;
		}

		return result && mTouchState != TOUCH_STATE_REST;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (getChildCount() == 0) {
			return false;
		}

		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();

		switch (action) {
			case MotionEvent.ACTION_DOWN:
				/*
				 * 如果屏幕正在滑动时，用户点击控件，此时停止动画效果
				 */
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
				}

				// Remember where the motion event started
				mLastMotionX = x;
				mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
				break;
			case MotionEvent.ACTION_MOVE:
				final int xDiff = (int) Math.abs(x - mLastMotionX);
				boolean xMoved = xDiff > mTouchSlop;
				if (xMoved) {
					// 当用户左右滑动超出手机设置的距离后，开始滑动控件
					mTouchState = TOUCH_STATE_SCROLLING;
				}

				if (mTouchState == TOUCH_STATE_SCROLLING) {
					// Scroll to follow the motion event
					final int deltaX = (int) (mLastMotionX - x);
					mLastMotionX = x;

					final int scrollX = getScrollX();
					if (deltaX < 0) {
						if (scrollX > 0) {
							scrollBy(Math.max(-scrollX, deltaX), 0);
						}
					} else if (deltaX > 0) {
						final int availableToScroll = getChildAt(getChildCount() - 1).getRight() - scrollX - getWidth();
						if (availableToScroll > 0) {
							scrollBy(Math.min(availableToScroll, deltaX), 0);
						}
					}
					return true;
				}
				break;

			case MotionEvent.ACTION_UP:
				if (mTouchState == TOUCH_STATE_SCROLLING) {
					final VelocityTracker velocityTracker = mVelocityTracker;
					velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
					int velocityX = (int) velocityTracker.getXVelocity();
					if (velocityX > SNAP_VELOCITY && mCurrentScreen > 0) {
						// 向左滑动
						snapToScreen(mCurrentScreen - 1);
					} else if (velocityX < -SNAP_VELOCITY && mCurrentScreen < getChildCount() - 1) {
						// 向右滑动
						snapToScreen(mCurrentScreen + 1);
					} else {
						snapToDestination();
					}

					if (mVelocityTracker != null) {
						mVelocityTracker.recycle();
						mVelocityTracker = null;
					}
				}

				mTouchState = TOUCH_STATE_REST;
				reset();
				break;
			case MotionEvent.ACTION_CANCEL:
				mTouchState = TOUCH_STATE_REST;
				reset();
			default:
				break;
		}

		return true;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		if (getWidth() == 0) {
			return;
		}
		final int screenWidth = getWidth();
		final int scrollX = getScrollX();
		final int scrollY = getScrollY();

		final int whichScreen = (scrollX + (screenWidth / 2)) / screenWidth;
		int selection = this.getSelectedItemPosition();

		// 用户滑动画面中间时，即开始切换当前画面，未完成
		Log.d(TAG, "onScrollChanged: currentScreen:" + mCurrentScreen + ", screenWidth:" + screenWidth
				+ " , whichScreen:" + whichScreen + ", scrollX:" + scrollX + ", scrollY:" + scrollY);
		Log.d(TAG, "onScrollChanged: left:" + l + ", top:" + t + ", oldLeft:" + oldl + ", oldTop:" + oldt);

		if (l > oldl) {
			// 向右滑动
			if (whichScreen > mCurrentScreen) {
				OnItemPreSelected(selection + 1);
				mScrollWhichScreen = whichScreen;
			} else if (whichScreen > mScrollWhichScreen) {
				OnItemPreSelected(selection);
			}
		} else {
			// 向左滑动
			if (whichScreen < mCurrentScreen) {
				OnItemPreSelected(selection - 1);
				mScrollWhichScreen = whichScreen;
			} else if (whichScreen < mScrollWhichScreen) {
				OnItemPreSelected(selection);
			}
		}
	};

	private void OnItemPreSelected(int selection) {
		if (mOnItemPreSelectedListener == null)
			return;

		if (selection >= 0) {
			View v = getSelectedView();
			mOnItemPreSelectedListener.onItemSelected(this, v, selection, getAdapter().getItemId(selection));
		} else {
			mOnItemPreSelectedListener.onNothingSelected(this);
		}
	}

	/**
	 * 获取控件的移动方向
	 * 
	 * @param absX
	 *            x轴移动的绝对值
	 * @param absY
	 *            y轴移动的绝对值
	 * @return 返回控件的移动方向
	 */
	private int getDirection(float absX, float absY) {
		if ((absX >= DEFAULT_X_DISTANCE && absY < DEFAULT_Y_DISTANCE)
				|| (absY >= DEFAULT_Y_DISTANCE && absX > DEFAULT_X_DISTANCE && (absY / absX < mRate))) {
			return DIRECTION_LEFT_RIGHT;
		} else if ((absX < DEFAULT_X_DISTANCE && absY >= DEFAULT_Y_DISTANCE)
				|| (absX > DEFAULT_X_DISTANCE && absY >= DEFAULT_Y_DISTANCE && (absY / absX >= mRate))) {
			return DIRECTION_UP_DOWN;
		} else {
			return DIRECTION_NONE;
		}
	}

	/**
	 * 重置滑动的状态、方向、x和y轴的坐标值
	 */
	private void reset() {
		mTouchState = TOUCH_STATE_REST;
		mDirection = DIRECTION_NONE;
		mLastMotionX = -1;
		mLastMotionY = -1;
		Log.d(TAG, "onInterceptTouchEvent ACTION_UP reset");
	}

	private void snapToDestination() {
		final int screenWidth = getWidth();
		final int whichScreen = (getScrollX() + (screenWidth / 2)) / screenWidth;

		snapToScreen(whichScreen);
	}

	/**
	 * 滑动到指定的屏幕
	 * 
	 * @param whichScreen
	 *            指定的子屏幕
	 */
	private void snapToScreen(int whichScreen) {
		mLastScrollDirection = whichScreen - mCurrentScreen;
		if (!mScroller.isFinished()) {
			return;
		}

		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));

		mNextScreen = whichScreen;

		final int newX = whichScreen * getWidth();
		final int delta = newX - getScrollX();
		mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
		invalidate();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		} else if (mNextScreen != INVALID_SCREEN) {
			mCurrentScreen = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));
			mNextScreen = INVALID_SCREEN;
			postViewSwitched(mLastScrollDirection);
		}
		// EvtLog.d(TAG, "mCurrentAdapterIndex:" + mCurrentAdapterIndex +
		// ", mCurrentBufferIndex:" + mCurrentBufferIndex
		// + ", mCurrentScreen:" + mCurrentScreen);
	}

	/**
	 * Scroll to the {@link View} in the view buffer specified by the index.
	 * 
	 * @param indexInBuffer
	 *            Index of the view in the view buffer.
	 */
	private void setVisibleView(int indexInBuffer, boolean uiThread) {
		mCurrentScreen = Math.max(0, Math.min(indexInBuffer, getChildCount() - 1));
		mScrollWhichScreen = mCurrentScreen;
		int dx = (mCurrentScreen * getWidth()) - mScroller.getCurrX();
		mScroller.startScroll(mScroller.getCurrX(), mScroller.getCurrY(), dx, 0, 0);
		if (uiThread) {
			invalidate();
		} else {
			postInvalidate();
		}
	}

	/**
	 * Set the listener that will receive notifications every time the {code
	 * ViewFlow} scrolls.
	 * 
	 * @param l
	 *            the scroll listener
	 */
	public void setOnViewSwitchListener(ViewSwitchListener l) {
		mViewSwitchListener = l;
	}

	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		setAdapter(adapter, 0);
	}

	/**
	 * 同时设置Adapter和position信息
	 * 
	 * @param adapter
	 *            适配列表信息
	 * @param position
	 *            起始位置
	 */
	public void setAdapter(Adapter adapter, int position) {
		if (adapter == null || position < 0 || (position > 0 && position >= adapter.getCount())) {
			return;
		}

		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}
		mAdapter = adapter;
		mDataSetObserver = new AdapterDataSetObserver();
		mAdapter.registerDataSetObserver(mDataSetObserver);

		if (mAdapter.getCount() == 0) {
			return;
		}

		ArrayList<View> recycleViews = new ArrayList<View>();
		View recycleView;
		while (!mLoadedViews.isEmpty()) {
			recycleView = mLoadedViews.remove();
			recycleViews.add(recycleView);
			detachViewFromParent(recycleView);
		}

		int beginIndex = Math.max(0, position - mSideBuffer);
		for (int i = beginIndex; i < Math.min(mAdapter.getCount(), position + mSideBuffer + 1); i++) {
			mLoadedViews.addLast(makeAndAddView(i, true, recycleViews.isEmpty() ? null : recycleViews.remove(0)));
			if (i == position) {
				mCurrentBufferIndex = mLoadedViews.size() - 1;
			}
		}
		mCurrentAdapterIndex = position;

		for (View view : recycleViews) {
			removeDetachedView(view, false);
		}
		requestLayout();
		setVisibleView(mCurrentBufferIndex, false);

		computeScroll();

		fireOnSelected();

		if (mViewSwitchListener != null) {
			mViewSwitchListener.onSwitched(mLoadedViews.get(mCurrentBufferIndex), mCurrentAdapterIndex);
		}

		EvtLog.d(TAG, "setAdapter:");
	}

	@Override
	public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
		super.setOnItemSelectedListener(listener);

		this.mOnItemSelectedListener = getOnItemSelectedListener();
		// if(this.getAdapter() != null){
		// fireOnSelected();
		// }
	};

	public void setOnItemPreSelectedListener(AdapterView.OnItemSelectedListener listener) {
		this.mOnItemPreSelectedListener = listener;
	}

	@Override
	public View getSelectedView() {
		// return mCurrentAdapterIndex < mLoadedViews.size() ?
		// mLoadedViews.get(mCurrentBufferIndex) : null;
		return mLoadedViews.size() > 0 && mCurrentBufferIndex < mLoadedViews.size() ? mLoadedViews
				.get(mCurrentBufferIndex) : null;
	}

	@Override
	public void setSelection(int position) {
		if (mAdapter == null || position >= mAdapter.getCount()) {
			return;
		}

		ArrayList<View> recycleViews = new ArrayList<View>();
		View recycleView;
		while (!mLoadedViews.isEmpty()) {
			recycleViews.add(recycleView = mLoadedViews.remove());
			detachViewFromParent(recycleView);
		}

		int beginIndex = Math.max(0, position - mSideBuffer);
		for (int i = beginIndex; i < Math.min(mAdapter.getCount(), position + mSideBuffer + 1); i++) {
			mLoadedViews.addLast(makeAndAddView(i, true, recycleViews.isEmpty() ? null : recycleViews.remove(0)));
			if (i == position) {
				mCurrentBufferIndex = mLoadedViews.size() - 1;
			}
		}
		mCurrentAdapterIndex = position;

		for (View view : recycleViews) {
			removeDetachedView(view, false);
		}
		requestLayout();
		setVisibleView(mCurrentBufferIndex, false);

		computeScroll();

		fireOnSelected();

		if (mViewSwitchListener != null) {
			mViewSwitchListener.onSwitched(mLoadedViews.get(mCurrentBufferIndex), mCurrentAdapterIndex);
		}

		EvtLog.d(TAG, "setSelection:");
	}

	@Override
	public int getSelectedItemPosition() {
		return mCurrentAdapterIndex;
	};

	/**
	 * 向后翻页
	 */
	public void scrollToNext() {
		if (mCurrentScreen < getChildCount() - 1) {
			snapToScreen(mCurrentScreen + 1);
		} else {
			snapToDestination();
		}
	}

	/**
	 * 向前翻页
	 */
	public void scrollToPrevious() {
		if (mCurrentScreen > 0) {
			snapToScreen(mCurrentScreen - 1);
		} else {
			snapToDestination();
		}
	}

	private void resetFocus() {
		logBuffer();
		mLoadedViews.clear();
		removeAllViewsInLayout();

		for (int i = Math.max(0, mCurrentAdapterIndex - mSideBuffer); i < Math.min(mAdapter.getCount(),
				mCurrentAdapterIndex + mSideBuffer + 1); i++) {
			mLoadedViews.addLast(makeAndAddView(i, true, null));
			if (i == mCurrentAdapterIndex) {
				mCurrentBufferIndex = mLoadedViews.size() - 1;
			}
		}
		logBuffer();
		requestLayout();
	}

	private void postViewSwitched(int direction) {
		if (direction == 0) {
			return;
		}

		if (direction > 0) { // to the right
			mCurrentAdapterIndex++;
			mCurrentBufferIndex++;

			View recycleView = null;
			// Remove view outside buffer range
			if (mCurrentAdapterIndex > mSideBuffer) {
				recycleView = mLoadedViews.removeFirst();
				detachViewFromParent(recycleView);
				// removeView(recycleView);
				mCurrentBufferIndex--;
			}

			// Add new view to buffer
			int newBufferIndex = mCurrentAdapterIndex + mSideBuffer;
			if (newBufferIndex < mAdapter.getCount())
				mLoadedViews.addLast(makeAndAddView(newBufferIndex, true, recycleView));

		} else { // to the left
			mCurrentAdapterIndex--;
			mCurrentBufferIndex--;
			View recycleView = null;

			// Remove view outside buffer range
			if (mAdapter.getCount() - 1 - mCurrentAdapterIndex > mSideBuffer) {
				recycleView = mLoadedViews.removeLast();
				detachViewFromParent(recycleView);
			}

			// Add new view to buffer
			int newBufferIndex = mCurrentAdapterIndex - mSideBuffer;
			if (newBufferIndex > -1) {
				mLoadedViews.addFirst(makeAndAddView(newBufferIndex, false, recycleView));
				mCurrentBufferIndex++;
			}
		}

		requestLayout();
		setVisibleView(mCurrentBufferIndex, true);

		if (mViewSwitchListener != null) {
			mViewSwitchListener.onSwitched(mLoadedViews.get(mCurrentBufferIndex), mCurrentAdapterIndex);
		}
		fireOnSelected();
		logBuffer();
	}

	/**
	 * 执行item的选择时的通知事件
	 */
	private void fireOnSelected() {
		if (mOnItemSelectedListener != null) {

			int selection = this.getSelectedItemPosition();
			if (selection >= 0) {
				View v = getSelectedView();
				mOnItemSelectedListener.onItemSelected(this, v, selection, getAdapter().getItemId(selection));
			} else {
				mOnItemSelectedListener.onNothingSelected(this);
			}
		}
		if (mOnItemPreSelectedListener != null) {
			int selection = this.getSelectedItemPosition();
			if (selection >= 0) {
				View v = getSelectedView();
				mOnItemPreSelectedListener.onItemSelected(this, v, selection, getAdapter().getItemId(selection));
			} else {
				mOnItemPreSelectedListener.onNothingSelected(this);
			}
		}
	}

	private View setupChild(View child, boolean addToEnd, boolean recycle) {
		ViewGroup.LayoutParams p = (ViewGroup.LayoutParams) child.getLayoutParams();
		if (p == null) {
			p = new AbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
		}
		if (recycle) {
			attachViewToParent(child, addToEnd ? -1 : 0, p);
		} else {
			addViewInLayout(child, addToEnd ? -1 : 0, p, true);
		}

		return child;
	}

	private View makeAndAddView(int position, boolean addToEnd, View convertView) {
		View view = mAdapter.getView(position, convertView, this);
		return setupChild(view, addToEnd, convertView != null);
	}

	class AdapterDataSetObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			View v = getChildAt(mCurrentBufferIndex);
			if (v != null) {
				for (int index = 0; index < mAdapter.getCount(); index++) {
					if (v.equals(mAdapter.getItem(index))) {
						mCurrentAdapterIndex = index;
						break;
					}
				}
			}
			resetFocus();
		}

		@Override
		public void onInvalidated() {
			// 暂时未实现
		}
	}

	/**
	 * 打印相关日志
	 */
	private void logBuffer() {
		Log.d(TAG,
				"Size of mLoadedViews: " + mLoadedViews.size() + "X: " + mScroller.getCurrX() + ", Y: "
						+ mScroller.getCurrY());
		Log.d(TAG, "IndexInAdapter: " + mCurrentAdapterIndex + ", IndexInBuffer: " + mCurrentBufferIndex);
	}

	/**
	 * 返回当前是否在滚动
	 * 
	 * @return 是否在滚动
	 */
	public boolean isScrolling() {
		return !mScroller.isFinished();
	}
}
