package com.refeved.monitor.view.component.pull2refresh;

public abstract interface OnRefreshListener {
	public abstract void onBeforeRefresh();

	public abstract void refreshInBackground();

	public abstract void onAfterRefresh();

	public static abstract class Standard implements OnRefreshListener {
		public void onBeforeRefresh() {
		}
	}
}
