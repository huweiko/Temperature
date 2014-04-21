package com.refeved.monitor.struct;


public class DeviceDealLog {
	
	private String mAcid;
	private String mDescription;
	private String mStarttime;
	private String mStartUserName;
	private String mStartDes;
	private String mEndUserName;
	private String mEndtime;
	private String mEndDes;

	public String getmacid() {
		return mAcid;
	}

	public void setmacid(String mAcid) {
		this.mAcid = mAcid;
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
	
	public String getmStartUserName() {
		return mStartUserName;
	}

	public void setmStartUserName(String mStartUserName) {
		this.mStartUserName = mStartUserName;
	}

	public String getmStartDes() {
		return mStartDes;
	}

	public void setmStartDes(String mStartDes) {
		this.mStartDes = mStartDes;
	}

	public String getmEndUserName() {
		return mEndUserName;
	}

	public void setmEndUserName(String mEndUserName) {
		this.mEndUserName = mEndUserName;
	}

	public String getmEndDes() {
		return mEndDes;
	}

	public void setmEndDes(String mEndDes) {
		this.mEndDes = mEndDes;
	}
	
	public DeviceDealLog(String mAcid,String mDescription ,String mStarttime ,String mStartUserName ,String mStartDes ,String mEndUserName ,String mEndtime ,String mEndDes) {
		// TODO Auto-generated constructor stub
		this.mAcid = mAcid;
		this.mDescription = mDescription;
		this.mStarttime = mStarttime;
		this.mStartUserName = mStartUserName;
		this.mStartDes = mStartDes;
		this.mEndUserName = mEndUserName;
		this.mEndtime = mEndtime;
		this.mEndDes = mEndDes;
	}
}
