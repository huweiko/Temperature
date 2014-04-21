package com.refeved.monitor.struct;

import java.util.Calendar;

public class ErrorDev extends Device{
	Calendar mCal;
	Boolean mChecked = true;
	
	public Boolean getmChecked() {
		return mChecked;
	}

	public void setmChecked(Boolean mChecked) {
		this.mChecked = mChecked;
	}

	public ErrorDev(String type, String id, String location, String status,
			String descprition, String low, String high , Calendar cal,String sn) {
		super(type, id, location, status, descprition, low, high, sn);
		
		mCal = cal;
	}

	public Calendar getmCal() {
		return mCal;
	}

	public void setmCal(Calendar mCal) {
		this.mCal = mCal;
	}

}
