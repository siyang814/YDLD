package com.framework.widget;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import com.framework.R;
import com.framework.Exception.EvtLog;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class AutoRefreshListView extends ListView implements OnScrollListener {

	private static final int TAP_TO_REFRESH = 1;
	private static final int PULL_TO_REFRESH = 2;
	private static final int RELEASE_TO_REFRESH = 3;
	private static final int REFRESHING = 4;

	private static final int NO_POINTER = 0;
	private static final int HAS_POINTERS = 1;

	private static final int EXTENDING_FOOTER = 1;
	private static final int EXTENDED_FOOTER = 2;

	private static final String TAG = "AutoRefreshListView";
	private static final String ATTR_IS_HEADER_VISIABLE = "isHeaderVisiable";
	private static final String ATTR_IS_FOOTER_VISIABLE = "isFooterVisiable";

	private LayoutInflater mInflater;

	private OnRefreshListener mOnHeaderRefreshListener;
	private RelativeLayout mHeaderRefreshView;
	private RelativeLayout mHeaderContentView;
	private LinearLayout mHeaderRefreshViewPrompt;
	private TextView mHeaderRefreshViewText;
	private TextView mHeaderRefreshViewTime;
	private ProgressBar mHeaderRefreshViewProgress;
	private ImageView mHeaderRefreshViewImage;
	private int mHeaderRefreshViewHeight;
	private Boolean mIsHeaderRefresh = true;

	private RotateAnimation mFlipAnimation;
	private RotateAnimation mReverseFlipAnimation;

	private OnRefreshListener mOnFooterRefreshListener;
	private RelativeLayout mFooterRefreshView;
	private RelativeLayout mFooterContentView;
	private TextView mFooterRefreshViewText;
	private ProgressBar mFooterRefreshViewProgress;
	private int mFooterRefreshViewHeight;
	private TextView mFooterView;

	private int mHeight = -1;
	private float mLastMotionY;
	private int mTouchSlot;

	// 当前是否有手指触摸屏幕，用于onDraw判断是否要触发重新计算extraFooter的高度
	private int mTouchStatus = NO_POINTER;

	/**
	 * Listener that will receive notifications every time the list scrolls.
	 */
	private OnScrollListener mOnScrollListener;

	private int mCurrentScrollState;
	// private int mRefreshState = TAP_TO_REFRESH;
	private int mRefreshState = PULL_TO_REFRESH;
	private Boolean mIsCompleted = false;

	private int mRefreshOriginalTopPadding;
	private int mRefreshOriginalBottomPadding;

	public AutoRefreshListView(Context context) {
		super(context);
		init(context, null);
	}

	public AutoRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public AutoRefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mHeaderRefreshView = (RelativeLayout) mInflater.inflate(R.layout.auto_refresh_header, this, false);
		mHeaderContentView = (RelativeLayout) mHeaderRefreshView.findViewById(R.id.header_content);
		mHeaderRefreshViewPrompt = (LinearLayout) mHeaderRefreshView.findViewById(R.id.refresh_prompt_text);
		mHeaderRefreshViewText = (TextView) mHeaderRefreshView.findViewById(R.id.pull_to_refresh_text);
		mHeaderRefreshViewTime = (TextView) mHeaderRefreshView.findViewById(R.id.pull_to_refresh_time);
		mHeaderRefreshViewProgress = (ProgressBar) mHeaderRefreshView.findViewById(R.id.pull_to_refresh_progress);
		mHeaderRefreshViewImage = (ImageView) mHeaderRefreshView.findViewById(R.id.pull_to_refresh_image);
		mHeaderRefreshView.setOnClickListener(new OnClickHeaderRefreshListener());

		mFooterRefreshView = (RelativeLayout) mInflater.inflate(R.layout.auto_refresh_footer, this, false);
		mFooterContentView = (RelativeLayout) mFooterRefreshView.findViewById(R.id.footer_content);
		mFooterRefreshViewText = (TextView) mFooterRefreshView.findViewById(R.id.pull_to_refresh_text);
		mFooterRefreshViewProgress = (ProgressBar) mFooterRefreshView.findViewById(R.id.pull_to_refresh_progress);
		mFooterRefreshView.setOnClickListener(new OnClickFooterRefreshListener());

		mRefreshOriginalTopPadding = mHeaderRefreshView.getPaddingTop();
		mRefreshOriginalBottomPadding = mHeaderRefreshView.getPaddingBottom();

		mHeaderRefreshView.setFocusable(true);
		mHeaderRefreshView.setClickable(true);

		// if(attrs != null){
		// boolean isHeaderVisiable = attrs.getAttributeBooleanValue(null,
		// ATTR_IS_HEADER_VISIABLE, true);
		// boolean isFooterVisiable = attrs.getAttributeBooleanValue(null,
		// ATTR_IS_FOOTER_VISIABLE, true);
		// EvtLog.d("trace", isHeaderVisiable+", "+isFooterVisiable);
		//
		// if(isHeaderVisiable){
		// addHeaderView(mHeaderRefreshView);
		// }
		// if(isFooterVisiable){
		// addFooterView(mFooterRefreshView);
		// }
		// }else{
		// addHeaderView(mHeaderRefreshView);
		// addFooterView(mFooterRefreshView);
		// }
		addHeaderView(mHeaderRefreshView);
		addFooterView(mFooterRefreshView);

		mFooterView = new TextView(context);
		mFooterView.setText(" ");
		mFooterView.setHeight(0);
		// mFooterView.setBackgroundColor(Color.RED);
		addFooterView(mFooterView, null, false);

		super.setOnScrollListener(this);

		measureView(mHeaderRefreshView);
		measureView(mFooterRefreshView);
		// measureView(mFooterView);
		mHeaderRefreshViewHeight = mHeaderRefreshView.getMeasuredHeight();
		mFooterRefreshViewHeight = mFooterRefreshView.getMeasuredHeight();

		setupAnimations();

		final ViewConfiguration configuration = ViewConfiguration.get(context);
		mTouchSlot = configuration.getScaledTouchSlop();
		EvtLog.d(TAG, "touchSlot: " + mTouchSlot);

		resetHeader();
	}

	private void setupAnimations() {
		// Load all of the animations we need in code rather than through XML
		mFlipAnimation = new RotateAnimation(
				0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());
		mFlipAnimation.setDuration(250);
		mFlipAnimation.setFillAfter(true);
		mReverseFlipAnimation = new RotateAnimation(
				-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(250);
		mReverseFlipAnimation.setFillAfter(true);
	}

	/**
	 * 设置头部和尾部的布局文件，需要完善
	 * 
	 * @param headLayoutId
	 * @param footerLayoutId
	 */
	public void setLayout(int headLayoutId, int footerLayoutId) {

	}

	public void setHeaderVisiable(boolean bVisiable) {
		// this.mHeaderRefreshView.setVisibility(visibility);
		int count = mHeaderRefreshView.getChildCount();
		EvtLog.d("trace", "before remove, header childs: " + count);

		if (bVisiable) {
			if (count == 0) {
				this.mHeaderRefreshView.addView(mHeaderContentView);
				this.setHeaderDividersEnabled(true);
				measureView(mHeaderRefreshView);
				mHeaderRefreshViewHeight = mHeaderRefreshView.getMeasuredHeight();
			}
		} else {
			if (count > 0) {
				this.mHeaderRefreshView.removeAllViews();
				this.setHeaderDividersEnabled(false);
				measureView(mHeaderRefreshView);
				mHeaderRefreshViewHeight = mHeaderRefreshView.getMeasuredHeight();
			}
		}
	}

	public void setFooterVisiable(boolean bVisiable) {
		// this.mFooterRefreshView.setVisibility(visibility);
		int count = mFooterRefreshView.getChildCount();
		EvtLog.d("trace", "before remove, footer childs: " + count);

		if (bVisiable) {
			if (count == 0) {
				this.mFooterRefreshView.addView(mFooterContentView);
				this.setFooterDividersEnabled(true);
				measureView(mFooterRefreshView);
				mFooterRefreshViewHeight = mFooterRefreshView.getMeasuredHeight();
				adaptExtraFooterHeight();
			}
		} else {
			if (count > 0) {
				this.mFooterRefreshView.removeAllViews();
				this.setFooterDividersEnabled(false);
				measureView(mFooterRefreshView);
				mFooterRefreshViewHeight = mFooterRefreshView.getMeasuredHeight();
				adaptExtraFooterHeight();
			}
		}
	}

	public void setHeaderText(int textResId) {
		mHeaderRefreshViewText.setText(textResId);
	}

	public void setFooterText(int textResId) {
		mFooterRefreshViewText.setText(textResId);
	}

	public void setCompleted(boolean isCompleted) {
		this.mIsCompleted = isCompleted;
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		setSelection(1);
		//setSelection(1);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {

		super.setAdapter(adapter);
		setSelectionFromTop(1, -2);

		if (adapter != null) {
			adapter.registerDataSetObserver(mDataSetObserver);
		}
	}

	/**
	 * Set the listener that will receive notifications every time the list
	 * scrolls.
	 * 
	 * @param l
	 *            The scroll listener.
	 */
	@Override
	public void setOnScrollListener(AbsListView.OnScrollListener onScrollListener) {
		mOnScrollListener = onScrollListener;
	}

	/**
	 * Register a callback to be invoked when this list should be refreshed.
	 * 
	 * @param onRefreshListener
	 *            The callback to run.
	 */
	public void setOnHeaderRefreshListener(OnRefreshListener onRefreshListener) {
		mOnHeaderRefreshListener = onRefreshListener;
	}

	/**
	 * Register a callback to be invoked when this list should be refreshed.
	 * 
	 * @param onRefreshListener
	 *            The callback to run.
	 */
	public void setOnFooterRefreshListener(OnRefreshListener onRefreshListener) {
		mOnFooterRefreshListener = onRefreshListener;
	}

	private void applyHeaderPadding(MotionEvent ev) {
		final int historySize = ev.getHistorySize();

		// Workaround for getPointerCount() which is unavailable in 1.5
		// (it's always 1 in 1.5)
		int pointerCount = 1;
		try {
			Method method = MotionEvent.class.getMethod("getPointerCount");
			pointerCount = (Integer) method.invoke(ev);
		} catch (NoSuchMethodException e) {
			pointerCount = 1;
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalAccessException e) {
			System.err.println("unexpected " + e);
		} catch (InvocationTargetException e) {
			System.err.println("unexpected " + e);
		}
	}

	/**
	 * Sets the header padding back to original size.
	 */
	private void resetHeaderPadding() {
		mHeaderRefreshView.setPadding(mHeaderRefreshView.getPaddingLeft(), mHeaderRefreshView.getPaddingTop(),
				mHeaderRefreshView.getPaddingRight(), mRefreshOriginalBottomPadding);
	}

	/**
	 * Resets the header to the original state.
	 */
	private void resetHeader() {
		EvtLog.d(TAG, "resetHeader, " + mRefreshState);

		// if (mRefreshState != TAP_TO_REFRESH) {
		// mRefreshState = TAP_TO_REFRESH;
		if (mRefreshState != PULL_TO_REFRESH) {
			mRefreshState = PULL_TO_REFRESH;

			// resetHeaderPadding();
			//
			// // Set refresh view text to the pull label
			// mHeaderRefreshView.setVisibility(View.VISIBLE);
			// mHeaderRefreshViewImage.setVisibility(View.VISIBLE);
			// mHeaderRefreshViewText.setVisibility(View.VISIBLE);
			// mHeaderRefreshViewText.setText(R.string.pull_to_refresh_pull_label);
			// mHeaderRefreshViewProgress.setVisibility(View.GONE);
			// mFooterRefreshViewProgress.setVisibility(View.GONE);
		}

		resetHeaderPadding();

		// Set refresh view text to the pull label
		mHeaderRefreshView.setVisibility(View.VISIBLE);
		mHeaderRefreshViewImage.setVisibility(View.VISIBLE);
		mHeaderRefreshViewPrompt.setVisibility(View.VISIBLE);
		mHeaderRefreshViewText.setVisibility(View.VISIBLE);
		mHeaderRefreshViewText.setText(R.string.pull_to_refresh_pull_label);
		mHeaderRefreshViewTime.setVisibility(View.VISIBLE);
		mHeaderRefreshViewTime.setText(getTimeString());
		mHeaderRefreshViewProgress.setVisibility(View.GONE);
		mFooterRefreshViewProgress.setVisibility(View.GONE);
	}

	private void refreshTime() {
		mHeaderRefreshViewTime.setText(getTimeString());
	}

	Date mLastRefreshTime;

	private String getTimeString() {
		if (mLastRefreshTime == null) {
			return "没有刷新";
		} else {
			Date now = new Date();
			int t = now.getYear() - mLastRefreshTime.getYear();
			if (t > 0) {
				return "更新于" + t + "年前";
			}
			t = now.getMonth() - mLastRefreshTime.getMonth();
			if (t > 0) {
				return "更新于" + t + "个月前";
			}
			t = now.getDate() - mLastRefreshTime.getDate();
			if (t > 0) {
				return "更新于" + t + "天前";
			}
			t = now.getHours() - mLastRefreshTime.getHours();
			if (t > 0) {
				return "更新于" + t + "小时前";
			}
			t = now.getMinutes() - mLastRefreshTime.getMinutes();
			if (t > 0) {
				return "更新于" + t + "分钟前";
			} else {
				return "更新于1分钟前";
			}
		}
	}

	private void saveRefreshTime() {
		mLastRefreshTime = null;
		mLastRefreshTime = new Date();
	}

	/**
	 * Resets the header to the original state.
	 */
	private void resetFooter() {
		if (mRefreshState != TAP_TO_REFRESH) {
			mRefreshState = TAP_TO_REFRESH;

			// resetHeaderPadding();

			// Set refresh view text to the pull label
			mHeaderRefreshViewText.setText(R.string.pull_to_refresh_pull_label);
			mHeaderRefreshViewProgress.setVisibility(View.GONE);
			mFooterRefreshViewProgress.setVisibility(View.GONE);
		}
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

		// 如果footer可见了，自动加载
		if (mRefreshState != REFRESHING && !mIsCompleted) {
			if (visibleItemCount < totalItemCount && firstVisibleItem + visibleItemCount == totalItemCount) {
				onFooterRefresh();
				return;
			}
		}

		// When the headerview is completely visible, change the text to say
		// "Release to refresh..." and flip the arrow drawable.
		if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL && mRefreshState != REFRESHING) {

			if (firstVisibleItem == 0) {
				EvtLog.d(TAG, "header 可见");
				mHeaderRefreshViewImage.setVisibility(View.VISIBLE);
				if ((mHeaderRefreshView.getBottom() >= mHeaderRefreshViewHeight * 0.8 || mHeaderRefreshView.getTop() >= 0)
						&& mRefreshState != RELEASE_TO_REFRESH) {

					EvtLog.d(TAG, "释放刷新");
					mHeaderRefreshViewText.setText(R.string.pull_to_refresh_release_label);
					mHeaderRefreshViewImage.clearAnimation();
					mHeaderRefreshViewImage.startAnimation(mFlipAnimation);
					mRefreshState = RELEASE_TO_REFRESH;
				} else if (mHeaderRefreshView.getBottom() < mHeaderRefreshViewHeight * 0.8
						&& mRefreshState != PULL_TO_REFRESH) {

					EvtLog.d(TAG, "下拉刷新");
					mHeaderRefreshViewText.setText(R.string.pull_to_refresh_pull_label);
					mHeaderRefreshViewImage.clearAnimation();
					mHeaderRefreshViewImage.startAnimation(mReverseFlipAnimation);
					mRefreshState = PULL_TO_REFRESH;
				}
			} else {
				EvtLog.d(TAG, "header 不可见");
				mHeaderRefreshViewImage.clearAnimation();
				mHeaderRefreshViewImage.setVisibility(View.GONE);
				resetHeader();
			}
		}

		// 手势往下一滑，然后headerview滚动出来之后，马上停止，并让第一条数据显示，不显示headerview
		else if (mCurrentScrollState == SCROLL_STATE_FLING && firstVisibleItem == 0 && mRefreshState != REFRESHING) {
			setSelectionFromTop(1, -2);
			EvtLog.d(TAG, "手势往下一滑");
		}

		if (mOnScrollListener != null) {
			mOnScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		final int y = (int) event.getY();
		switch (event.getAction()) {
		
			case MotionEvent.ACTION_UP:
				mTouchStatus = NO_POINTER;
				System.out.println("-----------------"+event.getAction());
				if (!isVerticalScrollBarEnabled()) {
					setVerticalScrollBarEnabled(true);
				}

				if (Math.abs(y - mLastMotionY) > mTouchSlot && getFirstVisiblePosition() == 0
						&& mRefreshState != REFRESHING) {
					// 刷新数据
					if (mHeaderRefreshView.getBottom() >= mHeaderRefreshViewHeight * 0.8
							&& mRefreshState == RELEASE_TO_REFRESH) {
						onHeaderRefresh();
					}
					// 不刷新数据
					else if (mHeaderRefreshView.getBottom() < mHeaderRefreshViewHeight * 0.8
							|| mHeaderRefreshView.getTop() <= 0) {

						EvtLog.d(TAG, "resetHeader, 不刷新数据");
						resetHeader();
						setSelectionFromTop(1, -2);
					}
				}

				break;
			case MotionEvent.ACTION_DOWN:
				mLastMotionY = y;
				mTouchStatus = HAS_POINTERS;
				refreshTime();
				break;
			case MotionEvent.ACTION_MOVE:
				applyHeaderPadding(event);
				break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 计算第2个footer的高度，使整个listview能够选中第一个元素，将header顶上去不可见
	 */
	private void adaptExtraFooterHeight() {
		int listHeight = getHeight();
		ListAdapter adapter = getAdapter();
		if (listHeight > 0) {
			if (adapter != null) {
				int itemCount = adapter.getCount();
				// 没有元素
				if (itemCount == 3) {
					int extraFooterHeight = listHeight - mHeaderRefreshViewHeight - mFooterRefreshViewHeight
							+ mHeaderRefreshViewHeight * 2 + 3;
					EvtLog.d(TAG, "adaptExtraFooterHeight, lh: " + listHeight + ", hh: " + mHeaderRefreshViewHeight
							+ ", fh: " + mFooterRefreshViewHeight + ", efh: " + extraFooterHeight + ", ic: "
							+ (itemCount - 3));
					mFooterView.setHeight(extraFooterHeight);
					invalidate();
					if (getFirstVisiblePosition() == 0) {
						setSelectionFromTop(1, -2);
						checkSelection();
					}
				}
				// 有元素
				else {
					int firstPos = getFirstVisiblePosition();
					int lastPos = getLastVisiblePosition();
					int childIdx = firstPos;
					if (firstPos == 0 && 1 <= lastPos) {
						childIdx = 1;
					}
					View itemView = getChildAt(childIdx);
					if (itemView != null) {
						int itemHeight = itemView.getHeight();
						if (itemHeight > 0) {
							int extraFooterHeight = listHeight - (itemHeight + getDividerHeight()) * (itemCount - 3)
									- mFooterRefreshViewHeight + 3;
							EvtLog.d(TAG, "adaptExtraFooterHeight, lh: " + listHeight + ", hh: "
									+ mHeaderRefreshViewHeight + ", fh: " + mFooterRefreshViewHeight + ", efh: "
									+ extraFooterHeight + ", idx: " + childIdx + ", ih: " + itemHeight + ", ic: "
									+ (itemCount - 3));
							mFooterView.setHeight(extraFooterHeight);
							invalidate();
							if (getFirstVisiblePosition() == 0) {
								setSelectionFromTop(1, -2);
								checkSelection();
							}
							EvtLog.d(TAG, "firstVisible: " + getFirstVisiblePosition() + ", mHeaderRefreshViewBottom: "
									+ mHeaderRefreshView.getBottom() + ",firstPos: " + getFirstVisiblePosition()
									+ ",lastPos: " + getLastVisiblePosition());

						}
						// 取不到列表元素view的高度时
						else {
							postDelayed(new Runnable() {

								@Override
								public void run() {
									adaptExtraFooterHeight();
								}
							}, 300);
						}
					}
					// 取不到列表元素view时
					else {
						postDelayed(new Runnable() {

							@Override
							public void run() {
								adaptExtraFooterHeight();
							}
						}, 300);
					}
				}
			}
		}
		// postDelayed(adaptExtraFooterHeightRunnable, 5);
	}

	private int mCheckSelectionCount = 0;
	private int mCheckSelectionCountMax = 60;

	private void checkSelection() {
		EvtLog.d(TAG, "checkSelection, first: " + getFirstVisiblePosition() + ", state: " + mRefreshState
				+ ", checkSelectionCount: " + mCheckSelectionCount);
		if (mCheckSelectionCount >= mCheckSelectionCountMax) {
			return;
		}
		invalidate();
		postDelayed(new Runnable() {
			@Override
			public void run() {
				if (getFirstVisiblePosition() == 0 && mRefreshState != REFRESHING) {
					adaptExtraFooterHeight();
					mCheckSelectionCount++;
				} else {
					mCheckSelectionCount = 0;
				}
			}
		}, 300);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		EvtLog.d("testC", "onDraw, " + getFirstVisiblePosition());
		// if (mExtentFooterStatus == EXTENDED_FOOTER && mTouchStatus ==
		// NO_POINTER && mRefreshState != REFRESHING) {
		// EvtLog.d(TAG, "onDraw, adaptExtraFooterHeight");
		// adaptExtraFooterHeight();
		// }

		if (getFirstVisiblePosition() == 0 && mTouchStatus == NO_POINTER && mRefreshState != REFRESHING) {
			adaptExtraFooterHeight();
			checkSelection();
		}
	}

	private DataSetObserver mDataSetObserver = new DataSetObserver() {
		@Override
		public void onChanged() {
			super.onChanged();
			EvtLog.d(TAG, "DataSetObserver.onChanged, adaptExtraFooterHeight");
			adaptExtraFooterHeight();
		}
	};

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		mCurrentScrollState = scrollState;
		if (mOnScrollListener != null) {
			mOnScrollListener.onScrollStateChanged(view, scrollState);
		}

		EvtLog.d(TAG, "onScrollStateChanged, scrollState: " + scrollState + ", firstVisibleItemIdx: "
				+ getFirstVisiblePosition());
		if (scrollState == SCROLL_STATE_IDLE && getFirstVisiblePosition() == 0 && mRefreshState != REFRESHING) {
			adaptExtraFooterHeight();
		}
	}

	public void onHeaderRefresh() {
		EvtLog.d(TAG, "onHeaderRefresh");

		resetHeaderPadding();

		mHeaderRefreshViewImage.clearAnimation();
		mHeaderRefreshViewImage.setVisibility(View.GONE);

		mHeaderRefreshViewProgress.setVisibility(View.VISIBLE);
		mFooterRefreshViewProgress.setVisibility(View.VISIBLE);
		// Set refresh view text to the refreshing label
		mHeaderRefreshViewText.setText(R.string.pull_to_refresh_refreshing_label);
		mFooterRefreshViewText.setText(R.string.more);
		mRefreshState = REFRESHING;

		if (mOnHeaderRefreshListener != null) {
			mOnHeaderRefreshListener.onRefresh();
		}

		setCompleted(false);
		EvtLog.d(TAG, "onHeaderRefresh end");
	}

	public void onFooterRefresh() {
		EvtLog.d(TAG, "onFooterRefresh");
		if (mIsCompleted) {
			return;
		}

		mHeaderRefreshViewProgress.setVisibility(View.VISIBLE);
		mHeaderRefreshViewImage.setVisibility(View.INVISIBLE);
		mFooterRefreshViewProgress.setVisibility(View.VISIBLE);
		mHeaderRefreshViewText.setText(R.string.pull_to_refresh_refreshing_label);
		// Set refresh view text to the refreshing label
		mRefreshState = REFRESHING;

		if (mOnFooterRefreshListener != null) {
			mOnFooterRefreshListener.onRefresh();
		}
	}

	/**
	 * Resets the list to a normal state after a refresh.
	 */
	public void onHeaderRefreshComplete() {
		// If refresh view is visible when loading completes, scroll down to
		// the next item.
		if (mHeaderRefreshView.getBottom() > 0) {
			EvtLog.d(TAG, "onHeaderRefreshComplete setSelection");
			invalidateViews();
			setSelectionFromTop(1, -2);
		}

		EvtLog.d(TAG, "resetHeader,  onHeaderRefreshComplete");
		resetHeader();
		saveRefreshTime();
	}

	/**
	 * Resets the list to a normal state after a refresh.
	 */
	public void onFooterRefreshComplete() {
		// If refresh view is visible when loading completes, scroll down to the
		// next item.
		if (mFooterRefreshView.getBottom() > 0) {
			invalidateViews();
		}
		resetFooter();
	}

	@Override
	public void onFilterComplete(int count) {
		super.onFilterComplete(count);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	/**
	 * Invoked when the refresh view is clicked on. This is mainly used when
	 * there's only a few items in the list and it's not possible to drag the
	 * list.
	 */
	private class OnClickHeaderRefreshListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (mRefreshState != REFRESHING) {
				onHeaderRefresh();
			}
		}
	}

	/**
	 * Invoked when the refresh view is clicked on. This is mainly used when
	 * there's only a few items in the list and it's not possible to drag the
	 * list.
	 */
	private class OnClickFooterRefreshListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			if (mRefreshState != REFRESHING) {
				onFooterRefresh();
			}
		}
	}

	/**
	 * Interface definition for a callback to be invoked when list should be
	 * refreshed.
	 */
	public interface OnRefreshListener {
		/**
		 * Called when the list should be refreshed.
		 * <p>
		 * A call to {@link PullToRefreshListView #onRefreshComplete()} is
		 * expected to indicate that the refresh has completed.
		 */
		public void onRefresh();
	}
}
