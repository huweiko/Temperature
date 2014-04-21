package com.refeved.monitor.view.component.pull2refresh;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.refeved.monitor.R;

class DefaultLoadingView implements PullToRefreshInterfaces.Views.LoadingView {
	private final RelativeLayout j;
	private final ProgressBar k;
	private final TextView l;
	private final ImageView m;
	private final Animation n;
	private final Animation o;
	private String p;
	private final String q;
	private final String r;
	private PullToRefreshInterfaces.States s;

	public DefaultLoadingView(Context content,
			PullToRefreshInterfaces.PullDirections paramPullDirections) {
		this.j = ((RelativeLayout) LayoutInflater.from(content).inflate(
				R.layout.dux_p2r_updn_ldvw, null));
		this.k = ((ProgressBar) this.j.findViewById(R.id.dux_p2r_ldvw_prgrs));
		this.l = ((TextView) this.j.findViewById(R.id.dux_p2r_ldvw_txt));
		this.m = ((ImageView) this.j.findViewById(R.id.dux_p2r_ldvw_img));

		this.r = content.getResources().getString(R.string.release_to_refresh);
		this.q = content.getResources().getString(R.string.loading);

		LinearInterpolator localLinearInterpolator = new LinearInterpolator();
		this.n = new RotateAnimation(0.0F, -180.0F, 1, 0.5F, 1, 0.5F);

		this.n.setInterpolator(localLinearInterpolator);
		this.n.setDuration(125L);
		this.n.setFillAfter(true);

		this.o = new RotateAnimation(-180.0F, 0.0F, 1, 0.5F, 1, 0.5F);

		this.o.setInterpolator(localLinearInterpolator);
		this.o.setDuration(125L);
		this.o.setFillAfter(true);

		switch (paramPullDirections) {
		case PULL_DOWN:
			this.m.setImageResource(R.drawable.pulltorefresh_down_arrow);
			this.p = content.getResources().getString(
					R.string.pull_down_to_refresh);
			break;
		case PULL_UP:
			this.p = content.getResources().getString(
					R.string.pull_up_to_refresh);
			this.m.setImageResource(R.drawable.pulltorefresh_up_arrow);
		}

		onStateChanged(PullToRefreshInterfaces.States.STATE_PULL);
	}

	@Override
    public View asView() {
		return this.j;
	}

	@Override
    public void onStateChanged(PullToRefreshInterfaces.States state) {
		if (state == this.s) {
			return;
		}
		switch (state) {
		case STATE_PULL:
			c();
			break;
		case STATE_LOADING:
			a();
			break;
		case STATE_RELEASE:
			b();
		}

		this.s = state;
	}

	private void a() {
		this.l.setText(this.q);
		this.m.clearAnimation();
		this.m.setVisibility(4);
		this.k.setVisibility(0);
	}

	private void b() {
		this.l.setText(this.r);
		this.m.clearAnimation();
		this.m.startAnimation(this.n);
	}

	private void c() {
		this.l.setText(this.p);
		this.k.setVisibility(8);
		this.m.setVisibility(0);
		this.m.clearAnimation();
		if (this.s == PullToRefreshInterfaces.States.STATE_RELEASE)
			this.m.startAnimation(this.o);
	}
}