package com.refeved.monitor.view.component.pull2refresh;

import android.view.View;

public class VerticalEdgeDetector extends DetectEdgeUtil {
	public boolean isTopEdgeVisible(View view) {
		return DetectEdgeUtil.detectTopEdge(view);
	}

	public boolean isBottomEdgeVisible(View view) {
		return DetectEdgeUtil.detectBottomEdge(view);
	}
}