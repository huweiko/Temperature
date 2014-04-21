package com.refeved.monitor.struct;
public class DeviceScanning{
	
	public String mDescribe;//设备描述
	public String mAid;//设备地址aid
	public String mType;//设备类型 6：代表温度设备  7：代表湿度设备
	public String mClow;//温湿度上限
	public String mChigh;//温湿度下限
	public String mCdif;//温度偏差
	public String mSN;//设备序列号
	
	public DeviceScanning(String Describe, String Aid, String Type, String Clow, String Chigh, String Cdif, String SN ){
		mDescribe = Describe;
		mAid = Aid;
		mType = Type;
		mClow = Clow;
		mChigh = Chigh;
		mCdif = Cdif;
		mSN = SN;
	}
};