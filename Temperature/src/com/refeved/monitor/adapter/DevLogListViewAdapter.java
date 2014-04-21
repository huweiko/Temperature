package com.refeved.monitor.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.refeved.monitor.R;
import com.refeved.monitor.struct.Device;
import com.refeved.monitor.struct.DeviceLog;
import com.refeved.monitor.ui.DevDetailActivity;

public class DevLogListViewAdapter extends BaseAdapter {
	
	private Context 					context;//运行上下文
	private List<DeviceLog> 					listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private int 						itemViewResource;//自定义项视图源 

	static class ListItemView{				//自定义控件集合  
		public ImageView imageView;
	    public TextView time_yymmdd;
	    public TextView time_hhmmss;
	    public TextView Temperature;
	}  
	
	public DevLogListViewAdapter(Context context, List<DeviceLog> data,int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
	}
	
	public void setListItems(List<DeviceLog> data){
		listItems = data;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ListItemView  listItemView = null;
		
		if (convertView == null) {
			//获取list_item布局文件的视图
			convertView = listContainer.inflate(this.itemViewResource, null);
			
			listItemView = new ListItemView();
			//获取控件对象
			listItemView.imageView = (ImageView)convertView.findViewById(R.id.listitem_imageview);
			listItemView.Temperature= (TextView)convertView.findViewById(R.id.dev_detail_log_listitem_temperature_textview);
			listItemView.time_yymmdd = (TextView)convertView.findViewById(R.id.dev_detail_log_listitem_time_textview_YY_MM_DD);
			listItemView.time_hhmmss = (TextView)convertView.findViewById(R.id.dev_detail_log_listitem_time_textview_HH_MM_SS);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//设置文字和图片
		DeviceLog deviceLog = listItems.get(position);
		if(DevDetailActivity.mDeviceType.equals(Device.Type_Frige)){
			listItemView.Temperature.setText(deviceLog.getmTemperature()+"°C");
		}
		else if(DevDetailActivity.mDeviceType.equals(Device.Type_Humidity)){
			listItemView.Temperature.setText(deviceLog.getmTemperature());
		}
		
		String strtime = deviceLog.getmTime();
		String [] sp = strtime.split(" ");
		listItemView.time_yymmdd.setText(sp[0]);
		listItemView.time_hhmmss.setText(sp[1]);
		listItemView.imageView.setBackgroundResource(R.drawable.log_error);
	
		
		return convertView;
	}

}
