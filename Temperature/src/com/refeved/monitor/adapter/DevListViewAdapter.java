package com.refeved.monitor.adapter;

import java.util.List;

import com.refeved.monitor.R;
import com.refeved.monitor.struct.DevFrige;
import com.refeved.monitor.struct.Device;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class DevListViewAdapter extends BaseAdapter {
	
	private Context 					context;//运行上下文
	private List<Device> 					listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	private int 						itemViewResource;//自定义项视图源 

	static class ListItemView{				//自定义控件集合  
		public ImageView imageView;
        public TextView id;  
	    public TextView status;
	    public TextView description;
	}  
	
	public DevListViewAdapter(Context context, List<Device> data,int resource) {
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.itemViewResource = resource;
		this.listItems = data;
	}
	
	public void setListItems(List<Device> data){
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
			listItemView.id = (TextView)convertView.findViewById(R.id.device_info_id);
			listItemView.status= (TextView)convertView.findViewById(R.id.device_info_status);
			listItemView.description= (TextView)convertView.findViewById(R.id.device_info_description);
			listItemView.imageView = (ImageView)convertView.findViewById(R.id.listitem_imageview);
			
			//设置控件集到convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	
		
		//设置文字和图片
		Device device = listItems.get(position);
	
		listItemView.id.setText(context.getString(R.string.device_info_id_tittle)+device.getmId());
		listItemView.imageView.setBackgroundResource(R.drawable.device_info_icon);
		if(device.getmType().equals(Device.Type_Frige))
		{
			DevFrige devFrige = (DevFrige) device;
			listItemView.status.setText(context.getString(R.string.device_info_status_tittile)+
					devFrige.getmStatus()+"  "+context.getString(R.string.frige_info_temp_tittle)+devFrige.getmTemperature());
		}
		else
		{
			listItemView.status.setText(context.getString(R.string.device_info_status_tittile)+device.getmStatus());
		}
		listItemView.description.setText(context.getString(R.string.device_info_description_tittle)+device.getmDescription());
		
		return convertView;
	}

}
