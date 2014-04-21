/*
 * 文件名：LoginActivity.java
 * 功能：登陆界面
 * 作者：huwei
 * 创建时间：2013-10-17
 * 
 * 
 * 
 * */
package com.refeved.monitor.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.LayoutParams;
import com.refeved.monitor.AppContext;
import com.refeved.monitor.R;
import com.refeved.monitor.UIHealper;

public class LoginSettingActivity extends BaseActivity
{
	private AppContext appContext;
	String SERTINGS_EXTERNAL_SERVER_IP;
	String SERTINGS_INWARD_SERVER_IP;
	View mCustomView;//定制界面
	private EditText externalEt,inwardEt;
    SharedPreferences settings; 
    
	public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        appContext = (AppContext) getApplication();

        setContentView(R.layout.login_setting_activity);
        settings = appContext.getSharedPreferences(getString(R.string.settings_filename),MODE_PRIVATE); 
    	
    	externalEt=(EditText)findViewById(R.id.external_ip_editview);
    	inwardEt=(EditText)findViewById(R.id.inward_ip_editview);
		
		externalEt.setText(appContext.ExternalServerIP);
		
		inwardEt.setText(appContext.InwardServerIP);
		//得到我们要显示的xml文件
		mCustomView = getLayoutInflater().inflate(R.layout.login_setting_actionbar,
				null);
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		//将取出来的xml文件装载进ActionBar
		actionBar.setCustomView(mCustomView, new ActionBar.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_gradient_bg));
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		Button back_button = (Button) mCustomView.findViewById(R.id.login_setting_back);
		Button finish_button = (Button) mCustomView.findViewById(R.id.login_setting_finish);
		back_button.setOnClickListener(new Button.OnClickListener()
        {
        	public void onClick(View v)
        	{ 
        		
                Intent intent = new Intent();
                LoginSettingActivity.this.setResult(RESULT_OK, intent);
                LoginSettingActivity.this.finish();
        	}
        });
		
		finish_button.setOnClickListener(new Button.OnClickListener()
        {
        	public void onClick(View v)
        	{ 
        		final String externalIP = externalEt.getText().toString();
				final String inwardIP = inwardEt.getText().toString();
				if(UIHealper.isIpv4(externalIP) == false || UIHealper.isIpv4(inwardIP) == false){
					UIHealper.DisplayToast(appContext, "输入的IP地址不合法！");
					return;
				}
//				new Thread(){
//					public void run(){
//						WebClient.checknetwork(inwardIP,externalIP);	
//					}
//				}.start();
				onSaveContent();
                Intent intent = new Intent();
                LoginSettingActivity.this.setResult(RESULT_OK, intent);
                LoginSettingActivity.this.finish();
        	}
        });
		
    }
	//保存用户名和密码
	protected void onSaveContent() 
	{
		super.onStop();
	    appContext.ExternalServerIP = externalEt.getText().toString();
		appContext.InwardServerIP = inwardEt.getText().toString();
		//获取SharedPreferences时，需要设置两参数
		//第一个是保存的文件的名称，第二个参数是保存的模式（是否只被本应用使用）

		Editor editor = settings.edit();
		//添加要保存的数据
		editor.putString(getString(R.string.settings_external_server_IP), appContext.ExternalServerIP);
		editor.putString(getString(R.string.settings_inward_server_IP), appContext.InwardServerIP);
		//确认保存
		editor.commit();
	}

}