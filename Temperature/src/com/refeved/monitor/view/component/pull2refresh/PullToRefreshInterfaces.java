package com.refeved.monitor.view.component.pull2refresh;

import android.content.Context;
import android.view.View;

public final class PullToRefreshInterfaces {
	public static enum States {
		STATE_PULL, STATE_LOADING, STATE_RELEASE
	}

	public static enum PullDirections {
		PULL_UP, PULL_DOWN
	}

	public static final class Factories {
		public static abstract interface LoadingViewFactory {
			public abstract PullToRefreshInterfaces.Views.LoadingView createLoadingView(
					Context paramContext,
					PullToRefreshInterfaces.PullDirections paramPullDirections);
		}
	}

	public static final class Views {
		public static abstract interface LoadingView {
			public abstract View asView();

			public abstract void onStateChanged(
					PullToRefreshInterfaces.States paramStates);
		}
	}
}
