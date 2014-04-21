package com.refeved.monitor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.refeved.monitor.struct.Device;
import com.refeved.monitor.struct.DeviceStatus;
import com.refeved.monitor.ui.DevDetailActivity;
import com.refeved.monitor.ui.MainActivity;

public class UIHealper {
	public static void DisplayToast(Context context, CharSequence charSequence)
	{
		Toast.makeText(context, charSequence, Toast.LENGTH_SHORT).show();
	}

	public static void showDevDetail(Context context, Device device)
	{
		Intent intent = new Intent(context, DevDetailActivity.class);
		String id = context.getResources().getString(R.string.device_info_id_tittle);
		intent.putExtra(id, device.getmId());

		//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static void showHome(Context context)
	{
		Intent intent = new Intent(context,MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		context.startActivity(intent);
	}

	public static String getSettingValue(String[] arrayOption,String[] arrayValue,String key){
		for(int i = 0 ;i < arrayOption.length ; i++)
		{
			if(arrayOption[i].equals(key))
			{
				return arrayValue[i];
			}
		}

		return "0";
	}

	public static String parseStatus(String status){
		int Istatus = Integer.parseInt(status);
//		return DeviceStatus.str_Device_Status[Istatus];
		return DeviceStatus_convert(Istatus);
	}
	public static String DeviceStatus_convert(int l_Status){
		String reture_Status = "";
		if((l_Status & 0x08) != 0){
			reture_Status = "网络异常 ";
		}
		else{
			if((l_Status & 0x10) != 0){
				reture_Status = "设备报警 ";
			}
			if((l_Status & 0x04) != 0){
				reture_Status += "人工处理中 ";
			}
			if((l_Status & 0x02) != 0){
				reture_Status += "开门中 ";
			}
			if((l_Status & 0x01) != 0){
				if(reture_Status.equals(""))
					reture_Status += "设备正常 ";
			}
			else{
				reture_Status += "使用电池 ";
			}
		}
		return reture_Status;
		
	}
	
	/**
     * 判断是否为合法IP
     * 网上摘的，自己验证下，怎么用，我就不用说了吧？
     * @return true or false
     */
    public static boolean isIpv4(String ipAddress) {
        String ip = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
        Pattern pattern = Pattern.compile(ip);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }
}
