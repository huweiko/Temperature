package com.refeved.monitor.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.refeved.monitor.R;
import com.refeved.monitor.struct.DevFrige;
import com.refeved.monitor.struct.DevHumidity;
import com.refeved.monitor.struct.Device;

@SuppressLint("ResourceAsColor")
public class DevGridViewAdapter extends BaseAdapter {
	private Context 					context;//����������
	private List<Device> 					listItems;//���ݼ���
	private LayoutInflater 				listContainer;//��ͼ����
	private int 						itemViewResource;//�Զ�������ͼԴ 
	//	private int						itemTagResource;

	static class ListItemView{				//�Զ���ؼ�����  
		public ImageView imageView;
		public TextView id;  
		public TextView status;
		public TextView description;
		public TextView num;
		public TextView boundry;
	}  

	public DevGridViewAdapter(Context context, List<Device> data ,int itemLayoutResoure){
		this.context = context;			
		this.listContainer = LayoutInflater.from(context);	//������ͼ����������������
		this.itemViewResource = itemLayoutResoure;
		this.listItems = data;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listItems.size();
	}

	public void setListItems(List<Device> listItems) {
		this.listItems = listItems;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return listItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override  
	public View getView(int position, View convertView, ViewGroup parent) {  
		//����һ��ImageView,��ʾ��GridView��  

		ListItemView  listItemView = null;
		if (convertView == null) {
			//��ȡlist_item�����ļ�����ͼ
			convertView = listContainer.inflate(this.itemViewResource, null);

			listItemView = new ListItemView();
			//��ȡ�ؼ�����
			listItemView.id = (TextView)convertView.findViewById(R.id.device_info_id);
			listItemView.status= (TextView)convertView.findViewById(R.id.device_info_status);
			listItemView.description= (TextView)convertView.findViewById(R.id.device_info_description);
			listItemView.imageView = (ImageView)convertView.findViewById(R.id.listitem_imageview);
			listItemView.num = (TextView) convertView.findViewById(R.id.device_info_num);
			listItemView.boundry = (TextView) convertView.findViewById(R.id.device_info_low_high);

			//���ÿؼ�����convertView
			convertView.setTag(listItemView);
		}else {
			listItemView = (ListItemView)convertView.getTag();
		}	

		listItemView.status.setBackgroundColor(0);
		listItemView.num.setBackgroundColor(0);

		Device device = listItems.get(position);

		listItemView.id.setText(context.getString(R.string.device_info_id_tittle)+device.getmId());
		listItemView.imageView.setBackgroundResource(R.drawable.device_info_icon);
		if(device.getmType().equals(Device.Type_Frige))
		{
			DevFrige devFrige = (DevFrige) device;
			//			listItemView.status.setText(context.getString(R.string.device_info_status_tittile)+
			//					devFrige.getmStatus()+"  "+context.getString(R.string.frige_info_temp_tittle)+devFrige.getmTemperature());
			listItemView.status.setText(context.getString(R.string.device_info_status_tittile)+
					parseStatus(devFrige.getmStatus()));
			if(!devFrige.getmStatus().equals("0")){
				listItemView.status.setBackgroundColor(R.color.red);
			}

			try {
				listItemView.num.setText(devFrige.getmTemperature());
				listItemView.boundry.setText("("+devFrige.getmLow()+ "," +devFrige.getmHigh()+")" );
				double current = Double.parseDouble(devFrige.getmTemperature());
				double low = Double.parseDouble(devFrige.getmLow());
				double high = Double.parseDouble(devFrige.getmHigh());

				if(current > high || current < low )
				{
					listItemView.num.setBackgroundColor(R.color.red);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		else if(device.getmType().equals(Device.Type_Humidity))
		{
			DevHumidity devHum = (DevHumidity) device;
			//			listItemView.status.setText(context.getString(R.string.device_info_status_tittile)+
			//					devHum.getmStatus()+"  "+context.getString(R.string.hum_info_humidity_tittle)+devHum.getmHumidity());
			listItemView.status.setText(context.getString(R.string.device_info_status_tittile)+
					parseStatus(devHum.getmStatus()));
			if(!devHum.getmStatus().equals("0")){
				listItemView.status.setBackgroundColor(R.color.red);
			}
			listItemView.num.setText(devHum.getmHumidity());
			listItemView.boundry.setText("("+devHum.getmLow()+ "," +devHum.getmHigh()+")" );

			try {
				double current = Double.parseDouble(devHum.getmHumidity());
				double low = Double.parseDouble(devHum.getmLow());
				double high = Double.parseDouble(devHum.getmHigh());

				if(current > high || current < low )
				{
					listItemView.num.setBackgroundColor(R.color.red);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		else
		{
			listItemView.status.setText(context.getString(R.string.device_info_status_tittile)+device.getmStatus());
		}

		listItemView.description.setText(context.getString(R.string.device_info_description_tittle)+device.getmDescription());

		return convertView;
	}

	private static String parseStatus(String status){
		String result = "error";
		if(status.equals("0"))
		{
			result = "����";
		}
		else if(status.equals("1"))
		{
			result = "����Ͽ�";
		}
		else if(status.equals("3"))
		{
			result = "�˹�������";
		}

		return result;
	}
}