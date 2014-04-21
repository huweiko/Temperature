package com.refeved.monitor.struct;


public class DeviceAbnormalLog {

	private String mAcid;
	private String mDescription;
	private String mExceptionType;
	private String mStarttime;
	private String mEndtime;
	
	public String getmacid() {
		return mAcid;
	}

	public void setmacid(String mAcid) {
		this.mAcid = mAcid;
	}
	
	public String getmExceptionType() {
		return mExceptionType;
	}

	public void setmExceptionType(String mExceptionType) {
		this.mExceptionType = mExceptionType;
	}

	public String getmDescription() {
		return mDescription;
	}

	public void setmDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	public String getmStarttime() {
		return mStarttime;
	}

	public void setmStarttime(String mStarttime) {
		this.mStarttime = mStarttime;
	}

	public String getmEndtime() {
		return mEndtime;
	}

	public void setmEndtime(String mEndtime) {
		this.mEndtime = mEndtime;
	}
	
	public DeviceAbnormalLog(String mAcid,String mDescription ,String mExceptionType,String mStarttime,String mEndtime) {
		// TODO Auto-generated constructor stub
		this.mAcid          = mAcid;
		this.mDescription   = mDescription;
		this.mExceptionType = mExceptionType;
		this.mStarttime     = mStarttime;
		this.mEndtime       = mEndtime;
	}
}
