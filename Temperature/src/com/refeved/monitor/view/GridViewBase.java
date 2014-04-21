package com.refeved.monitor.view;

import java.util.List;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class GridViewBase extends GridView implements UpdateAdapterInterface{
//	DevGridViewAdapter mDevGridViewAdapter;
	public GridViewBase(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

	}
	
	public GridViewBase(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GridViewBase(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

	public void updateListView(@SuppressWarnings("rawtypes") List listItems) {
		// TODO Auto-generated method stub

	}
		
	@SuppressWarnings("rawtypes")
	@Override
	public List getItems() {
		// TODO Auto-generated method stub
		return null;
	}

}
