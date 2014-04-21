package com.refeved.monitor.struct;

public class Device {
	public final static String Type_Frige = "DEV_TYPE_FRIGE";
	public final static String Type_Humidity = "DEV_TYPE_HUM";
	public final static String Type_Tag = "DEV_TYPE_TAG";
	
	private String mType;
	private String mId;
	private String mLocation;
	private String mStatus;
	private String mDescription;
	private String mLow;
	private String mHigh;
	private String mSn;
	
	public Device(String type , String id , String location , String status , String descprition,String low, String high,String sn)
	{
		mType = type;
		mId = id;
		mLocation = location;
		mStatus = status;
		mDescription = descprition;
		mLow = low;
		mHigh = high;
		mSn = sn;
	}

	public String getmType() {
		return mType;
	}

	public void setmType(String mType) {
		this.mType = mType;
	}

	public String getmId() {
		return mId;
	}

	public void setmId(String mId) {
		this.mId = mId;
	}

	public String getmLocation() {
		return mLocation;
	}

	public void setmLocation(String mLocation) {
		this.mLocation = mLocation;
	}

	public String getmStatus() {
		return mStatus;
	}

	public void setmStatus(String mStatus) {
		this.mStatus = mStatus;
	}

	public String getmDescription() {
		return mDescription;
	}

	public void setmDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	public String getmLow() {
		return mLow;
	}

	public void setmLow(String mLow) {
		this.mLow = mLow;
	}

	public String getmHigh() {
		return mHigh;
	}

	public void setmHigh(String mHigh) {
		this.mHigh = mHigh;
	}

	public String getmSn() {
		return mSn;
	}

	public void setmSn(String mSn) {
		this.mSn = mSn;
	}
}
