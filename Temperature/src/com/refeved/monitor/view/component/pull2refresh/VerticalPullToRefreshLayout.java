package com.refeved.monitor.view.component.pull2refresh;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

public class VerticalPullToRefreshLayout extends
		AbstractVerticalPullToRefreshLayout {
	private static final int[] f = new int[2];
	private static final int[] g = new int[2];
	private static final Rect h = new Rect();
	private OnRefreshListener mOnPullDownRefreshListener;
	private PullToRefreshInterfaces.Views.LoadingView mPullDownLoadingView;
	private OnRefreshListener mOnPullUpRefreshListener;
	private PullToRefreshInterfaces.Views.LoadingView mPullUpLoadingView;
	private PullToRefreshInterfaces.PullDirections mCurrentDirection;
	private VerticalEdgeDetector mEdgeDetector;
	private int mPullDownLoadingViewHeight;
	private int mPullUpLoadingViewHeight;
	private boolean mIsListView = false;
	private PullToRefreshInterfaces.Views.LoadingView mPullDownLoadingViewForListView;
	private PullToRefreshInterfaces.Views.LoadingView mPullUpLoadingViewForListView;

	// private int topLogoHeight;

	public VerticalPullToRefreshLayout(Context context) {
		super(context);
	}

	public PullToRefreshInterfaces.PullDirections getmCurrentDirection() {
		return mCurrentDirection;
	}

	public void setmCurrentDirection(
			PullToRefreshInterfaces.PullDirections mCurrentDirection) {
		this.mCurrentDirection = mCurrentDirection;
	}

	public VerticalPullToRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void init(Context paramContext, AttributeSet paramAttributeSet) {
		super.init(paramContext, paramAttributeSet);
		super.setOrientation(1);
	}

	@Override
	void createLoadingViewForListView() {
		if ((this.mRefreshView != null)
				&& ((this.mRefreshView instanceof ListView))) {
			this.mIsListView = true;
			createPullDownViewForListView();
			createPullUpViewForListView();
		}
	}

	@Override
	void removeLoadingView() {
		removePullDownLoadingView();
		setOnPullDownRefreshListener(this.mOnPullDownRefreshListener);

		removePullUpLoadingView();
		setOnPullUpRefreshListener(this.mOnPullUpRefreshListener);
	}

	private void removePullDownLoadingView() {
		if (this.mPullDownLoadingView != null) {
			removeView(this.mPullDownLoadingView.asView());
			this.mPullDownLoadingView = null;
		}
	}

	private void removePullUpLoadingView() {
		if (this.mPullUpLoadingView != null) {
			removeView(this.mPullUpLoadingView.asView());
			// //2013-3-23 safeng修改 --start>
			// //列表滑动到最底下，没有数据时则不允许继续上拉进行刷新，所以在将上拉监听去除时，控件还是记录的上拉状�?，此时调用startLoading方法
			// 会出现空指针异常
			// //�?��在这�?手动将刷新方�?改为下拉
			// this.mCurrentDirection =
			// PullToRefreshInterfaces.PullDirections.PULL_DOWN;
			// //(上拉刷新 设置不能上拉之后 会出现最后一条陷下去的情�?手动设置padding还原)
			// setPadding(0, getPaddingTop(), 0, 0);
			// //<----end
			this.mPullUpLoadingView = null;
		}
	}

	@Override
	public void setLoadingViewFactory(
			PullToRefreshInterfaces.Factories.LoadingViewFactory factory) {
		super.setLoadingViewFactory(factory);
	}

	public void setOnPullDownRefreshListener(OnRefreshListener listener) {
		this.mOnPullDownRefreshListener = listener;
		if (listener == null) {
			removePullDownLoadingView();
			return;
		}
		if (this.mPullDownLoadingView == null)
			createPullDownView();
	}

	private void createPullDownView() {
		this.mPullDownLoadingView = getLoadingViewFactory().createLoadingView(
				getContext(), PullToRefreshInterfaces.PullDirections.PULL_DOWN);
		addLoadingView(this.mPullDownLoadingView.asView(), 0,
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
		measure(this.mPullDownLoadingView);
		this.mPullDownLoadingViewHeight = this.mPullDownLoadingView.asView()
				.getMeasuredHeight();
		// 2013-2-16 safeng修改 下拉不需要出现葡萄logo即可�?��刷新
		// ImageView img = (ImageView)
		// this.mPullDownLoadingView.asView().findViewById(R.id.pulltorefresh_logo_img);
		// topLogoHeight = img.getMeasuredHeight();
		setPadding(0, -this.mPullDownLoadingViewHeight, 0, getPaddingBottom());
	}

	private void createPullDownViewForListView() {
		if (this.mIsListView) {
			this.mPullDownLoadingViewForListView = getLoadingViewFactory()
					.createLoadingView(getContext(),
							PullToRefreshInterfaces.PullDirections.PULL_DOWN);
			FrameLayout layout = new FrameLayout(getContext());
			View localView = this.mPullDownLoadingViewForListView.asView();
			layout.addView(localView, LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			localView.setVisibility(GONE);
			((ListView) this.mRefreshView).addHeaderView(layout, null, false);
		}
	}

	public void setOnPullUpRefreshListener(OnRefreshListener listener) {
		this.mOnPullUpRefreshListener = listener;
		if (listener == null) {
			removePullUpLoadingView();
			return;
		}
		if (this.mPullUpLoadingView == null)
			createPullUpView();
	}

	private void createPullUpView() {
		this.mPullUpLoadingView = getLoadingViewFactory().createLoadingView(
				getContext(), PullToRefreshInterfaces.PullDirections.PULL_UP);
		addLoadingView(this.mPullUpLoadingView.asView(), -1,
				new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT));
		measure(this.mPullUpLoadingView);
		this.mPullUpLoadingViewHeight = this.mPullUpLoadingView.asView()
				.getMeasuredHeight();
		setPadding(0, getPaddingTop(), 0, -this.mPullUpLoadingViewHeight);
	}

	private void createPullUpViewForListView() {
		if (this.mIsListView) {
			this.mPullUpLoadingViewForListView = getLoadingViewFactory()
					.createLoadingView(getContext(),
							PullToRefreshInterfaces.PullDirections.PULL_UP);
			FrameLayout layout = new FrameLayout(getContext());
			View localView = this.mPullUpLoadingViewForListView.asView();
			layout.addView(localView, LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			localView.setVisibility(GONE);
			((ListView) this.mRefreshView).addFooterView(layout);
		}
	}

	private static void measure(
			PullToRefreshInterfaces.Views.LoadingView loadingView) {
		View view = loadingView.asView();
		ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
		if (layoutParams == null) {
			layoutParams = new ViewGroup.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0,
				layoutParams.width);
		int layoutParamsHeight = layoutParams.height;
		int childHeightSpec;
		if (layoutParamsHeight > 0)
			childHeightSpec = View.MeasureSpec.makeMeasureSpec(
					layoutParamsHeight, MeasureSpec.EXACTLY);
		else {
			childHeightSpec = View.MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		view.measure(childWidthSpec, childHeightSpec);
	}

	public void setEdgeDetector(VerticalEdgeDetector detector) {
		this.mEdgeDetector = detector;
	}

	private VerticalEdgeDetector getEdgeDetector() {
		if (this.mEdgeDetector == null) {
			this.mEdgeDetector = new VerticalEdgeDetector();
		}
		return this.mEdgeDetector;
	}

	@Override
	boolean isPullToRefresh(float diffX, float diffY) {
		boolean isVertical = Math.abs(diffY) > Math.abs(diffX);
		boolean isPullUp = (diffY < 0.0F) && pullUpEnable();
		boolean isPullDown = (diffY >= 0.0F) && (pullDownEnable());
		boolean isPullToRefresh = isVertical && ((isPullUp) || (isPullDown));
		if (isPullToRefresh) {
			if (isPullUp) {
				this.mCurrentDirection = PullToRefreshInterfaces.PullDirections.PULL_UP;
			} else if (isPullDown) {
				this.mCurrentDirection = PullToRefreshInterfaces.PullDirections.PULL_DOWN;
			}
		}
		return isPullToRefresh;
	}

	@Override
	boolean pullable() {
		return (this.mCurrentState != PullToRefreshInterfaces.States.STATE_LOADING)
				&& ((pullDownEnable()) || (pullUpEnable()));
	}

	boolean pullDownEnable() {
		if (this.mRefreshView != null) {
			return (pullDownMonitored())
					&& (getEdgeDetector().isTopEdgeVisible(this.mRefreshView));
		}
		return false;
	}

	boolean pullUpEnable() {
		if (this.mRefreshView != null) {
			return (pullUpMonitored())
					&& (getEdgeDetector()
							.isBottomEdgeVisible(this.mRefreshView));
		}
		return false;
	}

	@Override
	void scroll() {
		int selection = 0;
		PullToRefreshInterfaces.Views.LoadingView loadingView = null;
		PullToRefreshInterfaces.Views.LoadingView loadingViewForListView = null;

		int height = 0;
		int heightForListView = 0;
		if ((this.mCurrentDirection == PullToRefreshInterfaces.PullDirections.PULL_DOWN)
				&& (pullDownMonitored())) {
			height = -this.mPullDownLoadingViewHeight;
			if (this.mIsListView) {
				loadingView = this.mPullDownLoadingView;
				loadingViewForListView = this.mPullDownLoadingViewForListView;
				selection = 0;
				heightForListView = getScrollY()
						+ this.mPullDownLoadingViewHeight;
				height = 0;
			}
		} else if ((this.mCurrentDirection == PullToRefreshInterfaces.PullDirections.PULL_UP)
				&& (pullUpMonitored())) {
			height = this.mPullUpLoadingViewHeight;
			if (this.mIsListView) {
				loadingView = this.mPullUpLoadingView;
				loadingViewForListView = this.mPullUpLoadingViewForListView;
				selection = ((ListView) this.mRefreshView).getCount() - 1;
				heightForListView = getScrollY()
						- this.mPullUpLoadingViewHeight;
				height = 0;
			}
		}

		if (this.mIsListView) {
			scrollTo(heightForListView);
			if (loadingView != null && loadingView.asView() != null)
				loadingView.asView().setVisibility(INVISIBLE);
			if (loadingViewForListView != null
					&& loadingViewForListView.asView() != null) {
				loadingViewForListView.asView().setVisibility(VISIBLE);
			}
			((ListView) this.mRefreshView).setSelection(selection);
		}
		smoothTo(height);
	}

	@Override
	public void startLoading() {
		if (this.mCurrentDirection == null) {
			this.mCurrentDirection = PullToRefreshInterfaces.PullDirections.PULL_DOWN;
		}
		super.startLoading();
	}

	@Override
	public void resetState() {
		if (!this.mIsListView) {
			super.resetState();
			return;
		}

		if ((this.mCurrentDirection == PullToRefreshInterfaces.PullDirections.PULL_DOWN)
				&& (pullDownMonitored())) {
			v();
		} else if ((this.mCurrentDirection == PullToRefreshInterfaces.PullDirections.PULL_UP)
				&& (pullUpMonitored())) {
			w();
		}
	}

	private void v() {
		ListView view = (ListView) this.mRefreshView;
		if ((!this.mPullDownLoadingViewForListView.asView().isShown())
				|| (!this.mPullDownLoadingViewForListView.asView()
						.getGlobalVisibleRect(h))) {
			this.mPullDownLoadingView.asView().setVisibility(0);// visible
			this.mPullDownLoadingViewForListView.asView().setVisibility(8);// gone
			super.resetState();
			return;
		}

		int[] arrayOfInt1 = f;
		int[] arrayOfInt2 = g;
		this.mRefreshLayout.getLocationInWindow(arrayOfInt1);
		this.mPullDownLoadingViewForListView.asView().getLocationInWindow(
				arrayOfInt2);
		int i1 = arrayOfInt1[1] - arrayOfInt2[1];

		this.mPullDownLoadingView.asView().setVisibility(0);
		scrollTo(i1 - this.mPullDownLoadingViewHeight);
		view.setSelectionFromTop(view.getFirstVisiblePosition(), 0);
		this.mPullDownLoadingViewForListView.asView().setVisibility(8);

		super.resetState();
	}

	private void w() {
		ListView localListView = (ListView) this.mRefreshView;
		if ((!this.mPullUpLoadingViewForListView.asView().isShown())
				|| (!this.mPullUpLoadingViewForListView.asView()
						.getGlobalVisibleRect(h))) {
			this.mPullUpLoadingView.asView().setVisibility(0);
			this.mPullUpLoadingViewForListView.asView().setVisibility(8);
			super.resetState();
			return;
		}

		int[] arrayOfInt1 = f;
		int[] arrayOfInt2 = g;
		this.mRefreshLayout.getLocationInWindow(arrayOfInt1);
		this.mPullUpLoadingViewForListView.asView().getLocationInWindow(
				arrayOfInt2);
		int i1 = arrayOfInt1[1] + this.mRefreshView.getHeight()
				- arrayOfInt2[1];

		this.mPullUpLoadingView.asView().setVisibility(0);
		scrollTo(i1);
		int i2 = localListView.getChildCount();
		if (i2 > 0) {
			View localView = localListView.getChildAt(i2 - 1);
			localListView.setSelectionFromTop(
					localListView.getLastVisiblePosition(),
					localListView.getTop() + localListView.getHeight()
							- localView.getHeight());
		}

		this.mPullUpLoadingViewForListView.asView().setVisibility(8);

		super.resetState();
	}

	@Override
	void onStateChanged(PullToRefreshInterfaces.States paramStates) {
		PullToRefreshInterfaces.Views.LoadingView loadingView = null;
		PullToRefreshInterfaces.Views.LoadingView loadingViewForListView = null;
		if ((this.mCurrentDirection == PullToRefreshInterfaces.PullDirections.PULL_DOWN)
				&& (pullDownMonitored())) {
			loadingView = this.mPullDownLoadingView;
			if (this.mIsListView)
				loadingViewForListView = this.mPullDownLoadingViewForListView;
		} else if ((this.mCurrentDirection == PullToRefreshInterfaces.PullDirections.PULL_UP)
				&& (pullUpMonitored())) {
			loadingView = this.mPullUpLoadingView;
			if (this.mIsListView) {
				loadingViewForListView = this.mPullUpLoadingViewForListView;
			}
		}
		if (loadingView != null) {
			loadingView.onStateChanged(paramStates);
		}
		if (loadingViewForListView != null)
			loadingViewForListView.onStateChanged(paramStates);
	}

	@Override
	OnRefreshListener getRefreshListener() {
		if ((this.mCurrentDirection == PullToRefreshInterfaces.PullDirections.PULL_DOWN)
				&& (pullDownMonitored())) {
			return this.mOnPullDownRefreshListener;
		}
		if ((this.mCurrentDirection == PullToRefreshInterfaces.PullDirections.PULL_UP)
				&& (pullUpMonitored())) {
			return this.mOnPullUpRefreshListener;
		}
		return null;
	}

	boolean pullDownMonitored() {
		return this.mOnPullDownRefreshListener != null;
	}

	boolean pullUpMonitored() {
		return this.mOnPullUpRefreshListener != null;
	}

	@Override
	boolean a(float y) {
		int i2 = getScrollY();
		this.mCurrentDirection = (y < 0.0F ? PullToRefreshInterfaces.PullDirections.PULL_UP
				: PullToRefreshInterfaces.PullDirections.PULL_DOWN);
		int i1;
		switch (mCurrentDirection) {
		case PULL_UP:
			if (!pullUpEnable()) {
				return false;
			}
			i1 = Math.round(Math.min(y, 0.0F));
			break;
		case PULL_DOWN:
		default:
			if (!pullDownEnable()) {
				return false;
			}
			i1 = Math.round(Math.max(y, 0.0F));
		}

		scrollTo(-i1);

		int i3 = this.mCurrentDirection == PullToRefreshInterfaces.PullDirections.PULL_UP ? this.mPullUpLoadingViewHeight
				: this.mPullDownLoadingViewHeight;
		// 2013-2-16 safeng修改 下拉不需要出现葡萄logo即可�?��刷新
		// i3 = i3 - topLogoHeight;
		if (i1 != 0) {
			if ((this.mCurrentState == PullToRefreshInterfaces.States.STATE_PULL)
					&& (i3 < Math.abs(i1))) {
				setCurrentState(PullToRefreshInterfaces.States.STATE_RELEASE);
				return true;
			}
			if ((this.mCurrentState == PullToRefreshInterfaces.States.STATE_RELEASE)
					&& (i3 >= Math.abs(i1))) {
				setCurrentState(PullToRefreshInterfaces.States.STATE_PULL);
				return true;
			}
		}

		return i2 != i1;
	}

	@Override
	LinearLayout.LayoutParams getRefreshLayoutParams() {
		return new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, 0, 1.0F);
	}
}