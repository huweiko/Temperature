package com.refeved.monitor.struct;

public class DeviceStatus {
	public static final int ALARM               = 0x10; //设备报警
	public static final int NET_ARBNORMAL       = 0x08; //设备异常
	public static final int MANUAL_HANDLING     = 0x04; //人工处理
	public static final int OPEN_DOOR           = 0x02; //开门中
	public static final int IS_CHARGING         = 0x01; //正在充电
	
	public static final String STR_ALARM           = "设备报警";
	public static final String STR_NET_ARBNORMAL   = "设备异常";
	public static final String STR_MANUAL_HANDLING = "人工处理中";
	public static final String STR_OPEN_DOOR       = "开门中";
	public static final String STR_IS_CHARGING     = "正在充电";
	/*
	 * 该数组记录了设备异常的所有状态，设备状态是一个整形数，低字节五位数分别代表五个状态
	 * bit[0]  置1：正在充电
	 * bit[1]  置1：开门中
	 * bit[2]  置1：人工处理中
	 * bit[3]  置1：网络异常
	 * bit[4]  置1：设备报警
	 * */
	public static final String str_Device_Status[] = {
			"使用电池",       //0
			"设备正常" ,       //1
			"开门中、使用电池" ,       //2
			"开门中" ,       //3
			"人工处理中、使用电池" ,       //4
			"人工处理中" ,       //5
			"人工处理中、开门中、使用电池" ,       //6
			"人工处理中、开门中" ,       //7
			"网络异常" ,       //8
			"网络异常" ,       //9
			"网络异常" ,       //10
			"网络异常" ,       //11
			"网络异常" ,       //12
			"网络异常" ,       //13
			"网络异常" ,       //14
			"网络异常" ,       //15
			"设备报警、使用电池" ,       //16
			"设备报警" ,       //17
			"设备报警、开门中、使用电池" ,       //18
			"设备报警、开门中" ,       //19
			"设备报警、人工处理中、使用电池" ,       //20
			"设备报警、人工处理中" ,       //21
			"设备报警、人工处理中、使用电池、开门中" ,       //22
			"设备报警、人工处理中、开门中" ,       //23
			"网络异常" ,       //24
			"网络异常" ,       //25
			"网络异常" ,       //26
			"网络异常" ,       //27
			"网络异常" ,       //28
			"网络异常" ,       //29
			"网络异常" ,       //30
			"网络异常" ,       //31
			};
}