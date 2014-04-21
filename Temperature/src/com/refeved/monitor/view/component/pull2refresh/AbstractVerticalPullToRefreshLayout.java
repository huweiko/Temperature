package com.refeved.monitor.view.component.pull2refresh;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public abstract class AbstractVerticalPullToRefreshLayout extends LinearLayout {

	private static int mTouchSlop;
	private PullToRefreshInterfaces.Factories.LoadingViewFactory mLoadingViewFactory;
	private boolean i;
	private boolean mPullToRefreshEnabled = true;
	private boolean mExitTasksEarly = false;
	protected PullToRefreshInterfaces.States mCurrentState;
	protected View mRefreshView;
	protected FrameLayout mRefreshLayout;
	private boolean mDisableScrollingWhileRefreshing;
	float d;
	float e;
	private SmoothScrollRunnable mSmoothScrollRunnable;
	private final Handler mHandler = new Handler();
	private float mLastMotionX;
	private float mLastMotionY;
	private boolean q;
	private boolean mIntercept;

	public AbstractVerticalPullToRefreshLayout(Context paramContext) {
		this(paramContext, null);
	}

	public AbstractVerticalPullToRefreshLayout(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init(paramContext, paramAttributeSet);
	}

	protected void init(Context context, AttributeSet paramAttributeSet) {
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		setLoadingViewFactory(new DefaultLoadingViewFactory());
		this.mRefreshLayout = new FrameLayout(context);
		super.addView(this.mRefreshLayout, -1, getRefreshLayoutParams());
		setPadding(0, 0, 0, 0);
	}

	protected void addLoadingView(View child, int index,
			LinearLayout.LayoutParams params) {
		super.addView(child, index, params);
	}

	protected PullToRefreshInterfaces.Factories.LoadingViewFactory getLoadingViewFactory() {
		return this.mLoadingViewFactory;
	}

	@Override
	public void addView(View child, int index, ViewGroup.LayoutParams params) {
		if (this.mRefreshView != null) {
			throw new UnsupportedOperationException(
					"PullToRefreshLayout can host only one direct child");
		}
		this.mRefreshView = child;
		this.mRefreshLayout.addView(child, params);
		createLoadingViewForListView();
	}

	public void setLoadingViewFactory(
			PullToRefreshInterfaces.Factories.LoadingViewFactory factory) {
		this.mLoadingViewFactory = factory;
		removeLoadingView();
	}

	void b() {
		if (this.mCurrentState != PullToRefreshInterfaces.States.STATE_PULL)
			resetState();
	}

	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	public void setPullToRefreshEnabled(boolean enable) {
		this.mPullToRefreshEnabled = enable;
	}

	public boolean isPullToRefreshEnabled() {
		return this.mPullToRefreshEnabled;
	}

	public void setDisableScrollingWhileRefreshing(boolean disable) {
		this.mDisableScrollingWhileRefreshing = disable;
	}

	public boolean isDisableScrollingWhileRefreshing() {
		return this.mDisableScrollingWhileRefreshing;
	}

	public boolean isRefreshing() {
		return this.mCurrentState == PullToRefreshInterfaces.States.STATE_LOADING;
	}

	public void setExitTasksEarly(boolean mExitTasksEarly) {
		this.mExitTasksEarly = mExitTasksEarly;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (this.mIntercept) {
			return onTouchEvent(ev);
		}
		boolean bool = this.mIntercept;
		this.mIntercept = onInterceptTouchEvent(ev);
		if ((this.mIntercept) && (!bool)) {
			int i1 = ev.getAction();
			ev.setAction(3);
			int i2 = getChildCount();
			for (int i3 = 0; i3 < i2; i3++) {
				View localView = getChildAt(i3);
				localView.dispatchTouchEvent(ev);
			}
			ev.setAction(i1);
		}

		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (!this.mPullToRefreshEnabled) {
			return false;
		}

		if ((isRefreshing()) && (this.mDisableScrollingWhileRefreshing)) {
			return true;
		}

		int action = ev.getAction();

		float x = ev.getX();
		float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			if (!pullable())
				break;
			b(x, y);
			break;
		case MotionEvent.ACTION_DOWN:
			this.e = (this.d = isVertical() ? y : x);
			this.mLastMotionX = x;
			this.mLastMotionY = y;
			this.i = false;
			this.q = false;
		}

		return this.i;
	}

	private void b(float x, float y) {
		float diff = isVertical() ? y : x;
		float distance = Math.abs(diff - this.d);

		if (distance > mTouchSlop) {
			this.i = isPullToRefresh(x - this.mLastMotionX, y
					- this.mLastMotionY);
		}

		if (this.i) {
			this.d = diff;
			setCurrentState(PullToRefreshInterfaces.States.STATE_PULL);
		}
	}

	public boolean onTouchEvent(MotionEvent event) {
		if (!this.mPullToRefreshEnabled) {
			return false;
		}
		if ((isRefreshing()) && (this.mDisableScrollingWhileRefreshing)) {
			return true;
		}
		if ((event.getAction() == 0) && (event.getEdgeFlags() != 0)) {
			return false;
		}

		float x = event.getX();
		float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
			if ((!this.i) && (pullable())) {
				b(x, y);
			}
			if (!this.i)
				break;
			this.e = (isVertical() ? y : x);
			float f3 = (this.e - this.d) / 2.0F;
			if (!this.q) {
				if (mTouchSlop < Math.abs(f3)) {
					this.d = this.e;
					// f3 = 0.0F;
					this.q = true;
				}
			} else {
				return a(f3);
			}

			break;
		case MotionEvent.ACTION_DOWN:
			if (!pullable())
				break;
			this.e = (this.d = isVertical() ? y : x);
			return true;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			this.mIntercept = false;
			if (!this.i)
				break;
			this.i = false;
			if (this.mCurrentState == PullToRefreshInterfaces.States.STATE_RELEASE) {
				startLoading();
			} else {
				smoothTo(0);
			}
			return true;
		}

		return false;
	}

	private boolean isVertical() {
		return getOrientation() == 1;
	}

	public void resetState() {
		setCurrentState(PullToRefreshInterfaces.States.STATE_PULL);
		this.i = false;
		smoothTo(0);
	}

	protected void setCurrentState(PullToRefreshInterfaces.States paramStates) {
		this.mCurrentState = paramStates;
		onStateChanged(this.mCurrentState);
	}

	protected void scrollTo(int y) {
		if (isVertical()) {
			scrollTo(0, y);
		} else {
			scrollTo(y, 0);
		}
	}

	protected void smoothTo(int y) {
		if (this.mSmoothScrollRunnable != null) {
			this.mSmoothScrollRunnable.stop();
		}

		int distance = isVertical() ? getScrollY() : getScrollX();
		if (distance != y) {
			this.mSmoothScrollRunnable = new SmoothScrollRunnable(distance, y);
			this.mHandler.post(this.mSmoothScrollRunnable);
		}
	}

	void startLoading() {
		setCurrentState(PullToRefreshInterfaces.States.STATE_LOADING);
		scroll();
		OnRefreshListener listener = getRefreshListener();
		if (listener != null)
			new a(listener).execute(new Void[0]);
	}

	abstract void removeLoadingView();

	abstract boolean isPullToRefresh(float x, float y);

	abstract boolean pullable();

	abstract boolean a(float paramFloat);

	abstract LinearLayout.LayoutParams getRefreshLayoutParams();

	abstract void scroll();

	abstract void onStateChanged(PullToRefreshInterfaces.States paramStates);

	abstract OnRefreshListener getRefreshListener();

	abstract void createLoadingViewForListView();

	class a extends AsyncTask<Void, Void, Void> {
		private OnRefreshListener b;

		a(OnRefreshListener arg2) {
			this.b = arg2;
		}

		protected void onPreExecute() {
			super.onPreExecute();
			if (this.b != null && !mExitTasksEarly)
				this.b.onBeforeRefresh();
		}

		@Override
		protected void onPostExecute(Void result) {
			if (this.b != null && !mExitTasksEarly) {
				this.b.onAfterRefresh();
			}

			super.onPostExecute(result);
			b();
		}

		@Override
		protected Void doInBackground(Void... params) {
			if (this.b != null) {
				this.b.refreshInBackground();
			}

			return null;
		}
	}

	final class SmoothScrollRunnable implements Runnable {
		private final Interpolator interpolator;
		private final int to;
		private final int from;
		private boolean continueRunning = true;
		private long startTime = -1L;
		private int current = -1;

		public SmoothScrollRunnable(int from, int to) {
			this.from = from;
			this.to = to;
			this.interpolator = new AccelerateDecelerateInterpolator();
		}

		public void run() {
			if (this.startTime == -1L) {
				this.startTime = System.currentTimeMillis();
			} else {
				long l = 1000L * (System.currentTimeMillis() - this.startTime) / 190L;
				l = Math.max(Math.min(l, 1000L), 0L);

				int i = Math.round((this.from - this.to)
						* this.interpolator
								.getInterpolation((float) l / 1000.0F));

				this.current = (this.from - i);
				AbstractVerticalPullToRefreshLayout.this.scrollTo(this.current);
			}

			if ((this.continueRunning) && (this.to != this.current))
				mHandler.postDelayed(this, 16L);
		}

		public void stop() {
			this.continueRunning = false;
			mHandler.removeCallbacks(this);
		}
	}
}