package com.refeved.monitor.struct;

public class DevFrige extends Device{
	private String mTemperature;
	
	public String getmTemperature() {
		return mTemperature;
	}

	public void setmTemperature(String mTemperature) {
		this.mTemperature = mTemperature;
	}

	public DevFrige(String type, String id, String location,String status,String description,String low,String high ,String temp,String sn) {
		super(type, id, location, status,description, low, high,sn);
		mTemperature = temp;
		// TODO Auto-generated constructor stub
		
	}
	
}
