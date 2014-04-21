/*
 * �ļ�����LoginActivity.java
 * ���ܣ���½����
 * ���ߣ�huwei
 * ����ʱ�䣺2013-10-17
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
	View mCustomView;//���ƽ���
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
		//�õ�����Ҫ��ʾ��xml�ļ�
		mCustomView = getLayoutInflater().inflate(R.layout.login_setting_actionbar,
				null);
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		//��ȡ������xml�ļ�װ�ؽ�ActionBar
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
					UIHealper.DisplayToast(appContext, "�����IP��ַ���Ϸ���");
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
	//�����û���������
	protected void onSaveContent() 
	{
		super.onStop();
	    appContext.ExternalServerIP = externalEt.getText().toString();
		appContext.InwardServerIP = inwardEt.getText().toString();
		//��ȡSharedPreferencesʱ����Ҫ����������
		//��һ���Ǳ�����ļ������ƣ��ڶ��������Ǳ����ģʽ���Ƿ�ֻ����Ӧ��ʹ�ã�

		Editor editor = settings.edit();
		//����Ҫ���������
		editor.putString(getString(R.string.settings_external_server_IP), appContext.ExternalServerIP);
		editor.putString(getString(R.string.settings_inward_server_IP), appContext.InwardServerIP);
		//ȷ�ϱ���
		editor.commit();
	}

}