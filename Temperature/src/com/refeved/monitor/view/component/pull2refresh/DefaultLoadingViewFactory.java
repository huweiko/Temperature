package com.refeved.monitor.view.component.pull2refresh;

import android.content.Context;

class DefaultLoadingViewFactory implements
		PullToRefreshInterfaces.Factories.LoadingViewFactory {
	public PullToRefreshInterfaces.Views.LoadingView createLoadingView(
			Context context, PullToRefreshInterfaces.PullDirections dir) {
		switch (dir) {
		case PULL_DOWN:
		case PULL_UP:
			return new DefaultLoadingView(context, dir);
		}

		return null;
	}
}