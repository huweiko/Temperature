package com.refeved.monitor.struct;

public class DeviceLog {
	private String mTemperature;
	private String mTime;
	public String getmTemperature() {
		return mTemperature;
	}

	public void setmTemperature(String Temperature) {
		this.mTemperature = Temperature;
	}

	public String getmTime() {
		return mTime;
	}

	public void setmTime(String Time) {
		this.mTime = Time;
	}

	

	
	public DeviceLog(String Temperature,String Time) {
		// TODO Auto-generated constructor stub
		mTemperature = Temperature;
		mTime = Time;
	}
}
