package com.refeved.monitor.ui;


import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.refeved.monitor.AppManager;


public class BaseActivity extends SherlockFragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//����Activity����ջ
		AppManager.getAppManager().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		//����Activity&�Ӷ�ջ���Ƴ�
		AppManager.getAppManager().removeActivity(this);
	}
	
}
