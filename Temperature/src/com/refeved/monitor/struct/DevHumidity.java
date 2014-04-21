/*
 * 文件名：DevHumidity.java
 * 功能：设备湿度信息结构体
 * 作者：huwei
 * 创建时间：2013-10-25
 * 
 * 
 * 
 * */
package com.refeved.monitor.struct;

public class DevHumidity extends Device{
	private String mHumidity;
	public DevHumidity(String type, String id, String location, String status,
			String descprition,String low,String high,String humidity,String sn) {
		super(type, id, location, status, descprition, low, high, sn);
		
		mHumidity = humidity;
	}
	public String getmHumidity() {
		return mHumidity;
	}
	public void setmHumidity(String mHumidity) {
		this.mHumidity = mHumidity;
	}

}
