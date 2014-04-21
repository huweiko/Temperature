package com.refeved.monitor.view.component.pull2refresh;

import android.view.View;
import android.widget.AdapterView;

public abstract class DetectEdgeUtil {
	boolean isTopEdgeVisible(View view) {
		return false;
	}

	boolean isBottomEdgeVisible(View view) {
		return false;
	}

	public static final boolean detectTopEdge(View view) {
		if ((view == null) || (view.getVisibility() != 0)) {
			return false;
		}
		if ((view instanceof AdapterView)) {
			return a((AdapterView) view);
		}

		return view.getScrollY() >= 0;
	}

	public static final boolean detectLeftEdge(View view) {
		if ((view == null) || (view.getVisibility() != 0)) {
			return false;
		}
		if ((view instanceof AdapterView)) {
			return b((AdapterView) view);
		}

		return view.getScrollX() >= 0;
	}

	public static final boolean detectBottomEdge(View view) {
		if ((view == null) || (view.getVisibility() != 0)) {
			return false;
		}
		if ((view instanceof AdapterView)) {
			return d((AdapterView) view);
		}
		return view.getScrollY() + view.getMeasuredHeight() >= view.getBottom();
	}

	public static final boolean detectRightEdge(View view) {
		if ((view == null) || (view.getVisibility() != 0)) {
			return false;
		}
		if ((view instanceof AdapterView)) {
			return c((AdapterView) view);
		}
		return view.getScrollX() + view.getMeasuredWidth() >= view.getRight();
	}

	private static boolean a(AdapterView paramAdapterView) {
		if (paramAdapterView.getCount() == 0) {
			return true;
		}
		if (paramAdapterView.getFirstVisiblePosition() == 0) {
			View localView = paramAdapterView.getChildAt(0);
			if (localView != null) {
				return localView.getTop() >= paramAdapterView.getTop();
			}
		}
		return false;
	}

	private static boolean b(AdapterView paramAdapterView) {
		if (paramAdapterView.getCount() == 0) {
			return true;
		}
		if (paramAdapterView.getFirstVisiblePosition() == 0) {
			View localView = paramAdapterView.getChildAt(0);
			if (localView != null) {
				return localView.getLeft() >= paramAdapterView.getLeft();
			}
		}
		return false;
	}

	private static boolean c(AdapterView paramAdapterView) {
		int i = paramAdapterView.getCount();
		int j = paramAdapterView.getLastVisiblePosition();
		if (i == 0) {
			return true;
		}
		if (j == i - 1) {
			int k = j - paramAdapterView.getFirstVisiblePosition();
			View localView = paramAdapterView.getChildAt(k);
			if (localView != null) {
				return localView.getRight() <= paramAdapterView.getRight();
			}
		}
		return false;
	}

	private static boolean d(AdapterView paramAdapterView) {
		int i = paramAdapterView.getCount();
		int j = paramAdapterView.getLastVisiblePosition();
		if (i == 0) {
			return true;
		}
		if (j == i - 1) {
			int k = j - paramAdapterView.getFirstVisiblePosition();
			View localView = paramAdapterView.getChildAt(k);
			if (localView != null) {
				return localView.getBottom() <= paramAdapterView.getBottom();
			}
		}
		return false;
	}
}