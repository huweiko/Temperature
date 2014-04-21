package com.refeved.monitor.view;

import java.util.List;

import android.content.Context;
import android.widget.ListAdapter;
import android.widget.ListView;

@SuppressWarnings("unused")
public class ListViewBase extends ListView implements UpdateAdapterInterface{

	public ListViewBase(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List getItems() {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateListView(List listItems) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("rawtypes")
	public void updateListView(List listItemsAbnormal, List listItemDeal,
			int status) {
		// TODO Auto-generated method stub
		
	}

}
