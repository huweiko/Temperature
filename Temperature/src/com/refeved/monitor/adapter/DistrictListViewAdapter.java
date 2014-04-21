package com.refeved.monitor.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.refeved.monitor.R;
import com.refeved.monitor.struct.TreeNode;
import com.refeved.monitor.ui.DistrictListViewActivity;
import com.refeved.monitor.ui.LineBreakLayout;
import com.refeved.monitor.ui.RepeatingImageButton;

public class DistrictListViewAdapter extends BaseAdapter {
	
	private Context 					context;//运行上下文
	private List<TreeNode> 					listItems;//数据集合
	private LayoutInflater 				listContainer;//视图容器
	
	public DistrictListViewAdapter(Context context,List<TreeNode> data) {
		this.context = context;		
		this.listContainer = LayoutInflater.from(context);	//创建视图容器并设置上下文
		this.listItems = data;
	}
	
	public void setListItems(List<TreeNode> data){
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

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		List<TreeNode> items = listItems.get(position).getmChildren();

		LinearLayout itemMainLayout = new LinearLayout(context);
		itemMainLayout.setOrientation(LinearLayout.VERTICAL);
		
		if(items.size() <= 0) return itemMainLayout;
		
		View tagView = listContainer.inflate(R.layout.listitem_tag, null);
		TextView tagText = (TextView) tagView.findViewById(R.id.listview_item_tag);
		tagText.setText(listItems.get(position).getmName());
		tagText.setTextColor(0Xffffffff);
		itemMainLayout.addView(tagView);
		
		LineBreakLayout buttonLayout = new LineBreakLayout(context);
		
		int count = 0;
		while(count < items.size()){
			//Button button = new Button(context);
			RepeatingImageButton button = new RepeatingImageButton(context);
			button.setOnClickListener((DistrictListViewActivity)context);
			button.setRepeatListener((DistrictListViewActivity)context, 1000);
			button.setText(items.get(count).getmName());
			button.setTextColor(0Xffffffff);
			button.setBackgroundResource(R.drawable.color_button);
			//button.setBackgroundResource(R.drawable.round_button);
//			button.setTag(R.id.button_tag_index1,items.get(count).getmId());
			button.setTag(items.get(count));
			count++;
			//button.setBackgroundResource(R.drawable.button);
			button.setTextSize(20);
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			button.setLayoutParams(params);
			buttonLayout.addView(button);
		}
		itemMainLayout.addView(buttonLayout);
		
		return itemMainLayout;
	}

}