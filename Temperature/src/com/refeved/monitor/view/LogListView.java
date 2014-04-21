package com.refeved.monitor.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.ListView;

import com.refeved.monitor.R;
import com.refeved.monitor.adapter.DevDealLogListViewAdapter;
import com.refeved.monitor.struct.DeviceAbnormalLog;
import com.refeved.monitor.struct.DeviceDealLog;

public class LogListView extends ListViewBase{
//	DevLogListViewAdapter mLogListViewAdapter;
//	DevAbnormalLogListViewAdapter mAbnormalLogListViewAdapter;
	DevDealLogListViewAdapter mDealLogListViewAdapter;
	protected ListView listView;
	public LogListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		
			mDealLogListViewAdapter = new DevDealLogListViewAdapter(context, new ArrayList<DeviceDealLog>(),new ArrayList<DeviceAbnormalLog>(), R.layout.dev_deal_log_listitem);
			
//			mLogListViewAdapter = new DevLogListViewAdapter(context, new ArrayList<DeviceLog>(),new ArrayList<DeviceDealLog>(), R.layout.dev_detail_log_listitem);
			
//	
	
//		mAbnormalLogListViewAdapter = new DevAbnormalLogListViewAdapter(context, new ArrayList<DeviceAbnormalLog>(), R.layout.dev_abnormal_log_listitem);
//		this.setAdapter(mAbnormalLogListViewAdapter);
//		
		
	}
    public ListView getListView() {
        return listView;
    }
    
    @Override
    public DevDealLogListViewAdapter getAdapter(){
        return mDealLogListViewAdapter;
    }
    public void setListView(ListView listView) {
        this.listView = listView;
        this.listView.setAdapter(mDealLogListViewAdapter);
    }

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void updateListView(List listItemDeal,List listItemsAbnormal,int status){
		mDealLogListViewAdapter.setListItems(listItemDeal,listItemsAbnormal,status);
		mDealLogListViewAdapter.notifyDataSetChanged();
	}

}
