package com.refeved.monitor.adapter;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.refeved.monitor.R;
import com.refeved.monitor.UIHealper;
import com.refeved.monitor.struct.DevFrige;
import com.refeved.monitor.struct.DevHumidity;
import com.refeved.monitor.struct.Device;
import com.refeved.monitor.view.OptimizeGridView.OptimizeGridAdapter;

public class GridAdapter extends BaseAdapter implements OptimizeGridAdapter<Device>{
	
	public static class Item{
		public String text;
		public int resId;
	}

	private List<Device> mItems = new ArrayList<Device>();
	private Context mContext;
	public GridAdapter(Context context) {
		mContext = context;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public Object getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public boolean isEnabled(int position) {
		if(!mItems.get(position).getmType().equals(Device.Type_Frige) &&
				!mItems.get(position).getmType().equals(Device.Type_Humidity)){
			return false;
		}
		return super.isEnabled(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item, null);
		}
		int Istatus = 0;
		ImageView image = (ImageView) convertView.findViewById(R.id.icon);
		TextView des = (TextView) convertView.findViewById(R.id.text_des);
		TextView degree = (TextView) convertView.findViewById(R.id.text_degree);
		TextView status = (TextView) convertView.findViewById(R.id.text_status);
		View press = convertView.findViewById(R.id.press);
		View alarm = convertView.findViewById(R.id.alarm);
		Device item = (Device) getItem(position);
		if(item.getmStatus() != null && item.getmStatus() != ""){
			Istatus = Integer.parseInt(item.getmStatus());
		}
		//解决残影问题，这里判断如果是NULL item则只显示一个白色背景
		if(isNullItem(item)) {
			convertView.setBackgroundResource(R.drawable.grid_bg);
			image.setVisibility(View.INVISIBLE);
			des.setVisibility(View.INVISIBLE);
			degree.setVisibility(View.INVISIBLE);
			status.setVisibility(View.INVISIBLE);
			press.setVisibility(View.INVISIBLE);
			alarm.setVisibility(View.INVISIBLE);
			return convertView;
		}
		image.setVisibility(View.VISIBLE);
		des.setVisibility(View.VISIBLE);
		convertView.setBackgroundResource(R.drawable.grid_item_border);
		if(item.getmType().equals(Device.Type_Tag))
		{
			image.setImageResource(R.drawable.icon_house);
			des.setText(item.getmLocation());
			degree.setVisibility(View.INVISIBLE);
			status.setVisibility(View.INVISIBLE);
			alarm.setVisibility(View.INVISIBLE);
			press.setVisibility(View.INVISIBLE);
		}
		else if(item.getmType().equals(Device.Type_Frige))
		{
			image.setImageResource(R.drawable.icon_temperature);
			des.setText(item.getmDescription());
			status.setVisibility(View.VISIBLE);
			status.setText(UIHealper.parseStatus(item.getmStatus()));
			degree.setVisibility(View.VISIBLE);
			degree.setText(((DevFrige)item).getmTemperature());
			press.setVisibility(View.VISIBLE);
			alarm.setVisibility(View.VISIBLE);
	
			if(Istatus == 1){
				alarm.setBackgroundResource(R.color.transparent_device);
			}
			else if((Istatus & 0x08) == 0x08){
				alarm.setBackgroundResource(R.color.alarm_gray_color);
			}
			else if((Istatus & 0x10) == 0x10){

				alarm.setBackgroundResource(R.color.alarm_red_color);
			}
			else {
				
				alarm.setBackgroundResource(R.color.alarm_yellow_color);
			}

		}
		else if(item.getmType().equals(Device.Type_Humidity))
		{
			image.setImageResource(R.drawable.icon_humidity);
			des.setText(item.getmDescription());
			status.setVisibility(View.VISIBLE);
			status.setText(UIHealper.parseStatus(item.getmStatus()));
			degree.setVisibility(View.VISIBLE);
			degree.setText(((DevHumidity)item).getmHumidity());
			press.setVisibility(View.VISIBLE);
			alarm.setVisibility(View.VISIBLE);
				
			if(Istatus == 1){
				alarm.setBackgroundResource(R.color.transparent_device);
				
			}	
			else if((Istatus & 0x08) == 0x08){
				
				alarm.setBackgroundResource(R.color.alarm_gray_color);
			}
			else if((Istatus & 0x10) == 0x10){
				
				alarm.setBackgroundResource(R.color.alarm_red_color);
			}
			else {
				alarm.setBackgroundResource(R.color.alarm_yellow_color);
			}

		}
		
		return convertView;
	}


	public static Device NULL_ITEM = new Device("", "", "", "", "", "", "","");
	@Override
	public List<Device> getItems() {
		return mItems;
	}

	@Override
	public void setItems(List<Device> items) {
		mItems = items;
	}

	@Override
	public Device getNullItem() {
		return NULL_ITEM;
	}

	@Override
	public boolean isNullItem(Device item) {
		return item == NULL_ITEM;
	}

	@Override
	public void setColumn(int column) {
		// TODO Auto-generated method stub
		int count = 0;
		List<Device> list = new ArrayList<Device>();
		for(int i =0 ;i < mItems.size() ; i++)
		{
			if(mItems.get(i).getmType().equals(Device.Type_Tag)){
				if(count != 0)
				{
					int remainder = count % column;
					
					if(remainder != 0)
					{
						for(int j =0 ; j < column -remainder;j++)
						{
							list.add(getNullItem());
						}
					}
				}

				count = 0;
				list.add(mItems.get(i));
			}
			else
			{
				list.add(mItems.get(i));
			}
			
			count++;
		}

		if(list.size() > 0)
			setItems(list);
	}
}
